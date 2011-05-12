package placebooks.controller;

import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

// NOTE: This class contains admin controller debug stuff. Put dirty debug stuff
// in here.

@Controller
public class PlaceBooksAdminControllerDebug
{

	private static final Logger log = Logger.getLogger(PlaceBooksAdminControllerDebug.class.getName());

	@RequestMapping(value = "/admin/debug/print_placebooks", method = RequestMethod.GET)
	public ModelAndView printPlaceBooks()
	{

		final EntityManager pm = EMFSingleton.getEntityManager();
		List<PlaceBook> pbs = null;
		try
		{
			final TypedQuery<PlaceBook> query = pm.createQuery("SELECT p FROM PlaceBook p", PlaceBook.class);
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
			mav = new ModelAndView("message", "text", "Error listing PlaceBooks");
		}

		for (final PlaceBook pb : pbs)
		{
			for (final Entry<String, String> e : pb.getMetadata().entrySet())
			{
				log.info("entry: '" + e.getKey() + "' => '" + e.getValue() + "'");
			}
		}

		pm.close();

		return mav;

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
