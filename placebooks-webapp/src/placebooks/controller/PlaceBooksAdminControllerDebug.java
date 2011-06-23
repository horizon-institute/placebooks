package placebooks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import placebooks.model.AudioItem;
import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailTracksResponse;
import placebooks.model.EverytrailTripsResponse;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.MapImageItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.model.WebBundleItem;
import placebooks.utils.InitializeDatabase;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


// NOTE: This class contains admin controller debug stuff. Put dirty debug stuff
// in here that is NOT generic / not for front-end. 
// 
// Or use this class as a testing ground...

@Controller
public class PlaceBooksAdminControllerDebug
{

	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminControllerDebug.class.getName());


	private static final int MEGABYTE = 1048576;


	@RequestMapping(value = "/admin/publish_placebook/{key}",
			method = RequestMethod.GET)
			public ModelAndView publishPlaceBook(@PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		final PlaceBook p = em.find(PlaceBook.class, key);
		final PlaceBook p_ = PlaceBooksAdminHelper.publishPlaceBook(em, p);
		em.close();

		log.info("Published PlaceBook, old key = " + key + ", new key = " 
				+ p_.getKey());

		return new ModelAndView("message", "text", 
				"Published PlaceBook, new key = " + key);
	}

	@RequestMapping(value = "/admin/add_item/map", 
			method = RequestMethod.POST)
			@SuppressWarnings("unchecked")	
			public ModelAndView addMapImageItem(final HttpServletRequest req, 
					final HttpServletResponse res)
	{

		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBooksAdminController.ItemData itemData = 
			new PlaceBooksAdminController.ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.getTransaction().begin();

			for (final Enumeration<String> params = req.getParameterNames(); 
			params.hasMoreElements();)
			{
				final String param = params.nextElement();
				final String value = req.getParameterValues(param)[0];
				if (!itemData.processItemData(pm, param, value))
				{
					String[] split = PlaceBooksAdminHelper.getExtension(param);
					if (split == null)
						continue;

					String prefix = split[0], suffix = split[1];

					final PlaceBook p = pm.find(PlaceBook.class, suffix);

					if (prefix.contentEquals("map"))
					{
						pbi = new MapImageItem(null, null, null, null);
						p.addItem(pbi);
					}
				}

			}

			if (pbi == null || itemData.getOwner() == null) 
			{ 
				return new ModelAndView(
						"message", "text", "Error setting data elements"); 
			}

			pbi.setOwner(itemData.getOwner());
			pbi.setGeometry(itemData.getGeometry());
			pbi.setSourceURL(itemData.getSourceURL());
			((MapImageItem)pbi).setPath(
					(TileHelper.getMap(pbi.getGeometry())).getPath()
			);

			pm.getTransaction().commit();

		}		
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
			pm.close();
		}

		return new ModelAndView("message", "text", "MapImageItem added");

	}


	@RequestMapping(value = "/admin/debug/print_placebooks", 
			method = RequestMethod.GET)
			public ModelAndView printPlaceBooks()
	{

		final EntityManager pm = EMFSingleton.getEntityManager();
		List<PlaceBook> pbs = null;
		try
		{
			final TypedQuery<PlaceBook> query = 
				pm.createQuery("SELECT p FROM PlaceBook p", PlaceBook.class);
			pbs = query.getResultList();
			// query.closeAll();
		}
		catch (final ClassCastException e)
		{
			log.error(e.toString());
		}

		ModelAndView mav = null;
		if (pbs != null)
		{
			mav = new ModelAndView("placebooks");
			mav.addObject("pbs", pbs);
		}
		else
		{
			mav = new ModelAndView("message", "text", 
			"Error listing PlaceBooks");
		}

		for (final PlaceBook pb : pbs)
		{
			for (final Entry<String, String> e : pb.getMetadata().entrySet())
			{
				log.info("entry: '" + e.getKey() + "' => '" + e.getValue() + 
				"'");
			}
		}

		pm.close();

		return mav;

	}

	@RequestMapping(value = "/admin/add_placebook", 
			method = RequestMethod.POST)
			public ModelAndView addPlaceBook(@RequestParam final String owner, 
					@RequestParam final String geometry)
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

			if (owner_ == null) 
			{ 
				return new ModelAndView("message", "text", 
				"User does not exist"); 
			}

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
				pm.close();
			}

			return new ModelAndView("message", "text", "PlaceBook added");
		}
		else
		{
			return new ModelAndView("message", "text", "Error in POST");
		}
	}

	@RequestMapping(value = "/admin/add_placebookitem_mapping/{type}", 
			method = RequestMethod.POST)
			public ModelAndView addPlaceBookItemMapping(
					@RequestParam final String key, @RequestParam final String mKey,
					@RequestParam final String mValue, 
					@PathVariable("type") final String type)
	{
		if (!type.equals("metadata") && !type.equals("parameter"))
			return new ModelAndView("message", "text", "Error in type");

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
				pm.close();
			}

			return new ModelAndView("message", "text", "Metadata/param added");
		}
		else
		{
			return new ModelAndView("message", "text", "Error in POST");
		}
	}

	@RequestMapping(value = "/admin/add_placebook_metadata", 
			method = RequestMethod.POST)
			public ModelAndView addPlaceBookMetadata(@RequestParam final String key, 
					@RequestParam final String mKey,
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

				pm.close();
			}
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

	@RequestMapping(value = "/admin/add_item/webbundle", 
			method = RequestMethod.POST)
			@SuppressWarnings("unchecked")
			public ModelAndView createWebBundle(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBooksAdminController.ItemData itemData = 
			new PlaceBooksAdminController.ItemData();
		WebBundleItem wbi = null;

		try
		{
			pm.getTransaction().begin();

			for (final Enumeration<String> params = req.getParameterNames(); 
			params.hasMoreElements();)
			{
				final String param = params.nextElement();
				final String value = req.getParameterValues(param)[0];
				if (!itemData.processItemData(pm, param, value))
				{
					String[] split = PlaceBooksAdminHelper.getExtension(param);
					if (split == null)
						continue;

					String prefix = split[0], suffix = split[1];

					if (prefix.contentEquals("url"))
					{
						try
						{
							final PlaceBook p = pm.find(PlaceBook.class, 
									suffix);
							URL sourceURL = null;
							if (value.length() > 0)
							{
								sourceURL = new URL(value);
							}
							wbi = new WebBundleItem(null, null, sourceURL, 
									null, null);
							p.addItem(wbi);
							pm.getTransaction().commit();
							pm.getTransaction().begin();
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
				wbi.setWebBundlePath(wbi.generateWebBundlePath());
			}

			if (wbi == null || (wbi != null && (wbi.getSourceURL() == null || 
					wbi.getOwner() == null))) 
			{ 
				throw new Exception("Error setting data elements"); 
			}

			PlaceBooksAdminHelper.scrape(wbi);

			pm.getTransaction().commit();
		}
		catch (final Throwable e)
		{
			log.warn(e.getMessage(), e);
		}
		finally
		{
			if (pm.getTransaction().isActive())
			{
				pm.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}

			pm.close();
		}

		return new ModelAndView("message", "text", "Scraped");
	}


	@RequestMapping(value = "/admin/delete_placebook/{key}", 
			method = RequestMethod.GET)
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

			pm.close();
		}

		log.info("Deleted PlaceBook");

		return new ModelAndView("message", "text", "Deleted PlaceBook: " + key);
	}

	@RequestMapping(value = "/admin/delete_placebookitem/{key}", 
			method = RequestMethod.GET)
			public ModelAndView deletePlaceBookItem(@PathVariable("key") 
					final String key)
	{

		final EntityManager pm = EMFSingleton.getEntityManager();

		try
		{
			pm.getTransaction().begin();
			final PlaceBookItem item = pm.find(PlaceBookItem.class, key);
			item.deleteItemData();
			item.getPlaceBook().removeItem(item);
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
			pm.close();
		}

		log.info("Deleted PlaceBookItem " + key);

		return new ModelAndView("message", "text", "Deleted PlaceBookItem: " 
				+ key);

	}

	@RequestMapping(value = "/admin/add_item/text", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView uploadText(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBooksAdminController.ItemData itemData = 
			new PlaceBooksAdminController.ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.getTransaction().begin();

			for (final Enumeration<String> params = req.getParameterNames(); 
			params.hasMoreElements();)
			{
				final String param = params.nextElement();
				final String value = req.getParameterValues(param)[0];
				if (!itemData.processItemData(pm, param, value))
				{
					String[] split = PlaceBooksAdminHelper.getExtension(param);
					if (split == null)
						continue;

					String prefix = split[0], suffix = split[1];

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

			if ((pbi != null && ((TextItem) pbi).getText() == null) || 
					pbi == null || itemData.getOwner() == null) 
			{ 
				return new ModelAndView(
						"message", "text", "Error setting data elements"); 
			}

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
			pm.close();
		}

		return new ModelAndView("message", "text", "TextItem added");
	}


	@RequestMapping(value = "/admin/add_item/uploadandcreate", 
			method = RequestMethod.POST)
			public ModelAndView uploadFile(final HttpServletRequest req)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBooksAdminController.ItemData itemData = 
			new PlaceBooksAdminController.ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.getTransaction().begin();

			@SuppressWarnings("unchecked")			
			final List<FileItem> items = 
				new ServletFileUpload(new DiskFileItemFactory())
			.parseRequest(req);

			for (FileItem item : items)
			{
				if (item.isFormField())
				{
					String value = Streams.asString(item.getInputStream());
					itemData.processItemData(pm, item.getFieldName(), value);
				}
				else
				{
					log.info("*** item.getSize() = " + item.getSize());
					//String property = null;

					String[] split = 
						PlaceBooksAdminHelper.getExtension(item.getFieldName());
					if (split == null)
						continue;

					String prefix = split[0], suffix = split[1];

					final PlaceBook p = pm.find(PlaceBook.class, suffix);

					if (prefix.contentEquals("gpstrace"))
					{
						final InputStreamReader reader = 
							new InputStreamReader(item.getInputStream());
						final StringWriter writer = new StringWriter();
						int data;
						while((data = reader.read()) != -1)
						{
							writer.write(data);
						}
						reader.close();
						writer.close();
						pbi = new GPSTraceItem(null, null, null, 
								writer.toString());
						p.addItem(pbi);

						continue;
					}
					else if (!prefix.contentEquals("video") &&
							!prefix.contentEquals("audio") && 
							!prefix.contentEquals("image"))
					{
						throw new Exception("Unsupported file type");
					}

					final String path = 
						PropertiesSingleton
						.get(this.getClass().getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_MEDIA, "");

					if (!new File(path).exists() && !new File(path).mkdirs()) 
					{
						throw new Exception("Failed to write file"); 
					}

					File file = null;

					final int extIdx = item.getName().lastIndexOf(".");
					final String ext = 
						item.getName().substring(extIdx + 1, 
								item.getName().length());

					if (prefix.contentEquals("video"))
					{
						int maxSize = Integer.parseInt(
								PropertiesSingleton
								.get(PlaceBooksAdminHelper.class.getClassLoader())
								.getProperty(
										PropertiesSingleton.IDEN_VIDEO_MAX_SIZE, 
										"20"
								)
						);
						if ((item.getSize() / MEGABYTE) > maxSize)
							throw new Exception("File too big");

						pbi = new VideoItem(null, null, null, null);
						p.addItem(pbi);
						pm.getTransaction().commit();
						pm.getTransaction().begin();
						((VideoItem) pbi).setPath(path + "/" + pbi.getKey() 
								+ "." + ext);

						file = new File(((VideoItem) pbi).getPath());
					}
					else if (prefix.contentEquals("audio"))
					{
						int maxSize = Integer.parseInt(
								PropertiesSingleton
								.get(PlaceBooksAdminHelper.class.getClassLoader())
								.getProperty(
										PropertiesSingleton.IDEN_AUDIO_MAX_SIZE, 
										"10"
								)
						);
						if ((item.getSize() / MEGABYTE) > maxSize)
							throw new Exception("File too big");

						pbi = new AudioItem(null, null, null, null);
						p.addItem(pbi);
						pm.getTransaction().commit();
						pm.getTransaction().begin();
						((AudioItem) pbi).setPath(path + "/" + pbi.getKey() 
								+ "." + ext);

						file = new File(((AudioItem) pbi).getPath());
					}
					else if (prefix.contentEquals("image"))
					{
						int maxSize = Integer.parseInt(
								PropertiesSingleton
								.get(PlaceBooksAdminHelper.class.getClassLoader())
								.getProperty(
										PropertiesSingleton.IDEN_IMAGE_MAX_SIZE, 
										"1"
								)
						);
						if ((item.getSize() / MEGABYTE) > maxSize)
							throw new Exception("File too big");

						pbi = new ImageItem(null, null, null, null);
						p.addItem(pbi);
						pm.getTransaction().commit();
						pm.getTransaction().begin();
						((ImageItem)pbi).setPath(path + "/" + pbi.getKey()
								+ "." + ext);
						file = new File(((ImageItem)pbi).getPath());
					}

					final InputStream input = item.getInputStream();
					final OutputStream output = new FileOutputStream(file);
					int byte_;
					while ((byte_ = input.read()) != -1)
					{
						output.write(byte_);
					}
					output.close();
					input.close();

					log.info("Wrote " + prefix + " file " 
							+ file.getAbsolutePath());

				}

			}

			if (pbi == null || itemData.getOwner() == null) 
			{ 
				throw new Exception("Error setting data elements"); 
			}

			pbi.setOwner(itemData.getOwner());
			pbi.setSourceURL(itemData.getSourceURL());
			pbi.setGeometry(itemData.getGeometry());

			pm.getTransaction().commit();
		}
		catch (final Throwable e)
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

			pm.close();
		}

		return new ModelAndView("message", "text", "Done");
	}

	@RequestMapping(value = "/admin/everytrail")
	public void getEverytrailData() throws Exception
	{
		EntityManager entityManager = EMFSingleton.getEntityManager();
		User testUser = UserManager.getCurrentUser(entityManager);
		LoginDetails details = testUser.getLoginDetails(EverytrailHelper.SERVICE_NAME);		
		if(details == null)
		{
			return;
		}
		
		EverytrailLoginResponse loginResponse = 
			EverytrailHelper.UserLogin(details.getUsername(), 
					details.getPassword());

		if(loginResponse.getStatus().equals("error"))
		{
			throw new Exception("Everytrail Login Failed");
		}
		
		entityManager.getTransaction().begin();
		// Save user id
		details.setUserID(loginResponse.getValue());
		entityManager.getTransaction().commit();		
		EverytrailTripsResponse trips = EverytrailHelper.Trips(loginResponse.getValue());

		for (Node trip : trips.getTrips())
		{
			final NamedNodeMap tripAttr = trip.getAttributes();
			final String tripId = tripAttr.getNamedItem("id").getNodeValue();
			log.debug("Getting tracks for trip: " + tripId);
			EverytrailTracksResponse tracks = 
				EverytrailHelper.Tracks(tripId, details.getUsername(), 
						details.getPassword());
			for (Node track : tracks.getTracks())
			{

				GPSTraceItem gpsItem = new GPSTraceItem(testUser, null, null, "");
				ItemFactory.toGPSTraceItem(testUser, track, gpsItem);
				gpsItem = (GPSTraceItem) gpsItem.saveUpdatedItem();
			}
		}

		EverytrailPicturesResponse picturesResponse = 	
			EverytrailHelper.Pictures(loginResponse.getValue());

		Vector<Node> pictures = picturesResponse.getPictures();

		for (Node picture : pictures)
		{
			ImageItem imageItem = new ImageItem(testUser, null, null, null);
			ItemFactory.toImageItem(testUser, picture, imageItem);
			imageItem = (ImageItem) imageItem.saveUpdatedItem();
		}	
	}

	@RequestMapping(value = "/admin/delete/all_placebooks", 
			method = RequestMethod.GET)
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

	@RequestMapping(value = "/admin/test/everytrail/login", method = RequestMethod.POST)
	public ModelAndView testEverytrailLogin(final HttpServletRequest req)
	{
		log.info("Logging into everytrail as " + req.getParameter("username") + "...");
		final EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"),
				req.getParameter("password"));
		return new ModelAndView("message", "text", "Log in status: " + response.getStatus() + "<br/>Log in value: "
				+ response.getValue() + "<br/>");
	}

	@RequestMapping(value = "/admin/reset", method = RequestMethod.GET)
	public ModelAndView reset(final HttpServletRequest req, final HttpServletResponse res)
	{
		InitializeDatabase.main(null);
		return null;
	}

	@RequestMapping(value = "/admin/test/everytrail/pictures", method = RequestMethod.POST)
	public ModelAndView testEverytrailPictures(final HttpServletRequest req)
	{
		ModelAndView returnView;

		final EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"),
				req.getParameter("password"));
		log.debug("logged in");
		if (response.getStatus().equals("success"))
		{
			final EverytrailPicturesResponse picturesResponse = EverytrailHelper.Pictures(response.getValue());
			log.debug(picturesResponse.getStatus());
			returnView = new ModelAndView("message", "text", "Logged in and got picutre list: <br /><pre>"
					+ picturesResponse.getStatus() + "</pre><br/>");
		}
		else
		{
			return new ModelAndView("message", "text", "Log in status: " + response.getStatus()
					+ "<br />Log in value: " + response.getValue() + "<br/>");
		}
		return returnView;
	}

	@RequestMapping(value = "/admin/test/everytrail/trips", method = RequestMethod.POST)
	public ModelAndView testEverytrailTrips(final HttpServletRequest req)
	{
		ModelAndView returnView;

		final EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"),
				req.getParameter("password"));
		log.debug("logged in");
		if (response.getStatus().equals("success"))
		{
			final EverytrailTripsResponse tripsResponse = EverytrailHelper.Trips(response.getValue());
			log.debug(tripsResponse.getStatus());
			returnView = new ModelAndView("message", "text", "Logged in and got trip list: <br /><pre>"
					+ tripsResponse.getStatus() + "</pre><br/>");
		}
		else
		{
			return new ModelAndView("message", "text", "Log in status: " + response.getStatus() + "<br/>Log in value: "
					+ response.getValue() + "<br/>");
		}
		return returnView;
	}

}
