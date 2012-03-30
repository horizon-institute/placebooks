package placebooks.client.ui.openlayers;

import placebooks.client.JavaScriptInjector;
import placebooks.client.model.ServerInfo;

public class OSLayer extends Layer
{
	public final static OSLayer create(final String name, final ServerInfo serverInfo)
	{
		JavaScriptInjector.inject(ScriptResources.INSTANCE.openspace().getText());
		return createLayer(	name, serverInfo.getOpenSpaceHost(), serverInfo.getOpenSpaceBaseURL(),
							serverInfo.getOpenSpaceKey());
	}

	private final static native OSLayer createLayer(final String name, final String hostURL, final String tileURL,
			final String key)
	/*-{
		return new $wnd.OpenLayers.Layer.UKOrdnanceSurvey(name, {
			key : key,
			tileURL: tileURL,
			hostURL: hostURL					
		});
	}-*/;

	protected OSLayer()
	{

	}
}