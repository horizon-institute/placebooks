package placebooks.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PlaceBooksAccountController
{

   	private static final Logger log = 
		Logger.getLogger(PlaceBooksAccountController.class.getName());

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public String adminPage() 
	{
		return "accounts";
    }

}
