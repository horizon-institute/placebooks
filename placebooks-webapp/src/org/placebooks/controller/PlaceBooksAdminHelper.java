package org.placebooks.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

import org.apache.commons.io.FileUtils;
import org.placebooks.model.GPSTraceItem;
import org.placebooks.model.MapImageItem;
import org.placebooks.model.MediaItem;
import org.placebooks.model.PlaceBook;
import org.placebooks.model.PlaceBookBinder;
import org.placebooks.model.PlaceBookBinder.State;
import org.placebooks.model.PlaceBookBinderSearchIndex;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.PlaceBookItemSearchIndex;
import org.placebooks.model.User;
import org.placebooks.model.WebBundleItem;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;

import com.vividsolutions.jts.geom.Geometry;

public final class PlaceBooksAdminHelper
{
	public static final String[] getExtension(final String field)
	{
		final int delim = field.indexOf(".");
		if (delim == -1) { return null; }

		final String[] out = new String[2];
		out[0] = field.substring(0, delim);
		out[1] = field.substring(delim + 1, field.length());

		return out;
	}

	public static final File makePackage(final EntityManager em, final PlaceBookBinder pb, final Parser parser)
	{
		for (final PlaceBook p : pb.getPlaceBooks())
		{
			try
			{
				em.getTransaction().begin();
				final List<PlaceBookItem> toDel = new ArrayList<PlaceBookItem>();

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
				{
					p.removeItem(pbi);
				}

				em.getTransaction().commit();
			}
			catch (final Throwable e)
			{
				Log.error(e);
			}
			finally
			{
				if (em.getTransaction().isActive())
				{
					em.getTransaction().rollback();
					Log.error("Rolling current persist transaction back");
				}
			}

			// Create overview map
			for (final PlaceBookItem item : p.getItems())
			{
				if (item instanceof GPSTraceItem)
				{
					try
					{
						p.calcBoundary();
						if (p.getGeometry() != null)
						{
							em.getTransaction().begin();
							final MapImageItem mii = OSMTileHelper.getMap(p);
							p.addItem(mii);			
							mii.setParameters(item.getParameters());
							Log.debug("Adding overview MapImageItem");

							em.getTransaction().commit();
							
							break;
						}
						else
						{
							Log.error("Fatal error in creating map, boundary for " + "PlaceBook page was null");
						}
					}
					catch (final Throwable e)
					{
						Log.error(e);
					}
					finally
					{
						if (em.getTransaction().isActive())
						{
							em.getTransaction().rollback();
							Log.error("Rolling current persist transaction back");
						}
					}
				}
			}
		}

		// Clean package dir
		try
		{
			final File clean = new File(pb.getPackagePath());
			if (clean.exists())
			{
				FileUtils.deleteDirectory(clean);
			}
		}
		catch (final Throwable e)
		{
			Log.error(e);
		}

		final String pkgPath = pb.getPackagePath();
		if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
		{
			try
			{
				final FileWriter fw = new FileWriter(new File(pkgPath + "/data.json"));
				fw.write(parser.write(pb));
				fw.close();
			}
			catch (final IOException e)
			{
				Log.error(e);
				return null;
			}
		}

		for (final PlaceBook p : pb.getPlaceBooks())
		{
			for (final PlaceBookItem item : p.getItems())
			{
				try
				{
					item.copyDataToPackage();
				}
				catch (final Exception e)
				{
					Log.error(e);
				}
			}
		}

		final String pkgZPath = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

		final File zipFile = new File(pkgZPath + "/" + pb.getKey() + ".placebook");

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
				Log.info("Current working directory is " + currentDir.getAbsolutePath());

				final byte data[] = new byte[2048];
				BufferedInputStream bis = null;
				for (final File file : files)
				{
					Log.info("Adding file to archive: " + file.getPath());
					final FileInputStream fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis, 2048);
					zos.putNextEntry(new ZipEntry(file.getName()));

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
				Log.error("Package path doesn't exist or can't be created");
				return null;
			}
		}
		catch (final IOException e)
		{
			Log.error(e);
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
					if (item instanceof WebBundleItem)
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
			Log.error(e);
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				Log.error("Rolling back PlaceBookBinder copy");
			}
		}
		return pb_;
	}

	public static final PlaceBookBinder savePlaceBookBinder(final EntityManager manager,
			final PlaceBookBinder placeBookBinder, final User user)
	{
		try
		{
			manager.getTransaction().begin();

			final Collection<PlaceBookItem> updateMedia = new ArrayList<PlaceBookItem>();
			PlaceBookBinder binder = updatePlaceBookBinder(manager, placeBookBinder, user, updateMedia);
			for (PlaceBook placeBook : binder.getPlaceBooks())
			{
				placeBook = manager.merge(placeBook);
			}

			for (final PlaceBookItem item : updateMedia)
			{
				try
				{
					final String original = item.getMetadataValue("originalItemID");
					boolean originalFound = false;
					if (original != null)
					{
						final PlaceBookItem originalItem = manager.find(PlaceBookItem.class, original);
						if (originalItem != null)
						{
							originalFound = true;
							item.update(originalItem);
						}
					}
					if (!originalFound)
					{
						if (item instanceof MediaItem)
						{
							final MediaItem mediaItem = (MediaItem) item;
							// Check if the file exists...
							if (mediaItem.attemptPathFix() == null)
							{
								mediaItem.RedownloadItem();
							}
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
				}
				catch (final Exception e)
				{
					Log.error(e);
				}
			}

			// Rebuild search index
			binder.rebuildSearchIndex();

			Log.info("Search Indexes " + binder.getSearchIndex().getIndex().size());
			
			binder = manager.merge(binder);

			manager.getTransaction().commit();

			return manager.find(PlaceBookBinder.class, binder.getKey());
		}
		catch (final Throwable e)
		{
			Log.error(e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				Log.error("Rolling back");
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

		Log.info("wgetCmd=" + wgetCmd.toString());

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
			Log.info("wbi.getWebBundle() = " + wbi.getWebBundle());

			return true;
		}

		return false;
	}

	public static final Set<Map.Entry<PlaceBookBinder, Integer>> search(final EntityManager em, final String terms)
	{

		final int nTerms = Integer.parseInt(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_SEARCH_TERMS, "5"));

		final Set<String> search = SearchHelper.getIndex(terms, nTerms);

		final TypedQuery<PlaceBookBinderSearchIndex> query1 = em
				.createQuery("SELECT p FROM PlaceBookBinderSearchIndex p", PlaceBookBinderSearchIndex.class);
		final List<PlaceBookBinderSearchIndex> pbIndexes = query1.getResultList();

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
			hits.put(index.getPlaceBookBinder(), new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = em.createQuery(	"SELECT p FROM PlaceBookItemSearchIndex p",
																			PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			if (index.getPlaceBookItem() == null || index.getPlaceBookItem().getPlaceBook() == null
					|| index.getPlaceBookItem().getPlaceBook().getPlaceBookBinder() == null)
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
				try
				{
					hits.put(p, p.getGeometry().distance(geometry));
				}
				catch(Exception e)
				{
					Log.error("Error getting distance between "+ geometry + " and "  + p.getGeometry(), e);
				}
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

	private static final PlaceBook updatePlaceBook(final EntityManager manager, final PlaceBook placebook,
			final User currentUser, final Collection<PlaceBookItem> updateMedia)
	{
		PlaceBook result = placebook;

		if (placebook.getKey() != null)
		{
			final PlaceBook dbPlaceBook = manager.find(PlaceBook.class, placebook.getKey());

			if (dbPlaceBook != null)
			{
				for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
				{
					dbPlaceBook.addMetadataEntry(entry.getKey(), entry.getValue());
				}

				dbPlaceBook.setGeometry(placebook.getGeometry());

				result = dbPlaceBook;
			}
		}

		final List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();
		for (final PlaceBookItem item : placebook.getItems())
		{
			final PlaceBookItem resultItem = updatePlaceBookItem(manager, item, currentUser, updateMedia);
			resultItem.setPlaceBook(result);
			items.add(resultItem);
		}
		result.setItems(items);

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
			Log.error(e);
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
					final PlaceBook pbresult = updatePlaceBook(manager, placebook, currentUser, updateMedia);
					pbresult.setPlaceBookBinder(dbBinder);
					placebooks.add(pbresult);
				}
				dbBinder.setPlaceBooks(placebooks);

				dbBinder.setMetadata(binder.getMetadata());
				dbBinder.setParameters(binder.getParameters());

				dbBinder.setGeometry(binder.getGeometry());

				dbBinder.setPermissions(binder.getPermissions());

				result = dbBinder;
			}
		}

		final List<PlaceBook> placebooks = new ArrayList<PlaceBook>();
		for (final PlaceBook placebook : binder.getPlaceBooks())
		{
			final PlaceBook pbresult = updatePlaceBook(manager, placebook, currentUser, updateMedia);
			pbresult.setPlaceBookBinder(result);
			placebooks.add(pbresult);
		}
		result.setPlaceBooks(placebooks);

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
			Log.error(e);
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
