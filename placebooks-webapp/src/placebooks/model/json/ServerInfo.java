package placebooks.model.json;

import java.util.ArrayList;
import java.util.List;

import placebooks.controller.PlaceBooksAdminHelper;
import placebooks.controller.PropertiesSingleton;
import placebooks.services.ServiceInfo;
import placebooks.services.ServiceRegistry;

public class ServerInfo
{
	private final String openSpaceKey;

	private final String openSpaceHost;

	private final String openSpaceBaseURL;

	private final String serverName;

	private final int maxImageSize;
	private final int maxVideoSize;
	private final int maxAudioSize;
	
	private final List<ServiceInfo> services = new ArrayList<ServiceInfo>();

	public ServerInfo()
	{
		this.openSpaceKey = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_APIKEY, "");
		this.openSpaceHost = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_HOST, "");
		this.serverName = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_SERVER_NAME, "");
		this.openSpaceBaseURL = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_BASEURL, "");
		this.maxVideoSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_VIDEO_MAX_SIZE, "25"));

		this.maxImageSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_IMAGE_MAX_SIZE, "1"));

		this.maxAudioSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_AUDIO_MAX_SIZE, "10"));

		for(ServiceInfo info: ServiceRegistry.getServices())
		{
			services.add(info);
		}
	}

	public final String getOpenSpaceBaseURL()
	{
		return openSpaceBaseURL;
	}

	public final String getOpenSpaceHost()
	{
		return openSpaceHost;
	}

	public final String getOpenSpaceKey()
	{
		return openSpaceKey;
	}

	public final String getServerName()
	{
		return serverName;
	}
	
	public final int getMaxImageSize()
	{
		return maxImageSize;
	}

	public final int getMaxVideoSize()
	{
		return maxVideoSize;
	}

	public final int getMaxAudioSize()
	{
		return maxAudioSize;
	}

}
