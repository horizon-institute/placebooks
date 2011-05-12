package placebooks.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class WebBundleItem extends PlaceBookItem
{
	public static class WebPreview
	{
		private BufferedImage headerImage;
		private String headerText;

		public WebPreview(final String htmlPage, final BufferedImage headerImage)
		{
			// Select headerText from htmlPage TODO
			this.headerText = "";
			this.headerImage = headerImage;
		}

		public BufferedImage getHeaderImage()
		{
			return headerImage;
		}

		public String getHeaderText()
		{
			return headerText;
		}

	}

	private static final Logger log = Logger.getLogger(WebBundleItem.class.getName());

	@Transient
	private BufferedImage thumbnail;

	private File webBundle;

	public WebBundleItem(final User owner, final Geometry geom, final URL sourceURL, final File webBundle)
	{
		super(owner, geom, sourceURL);
		this.webBundle = webBundle;
		thumbnail = null;
	}

	WebBundleItem()
	{
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		final Element item = getConfigurationHeader(config);

		try
		{
			getWebBundle(); // TODO: why is this needed?

			final File from = new File(getWebBundlePath());
			final File to = new File(getPlaceBook().getPackagePath() + "/" + getKey());

			FileUtils.copyDirectory(from, to);

			final Element filename = config.createElement("filename");
			filename.appendChild(config.createTextNode(webBundle.getName()));
			item.appendChild(filename);
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}

		root.appendChild(item);
	}

	@Override
	public void deleteItemData()
	{
		try
		{
			FileUtils.deleteDirectory(webBundle);
		}
		catch (final IOException e)
		{
			log.error(e.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getEntityName()
	{
		return WebBundleItem.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		// TODO Auto-generated method stub
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
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

	public String getWebBundle()
	{
		return webBundle.toString();
	}

	public String getWebBundlePath()
	{
		return PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_WEBBUNDLE, "") + getKey();
	}

	// An alternative preview - a FaceBook style header text plus image drawn
	// from the webpage in question
	public WebPreview getWebPreview()
	{
		return null;
	}

	public void setWebBundle(final String filepath)
	{
		if (filepath != null)
		{
			webBundle = new File(filepath);
		}
	}

}
