package placebooks.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import placebooks.model.AudioItem;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.PlaceBookItemSearchIndex;
import placebooks.model.PlaceBookSearchIndex;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.model.WebBundleItem;
import placebooks.model.json.Shelf;
import placebooks.utils.InitializeDatabase;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

// TODO: general todo is to do file checking to reduce unnecessary file writes, 
// part of which is ensuring new file writes in cases of changes
// 
// TODO: stop orphan / null field elements being added to database

@Controller
public class PlaceBooksAdminController
{
	// Helper class for passing around general PlaceBookItem data
	private static class ItemData
	{
		private Geometry geometry;
		private User owner;
		private URL sourceURL;

		public ItemData()
		{
		}

		public Geometry getGeometry()
		{
			return geometry;
		}

		public User getOwner()
		{
			return owner;
		}

		public URL getSourceURL()
		{
			return sourceURL;
		}

		public void setGeometry(final Geometry geometry)
		{
			this.geometry = geometry;
		}

		public void setOwner(final User owner)
		{
			this.owner = owner;
		}

		public void setSourceURL(final URL sourceURL)
		{
			this.sourceURL = sourceURL;
		}
	}

	private static final Logger log = Logger.getLogger(PlaceBooksAdminController.class.getName());

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
		catch (final ParserConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (final TransformerConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (final TransformerException e)
		{
			log.error(e.toString());
		}

		return null;
	}

	// Assumes currently open EntityManager
	private static boolean processItemData(final ItemData i, final EntityManager pm, final String field,
			final String value)
	{
		if (field.equals("owner"))
		{
			i.setOwner(UserManager.getUser(pm, value));
		}
		else if (field.equals("sourceurl"))
		{
			try
			{
				i.setSourceURL(new URL(value));
			}
			catch (final java.net.MalformedURLException e)
			{
				log.error(e.toString());
			}
		}
		else if (field.equals("geometry"))
		{
			try
			{
				i.setGeometry(new WKTReader().read(value));
			}
			catch (final ParseException e)
			{
				log.error(e.toString());
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String accountPage()
	{
		return "account";
	}

	@RequestMapping(value = "/admin/add_placebook", method = RequestMethod.POST)
	public ModelAndView addPlaceBook(@RequestParam final String owner, @RequestParam final String geometry)
	{
		if (owner != null)
		{
			Geometry geometry_ = null;
			try
			{
				geometry_ = new WKTReader().read(geometry);
			}
			catch (final ParseException e)
			{
				log.error(e.toString());
			}

			// If created inside getting the EntityManager, some fields are
			// null. Not sure why... TODO
			final PlaceBook p = new PlaceBook(null, geometry_);

			final EntityManager pm = EMFSingleton.getEntityManager();

			final User owner_ = UserManager.getUser(pm, owner);

			if (owner_ == null) { return new ModelAndView("message", "text", "User does not exist"); }

			p.setOwner(owner_);

			try
			{
				pm.getTransaction().begin();
				pm.persist(p);
				pm.getTransaction().commit();
			}
			finally
			{
				if (pm.getTransaction().isActive())
				{
					pm.getTransaction().rollback();
					log.error("Rolling current persist transaction back");
				}
			}
			pm.close();

			return new ModelAndView("message", "text", "PlaceBook added");
		}
		else
		{
			return new ModelAndView("message", "text", "Error in POST");
		}
	}

	@RequestMapping(value = "/admin/add_placebookitem_mapping/{type}", method = RequestMethod.POST)
	public ModelAndView addPlaceBookItemMapping(@RequestParam final String key, @RequestParam final String mKey,
			@RequestParam final String mValue, @PathVariable("type") final String type)
	{
		if (!type.equals("metadata") && !type.equals("parameter")) { return new ModelAndView("message", "text",
				"Error in type"); }

		if (key != null && mKey != null && mValue != null)
		{
			final EntityManager pm = EMFSingleton.getEntityManager();

			try
			{
				pm.getTransaction().begin();
				final PlaceBookItem p = pm.find(PlaceBookItem.class, key);
				if (type.equals("metadata"))
				{
					p.addMetadataEntry(mKey, mValue);
				}
				else if (type.equals("parameter"))
				{
					int iValue = -1;
					try
					{
						iValue = Integer.parseInt(mValue);
					}
					catch (final NumberFormatException e)
					{
						log.error("Error parsing parameter data value");
					}

					p.addParameterEntry(mKey, new Integer(iValue));
				}
				pm.getTransaction().commit();
			}
			finally
			{
				if (pm.getTransaction().isActive())
				{
					pm.getTransaction().rollback();
					log.error("Rolling current persist transaction back");
				}
			}
			pm.close();

			return new ModelAndView("message", "text", "Metadata/param added");
		}
		else
		{
			return new ModelAndView("message", "text", "Error in POST");
		}
	}

	@RequestMapping(value = "/admin/add_placebook_metadata", method = RequestMethod.POST)
	public ModelAndView addPlaceBookMetadata(@RequestParam final String key, @RequestParam final String mKey,
			@RequestParam final String mValue)
	{
		if (key != null && mKey != null && mValue != null)
		{
			final EntityManager pm = EMFSingleton.getEntityManager();

			try
			{
				pm.getTransaction().begin();
				final PlaceBook p = pm.find(PlaceBook.class, key);
				p.addMetadataEntry(mKey, mValue);
				pm.getTransaction().commit();
			}
			finally
			{
				if (pm.getTransaction().isActive())
				{
					pm.getTransaction().rollback();
					log.error("Rolling current persist transaction back");
				}
			}
			pm.close();

			return new ModelAndView("message", "text", "Metadata added");
		}
		else
		{
			return new ModelAndView("message", "text", "Error in POST");
		}
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminPage()
	{
		return "admin";
	}

	@RequestMapping(value = "/createUserAccount", method = RequestMethod.POST)
	public String createUserAccount(@RequestParam final String name, @RequestParam final String email,
			@RequestParam final String password)
	{
		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		final User user = new User(name, email, encoder.encodePassword(password, null));

		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			manager.getTransaction().begin();
			manager.persist(user);
			manager.getTransaction().commit();
		}
		catch (final Exception e)
		{
			log.error("Error creating user", e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling back user creation");
			}
			manager.close();
		}

		return "redirect:/login.html";
	}

	@RequestMapping(value = "/admin/add_item/webbundle", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView createWebBundle(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final ItemData itemData = new ItemData();
		WebBundleItem wbi = null;

		try
		{
			pm.getTransaction().begin();

			for (final Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();)
			{
				final String param = params.nextElement();
				final String value = req.getParameterValues(param)[0];
				if (!processItemData(itemData, pm, param, value))
				{
					final int delim = param.indexOf(".");
					if (delim == -1)
					{
						continue;
					}

					final String prefix = param.substring(0, delim), suffix = param
							.substring(delim + 1, param.length());

					if (prefix.contentEquals("url"))
					{
						try
						{
							final PlaceBook p = pm.find(PlaceBook.class, suffix);
							URL sourceURL = null;
							if (value.length() > 0)
							{
								sourceURL = new URL(value);
							}
							wbi = new WebBundleItem(null, null, sourceURL, new File(""));
							p.addItem(wbi);
						}
						catch (final java.net.MalformedURLException e)
						{
							log.error(e.toString());
						}
					}
				}

			}

			if (wbi != null)
			{
				wbi.setOwner(itemData.getOwner());
				wbi.setGeometry(itemData.getGeometry());
			}

			if (wbi == null || (wbi != null && (wbi.getSourceURL() == null || wbi.getOwner() == null))) { return new ModelAndView(
					"message", "text", "Error setting data elements"); }

			final StringBuffer wgetCmd = new StringBuffer();
			wgetCmd.append(PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_WGET, ""));

			if (wgetCmd.equals("")) { return new ModelAndView("message", "text", "Error in wget command"); }

			wgetCmd.append(" -U \"");
			wgetCmd.append(PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_USER_AGENT, ""));
			wgetCmd.append("\" ");

			final String webBundlePath = wbi.getWebBundlePath();

			wgetCmd.append("-P " + webBundlePath + " " + wbi.getSourceURL().toString());

			log.info("wgetCmd=" + wgetCmd.toString());

			if (new File(webBundlePath).exists() || new File(webBundlePath).mkdirs())
			{
				try
				{
					final Process p = Runtime.getRuntime().exec(wgetCmd.toString());

					final BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					String line = "";
					while ((line = stderr.readLine()) != null)
					{
						log.error("[wget output] " + line);
					}
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
					wbi.setWebBundle(webBundlePath + "/" + urlStr.substring(protocol + 3, urlStr.length()));
					log.info("wbi.getWebBundle() = " + wbi.getWebBundle());

				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}

			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
		}

		pm.close();

		return new ModelAndView("message", "text", "Scraped");
	}

	@RequestMapping(value = "/admin/delete/all_placebooks", method = RequestMethod.GET)
	public ModelAndView deleteAllPlaceBook()
	{

		final EntityManager pm = EMFSingleton.getEntityManager();

		try
		{
			pm.getTransaction().begin();
			/*
			 * Query query = pm.newQuery(PlaceBook.class); pbs = (List<PlaceBook>)query.execute();
			 * for (PlaceBook pb : pbs) { for (PlaceBookItem item : pb.getItems())
			 * item.deleteItemData(); }
			 */

			pm.createQuery("DELETE FROM PlaceBook p").executeUpdate();
			pm.createQuery("DELETE FROM PlaceBookItem p").executeUpdate();
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current delete all transaction back");
			}
		}

		pm.close();

		log.info("Deleted all PlaceBooks");

		return new ModelAndView("message", "text", "Deleted all PlaceBooks");

	}

	@RequestMapping(value = "/admin/delete_placebook/{key}", method = RequestMethod.GET)
	public ModelAndView deletePlaceBook(@PathVariable("key") final String key)
	{

		final EntityManager pm = EMFSingleton.getEntityManager();

		try
		{
			pm.getTransaction().begin();
			final PlaceBook p = pm.find(PlaceBook.class, key);
			pm.remove(p);
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current delete single transaction back");
			}
		}

		pm.close();

		log.info("Deleted PlaceBook");

		return new ModelAndView("message", "text", "Deleted PlaceBook: " + key);
	}

	@RequestMapping(value = "/admin/delete_placebookitem/{key}", method = RequestMethod.GET)
	public ModelAndView deletePlaceBookItem(@PathVariable("key") final String key)
	{

		final EntityManager pm = EMFSingleton.getEntityManager();

		try
		{
			pm.getTransaction().begin();
			final PlaceBookItem item = pm.find(PlaceBookItem.class, key);
			pm.remove(item);
			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current delete single transaction back");
			}
		}

		pm.close();

		log.info("Deleted PlaceBookItem " + key);

		return new ModelAndView("message", "text", "Deleted PlaceBookItem: " + key);

	}

	// TODO: currently uses startsWith for string search. Is this right??
	// owner.key query on key value for User works without startsWith
	@RequestMapping(value = "/placebook/{key}", method = RequestMethod.GET)
	public ModelAndView getPlaceBookJSON(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			PlaceBook placebook = null;
			if (key.equals("new"))
			{
				// Create new placebook
				placebook = new PlaceBook(UserManager.getCurrentUser(manager), null);

				manager.persist(placebook);
			}
			else
			{
				placebook = manager.find(PlaceBook.class, key);
			}

			if (placebook != null)
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/json");
					mapper.writeValue(sos, placebook);
					log.info("Placebook: " + mapper.writeValueAsString(placebook));
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			manager.close();
		}

		return null;
	}

