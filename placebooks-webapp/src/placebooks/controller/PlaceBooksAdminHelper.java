package placebooks.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.model.GPSTraceItem;
import placebooks.model.MapImageItem;
import placebooks.model.MediaItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookBinder;
import placebooks.model.PlaceBookBinder.State;
import placebooks.model.PlaceBookItem;
import placebooks.model.PlaceBookItemSearchIndex;
import placebooks.model.PlaceBookSearchIndex;
import placebooks.model.User;
import placebooks.model.WebBundleItem;

import com.vividsolutions.jts.geom.Geometry;

public final class PlaceBooksAdminHelper
{
	private static final Logger log = Logger.getLogger(PlaceBooksAdminHelper.class.getName());

	public static final String[] getExtension(final String field)
	{
		final int delim = field.indexOf(".");
		if (delim == -1) { return null; }

		final String[] out = new String[2];
		out[0] = field.substring(0, delim);
		out[1] = field.substring(delim + 1, field.length());

		return out;
	}

	public static final File makePackage(final EntityManager em, final PlaceBookBinder pb)
	{

		// Mapping
		for (final PlaceBook p : pb.getPlaceBooks())
		{

			if (!p.hasPlaceBookItemClass(MapImageItem.class) && p.hasPlaceBookItemClass(GPSTraceItem.class))
			{
				try
				{
					p.calcBoundary();
					/*
					 * final int timeout = Integer.parseInt( PropertiesSingleton
					 * .get(PlaceBooksAdminHelper.class.getClassLoader()) .getProperty(
					 * PropertiesSingleton.IDEN_WGET_TIMEOUT, "20000" ) );
					 */
					em.getTransaction().begin();
					/*
					 * final Runnable r = new Runnable() {
					 * 
					 * @Override public void run() { try {
					 */
					final TileHelper.MapMetadata md = TileHelper.getMap(p);
					p.setGeometry(md.getBoundingBox());
					final MapImageItem mii = new MapImageItem(null, null, null, null);
					p.addItem(mii);
					mii.setPlaceBook(p);
					mii.setOwner(p.getOwner());
					mii.setGeometry(p.getGeometry());
					mii.setPath(md.getFile().getPath());
					/*
					 * } catch (final Throwable e) { log.error(e.getMessage(), e); } } }; final
					 * Thread t = new Thread(r); t.start();
					 * log.info("Waiting for process... allowing " + timeout + " millis");
					 * Thread.sleep(timeout); t.destroy();
					 */

					em.getTransaction().commit();
				}
				catch (final Throwable e)
				{
					log.error(e.getMessage(), e);
				}
				finally
				{
					if (em.getTransaction().isActive())
					{
						em.getTransaction().rollback();
						log.error("Rolling current persist transaction back");
					}
				}
			}
			else
			{
				log.info("PlaceBook already has MapImageItem");
			}
		}

		final String out = placeBookBinderToXML(pb);

		if (out == null) { return null; }

		final String pkgPath = pb.getPackagePath();
		if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
		{
			try
			{
				final FileWriter fw = new FileWriter(new File(pkgPath
						+ "/"
						+ PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_CONFIG, "")));
				fw.write(out);
				fw.close();
			}
			catch (final IOException e)
			{
				log.error(e.toString(), e);
				return null;
			}
		}
		final String pkgZPath = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

		final File zipFile = new File(pkgZPath + "/" + pb.getKey() + ".zip");

