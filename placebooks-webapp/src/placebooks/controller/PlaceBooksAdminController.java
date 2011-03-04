package placebooks.controller;

import placebooks.model.*;

import java.util.*;
import java.io.File;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.jdo.*;

import org.apache.log4j.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.ParseException;

@Controller
public class PlaceBooksAdminController
{

   	private static final Logger log = 
		Logger.getLogger(PlaceBooksAdminController.class.getName());

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage() 
	{
		return "admin";
    }

	@RequestMapping(value = "/admin/newPB", method = RequestMethod.GET)
    public ModelAndView newPlaceBookTest() 
	{
		int owner = 1;
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

		PlaceBook p = new PlaceBook(owner, geometry);
		try 
		{
			p.addItem(
				new TextItem(owner, geometry, new URL("http://www.google.com"),
							 "Test text string")
			);
			p.addItem(new AudioItem(owner, geometry, new URL("http://blah.com"),
								new File("storage/audio.mp3")));

			p.addItem(new VideoItem(owner, geometry, new URL("http://qwe.com"),
								new File("storage/videofile.mp4")));

			p.addItem(new ImageItem(owner, geometry, 
				new URL("http://www.blah.com"), 
				new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR)));

/*			try 
			{
				StringReader reader = new StringReader(trace);
				InputSource source = new InputSource(reader);
				DocumentBuilderFactory factory = 
					DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				this.trace = builder.parse(source);
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


			p.addItem(new GPSTraceItem(owner, geometry, 
				new URL("http://www.blah.com"), gpxDoc));
*/
		

		}
		catch (java.net.MalformedURLException e)
		{
			log.error(e.toString());
		}

		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		try
		{
			pm.currentTransaction().begin();
			pm.makePersistent(p);
			log.info("newPlaceBook key = " + p.getKey());
			p.setItemKeys();
			pm.currentTransaction().commit();
		}
		finally
		{
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			//pm.close();
		}

		return new ModelAndView("message", 
								"text", 
								"New PlaceBook created");

	}
	
	@RequestMapping(value = "/admin/getPlaceBooks", method = RequestMethod.GET)
	public ModelAndView getPlaceBooks()
	{
		ArrayList<PlaceBook> pbs = new ArrayList<PlaceBook>();
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
	
		try
		{
			pm.currentTransaction().begin();
			Query query = pm.newQuery(PlaceBook.class, "owner == 1");
			Collection result = (Collection)query.execute();
			Iterator i = result.iterator();
	        if (!i.hasNext())
        		return null;
			while (i.hasNext())
				pbs.add((PlaceBook)i.next());
			
			pm.currentTransaction().commit();
		}
		finally
		{
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			//pm.close();
		}

		StringBuffer out = new StringBuffer();
		if (pbs != null)
		{
			for (PlaceBook pb : pbs)
			{
				ArrayList<PlaceBookItem> items = pb.getItems();

				out.append("PlaceBook: " + pb.getKey() + ", owner=" 
					+ pb.getOwner() + ", timestamp=" 
					+ pb.getTimestamp().toString() + ", " + items.size() 
					+ " elements<br/>");
				
				for (PlaceBookItem pbi : items)
				{

					out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
					if (pbi instanceof TextItem)
						out.append("TextItem");
					else if (pbi instanceof VideoItem)
						out.append("VideoItem");
					else if (pbi instanceof AudioItem)
						out.append("AudioItem");
					else if (pbi instanceof ImageItem)
						out.append("ImageItem");

					out.append(": " + pbi.getKey() + ", owner=" 
							   + pbi.getOwner() + ", timestamp=" 
							   + pbi.getTimestamp().toString());

					out.append("<br/>");
				}
				out.append("<br/>");
			}
		}
				
		return new ModelAndView("message", 
								"text", 
								out.toString());

    }
	
	@RequestMapping(value = "/admin/delPlaceBooks", method = RequestMethod.GET)
    public ModelAndView delete() 
	{
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		pm.newQuery(PlaceBook.class).deletePersistentAll();
		pm.newQuery(TextItem.class).deletePersistentAll();
		tx.commit();

		log.info("Deleted all PlaceBooks");

		return new ModelAndView("message", 
								"text", 
								"Deleted all PlaceBooks");

    }


}
