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
import java.util.Iterator;
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
import placebooks.model.PlaceBookBinderSearchIndex;
import placebooks.model.PlaceBookItem;
import placebooks.model.PlaceBookItemSearchIndex;
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
			try
			{
				// Delete all existing maps
				em.getTransaction().begin();
				final List<PlaceBookItem> toDel = 
						new ArrayList<PlaceBookItem>();

				for (final PlaceBookItem pbi : p.getItems())
				{
					if (pbi instanceof MapImageItem)
					{
						pbi.deleteItemData();
						toDel.add(pbi);
						em.remove(pbi);
					}

				}

				for (final PlaceBookItem pbi : toDel)
					p.removeItem(pbi);

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


			//			if (!p.hasPlaceBookItemClass(MapImageItem.class) && p.hasPlaceBookItemClass(GPSTraceItem.class))
			// Create overview map
			if (p.hasPlaceBookItemClass(GPSTraceItem.class))
			{
				try
				{
					p.calcBoundary();
					em.getTransaction().begin();
					final MapMetadata md = TileHelper.getMap(p);
					p.setGeometry(md.getBoundingBox());
					final MapImageItem mii = new MapImageItem(null, null, null, null);
					p.addItem(mii);
					mii.setPlaceBook(p);
					mii.setOwner(p.getOwner());
					mii.setGeometry(p.getGeometry());
					mii.addMetadataEntry("type", "overview");
					mii.setPath(md.getFile().getPath());
					log.debug("Adding overview MapImageItem");

					if (md.getSelectedScale() > 0)
					{
						// Create local content maps if necessary
						final List<PlaceBookItem> toAdd = 
								new ArrayList<PlaceBookItem>();
						for (final PlaceBookItem pbi : p.getItems())
						{
							if (!(pbi instanceof GPSTraceItem) && 
									pbi.getMetadataValue("mapItemID") != null)
							{
								final MapMetadata md_ = TileHelper.getMap(pbi);
								final MapImageItem mii_ = 
										new MapImageItem(null, null, null, null);
								mii_.setPlaceBook(p);
								mii_.setOwner(p.getOwner());
								mii_.setGeometry(p.getGeometry());
								mii_.addMetadataEntry("type", "content");
								mii_.addMetadataEntry("ref", pbi.getKey());
								mii_.setPath(md_.getFile().getPath());
								toAdd.add(mii_);
								log.debug("Adding content-local MapImageItem");
							}
						}

						for (final PlaceBookItem pbi : toAdd)
							p.addItem(pbi);
					}

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
			//else
			//{
			//	log.info("PlaceBook already has MapImageItem");
			//}

		}

		// Clean package dir
		try
		{
			final File clean = new File(pb.getPackagePath());
			if (clean.exists())
				FileUtils.deleteDirectory(clean);
		}
		catch (final Throwable e)
		{
			log.error(e.toString());
		}


		final Map<String, String> out = placeBookBinderToXMLMap(pb);

		if (out.size() == 0)
			return null;


		final String pkgPath = pb.getPackagePath();
		if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
		{
			try
			{
				for (final Map.Entry<String, String> entry : out.entrySet())
				{
					final FileWriter fw = 
							new FileWriter(new File(pkgPath	+ "/" 
									+ entry.getKey())
									);
					fw.write(entry.getValue());
					fw.close();
				}
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

	public static final PlaceBookBinder savePlaceBookBinder(final EntityManager manager,
			final PlaceBookBinder placeBookBinder)
	{
		final User currentUser = UserManager.getCurrentUser(manager);

		try
		{
			manager.getTransaction().begin();

			final Collection<PlaceBookItem> updateMedia = new ArrayList<PlaceBookItem>();
			PlaceBookBinder binder = updatePlaceBookBinder(manager, placeBookBinder, currentUser, updateMedia);
			for (PlaceBook placeBook : binder.getPlaceBooks())
			{
				placeBook = manager.merge(placeBook);
			}

			for (final PlaceBookItem item : updateMedia)
			{
				try
				{
					String original = item.getMetadataValue("originalItemID");
					boolean originalFound = false;
					if(original!=null)
					{
						PlaceBookItem originalItem = manager.find(PlaceBookItem.class, original);
						if(originalItem!=null)
						{
							originalFound = true;
							item.update(originalItem);
						}
					}
					if(!originalFound)
					{
						if (item instanceof MediaItem)
						{
							final MediaItem mediaItem = (MediaItem) item;
							// Check if the file exists...
							if(mediaItem.attemptPathFix()==null)
							{
								mediaItem.RedownloadItem();
							}
						}
						else if (item instanceof GPSTraceItem)
						{
							final GPSTraceItem gpsItem = (GPSTraceItem) item;
							if (gpsItem.getSourceURL() != null)
							{
								gpsItem.readTrace(CommunicationHelper.getConnection(gpsItem.getSourceURL()).getInputStream());
							}
						}
						else if (item instanceof WebBundleItem)
						{
							final WebBundleItem webItem = (WebBundleItem) item;
							scrape(webItem);
						}
					}
				}
				catch (final Exception e)
				{
					log.info(e.getMessage(), e);
				}
			}

			// Rebuild search index
			binder.rebuildSearchIndex();

			binder = manager.merge(binder);

			manager.getTransaction().commit();

			return manager.find(PlaceBookBinder.class, binder.getKey());
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

	public static final Set<Map.Entry<PlaceBookBinder, Integer>> 
	search(final EntityManager em, final String terms)
	{

		final int nTerms = 
				Integer.parseInt(
						PropertiesSingleton.get(
								CommunicationHelper.class.getClassLoader()
								).getProperty(PropertiesSingleton.IDEN_SEARCH_TERMS, "5"));


		final Set<String> search = SearchHelper.getIndex(terms, nTerms);

		final TypedQuery<PlaceBookBinderSearchIndex> query1 = 
				em.createQuery("SELECT p FROM PlaceBookBinderSearchIndex p",
						PlaceBookBinderSearchIndex.class);
		final List<PlaceBookBinderSearchIndex> pbIndexes = 
				query1.getResultList();

		// Search rationale: ratings are accumulated per PlaceBookBinder for
		// that Binder plus any PlaceBookItems associated with all the Binder's
		// PlaceBooks
		final Map<PlaceBookBinder, Integer> hits = new HashMap<PlaceBookBinder, Integer>();

		for (final PlaceBookBinderSearchIndex index : pbIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			Integer rating = hits.get(index.getPlaceBookBinder());
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(index.getPlaceBookBinder(), 
					new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = 
				em.createQuery("SELECT p FROM PlaceBookItemSearchIndex p",
						PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			if (index.getPlaceBookItem() == null || index.getPlaceBookItem().getPlaceBook() == null ||
					index.getPlaceBookItem().getPlaceBook().getPlaceBookBinder() == null)
			{
				continue;
			}
			final PlaceBookBinder p = 
					index.getPlaceBookItem().getPlaceBook().getPlaceBookBinder();
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

	// Map returned is as follows:
	//		filename to write to -> XML string
	private static Map<String, String> 
	placeBookBinderToXMLMap(final PlaceBookBinder pb)
	{

		final Map<String, String> map = new HashMap<String, String>();

		try
		{

			final DocumentBuilder builder = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document config = builder.newDocument();
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer t = tf.newTransformer();

			final Element root = pb.createConfigurationRoot(config);

			log.info("Writing PlaceBook config data");

			for (final PlaceBook p : pb.getPlaceBooks())
			{
				final Document config_ = builder.newDocument();
				final Element root_ = p.createConfigurationRoot(config_);
				log.info("Writing PlaceBookItem config data");
				// Note: ImageItem, VideoItem and AudioItem write their data to
				// a package directly as well as creating XML configuration
				for (final PlaceBookItem item : p.getItems())
					item.appendConfiguration(config_, root_);
				config_.appendChild(root_);

				final DOMSource source_ = new DOMSource(config_);

				StringWriter out_ = new StringWriter();
				final StreamResult result_ = new StreamResult(out_);
				t.transform(source_, result_);

				map.put(p.getKey() + ".xml", out_.getBuffer().toString());

				final Element pageXMLFile = config.createElement("page");
				pageXMLFile.appendChild(
						config.createTextNode(p.getKey() + ".xml")
						);
				root.appendChild(pageXMLFile);

				t.reset();
			}

			config.appendChild(root);

			final DOMSource source = new DOMSource(config);

			StringWriter out = new StringWriter();
			final StreamResult result = new StreamResult(out);
			t.transform(source, result);

			map.put(
					PropertiesSingleton.get(
							PlaceBooksAdminHelper.class.getClassLoader()
							).getProperty(PropertiesSingleton.IDEN_CONFIG, "config.xml"), 
							out.getBuffer().toString()
					);
		}
		catch (final Throwable e)
		{
			log.error(e.toString(), e);
		}

		return map;
	}

	private static final PlaceBook updatePlaceBook(final EntityManager manager, final PlaceBook placebook,
			final User currentUser, final Collection<PlaceBookItem> updateMedia)
	{
		PlaceBook result = placebook;

		if (placebook.getKey() != null)
		{
			final PlaceBook dbPlaceBook = manager.find(PlaceBook.class, placebook.getKey());

			if (dbPlaceBook != null)
			{
				final List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();
				for (final PlaceBookItem item : placebook.getItems())
				{
					PlaceBookItem resultItem = updatePlaceBookItem(manager, item, currentUser, updateMedia);
					resultItem.setPlaceBook(dbPlaceBook);
					items.add(resultItem);
				}
				dbPlaceBook.setItems(items);

				for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
				{
					dbPlaceBook.addMetadataEntry(entry.getKey(), entry.getValue());
				}

				dbPlaceBook.setGeometry(placebook.getGeometry());

				result = dbPlaceBook;
			}
		}

		if (result.getOwner() == null)
		{
			result.setOwner(currentUser);
		}

		if (result.getTimestamp() == null)
		{
			result.setTimestamp(new Date());
		}

		try
		{
			result.calcBoundary();
		}
		catch (final Exception e)
		{
			log.warn("Error Calculating boundry", e);
		}

		return result;
	}

	private static final PlaceBookBinder updatePlaceBookBinder(final EntityManager manager,
			final PlaceBookBinder binder, final User currentUser, final Collection<PlaceBookItem> updateMedia)
	{
		PlaceBookBinder result = binder;
		if (binder.getKey() != null)
		{
			final PlaceBookBinder dbBinder = manager.find(PlaceBookBinder.class, binder.getKey());
			// Get the DB version of this Binder and remove PlaceBooks that
			// are no longer part of the new Binder
			if (dbBinder != null)
			{
				for (final PlaceBook dbPlaceBook : dbBinder.getPlaceBooks())
				{
					final PlaceBook mPlaceBook = matchPlaceBook(dbPlaceBook, binder.getPlaceBooks());

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
					}
				}

				final List<PlaceBook> placebooks = new ArrayList<PlaceBook>();
				for (final PlaceBook placebook : binder.getPlaceBooks())
				{
					PlaceBook pbresult = updatePlaceBook(manager, placebook, currentUser, updateMedia);
					pbresult.setPlaceBookBinder(dbBinder);
					placebooks.add(pbresult);
				}
				dbBinder.setPlaceBooks(placebooks);

				for (final Entry<String, String> entry : binder.getMetadata().entrySet())
				{
					dbBinder.addMetadataEntry(entry.getKey(), entry.getValue());
				}

				dbBinder.setGeometry(binder.getGeometry());

				dbBinder.setPermissions(binder.getPermissions());

				result = dbBinder;
			}
		}

		if (result.getOwner() == null)
		{
			result.setOwner(currentUser);
		}

		if (result.getTimestamp() == null)
		{
			result.setTimestamp(new Date());
		}

		try
		{
			result.calcBoundary();
		}
		catch (final Exception e)
		{
			log.warn("Error Calculating boundry", e);
		}

		return result;
	}

	private static final PlaceBookItem updatePlaceBookItem(final EntityManager manager, final PlaceBookItem item,
			final User currentUser, final Collection<PlaceBookItem> updateMedia)
	{
		PlaceBookItem result = item;
		if (result.getKey() != null)
		{
			final PlaceBookItem oldItem = manager.find(PlaceBookItem.class, result.getKey());
			if (oldItem != null)
			{
				result = oldItem;
			}

			if (item.getSourceURL() != null && !item.getSourceURL().equals(result.getSourceURL()))
			{
				result.setSourceURL(item.getSourceURL());
				updateMedia.add(result);
			}
		}
		else
		{
			result.setTimestamp(new Date());
			manager.persist(result);

			if (item.getMetadata().get("originalItemID") != null)
			{
				final PlaceBookItem originalItem = manager.find(PlaceBookItem.class,
						item.getMetadata().get("originalItemID"));
				if (originalItem != null)
				{
					// We want to keep the metadata & parameters
					// from the new item
					final Map<String, String> meta = new HashMap<String, String>(result.getMetadata());
					final Map<String, Integer> para = new HashMap<String, Integer>(result.getParameters());
					result.update(originalItem);
					result.setMedataData(meta);
					result.setParameters(para);
				}
			}
			else
			{
				updateMedia.add(result);
			}
		}

		if (result.getOwner() == null)
		{
			result.setOwner(currentUser);
		}

		result.update(item);

		return result;
	}

}
