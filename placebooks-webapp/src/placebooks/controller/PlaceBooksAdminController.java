package placebooks.controller;

import placebooks.model.*;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.URL;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

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
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


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

	@RequestMapping(value = "/admin/webbundle/*", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ModelAndView createWebBundle(HttpServletRequest req)
	{
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		
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
							PlaceBook p = 
								(PlaceBook)pm.getObjectById(PlaceBook.class, 
															suffix);
							wbi = new WebBundleItem(null, null, new URL(value),
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

			wbi.setOwner(itemData.getOwner());
			wbi.setGeometry(itemData.getGeometry());

			if (wbi == null || (wbi != null && wbi.getSourceURL() == null))
			{
				pm.close();
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
				pm.close();
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
	
	private void scrape()
	{
		
	}


	// Helper class for passing around general PlaceBookItem data
	private static class ItemData
	{
		private static Geometry geometry;
		private static URL sourceURL;
		private static User owner;

		public ItemData() { }

		public void setOwner(User owner) { this.owner = owner; }
		public User getOwner() { return owner; }
		public void setGeometry(Geometry geometry) { this.geometry = geometry; }
		public Geometry getGeometry() { return geometry; }
		public void setSourceURL(URL sourceURL) { this.sourceURL = sourceURL; }
		public URL getSourceURL() { return sourceURL; }
	}

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


	@RequestMapping(value = "/admin/upload/*", method = RequestMethod.POST)
	public ModelAndView uploadFile(HttpServletRequest req)
	{
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();
		
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
						pm.close();
						return new ModelAndView("message", "text", 
					  			    "Error determining relevant PlaceBook key");
					}

					String prefix = field.substring(0, delim),
						   suffix = field.substring(delim + 1, field.length());

					if (prefix.contentEquals("video"))
						property = PropertiesSingleton.IDEN_VIDEO;
					else if (prefix.contentEquals("audio"))
						property = PropertiesSingleton.IDEN_AUDIO;
					else
					{
						pm.close();
						return new ModelAndView("message", "text", 
								  			    "Unsupported file type");
					}
					
					String path = PropertiesSingleton
									.get(this.getClass().getClassLoader())
									.getProperty(property, "");

					if (!new File(path).exists() && !new File(path).mkdirs())
					{
						pm.close();
						return new ModelAndView("message", "text", 
								  			    "Failed to write file");
					}

	
					File file = null;
										
					PlaceBook p = 
						(PlaceBook)pm.getObjectById(PlaceBook.class, suffix);

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
				pm.close();
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
		
		PlaceBook p = (PlaceBook)PMFSingleton
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

					String files[] = new File(pkgPath).list();

					byte data[] = new byte[2048];
					BufferedInputStream bis = null;
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
			//return new ModelAndView("package", "payload", out);
		}

		PMFSingleton.get().getPersistenceManager().close();
		return new ModelAndView("message", "text", "Error generating package");

	}




	@RequestMapping(value = "/admin/delete/{key}", method = RequestMethod.GET)
    public ModelAndView deletePlaceBook(@PathVariable("key") String key) 
	{

		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();

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


	@RequestMapping(value = "/admin/delete/all", method = RequestMethod.GET)
    public ModelAndView deleteAllPlaceBook() 
	{
			
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();

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

