package placebooks.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import placebooks.model.MediaItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.PlaceBookItemSearchIndex;
import placebooks.model.PlaceBookSearchIndex;
import placebooks.model.WebBundleItem;


public final class PlaceBooksAdminHelper
{
	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminHelper.class.getName());

	// Takes a PlaceBook, copies it, and returns the (published) copy
	public static final PlaceBook publishPlaceBook(final EntityManager em,
												   final PlaceBook p)
	{
		PlaceBook p_ = null;
		try
		{
			em.getTransaction().begin();
			p_ = new PlaceBook(p);
			p_.setState(PlaceBook.STATE_PUBLISHED);
			em.persist(p_);
			em.getTransaction().commit();
			
			// Copy data on disk now that keys have been generated
			for (final PlaceBookItem item : p_.getItems())
			{
				if (item instanceof MediaItem)
				{
					final String data = ((MediaItem)item).getPath();
					((MediaItem)item).writeDataToDisk(data, 
										 new FileInputStream(new File(data)));
				}
				else if (item instanceof WebBundleItem)
				{
					final WebBundleItem wbi = (WebBundleItem)item;
					final String data = wbi.generateWebBundlePath(); 
					FileUtils.copyDirectory(
						new File(wbi.getWebBundlePath()), 
						new File(data)
					);
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


	public static final boolean scrape(WebBundleItem wbi)
	{

		final StringBuffer wgetCmd = new StringBuffer();
		wgetCmd.append(PropertiesSingleton
						.get(PlaceBooksAdminHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_WGET, ""));

		if (wgetCmd.equals(""))
			return false;

		wgetCmd.append(" -U \"");
		wgetCmd.append(PropertiesSingleton
						.get(PlaceBooksAdminHelper.class.getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_USER_AGENT, ""));
		wgetCmd.append("\" ");

		final String webBundlePath = wbi.generateWebBundlePath();

		wgetCmd.append("-P " + webBundlePath + " " + 
					   wbi.getSourceURL().toString());

		log.info("wgetCmd=" + wgetCmd.toString());

		if (new File(webBundlePath).exists() || 
			new File(webBundlePath).mkdirs())
		{
			try
			{
				final Process p = Runtime.getRuntime().exec(wgetCmd.toString());

				final BufferedReader stderr = 
					new BufferedReader(	
						new InputStreamReader(p.getErrorStream())
					);

				String line = "";
				while ((line = stderr.readLine()) != null)
					log.error("[wget output] " + line);
				
				log.info("Waiting for process...");
				try
				{
					p.waitFor();
				}
				catch (final InterruptedException e)
				{
					log.error(e.toString());
				}
				log.info("... Process ended");

				final String urlStr = wbi.getSourceURL().toString();
				final int protocol = urlStr.indexOf("://");
				wbi.setWebBundlePath(webBundlePath);
				wbi.setWebBundleName(
					urlStr.substring(protocol + 3, urlStr.length())
				);
				log.info("wbi.getWebBundle() = " + wbi.getWebBundle());

			}
			catch (final IOException e)
			{
				log.error(e.toString());
				return false;
			}

			return true;
		}

		return false;
	}

	public static final File makePackage(PlaceBook p)
	{
		final String out = placeBookToXML(p);

		if (out == null)
			return null;
		
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
				log.error(e.toString());
				return null;
			}
		}

		final String pkgZPath = 
			PropertiesSingleton
				.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

		final File zipFile = new File(pkgZPath + p.getKey() + ".zip");

		try 
		{
			// Compress package path
			if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
			{

				final ZipOutputStream zos = 
					new ZipOutputStream(
						new BufferedOutputStream(
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
			log.error(e.toString());
			return null;
		}
	
		return zipFile;
	}

	public static final Set<Map.Entry<PlaceBook, Integer>> search(String terms)
	{

		final Set<String> search = SearchHelper.getIndex(terms, 5);

		final EntityManager pm = EMFSingleton.getEntityManager();

		final TypedQuery<PlaceBookSearchIndex> query1 = 
			pm.createQuery("SELECT p FROM PlaceBookSearchIndex p",
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
			hits.put(index.getPlaceBook(), 
					 new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = 
			pm.createQuery("SELECT p FROM PlaceBookItemSearchIndex p",
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

		pm.close();

		return hits.entrySet();
	}

	private static String placeBookToXML(final PlaceBook p)
	{
		StringWriter out = null;

		try
		{

			final DocumentBuilder builder = 
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
			log.error(e.toString());
		}

		return null;
	}

	private static void getFileListRecursive(final File path, 
											 final List<File> out)
	{
		final List<File> files = 
			new ArrayList<File>(Arrays.asList(path.listFiles()));

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


	public static final String[] getExtension(final String field)
	{
		final int delim = field.indexOf(".");
		if (delim == -1)
			return null;

		String[] out = new String[2];
		out[0] = field.substring(0, delim);
		out[1] = field.substring(delim + 1, field.length());

		return out;
	}

}
