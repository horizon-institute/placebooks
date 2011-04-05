package placebooks.controller;

import java.util.*;

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


	@RequestMapping(value = "/admin/debug/print_placebooks", 
					method = RequestMethod.GET)
	@SuppressWarnings("unchecked")	
	public ModelAndView printPlaceBooks()
	{

		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		List<PlaceBook> pbs = null;
		try
		{
			Query query = pm.newQuery(PlaceBook.class);
			pbs = (List<PlaceBook>)query.execute();
			//query.closeAll();
		}
		catch (ClassCastException e)
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

		for (PlaceBook pb : pbs)
		{
			Set s = (Set)pb.getMetadata().entrySet();
			for (Iterator i = s.iterator(); i.hasNext(); )
			{
				Map.Entry e = (Map.Entry)i.next();
				log.info("entry: '" + e.getKey() + "' => '" + e.getValue() 
						 + "'");
			}
		}

		PMFSingleton.get().getPersistenceManager().close();
	
		return mav;

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


