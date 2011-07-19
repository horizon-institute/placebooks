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
import placebooks.model.PlaceBook.State;
import placebooks.model.PlaceBookItem;
import placebooks.model.PlaceBookItemSearchIndex;
import placebooks.model.PlaceBookSearchIndex;
import placebooks.model.User;
import placebooks.model.WebBundleItem;

public final class PlaceBooksAdminHelper
{
	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminHelper.class.getName());

	public static final String[] getExtension(final String field)
	{
		final int delim = field.indexOf(".");
		if (delim == -1) { return null; }

		final String[] out = new String[2];
		out[0] = field.substring(0, delim);
		out[1] = field.substring(delim + 1, field.length());

		return out;
	}

	public static final File makePackage(final EntityManager em, 
										 final PlaceBook p)
	{
		
		// Mapping
		// TODO: PlaceBook can only have 1 MapImageItem for the moment
		if (!p.hasPlaceBookItemClass(MapImageItem.class))
		{
			try
			{
				final File mapGeom = TileHelper.getMap(p);
				em.getTransaction().begin();
				MapImageItem mii = new MapImageItem(null, null, null, null);
				p.addItem(mii);
				mii.setPlaceBook(p);
				mii.setOwner(p.getOwner());
				mii.setGeometry(p.getGeometry());
				mii.setPath(mapGeom.getPath());
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
			log.info("PlaceBook already has MapImageItem");

		final String out = placeBookToXML(p);

		if (out == null) { return null; }

		final String pkgPath = p.getPackagePath();
		if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
		{
			try
			{
				final FileWriter fw = new FileWriter(new File(pkgPath
						+ "/"
						+ PropertiesSingleton
							.get(PlaceBooksAdminHelper.class.getClassLoader())
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

		final String pkgZPath = PropertiesSingleton
									.get(PlaceBooksAdminHelper.class.getClassLoader())
									.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

		final File zipFile = new File(pkgZPath + p.getKey() + ".zip");

		try
		{
			// Compress package path
			if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
			{

				final ZipOutputStream zos = 
					new ZipOutputStream(new BufferedOutputStream(
						new FileOutputStream(zipFile))
					);
				zos.setMethod(ZipOutputStream.DEFLATED);

				final ArrayList<File> files = new ArrayList<File>();
				getFileListRecursive(new File(pkgPath), files);

				final File currentDir = new File(".");
				log.info("Current working directory is " 
						 + currentDir.getAbsolutePath());

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

	// Takes a PlaceBook, copies it, and returns the (published) copy
	public static final PlaceBook publishPlaceBook(final EntityManager em, 
												   final PlaceBook p)
	{
		PlaceBook p_ = null;
		try
		{
			em.getTransaction().begin();
			p_ = new PlaceBook(p);
			p_.setState(State.PUBLISHED);
			em.persist(p_);
			em.getTransaction().commit();

			// Copy data on disk now that keys have been generated
			for (final PlaceBookItem item : p_.getItems())
			{
				if (item instanceof MediaItem)
				{
					final String data = ((MediaItem) item).getPath();
					((MediaItem) item).writeDataToDisk(data, 
											new FileInputStream(new File(data)));
				}
				else if (item instanceof WebBundleItem)
				{
					final WebBundleItem wbi = (WebBundleItem) item;
					final String data = wbi.generateWebBundlePath();
					FileUtils.copyDirectory(new File(wbi.getWebBundlePath()), 
											new File(data));
					wbi.setWebBundlePath(data);
				}
			}

		}
		catch (final Throwable e)
		{
			log.error("Error creating PlaceBook copy", e);
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				log.error("Rolling back PlaceBook copy");
			}
		}
		return p_;
	}

	public static final PlaceBook savePlaceBook(final EntityManager manager, final PlaceBook p)
	{
		try
		{

			final User currentUser = UserManager.getCurrentUser(manager);
			manager.getTransaction().begin();
			PlaceBook placebook = p;
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
							if(item instanceof GPSTraceItem)
							{
							}
							item.deleteItemData();
							manager.remove(item);
						}
					}

					dbPlacebook.setItems(placebook.getItems());
					for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
					{
						dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
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
							// We want to keep the metadata & parameters from the new item
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
			// placebook.calcBoundary();
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
						// final WebBundleItem webItem = (WebBundleItem)item;
						// TODO
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

	public static final boolean scrape(final WebBundleItem wbi)
	{

		final StringBuffer wgetCmd = new StringBuffer();
		wgetCmd.append(PropertiesSingleton
						.get(PlaceBooksAdminHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_WGET, ""));

		if (wgetCmd.equals("")) { return false; }

		// TODO: User Agent string does not work for some reason
		/*wgetCmd.append(" -U \"");
		wgetCmd.append(PropertiesSingleton
						.get(PlaceBooksAdminHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_USER_AGENT, ""));
		wgetCmd.append("\" ");*/

		final String webBundlePath = wbi.generateWebBundlePath();

		wgetCmd.append("-P " + webBundlePath + " " 
					   + wbi.getSourceURL().toString());

		log.info("wgetCmd=" + wgetCmd.toString());

		if (new File(webBundlePath).exists() || 
			new File(webBundlePath).mkdirs())
		{
			final int timeout = 
				Integer.parseInt(PropertiesSingleton
									.get(PlaceBooksAdminHelper.class.getClassLoader())
									.getProperty(
										PropertiesSingleton.IDEN_WGET_TIMEOUT, 
										"10000")
				);
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

	public static final Set<Map.Entry<PlaceBook, Integer>> 
		search(final EntityManager em, final String terms)
	{

		final Set<String> search = SearchHelper.getIndex(terms, 5);

		final TypedQuery<PlaceBookSearchIndex> query1 = 
			em.createQuery("SELECT p FROM PlaceBookSearchIndex p",
						   PlaceBookSearchIndex.class);
		final List<PlaceBookSearchIndex> pbIndexes = query1.getResultList();

		// Search rationale: ratings are accumulated per PlaceBook for that
		// PlaceBook plus any PlaceBookItems
		final Map<PlaceBook, Integer> hits = new HashMap<PlaceBook, Integer>();

		for (final PlaceBookSearchIndex index : pbIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			Integer rating = hits.get(index.getPlaceBook());
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(index.getPlaceBook(), new Integer(keywords.size() 
					 + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = 
			em.createQuery("SELECT p FROM PlaceBookItemSearchIndex p",
						   PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = 
			query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			final PlaceBook p = index.getPlaceBookItem().getPlaceBook();
			Integer rating = hits.get(p);
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(p, new Integer(keywords.size() + rating.intValue()));
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

	private static String placeBookToXML(final PlaceBook p)
	{
		StringWriter out = null;

		try
		{

			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document config = builder.newDocument();

			final Element root = p.createConfigurationRoot(config);
			config.appendChild(root);

			// Note: ImageItem, VideoItem and AudioItem write their data to a
			// package directly as well as creating XML configuration
			for (final PlaceBookItem item : p.getItems())
			{
				item.appendConfiguration(config, root);
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

}
