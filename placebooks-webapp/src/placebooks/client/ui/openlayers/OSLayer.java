package placebooks.client.ui.openlayers;

import placebooks.client.JavaScriptInjector;

public class OSLayer extends Layer
{
	public final static OSLayer create(final String name)
	{
		JavaScriptInjector.inject(ScriptResources.INSTANCE.openspace().getText());

		return createLayer(name);
	}

	private final static native OSLayer createLayer(final String name)
	/*-{
		return new $wnd.OpenLayers.Layer.UKOrdnanceSurvey(name);
	}-*/;

	protected OSLayer()
	{

	}
}
