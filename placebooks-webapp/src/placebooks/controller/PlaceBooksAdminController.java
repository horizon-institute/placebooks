package placebooks.controller;

import placebooks.model.*;
import placebooks.model.json.*;

import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.commons.fileupload.util.Streams;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import placebooks.model.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


// TODO: general todo is to do file checking to reduce unnecessary file writes, 
// part of which is ensuring new file writes in cases of changes
// 
// TODO: stop orphan / null field elements being added to database


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

	@RequestMapping(value = "/createUserAccount", method = RequestMethod.POST)
	public String createUserAccount(@RequestParam final String name, @RequestParam final String email,
			@RequestParam final String password)
	{
		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		final User user = new User(name, email, encoder.encodePassword(password, null));

		final PersistenceManager manager = PMFSingleton.getPersistenceManager();
		try
		{
			manager.currentTransaction().begin();
			manager.makePersistent(user);
			manager.currentTransaction().commit();	
		}
		catch(Exception e)
		{
			log.error("Error creating user", e);			
		}
		finally
		{
			if (manager.currentTransaction().isActive())
			{
				manager.currentTransaction().rollback();
				log.error("Rolling back user creation");
			}
			manager.close();
		}

		return "redirect:/login.html";
	}
	
	// TODO: currently uses startsWith for string search. Is this right??
	// owner.key query on key value for User works without startsWith
	@RequestMapping(value = "/admin/shelf/{owner}", 
					method = RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public ModelAndView getPlaceBooksJSON(HttpServletRequest req, 
										  HttpServletResponse res, 
										  @PathVariable("owner") String owner)
	{
		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();
		final Query q = pm.newQuery(PlaceBook.class);
		q.setFilter("owner.email.startsWith('" + owner + "')");
		Collection<PlaceBook> pbs = (Collection<PlaceBook>)q.execute();
		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		if (!pbs.isEmpty())
		{
			Shelf s = new Shelf(pbs);
			try
			{
				ObjectMapper mapper = new ObjectMapper();
				ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/json");
				mapper.writeValue(sos, s);
				sos.flush();
			}
			catch (IOException e)
			{
				log.error(e.toString());
			}
		}

		pm.close();

		return null;
	}

	@RequestMapping(value = "/admin/add_placebook", method = RequestMethod.POST)
	public ModelAndView addPlaceBook(@RequestParam String owner,
									 @RequestParam String geometry)
	{
		if (owner != null)
		{
			Geometry geometry_ = null;
			try 
			{
				geometry_ = new WKTReader().read(geometry);
			} 
			catch (ParseException e)
			{
				log.error(e.toString());
			}
			
			// If created inside getting the PersistenceManager, some fields are
			// null. Not sure why... TODO
			PlaceBook p = new PlaceBook(null, geometry_);

			final PersistenceManager pm = 
				PMFSingleton.get().getPersistenceManager();

			User owner_ = UserManager.getUser(pm, owner);

			if (owner_ == null)
			{
				return new ModelAndView("message", "text", 
										"User does not exist");
			}
	
			p.setOwner(owner_);		

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

			return new ModelAndView("message", "text", "PlaceBook added");
		}
		else
			return new ModelAndView("message", "text", "Error in POST");
	}


	@RequestMapping(value = "/admin/add_metadata", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView addMetadata(@RequestParam String key, 
									@RequestParam String mKey, 
									@RequestParam String mValue)
	{		
		if (key != null && mKey != null && mValue != null)
		{
			final PersistenceManager pm = 
				PMFSingleton.get().getPersistenceManager();
			
			try
			{	
				pm.currentTransaction().begin();
				PlaceBook p = pm.getObjectById(PlaceBook.class, key);
				p.addMetadataEntry(mKey, mValue);
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

			return new ModelAndView("message", "text", "Metadata added");
		}
		else
			return new ModelAndView("message", "text", "Error in POST");
	}


	@RequestMapping(value = "/admin/add_item/text", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView uploadText(HttpServletRequest req)
	{
		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();
		
		ItemData itemData = new ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.currentTransaction().begin();

			for (Enumeration<String> params = req.getParameterNames(); 
				 params.hasMoreElements(); )
			{
				String param = params.nextElement();
				String value = req.getParameterValues(param)[0];
				if (!processItemData(itemData, pm, param, value))
				{
					int delim = param.indexOf(".");
					if (delim == -1)
					{
						return new ModelAndView("message", "text", 
												"Error");
					}

					String prefix = param.substring(0, delim),
						   suffix = param.substring(delim + 1, param.length());

					PlaceBook p = pm.getObjectById(PlaceBook.class, suffix);

					if (prefix.contentEquals("text"))
					{
						String value_ = null;
						if (value.length() > 0)
							value_ = value;
						pbi = new TextItem(null, null, null, value_);
						p.addItem(pbi);			
					}
				}

			}
		
			if ((pbi != null && ((TextItem)pbi).getText() == null) || 
				pbi == null || itemData.getOwner() == null)
			{
				return new ModelAndView("message", "text", 
										"Error setting data elements");
			}

			pbi.setOwner(itemData.getOwner());
			pbi.setGeometry(itemData.getGeometry());
			pbi.setSourceURL(itemData.getSourceURL());

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

		return new ModelAndView("message", "text", "TextItem added");
	}


	@RequestMapping(value = "/admin/add_item/webbundle", 
					method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView createWebBundle(HttpServletRequest req)
	{
		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();
		
		ItemData itemData = new ItemData();
		WebBundleItem wbi = null;

		try
		{
			pm.currentTransaction().begin();

			for (Enumeration<String> params = req.getParameterNames(); 
				 params.hasMoreElements(); )
			{
				String param = params.nextElement();
				String value = req.getParameterValues(param)[0];
				if (!processItemData(itemData, pm, param, value))
				{
					int delim = param.indexOf(".");
					if (delim == -1)
					{
						pm.close();
						return new ModelAndView("message", "text", 
												"Error");
					}

					String prefix = param.substring(0, delim),
						   suffix = param.substring(delim + 1, param.length());

					if (prefix.contentEquals("url"))
					{
						try
						{
							PlaceBook p = pm.getObjectById(PlaceBook.class, 
															suffix);
							URL sourceURL = null;
							if (value.length() > 0)
								sourceURL = new URL(value);
							wbi = new WebBundleItem(null, null, sourceURL,
													new File(""));
							p.addItem(wbi);
						}
						catch (java.net.MalformedURLException e)
						{
							log.error(e.toString());
						}
					}
				}

			}
			
			if (wbi != null)
			{
				wbi.setOwner(itemData.getOwner());
				wbi.setGeometry(itemData.getGeometry());
			}
	
			if (wbi == null || (wbi != null && (wbi.getSourceURL() == null || 
												wbi.getOwner() == null)))
			{
				return new ModelAndView("message", "text", 
										"Error setting data elements");
			}
			
			StringBuffer wgetCmd = new StringBuffer();
			wgetCmd.append(
				PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_WGET, "")
			);

			if (wgetCmd.equals(""))
			{
				return new ModelAndView("message", "text", 
										"Error in wget command");
			}

			wgetCmd.append(" --user-agent=\"");
			wgetCmd.append(
				PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_USER_AGENT, "")
			);
			wgetCmd.append("\" ");

			String webBundlePath = 
				PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_WEBBUNDLE, "") 
				+ wbi.getKey();

			wgetCmd.append("-P " + webBundlePath + " " 
						   + wbi.getSourceURL().toString());

			log.info("wgetCmd=" + wgetCmd.toString());

			if (new File(webBundlePath).exists() || 
				new File(webBundlePath).mkdirs())
			{
				try
				{
					Process p = Runtime.getRuntime().exec(wgetCmd.toString());

					BufferedReader stderr = 
						new BufferedReader(
							new InputStreamReader(p.getErrorStream()));
					
					String line = "";
					while ((line = stderr.readLine()) != null)
						log.error("[wget output] " + line);
					log.info("Waiting for process...");
					try
					{
						p.waitFor();
					}
					catch (InterruptedException e)
					{
						log.error(e.toString());
					}
					log.info("... Process ended");

					String urlStr = wbi.getSourceURL().toString();
					int protocol = urlStr.indexOf("://");
					wbi.setWebBundle(
						webBundlePath + "/" 
						+ urlStr.substring(protocol + 3, urlStr.length())
					);
					log.info("wbi.getWebBundle() = " + wbi.getWebBundle());

				}
				catch (IOException e)
				{
					log.error(e.toString());
				}
			}


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

		return new ModelAndView("message", "text", "Scraped");
	}
	
	@RequestMapping(value = "/admin/add_item/upload", 
					method = RequestMethod.POST)
	public ModelAndView uploadFile(HttpServletRequest req)
	{
		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();
		
		ItemData itemData = new ItemData();
		PlaceBookItem pbi = null;

		try
		{
			pm.currentTransaction().begin();

			FileItemIterator i = new ServletFileUpload().getItemIterator(req);
			while (i.hasNext())
        	{
				FileItemStream item = i.next();
				if (item.isFormField())
				{
					processItemData(itemData, pm, item.getFieldName(), 
									Streams.asString(item.openStream()));
				}
				else
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

					PlaceBook p = pm.getObjectById(PlaceBook.class, suffix);

					if (prefix.contentEquals("image"))
					{
						pbi = new ImageItem(null, null, null, null);
						p.addItem(pbi);
						
						InputStream input = item.openStream();
						BufferedImage b = ImageIO.read(input);
						input.close();
						((ImageItem)pbi).setImage(b);
						
						continue;
					}
					else if (prefix.contentEquals("gpstrace"))
					{
						Document gpxDoc = null;
						//StringReader reader = new StringReader(value);
						InputStream reader = item.openStream();
						InputSource source = new InputSource(reader);
						DocumentBuilder builder = DocumentBuilderFactory
													.newInstance()
													.newDocumentBuilder();
						gpxDoc = builder.parse(source);
						reader.close();
						pbi = new GPSTraceItem(null, null, null, gpxDoc);
						p.addItem(pbi);

						continue;
					}
					else if (prefix.contentEquals("video"))
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

					File file = null;
										
					int extIdx = item.getName().lastIndexOf(".");
					String ext = 
						item.getName().substring(extIdx + 1, 
												 item.getName().length());

					if (property.equals(PropertiesSingleton.IDEN_VIDEO))
					{
						pbi = new VideoItem(null, null, null, new File(""));
						p.addItem(pbi);
						((VideoItem)pbi).setVideo(path + "/" + pbi.getKey() 
												  + "." + ext);
						
						file = new File(((VideoItem)pbi).getVideo());
					}
					else if (property.equals(
								PropertiesSingleton.IDEN_AUDIO))
					{
						pbi = new AudioItem(null, null, null, new File(""));
						p.addItem(pbi);
						((AudioItem)pbi).setAudio(path + "/" + pbi.getKey() 
												  + "." + ext);
			
						file = new File(((AudioItem)pbi).getAudio());
					}
					
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

			}

			if (pbi == null || itemData.getOwner() == null)
			{
				return new ModelAndView("message", "text", 
										"Error setting data elements");
			}


			pbi.setOwner(itemData.getOwner());
			pbi.setSourceURL(itemData.getSourceURL());
			pbi.setGeometry(itemData.getGeometry());

			pm.currentTransaction().commit();
	    }
        catch (FileUploadException e) 
		{
            log.error(e.toString());
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
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
				log.error("Rolling current persist transaction back");
			}
		}

		pm.close();

		return new ModelAndView("message", "text", "Done");
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
    public ModelAndView makePackage(HttpServletRequest req, 
									HttpServletResponse res, 
									@PathVariable("key") String key)
	{
		
		PlaceBook p = PMFSingleton
							.get()
							.getPersistenceManager()
							.getObjectById(PlaceBook.class, key);

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

			PMFSingleton.get().getPersistenceManager().close();

			try 
			{
				String pkgZPath = 
					PropertiesSingleton
						.get(this.getClass().getClassLoader())
						.getProperty(PropertiesSingleton.IDEN_PKG_Z, "");

				File zipFile = new File(pkgZPath + p.getKey() + ".zip");

				// Compress package path
				if (new File(pkgZPath).exists() || new File(pkgZPath).mkdirs())
				{

					ZipOutputStream zos = 
						new ZipOutputStream(
							new BufferedOutputStream(
								new FileOutputStream(zipFile))
						);
					zos.setMethod(ZipOutputStream.DEFLATED);

					ArrayList<File> files = new ArrayList<File>();
					getFileListRecursive(new File(pkgPath), files);


					byte data[] = new byte[2048];
					BufferedInputStream bis = null;
					for (File file : files)
					{
						log.info("Adding file to archive: " + file.getPath());
						FileInputStream fis = new FileInputStream(file);
						bis = new BufferedInputStream(fis, 2048);
						zos.putNextEntry(new ZipEntry(file.getPath()));

						int j;
		            	while((j = bis.read(data, 0, 2048)) != -1)
        					zos.write(data, 0, j);
            			bis.close();
         			}
				
					zos.close();
				}

				// Serve up file from disk
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				FileInputStream fis = new FileInputStream(zipFile);
				BufferedInputStream bis = new BufferedInputStream(fis);

				byte data[] = new byte[2048];
				int i;
				while ((i = bis.read(data, 0, 2048)) != -1)
					bos.write(data, 0, i);
				fis.close();
				
				ServletOutputStream sos = res.getOutputStream();
				res.setContentType("application/zip");
    	        res.setHeader("Content-Disposition", "attachment; filename=\"" 
							  + p.getKey() + ".zip\"");
				sos.write(bos.toByteArray());
				sos.flush();

			} 
			catch(IOException e) 
			{
        		log.error(e.toString());
			}
			
			return null;
		}
		else
			PMFSingleton.get().getPersistenceManager().close();

		return new ModelAndView("message", "text", "Error generating package");

	}

	@RequestMapping(value = "/admin/delete/{key}", method = RequestMethod.GET)
    public ModelAndView deletePlaceBook(@PathVariable("key") String key) 
	{

		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();

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
				log.error("Rolling current delete single transaction back");
			}
		}

		pm.close();

		log.info("Deleted all PlaceBooks");

		return new ModelAndView("message", 
								"text", 
								"Deleted PlaceBook: " + key);



	}

	@RequestMapping(value = "/admin/delete/all_placebooks", 
					method = RequestMethod.GET)
    public ModelAndView deleteAllPlaceBook() 
	{
			
		final PersistenceManager pm = 
			PMFSingleton.get().getPersistenceManager();

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
				log.error("Rolling current delete all transaction back");
			}
		}

		pm.close();

		log.info("Deleted all PlaceBooks");

		return new ModelAndView("message", 
								"text", 
								"Deleted all PlaceBooks");

    }

	
	// Helper methods below

	private static void getFileListRecursive(File path, ArrayList<File> out)
	{
		ArrayList<File> files = 
			new ArrayList<File>(Arrays.asList(path.listFiles()));

		for (File file : files)
		{
			if (file.isDirectory())
				getFileListRecursive(file, out);
			else
				out.add(file);
		}
	}

	// Helper class for passing around general PlaceBookItem data
	private static class ItemData
	{
		private Geometry geometry;
		private URL sourceURL;
		private User owner;

		public ItemData() { }

		public void setOwner(User owner) { this.owner = owner; }
		public User getOwner() { return owner; }
		public void setGeometry(Geometry geometry) { this.geometry = geometry; }
		public Geometry getGeometry() { return geometry; }
		public void setSourceURL(URL sourceURL) { this.sourceURL = sourceURL; }
		public URL getSourceURL() { return sourceURL; }
	}

	// Assumes currently open PersistenceManager
	private static boolean processItemData(ItemData i, PersistenceManager pm,  
										   String field, String value)
	{
		if (field.equals("owner"))
		{
			i.setOwner(UserManager.getUser(pm, value));
		}
		else if (field.equals("sourceurl"))
		{
			try
			{
				i.setSourceURL(new URL(value));
			}
			catch (java.net.MalformedURLException e)
			{
				log.error(e.toString());
			}
		}
		else if (field.equals("geometry"))
		{
			try
			{
				i.setGeometry(new WKTReader().read(value));
			}
			catch (ParseException e)
			{
				log.error(e.toString());
			}
		}
		else
			return false;
		return true;
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
			root.setAttribute("owner", p.getOwner().getKey());
			
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

}

