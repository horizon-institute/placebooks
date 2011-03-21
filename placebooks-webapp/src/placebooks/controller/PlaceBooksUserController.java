package placebooks.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlaceBooksUserController
{
	private static final Logger log = Logger.getLogger(PlaceBooksUserController.class.getName());

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String adminPage()
	{
		return "user";
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public String login(@RequestParam("email") final String email, @RequestParam("password") final String password)
	{
		log.info("Log in : " + email);
		return "bleh";
	}

	public String logout()
	{
		return "";
	}
}
