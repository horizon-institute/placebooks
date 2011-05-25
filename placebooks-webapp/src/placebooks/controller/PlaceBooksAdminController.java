package placebooks.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import placebooks.model.AudioItem;
import placebooks.model.GPSTraceItem;
import placebooks.model.ImageItem;
import placebooks.model.LoginDetails;
import placebooks.model.MediaItem;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;
import placebooks.model.VideoItem;
import placebooks.model.WebBundleItem;
import placebooks.model.json.Shelf;

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

	@RequestMapping(value = "/palette", method = RequestMethod.GET)
	public void getPaletteItemsJSON(final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);
		final TypedQuery<PlaceBookItem> q = manager
				.createQuery("SELECT p FROM PlaceBookItem p WHERE p.owner = :owner AND p.placebook IS NULL",
							PlaceBookItem.class);
		q.setParameter("owner", user);

		final Collection<PlaceBookItem> pbs = q.getResultList();
		log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
		log.info("User " + user.getName());
		try
		{
			final ServletOutputStream sos = res.getOutputStream();
			final ObjectMapper mapper = new ObjectMapper();			
			mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			sos.print("[");
			boolean comma = false;
			for(PlaceBookItem item: pbs)
			{
				if(comma)
				{
					sos.print(",");
				}
				else
				{
					comma = true;
				}
				sos.print(mapper.writeValueAsString(item));
			}
			sos.print("]");

			//mapper.enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT);		

			res.setContentType("application/json");
			log.info("Palette Items: " + mapper.writeValueAsString(pbs));			
			sos.flush();
			sos.close();
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}

		manager.close();
	}

	@RequestMapping(value = "/placebook/{key}", method = RequestMethod.GET)
	public void getPlaceBookJSON(final HttpServletResponse res, @PathVariable("key") final String key)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final PlaceBook placebook = manager.find(PlaceBook.class, key);
			if (placebook != null)
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);					
					final ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/json");
					mapper.writeValue(sos, placebook);
					log.info("Placebook: " + mapper.writeValueAsString(placebook));
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			manager.close();
		}
	}

	@RequestMapping(value = "/shelf", method = RequestMethod.GET)
	public void getPlaceBooksJSON(final HttpServletRequest req, final HttpServletResponse res)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(manager);
			if (user != null)
			{
				final TypedQuery<PlaceBook> q = manager.createQuery("SELECT p FROM PlaceBook p WHERE p.owner= :owner",
																	PlaceBook.class);
				q.setParameter("owner", user);

				final Collection<PlaceBook> pbs = q.getResultList();
				log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
				log.info("User " + user.getName());
				try
				{
					final Shelf shelf = new Shelf(user, pbs);
					final ObjectMapper mapper = new ObjectMapper();
					log.info("Shelf: " + mapper.writeValueAsString(shelf));
					final ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/json");
					mapper.writeValue(sos, shelf);
					sos.flush();
				}
				catch (final Exception e)
				{
					log.error(e.toString());
				}
			}
			else
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					mapper.writeValue(sos, req.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
							.toString());
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
		finally
		{
			manager.close();
		}
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

		final EntityManager manager = EMFSingleton.getEntityManager();
		try
		{
			manager.getTransaction().begin();
			manager.persist(user);
			manager.getTransaction().commit();
		}
		catch (final Exception e)
		{
			log.error("Error creating user", e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling back user creation");
			}
			manager.close();
		}

		return "redirect:/login.html";
	}

	@RequestMapping(value = "/addLoginDetails", method = RequestMethod.POST)
	public String addLoginDetails(@RequestParam final String username,
			@RequestParam final String password, @RequestParam final String service)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final User user = UserManager.getCurrentUser(manager);
		
		try
		{
			manager.getTransaction().begin();
			final LoginDetails loginDetails = new LoginDetails(user, service, null, username, password);
			manager.persist(loginDetails);
			user.add(loginDetails);			
			manager.getTransaction().commit();
		}
		catch (final Exception e)
		{
			log.error("Error creating user", e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
				log.error("Rolling login detail creation");
			}
			manager.close();
		}

		return "redirect:/index.html";
	}
	
	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	public void currentUser(final HttpServletRequest req, final HttpServletResponse res)
	{
		res.setContentType("application/json");
		final EntityManager entityManager = EMFSingleton.getEntityManager();
		try
		{
			final User user = UserManager.getCurrentUser(entityManager);
			if (user == null)
			{

			}
			else
			{
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					mapper.writeValue(sos, user);
					log.info("User: " + mapper.writeValueAsString(user));
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
		finally
		{
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
			}
			entityManager.close();
		}
	}
	

	private boolean containsItem(final PlaceBookItem findItem, final List<PlaceBookItem> items)
	{
		for (final PlaceBookItem item : items)
		{
			if (findItem.getKey().equals(item.getKey())) { return true; }
		}
		return false;
	}
	
	private PlaceBook get(EntityManager manager, String key)
	{
		try
		{
			if (key != null)
			{
				return manager.find(PlaceBook.class, key);
			}
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return null;
	}
	
	private void setProxy()
	{
		Properties properties = PropertiesSingleton.get(getClass().getClassLoader());
		if(new Boolean(properties.getProperty(PropertiesSingleton.PROXY_ACTIVE, "false")))
		{
			System.getProperties().put( "proxySet", "true" );
			System.getProperties().put( "proxyHost", properties.getProperty(PropertiesSingleton.PROXY_HOST));
			System.getProperties().put( "proxyPort", properties.getProperty(PropertiesSingleton.PROXY_PORT) );					
		}
	}

	@RequestMapping(value = "/saveplacebook", method = RequestMethod.POST)
	public void savePlaceBookJSON(final HttpServletResponse res, @RequestParam("placebook") final String json)
	{
		log.info("Save Placebook: " + json);
		final ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);		
		final EntityManager manager = EMFSingleton.getEntityManager();				
		manager.getTransaction().begin();
		try
		{
			PlaceBook placebook = mapper.readValue(json, PlaceBook.class);
			final PlaceBook dbPlacebook = get(manager, placebook.getKey());
			final Collection<PlaceBookItem> updateItems = new ArrayList<PlaceBookItem>();
			if (dbPlacebook != null)
			{
				// Remove any items that are no longer used
				for (final PlaceBookItem item : dbPlacebook.getItems())
				{
					if (!containsItem(item, placebook.getItems()))
					{
						manager.remove(item);
					}
				}

				dbPlacebook.setItems(placebook.getItems());
				
				for (final Entry<String, String> entry : placebook.getMetadata().entrySet())
				{
					dbPlacebook.addMetadataEntry(entry.getKey(), entry.getValue());
				}
	
				dbPlacebook.setGeometry(placebook.getGeometry());
				
				placebook = dbPlacebook;
			}
			
			final Collection<PlaceBookItem> newItems = new ArrayList<PlaceBookItem>(placebook.getItems());
			placebook.setItems(new ArrayList<PlaceBookItem>());
			for (final PlaceBookItem newItem : newItems)
			{
				// Update existing item if possible
				PlaceBookItem item = newItem;
				if(item.getKey() != null)
				{
					PlaceBookItem oldItem = manager.find(PlaceBookItem.class, item.getKey());
					if(oldItem != null)
					{
						item = oldItem;
					}
														
					if(newItem.getSourceURL() != null && !newItem.getSourceURL().equals(item.getSourceURL()))
					{
						item.setSourceURL(newItem.getSourceURL());
						updateItems.add(item);
					}				
						
					for(Entry<String, Integer> entry: newItem.getParameters().entrySet())
					{
						item.addParameterEntry(entry.getKey(), entry.getValue());	
					}
						
					if(item instanceof TextItem)
					{
						TextItem text = (TextItem)item;
						text.setText(((TextItem)newItem).getText());
					}
				}
				else
				{
					manager.persist(item);
					updateItems.add(item);
				}
					
				for(Entry<String, String> metadataItem: newItem.getMetadata().entrySet())
				{
					item.addMetadataEntry(metadataItem.getKey(), metadataItem.getValue());
				}
	
				if(item.getOwner() == null)
				{
					item.setOwner(UserManager.getCurrentUser(manager));
				}

				if(item.getTimestamp() == null)
				{
					item.setTimestamp(new Date());
				}
				
				placebook.addItem(item);
			}

			if(placebook.getOwner() == null)
			{
				placebook.setOwner(UserManager.getCurrentUser(manager));
			}
			
			if(placebook.getTimestamp() == null)
			{
				placebook.setTimestamp(new Date());
			}
				
			placebook = manager.merge(placebook);									
			manager.getTransaction().commit();

			log.info("Saved Placebook:" + mapper.writeValueAsString(placebook));
			
			manager.getTransaction().begin();
			setProxy();			
			for(PlaceBookItem item: updateItems)
			{
				try
				{
					if(item instanceof MediaItem)
					{
						final MediaItem mediaItem = (MediaItem)item;
						mediaItem.writeDataToDisk(mediaItem.getSourceURL().toExternalForm(), mediaItem.getSourceURL().openStream());
					}
					else if(item instanceof GPSTraceItem)
					{
						final GPSTraceItem gpsItem = (GPSTraceItem)item;
						gpsItem.readTrace(gpsItem.getSourceURL().openStream());
					}
					else if(item instanceof WebBundleItem)
					{
						//final WebBundleItem webItem = (WebBundleItem)item;
						// TODO
					}
				}
				catch(Exception e)
				{
					log.info(e.getMessage(), e);
				}
			}
			manager.getTransaction().commit();			
			
			res.setContentType("application/json");				
			final ServletOutputStream sos = res.getOutputStream();
			final PlaceBook resultPlacebook = manager.find(PlaceBook.class, placebook.getKey());
			log.info("Saved Placebook:" + mapper.writeValueAsString(resultPlacebook));
			mapper.writeValue(sos, resultPlacebook);
			sos.flush();			
		}
		catch (final Throwable e)
		{
			log.warn(e.getMessage(), e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			manager.close();
		}
	}

	@RequestMapping(value = "/admin/shelf/{owner}", method = RequestMethod.GET)
	public ModelAndView getPlaceBooksJSON(final HttpServletRequest req, 
										  final HttpServletResponse res,
				   @PathVariable("owner") final String owner)
	{
		if (owner.trim().isEmpty())
			return null;

		final EntityManager pm = EMFSingleton.getEntityManager();
		final TypedQuery<User> uq = 
			pm.createQuery("SELECT u FROM User u WHERE u.email LIKE :email", 
						   User.class);
		uq.setParameter("email", owner.trim());
		try
		{
			final User user = uq.getSingleResult();

			final TypedQuery<PlaceBook> q = 
				pm.createQuery(
					"SELECT p FROM PlaceBook p WHERE p.owner = :user",
					PlaceBook.class
				);

			q.setParameter("user", user);
			final Collection<PlaceBook> pbs = q.getResultList();

			log.info("Converting " + pbs.size() + " PlaceBooks to JSON");
			if (!pbs.isEmpty())
			{
				final Shelf s = new Shelf(user, pbs);
				try
				{
					final ObjectMapper mapper = new ObjectMapper();
					final ServletOutputStream sos = res.getOutputStream();
					res.setContentType("application/json");
					mapper.writeValue(sos, s);
					sos.flush();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}

		}
		catch (final NoResultException e)
		{
			log.error(e.toString());
		}
		finally
		{
			pm.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/package/{key}", method = RequestMethod.GET)
	public ModelAndView makePackage(final HttpServletRequest req, 
									final HttpServletResponse res,
		  	   @PathVariable("key") final String key)
	{
		final EntityManager pm = EMFSingleton.getEntityManager();

		final PlaceBook p = pm.find(PlaceBook.class, key);
		final File zipFile = PlaceBooksAdminHelper.makePackage(p);
		if (zipFile == null)
		{
			return new ModelAndView("message", "text", 
									"Making and compressing package");
		}
			
		try 
		{
			// Serve up file from disk
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final FileInputStream fis = new FileInputStream(zipFile);
			final BufferedInputStream bis = new BufferedInputStream(fis);

			final byte data[] = new byte[2048];
			int i;
			while ((i = bis.read(data, 0, 2048)) != -1)
			{
				bos.write(data, 0, i);
			}
			fis.close();

			final ServletOutputStream sos = res.getOutputStream();
			res.setContentType("application/zip");
			res.setHeader("Content-Disposition", "attachment; filename=\"" 
						  + p.getKey() + ".zip\"");
			sos.write(bos.toByteArray());
			sos.flush();

		}
		catch (final IOException e)
		{
			log.error(e.toString());
			return new ModelAndView("message", "text", "Error sending package");
		}
		finally
		{
			pm.close();
		}

		return null;
	}

	
	@RequestMapping(value = "/admin/search/{terms}", method = RequestMethod.GET)
	public ModelAndView searchGET(@PathVariable("terms") final String terms)
	{
		final long timeStart = System.nanoTime();
		final long timeEnd;


		final StringBuffer out = new StringBuffer();
		for (final Map.Entry<PlaceBook, Integer> entry : 
			 PlaceBooksAdminHelper.search(terms))
		{
			out.append("key=" + entry.getKey().getKey() + ", score=" 
					   + entry.getValue() + "<br>");
		}

		timeEnd = System.nanoTime();
		out.append("<br>Execution time = " + (timeEnd - timeStart) + " ns");

		return new ModelAndView("message", "text", "search results:<br>" 
								+ out.toString());
	}

	@RequestMapping(value = "/admin/search", method = RequestMethod.POST)
	public ModelAndView searchPOST(final HttpServletRequest req)
	{
		final StringBuffer out = new StringBuffer();
		final String[] terms = req.getParameter("terms").split("\\s");
		for (int i = 0; i < terms.length; ++i)
		{
			out.append(terms[i]);
			if (i < terms.length - 1)
			{
				out.append("+");
			}
		}

		return searchGET(out.toString());
	}	
	
	@RequestMapping(value = "/admin/add_item/upload", 
					method = RequestMethod.POST)
	public ModelAndView uploadFile(final HttpServletRequest req)
	{
		final EntityManager manager = EMFSingleton.getEntityManager();
		final ItemData itemData = new ItemData();
		
		try
		{
			FileItem fileData = null;
			String name = null;
			String type = null;
			String itemKey = null;
			String placebookKey = null;
			
			manager.getTransaction().begin();
			@SuppressWarnings("unchecked")
			final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
			for(FileItem item: items)
			{	
				if (item.isFormField())
				{
					String value = Streams.asString(item.getInputStream());
					if(!itemData.processItemData(manager, item.getFieldName(), value))
					{
						if(item.getFieldName().equals("key"))
						{
							placebookKey = value;
						}
						else if(item.getFieldName().equals("itemKey"))
						{
							itemKey = value;
						}
					}
				}
				else
				{
					name = item.getName();					
					fileData = item;
					String[] split = PlaceBooksAdminHelper.getExtension(item.getFieldName());
					if (split == null)
						continue;

					type = split[0];
				}
			}

			if(itemData.getOwner() == null)
			{
				itemData.setOwner(UserManager.getCurrentUser(manager));
			}
			
			PlaceBookItem item = null;
			if(itemKey != null)
			{
				item = manager.find(PlaceBookItem.class, itemKey);
			}
			else if(placebookKey != null)
			{
				PlaceBook placebook = manager.find(PlaceBook.class, placebookKey);
				
				if(type.equals("gpstrace"))
				{
					item = new GPSTraceItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
					((GPSTraceItem)item).readTrace(fileData.getInputStream());
				}
				else if(type.equals("image"))
				{
					item = new ImageItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);			
				}
				else if(type.equals("video"))
				{
					item = new VideoItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);
				}
				else if(type.equals("audio"))
				{
					item = new AudioItem(itemData.getOwner(), itemData.getGeometry(), itemData.getSourceURL(), null);
					item.setPlaceBook(placebook);					
				}					
			}
			
			if(item instanceof MediaItem)
			{
				manager.getTransaction().commit();
				((MediaItem)item).writeDataToDisk(name, fileData.getInputStream());
				manager.getTransaction().begin();				
			}	
			
			manager.getTransaction().commit();
			
			return new ModelAndView("message", "text", "Success");					
		}
		catch (final Exception e)
		{
			log.error(e.toString(), e);
		}
		finally
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}

			manager.close();
		}

		return new ModelAndView("message", "text", "Failed");
	}

	@RequestMapping(value = "/admin/serve/gpstraceitem/{key}", 
					method = RequestMethod.GET)
	public ModelAndView serveGPSTraceItem(final HttpServletRequest req, 
								   	      final HttpServletResponse res,
								   	      @PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();

		try
		{
			final GPSTraceItem g = em.find(GPSTraceItem.class, key);

			if (g != null)
			{
				final String trace = g.getTrace();
				
				res.setContentType("text/xml");
				final PrintWriter p = res.getWriter();
				p.print(trace);
				p.close();
			}
			else
				throw new Exception("GPSTrace is null");
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/serve/imageitem/{key}", 
					method = RequestMethod.GET)
	public ModelAndView serveImageItem(final HttpServletRequest req, 
								   	   final HttpServletResponse res,
								   	   @PathVariable("key") final String key)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		log.info("Serving Image Item " + key);

		try
		{
			final ImageItem i = em.find(ImageItem.class, key);

			if (i != null && i.getPath() != null)
			{
				try
				{
					File image = new File(i.getPath());
					ImageInputStream iis = 
						ImageIO.createImageInputStream(image);
					Iterator<ImageReader> readers = 
						ImageIO.getImageReaders(iis);
					String fmt = "png";
					while (readers.hasNext()) 
					{
						ImageReader read = readers.next();
						fmt = read.getFormatName();
						System.out.println("*** format name = " + fmt);
					}

					OutputStream out = res.getOutputStream();
					ImageIO.write(ImageIO.read(image), fmt, out);
					out.close();
				}
				catch (final IOException e)
				{
					log.error(e.toString());
				}
			}
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		return null;
	}

	@RequestMapping(value = "/admin/serve/{type}item/{key}", 
					method = RequestMethod.GET)
	public void streamMediaItem(final HttpServletRequest req, 
								   	    final HttpServletResponse res,
									    @PathVariable("type") final String type,
								   	    @PathVariable("key") final String key)
	{
		String path = null;
		final EntityManager em = EMFSingleton.getEntityManager();

		try
		{
			final MediaItem m = em.find(MediaItem.class, key);

			if (m != null && m.getPath() != null)
			{
				path = m.getPath();
			}
			else
				throw new Exception("Error getting media file, invalid key");
		}
		catch (final Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			em.close();
		}

		if (path == null)
		{
			return;
		}

		try
		{
			String type_ = null;
			if (type.trim().equalsIgnoreCase("video"))
				type_ = "video";
			else if (type.trim().equalsIgnoreCase("audio"))
				type_ = "audio";
			else
				throw new Exception("Unrecognised media item type");

			final File file = new File(path);

			final String[] split = PlaceBooksAdminHelper.getExtension(path);
			if (split == null)
				throw new Exception("Error getting file suffix");

			
			final ServletOutputStream sos = res.getOutputStream();
			final long contentLength = file.length();			
			res.setContentType(type_ + "/" + split[1]);
			res.addHeader("Accept-Ranges", "bytes");
			res.addHeader("Content-Length", Long.toString(contentLength));

			final FileInputStream fis = new FileInputStream(file);
			final BufferedInputStream bis = new BufferedInputStream(fis);

			final String range = req.getHeader("Range");
			long startByte = 0;
			long endByte = contentLength -1;
			if(range != null)
			{
				if(range.startsWith("bytes="))
				{
					try
					{
						String[] rangeItems = range.substring(6).split("-");
						startByte = Long.parseLong(rangeItems[0]);
						endByte = Long.parseLong(rangeItems[1]);
					}
					catch(Exception e)
					{
						
					}
				}
			}
			
			res.addHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + contentLength);
			
			final int bufferLen = 2048; 
			final byte data[] = new byte[bufferLen];
			int length;
			bis.skip(startByte);
			try
			{
				while ((length = bis.read(data, 0, bufferLen)) != -1)
				{
					sos.write(data, 0, length);	
				}
				sos.flush();
			}
			finally
			{				
				fis.close();
				sos.close();
			}
		}
		catch (final Throwable e)
		{
//			Enumeration headers = req.getHeaderNames();
//			while(headers.hasMoreElements())
//			{
//				String header = (String)headers.nextElement();
//				log.info(header + ": " + req.getHeader(header));
//			}
			log.error("Error serving " + type + " " + key);
		}
	}
	

	
	// Helper class for passing around general PlaceBookItem data
	public static class ItemData
	{
		private Geometry geometry;
		private User owner;
		private URL sourceURL;

		public ItemData()
		{
		}

		public Geometry getGeometry()
		{
			return geometry;
		}

		public User getOwner()
		{
			return owner;
		}

		public URL getSourceURL()
		{
			return sourceURL;
		}

		private void setGeometry(final Geometry geometry)
		{
			this.geometry = geometry;
		}

		private void setOwner(final User owner)
		{
			this.owner = owner;
		}

		private void setSourceURL(final URL sourceURL)
		{
			this.sourceURL = sourceURL;
		}
		
		public boolean processItemData(final EntityManager pm, 
											   final String field,
											   final String value)
		{
			if (field.equals("owner"))
			{
				setOwner(UserManager.getUser(pm, value));
			}
			else if (field.equals("sourceurl"))
			{
				try
				{
					setSourceURL(new URL(value));
				}
				catch (final java.net.MalformedURLException e)
				{
					log.error(e.toString());
				}
			}
			else if (field.equals("geometry"))
			{
				try
				{
					setGeometry(new WKTReader().read(value));
				}
				catch (final ParseException e)
				{
					log.error(e.toString());
				}
			}
			else
			{
				return false;
			}
			return true;
		}
	}
}
