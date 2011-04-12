package placebooks.model;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteOrder;
import java.util.logging.XMLFormatter;

import javax.imageio.ImageIO;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import placebooks.controller.EverytrailHelper;
import placebooks.controller.PropertiesSingleton;

import com.google.gwt.user.client.ui.Image;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class ImageItem extends PlaceBookItem
{
	@Persistent
	private BufferedImage image; 

	@NotPersistent
	private File imageFile;
	
	public ImageItem(User owner, Geometry geom, URL sourceURL, 
					 BufferedImage image)
	{
		super(owner, geom, sourceURL);
		this.image = image;
		imageFile = null;
	}

	public ImageItem(User owner, Node everytrailPicture)
	{
		super(owner);
		NamedNodeMap pictureAttributes = everytrailPicture.getAttributes();
		for(int attributeIndex=0;attributeIndex<pictureAttributes.getLength();attributeIndex++)
		{
			if(pictureAttributes.item(attributeIndex).getNodeName().equals("id"))
			{
				this.addParameterEntry("picture_id", pictureAttributes.item(attributeIndex).getNodeValue());
			}
		}
		NodeList pictureProperties = everytrailPicture.getChildNodes();		
		for(int propertyIndex=0;propertyIndex<pictureProperties.getLength();propertyIndex++)
		{
			Node item = pictureProperties.item(propertyIndex);
			String itemName = item.getNodeName(); 
			log.debug("Inspecting property: " + itemName + " which is " + item.getTextContent());
			if(itemName.equals("fullsize"))
			{
				try
				{
					this.setSourceURL(new URL(item.getTextContent()));					
					URL url = this.getSourceURL();
					URLConnection conn;
				   if(PropertiesSingleton.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_ACTIVE, "false").equalsIgnoreCase("true"))
				   {
				   	log.debug("Using proxy: " + PropertiesSingleton.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, "") + ":" +
				   			PropertiesSingleton.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""));
					    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					   		 PropertiesSingleton.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_HOST, ""),
					   		 Integer.parseInt(PropertiesSingleton.get(ImageItem.class.getClassLoader()).getProperty(PropertiesSingleton.PROXY_PORT, ""))));
					    conn = url.openConnection(proxy);
				    }
				    else
				    {
				   	 conn = url.openConnection();
				    }
				   
				   
				   // Get the response
				   BufferedImage bi = ImageIO.read(conn.getInputStream());
				   this.setImage(bi);
				   log.debug("image width: " + bi.getWidth() + "px Height: " + bi.getHeight() + "px");	
				}
				catch(MalformedURLException ex)
				{
					log.error("Can't convert Everytrail Picture URL to a valid URL.");
					log.debug(ex.getMessage());
				}
				catch(IOException ex)
				{
					log.error("Can't download Everytrail Picture and convert to BufferedImage.");
					log.debug(ex.getMessage());					
				}
			}
			if(itemName.equals("location"))
			{
				NamedNodeMap locationAttributes = item.getAttributes();
				String lat = null;
				String lon = null;
				for(int locAttributeIndex=0;locAttributeIndex<locationAttributes.getLength();locAttributeIndex++)
				{
					if(locationAttributes.item(locAttributeIndex).getNodeName().equals("lat"))
					{
						lat = locationAttributes.item(locAttributeIndex).getNodeValue();
					}
					if(locationAttributes.item(locAttributeIndex).getNodeName().equals("lon"))
					{
						lon = locationAttributes.item(locAttributeIndex).getNodeValue();
					}
				}
				try
				{
					GeometryFactory gf = new GeometryFactory();
					Geometry newGeom = gf.toGeometry(new Envelope(new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat))));
					log.debug("Detected coordinates " + lat.toString() + ", " + lon.toString());
					this.setGeometry(newGeom);
				}
				catch(Exception ex)
				{
					log.error("Couldn't get lat/lon data from Everytrail picture.");
					log.debug(ex.getMessage());
				}
			}
		}
	}
	
	public String getEntityName()
	{
		return ImageItem.class.getName();
	}

	public File getImagePath()
	{
		return imageFile;
	}

	public void setImage(BufferedImage image)
	{
		this.image = image;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		
		// Dump image to disk
		try 
		{
			String path = PropertiesSingleton
							.get(this.getClass().getClassLoader())
							.getProperty(PropertiesSingleton.IDEN_PKG, "") 
							+ getPlaceBook().getKey();
			imageFile = new File(path + "/" + getKey() + ".png");
			log.info("Writing ImageItem data to " 
					 + imageFile.getAbsolutePath());
			if (new File(path).exists() || new File(path).mkdirs())
			{
			    ImageIO.write(image, "PNG", imageFile);
				Element filename = config.createElement("filename");
				filename.appendChild(
					config.createTextNode(imageFile.getName())
				);
				item.appendChild(filename);
			}
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}
		
		root.appendChild(item);
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		StringBuilder output = new StringBuilder();
		output.append("<img src='");
		output.append(imageFile.getPath());
		output.append("' class='placebook-item-image' id='");
		output.append(this.getPlaceBook().getKey());
		output.append("' />");
		return output.toString();
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
	}

}

