package placebooks.controller;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;

import java.net.URL;

import java.io.StringWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import placebooks.model.*;
import placebooks.utils.InitializeDatabase;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import uk.me.jstott.jcoord.LatLng;


// NOTE: This class contains admin controller debug stuff. Put dirty debug stuff
// in here that is NOT generic / not for front-end. 
// 
// Or use this class as a testing ground...

@Controller
public class PlaceBooksAdminControllerDebug
{

	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminControllerDebug.class.getName());


	@RequestMapping(value = "/admin/debug/get_tiles", 
					method = RequestMethod.GET)
	public ModelAndView getTiles(final HttpServletRequest req, 
								 final HttpServletResponse res)
	{
		
//		final String poly = 
//			"POLYGON ((56.89869142157009 -5.145721435546875, 56.70770945859063 -5.145721435546875, 56.70770945859063 -4.808921813964844, 56.89869142157009 -4.808921813964844, 56.89869142157009 -5.145721435546875))";
		final String poly = "POLYGON ((52.7295 1.2595, 52.6233 1.2595, 52.6233 1.3823, 52.7295 1.3823, 52.7295 1.2595))";
		
		try
		{
			final File map = TileHelper.getMap(new WKTReader().read(poly));
			
			if (map != null && map.exists())
			{
				ImageInputStream iis = ImageIO.createImageInputStream(map);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
				String fmt = "png";
				while (readers.hasNext()) 
				{
					ImageReader read = readers.next();
					fmt = read.getFormatName();
				}
	
				OutputStream out = res.getOutputStream();
				ImageIO.write(ImageIO.read(map), fmt, out);
				out.close();
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
			return new ModelAndView("message", "text", "Error, " 
									+ e.toString());
		}

		return null;

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
													null);
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
				wbi.setWebBundle(wbi.getWebBundlePath());
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

			final FileItemIterator i = 
				new ServletFileUpload().getItemIterator(req);
			while (i.hasNext())
			{
				final FileItemStream item = i.next();
				if (item.isFormField())
				{
					itemData.processItemData(pm, item.getFieldName(), 
									Streams.asString(item.openStream()));
				}
				else
				{
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
							new InputStreamReader(item.openStream());
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
						pbi = new ImageItem(null, null, null, null);
						p.addItem(pbi);
						pm.getTransaction().commit();
						pm.getTransaction().begin();
						((ImageItem)pbi).setPath(path + "/" + pbi.getKey()
												  + "." + ext);
						file = new File(((ImageItem)pbi).getPath());
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



	// TODO: total hack below.
	@RequestMapping(value = "/admin/everytrail")
	public void getEverytrailData()
	{
		EntityManager entityManager = EMFSingleton.getEntityManager();
		User testUser = UserManager.getCurrentUser(entityManager);
		LoginDetails details = testUser.getLoginDetails("Everytrail");
		
		EverytrailLoginResponse loginResponse = 
			EverytrailHelper.UserLogin(details.getUsername(), 
									   details.getPassword());
		
		EverytrailTripsResponse trips = 
			EverytrailHelper.Trips(loginResponse.getValue());

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
				final NamedNodeMap trackAttr = track.getAttributes();
				log.info("trackAttr = " + trackAttr.toString());
				final NodeList props = track.getChildNodes();
				for (int i = 0; i < props.getLength(); ++ i)
				{
					Node item = props.item(i);
					String itemName = item.getNodeName();
					log.debug("Inspecting property: " + itemName + 
							  " which is " + item.getTextContent());
					if (itemName.equalsIgnoreCase("trk"))
					{
																		
						try
						{
							Transformer t = 
							TransformerFactory.newInstance().newTransformer();
						    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
											    "yes");
							StringWriter s = new StringWriter();
						    t.transform(new DOMSource(item), 
										new StreamResult(s));

							entityManager.getTransaction().begin();
							GPSTraceItem g = 
								new GPSTraceItem(testUser, null, null, 
												 s.toString());
							entityManager.persist(g);
							entityManager.getTransaction().commit();
						}
						catch (final Throwable e)
						{
							log.error(e.getMessage(), e);
						}
						finally
						{
							if (entityManager.getTransaction().isActive())
							{
								entityManager.getTransaction().rollback();
								log.error("Rolling current persist transaction back");
							}
						}
					}
					

				}
			}
		}

		EverytrailPicturesResponse picturesResponse = 	
			EverytrailHelper.Pictures(loginResponse.getValue());
		
		Vector<Node> pictures = picturesResponse.getPictures();
		
		try 
		{
			for (Node picture : pictures)
			{
				entityManager.getTransaction().begin();
				ImageItem imageItem = new ImageItem(testUser, null, null, null);
				entityManager.persist(imageItem);
				entityManager.getTransaction().commit();
				log.error("************** imageItem key = " + imageItem.getKey());
				entityManager.getTransaction().begin();
				ItemFactory.toImageItem(testUser, picture, imageItem);
				entityManager.getTransaction().commit();
			}
		}
		finally
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
			entityManager.close();
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
