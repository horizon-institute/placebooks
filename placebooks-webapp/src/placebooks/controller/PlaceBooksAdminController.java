package placebooks.controller;

import placebooks.model.*;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.URL;
import java.awt.image.BufferedImage;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


// NOTE: This is currently a testing ground for basic server functionality.

// TODO: general todo is to do file checking to reduce unnecessary file writes, 
// part of which is ensuring new file writes in cases of changes

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

	@RequestMapping(value = "/account", method = RequestMethod.GET)
    public String accountPage() 
	{
		return "account";
    }
	
	@RequestMapping(value = "/admin/new/placebook", method = RequestMethod.GET)
    public ModelAndView newPlaceBookTest() 
	{
		User owner = UserManager.getCurrentUser();
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

		//List<PlaceBookItem> items = new ArrayList<PlaceBookItem>();
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
			p.addItem(new GPSTraceItem(owner, geometry, 
					  				   new URL("http://www.blah.com"), gpxDoc));
		}
		catch (java.net.MalformedURLException e)
		{
			log.error(e.toString());
		}

		PersistenceManager pm = PMFSingleton.getPersistenceManager();
		try
		{
			pm.currentTransaction().begin();
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


	/** 
	 * Users of this method must close the PersistenceManager when they are 
	 * done.
	 */
	@SuppressWarnings("unchecked")
	private List<PlaceBook> getPlaceBooksQuery(String queryStr)
	{
		PersistenceManager pm = PMFSingleton.getPersistenceManager();
	
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

	@RequestMapping(value = "/admin/print/placebooks", 
					method = RequestMethod.GET)
	public ModelAndView getPlaceBooks()
	{

		List<PlaceBook> pbs = getPlaceBooksQuery("owner.email == 'stuart@tropic.org.uk'");
		StringBuffer out = new StringBuffer();
		if (pbs != null)
		{
			out.append(placeBooksToHTMLDebug(pbs));
		}
		else
			out.append("PlaceBook query returned null");

		PMFSingleton.getPersistenceManager().close();

		return new ModelAndView("message", "text", out.toString());

    }

	private String placeBooksToHTMLDebug(List<PlaceBook> pbs)
	{
		StringBuffer out = new StringBuffer();

		for (PlaceBook pb : pbs)
		{
			out.append("PlaceBook: " + pb.getKey() + ", owner=" 
				+ pb.getOwner().getEmail() + ", timestamp=" 
				+ pb.getTimestamp().toString() + ", " + pb.getItems().size()
				+ " elements [<a href='../package/" 
				+ pb.getKey() 
				+ "'>package</a>] [<a href='../delete/" 
				+ pb.getKey() 
				+ "'>delete</a>]<form action='../upload/' method='POST' enctype='multipart/form-data'>Upload video: <input type='file' name='video."
				+ pb.getKey() 
				+ "'><input type='submit' value='Upload'></form><form action='../upload/' method='POST' enctype='multipart/form-data'>Upload audio: <input type='file' name='audio."
				+ pb.getKey() 
				+ "'><input type='submit' value='Upload'></form><br/>");
			
			for (PlaceBookItem pbi : pb.getItems())
			{

				out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				out.append(pbi.getEntityName());
				out.append(": " + pbi.getKey() + ", owner=" 
						   + pbi.getOwner().getEmail() + ", timestamp=" 
						   + pbi.getTimestamp().toString());

				out.append("<br/>");
			}

			out.append("<br/>");
		}

		return out.toString();
	}

	@RequestMapping(value = "/admin/upload/*", method = RequestMethod.POST)
	public ModelAndView uploadFile(HttpServletRequest req)
	{
		try
		{
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator i = upload.getItemIterator(req);
			while (i.hasNext())
        	{
				FileItemStream item = i.next();
				if (!item.isFormField())
				{
					String property = null;
					String field = item.getFieldName();
					int delim = field.indexOf(".");
					if (delim == -1)
					{
						return new ModelAndView("message", "text", 
					  			    "Error determining relevant PlaceBook key");
					}

					String prefix = field.substring(0, delim),
						   suffix = field.substring(delim + 1, field.length());

					log.info("prefix, suffix: " + prefix + "," + suffix);
					if (prefix.contentEquals("video"))
						property = PropertiesSingleton.IDEN_VIDEO;
					else if (prefix.contentEquals("audio"))
						property = PropertiesSingleton.IDEN_AUDIO;
					else
					{
						return new ModelAndView("message", "text", 
								  			    "Unsupported file type");
					}
					
					String path = PropertiesSingleton
									.get(this.getClass().getClassLoader())
									.getProperty(property, "");

					if (!new File(path).exists() && !new File(path).mkdirs())
					{
						return new ModelAndView("message", "text", 
								  			    "Failed to write file");
					}

					PersistenceManager pm = 
							PMFSingleton.getPersistenceManager();

					try 
					{
						File file = null;
						User user = 
							UserManager.getUser("stuart@tropic.org.uk");
						
						pm.currentTransaction().begin();
						PlaceBook p = pm.getObjectById(PlaceBook.class, suffix);

						int extIdx = item.getName().lastIndexOf(".");
						String ext = 
							item.getName().substring(extIdx + 1, 
												     item.getName().length());

						if (property.equals(PropertiesSingleton.IDEN_VIDEO))
						{
							VideoItem v = new VideoItem(user, null, null, 
														new File(""));
							p.addItem(v);
							v.setVideo(path + "/" + v.getKey() + "." + ext);
							
							file = new File(v.getVideo());
						}
						else if (property.equals(
									PropertiesSingleton.IDEN_AUDIO))
						{
							AudioItem a = new AudioItem(user, null, null, 
														new File(""));
							p.addItem(a);
							a.setAudio(path + "/" + a.getKey() + "." + ext);
				
							file = new File(a.getAudio());
						}
						
						pm.currentTransaction().commit();

						InputStream input = item.openStream();
						OutputStream output = new FileOutputStream(file);
						int byte_;
						while ((byte_ = input.read()) != -1)
							output.write(byte_);
        	   			output.close();
						input.close();

						log.info("Wrote " + prefix + " file " 
								 + file.getAbsolutePath());

					}
					finally
					{
						if (pm.currentTransaction().isActive())
						{
							pm.currentTransaction().rollback();
							log.error(
								"Rolling current persist transaction back");
						}
					}

					pm.close();
						
				}
			}
	    }
        catch (FileUploadException e) 
		{
            log.error(e.toString());
        }
        catch (IOException e) 
		{
            log.error(e.toString());
        }

		return new ModelAndView("message", "text", 
								"Done");
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
    public ModelAndView makePackage(@PathVariable("key") String key)
	{
		
		PlaceBook p = PMFSingleton.getPersistenceManager().getObjectById(PlaceBook.class, key);

		String out = placeBookToXML(p);
		
		if (out != null)
		{
			String pkgPath = 
				PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_PKG, "") 
					+ p.getKey();
			if (new File(pkgPath).exists() || new File(pkgPath).mkdirs())
			{
				try
				{
					FileWriter fw = 
						new FileWriter(new File(pkgPath + "/" + 
							PropertiesSingleton
								.get(this.getClass().getClassLoader())
								.getProperty(PropertiesSingleton.IDEN_CONFIG, 
											 "")
						));

					fw.write(out);
					fw.close();
				}
				catch (IOException e)
				{
					log.error(e.toString());
				}
			}

			PMFSingleton.getPersistenceManager().close();

			// Compress package path and serve it
			try 
			{
				String pkgZPath = 
					PropertiesSingleton
						.get(this.getClass().getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

				if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
				{

					FileOutputStream fos = 
						new FileOutputStream(pkgZPath + p.getKey() + ".zip");

					ZipOutputStream zos = 
						new ZipOutputStream(new BufferedOutputStream(fos));
					zos.setMethod(ZipOutputStream.DEFLATED);

					String files[] = new File(pkgPath).list();
					BufferedInputStream bis = null;

					byte data[] = new byte[2048];
					for (int i = 0; i < files.length; ++i)
					{
						File entry = new File(pkgPath + "/" + files[i]);
						log.info("Adding file to archive: " + entry.getPath());
						FileInputStream fis = new FileInputStream(entry);
						bis = new BufferedInputStream(fis, 2048);
						zos.putNextEntry(new ZipEntry(entry.getPath()));

						int j;
		            	while((j = bis.read(data, 0, 2048)) != -1)
        					zos.write(data, 0, j);
            			bis.close();
         			}
				
					zos.close();
				}

			} 
			catch(IOException e) 
			{
        		log.error(e.toString());
			}
			
			return new ModelAndView("package", "payload", out);
		}

		PMFSingleton.getPersistenceManager().close();
		return new ModelAndView("message", "text", "Error generating package");

	}


	private static String placeBookToXML(PlaceBook p)
	{
		StringWriter out = null;
		
		try 
		{

			DocumentBuilder builder = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document config = builder.newDocument();

			Element root = config.createElement(PlaceBook.class.getName());
			config.appendChild(root);
			root.setAttribute("key", p.getKey());
			root.setAttribute("owner", p.getOwner().getEmail());
			
			Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(config.createTextNode(
									p.getTimestamp().toString()));
			root.appendChild(timestamp);

			Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(
									p.getGeometry().toText()));
			root.appendChild(geometry);
			
			// Note: ImageItem, VideoItem and AudioItem write their data to a 
			// package directly as well as creating XML configuration
			for (PlaceBookItem item : p.getItems())
				item.appendConfiguration(config, root);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(config);

			out = new StringWriter();
			StreamResult result =  new StreamResult(out);
			t.transform(source, result);
		
			return out.getBuffer().toString();
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
	
		return null;
	}


	@RequestMapping(value = "/admin/delete/{key}", method = RequestMethod.GET)
    public ModelAndView deletePlaceBook(@PathVariable("key") String key) 
	{

		PersistenceManager pm = PMFSingleton.getPersistenceManager();

		try 
		{
			pm.currentTransaction().begin();
			pm.newQuery(PlaceBook.class, 
						"key == '" + key + "'").deletePersistentAll();
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
								"Deleted PlaceBook: " + key);



	}


	@RequestMapping(value = "/admin/delete/all", method = RequestMethod.GET)
    public ModelAndView deleteAllPlaceBook() 
	{
			
		PersistenceManager pm = PMFSingleton.getPersistenceManager();

		try 
		{
			pm.currentTransaction().begin();
			pm.newQuery(PlaceBook.class).deletePersistentAll();
			pm.newQuery(PlaceBookItem.class).deletePersistentAll();
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
