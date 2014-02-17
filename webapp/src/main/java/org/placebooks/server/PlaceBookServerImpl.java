package org.placebooks.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.fileupload.FileItem;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Group;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.ServerInfo;
import org.placebooks.client.model.ServiceInfo;
import org.placebooks.client.model.Shelf;
import org.placebooks.controller.EMFSingleton;
import org.placebooks.controller.MediaHelper;
import org.placebooks.controller.PlaceBooksAdminHelper;
import org.placebooks.controller.PresetItemsHelper;
import org.placebooks.controller.PropertiesSingleton;
import org.placebooks.model.GPSTraceItem;
import org.placebooks.model.ImageItem;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.MediaItem;
import org.placebooks.model.PlaceBook;
import org.placebooks.model.PlaceBookBinder;
import org.placebooks.model.PlaceBookBinder.State;
import org.placebooks.model.PlaceBookGroup;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.Session;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.placebooks.model.json.JsonDownloadIgnore;
import org.placebooks.model.json.JsonIgnore;
import org.placebooks.services.Service;
import org.placebooks.services.ServiceRegistry;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.server.Exclude;
import org.wornchaos.server.HTTPException;
import org.wornchaos.server.Transact;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class PlaceBookServerImpl extends EMFJSONServer implements PlaceBookServerExt
{
	private static final String sessionName = "placebook_session";

	private final static LinkedList<String> recentBooks = new LinkedList<String>();

	private static MessageDigest passwordDigest;

	private static Entry createEntry(final PlaceBookBinder binder)
	{
		final Entry entry = new Entry();
		entry.setKey(binder.getKey());
		entry.setTitle(binder.getMetadataValue("title"));
		entry.setOwner(binder.getOwner().getKey());
		entry.setDescription(binder.getMetadataValue("description"));
		entry.setPreviewImage(binder.getMetadataValue("placebookImage"));
		entry.setState(binder.getState().toString());
		entry.setOwnerName(binder.getOwner().getName());
		// numPlaceBooks = placebookBinder.getPlaceBooks().size();
		// entry.setTimestamp(binder.getTimestamp());
		// permissions = placebookBinder.getPermissionsAsString();
		// entry.setAactivity = placebookBinder.getMetadataValue("activity");

		if (binder.getGeometry() != null)
		{
			entry.setCenter(binder.getGeometry().getEnvelope().getCentroid().toString());
		}
		return entry;
	}

	private static Group createGroup(final PlaceBookGroup group)
	{
		final Group result = new Group();
		result.setId(group.getId());
		result.setDescription(group.getDescription());
		result.setTitle(group.getTitle());
		result.setImage(createItem(group.getImage()));
		return result;
	}

	private static Item createItem(final PlaceBookItem item)
	{
		final Item result = new Item();
		if (item.getGeometry() != null)
		{
			result.setGeom(item.getGeometry().toString());
		}
		if (item instanceof MediaItem)
		{
			result.setHash(((MediaItem) item).getHash());
		}
		result.setId(item.getKey());
		if (item.getTimestamp() != null)
		{
			result.setTimestamp(item.getTimestamp().getTime());
		}
		if (item instanceof TextItem)
		{
			result.setText(((TextItem) item).getText());
		}
		result.getMetadata().putAll(item.getMetadata());
		result.getParameters().putAll(item.getParameters());
		result.setType(Enum.valueOf(Item.Type.class, item.getClass().getSimpleName()));
		return result;
	}

	private static Shelf createShelf(final Iterable<PlaceBookBinder> placebooks, final Object object)
	{
		final Shelf shelf = new Shelf();
		for (final PlaceBookBinder binder : placebooks)
		{
			shelf.getEntries().add(createEntry(binder));
		}

		if (object instanceof PlaceBookGroup)
		{
			shelf.setGroup(createGroup((PlaceBookGroup) object));
		}
		else if (object instanceof User)
		{
			shelf.setUser(createUser((User) object));
		}

		return shelf;
	}

	private static org.placebooks.client.model.User createUser(final User user)
	{
		final org.placebooks.client.model.User result = new org.placebooks.client.model.User();
		result.setId(user.getKey());
		result.setEmail(user.getEmail());
		result.setName(user.getName());
		for (final PlaceBookGroup group : user.getGroups())
		{
			result.getGroups().add(createGroup(group));
		}
		return result;
	}

	private static String encodePassword(final String password) throws NoSuchAlgorithmException
	{
		if (passwordDigest == null)
		{
			passwordDigest = MessageDigest.getInstance("MD5");
		}
		return (new HexBinaryAdapter()).marshal(passwordDigest.digest(password.getBytes())).toLowerCase();
	}

	private static ServerInfo getServerInfo()
	{
		final ServerInfo serverInfo = new ServerInfo();
		serverInfo.setServerName(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_SERVER_NAME, null));
		serverInfo.setVideoSize(Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_VIDEO_MAX_SIZE, "25")));
		serverInfo.setImageSize(Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_IMAGE_MAX_SIZE, "1")));
		serverInfo.setAudioSize(Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_AUDIO_MAX_SIZE, "10")));

		for (final ServiceInfo info : ServiceRegistry.getServices())
		{
			serverInfo.getServices().add(info);
		}

		return serverInfo;
	}

	private static User getUser(final EntityManager entityManager, final AsyncCallback<?> callback)
	{
		final String sessionID = getResponse(callback).getSessionID(sessionName);
		if (sessionID == null) { return null; }

		final Session session = entityManager.find(Session.class, sessionID);
		if (session == null) { return null; }

		return session.getUser();
	}

	private static void startUserSession(final EntityManager entityManager, final AsyncCallback<?> callback,
			final User user)
	{
		try
		{
			final String sessionID = getResponse(callback).createSessionID(sessionName);
			final Session session = new Session();
			session.setId(sessionID);
			session.setUser(user);

			entityManager.getTransaction().begin();

			entityManager.persist(session);

			entityManager.getTransaction().commit();

			Log.info("Persisted session");
		}
		finally
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
				Log.error("Rolling back session creation");
			}
		}
	}

	private static User verifyUser(final EntityManager entityManager, final AsyncCallback<?> callback)
			throws HTTPException
	{
		final User user = getUser(entityManager, callback);
		if (user == null) { throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); }
		return user;
	}

	@Transact
	@Override
	public void addGroup(final String placebookID, final String groupID,
			final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);

		try
		{
			verifyUser(entityManager, callback);
			final PlaceBookGroup group = entityManager.find(PlaceBookGroup.class, groupID);
			final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, placebookID);

			if (placebook == null) { throw new HTTPException(HttpServletResponse.SC_NOT_FOUND, "PlaceBook not found"); }
			if (group == null) { throw new HTTPException(HttpServletResponse.SC_NOT_FOUND, "Group not found"); }

			entityManager.getTransaction().begin();

			placebook.add(group);
			group.add(placebook);

			entityManager.getTransaction().commit();

			getResponse(callback).write(placebook);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	protected EntityManagerFactory createEntityManagerFactory()
	{
		final Properties properties = PropertiesSingleton.get(EMFSingleton.class.getClassLoader());
		return Persistence.createEntityManagerFactory("placebooks", properties);
	}

	@Override
	protected Parser createParser(final Method method)
	{
		if (method != null)
		{
			final Exclude exclude = method.getAnnotation(Exclude.class);
			if (exclude != null) { return new GsonParser(exclude.value()); }
		}
		return new GsonParser();
	}

	@Override
	public void deletePlaceBook(final String placebookID, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			verifyUser(entityManager, callback);
			entityManager.getTransaction().begin();
			final PlaceBookBinder binder = entityManager.find(PlaceBookBinder.class, placebookID);
			if (binder == null) { throw new HTTPException(HttpServletResponse.SC_NOT_FOUND, "PlaceBook not found"); }
			for (final PlaceBook placebook : binder.getPlaceBooks())
			{
				for (final PlaceBookItem item : placebook.getItems())
				{
					item.deleteItemData();
				}
			}

			for (final PlaceBookGroup group : binder.getGroups())
			{
				group.remove(binder);
			}
			entityManager.remove(binder);
			entityManager.getTransaction().commit();

			getShelf(callback);

			Log.info("Deleted PlaceBook");
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void getFeaturedPlaceBooks(final int count, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final TypedQuery<PlaceBookBinder> q = entityManager
					.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.state= :state", PlaceBookBinder.class);
			q.setParameter("state", State.PUBLISHED);

			final Shelf shelf = new Shelf();
			final List<PlaceBookBinder> pbs = q.getResultList();
			if (!pbs.isEmpty())
			{
				final Random random = new Random();
				for (int index = 0; index < count && !pbs.isEmpty(); index++)
				{
					final int rindex = random.nextInt(pbs.size());
					shelf.getEntries().add(createEntry(pbs.get(rindex)));
					pbs.remove(rindex);
				}
			}

			callback.onSuccess(shelf);
		}
		catch (final Exception e)
		{
			Log.error(e);
		}
	}

	@Override
	public void getGroup(final String id, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final PlaceBookGroup group = entityManager.find(PlaceBookGroup.class, id);
			if (group == null) { throw new HTTPException(HttpServletResponse.SC_NOT_FOUND, "Group not found"); }

			callback.onSuccess(createShelf(group.getPlaceBooks(), group));
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void getMedia(final String type, final String hash, final AsyncCallback<File> callback)
	{
		String itemPath = "";
		if (type.equalsIgnoreCase("imageitem"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash;
		}
		else if (type.equalsIgnoreCase("thumb"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_THUMBS, "") + File.separator + hash;
		}
		else if (type.equalsIgnoreCase("audioitem"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash;
		}
		else if (type.equalsIgnoreCase("videoitem"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash + "-chrome.ogg";

		}
		else if (type.equalsIgnoreCase("videoitemmobile"))
		{
			itemPath = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "") + File.separator + hash + "-mobile.ogg";
		}
		else if (type.equalsIgnoreCase("gpstraceitem"))
		{
			final EntityManager manager = getEntityManager(callback);
			try
			{
				final GPSTraceItem item = manager.find(GPSTraceItem.class, hash);
				getResponse(callback).setMimeType("application/gpx+xml");
				getResponse(callback).write(item.getText());
			}
			catch (final Exception e)
			{
				Log.error(e);
			}
			return;
		}
		else
		{
			callback.onFailure(new HTTPException(HttpServletResponse.SC_NOT_FOUND));
			return;
		}

		// ?Is there a path to serve the file...
		if (!itemPath.equalsIgnoreCase(""))
		{
			Log.info("Looking to serve file:" + itemPath);
			File serveFile = new File(itemPath);
			if (!serveFile.exists())
			{
				// Attempt to find other versions of the file... in case extension guess was
				// wrong...
				final String dirPath = itemPath.replace(serveFile.getName(), "");
				Log.warn("Cannot find " + serveFile.getAbsolutePath());
				itemPath = MediaItem.FindClosestFile(dirPath, hash);
				if (itemPath != null)
				{
					Log.warn("Using alternative file: " + itemPath);
					serveFile = new File(itemPath);
				}
			}
			
			Log.info("Serving file:" + serveFile.getAbsolutePath());

			callback.onSuccess(serveFile);
		}
		else
		{
			callback.onFailure(new HTTPException(HttpServletResponse.SC_NOT_FOUND));
		}
	}

	@Override
	public void getPaletteItems(final AsyncCallback<Iterable<Item>> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User user = verifyUser(entityManager, callback);

			final TypedQuery<PlaceBookItem> q = entityManager
					.createQuery(	"SELECT p FROM PlaceBookItem p WHERE p.owner = :owner AND p.placebook IS NULL",
									PlaceBookItem.class);
			q.setParameter("owner", user);

			final Collection<PlaceBookItem> pbs = q.getResultList();

			// Add preset items to the Palette
			final ArrayList<PlaceBookItem> presetItems = PresetItemsHelper.getPresetItems(user);
			pbs.addAll(presetItems);

			// Log.info("Converting " + pbs.size() + " PlaceBookItems to JSON");
			// Log.info("User " + user.getName());

			getResponse(callback).write(pbs);

			runSync(callback, null);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void getPlaceBook(final String id, final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);

		try
		{
			final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, id);
			if (placebook != null)
			{
				final User user = getUser(entityManager, callback);
				if (!placebook.canBeRead(user)) { throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED,
						"User doesn't have sufficient permissions"); }

				placebookViewed(id);
				getResponse(callback).write(placebook);
			}
			else
			{
				throw new HTTPException(404, "PlaceBook not found");
			}
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void getRecentPlaceBooks(final AsyncCallback<String> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		try
		{
			final PrintWriter writer = getResponse(callback).getResponse().getWriter();
			getResponse(callback).setMimeType("application/atom+xml; charset=UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			writer.println();
			writer.println("<feed xmlns=\"http://www.w3.org/2005/Atom\">");
			writer.println("<title>Recent PlaceBooks</title>");

			try
			{
				final String url = getResponse(callback).getHostURL();
				final User user = getUser(entityManager, callback);

				writer.println("<updated>" + formatter.format(new Date()) + "</updated>");
				writer.println("<id>" + getResponse(callback).getRequest().getRequestURL() + "</id>");

				for (final String key : recentBooks)
				{
					final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, key);
					if (placebook != null && placebook.canBeRead(user))
					{
						writer.println("<entry>");
						writer.println("<title>" + placebook.getMetadata().get("title") + "</title>");
						writer.println("<summary>" + placebook.getMetadata().get("description") + "</summary>");
						writer.println("<id>" + url + "#placebook:" + key + "</id>");
						writer.println("<updated>" + formatter.format(placebook.getTimestamp()) + "</updated>");
						writer.println("<link rel=\"self\" href=\"" + url + "#placebook:" + key + "\" />");
						writer.println("<link rel=\"enclosure\" href=\"" + url + "/command/package/" + key + "\" />");
						writer.println("<link rel=\"edit\" href=\"" + url + "#placebook:edit:" + key + "\" />");

						writer.println("</entry>");
					}
				}
			}
			finally
			{
				entityManager.close();

				writer.println("</feed>");
				getResponse(callback).getResponse().flushBuffer();
			}
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void getServerInfo(final AsyncCallback<ServerInfo> callback)
	{
		callback.onSuccess(getServerInfo());
	}

	@Override
	public Class<?> getServerInterface()
	{
		return PlaceBookServerExt.class;
	}

	@Override
	public void getShelf(final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User user = verifyUser(entityManager, callback);
			getShelf(user, entityManager, callback);

			runSync(callback, null);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	private void getShelf(final User user, final EntityManager entityManager, final AsyncCallback<Shelf> callback)
	{
		final TypedQuery<PlaceBookBinder> q = entityManager
				.createQuery(	"SELECT p FROM PlaceBookBinder p WHERE p.owner = :user OR p.permsUsers LIKE :email",
								PlaceBookBinder.class);
		q.setParameter("user", user);
		q.setParameter("email", "%" + user.getEmail() + "%");

		final Collection<PlaceBookBinder> pbs = q.getResultList();
		Log.info("Converting " + pbs.size() + " PlaceBookBinders to JSON");
		Log.info("User " + user.getName());

		callback.onSuccess(createShelf(pbs, user));
	}

	@Override
	public void getUser(final AsyncCallback<org.placebooks.client.model.User> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User user = verifyUser(entityManager, callback);

			getResponse(callback).write(user);

			runSync(callback, null);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void linkAccount(final String username, final String password, final String service,
			final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User user = verifyUser(entityManager, callback);
			if (user == null) { return; }

			entityManager.getTransaction().begin();

			// Login details must be unique to user
			final TypedQuery<LoginDetails> q_ = entityManager
					.createQuery(	"SELECT l FROM LoginDetails l WHERE l.service= :service AND l.username= :username AND l.user.id != :userid",
									LoginDetails.class);
			q_.setParameter("service", service);
			q_.setParameter("username", username);
			q_.setParameter("userid", user.getKey());
			final Collection<LoginDetails> ll = q_.getResultList();
			Log.debug("Found " + ll.size() + " LoginDetails");

			if (ll.size() > 0)
			{
				Log.error("LoginDetails already linked to user");
				throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED);
			}

			final Service serviceImpl = ServiceRegistry.getService(service);
			if (service != null)
			{
				if (!serviceImpl.checkLogin(username, password)) { throw new HTTPException(
						HttpServletResponse.SC_BAD_REQUEST); }
			}

			final LoginDetails loginDetails = new LoginDetails(user, service, null, username, password);
			entityManager.persist(loginDetails);
			user.add(loginDetails);
			entityManager.getTransaction().commit();

			final TypedQuery<PlaceBookBinder> q = entityManager
					.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.owner= :owner", PlaceBookBinder.class);
			q.setParameter("owner", user);

			final Collection<PlaceBookBinder> pbs = q.getResultList();
			callback.onSuccess(createShelf(pbs, user));
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}

		runSync(callback, service);
	}

	@Override
	public void login(final String email, final String password, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final TypedQuery<User> query = entityManager.createQuery(	"SELECT u FROM User u WHERE u.email = :email",
																		User.class);
			query.setParameter("email", email);
			final User user = query.getSingleResult();
			if (user == null) { throw new HTTPException(400, "Unknown email"); }

			Log.info("Password Hash " + encodePassword(password) + " " + user.getPasswordHash());

			if (!user.getPasswordHash().equals(encodePassword(password))) { throw new HTTPException(400,
					"Incorrect Password"); }

			startUserSession(entityManager, callback, user);

			getShelf(user, entityManager, callback);

			runSync(callback, null);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void logout(final AsyncCallback<String> callback)
	{
		final String sessionID = getResponse(callback).getSessionID(sessionName);
		if (sessionID == null) { return; }

		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final Session session = entityManager.find(Session.class, sessionID);
			if (session == null) { return; }

			entityManager.getTransaction().begin();

			entityManager.remove(session);

			entityManager.getTransaction().commit();

			getResponse(callback).getResponse().reset();
			getResponse(callback).write("Success");
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void oauth(final String service, final AsyncCallback<String> callback)
	{
		try
		{
			final Service serv = ServiceRegistry.getService(service);
			if (serv != null)
			{
				Log.info(serv.getInfo().getName());
				final EntityManager entityManager = getEntityManager(callback);
				final User user = verifyUser(entityManager, callback);

				final String result = serv.getAuthenticationURL(entityManager, user, null);
				Log.info(result);

				if (result != null)
				{
					getResponse(callback).redirect(result);
					return;
				}
			}

			getResponse(callback).redirectReferrer();
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void placebookPackage(final String id, final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User currentUser = getUser(entityManager, callback);
			final PlaceBookBinder placebook = entityManager.find(PlaceBookBinder.class, id);
			if (!placebook.canBeRead(currentUser)) { throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED,
					"User doesn't have sufficient permissions"); }

			@SuppressWarnings("unchecked")
			final File zipFile = PlaceBooksAdminHelper.makePackage(entityManager, placebook, new GsonParser(
					JsonDownloadIgnore.class, JsonIgnore.class));
			if (zipFile == null)
			{
				Log.info("Failed to create zip file");
				return;
			}

			getResponse(callback).setMimeType("application/x-placebook");
			getResponse(callback).setHeader("Content-Disposition",
											"attachment; filename=\"" + placebook.getKey() + ".placebook\"");
			getResponse(callback).setHeader("Content-Length", Long.toString(zipFile.length()));
			getResponse(callback).write(new FileInputStream(zipFile));
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	private void placebookViewed(final String key)
	{
		while (recentBooks.remove(key))
		{
		}

		while (recentBooks.size() >= 10)
		{
			recentBooks.removeLast();
		}

		recentBooks.addFirst(key);
	}

	@Override
	public void publishPlaceBook(final org.placebooks.client.model.PlaceBook placebook,
			final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User user = verifyUser(entityManager, callback);
			final PlaceBookBinder dbBinder = entityManager.find(PlaceBookBinder.class, placebook.getId());
			if (dbBinder == null) { throw new HTTPException(HttpServletResponse.SC_NOT_FOUND); }
			if (dbBinder.getOwner() != user)
			{
				final PlaceBookBinder.Permission perms = dbBinder.getPermission(user);
				if (perms == null) { throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED,
						"User doesn't have sufficient permissions"); }
			}

			final PlaceBookBinder input = getResponse(callback).getParser().parse(	PlaceBookBinder.class,
																					getResponse(callback).getRequest()
																							.getParameter("placebook"));
			final PlaceBookBinder result = PlaceBooksAdminHelper.savePlaceBookBinder(entityManager, input, user);
			final PlaceBookBinder published = PlaceBooksAdminHelper.publishPlaceBookBinder(entityManager, result);

			getResponse(callback).write(published);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void qrcode(final String type, final String key, final AsyncCallback<InputStream> callback)
	{
		try
		{
			Log.debug("QR Code: " + key);

			final String eTag = key;
			final String ifNoneMatch = getResponse(callback).getRequest().getHeader("If-None-Match");
			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) { throw new HTTPException(304); }

			String hostURL = getResponse(callback).getHostURL();
			if (!hostURL.endsWith("/"))
			{
				hostURL = hostURL + "/";
			}
			final String url = hostURL + "?utm_medium=qrcode&utm_source=qrcode#" + type + ":" + key;
			final QRCodeWriter writer = new QRCodeWriter();
			final BitMatrix matrix = writer.encode(url, com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);
			final MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, 0x00FFFFFF);
			final BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, config);

			getResponse(callback).getResponse().reset();
			getResponse(callback).getResponse().setBufferSize(MediaHelper.DEFAULT_BUFFER_SIZE);
			getResponse(callback).setMimeType("image/png");
			getResponse(callback).setHeader("ETag", eTag);
			getResponse(callback).getResponse().setDateHeader("Last-Modified", System.currentTimeMillis());
			getResponse(callback).getResponse().setDateHeader(	"Expires",
																System.currentTimeMillis()
																		+ MediaHelper.DEFAULT_EXPIRE_TIME);

			final OutputStream out = getResponse(callback).getResponse().getOutputStream();
			ImageIO.write(image, "png", out);
			out.close();
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void registerAccount(final String name, final String email, final String password,
			final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final String encodedPassword = encodePassword(password);
			final User user = new User(name, email, encodedPassword);

			entityManager.getTransaction().begin();
			entityManager.persist(user);
			entityManager.getTransaction().commit();

			startUserSession(entityManager, callback, user);

			getShelf(user, entityManager, callback);

			runSync(callback, null);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	private void runSync(final AsyncCallback<?> callback, final String service)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final EntityManager entityManager = createEntityManagerFactory().createEntityManager();
				try
				{
					final User user = verifyUser(entityManager, callback);
					if (service == null)
					{
						ServiceRegistry.updateServices(entityManager, user);
					}
					else
					{
						ServiceRegistry.updateService(entityManager, user, service);
					}
				}
				catch (final Exception e)
				{
					Log.error(e);
				}
				finally
				{
					entityManager.close();
				}
			}
		}).start();
	}

	@Override
	public void saveGroup(final Shelf shelf, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User currentUser = verifyUser(entityManager, callback);
			if (currentUser == null) { return; }

			PlaceBookGroup group;
			if (shelf.getGroup() != null && shelf.getGroup().getId() != null)
			{
				group = entityManager.find(PlaceBookGroup.class, shelf.getGroup().getId());

				if (group.getOwner() == null)
				{
					group.setOwner(currentUser);
				}
				else if (group.getOwner() != currentUser) { throw new HTTPException(
						HttpServletResponse.SC_UNAUTHORIZED, "User doesn't have sufficient permissions"); }
			}
			else
			{
				group = new PlaceBookGroup();
			}

			final Collection<PlaceBookBinder> placebooks = new HashSet<PlaceBookBinder>();
			for (final Entry entry : shelf.getEntries())
			{
				final PlaceBookBinder binder = entityManager.find(PlaceBookBinder.class, entry.getKey());
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

			group.setDescription(shelf.getGroup().getDescription());
			group.setTitle(shelf.getGroup().getTitle());
			if (group.getImage() == null)
			{
				group.setImage(new ImageItem());
			}

			group.setImage(getResponse(callback).transform(ImageItem.class, shelf.getGroup().getImage()));

			group.setPlaceBooks(placebooks);

			entityManager.getTransaction().begin();

			group = entityManager.merge(group);

			currentUser.add(group);

			entityManager.getTransaction().commit();

			callback.onSuccess(createShelf(group.getPlaceBooks(), group));
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void savePlaceBook(final org.placebooks.client.model.PlaceBook placebook,
			final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);

		try
		{
			final User user = verifyUser(entityManager, callback);
			if (placebook.getId() != null)
			{
				final PlaceBookBinder dbBinder = entityManager.find(PlaceBookBinder.class, placebook.getId());
				if (!dbBinder.canBeWriten(user)) { throw new HTTPException(HttpServletResponse.SC_UNAUTHORIZED,
						"User doesn't have sufficient permissions"); }
			}

			final PlaceBookBinder input = getResponse(callback).getParser().parse(	PlaceBookBinder.class,
																					getResponse(callback).getRequest()
																							.getParameter("placebook"));
			final PlaceBookBinder result = PlaceBooksAdminHelper.savePlaceBookBinder(entityManager, input, user);

			getResponse(callback).write(result);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void search(final String search, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final Shelf shelf = new Shelf();
			if (search == null || search.isEmpty())
			{
				final TypedQuery<PlaceBookBinder> q = entityManager
						.createQuery("SELECT p FROM PlaceBookBinder p WHERE p.state= :state", PlaceBookBinder.class);
				q.setParameter("state", State.PUBLISHED);
				final List<PlaceBookBinder> binders = q.getResultList();

				for (final PlaceBookBinder binder : binders)
				{
					shelf.getEntries().add(createEntry(binder));
				}
			}
			else
			{
				for (final Map.Entry<PlaceBookBinder, Integer> searchItem : PlaceBooksAdminHelper
						.search(entityManager, search))
				{
					final PlaceBookBinder p = searchItem.getKey();
					if (p != null && p.getState() == PlaceBookBinder.State.PUBLISHED && searchItem.getValue() > 0)
					{
						Log.info("Search result: pb key=" + searchItem.getKey().getKey() + ", score="
								+ searchItem.getValue());
						final Entry entry = createEntry(p);
						entry.setScore(searchItem.getValue());
						shelf.getEntries().add(entry);
					}
				}
			}
			callback.onSuccess(shelf);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void searchLocation(final String geometry, final AsyncCallback<Shelf> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final Geometry geometry_ = new WKTReader().read(geometry);
			final Shelf shelf = new Shelf();
			for (final Map.Entry<PlaceBookBinder, Double> searchItem : PlaceBooksAdminHelper
					.searchLocationForPlaceBookBinders(entityManager, geometry_))
			{
				final PlaceBookBinder p = searchItem.getKey();
				if (p != null && p.getState() == PlaceBookBinder.State.PUBLISHED && searchItem.getValue() > 0)
				{
					Log.info("Search result: pb key=" + searchItem.getKey().getKey() + ", score="
							+ searchItem.getValue());
					final Entry entry = createEntry(p);
					entry.setDistance(searchItem.getValue().floatValue());
					shelf.getEntries().add(entry);
				}
			}
			callback.onSuccess(shelf);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Override
	public void sync(final String serviceName)
	{
		Log.info("Sync " + serviceName);
		// TODO
		// final EntityManager entityManager = createEntityManager();
		// final User user = verifyUser(entityManager, res);
		// ServiceRegistry.updateService(entityManager, user, serviceName);
		// res.setStatus(200);
	}

	@Transact
	@Override
	public void uploadFile(final String type, final String id, final FileItem file, final AsyncCallback<String> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		Log.info("Uploading " + type + " for item " + id);
		try
		{
			final User currentUser = verifyUser(entityManager, callback);
			entityManager.getTransaction().begin();

			if (type != null)
			{
				final ServerInfo info = getServerInfo();
				long size = 0;
				if (type.equals("ImageItem"))
				{
					size = info.getImageSize();
				}
				else if (type.equals("AudioItem"))
				{
					size = info.getAudioSize();
				}
				else if (type.equals("VideoItem"))
				{
					size = info.getVideoSize();
				}

				size = size * 1024 * 1024;

				if (size > 0 && file.getSize() > size) { throw new HTTPException(413, "Error: Item too Large\n"); }
			}

			PlaceBookItem item = null;
			if (id != null)
			{
				item = entityManager.find(PlaceBookItem.class, id);
				if (item.getPlaceBook() != null)
				{
					final PlaceBookBinder dbBinder = item.getPlaceBook().getPlaceBookBinder();
					if (dbBinder.getOwner() != currentUser)
					{
						PlaceBookBinder.Permission perms = PlaceBookBinder.Permission.R_W;
						perms = dbBinder.getPermission(currentUser);
						if (perms == null || (perms != null && perms == PlaceBookBinder.Permission.R)) { throw new Exception(
								"No permission to upload"); }
					}
				}

				if (item instanceof MediaItem)
				{
					((MediaItem) item).setSourceURL(null);
					((MediaItem) item).writeDataToDisk(file.getInputStream());

					getResponse(callback).setMimeType("text/html");
					callback.onSuccess(((MediaItem) item).getHash());
				}
				else if (item instanceof GPSTraceItem)
				{
					final GPSTraceItem gpsItem = (GPSTraceItem)item;
					gpsItem.setSourceURL(null);
					gpsItem.readText(file.getInputStream());
					
					getResponse(callback).setMimeType("text/html");
					callback.onSuccess(gpsItem.getText());
					
					entityManager.merge(gpsItem);
				}

				entityManager.getTransaction().commit();
			}
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}

	@Transact
	@Override
	public void uploadPackage(final InputStream file,
			final AsyncCallback<org.placebooks.client.model.PlaceBook> callback)
	{
		final EntityManager entityManager = getEntityManager(callback);
		try
		{
			final User currentUser = verifyUser(entityManager, callback);
			if (currentUser == null) { return; }

			Log.info("Uploading Package");
			final String name = UUID.randomUUID().toString();
			final String outputFolder = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") + "/" + name;

			final ZipInputStream zis = new ZipInputStream(file);
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();
			final byte[] buffer = new byte[1024];
			while (ze != null)
			{
				final String fileName = ze.getName();
				final File newFile = new File(outputFolder, fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				final FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

			final String mediaDir = PropertiesSingleton.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

			final File folder = new File(outputFolder);
			for (final File media : folder.listFiles())
			{
				if (media.getName().equals("data.json"))
				{
					continue;
				}

				final File dest = new File(mediaDir, media.getName());
				if (!dest.exists())
				{
					media.renameTo(dest);
				}
			}

			@SuppressWarnings("unchecked")
			final Parser parser = new GsonParser(Id.class, JsonIgnore.class, JsonDownloadIgnore.class);
			final File dataJson = new File(outputFolder, "data.json");
			final PlaceBookBinder binder = parser.parse(PlaceBookBinder.class, new FileReader(dataJson));

			entityManager.getTransaction().begin();

			User user = null;
			if (binder.getOwner() != null)
			{
				final TypedQuery<User> query = entityManager
						.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
				query.setParameter("email", binder.getOwner().getEmail());
				try
				{
					user = query.getSingleResult();
				}
				catch (final NoResultException e)
				{

				}
				catch (final Exception e)
				{
					Log.error(e);
				}
			}

			if (user == null)
			{
				entityManager.persist(binder.getOwner());
				user = binder.getOwner();
				binder.setState(State.PUBLISHED);
			}

			if (user != null)
			{
				binder.setOwner(user);
				user.add(binder);
			}
			for (final PlaceBook pb : binder.getPlaceBooks())
			{
				if (user != null)
				{
					pb.setOwner(user);
				}
				for (final PlaceBookItem pbi : pb.getItems())
				{
					if (user != null)
					{
						pbi.setOwner(user);
					}
					entityManager.persist(pbi);
				}
				entityManager.persist(pb);
			}

			entityManager.persist(binder);
			entityManager.getTransaction().commit();

			getResponse(callback).write(binder);
		}
		catch (final Exception e)
		{
			callback.onFailure(e);
		}
	}
}