package placebooks.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import placebooks.model.AudioItem;
import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailTripsResponse;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.MediaItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBook.State;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.model.json.PlaceBookDistanceEntry;
import placebooks.model.json.PlaceBookItemDistanceEntry;
import placebooks.model.json.PlaceBookSearchEntry;
import placebooks.model.json.ServerInfo;
import placebooks.model.json.Shelf;
import placebooks.model.json.ShelfEntry;
import placebooks.model.json.UserShelf;
import placebooks.services.Service;
import placebooks.services.ServiceRegistry;

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
	public static class ItemData
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

		public boolean processItemData(final EntityManager pm, final String field, final String value)
		{
			if (field.equals("owner"))
			{
				setOwner(UserManager.getUser(pm, value));
			}
			else if (field.equals("sourceurl"))
			{
				try
				{
					setSourceURL(new URL(value));
				}
				catch (final java.net.MalformedURLException e)
				{
					log.error(e.toString(), e);
				}
			}
			else if (field.equals("geometry"))
			{
				try
				{
					setGeometry(new WKTReader().read(value));
				}
				catch (final ParseException e)
				{
					log.error(e.toString(), e);
				}
			}
			else
			{
				return false;
			}
			return true;
		}

		private void setGeometry(final Geometry geometry)
		{
			this.geometry = geometry;
		}

		private void setOwner(final User owner)
		{
			this.owner = owner;
		}

		private void setSourceURL(final URL sourceURL)
		{
			this.sourceURL = sourceURL;
		}
	}

	private static final Logger log = Logger.getLogger(PlaceBooksAdminController.class.getName());

	private static final int MEGABYTE = 1048576;

	@RequestMapping(value = "/sync/{service}")
	public void syncService(@PathVariable("service") final String serviceName)
	{
		Service service = ServiceRegistry.getService(serviceName);
		if(service != null)
		{
			final EntityManager manager = EMFSingleton.getEntityManager();
			final User user = UserManager.getCurrentUser(manager);
			
			service.sync(manager, user, true);
		}
	}
	
	@RequestMapping(value = "/view/{key}", method = RequestMethod.GET)
	public void viewPlaceBook(final HttpServletRequest req, final HttpServletResponse res, @PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();	
		try
		{
			final PlaceBook placebook = manager.find(PlaceBook.class, key);
			if (placebook != null)
			{
				try
				{
					if(placebook.getState() != State.PUBLISHED)
					{
						return;
					}

					String urlbase;
					if(req.getServerPort() != 80)
					{
						urlbase = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/";
					}
					else
					{
						urlbase = req.getScheme() + "://" + req.getServerName() + req.getContextPath() + "/";						
					}


					final PrintWriter writer = res.getWriter();
					writer.write("<!doctype html>");
					writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
					writer.write(" xmlns:og=\"http://ogp.me/ns#\"");
					writer.write(" xmlns:fb=\"http://www.facebook.com/2008/fbml\">");
					writer.write("<head>");
					writer.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");					
					writer.write("<title>" + placebook.getMetadataValue("title") + "</title>");
					writer.write("<meta property=\"og:title\" content=\"" + placebook.getMetadataValue("title") + "\"/>");
					writer.write("<meta property=\"og:type\" content=\"article\"/>");
					//					writer.write("<meta property=\"og:url\" content=\"" + urlbase + "#preview:" + placebook.getKey() + "\"/>");
					if(placebook.getMetadataValue("placebookImage") != null)
					{
						writer.write("<meta property=\"og:image\" content=\"" + urlbase + "placebooks/a/admin/serve/imageitem/"+placebook.getMetadataValue("placebookImage") + "\"/>");
					}
					writer.write("<meta property=\"og:site_name\" content=\"PlaceBooks\"/>");
					writer.write("<meta property=\"og:description\" content=\"" + placebook.getMetadataValue("description") + "\"/>");					
					writer.write("<meta http-equiv=\"Refresh\" content=\"0; url=" + urlbase + "#preview:" + placebook.getKey() + "\" />");					
					writer.write("<link rel=\"icon\" type=\"image/png\" href=\"../../../images/Logo_016.png\" />");
					writer.write("</head>");
					writer.write("<body></body>");
					writer.write("</html>");					
					writer.flush();
					writer.close();
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
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String accountPage()
	{
		return "account";
	}

	@RequestMapping(value = "/addLoginDetails", method = RequestMethod.POST)
	public void addLoginDetails(@RequestParam final String username, @RequestParam final String password,
			@RequestParam final String service, final HttpServletResponse res)
	{
		Service serviceImpl = ServiceRegistry.getService(service);
		if(service != null)
		{
			if(!serviceImpl.checkLogin(username, password))
			{
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);

				return;			
			}
		}		

		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);

		try
		{
			manager.getTransaction().begin();
			final LoginDetails loginDetails = new LoginDetails(user, service, null, username, password);
			manager.persist(loginDetails);
			user.add(loginDetails);
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
				log.error("Rolling login detail creation");
			}
			manager.close();
		}
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

		return "redirect:/index.html";
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	public void currentUser(final HttpServletRequest req, final HttpServletResponse res)
	{
		res.setContentType("application/json");
		final EntityManager entityManager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(entityManager);
			if (user == null)
			{

			}
			else
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					mapper.writeValue(sos, user);
					log.info("User: " + mapper.writeValueAsString(user));
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
		finally
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
			}
			entityManager.close();
		}
	}

	@RequestMapping(value = "/palette", method = RequestMethod.GET)
	public void getPaletteItemsJSON(final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);
		if (user == null)
		{
			try
			{
				log.info("User not logged in");
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				res.setContentType("application/json");
				res.getWriter().write("User not logged in");
				return;
			}
			catch (final Exception e)
			{
				log.error(e.getMessage(), e);
			}
		}
		final TypedQuery<PlaceBookItem> q = manager
				.createQuery(	"SELECT p FROM PlaceBookItem p WHERE p.owner = :owner AND p.placebook IS NULL",
						PlaceBookItem.class);
		q.setParameter("owner", user);

		final Collection<PlaceBookItem> pbs = q.getResultList();
		
		// Add preset items to the Palette
		final ArrayList<PlaceBookItem> presetItems = PresetItemsHelper.getPresetItems(user);
		pbs.addAll(presetItems);
		
		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		log.info("User " + user.getName());
		try
		{
			final Writer sos = res.getWriter();
			final ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			sos.write("[");
			boolean comma = false;
			for (final PlaceBookItem item : pbs)
			{
				if (comma)
				{
					sos.write(",");
				}
				else
				{
					comma = true;
				}
				sos.write(mapper.writeValueAsString(item));
			}
			sos.write("]");

			// mapper.enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT);

			res.setContentType("application/json");
			//log.debug("Palette Items: " + mapper.writeValueAsString(pbs));
			sos.flush();
			sos.close();
		}
		catch (final Exception e)
		{
			log.error(e.getMessage(), e);
		}

		manager.close();
	}

	@RequestMapping(value = "/placebookitem/{key}", method = RequestMethod.GET)
	public void getPlaceBookItemJSON(final HttpServletResponse res, @PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final PlaceBookItem item = manager.find(PlaceBookItem.class, key);
			if (item != null)
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
					final ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/json");
					mapper.writeValue(sos, item);
					log.info("PlacebookItem: " + mapper.writeValueAsString(item));
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
	}
	
	@RequestMapping(value = "/randomized/{count}", method = RequestMethod.GET)	
	public void getRandomPlaceBooksJSON(final HttpServletResponse res, @PathVariable("count") final int count)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final TypedQuery<PlaceBook> q = manager.createQuery("SELECT p FROM PlaceBook p WHERE p.state= :state",
																PlaceBook.class);
			q.setParameter("state", State.PUBLISHED);

			final List<PlaceBook> pbs = q.getResultList();
			final Collection<ShelfEntry> result = new ArrayList<ShelfEntry>();
			final Random random = new Random();
			for(int index = 0; index < count; index ++)
			{
				int rindex = random.nextInt(pbs.size());
				PlaceBookSearchEntry entry = new PlaceBookSearchEntry(pbs.get(rindex), 0);
				result.add(entry);
				pbs.remove(rindex);
			}
			
			log.info("Converting " + result.size() + " PlaceBooks to JSON");
			try
			{
				final Shelf shelf = new Shelf();
				shelf.setEntries(result);
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
		}
		finally
		{
			manager.close();
		}
	}
	
	@RequestMapping(value = "/placebook/{key}", method = RequestMethod.GET)
	public void getPlaceBookJSON(final HttpServletResponse res, @PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final PlaceBook placebook = manager.find(PlaceBook.class, key);
			if (placebook != null)
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
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
	}

	@RequestMapping(value = "/shelf", method = RequestMethod.GET)
	public void getPlaceBooksJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(manager);
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					ServiceRegistry.updateServices(manager, user);
					
				}
			}).start();
			if (user != null)
			{
				final TypedQuery<PlaceBook> q = manager.createQuery("SELECT p FROM PlaceBook p WHERE p.owner= :owner",
						PlaceBook.class);
				q.setParameter("owner", user);

				final Collection<PlaceBook> pbs = q.getResultList();
				log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
				log.info("User " + user.getName());
				try
				{
					final UserShelf shelf = new UserShelf(pbs, user);
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
			}
			else
			{
				try
				{
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					mapper.writeValue(sos, req.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
		finally
		{
			manager.close();
		}
	}

	@RequestMapping(value = "/admin/shelf/{owner}", method = RequestMethod.GET)
	public ModelAndView getPlaceBooksJSON(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("owner") final String owner)
	{
		if (owner.trim().isEmpty()) { return null; }

		final EntityManager pm = EMFSingleton.getEntityManager();
		final TypedQuery<User> uq = pm.createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class);
		uq.setParameter("email", owner.trim());
		try
		{
			final User user = uq.getSingleResult();

			final TypedQuery<PlaceBook> q = pm.createQuery(	"SELECT p FROM PlaceBook p WHERE p.owner = :user",
					PlaceBook.class);

			q.setParameter("user", user);
			final Collection<PlaceBook> pbs = q.getResultList();

			log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
			if (!pbs.isEmpty())
			{
				final UserShelf s = new UserShelf(pbs, user);
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

		}
		catch (final NoResultException e)
		{
			log.error(e.toString());
		}
		finally
		{
			pm.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
	public ModelAndView makePackage(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBook p = pm.find(PlaceBook.class, key);
		final File zipFile = PlaceBooksAdminHelper.makePackage(pm, p);
		if (zipFile == null) { return new ModelAndView("message", "text", "Making and compressing package"); }

		try
		{
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
			res.addHeader("Content-Length", Integer.toString(bos.size()));
			sos.write(bos.toByteArray());
			sos.flush();

		}
		catch (final IOException e)
		{
			log.error(e.toString(), e);
			return new ModelAndView("message", "text", "Error sending package");
		}
		finally
		{
			pm.close();
		}

		return null;
	}

	@RequestMapping(value = "/publishplacebook", method = RequestMethod.POST)
	public void publishPlaceBookJSON(final HttpServletResponse res, @RequestParam("placebook") final String json)
	{
		log.info("Publish Placebook: " + json);
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User currentUser = UserManager.getCurrentUser(manager);
		if (currentUser == null)
		{
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			try
			{
				res.getWriter().write("User not logged in");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			return;
		}

		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
			final PlaceBook placebook = mapper.readValue(json, PlaceBook.class);
			final PlaceBook result = PlaceBooksAdminHelper.savePlaceBook(manager, placebook);
			log.debug("Saved Placebook:" + mapper.writeValueAsString(result));			
			log.info("Published Placebook:" + mapper.writeValueAsString(result));
			final PlaceBook published = PlaceBooksAdminHelper.publishPlaceBook(manager, result);

			res.setContentType("application/json");
			final ServletOutputStream sos = res.getOutputStream();
			log.debug("Published Placebook:" + mapper.writeValueAsString(published));
			mapper.writeValue(sos, published);
			sos.flush();
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
	}

	@RequestMapping(value = "/saveplacebook", method = RequestMethod.POST)
	public void savePlaceBookJSON(final HttpServletResponse res, @RequestParam("placebook") final String json)
	{
		log.info("Save Placebook: " + json);
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User currentUser = UserManager.getCurrentUser(manager);
		if (currentUser == null)
		{
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			try
			{
				res.getWriter().write("User not logged in");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			return;
		}

		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
			final PlaceBook placebook = mapper.readValue(json, PlaceBook.class);
			final PlaceBook result = PlaceBooksAdminHelper.savePlaceBook(manager, placebook);

			res.setContentType("application/json");
			final ServletOutputStream sos = res.getOutputStream();
			log.debug("Saved Placebook:" + mapper.writeValueAsString(result));
			mapper.writeValue(sos, result);
			sos.flush();
		}
		catch (final Throwable e)
		{
			log.info(json);
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
	}

	@RequestMapping(value = "/admin/location_search/placebook/{geometry}", method = RequestMethod.GET)
	public ModelAndView searchLocationPlaceBooksGET(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("geometry") final String geometry)
	{
		Geometry geometry_ = null;
		try
		{
			geometry_ = new WKTReader().read(geometry);
		}
		catch (final ParseException e)
		{
			log.error(e.toString(), e);
			return null;
		}

		final EntityManager em = EMFSingleton.getEntityManager();
		final Collection<ShelfEntry> pbs = new ArrayList<ShelfEntry>();
		for (final Map.Entry<PlaceBook, Double> entry : PlaceBooksAdminHelper
				.searchLocationForPlaceBooks(em, geometry_))
		{
			final PlaceBook p = entry.getKey();
			if (p != null)
			{
				log.info("Search result: pb key=" + entry.getKey().getKey() + ", distance=" + entry.getValue());
				pbs.add(new PlaceBookDistanceEntry(p, entry.getValue()));
			}
		}
		em.close();

		final Shelf s = new Shelf();
		s.setEntries(pbs);
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

		return null;
	}

	@RequestMapping(value = "/admin/location_search/placebookitem/{geometry}", method = RequestMethod.GET)
	public ModelAndView searchLocationPlaceBookItemsGET(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("geometry") final String geometry)
	{
		Geometry geometry_ = null;
		try
		{
			geometry_ = new WKTReader().read(geometry);
		}
		catch (final ParseException e)
		{
			log.error(e.toString(), e);
			return null;
		}

		final EntityManager em = EMFSingleton.getEntityManager();
		final Collection<ShelfEntry> ps = new ArrayList<ShelfEntry>();
		for (final Map.Entry<PlaceBookItem, Double> entry : PlaceBooksAdminHelper
				.searchLocationForPlaceBookItems(em, geometry_))
		{
			final PlaceBookItem p = entry.getKey();
			if (p != null)
			{
				log.info("Search result: pbi key=" + entry.getKey().getKey() + ", distance=" + entry.getValue());
				ps.add(new PlaceBookItemDistanceEntry(p, entry.getValue()));
			}
		}
		em.close();

		final Shelf s = new Shelf();
		s.setEntries(ps);
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

		return null;

	}

	@RequestMapping(value = "/admin/search/{terms}", method = RequestMethod.GET)
	public ModelAndView searchGET(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("terms") final String terms)
	{
		final long timeStart = System.nanoTime();
		final long timeEnd;

		final EntityManager em = EMFSingleton.getEntityManager();
		final Collection<ShelfEntry> pbs = new ArrayList<ShelfEntry>();
		for (final Map.Entry<PlaceBook, Integer> entry : PlaceBooksAdminHelper.search(em, terms))
		{
			final PlaceBook p = entry.getKey();
			if (p != null && p.getState() == PlaceBook.State.PUBLISHED)
			{
				log.info("Search result: pb key=" + entry.getKey().getKey() + ", score=" + entry.getValue());
				pbs.add(new PlaceBookSearchEntry(p, entry.getValue()));
			}
		}
		em.close();

		final Shelf s = new Shelf();
		s.setEntries(pbs);
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

		timeEnd = System.nanoTime();
		log.info("Search execution time = " + (timeEnd - timeStart) + " ns");

		return null;
	}

	@RequestMapping(value = "/admin/search", method = RequestMethod.POST)
	public ModelAndView searchPOST(final HttpServletRequest req, final HttpServletResponse res)
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

		return searchGET(req, res, out.toString());
	}

	@RequestMapping(value = "/admin/serve/gpstraceitem/{key}", method = RequestMethod.GET)
	public ModelAndView serveGPSTraceItem(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();

		log.info("Serving GPS Trace for " + key);

		try
		{
			final GPSTraceItem g = em.find(GPSTraceItem.class, key);

			if (g != null)
			{
				final String trace = g.getTrace();
				res.setContentType("text/xml");
				final PrintWriter p = res.getWriter();
				p.print(trace);
				p.close();
			}
			else
			{
				throw new Exception("GPSTrace is null");
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/serve/imageitem/{key}", method = RequestMethod.GET)
	public ModelAndView serveImageItem(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		log.info("Serving Image Item: " + key);

		try
		{
			final ImageItem i = em.find(ImageItem.class, key);
			log.info("ImageItem path:" + (i.getPath()!=null ? i.getPath() : "null"));

			if (i != null)
			{
				em.getTransaction().begin();
				em.getTransaction().commit();

				if(i.getPath() == null )
				{
					log.error("No image path for " + i.getKey() + " attempting to redownload from " + i.getSourceURL());
					final URLConnection conn = CommunicationHelper.getConnection(i.getSourceURL());
					i.writeDataToDisk(i.getKey() + ".jpg", conn.getInputStream());
				}
				if (i.getPath() != null)
				{		
					try
					{
						File image = new File(i.getPath());
						if(!image.exists())
						{
							log.error("Image '" + i.getPath() + "' does not exist for " + i.getKey() + " attempting to redownload from " + i.getSourceURL());
							final URLConnection conn = CommunicationHelper.getConnection(i.getSourceURL());
							i.writeDataToDisk(i.getKey() + ".jpg", conn.getInputStream());
							image = new File(i.getPath());
						}
						final ImageInputStream iis = ImageIO.createImageInputStream(image);
						final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
						String fmt = "png";
						while (readers.hasNext())
						{
							final ImageReader read = readers.next();
							fmt = read.getFormatName();
						}

						final OutputStream out = res.getOutputStream();
						ImageIO.write(ImageIO.read(image), fmt, out);
						out.close();
					}
					catch (final IOException e)
					{
						log.error(e.toString());
						res.setStatus(404);
					}
				}
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/serve/{type}item/{key}", method = RequestMethod.GET)
	public void streamMediaItem(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("type") final String type, @PathVariable("key") final String key)
	{
		String path = null;
		final EntityManager em = EMFSingleton.getEntityManager();

		try
		{
			final MediaItem m = em.find(MediaItem.class, key);
			if (m != null)
			{
				em.getTransaction().begin();
				m.attemptPathFix();
				em.getTransaction().commit();
			}
			else
			{
				throw new Exception("Error getting media file, invalid key");
			}
			path = m.getPath();			
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		if (path == null) { return; }

		try
		{
			String type_ = null;
			if (type.trim().equalsIgnoreCase("video"))
			{
				type_ = "video";
			}
			else if (type.trim().equalsIgnoreCase("audio"))
			{
				type_ = "audio";
			}
			else
			{
				throw new Exception("Unrecognised media item type");
			}

			final File file = new File(path);

			final String[] split = PlaceBooksAdminHelper.getExtension(path);
			if (split == null) { throw new Exception("Error getting file suffix"); }

			final ServletOutputStream sos = res.getOutputStream();
			final long contentLength = file.length();
			res.setContentType(type_ + "/" + split[1]);
			res.addHeader("Accept-Ranges", "bytes");
			res.addHeader("Content-Length", Long.toString(contentLength));

			final FileInputStream fis = new FileInputStream(file);
			final BufferedInputStream bis = new BufferedInputStream(fis);

			final String range = req.getHeader("Range");
			long startByte = 0;
			long endByte = contentLength - 1;
			if (range != null)
			{
				if (range.startsWith("bytes="))
				{
					try
					{
						final String[] rangeItems = range.substring(6).split("-");
						startByte = Long.parseLong(rangeItems[0]);
						endByte = Long.parseLong(rangeItems[1]);
					}
					catch (final Exception e)
					{

					}
				}
			}

			res.addHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + contentLength);

			final int bufferLen = 2048;
			final byte data[] = new byte[bufferLen];
			int length;
			bis.skip(startByte);
			try
			{
				while ((length = bis.read(data, 0, bufferLen)) != -1)
				{
					sos.write(data, 0, length);
				}
				sos.flush();
			}
			finally
			{
				sos.close();
				fis.close();
			}
		}
		catch (final Throwable e)
		{
			// Enumeration headers = req.getHeaderNames();
			// while(headers.hasMoreElements())
			// {
			// String header = (String)headers.nextElement();
			// log.info(header + ": " + req.getHeader(header));
			// }
			log.error("Error serving " + type + " " + key);
		}
	}

	@RequestMapping(value = "/admin/add_item/upload", method = RequestMethod.POST)
	public ModelAndView uploadFile(final HttpServletRequest req)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final ItemData itemData = new ItemData();

		try
		{
			FileItem fileData = null;
			String name = null;
			String type = null;
			String itemKey = null;
			String placebookKey = null;

			manager.getTransaction().begin();
			@SuppressWarnings("unchecked")
			final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
			for (final FileItem item : items)
			{
				if (item.isFormField())
				{
					final String value = Streams.asString(item.getInputStream());
					if (!itemData.processItemData(manager, item.getFieldName(), value))
					{
						if (item.getFieldName().equals("key"))
						{
							placebookKey = value;
						}
						else if (item.getFieldName().equals("itemKey"))
						{
							itemKey = value;
						}
					}
				}
				else
				{
					name = item.getName();
					fileData = item;
					final String[] split = PlaceBooksAdminHelper.getExtension(item.getFieldName());
					if (split == null)
					{
						continue;
					}

					type = split[0];

					String dLimit = null, iden = null;
					if (type.equals("image"))
					{
						iden = PropertiesSingleton.IDEN_IMAGE_MAX_SIZE;
						dLimit = "1";
					}
					else if (type.equals("video"))
					{
						iden = PropertiesSingleton.IDEN_VIDEO_MAX_SIZE;
						dLimit = "20";
					}
					else if (type.equals("audio"))
					{
						iden = PropertiesSingleton.IDEN_AUDIO_MAX_SIZE;
						dLimit = "10";
					}

					if (dLimit != null && iden != null)
					{
						final int maxSize = Integer.parseInt(PropertiesSingleton.get(	PlaceBooksAdminHelper.class
								.getClassLoader())
								.getProperty(iden, dLimit));
						if ((item.getSize() / MEGABYTE) > maxSize) { throw new Exception("File too big, limit = "
								+ Integer.toString(maxSize) + "Mb"); }
					}
				}
			}

			if (itemData.getOwner() == null)
			{
				itemData.setOwner(UserManager.getCurrentUser(manager));
			}

			PlaceBookItem item = null;
			if (itemKey != null)
			{
				item = manager.find(PlaceBookItem.class, itemKey);
			}
			else if (placebookKey != null)
			{
				final PlaceBook placebook = manager.find(PlaceBook.class, placebookKey);

				if (type.equals("gpstrace"))
				{
					item = new GPSTraceItem(itemData.getOwner(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
				else if (type.equals("image"))
				{
					item = new ImageItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
				else if (type.equals("video"))
				{
					item = new VideoItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
				else if (type.equals("audio"))
				{
					item = new AudioItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
			}

			if (item instanceof MediaItem)
			{
				((MediaItem) item).setSourceURL(null);
				((MediaItem) item).writeDataToDisk(name, fileData.getInputStream());
			}
			else if (item instanceof GPSTraceItem)
			{
				((GPSTraceItem) item).setSourceURL(null);
				((GPSTraceItem) item).readTrace(fileData.getInputStream());
			}

			manager.getTransaction().commit();

			return new ModelAndView("message", "text", "Success");
		}
		catch (final Exception e)
		{
			log.error(e.toString(), e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}

			manager.close();
		}

		return new ModelAndView("message", "text", "Failed");
	}

	@RequestMapping(value = "/admin/serverinfo", method = RequestMethod.GET)
	public ModelAndView getServerInfoJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		final ServerInfo si = new ServerInfo();
		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			final ServletOutputStream sos = res.getOutputStream();
			res.setContentType("application/json");
			mapper.writeValue(sos, si);
			sos.flush();
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
		return null;
	}

}
