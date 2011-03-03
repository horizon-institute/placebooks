package placebooks.controller;

import org.apache.log4j.*;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Controller
public class PlaceBooksAuthoringController
{

   private static final Logger log = 
		Logger.getLogger(PlaceBooksAuthoringController.class.getName());

	@RequestMapping(value = "/placebooks", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)	
    public String placeBooksGET() 
	{
		log.info("PlaceBooksAuthoringController.placeBooksGET()");
		return "placebooks";
    }

}
