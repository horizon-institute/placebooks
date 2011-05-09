package placebooks.client;

import placebooks.client.model.PlaceBook;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookCanvas;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class PlaceBookEditor implements EntryPoint
{
	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();
		RootPanel.get().add(canvas);
		canvas.setPlaceBook(PlaceBook
				.parse("{\"key\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":{\"key\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"email\":\"ktg@cs.nott.ac.uk\",\"passwordHash\":\"098f6bcd4621d373cade4e832627b4f6\",\"name\":\"Kevin Glover\",\"friends\":[]},\"timestamp\":1303214841887,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"items\":[{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8602e0001\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214841902,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test text string\"},{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test 2\"},{\"@class\":\"placebooks.model.ImageItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://farm6.static.flickr.com/5103/5634121971_cdfd1982ca.jpg\",\"metadata\":{},\"parameters\":{}}],\"metadata\":{},\"index\":null}"));
		canvas.reflow();
	}
}