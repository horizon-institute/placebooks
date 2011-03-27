package placebooks.controller;

import placebooks.model.*;

import java.io.IOException;
import java.util.List;
import java.io.StringReader;
import java.net.URL;
import java.awt.image.BufferedImage;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

// NOTE: This class contains admin controller debug stuff. Put dirty debug stuff
// in here.

@Controller
public class PlaceBooksAdminControllerDebug
{

	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminControllerDebug.class.getName());


	// Create a new fake PlaceBook for testing
	@RequestMapping(value = "/admin/new/placebook", method = RequestMethod.GET)
    public ModelAndView newPlaceBookTest() 
	{
		
		Geometry geometry = null;
		try 
		{
			geometry = new WKTReader().read(
								"POINT(52.5189367988799 -4.04983520507812)");
		} 
		catch (ParseException e)
		{
			log.error(e.toString());
		}
		
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		User owner = UserManager.getUser(pm, "stuart@tropic.org.uk");
		PlaceBook p = new PlaceBook(owner, geometry);
		try
		{

			try 
			{
				p.addItem(
					new TextItem(owner, geometry, new URL("http://www.google.com"),
								 "Test text string")
				);
				p.addItem(new ImageItem(owner, geometry, 
					new URL("http://www.blah.com"), 
					new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR)));
			}
			catch (java.net.MalformedURLException e)
			{
				log.error(e.toString());
			}
		
			Document gpxDoc = null;
			try 
			{
				// Some example XML
				String trace = "<gpx version=\"1.0\" creator=\"PlaceBooks 1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><time>2011-02-14T13:31:10.084Z</time><bounds minlat=\"52.950665120\" minlon=\"-1.183738050\" maxlat=\"52.950665120\" maxlon=\"-1.183738050\"/><trkseg><trkpt lat=\"52.950665120\" lon=\"-1.183738050\"><ele>0.000000</ele><time>2011-02-14T13:31:10.084Z</time></trkpt></trkseg></gpx>";
	
				StringReader reader = new StringReader(trace);
				InputSource source = new InputSource(reader);
				DocumentBuilder builder = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				gpxDoc = builder.parse(source);
				reader.close();
			} 
			catch (ParserConfigurationException e)
			{
				log.error(e.toString());
			}
			catch (SAXException e)
			{
				log.error(e.toString());
			}
			catch (IOException e)
			{
				log.error(e.toString());
			}
		
	
			try
			{
				p.addItem(new GPSTraceItem(owner, geometry, 
						  				   new URL("http://www.blah.com"), gpxDoc));
			}
			catch (java.net.MalformedURLException e)
			{
				log.error(e.toString());
			}


			pm.currentTransaction().begin();
			pm.makePersistent(p);
			//p.setItemKeys();
			pm.currentTransaction().commit();
		}
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
		}

		pm.close();

		return new ModelAndView("message", 
								"text", 
								"New PlaceBook created");

	}



	@RequestMapping(value = "/admin/print/placebooks", 
					method = RequestMethod.GET)
	@SuppressWarnings("unchecked")	
	public ModelAndView getPlaceBooks()
	{

		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		List<PlaceBook> pbs = null;
		try
		{
			Query query = pm.newQuery(PlaceBook.class, 
									  "owner.email == 'stuart@tropic.org.uk'");
			pbs = (List<PlaceBook>)query.execute();
			//query.closeAll();
		}
		catch (ClassCastException e)
		{
			log.error(e.toString());
		}

		StringBuffer out = new StringBuffer();
		if (pbs != null)
		{
			for (PlaceBook pb : pbs)
			{
				// TODO: sort of breaking MVC here, I'm aware, needs to be fixed
				out.append("<div style='border:2px dashed;padding:5px'><b>PlaceBook: " 
					+ pb.getKey() + ", owner=" 
					+ pb.getOwner().getEmail() + ", timestamp=" 
					+ pb.getTimestamp().toString() + ", " + pb.getItems().size()
					+ " elements</b> [<a href='../package/" 
					+ pb.getKey() 
					+ "'>package</a>] [<a href='../delete/" 
					+ pb.getKey() 
					+ "'>delete</a>]<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload video: <input type='file' name='video."
					+ pb.getKey() 
					+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='http://www.test.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form><form action='../upload/' method='POST' enctype='multipart/form-data'>Upload audio: <input type='file' name='audio."
					+ pb.getKey() 
					+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='http://www.test.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form><form action='../webbundle/' method='POST'>Web scrape: <input type='text' name='url."
					+ pb.getKey() 
					+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Scrape'></form>");
			
				for (PlaceBookItem pbi : pb.getItems())
				{

					out.append("<div style='border:1px dotted;padding:5px'>");
					out.append(pbi.getEntityName());
					out.append(": " + pbi.getKey() + ", owner=" 
							   + pbi.getOwner().getEmail() + ", timestamp=" 
							   + pbi.getTimestamp().toString());

					out.append("</div>");
				}

				out.append("</div><br />");
			}

		}
		else
			out.append("PlaceBook query returned null");

		PMFSingleton.get().getPersistenceManager().close();

		return new ModelAndView("message", "text", out.toString());

    }


	@RequestMapping(value = "/admin/test/everytrail/login", method = RequestMethod.POST)
    public ModelAndView testEverytrailLogin(HttpServletRequest req) 
	{
		log.info("Logging into everytrail as " + req.getParameter("username") + "...");
		EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"), req.getParameter("password"));
		return new ModelAndView("message", "text", "Log in status: " + response.getStatus() + "<br/>Log in value: " + response.getValue() + "<br/>");
	}

	@RequestMapping(value = "/admin/test/everytrail/pictures", method = RequestMethod.POST)
   public ModelAndView testEverytrailPictures(HttpServletRequest req) 
	{
		ModelAndView returnView;
		
		EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"), req.getParameter("password"));
		log.debug("logged in");
		if(response.getStatus().equals("success"))
		{
			EverytrailPicturesResponse picturesResponse = EverytrailHelper.Pictures(response.getValue());
			log.debug(picturesResponse.getStatus());
			returnView = new ModelAndView("message", "text", "Logged in and got picutre list: <br /><pre>" + picturesResponse.getStatus() + "</pre><br/>");
		}
		else
		{
			return new ModelAndView("message", "text", "Log in status: " + response.getStatus() + "<br />Log in value: " + response.getValue() + "<br/>");
		}
		return returnView;
	}

	@RequestMapping(value = "/admin/test/everytrail/trips", method = RequestMethod.POST)
   public ModelAndView testEverytrailTrips(HttpServletRequest req) 
	{
		ModelAndView returnView;
		
		EverytrailLoginResponse response = EverytrailHelper.UserLogin(req.getParameter("username"), req.getParameter("password"));
		log.debug("logged in");
		if(response.getStatus().equals("success"))
		{
			EverytrailTripsResponse tripsResponse = EverytrailHelper.Trips(response.getValue());
			log.debug(tripsResponse.getStatus());
			returnView = new ModelAndView("message", "text", "Logged in and got trip list: <br /><pre>" + tripsResponse.getStatus() + "</pre><br/>");
		}
		else
		{
			return new ModelAndView("message", "text", "Log in status: " + response.getStatus() + "<br/>Log in value: " + response.getValue() + "<br/>");
		}
		return returnView;
	}
	


}