	@RequestMapping(value = "/shelf", method = RequestMethod.GET)
	public ModelAndView getPlaceBooksJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);
		final TypedQuery<PlaceBook> q = manager.createQuery("SELECT p FROM PlaceBook p WHERE p.owner= :owner",
															PlaceBook.class);
		q.setParameter("owner", user);

		final Collection<PlaceBook> pbs = q.getResultList();
		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		log.info("User " + user.getName());
		try
		{
			final Shelf shelf = new Shelf(user, pbs);
			final ObjectMapper mapper = new ObjectMapper();
			log.info("Shelf: " + mapper.writeValueAsString(shelf));
			final ServletOutputStream sos = res.getOutputStream();
			res.setContentType("application/json");
			mapper.writeValue(sos, shelf);
			sos.flush();
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}

		manager.close();

		return null;
	}

	@RequestMapping(value = "/palette", method = RequestMethod.GET)
	public ModelAndView getPaletteItemsJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);
		final TypedQuery<PlaceBookItem> q = manager.createQuery("SELECT p FROM PlaceBookItem p WHERE p.owner= :owner AND p.placebook IS NULL",
															PlaceBookItem.class);
		q.setParameter("owner", user);

		final Collection<PlaceBookItem> pbs = q.getResultList();
		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		log.info("User " + user.getName());
		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			log.info("Shelf: " + mapper.writeValueAsString(pbs));
			final ServletOutputStream sos = res.getOutputStream();
			res.setContentType("application/json");
			mapper.writeValue(sos, pbs);
			sos.flush();
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}

		manager.close();

		return null;
	}

	// TODO: currently uses startsWith for string search. Is this right??
	// owner.key query on key value for User works without startsWith
	@RequestMapping(value = "/admin/reset", method = RequestMethod.GET)
	public ModelAndView reset(final HttpServletRequest req, final HttpServletResponse res)
	{
		InitializeDatabase.main(null);
		return null;
	}
	
	
	// TODO: currently uses startsWith for string search. Is this right??
	// owner.key query on key value for User works without startsWith
	@RequestMapping(value = "/admin/shelf/{owner}", method = RequestMethod.GET)
	public ModelAndView getPlaceBooksJSON(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("owner") final String owner)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();
		final TypedQuery<User> uq = pm.createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class);
		uq.setParameter("email", owner);
		final User user = uq.getSingleResult();

		final TypedQuery<PlaceBook> q = pm.createQuery(	"SELECT p FROM PlaceBook p WHERE p.owner = :user",
														PlaceBook.class);
		q.setParameter("user", user);
		final Collection<PlaceBook> pbs = q.getResultList();

		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		if (!pbs.isEmpty())
		{
			final Shelf s = new Shelf(user, pbs);
			try
			{
				final ObjectMapper mapper = new ObjectMapper();
				final ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/json");
				mapper.writeValue(sos, s);
				sos.flush();
			}
			catch (final IOException e)
			{
				log.error(e.toString());
			}
		}

		pm.close();

		return null;
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
	public ModelAndView makePackage(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBook p = pm.find(PlaceBook.class, key);

		final String out = placeBookToXML(p);

		if (out != null)
		{
			final String pkgPath = p.getPackagePath();
			if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
			{
				try
				{
					final FileWriter fw = new FileWriter(new File(pkgPath
							+ "/"
							+ PropertiesSingleton.get(this.getClass().getClassLoader())
									.getProperty(PropertiesSingleton.IDEN_CONFIG, "")));

					fw.write(out);
					fw.close();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}

			try
			{
				final String pkgZPath = PropertiesSingleton.get(this.getClass().getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

				final File zipFile = new File(pkgZPath + p.getKey() + ".zip");

				// Compress package path
				if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
				{

					final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
							zipFile)));
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

				// Serve up file from disk
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();
				final FileInputStream fis = new FileInputStream(zipFile);
				final BufferedInputStream bis = new BufferedInputStream(fis);

				final byte data[] = new byte[2048];
				int i;
				while ((i = bis.read(data, 0, 2048)) != -1)
				{
					bos.write(data, 0, i);
				}
				fis.close();

				final ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/zip");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + p.getKey() + ".zip\"");
				sos.write(bos.toByteArray());
				sos.flush();

			}
			catch (final IOException e)
			{
				log.error(e.toString());
			}
			pm.close();
			return null;
		}
		pm.close();

		return new ModelAndView("message", "text", "Error generating package");

	}

	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveplacebook", method = RequestMethod.POST)
	public ModelAndView savePlaceBookJSON(final HttpServletRequest req, final HttpServletResponse res,
			@RequestParam("placebook") final String json)
	{
		log.info("saveplacebook");
		final ObjectMapper mapper = new ObjectMapper();
		final EntityManager manager = EMFSingleton.getEntityManager();
		manager.getTransaction().begin();
		try
		{
			log.info(json);

			final PlaceBook placebook = mapper.readValue(json, PlaceBook.class);

			PlaceBook dbPlacebook = manager.find(PlaceBook.class, placebook.getKey());
			if(dbPlacebook == null)
			{
				dbPlacebook = placebook;
			}
			log.info(mapper.writeValueAsString(placebook));
	
			for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
			{
				dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
			}

			dbPlacebook.setItems(Collections.EMPTY_LIST);
			for (final PlaceBookItem item : placebook.getItems())
			{
				item.setOwner(dbPlacebook.getOwner());
				dbPlacebook.addItem(item);
				log.info("Added Item: " + mapper.writeValueAsString(item));
			}

			dbPlacebook.setGeometry(placebook.getGeometry());

			manager.merge(dbPlacebook);
			
			log.info("Added PlaceBook: " + mapper.writeValueAsString(dbPlacebook));
			
			manager.getTransaction().commit();
			
		}
		catch (final Throwable e)
		{
			log.warn(e.getMessage(), e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}

		return null;
	}

	// Helper methods below

	@RequestMapping(value = "/admin/search/{terms}", method = RequestMethod.GET)
	public ModelAndView searchGET(@PathVariable("terms") final String terms)
	{
		final long timeStart = System.nanoTime();
		final long timeEnd;

		final Set<String> search = SearchHelper.getIndex(terms, 5);

		final EntityManager pm = EMFSingleton.getEntityManager();

		final TypedQuery<PlaceBookSearchIndex> query1 = pm.createQuery(	"SELECT p FROM PlaceBookSearchIndex p",
																		PlaceBookSearchIndex.class);
		final List<PlaceBookSearchIndex> pbIndexes = query1.getResultList();

		// Search rationale: ratings are accumulated per PlaceBook for that
		// PlaceBook plus any PlaceBookItems
		final Map<String, Integer> hits = new HashMap<String, Integer>();

		for (final PlaceBookSearchIndex index : pbIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			Integer rating = hits.get(index.getPlaceBook().getKey());
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(index.getPlaceBook().getKey(), new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = pm.createQuery(	"SELECT p FROM PlaceBookItemSearchIndex p",
																			PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			final String key = index.getPlaceBookItem().getPlaceBook().getKey();
			Integer rating = hits.get(key);
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(key, new Integer(keywords.size() + rating.intValue()));
		}

		pm.close();

		final StringBuffer out = new StringBuffer();
		for (final Map.Entry<String, Integer> entry : hits.entrySet())
		{
			out.append("key=" + entry.getKey() + ", score=" + entry.getValue() + "<br>");
		}

		for (final String string : search)
		{
			out.append("keywords=" + string + ",");
		}

		timeEnd = System.nanoTime();
		out.append("<br>Execution time = " + (timeEnd - timeStart) + " ns");

		return new ModelAndView("message", "text", "search results:<br>" + out.toString());
	}

	@RequestMapping(value = "/admin/search", method = RequestMethod.POST)
	public ModelAndView searchPOST(final HttpServletRequest req)
	{
		final StringBuffer out = new StringBuffer();
		final String[] terms = req.getParameter("terms").split("\\s");
		for (int i = 0; i < terms.length; ++i)
		{
			out.append(terms[i]);
			if (i < terms.length - 1)
			{
				out.append("+");
			}
		}

		return searchGET(out.toString());
	}

	@RequestMapping(value = "/admin/add_item/upload", method = RequestMethod.POST)
	public ModelAndView uploadFile(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final ItemData itemData = new ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.getTransaction().begin();

			final FileItemIterator i = new ServletFileUpload().getItemIterator(req);
			while (i.hasNext())
			{
				final FileItemStream item = i.next();
				if (item.isFormField())
				{
					processItemData(itemData, pm, item.getFieldName(), Streams.asString(item.openStream()));
				}
				else
				{
					String property = null;
					final String field = item.getFieldName();
					final int delim = field.indexOf(".");
					if (delim == -1)
					{
						continue;
					}

					final String prefix = field.substring(0, delim), suffix = field
							.substring(delim + 1, field.length());

					final PlaceBook p = pm.find(PlaceBook.class, suffix);

					if (prefix.contentEquals("image"))
					{
						pbi = new ImageItem(null, null, null, null);
						p.addItem(pbi);

						final InputStream input = item.openStream();
						final BufferedImage b = ImageIO.read(input);
						input.close();
						((ImageItem) pbi).setImage(b);

						continue;
					}
					else if (prefix.contentEquals("gpstrace"))
					{
						Document gpxDoc = null;
						// StringReader reader = new StringReader(value);
						final InputStream reader = item.openStream();
						final InputSource source = new InputSource(reader);
						final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						gpxDoc = builder.parse(source);
						reader.close();
						pbi = new GPSTraceItem(null, null, null, gpxDoc);
						p.addItem(pbi);

						continue;
					}
					else if (prefix.contentEquals("video"))
					{
						property = PropertiesSingleton.IDEN_VIDEO;
					}
					else if (prefix.contentEquals("audio"))
					{
						property = PropertiesSingleton.IDEN_AUDIO;
					}
					else
					{
						return new ModelAndView("message", "text", "Unsupported file type");
					}

					final String path = PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(property,
																												"");

					if (!new File(path).exists() && !new File(path).mkdirs()) { return new ModelAndView("message",
							"text", "Failed to write file"); }

					File file = null;

					final int extIdx = item.getName().lastIndexOf(".");
					final String ext = item.getName().substring(extIdx + 1, item.getName().length());

					if (property.equals(PropertiesSingleton.IDEN_VIDEO))
					{
						pbi = new VideoItem(null, null, null, new File(""));
						p.addItem(pbi);
						((VideoItem) pbi).setVideo(path + "/" + pbi.getKey() + "." + ext);

						file = new File(((VideoItem) pbi).getVideo());
					}
					else if (property.equals(PropertiesSingleton.IDEN_AUDIO))
					{
						pbi = new AudioItem(null, null, null, new File(""));
						p.addItem(pbi);
						((AudioItem) pbi).setAudio(path + "/" + pbi.getKey() + "." + ext);

						file = new File(((AudioItem) pbi).getAudio());
					}

					final InputStream input = item.openStream();
					final OutputStream output = new FileOutputStream(file);
					int byte_;
					while ((byte_ = input.read()) != -1)
					{
						output.write(byte_);
					}
					output.close();
					input.close();

					log.info("Wrote " + prefix + " file " + file.getAbsolutePath());

				}

			}

			if (pbi == null || itemData.getOwner() == null) { return new ModelAndView("message", "text",
					"Error setting data elements"); }

			pbi.setOwner(itemData.getOwner());
			pbi.setSourceURL(itemData.getSourceURL());
			pbi.setGeometry(itemData.getGeometry());

			pm.getTransaction().commit();
		}
		catch (final FileUploadException e)
		{
			log.error(e.toString());
		}
		catch (final ParserConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (final SAXException e)
		{
			log.error(e.toString());
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
		}

		pm.close();

		return new ModelAndView("message", "text", "Done");
	}

	@RequestMapping(value = "/admin/add_item/text", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView uploadText(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final ItemData itemData = new ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.getTransaction().begin();

			for (final Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();)
			{
				final String param = params.nextElement();
				final String value = req.getParameterValues(param)[0];
				if (!processItemData(itemData, pm, param, value))
				{
					final int delim = param.indexOf(".");
					if (delim == -1)
					{
						continue;
					}

					final String prefix = param.substring(0, delim), suffix = param
							.substring(delim + 1, param.length());

					final PlaceBook p = pm.find(PlaceBook.class, suffix);

					if (prefix.contentEquals("text"))
					{
						String value_ = null;
						if (value.length() > 0)
						{
							value_ = value;
						}
						pbi = new TextItem(null, null, null, value_);
						p.addItem(pbi);
					}
				}

			}

			if ((pbi != null && ((TextItem) pbi).getText() == null) || pbi == null || itemData.getOwner() == null) { return new ModelAndView(
					"message", "text", "Error setting data elements"); }

			pbi.setOwner(itemData.getOwner());
			pbi.setGeometry(itemData.getGeometry());
			pbi.setSourceURL(itemData.getSourceURL());

			pm.getTransaction().commit();
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
		}

		pm.close();

		return new ModelAndView("message", "text", "TextItem added");
	}

}
