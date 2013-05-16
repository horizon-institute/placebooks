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
		openSpaceKey = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_APIKEY, "");
		openSpaceHost = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_HOST, "");
		serverName = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_SERVER_NAME, "");
		openSpaceBaseURL = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_OPENSPACE_BASEURL, "");
		maxVideoSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_VIDEO_MAX_SIZE, "25"));

		maxImageSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_IMAGE_MAX_SIZE, "1"));

		maxAudioSize = Integer.parseInt(PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader())
				.getProperty(PropertiesSingleton.IDEN_AUDIO_MAX_SIZE, "10"));

		for (final ServiceInfo info : ServiceRegistry.getServices())
		{
			services.add(info);
		}
	}

	public final int getMaxAudioSize()
	{
		return maxAudioSize;
	}

	public final int getMaxImageSize()
	{
		return maxImageSize;
	}

	public final int getMaxVideoSize()
	{
		return maxVideoSize;
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

}
