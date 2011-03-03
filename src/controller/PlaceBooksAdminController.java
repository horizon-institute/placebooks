package placebooks.controller;

import placebooks.model.*;

import java.util.ArrayList;

import org.apache.log4j.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Controller
public class PlaceBooksAdminController
{

   private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminController.class.getName());

	
		
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPageGET() 
	{
		log.info("PlaceBooksAdminController.adminPageGET()");
		return "admin";
    }

	@RequestMapping(value = "/admin/test", method = RequestMethod.GET)
    public ModelAndView test() 
	{
		PlaceBooksManager pm = new PlaceBooksManager();
		pm.newPlaceBook(1);
		ArrayList<PlaceBook> pbs = pm.getPlaceBooks(1);
		StringBuffer out = new StringBuffer();
		if (pbs != null)
		{
			for (PlaceBook p : pbs)
			{
				out.append(p.getKey() + ", " + p.getOwner() + ", " 
						   + p.getTimestamp().toString() + "<br/>");
			}
		}
				
		return new ModelAndView("message", 
								"text", 
								"Test page returned ok... " + out.toString());

    }
	
	@RequestMapping(value = "/admin/delete", method = RequestMethod.GET)
    public ModelAndView delete() 
	{
		PlaceBooksManager.deleteAllPlaceBooks();
		return new ModelAndView("message", 
								"text", 
								"Deleted all PlaceBooks");

    }


}
