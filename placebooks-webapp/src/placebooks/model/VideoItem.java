package placebooks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.persistence.Entity;

import org.apache.commons.io.IOUtils;

import placebooks.controller.CommunicationHelper;
import placebooks.controller.PropertiesSingleton;
import placebooks.controller.TranscodeHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class VideoItem extends MediaItem
{
	public VideoItem()
	{
		super();
	}

	public VideoItem(final User owner, final Geometry geom, final URL sourceURL,
					 final String video)
	{
		super(owner, geom, sourceURL, video);
	}
	
	public VideoItem(final VideoItem v)
	{
		super(v);
	}

	@Override
	public VideoItem deepCopy()
	{
		return new VideoItem(this);
	}

	@Override
	public String getEntityName()
	{
		return VideoItem.class.getName();
	}
	
	@Override
	protected void copyDataToPackage() throws IOException
	{
		// Check package dir exists already
		final String path = PropertiesSingleton.get(this.getClass().getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_PKG, "") + "/" + getPlaceBook().getPlaceBookBinder().getKey();

		if (path != null && (new File(path).exists() || new File(path).mkdirs()))
		{
			final File dataFile = new File(getMobilePath());
			final FileInputStream fis = new FileInputStream(dataFile);
			final File to = new File(path + "/" + dataFile.getName());

			log.info("Copying file, from=" + dataFile.toString() + ", to=" + to.toString());

			final FileOutputStream fos = new FileOutputStream(to);
			IOUtils.copy(fis, fos);
			fis.close();
			fos.close();
		}
	}
	
	
	/**
	 * Get the path of the video transcoded for android
	 * @return
	 */
	public String getMobilePath()
	{
		String mobilePath = null;
		String originalPath = getPath();
		if(originalPath!=null)
		{
			mobilePath = originalPath + "-mobile.mp4";
			File mobileFile = new File(mobilePath);
			//Check if the mobile version of the file exists and if it doesn't, and it should be transcoded, do it now.
			// Otherwise return the original items path
			if( !mobileFile.exists() && PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.VIDEOITEM_FFMPEG_TRANSCODE, "false").equals("true"))
			{
				TranscodeHelper.transcodeVideoForMobile(mobilePath);
			}
			else
			{
				mobilePath = originalPath;
			}
		}
		return mobilePath;
	}
	
	/**
	 * Get the path of the video transcoded for chrome
	 * @param postfix
	 * @return
	 */
	public String getChromePath()
	{
		String chromePath = null;
		String originalPath = getPath();
		if(originalPath!=null)
		{
			chromePath = originalPath + "-chrome.ogg";
			File mobileFile = new File(chromePath);
			//Check if the mobile version of the file exists and if it doesn't, and it should be transcoded, do it now.
			// Otherwise return the original items path
			if(!mobileFile.exists())
			{	
				if(PropertiesSingleton.get(CommunicationHelper.class.getClassLoader()).getProperty(PropertiesSingleton.VIDEOITEM_FFMPEG_TRANSCODE, "false").equals("true"))
				{
					TranscodeHelper.transcodeVideoForChrome(chromePath);
				}
				else
				{
					chromePath = originalPath;
				}
			}
		}
		return chromePath;
	}
}