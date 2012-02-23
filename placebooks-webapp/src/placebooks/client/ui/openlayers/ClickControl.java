package placebooks.client.ui.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class ClickControl extends JavaScriptObject
{
	public static final native ClickControl create(EventHandlerFunction eventHandler)
	/*-{
		$wnd.OpenLayers.Control.Click = $wnd.OpenLayers.Class($wnd.OpenLayers.Control, {
				defaultHandlerOptions: {
					'single': true,
	                'double': false,
	                'pixelTolerance': 0,
	                'stopSingle': false,
	                'stopDouble': false
	            },
	
	            initialize: function(eventHandler, options)
	            {
	            	this.eventHandler = eventHandler;
	                this.handlerOptions = $wnd.OpenLayers.Util.extend(
	                    {}, this.defaultHandlerOptions
	                );
	                $wnd.OpenLayers.Control.prototype.initialize.apply(
	                    this, arguments
	                ); 
	                this.handler = new $wnd.OpenLayers.Handler.Click(
	                    this, {
	                        'click': this.eventHandler
	                    }, this.handlerOptions
	                );
	            },
		});
		
		return new $wnd.OpenLayers.Control.Click(eventHandler);		
	}-*/;

	protected ClickControl()
	{
	}

	public final native void activate()
	/*-{
		this.activate();
	}-*/;
}
