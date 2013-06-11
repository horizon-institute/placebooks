package placebooks.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
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
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import placebooks.client.logger.Log;
import placebooks.model.AudioItem;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.MediaItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookBinder;
import placebooks.model.PlaceBookBinder.State;
import placebooks.model.PlaceBookGroup;
import placebooks.model.PlaceBookItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.model.json.GroupShelf;
import placebooks.model.json.PlaceBookBinderDistanceEntry;
import placebooks.model.json.PlaceBookBinderSearchEntry;
import placebooks.model.json.PlaceBookItemDistanceEntry;
import placebooks.model.json.ServerInfo;
import placebooks.model.json.Shelf;
import placebooks.model.json.ShelfEntry;
import placebooks.model.json.UserShelf;
import placebooks.services.Service;
import placebooks.services.ServiceRegistry;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

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

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private static final int MEGABYTE = 1048576;

	public PlaceBooksAdminController()
	{
		jsonMapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);
		jsonMapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
		jsonMapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
		jsonMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
		jsonMapper.setVisibilityChecker(new VisibilityChecker.Std(JsonAutoDetect.Visibility.NONE,
				JsonAutoDetect.Visibility.NONE, JsonAutoDetect.Visibility.NONE, JsonAutoDetect.Visibility.NONE,
				JsonAutoDetect.Visibility.ANY));
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String accountPage()
	{
		return "account";
	}

	@RequestMapping(value = "/addgroup", method = RequestMethod.POST)
	public void addGroup(@RequestParam final String groupID, @RequestParam final String placebookID,
			final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = authUser(manager, res);
		if (user == null) { return; }

		try
		{
			final PlaceBookGroup group = manager.find(PlaceBookGroup.class, groupID);
			final PlaceBookBinder placebook = manager.find(PlaceBookBinder.class, placebookID);

			if (group == null || placebook == null) { return; }

			manager.getTransaction().begin();

			placebook.add(group);
			group.add(placebook);

			manager.getTransaction().commit();

			try
			{
				res.setContentType("application/json; charset=UTF-8");
				jsonMapper.writeValue(res.getWriter(), placebook);
				res.flushBuffer();
			}
			catch (final Exception e)
			{
				log.error(e.toString());
			}
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

	@RequestMapping(value = "/addLoginDetails", method = RequestMethod.POST)
	public void addLoginDetails(@RequestParam final String username, @RequestParam final String password,
			@RequestParam final String service, final HttpServletResponse res)
	{

		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = authUser(manager, res);
		if (user == null) { return; }

		Service serviceImpl = null;

		try
		{
			manager.getTransaction().begin();

			// Login details must be unique to user
			final TypedQuery<LoginDetails> q_ = manager
					.createQuery(	"SELECT l FROM LoginDetails l WHERE l.service= :service AND l.username= :username AND l.user.id != :userid",
									LoginDetails.class);
			q_.setParameter("service", service);
			q_.setParameter("username", username);
			q_.setParameter("userid", user.getKey());
			final Collection<LoginDetails> ll = q_.getResultList();
			log.debug("Found " + ll.size() + " LoginDetails");

			if (ll.size() > 0)
			{
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				log.error("LoginDetails already linked to user");
				return;
			}

			serviceImpl = ServiceRegistry.getService(service);
			if (service != null)
			{
				if (!serviceImpl.checkLogin(username, password))
				{
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			}

			final LoginDetails loginDetails = new LoginDetails(user, service, null, username, password);
			manager.persist(loginDetails);
			user.add(loginDetails);
			manager.getTransaction().commit();

			final TypedQuery<PlaceBookBinder> q = manager
					.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.owner= :owner", PlaceBookBinder.class);
			q.setParameter("owner", user);

			final Collection<PlaceBookBinder> pbs = q.getResultList();
			log.info("Converting " + pbs.size() + " PlaceBookBinders to JSON");
			log.info("User " + user.getName());
			try
			{
				res.setContentType("application/json; charset=UTF-8");
				jsonMapper.writeValue(res.getWriter(), new UserShelf(pbs, user));
				res.flushBuffer();
			}
			catch (final Exception e)
			{
				log.error(e.toString());
			}
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

		if (serviceImpl == null)
		{
			return;
		}
		else
		{

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final EntityManager manager = EMFSingleton.getEntityManager();
					final Service serviceImpl = ServiceRegistry.getService(service);
					try
					{
						if (serviceImpl != null)
						{
							serviceImpl.sync(manager, user, true, Double.parseDouble(PropertiesSingleton
									.get(CommunicationHelper.class.getClassLoader())
									.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")), Double
									.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
											.getProperty(PropertiesSingleton.IDEN_SEARCH_LAT, "0")), Double
									.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
											.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));
						}
					}
					catch (final Exception e)
					{
						log.warn(e.getMessage(), e);
					}
					finally
					{
						manager.close();
					}
				}
			}).start();
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
		res.setContentType("application/json; charset=UTF-8");
		final EntityManager entityManager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(entityManager);
			if (user == null)
			{
				try
				{
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					jsonMapper.writeValue(	res.getWriter(),
											req.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
					res.flushBuffer();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
			else
			{
				try
				{
					res.setContentType("application/json; charset=UTF-8");
					jsonMapper.writeValue(res.getWriter(), user);
					res.flushBuffer();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final EntityManager manager = EMFSingleton.getEntityManager();
						log.info("Updating Services");
						ServiceRegistry.updateServices(manager, user);
						manager.close();
					}
				}).start();
			}
		}
		finally
		{
			entityManager.close();
		}
	}

	@RequestMapping(value = "/admin/deletebinder/{key}", method = RequestMethod.GET)
	public void deletePlaceBookBinder(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{

		final EntityManager pm = EMFSingleton.getEntityManager();
		if (authUser(pm, res) == null) { return; }

		try
		{
			pm.getTransaction().begin();
			final PlaceBookBinder p = pm.find(PlaceBookBinder.class, key);
			for (final PlaceBook placebook : p.getPlaceBooks())
			{
				for (final PlaceBookItem item : placebook.getItems())
				{
					item.deleteItemData();
				}
			}
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

			pm.close();
		}

		log.info("Deleted PlaceBook");
	}

	@RequestMapping(value = "/palette", method = RequestMethod.GET)
	public void getPaletteItemsJSON(final HttpServletResponse res)
	{

		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = authUser(manager, res);
		if (user == null) { return; }

		final TypedQuery<PlaceBookItem> q = manager
				.createQuery(	"SELECT p FROM PlaceBookItem p WHERE p.owner = :owner AND p.placebook IS NULL",
								PlaceBookItem.class);
		q.setParameter("owner", user);

		final Collection<PlaceBookItem> pbs = q.getResultList();

		// Add preset items to the Palette
		final ArrayList<PlaceBookItem> presetItems = PresetItemsHelper.getPresetItems(user);
		pbs.addAll(presetItems);

		// log.info("Converting " + pbs.size() + " PlaceBookItems to JSON");
		// log.info("User " + user.getName());

		res.setContentType("application/json; charset=UTF-8");
		try
		{
			final Writer sos = res.getWriter();
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
				sos.write(jsonMapper.writeValueAsString(item));
			}
			sos.write("]");

			res.flushBuffer();
		}
		catch (final Exception e)
		{
			log.error(e.getMessage(), e);
		}

		manager.close();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final EntityManager manager = EMFSingleton.getEntityManager();
				// log.info("Updating Services");
				ServiceRegistry.updateServices(manager, user);
				manager.close();
			}
		}).start();
	}

	@RequestMapping(value = "/placebookbinder/{key}", method = RequestMethod.GET)
	public void getPlaceBookBinderJSON(final HttpServletResponse res, @PathVariable("key") final String key)
	{
		final EntityManager entityManager = EMFSingleton.getEntityManager();

		try
		{
			final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, key);
			if (placebook != null)
			{
				final User user = UserManager.getCurrentUser(entityManager);
				if (!placebook.canBeRead(user))
				{
					try
					{
						log.info("User doesn't have sufficient permissions");
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						res.setContentType("application/json; charset=UTF-8");
						res.getWriter().write("User doesn't have sufficient permissions");
						return;
					}
					catch (final Exception e)
					{
						log.error(e.getMessage(), e);
						return;
					}
				}

				try
				{
					res.setContentType("application/json; charset=UTF-8");
					jsonMapper.writeValue(res.getWriter(), placebook);
					res.flushBuffer();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}
			else
			{
				res.setStatus(404);
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			entityManager.close();
		}
	}

	@RequestMapping(value = "/shelf", method = RequestMethod.GET)
	public void getPlaceBookBindersJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		log.info("Shelf");
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(manager);
			if (user != null)
			{
				final TypedQuery<PlaceBookBinder> q = manager
						.createQuery(	"SELECT p FROM PlaceBookBinder p WHERE p.owner = :user OR p.permsUsers LIKE :email",
										PlaceBookBinder.class);
				q.setParameter("user", user);
				q.setParameter("email", "%" + user.getEmail() + "%");

				final Collection<PlaceBookBinder> pbs = q.getResultList();
				log.info("Converting " + pbs.size() + " PlaceBookBinders to JSON");
				log.info("User " + user.getName());
				try
				{
					res.setContentType("application/json; charset=UTF-8");
					jsonMapper.writeValue(res.getWriter(), new UserShelf(pbs, user));
					res.flushBuffer();
				}
				catch (final Exception e)
				{
					log.error(e.toString());
				}

				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						final EntityManager manager = EMFSingleton.getEntityManager();
						ServiceRegistry.updateServices(manager, user);
					}
				}).start();
			}
			else
			{
				try
				{
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					jsonMapper
							.writeValue(res.getWriter(),
										((Exception) req.getSession()
												.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage());
					res.flushBuffer();
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
	public void getPlaceBookBindersJSON(final HttpServletResponse res, @PathVariable("owner") final String owner)
	{
		if (owner.trim().isEmpty()) { return; }

		final EntityManager pm = EMFSingleton.getEntityManager();
		if (authUser(pm) == null) { return; }

		final TypedQuery<User> uq = pm.createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class);
		uq.setParameter("email", owner.trim());
		try
		{
			final User user = uq.getSingleResult();

			final TypedQuery<PlaceBookBinder> q = pm.createQuery("SELECT p FROM PlaceBookBinder p "
					+ "WHERE p.owner = :user OR p.permsUsers LIKE :email", PlaceBookBinder.class);

			q.setParameter("user", user);
			q.setParameter("email", "%" + owner.trim() + "%");
			final List<PlaceBookBinder> pbs = q.getResultList();

			log.info("Converting " + pbs.size() + " PlaceBookBinders to JSON");
			if (!pbs.isEmpty())
			{
				try
				{
					res.setContentType("application/json; charset=UTF-8");
					jsonMapper.writeValue(res.getWriter(), new UserShelf(pbs, user));
					res.flushBuffer();
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
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
	public void getPlaceBookGroupJSON(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("id") final String id)
	{
		log.info("Group");
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final PlaceBookGroup group = manager.find(PlaceBookGroup.class, id);

			try
			{
				res.setContentType("application/json; charset=UTF-8");
				jsonMapper.writeValue(res.getWriter(), new GroupShelf(group));
				res.flushBuffer();
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

	@RequestMapping(value = "/groups", method = RequestMethod.GET)
	public void getPlaceBookGroupsJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		log.info("Group");
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(manager);
			if (user != null)
			{
				final TypedQuery<PlaceBookGroup> q = manager
						.createQuery("SELECT g FROM PlaceBookGroup g WHERE g.owner = :user", PlaceBookGroup.class);
				q.setParameter("user", user);

				final Collection<PlaceBookGroup> pbs = q.getResultList();
				log.info("Converting " + pbs.size() + " PlaceBookBinders to JSON");
				log.info("User " + user.getName());
				try
				{
					res.setContentType("application/json; charset=UTF-8");
					// TODO jsonMapper.writeValue(res.getWriter(), new UserShelf(pbs, user));
					res.flushBuffer();
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
					jsonMapper
							.writeValue(res.getWriter(),
										((Exception) req.getSession()
												.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage());
					res.flushBuffer();
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

	@RequestMapping(value = "/randomized/{count}", method = RequestMethod.GET)
	public void getRandomPlaceBookBindersJSON(final HttpServletResponse res, @PathVariable("count") final int count)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final TypedQuery<PlaceBookBinder> q = manager
					.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.state= :state", PlaceBookBinder.class);
			q.setParameter("state", State.PUBLISHED);

			final List<PlaceBookBinder> pbs = q.getResultList();
			final Collection<ShelfEntry> result = new ArrayList<ShelfEntry>();
			if (!pbs.isEmpty())
			{
				final Random random = new Random();
				for (int index = 0; index < count && !pbs.isEmpty(); index++)
				{
					final int rindex = random.nextInt(pbs.size());
					final PlaceBookBinderSearchEntry entry = new PlaceBookBinderSearchEntry(pbs.get(rindex), 0);
					result.add(entry);
					pbs.remove(rindex);
				}
			}
			log.info("Converting " + result.size() + " PlaceBooks to JSON");
			try
			{
				res.setContentType("application/json; charset=UTF-8");
				jsonMapper.writeValue(res.getWriter(), new Shelf(result));
				res.flushBuffer();
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

	@RequestMapping(value = "/admin/serverinfo", method = RequestMethod.GET)
	public void getServerInfoJSON(final HttpServletResponse res)
	{
		final ServerInfo si = new ServerInfo();
		try
		{
			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), si);
			res.flushBuffer();
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
	public void makePackage(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager entityManager = EMFSingleton.getEntityManager();
		final User currentUser = UserManager.getCurrentUser(entityManager);
		final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, key);
		if (!placebook.canBeRead(currentUser))
		{
			try
			{
				log.info("User doesn't have sufficient permissions");
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				res.setContentType("application/json; charset=UTF-8");
				res.getWriter().write("User doesn't have sufficient permissions");
				return;
			}
			catch (final Exception e)
			{
				log.error(e.getMessage(), e);
				return;
			}
		}

		final File zipFile = PlaceBooksAdminHelper.makePackage(entityManager, placebook);
		if (zipFile == null)
		{
			log.info("Failed to create zip file");
			return;
		}

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
			res.setContentType("application/x-placebook");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + placebook.getKey() + ".zip\"");
			res.addHeader("Content-Length", Integer.toString(bos.size()));
			sos.write(bos.toByteArray());
			sos.flush();

		}
		catch (final IOException e)
		{
			log.error(e.toString(), e);
			return;
		}
		finally
		{
			entityManager.close();
		}
	}

	@RequestMapping("/oauth")
	public String oauth(final HttpServletRequest request, @RequestParam final String service)
	{
		log.info(service);
		final Service serv = ServiceRegistry.getService(service);
		if (serv != null)
		{
			log.info(serv.getInfo().getName());
			final EntityManager manager = EMFSingleton.getEntityManager();
			final User user = UserManager.getCurrentUser(manager);

			final String result = serv.getAuthenticationURL(manager, user, null);
			log.info(result);

			if (result != null) { return "redirect:" + result; }
		}

		final String referrer = request.getHeader("referer");
		log.info(referrer);
		if (referrer != null) { return "redirect:" + referrer; }
		return "redirect:/index.html";
	}

	@RequestMapping(value = "/publishplacebookbinder", method = RequestMethod.POST)
	public void publishPlaceBookBinderJSON(final HttpServletResponse res,
			@RequestParam("placebookbinder") final String json)
	{
		log.info("Publish PlacebookBinder: " + json);
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = authUser(manager, res);
		if (user == null) { return; }

		try
		{
			final PlaceBookBinder placebookBinder = jsonMapper.readValue(json, PlaceBookBinder.class);

			final PlaceBookBinder dbBinder = manager.find(PlaceBookBinder.class, placebookBinder.getKey());

			if (dbBinder.getOwner() != user)
			{
				log.debug("This user is not the owner");
				final PlaceBookBinder.Permission perms = placebookBinder.getPermission(user);
				if (perms == null)
				{
					try
					{
						log.info("User doesn't have sufficient permissions");
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						res.setContentType("application/json; charset=UTF-8");
						res.getWriter().write("User doesn't have sufficient permissions");
						return;
					}
					catch (final Exception e)
					{
						log.error(e.getMessage(), e);
						return;
					}
				}
			}

			final PlaceBookBinder result = PlaceBooksAdminHelper.savePlaceBookBinder(manager, placebookBinder);
			log.debug("Saved PlacebookBinder:" + jsonMapper.writeValueAsString(result));
			log.info("Published PlacebookBinder:" + jsonMapper.writeValueAsString(result));

			final PlaceBookBinder published = PlaceBooksAdminHelper.publishPlaceBookBinder(manager, result);

			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), published);
			res.flushBuffer();
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

	@RequestMapping(value = "/qrcode/{key}", method = RequestMethod.GET)
	public void qrcode(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{

		try
		{
			log.debug("QR Code: " + key);

			final String eTag = key;

			final String ifNoneMatch = req.getHeader("If-None-Match");
			if (ifNoneMatch != null && MediaHelper.matches(ifNoneMatch, eTag))
			{
				res.setHeader("ETag", eTag);
				res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}

			final long ifModifiedSince = req.getDateHeader("If-Modified-Since");
			if (ifNoneMatch == null && ifModifiedSince != -1)
			{
				res.setHeader("ETag", eTag);
				res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}

			final String ifMatch = req.getHeader("If-Match");
			if (ifMatch != null && !MediaHelper.matches(ifMatch, eTag))
			{
				res.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}

			final QRCodeWriter writer = new QRCodeWriter();
			try
			{
				final String url = "http://www.placebooks.org/index.html#placebook:" + key;
				final BitMatrix matrix = writer.encode(url, com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);
				final BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

				res.reset();

				res.setBufferSize(MediaHelper.DEFAULT_BUFFER_SIZE);
				res.setContentType("image/png");
				res.setHeader("ETag", eTag);
				res.setDateHeader("Last-Modified", System.currentTimeMillis());
				res.setDateHeader("Expires", System.currentTimeMillis() + MediaHelper.DEFAULT_EXPIRE_TIME);

				final OutputStream out = res.getOutputStream();
				ImageIO.write(image, "png", out);
				out.close();
			}
			catch (final Exception e)
			{
				// exit the method
				return;
			}

		}
		catch (final Exception ex)
		{
			log.error("Exception in serving file: " + ex.toString());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/saveplacebookbinder", method = RequestMethod.POST)
	public void savePlaceBookBinderJSON(final HttpServletResponse res,
			@RequestParam("placebookbinder") final String json)
	{
		log.info("Save PlacebookBinder: " + json);
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User currentUser = authUser(manager, res);
		if (currentUser == null) { return; }

		try
		{
			final PlaceBookBinder placebookBinder = jsonMapper.readValue(json, PlaceBookBinder.class);

			if (placebookBinder.getKey() != null)
			{
				final PlaceBookBinder dbBinder = manager.find(PlaceBookBinder.class, placebookBinder.getKey());
				if (!dbBinder.canBeWriten(currentUser))
				{
					try
					{
						log.info("User doesn't have sufficient permissions");
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						res.setContentType("application/json; charset=UTF-8");
						res.getWriter().write("User doesn't have sufficient permissions");
						return;
					}
					catch (final Exception e)
					{
						log.error(e.getMessage(), e);
						return;
					}
				}
			}

			final PlaceBookBinder result = PlaceBooksAdminHelper.savePlaceBookBinder(manager, placebookBinder);

			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), result);
			res.flushBuffer();
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

	@RequestMapping(value = "/savegroup", method = RequestMethod.POST)
	public void savePlaceBookGroupJSON(final HttpServletResponse res, @RequestParam("group") final String json)
	{
		log.info("Save PlacebookGroup: " + json);
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User currentUser = authUser(manager, res);
		if (currentUser == null) { return; }

		try
		{
			final GroupShelf shelf = jsonMapper.readValue(json, GroupShelf.class);
			PlaceBookGroup group;

			if (shelf.getGroup() != null && shelf.getGroup().getId() != null)
			{
				group = manager.find(PlaceBookGroup.class, shelf.getGroup().getId());

				if (group.getOwner() == null)
				{
					group.setOwner(currentUser);
				}
				else if (group.getOwner() != currentUser)
				{
					try
					{
						log.info("User doesn't have sufficient permissions");
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						res.setContentType("application/json; charset=UTF-8");
						res.getWriter().write("User doesn't have sufficient permissions");
						return;
					}
					catch (final Exception e)
					{
						log.error(e.getMessage(), e);
						return;
					}
				}
			}
			else
			{
				group = new PlaceBookGroup();
			}

			final Collection<PlaceBookBinder> placebooks = new HashSet<PlaceBookBinder>();
			for (final ShelfEntry entry : shelf.getEntries())
			{
				final PlaceBookBinder binder = manager.find(PlaceBookBinder.class, entry.getKey());
				if (binder != null)
				{
					placebooks.add(binder);
					binder.add(group);
				}
			}

			for (final PlaceBookBinder binder : group.getPlaceBooks())
			{
				if (!placebooks.contains(binder))
				{
					binder.remove(group);
				}
			}

			group.update(shelf.getGroup());
			group.setPlaceBooks(placebooks);

			manager.getTransaction().begin();

			group = manager.merge(group);

			currentUser.add(group);

			manager.getTransaction().commit();

			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), new GroupShelf(group));
			res.flushBuffer();
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

	@RequestMapping(value = "/admin/search/{terms}", method = RequestMethod.GET)
	public void searchGET(final HttpServletResponse res, @PathVariable("terms") final String terms)
	{
		final long timeStart = System.nanoTime();
		final long timeEnd;

		final EntityManager em = EMFSingleton.getEntityManager();
		final Collection<ShelfEntry> pbs = new ArrayList<ShelfEntry>();
		for (final Map.Entry<PlaceBookBinder, Integer> entry : PlaceBooksAdminHelper.search(em, terms))
		{
			final PlaceBookBinder p = entry.getKey();
			if (p != null && p.getState() == PlaceBookBinder.State.PUBLISHED)
			{
				log.info("Search result: pb key=" + entry.getKey().getKey() + ", score=" + entry.getValue());
				pbs.add(new PlaceBookBinderSearchEntry(p, entry.getValue()));
			}
		}
		em.close();

		try
		{
			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), new Shelf(pbs));
			res.flushBuffer();
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}

		timeEnd = System.nanoTime();
		log.info("Search execution time = " + (timeEnd - timeStart) + " ns");
	}

	@RequestMapping(value = "/admin/location_search/placebookitem/{geometry}", method = RequestMethod.GET)
	public void searchLocationPlaceBookItemsGET(final HttpServletResponse res,
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
			return;
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

		try
		{
			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), new Shelf(ps));
			res.flushBuffer();
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
	}

	@RequestMapping(value = "/admin/location_search/placebookbinder/{geometry}", method = RequestMethod.GET)
	public void searchLocationPlaceBooksGET(final HttpServletResponse res,
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
			return;
		}

		final EntityManager em = EMFSingleton.getEntityManager();
		final Collection<ShelfEntry> pbs = new ArrayList<ShelfEntry>();
		for (final Map.Entry<PlaceBookBinder, Double> entry : PlaceBooksAdminHelper
				.searchLocationForPlaceBookBinders(em, geometry_))
		{
			final PlaceBookBinder p = entry.getKey();
			if (p != null)
			{
				log.info("Search result: pbb key=" + entry.getKey().getKey() + ", distance=" + entry.getValue());
				pbs.add(new PlaceBookBinderDistanceEntry(p, entry.getValue()));
			}
		}
		em.close();

		try
		{
			res.setContentType("application/json; charset=UTF-8");
			jsonMapper.writeValue(res.getWriter(), new Shelf(pbs));
			res.flushBuffer();
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
	}

	@RequestMapping(value = "/admin/search", method = RequestMethod.POST)
	public void searchPOST(final HttpServletRequest req, final HttpServletResponse res)
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

		searchGET(res, out.toString());
	}

	@RequestMapping(value = "/admin/serve/media/gpstraceitem/{hash}", method = RequestMethod.GET)
	public void serveGPSTraceItem(final HttpServletResponse res, @PathVariable("hash") final String hash)
	{
		// Serve a GPS trace based on hash... get the first item in the DB that has this hash since
		// they'll all be the same
		// TODO check permissions
		final EntityManager em = EMFSingleton.getEntityManager();
		log.info("Serving GPS Trace " + hash);

		try
		{

			final TypedQuery<GPSTraceItem> q = em.createQuery(	"SELECT g FROM GPSTraceItem g WHERE g.hash= :hash",
																GPSTraceItem.class);
			q.setParameter("hash", hash);

			final List<GPSTraceItem> items = q.getResultList();

			if (items.size() == 0)
			{
				log.debug("Can't find GPS trace: " + hash);
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else
			{
				final GPSTraceItem g = items.get(0);
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
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}
	}

	@RequestMapping(value = "/admin/serve/item/media/{type}/{key}", method = RequestMethod.GET)
	public void serveItemMedia(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("type") final String type, @PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		log.info("Serving Item: " + type + ":" + key);

		try
		{
			final ImageItem i = em.find(ImageItem.class, key);
			boolean found = false;
			if (i != null)
			{
				if (i.getHash() != null)
				{
					found = true;
				}
			}
			if (found)
			{
				serveMedia(req, res, type, i.getHash());
			}
			else
			{
				log.info("Item " + type + ":" + key + " not found in db");
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			try
			{
				e.printStackTrace(res.getWriter());
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
		finally
		{
			em.close();
		}
	}

	@RequestMapping(value = "/admin/serve/media/{type}/{hash}", method = RequestMethod.GET)
	public void serveMedia(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("type") final String type, @PathVariable("hash") final String hash)
	{
		String itemPath = "";
		if (type.equalsIgnoreCase("imageitem"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash;
		}
		else
		{
			if (type.equalsIgnoreCase("thumb"))
			{
				itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_THUMBS, "") + File.separator + hash;
			}
			else
			{
				if (type.equalsIgnoreCase("audioitem"))
				{
					itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash;
				}
				else
				{
					if (type.equalsIgnoreCase("videoitem"))
					{
						itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_MEDIA, "")
								+ File.separator
								+ hash
								+ "-chrome.ogg";

					}
					else
					{
						if (type.equalsIgnoreCase("videoitemmobile"))
						{
							itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
									.getProperty(PropertiesSingleton.IDEN_MEDIA, "")
									+ File.separator
									+ hash
									+ "-mobile.ogg";
						}
						else
						{
							res.setStatus(HttpServletResponse.SC_NOT_FOUND);
						}
					}
				}
			}
		}

		// ?Is there a path to serve the file...
		if (!itemPath.equalsIgnoreCase(""))
		{
			log.debug("Looking to serve file:" + itemPath);
			File serveFile = new File(itemPath);
			if (!serveFile.exists())
			{
				// Attempt to find other versions of the file... in case extension guess was
				// wrong...
				final String dirPath = itemPath.replace(serveFile.getName(), "");
				log.warn("Can't find file, trying alternatives in " + dirPath);
				itemPath = FileHelper.FindClosestFile(dirPath, hash);
				if (itemPath != null)
				{
					log.warn("Using alternative file: " + itemPath);
					serveFile = new File(itemPath);
				}
			}

			if (serveFile.exists())
			{
				try
				{
					log.debug("Serving file: " + serveFile.getPath());

					final long length = serveFile.length();
					final long lastModified = serveFile.lastModified();
					final String eTag = hash;

					final String ifNoneMatch = req.getHeader("If-None-Match");
					if (ifNoneMatch != null && MediaHelper.matches(ifNoneMatch, eTag))
					{
						res.setHeader("ETag", eTag);
						res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
						return;
					}

					final long ifModifiedSince = req.getDateHeader("If-Modified-Since");
					if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified)
					{
						res.setHeader("ETag", eTag);
						res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
						return;
					}

					final String ifMatch = req.getHeader("If-Match");
					if (ifMatch != null && !MediaHelper.matches(ifMatch, eTag))
					{
						res.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
						return;
					}

					final long ifUnmodifiedSince = req.getDateHeader("If-Unmodified-Since");
					if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified)
					{
						res.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
						return;
					}

					final MediaHelper.Range full = new MediaHelper.Range(0, length - 1, length);
					final List<MediaHelper.Range> ranges = new ArrayList<MediaHelper.Range>();
					final String range = req.getHeader("Range");
					if (range != null)
					{

						if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$"))
						{
							res.setHeader("Content-Range", "bytes */" + length);
							res.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							return;
						}

						final String ifRange = req.getHeader("If-Range");
						if (ifRange != null && !ifRange.equals(eTag))
						{
							try
							{
								final long ifRangeTime = req.getDateHeader("If-Range");
								if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified)
								{
									ranges.add(full);
								}
							}
							catch (final IllegalArgumentException ignore)
							{
								ranges.add(full);
							}
						}

						if (ranges.isEmpty())
						{
							for (final String part : range.substring(6).split(","))
							{
								long start = MediaHelper.sublong(part, 0, part.indexOf("-"));
								long end = MediaHelper.sublong(part, part.indexOf("-") + 1, part.length());

								if (start == -1)
								{
									start = length - end;
									end = length - 1;
								}
								else if (end == -1 || end > length - 1)
								{
									end = length - 1;
								}

								if (start > end)
								{
									res.setHeader("Content-Range", "bytes */" + length);
									res.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
									return;
								}

								ranges.add(new MediaHelper.Range(start, end, length));
							}
						}
					}

					boolean acceptsGzip = false;
					String disposition = "inline";
					String contentType = null;

					log.debug("Attempting MimeType match...");
					final MimeUtil2 mimeMagic = new MimeUtil2();
					mimeMagic.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");

					final Object[] ms = mimeMagic.getMimeTypes(serveFile, new MimeType("application/octet-stream"))
							.toArray();

					if (ms.length == 1)
					{
						contentType = ms[0].toString();
						log.info("MimeType matched as " + contentType);
					}

					else if (ms.length > 1)
					{
						log.error("MimeUtil2 gave too many matches: ");
						for (final Object element : ms)
						{
							log.error(" " + element.toString());
						}
					}

					mimeMagic.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");

					if (contentType == null)
					{
						contentType = "application/octet-stream";
					}

					if (contentType.startsWith("text"))
					{
						final String acceptEncoding = req.getHeader("Accept-Encoding");
						acceptsGzip = acceptEncoding != null && MediaHelper.accepts(acceptEncoding, "gzip");
						contentType += ";charset=UTF-8";
					}
					else if (!contentType.startsWith("markerImage"))
					{
						final String accept = req.getHeader("Accept");
						disposition = accept != null && MediaHelper.accepts(accept, contentType) ? "inline"
								: "attachment";
					}

					res.reset();

					res.setBufferSize(MediaHelper.DEFAULT_BUFFER_SIZE);
					res.setHeader("Content-Disposition", disposition + ";filename=\"" + itemPath + "\"");
					res.setHeader("Accept-Ranges", "bytes");
					res.setHeader("ETag", eTag);
					res.setDateHeader("Last-Modified", lastModified);
					res.setDateHeader("Expires", System.currentTimeMillis() + MediaHelper.DEFAULT_EXPIRE_TIME);

					RandomAccessFile input = null;
					OutputStream output = null;

					try
					{
						input = new RandomAccessFile(serveFile, "r");
						output = res.getOutputStream();

						if (ranges.isEmpty() || ranges.get(0) == full)
						{

							final MediaHelper.Range r = full;
							res.setContentType(contentType);
							res.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

							if (acceptsGzip)
							{
								res.setHeader("Content-Encoding", "gzip");
								output = new GZIPOutputStream(output, MediaHelper.DEFAULT_BUFFER_SIZE);
							}
							else
							{
								res.setHeader("Content-Length", String.valueOf(r.length));
							}
							MediaHelper.copy(input, output, r.start, r.length);
						}
						else if (ranges.size() == 1)
						{

							final MediaHelper.Range r = ranges.get(0);
							res.setContentType(contentType);
							res.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
							res.setHeader("Content-Length", String.valueOf(r.length));
							res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

							MediaHelper.copy(input, output, r.start, r.length);

						}
						else
						{
							res.setContentType("multipart/byteranges; boundary=" + MediaHelper.MULTIPART_BOUNDARY);
							res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

							final ServletOutputStream sos = (ServletOutputStream) output;

							for (final MediaHelper.Range r : ranges)
							{
								sos.println();
								sos.println("--" + MediaHelper.MULTIPART_BOUNDARY);
								sos.println("Content-Type: " + contentType);
								sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

								MediaHelper.copy(input, output, r.start, r.length);
							}

							sos.println();
							sos.println("--" + MediaHelper.MULTIPART_BOUNDARY + "--");
						}
					}
					finally
					{
						MediaHelper.close(output);
						MediaHelper.close(input);
					}
				}
				catch (final Exception ex)
				{
					log.error("Exception in serving file: " + ex.toString());
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
		else
		{
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@RequestMapping(value = "/sync/{serviceName}", method = RequestMethod.GET)
	public void syncService(final HttpServletResponse res, @PathVariable("serviceName") final String serviceName)
	{
		log.info("Sync " + serviceName);
		final Service service = ServiceRegistry.getService(serviceName);
		if (service != null)
		{
			final EntityManager manager = EMFSingleton.getEntityManager();
			final User user = authUser(manager, res);
			if (user == null) { return; }

			service.sync(manager, user, true, Double.parseDouble(PropertiesSingleton.get(	CommunicationHelper.class
																									.getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_SEARCH_LON, "0")), Double.parseDouble(PropertiesSingleton
					.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.IDEN_SEARCH_LAT,
																					"0")), Double
					.parseDouble(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_SEARCH_RADIUS, "0")));
		}

		res.setStatus(200);
	}

	@RequestMapping(value = "/admin/add_item/upload", method = RequestMethod.POST)
	public void uploadFile(final HttpServletRequest req, final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User currentUser = authUser(manager, res);
		if (currentUser == null) { return; }

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
					if (type.equals("markerImage"))
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
				itemData.setOwner(currentUser);
			}

			PlaceBookBinder.Permission perms = PlaceBookBinder.Permission.R_W;

			Log.info("Upload: " + itemKey);
			PlaceBookItem item = null;
			if (itemKey != null)
			{
				item = manager.find(PlaceBookItem.class, itemKey);
				if (item.getPlaceBook() != null)
				{
					final PlaceBookBinder dbBinder = item.getPlaceBook().getPlaceBookBinder();
					if (dbBinder.getOwner() != currentUser)
					{
						perms = dbBinder.getPermission(currentUser);
					}
				}
			}
			else if (placebookKey != null)
			{
				final PlaceBook placebook = manager.find(PlaceBook.class, placebookKey);

				if (type.equals("gpstrace"))
				{
					item = new GPSTraceItem(itemData.getOwner(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
				else if (type.equals("markerImage"))
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

				final PlaceBookBinder dbBinder = placebook.getPlaceBookBinder();
				if (dbBinder.getOwner() != currentUser)
				{
					perms = dbBinder.getPermission(currentUser);
				}
			}

			if (perms == null || (perms != null && perms == PlaceBookBinder.Permission.R)) { throw new Exception(
					"No permission to upload"); }

			if (item instanceof MediaItem)
			{
				((MediaItem) item).setSourceURL(null);
				((MediaItem) item).writeDataToDisk(name, fileData.getInputStream());
				log.info(jsonMapper.writeValueAsString(item));
			}
			else if (item instanceof GPSTraceItem)
			{
				((GPSTraceItem) item).setSourceURL(null);
				((GPSTraceItem) item).readTrace(fileData.getInputStream());
			}

			manager.getTransaction().commit();

			jsonMapper.writeValue(res.getWriter(), item);
			res.setContentType("text/html");
			res.getWriter().flush();
		}
		catch (final Exception e)
		{
			log.error(e.toString(), e);
			try
			{
				res.setContentType("application/json; charset=UTF-8");
				res.setStatus(500);
				res.getWriter().write(e.getMessage());
				res.getWriter().flush();
			}
			catch (final Exception e2)
			{
				log.error(e2.toString(), e2);
			}
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

	@RequestMapping(value = "/view/{key}", method = RequestMethod.GET)
	public void viewPlaceBook(final HttpServletRequest req, final HttpServletResponse res,
			@PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			String urlbase;
			if (req.getServerPort() != 80)
			{
				urlbase = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
						+ req.getContextPath() + "/";
			}
			else
			{
				urlbase = req.getScheme() + "://" + req.getServerName() + req.getContextPath() + "/";
			}

			final PlaceBookBinder placebook = manager.find(PlaceBookBinder.class, key);
			final User user = UserManager.getCurrentUser(manager);
			if (hasPermission(user, placebook))
			{
				try
				{
					final PrintWriter writer = res.getWriter();
					writer.write("<!doctype html>");
					writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
					writer.write(" xmlns:og=\"http://ogp.me/ns#\"");
					writer.write(" xmlns:fb=\"http://www.facebook.com/2008/fbml\">");
					writer.write("<head>");
					writer.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
					writer.write("<title>" + placebook.getMetadataValue("title") + "</title>");
					writer.write("<meta property=\"og:title\" content=\"" + placebook.getMetadataValue("title")
							+ "\"/>");
					writer.write("<meta property=\"og:type\" content=\"article\"/>");
					if (placebook.getMetadataValue("placebookImage") != null)
					{
						writer.write("<meta property=\"og:markerImage\" content=\"" + urlbase
								+ "placebooks/a/admin/serve/media/imageitem/"
								+ placebook.getMetadataValue("placebookImage") + "\"/>");
					}
					writer.write("<meta property=\"og:site_name\" content=\"PlaceBooks\"/>");
					writer.write("<meta property=\"og:description\" content=\""
							+ placebook.getMetadataValue("description") + "\"/>");
					writer.write("<meta http-equiv=\"Refresh\" content=\"0; url=" + urlbase + "#preview:"
							+ placebook.getKey() + "\" />");
					writer.write("<link rel=\"icon\" type=\"image/png\" href=\"../../../images/Logo_016.png\" />");
					writer.write("</head>");
					writer.write("<body></body>");
					writer.write("</html>");
					writer.flush();
					writer.close();
				}
				catch (final IOException e)
				{
					log.error(e.toString(), e);
				}

			}
			else
			{
				try
				{
					log.info("User doesn't have sufficient permissions");
					final PrintWriter writer = res.getWriter();
					writer.write("<!doctype html>");
					writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
					writer.write(" xmlns:og=\"http://ogp.me/ns#\"");
					writer.write(" xmlns:fb=\"http://www.facebook.com/2008/fbml\">");
					writer.write("<head>");
					writer.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
					writer.write("<title>PlaceBooks</title>");
					writer.write("<meta property=\"og:type\" content=\"article\"/>");
					writer.write("<meta property=\"og:site_name\" content=\"PlaceBooks\"/>");
					writer.write("<meta http-equiv=\"Refresh\" content=\"0; url=" + urlbase + "\"/>");
					writer.write("<link rel=\"icon\" type=\"image/png\" href=\"../../../images/Logo_016.png\" />");
					writer.write("</head>");
					writer.write("<body></body>");
					writer.write("</html>");
					writer.flush();
					writer.close();
					return;
				}
				catch (final Exception e)
				{
					log.error(e.toString(), e);
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

	private final User authUser(final EntityManager em)
	{
		return authUser(em, null);
	}

	private final User authUser(final EntityManager em, final HttpServletResponse res)
	{
		final User user = UserManager.getCurrentUser(em);
		if (user == null)
		{
			try
			{
				log.info("User not logged in");
				if (res != null)
				{
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					res.setContentType("application/json; charset=UTF-8");
					res.getWriter().write("User not logged in");
				}
				return null;
			}
			catch (final Exception e)
			{
				log.error(e.getMessage(), e);
				return null;
			}
		}
		return user;
	}

	private boolean hasPermission(final User user, final PlaceBookBinder placebook)
	{
		if (placebook == null) { return false; }

		if (placebook.getState() == PlaceBookBinder.State.PUBLISHED) { return true; }

		if (user == null) { return false; }

		if (placebook.getOwner() == user) { return true; }

		final PlaceBookBinder.Permission perms = placebook.getPermission(user);
		if (perms == null) { return false; }

		return true;
	}

}