		try
		{
			// Compress package path
			if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
			{

				final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
				zos.setMethod(ZipOutputStream.DEFLATED);

				final ArrayList<File> files = new ArrayList<File>();
				getFileListRecursive(new File(pkgPath), files);

				final File currentDir = new File(".");
				log.info("Current working directory is " + currentDir.getAbsolutePath());

				final byte data[] = new byte[2048];
				BufferedInputStream bis = null;
				for (final File file : files)
				{
					log.info("Adding file to archive: " + file.getPath());
					final FileInputStream fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis, 2048);
					zos.putNextEntry(new ZipEntry(file.getPath()));

					int j;
					while ((j = bis.read(data, 0, 2048)) != -1)
					{
						zos.write(data, 0, j);
					}
					bis.close();
				}

				zos.close();
			}
			else
			{
				log.error("Package path doesn't exist or can't be created");
				return null;
			}
		}
		catch (final IOException e)
		{
			log.error(e.toString(), e);
			return null;
		}

		return zipFile;
	}

	// Takes a PlaceBookBinder, copies it, and returns the (published) copy
	public static final PlaceBookBinder publishPlaceBookBinder(final EntityManager em, final PlaceBookBinder pb)
	{
		PlaceBookBinder pb_ = null;
		try
		{
			em.getTransaction().begin();
			pb_ = new PlaceBookBinder(pb);
			pb_.setState(State.PUBLISHED);
			em.persist(pb_);
			em.getTransaction().commit();

			// Copy data on disk now that keys have been generated
			em.getTransaction().begin();
			for (final PlaceBook p : pb.getPlaceBooks())
			{
				for (final PlaceBookItem item : p.getItems())
				{
					if (item instanceof MediaItem)
					{
						final String data = ((MediaItem) item).getPath();
						((MediaItem) item).writeDataToDisk(data, new FileInputStream(new File(data)));
					}
					else if (item instanceof WebBundleItem)
					{
						final WebBundleItem wbi = (WebBundleItem) item;
						final String data = wbi.generateWebBundlePath();
						FileUtils.copyDirectory(new File(wbi.getWebBundlePath()), new File(data));
						wbi.setWebBundlePath(data);
					}
				}
			}
			em.getTransaction().commit();

		}
		catch (final Throwable e)
		{
			log.error("Error creating PlaceBookBinder copy", e);
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				log.error("Rolling back PlaceBookBinder copy");
			}
		}
		return pb_;
	}

	public static final PlaceBookBinder savePlaceBookBinder(final EntityManager manager, PlaceBookBinder placeBookBinder)
	{
		final User currentUser = UserManager.getCurrentUser(manager);

		try
		{
			manager.getTransaction().begin();

			if (placeBookBinder.getKey() != null)
			{

				final PlaceBookBinder dbPlaceBookBinder = manager.find(PlaceBookBinder.class, placeBookBinder.getKey());
				// Get the DB version of this Binder and remove PlaceBooks that
				// are no longer part of the new Binder
				if (dbPlaceBookBinder != null)
				{
					for (final PlaceBook dbPlaceBook : dbPlaceBookBinder.getPlaceBooks())
					{
						final PlaceBook mPlaceBook = matchPlaceBook(dbPlaceBook, placeBookBinder.getPlaceBooks());

						// Delete unmatched PlaceBooks
						if (mPlaceBook == null)
						{
							for (final PlaceBookItem dbItem : dbPlaceBook.getItems())
							{
								dbItem.deleteItemData();
								manager.remove(dbItem);
							}

							manager.remove(dbPlaceBook);
						}
						else
						{
							// Remove any items that are no longer used
							for (final PlaceBookItem dbItem : dbPlaceBook.getItems())
							{
								if (!containsItem(dbItem, mPlaceBook.getItems()))
								{
									if (dbItem instanceof GPSTraceItem)
									{
										// Remove any MapImageItem if removing a
										// GPSTraceItem
										for (final PlaceBookItem mapItem : dbPlaceBook.getItems())
										{
											if (mapItem instanceof MapImageItem)
											{
												mapItem.deleteItemData();
												manager.remove(mapItem);
											}
										}

										for (final PlaceBookItem mapItem : mPlaceBook.getItems())
										{
											if (mapItem instanceof MapImageItem)
											{
												mPlaceBook.removeItem(mapItem);
											}
										}
									}
									dbItem.deleteItemData();
									manager.remove(dbItem);
								}
							}
							for (final Entry<String, String> entry : mPlaceBook.getMetadata().entrySet())
							{
								dbPlaceBook.addMetadataEntryIndexed(entry.getKey(), entry.getValue());
							}
							dbPlaceBook.setItems(mPlaceBook.getItems());
							dbPlaceBook.setGeometry(mPlaceBook.getGeometry());

						}

					}
					for (final Entry<String, String> entry : placeBookBinder.getMetadata().entrySet())
					{
						dbPlaceBookBinder.addMetadataEntry(entry.getKey(), entry.getValue());
					}
					dbPlaceBookBinder.setPlaceBooks(placeBookBinder.getPlaceBooks());

					dbPlaceBookBinder.setGeometry(placeBookBinder.getGeometry());

					dbPlaceBookBinder.setGeometry(placeBookBinder.getGeometry());
					placeBookBinder = dbPlaceBookBinder;
				}
			}

			for (PlaceBook placeBook : placeBookBinder.getPlaceBooks())
			{
				final Collection<PlaceBookItem> updateMedia = new ArrayList<PlaceBookItem>();
				final Collection<PlaceBookItem> newItems = new ArrayList<PlaceBookItem>(placeBook.getItems());
				placeBook.setItems(new ArrayList<PlaceBookItem>());
				placeBook.setPlaceBookBinder(placeBookBinder);
				for (final PlaceBookItem newItem : newItems)
				{
					// Update existing item if possible
					PlaceBookItem item = newItem;
					if (item.getKey() != null)
					{
						final PlaceBookItem oldItem = manager.find(PlaceBookItem.class, item.getKey());
						if (oldItem != null)
						{
							item = oldItem;
						}

						if (newItem.getSourceURL() != null && !newItem.getSourceURL().equals(item.getSourceURL()))
						{
							item.setSourceURL(newItem.getSourceURL());
							updateMedia.add(item);
						}
					}
					else
					{
						item.setTimestamp(new Date());
						manager.persist(item);

						if (newItem.getMetadata().get("originalItemID") != null)
						{
							final PlaceBookItem originalItem = manager.find(PlaceBookItem.class, newItem.getMetadata()
									.get("originalItemID"));
							if (originalItem != null)
							{
								// We want to keep the metadata & parameters
								// from the new item
								final Map<String, String> meta = new HashMap<String, String>(item.getMetadata());
								final Map<String, Integer> para = new HashMap<String, Integer>(item.getParameters());
								item.update(originalItem);
								item.setMedataData(meta);
								item.setParameters(para);
							}
						}
						else
						{
							updateMedia.add(item);
						}
					}

					if (item.getOwner() == null)
					{
						item.setOwner(currentUser);
					}

					item.update(newItem);

					placeBook.addItem(item);
				}

				if (placeBook.getOwner() == null)
				{
					placeBook.setOwner(currentUser);
				}

				if (placeBook.getTimestamp() == null)
				{
					placeBook.setTimestamp(new Date());
				}

				placeBook = manager.merge(placeBook);
				try
				{
					placeBook.calcBoundary();
				}
				catch (final Exception e)
				{
					log.warn("Error Calculating boundry", e);
				}

				for (final PlaceBookItem item : updateMedia)
				{
					try
					{
						if (item instanceof MediaItem)
						{
							final MediaItem mediaItem = (MediaItem) item;
							mediaItem.writeDataToDisk(mediaItem.getSourceURL().toExternalForm(), CommunicationHelper
									.getConnection(mediaItem.getSourceURL()).getInputStream());
						}
						else if (item instanceof GPSTraceItem)
						{
							final GPSTraceItem gpsItem = (GPSTraceItem) item;
							if (gpsItem.getSourceURL() != null)
							{
								gpsItem.readTrace(CommunicationHelper.getConnection(gpsItem.getSourceURL())
										.getInputStream());
							}
						}
						else if (item instanceof WebBundleItem)
						{
							final WebBundleItem webItem = (WebBundleItem) item;
							scrape(webItem);
						}
					}
					catch (final Exception e)
					{
						log.info(e.getMessage(), e);
					}
				}
			}

			if (placeBookBinder.getOwner() == null)
			{
				placeBookBinder.setOwner(currentUser);
			}

			if (placeBookBinder.getTimestamp() == null)
			{
				placeBookBinder.setTimestamp(new Date());
			}

			placeBookBinder = manager.merge(placeBookBinder);
			try
			{
				placeBookBinder.calcBoundary();
			}
			catch (final Exception e)
			{
				log.warn("Error Calculating boundry", e);
			}

			manager.getTransaction().commit();

			return manager.find(PlaceBookBinder.class, placeBookBinder.getKey());
		}
		catch (final Throwable e)
		{
			log.error("Error saving PlaceBookBinder copy", e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling back");
			}
		}
		return null;

	}

	public static final boolean scrape(final WebBundleItem wbi)
	{

		final StringBuffer wgetCmd = new StringBuffer();
		wgetCmd.append(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_WGET, ""));

		if (wgetCmd.equals("")) { return false; }

		// TODO: User Agent string does not work for some reason
		/*
		 * wgetCmd.append(" -U \""); wgetCmd.append(PropertiesSingleton
		 * .get(PlaceBooksAdminHelper.class.getClassLoader())
		 * .getProperty(PropertiesSingleton.IDEN_USER_AGENT, "")); wgetCmd.append("\" ");
		 */

		final String webBundlePath = wbi.generateWebBundlePath();

		wgetCmd.append(" -P " + webBundlePath + " " + wbi.getSourceURL().toString());

		log.info("wgetCmd=" + wgetCmd.toString());

		if (new File(webBundlePath).exists() || new File(webBundlePath).mkdirs())
		{
			final int timeout = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_WGET_TIMEOUT, "10000"));
			final ExecTimer t = new ExecTimer(timeout, wgetCmd.toString());
			t.start();
			final String urlStr = wbi.getSourceURL().toString();
			final int protocol = urlStr.indexOf("://");
			wbi.setWebBundlePath(webBundlePath);
			wbi.setWebBundleName(urlStr.substring(protocol + 3, urlStr.length()));
			log.info("wbi.getWebBundle() = " + wbi.getWebBundle());

			return true;
		}

		return false;
	}

	public static final Set<Map.Entry<PlaceBookBinder, Integer>> search(final EntityManager em, final String terms)
	{

		final Set<String> search = SearchHelper.getIndex(terms, 5);

		final TypedQuery<PlaceBookSearchIndex> query1 = em.createQuery(	"SELECT p FROM PlaceBookSearchIndex p",
																		PlaceBookSearchIndex.class);
		final List<PlaceBookSearchIndex> pbIndexes = query1.getResultList();

		// Search rationale: ratings are accumulated per PlaceBookBinder for
		// that Binder's PlaceBooks plus any PlaceBookItems
		final Map<PlaceBookBinder, Integer> hits = new HashMap<PlaceBookBinder, Integer>();

		for (final PlaceBookSearchIndex index : pbIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			Integer rating = hits.get(index.getPlaceBook().getPlaceBookBinder());
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(index.getPlaceBook().getPlaceBookBinder(), new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = em.createQuery(	"SELECT p FROM PlaceBookItemSearchIndex p",
																			PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			if (index.getPlaceBookItem() == null || index.getPlaceBookItem().getPlaceBook() == null)
			{
				continue;
			}
			final PlaceBookBinder p = index.getPlaceBookItem().getPlaceBook().getPlaceBookBinder();
			Integer rating = hits.get(p);
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(p, new Integer(keywords.size() + rating.intValue()));
		}

		return hits.entrySet();
	}

	public static final Set<Map.Entry<PlaceBookBinder, Double>> searchLocationForPlaceBookBinders(
			final EntityManager em, final Geometry geometry)
	{
		final List<PlaceBookBinder> pbs = em
				.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.state = :state", PlaceBookBinder.class)
				.setParameter("state", PlaceBookBinder.State.PUBLISHED).getResultList();

		final Map<PlaceBookBinder, Double> hits = new HashMap<PlaceBookBinder, Double>();
		for (final PlaceBookBinder p : pbs)
		{
			if (p.getGeometry() != null)
			{
				hits.put(p, p.getGeometry().distance(geometry));
			}
		}

		return hits.entrySet();
	}

	public static final Set<Map.Entry<PlaceBookItem, Double>> searchLocationForPlaceBookItems(final EntityManager em,
			final Geometry geometry)
	{
		// TODO: need to look up PlaceBookBinder state; not sure how
		final List<PlaceBookItem> ps = em
				.createQuery("SELECT p FROM PlaceBookItem p WHERE p.placebook.placebookbinder.state = :state",
								PlaceBookItem.class).setParameter("state", PlaceBookBinder.State.PUBLISHED)
				.getResultList();

		final Map<PlaceBookItem, Double> hits = new HashMap<PlaceBookItem, Double>();
		for (final PlaceBookItem p : ps)
		{
			if (p.getGeometry() != null)
			{
				hits.put(p, p.getGeometry().distance(geometry));
			}
		}

		return hits.entrySet();
	}

	private static boolean containsItem(final PlaceBookItem findItem, final List<PlaceBookItem> items)
	{
		for (final PlaceBookItem item : items)
		{
			if (findItem.getKey().equals(item.getKey())) { return true; }
		}
		return false;
	}

	private static boolean containsPlaceBook(final PlaceBook findPlaceBook, final List<PlaceBook> pbs)
	{
		for (final PlaceBook p : pbs)
		{
			if (findPlaceBook.getKey().equals(p.getKey())) { return true; }
		}
		return false;
	}

	private static void getFileListRecursive(final File path, final List<File> out)
	{
		final List<File> files = new ArrayList<File>(Arrays.asList(path.listFiles()));

		for (final File file : files)
		{
			if (file.isDirectory())
			{
				getFileListRecursive(file, out);
			}
			else
			{
				out.add(file);
			}
		}
	}

	private static final PlaceBook matchPlaceBook(final PlaceBook findPlaceBook, final List<PlaceBook> pbs)
	{
		for (final PlaceBook p : pbs)
		{
			if (findPlaceBook.getKey().equals(p.getKey())) { return p; }
		}

		return null;
	}

	private static String placeBookBinderToXML(final PlaceBookBinder pb)
	{
		StringWriter out = null;

		try
		{

			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document config = builder.newDocument();

			final Element root = pb.createConfigurationRoot(config);
			config.appendChild(root);
			log.info("Writing PlaceBook config data");
			for (final PlaceBook p : pb.getPlaceBooks())
			{
				final Element root_ = p.createConfigurationRoot(config);
				log.info("Writing PlaceBookItem config data");
				// Note: ImageItem, VideoItem and AudioItem write their data to
				// a package directly as well as creating XML configuration
				for (final PlaceBookItem item : p.getItems())
				{
					item.appendConfiguration(config, root_);
				}

				root.appendChild(root_);
			}

			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer t = tf.newTransformer();
			final DOMSource source = new DOMSource(config);

			out = new StringWriter();
			final StreamResult result = new StreamResult(out);
			t.transform(source, result);

			return out.getBuffer().toString();
		}
		catch (final Throwable e)
		{
			log.error(e.toString(), e);
		}

		return null;
	}

	private static final PlaceBook savePlaceBook(final EntityManager manager, PlaceBook placebook)
	{
		final User currentUser = UserManager.getCurrentUser(manager);

		try
		{
			manager.getTransaction().begin();

			if (placebook.getKey() != null)
			{
				final PlaceBook dbPlacebook = manager.find(PlaceBook.class, placebook.getKey());
				if (dbPlacebook != null)
				{
					// Remove any items that are no longer used
					for (final PlaceBookItem item : dbPlacebook.getItems())
					{
						if (!containsItem(item, placebook.getItems()))
						{
							if (item instanceof GPSTraceItem)
							{
								// Remove any MapImageItem if removing a
								// GPSTraceItem
								for (final PlaceBookItem mapItem : dbPlacebook.getItems())
								{
									if (mapItem instanceof MapImageItem)
									{
										mapItem.deleteItemData();
										manager.remove(mapItem);
									}
								}

								for (final PlaceBookItem mapItem : placebook.getItems())
								{
									if (mapItem instanceof MapImageItem)
									{
										placebook.removeItem(mapItem);
									}
								}
							}
							item.deleteItemData();
							manager.remove(item);
						}
					}

					dbPlacebook.setItems(placebook.getItems());
					for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
					{
						dbPlacebook.addMetadataEntryIndexed(entry.getKey(), entry.getValue());
					}

					dbPlacebook.setGeometry(placebook.getGeometry());
					placebook = dbPlacebook;
				}
			}

			final Collection<PlaceBookItem> updateMedia = new ArrayList<PlaceBookItem>();
			final Collection<PlaceBookItem> newItems = new ArrayList<PlaceBookItem>(placebook.getItems());
			placebook.setItems(new ArrayList<PlaceBookItem>());
			for (final PlaceBookItem newItem : newItems)
			{
				// Update existing item if possible
				PlaceBookItem item = newItem;
				if (item.getKey() != null)
				{
					final PlaceBookItem oldItem = manager.find(PlaceBookItem.class, item.getKey());
					if (oldItem != null)
					{
						item = oldItem;
					}

					if (newItem.getSourceURL() != null && !newItem.getSourceURL().equals(item.getSourceURL()))
					{
						item.setSourceURL(newItem.getSourceURL());
						updateMedia.add(item);
					}
				}
				else
				{
					item.setTimestamp(new Date());
					manager.persist(item);

					if (newItem.getMetadata().get("originalItemID") != null)
					{
						final PlaceBookItem originalItem = manager.find(PlaceBookItem.class,
																		newItem.getMetadata().get("originalItemID"));
						if (originalItem != null)
						{
							// We want to keep the metadata & parameters from
							// the new item
							final Map<String, String> meta = new HashMap<String, String>(item.getMetadata());
							final Map<String, Integer> para = new HashMap<String, Integer>(item.getParameters());
							item.update(originalItem);
							item.setMedataData(meta);
							item.setParameters(para);
						}
					}
					else
					{
						updateMedia.add(item);
					}
				}

				if (item.getOwner() == null)
				{
					item.setOwner(currentUser);
				}

				item.update(newItem);

				placebook.addItem(item);
			}

			if (placebook.getOwner() == null)
			{
				placebook.setOwner(currentUser);
			}

			if (placebook.getTimestamp() == null)
			{
				placebook.setTimestamp(new Date());
			}

			placebook = manager.merge(placebook);
			try
			{
				placebook.calcBoundary();
			}
			catch (final Exception e)
			{
				log.warn("Error Calculating boundry", e);
			}

			manager.getTransaction().commit();

			manager.getTransaction().begin();
			for (final PlaceBookItem item : updateMedia)
			{
				try
				{
					if (item instanceof MediaItem)
					{
						final MediaItem mediaItem = (MediaItem) item;
						mediaItem.writeDataToDisk(mediaItem.getSourceURL().toExternalForm(), CommunicationHelper
								.getConnection(mediaItem.getSourceURL()).getInputStream());
					}
					else if (item instanceof GPSTraceItem)
					{
						final GPSTraceItem gpsItem = (GPSTraceItem) item;
						if (gpsItem.getSourceURL() != null)
						{
							gpsItem.readTrace(CommunicationHelper.getConnection(gpsItem.getSourceURL())
									.getInputStream());
						}
					}
					else if (item instanceof WebBundleItem)
					{
						final WebBundleItem webItem = (WebBundleItem) item;
						scrape(webItem);
					}
				}
				catch (final Exception e)
				{
					log.info(e.getMessage(), e);
				}
			}
			manager.getTransaction().commit();

			return manager.find(PlaceBook.class, placebook.getKey());

		}
		catch (final Throwable e)
		{
			log.error("Error saving PlaceBook copy", e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling back");
			}
		}
		return null;

	}

}
