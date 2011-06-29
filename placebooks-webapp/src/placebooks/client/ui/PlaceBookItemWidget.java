package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.openlayers.MapWidget;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemWidget extends Composite
{
	interface RefreshHandler
	{
		void refresh(PlaceBookItemWidget itemWidget); 
	}
	
	protected static final double HEIGHT_PRECISION = 10000;

	protected final SimplePanel rootPanel = new SimplePanel();

	private PlaceBookCanvas canvas;

	private PlaceBookItem item;

	private PlaceBookPanel panel;

	private Widget widget;

	//private final RefreshHandler refreshHandler;
	
	private final SimplePanel widgetPanel = new SimplePanel();

	PlaceBookItemWidget(final PlaceBookCanvas canvas, final PlaceBookItem item) //, final RefreshHandler refreshHandler)
	{
		this.item = item;
		rootPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
		widgetPanel.getElement().getStyle().setMargin(5, Unit.PX);
		widgetPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		rootPanel.add(widgetPanel);
		this.canvas = canvas;
		//this.refreshHandler = refreshHandler;
	}

	public void addToCanvas(final PlaceBookCanvas canvas)
	{
		canvas.add(rootPanel);
	}

	public int getHeight()
	{
		return rootPanel.getElement().getClientHeight();
	}

	public PlaceBookItem getItem()
	{
		return item;
	}

	public PlaceBookPanel getPanel()
	{
		return panel;
	}

	public void refresh()
	{
//		refreshHandler.refresh(this);
		if (getItem().is(ItemType.TEXT))
		{
			getContentWidget().getElement().setInnerHTML(item.getText());
		}
		else if (getItem().is(ItemType.IMAGE))
		{
			final Image image = (Image) getContentWidget();
			if (getItem().hasParameter("height"))
			{
				image.setWidth("auto");
			}
			else
			{
				image.setWidth("100%");
			}
			image.setUrl(getItem().getURL());
		}
		else if (getItem().is(ItemType.AUDIO))
		{
			final Audio audio = (Audio) getContentWidget();
			audio.setSrc(getItem().getURL());
		}
		else if (getItem().is(ItemType.VIDEO))
		{
			final Video video = (Video) getContentWidget();
			video.setSrc(getItem().getURL());
		}
		else if (getItem().is(ItemType.GPS))
		{
			final MapWidget mapPanel = (MapWidget) getContentWidget();
			mapPanel.setURL(getItem().getURL(), getItem().getMetadata("routeVisible", "true").equals("true"));
			mapPanel.refreshMarkers(canvas.getItems());
		}
		else if (getItem().is(ItemType.WEB))
		{
			final Frame frame = (Frame) getContentWidget();
			frame.setUrl(getItem().getSourceURL());
		}
	}

	public void removeFromCanvas(final PlaceBookCanvas canvas)
	{
		canvas.remove(rootPanel);
	}

	public void setItem(final PlaceBookItem item)
	{
		this.item = item;
		refresh();
	}

	public void setPosition(final float left, final int top)
	{
		rootPanel.getElement().getStyle().setLeft(left, Unit.PCT);
		rootPanel.getElement().getStyle().setTop(top, Unit.PX);
	}

	int getOrder()
	{
		return item.getParameter("order", 0);
	}

	void resize(final String width)
	{
		rootPanel.setWidth(width);
		if (getItem().hasParameter("height") && getPanel() != null)
		{
			final int height = getItem().getParameter("height");
			final double heightPCT = height / HEIGHT_PRECISION;
			final int heightPX = (int) (getPanel().getOffsetHeight() * heightPCT);

			getContentWidget().setHeight(heightPX + "px");
		}
		else
		{
			getContentWidget().getElement().getStyle().clearHeight();
		}

		if (item.is(ItemType.GPS))
		{
			refresh();
		}
	}

	void setContentWidget(final Widget widget)
	{
		this.widget = widget;
		widgetPanel.add(widget);
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

	protected Widget getContentWidget()
	{
		return widget;
	}
}