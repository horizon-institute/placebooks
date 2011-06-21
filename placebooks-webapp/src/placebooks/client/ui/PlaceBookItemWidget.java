package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.openlayers.MapWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemWidget extends Composite
{
	protected static final double HEIGHT_PRECISION = 10000;
	
	private PlaceBookItem item;
	
	private PlaceBookPanel panel;
	
	PlaceBookItemWidget(final PlaceBookItem item)
	{
		this.item = item;
	}
	
	int getOrder()
	{
		if (getItem().hasParameter("order")) { return getItem().getParameter("order"); }
		return 0;
	}
	
	void setContentWidget(Widget widget)
	{
		initWidget(widget);
	}
	
	void setOrder(final int order)
	{
		GWT.log("Order: " + order);
		getItem().setParameter("order", order);
	}

	void setPanel(final PlaceBookPanel panel)
	{
		if (getPanel() != null)
		{
			getPanel().remove(this);
		}
		this.panel = panel;
		if (panel != null)
		{
			getItem().setParameter("panel", panel.getIndex());
			panel.add(this);
		}
	}
	
	void resize()
	{
		if (getItem().hasParameter("height") && getPanel() != null)
		{
			final int height = getItem().getParameter("height");
			final double heightPCT = height / HEIGHT_PRECISION;
			final int heightPX = (int) (getPanel().getOffsetHeight() * heightPCT);

			getContentWidget().getElement().getStyle().setHeight(heightPX, Unit.PX);
		}
		else
		{
			getContentWidget().setHeight("auto");			
		}
	}
	
	public int getContentHeight()
	{
		return getContentWidget().getElement().getClientHeight();
	}

	public void setTop(final int top)
	{
		getWidget().getElement().getStyle().setTop(top, Unit.PX);
	}
	
	public PlaceBookItem getItem()
	{
		return item;
	}
	
	public PlaceBookPanel getPanel()
	{
		return panel;
	}
	
	public void setItem(PlaceBookItem item)
	{
		this.item = item;
		refresh();
	}
	
	protected Widget getContentWidget()
	{
		return getWidget();
	}
	
	public void refresh()
	{
		if (getItem().is(ItemType.TEXT))
		{
			getContentWidget().getElement().setInnerHTML(item.getText());
		}
		else if (getItem().is(ItemType.IMAGE))
		{
			final Image image = (Image)getContentWidget();
			if (getItem().hasParameter("height"))
			{
				image.setWidth("auto");
			}
			image.setUrl(getItem().getURL());
		}
		else if (getItem().is(ItemType.AUDIO))
		{
			final Audio audio = (Audio)getContentWidget();
			audio.setSrc(getItem().getURL());
		}
		else if (getItem().is(ItemType.VIDEO))
		{
			final Video video = (Video)getContentWidget();
			video.setSrc(getItem().getURL());
		}
		else if (getItem().is(ItemType.GPS))
		{
			final MapWidget mapPanel = (MapWidget)getContentWidget();
			mapPanel.setURL(getItem().getURL(), getItem().getMetadata("routeVisible", "true").equals("true"));
		}
		else if (getItem().is(ItemType.WEB))
		{
			final Frame frame = (Frame)getContentWidget();
			frame.setUrl(getItem().getSourceURL());
		}
	}
}