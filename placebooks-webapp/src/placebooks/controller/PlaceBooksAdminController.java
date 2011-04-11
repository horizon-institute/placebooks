package placebooks.controller;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.PersistenceManager;
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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.model.AudioItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.User;
import placebooks.model.VideoItem;

import com.vividsolutions.jts.geom.Geometry;


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


	@RequestMapping(value = "/admin/upload/*", method = RequestMethod.POST)
	public ModelAndView uploadFile(HttpServletRequest req)
	{

		// TODO: set these as vars to pass in to method
		PersistenceManager pm = PMFSingleton.get().getPersistenceManager();		
		User owner = UserManager.getUser(pm, "stuart@tropic.org.uk");
		Geometry geom = null;
		URL url = null;


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

					File file = null;
										
					pm.currentTransaction().begin();
					PlaceBook p = pm.getObjectById(PlaceBook.class, suffix);

					int extIdx = item.getName().lastIndexOf(".");
					String ext = 
						item.getName().substring(extIdx + 1, 
											     item.getName().length());

					if (property.equals(PropertiesSingleton.IDEN_VIDEO))
					{
						VideoItem v = new VideoItem(owner, geom, url, 
													new File(""));
						p.addItem(v);
						v.setVideo(path + "/" + v.getKey() + "." + ext);
						
						file = new File(v.getVideo());
					}
					else if (property.equals(
								PropertiesSingleton.IDEN_AUDIO))
					{
						AudioItem a = new AudioItem(owner, geom, url, 
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
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
				log.error(
					"Rolling current persist transaction back");
			}
		}

		return new ModelAndView("message", "text", 
								"Done");
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

