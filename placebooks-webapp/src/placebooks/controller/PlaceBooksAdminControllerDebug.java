package placebooks.controller;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import placebooks.model.EverytrailLoginResponse;
import placebooks.model.EverytrailPicturesResponse;
import placebooks.model.EverytrailTripsResponse;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.User;

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
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		pm.currentTransaction().begin();

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
		
		User owner = UserManager.getUser(pm, "stuart@tropic.org.uk");

		PlaceBook p = new PlaceBook(owner, geometry);

		try
		{
			pm.makePersistent(p);
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
				out.append(
				"<div style='border:2px dashed;padding:5px'><b>PlaceBook: " 
				+ pb.getKey() + ", owner=" 
				+ pb.getOwner().getEmail() + ", timestamp=" 
				+ pb.getTimestamp().toString() + ", " 
				+ pb.getGeometry().toString() + ", " + pb.getItems().size()
				+ " elements</b> [<a href='../package/" + pb.getKey() 
				+ "'>package</a>] [<a href='../delete/" + pb.getKey() 
				+ "'>delete</a>] [<a href='../placebooks/" + pb.getKey() 
				+ "'>json</a>]" 
				+ "<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload video: <input type='file' name='video."
				+ pb.getKey() 
				+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='http://www.test.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form>"
				+ "<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload audio: <input type='file' name='audio."
				+ pb.getKey() 
				+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='http://www.test.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form>"
				+ "<form action='../webbundle/' method='POST'>Web scrape: <input type='text' name='url."
				+ pb.getKey() 
				+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Scrape'></form>"
				+ "<form action='../text/' method='POST'>Text: <input type='text' name='text."
				+ pb.getKey() 
				+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form>"
				+ "<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload image: <input type='file' name='image."
				+ pb.getKey() 
				+ "'><input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'><input type='hidden' value='http://www.test.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form>"
				+ "<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload GPS trace: <input type='file' name='gpstrace."
				+ pb.getKey() 
				+ "'><input type='hidden' value='http://www.everytrail.com' name='sourceurl'><input type='hidden' value='stuart@tropic.org.uk' name='owner'><input type='submit' value='Upload'></form>"
				);

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

//		ModelAndView mav = new ModelAndView("placebooks", "pbs", out.toString());

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


