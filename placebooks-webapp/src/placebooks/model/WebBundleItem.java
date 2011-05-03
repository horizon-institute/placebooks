package placebooks.model;

import placebooks.controller.PropertiesSingleton;

import java.io.*;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.log4j.Logger;

import org.apache.commons.io.FileUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Geometry;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class WebBundleItem extends PlaceBookItem
{
	@NotPersistent
  	private static final Logger log = 
		Logger.getLogger(WebBundleItem.class.getName());


	@Persistent
	private BufferedImage thumbnail;

	private File webBundle;

	public WebBundleItem(User owner, Geometry geom, URL sourceURL, 
						 File webBundle)
	{
		super(owner, geom, sourceURL);
		this.webBundle = webBundle;
		thumbnail = null;
	}

	public String getEntityName()
	{
		return WebBundleItem.class.getName();
	}

	public void deleteItemData()
	{
		try
		{
			FileUtils.deleteDirectory(webBundle);
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}
	}

	public String getWebBundlePath()
	{
		return PropertiesSingleton
					.get(this.getClass().getClassLoader())
					.getProperty(PropertiesSingleton.IDEN_WEBBUNDLE,
					"") + getKey();
	}

	public void appendConfiguration(Document config, Element root)
	{
		Element item = getConfigurationHeader(config);
		
		try
		{
			getWebBundle(); //TODO: why is this needed?

			File from = new File(getWebBundlePath());
			File to = new File(getPlaceBook().getPackagePath() 
							   + "/" + getKey());

			FileUtils.copyDirectory(from, to);

			Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(webBundle.getName()));
			item.appendChild(filename);
		}
		catch (IOException e)
		{
			log.error(e.toString());
		}

		root.appendChild(item);
	}

	// A thumbnail preview image of the webpage - rendered somehow and stored 
	// here
	public BufferedImage getPreview()
	{
		if (thumbnail == null)
		{
			// Thumbnail rendering functionality TODO
		}
		return thumbnail;
	}

	// An alternative preview - a FaceBook style header text plus image drawn 
	// from the webpage in question
	public WebPreview getWebPreview()
	{
		return null;
	}

	public static class WebPreview
	{
		private String headerText;
		private BufferedImage headerImage;

		public WebPreview(String htmlPage, BufferedImage headerImage)
		{
			// Select headerText from htmlPage TODO
			this.headerText = "";
			this.headerImage = headerImage;
		}

		public String getHeaderText() { return headerText; }
		public BufferedImage getHeaderImage() { return headerImage; }

	}

	@Persistent
	public String getWebBundle()
	{
		return webBundle.toString();
	}
	@Persistent
	public void setWebBundle(String filepath)
	{
		if (filepath != null)
			webBundle = new File(filepath);
	}

	/* (non-Javadoc)
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		// TODO Auto-generated method stub
		return "";
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
