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

import java.io.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

// NOTE: This is currently a testing ground for basic server functionality.

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

		List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();
		try 
		{
			items.add(
				new TextItem(owner, geometry, new URL("http://www.google.com"),
							 "Test text string")
			);
			items.add(new AudioItem(owner, geometry, new URL("http://blah.com"),
								new File("storage/audio.mp3")));

			items.add(new VideoItem(owner, geometry, new URL("http://qwe.com"),
								new File("storage/videofile.mp4")));

			items.add(new ImageItem(owner, geometry, 
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
			String trace = "<gpx version=\"1.0\" creator=\"PlaceBooks 1.0\" 				 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" 				 xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">			<time>			2011-02-14T13:31:10.084Z			</time>			<bounds minlat=\"52.950665120\" minlon=\"-1.183738050\" 					maxlat=\"52.950665120\" maxlon=\"-1.183738050\"/>			<trkseg>				<trkpt lat=\"52.950665120\" lon=\"-1.183738050\">				<ele>0.000000</ele>				<time>				2011-02-14T13:31:10.084Z				</time>				</trkpt>			</trkseg>			</gpx>";

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
			items.add(new GPSTraceItem(owner, geometry, 
					  				   new URL("http://www.blah.com"), gpxDoc));
		}
		catch (java.net.MalformedURLException e)
		{
			log.error(e.toString());
		}



		PlaceBook p = new PlaceBook(owner, geometry, items);

		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		try
		{
			pm.currentTransaction().begin();
			pm.makePersistent(p);
			p.setItemKeys();
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


	// Users of this method must close the PersistenceManager when they are done
	@SuppressWarnings("unchecked")
	private List<PlaceBook> getPlaceBooksQuery(String queryStr)
	{
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
	
		try
		{
			Query query = pm.newQuery(PlaceBook.class, queryStr);
			return (List<PlaceBook>)query.execute();
			//query.closeAll();
		}
		catch (ClassCastException e)
		{
			log.error(e.toString());
		}

		return null;
	}

	@RequestMapping(value = "/admin/getPlaceBooks", method = RequestMethod.GET)
	public ModelAndView getPlaceBooks()
	{

		List<PlaceBook> pbs = getPlaceBooksQuery("owner == 1");
		StringBuffer out = new StringBuffer();
		if (pbs != null)
		{
			out.append(placeBooksToHTMLDebug(pbs));
		}
		else
			out.append("PlaceBook query returned null");

		PMFSingleton.get().getPersistenceManager().close();

		return new ModelAndView("message", "text", out.toString());

    }
	
	private String placeBooksToHTMLDebug(List<PlaceBook> pbs)
	{
		StringBuffer out = new StringBuffer();

		for (PlaceBook pb : pbs)
		{
			out.append("PlaceBook: " + pb.getKey() + ", owner=" 
				+ pb.getOwner() + ", timestamp=" 
				+ pb.getTimestamp().toString() + ", " + pb.getItems().size()
				+ " elements [<a href='package/" + pb.getKey() 
				+ "'>package</a>]<br/>");
			
			for (PlaceBookItem pbi : pb.getItems())
			{

				out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				out.append(pbi.getEntityName());
				out.append(": " + pbi.getKey() + ", owner=" 
						   + pbi.getOwner() + ", timestamp=" 
						   + pbi.getTimestamp().toString());

				out.append("<br/>");
			}

			out.append("<br/>");
		}

		return out.toString();
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
    public ModelAndView makePackage(@PathVariable("key") String key)
	{
		
		List<PlaceBook> pbs = getPlaceBooksQuery("key == '" + key + "'");
		
		if (pbs.size() > 1)
		{
			PMFSingleton.get().getPersistenceManager().close();
			return new ModelAndView("message", "text", 
									"Found too many PlaceBooks for query");
		}

		PlaceBook pb = (PlaceBook)pbs.get(0);

		try 
		{

			DocumentBuilder builder = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document config = builder.newDocument();

			Element root = config.createElement(PlaceBook.class.getName());
			config.appendChild(root);
			root.setAttribute("key", pb.getKey());
			root.setAttribute("owner", Integer.toString(pb.getOwner()));
			
			Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(config.createTextNode(
									pb.getTimestamp().toString()));
			root.appendChild(timestamp);

			Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(
									pb.getGeometry().toText()));
			root.appendChild(geometry);
			
			for (PlaceBookItem pbi : pb.getItems())
			{
				pbi.appendConfiguration(config, root);
			}
 
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(config);

			StringWriter out = new StringWriter();
			StreamResult result =  new StreamResult(out);
			t.transform(source, result);

			PMFSingleton.get().getPersistenceManager().close();

			return new ModelAndView("package", "xmlcontent", 
									out.getBuffer().toString());
		}
		catch (ParserConfigurationException e)
		{
			log.error(e.toString());
		}
		catch (TransformerConfigurationException e) 
		{
            log.error(e.toString());
        }
		catch (TransformerException e) 
		{
            log.error(e.toString());
        }

		PMFSingleton.get().getPersistenceManager().close();
		return new ModelAndView("message", "text", "Error");

	}


	@RequestMapping(value = "/admin/delPlaceBooks", method = RequestMethod.GET)
    public ModelAndView delete() 
	{
			
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();

		try 
		{
			pm.currentTransaction().begin();
			pm.newQuery(PlaceBook.class).deletePersistentAll();
			pm.newQuery(TextItem.class).deletePersistentAll();
			pm.currentTransaction().commit();
		}
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
				log.error("Rolling current delete transaction back");
			}
		}

		pm.close();

		log.info("Deleted all PlaceBooks");

		return new ModelAndView("message", 
								"text", 
								"Deleted all PlaceBooks");

    }


}
