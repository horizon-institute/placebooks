package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor.SaveTimer;
import placebooks.client.ui.widget.DropMenu;
import placebooks.client.ui.widget.MenuItem;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemWidgetFrame extends PlaceBookItemWidget
{
	interface PlaceBookItemFrameUiBinder extends UiBinder<MousePanel, PlaceBookItemWidgetFrame>
	{
	}

	private static final PlaceBookItemFrameUiBinder uiBinder = GWT.create(PlaceBookItemFrameUiBinder.class);

	@UiField
	Panel borderSection;

	@UiField
	MousePanel dragSection;

	@UiField
	MousePanel frame;

	@UiField
	Panel menuButton;

	@UiField
	MousePanel resizeSection;
	
	@UiField
	Panel widgetPanel;

	private DropMenu dropMenu;
	
	private Widget widget;

	private final List<MenuItem> menuItems = new ArrayList<MenuItem>();

	private SaveTimer saveTimer;

	public PlaceBookItemWidgetFrame(final SaveTimer timer, final PlaceBookItem item)
	{
		super(item);
		initWidget(uiBinder.createAndBindUi(this));
		this.saveTimer = timer;
	}
	
	void addMenuItem(final MenuItem menuItem)
	{
		menuItems.add(menuItem);
	}
	
	@Override
	void setContentWidget(final Widget widget)
	{
		this.widget = widget;
		widgetPanel.add(widget);
	}

	@Override
	protected Widget getContentWidget()
	{
		return widget;
	}
	
	public void markChanged()
	{
		saveTimer.markChanged();
	}

	void addDragStartHandler(final MouseDownHandler handler)
	{
		dragSection.addMouseDownHandler(handler);
	}

	void addMouseOutHandler(final MouseOutHandler handler)
	{
		frame.addMouseOutHandler(handler);
	}
	
	@Override	
	public void setTop(final int top)
	{
		getWidget().getElement().getStyle().setTop(top - 20, Unit.PX);
	}	

	void addMouseOverHandler(final MouseOverHandler handler)
	{
		widget.addDomHandler(handler, MouseOverEvent.getType());
	}

	void addResizeStartHandler(final MouseDownHandler handler)
	{
		resizeSection.addMouseDownHandler(handler);
	}

	ImageResource getDragImage()
	{
		return getItem().getIcon();
	}
	
	void setContentHeight(final int heightPX)
	{
		final int heightPCT = (int) ((heightPX * HEIGHT_PRECISION) / getPanel().getOffsetHeight());
		getItem().setParameter("height", heightPCT);
		saveTimer.markChanged();
		// Assuming resize() will be called (via reflow on the panel) so don't set height here
	}	

	@UiHandler("menuButton")
	void handleMenuClick(final ClickEvent event)
	{
		final int x = menuButton.getElement().getAbsoluteLeft();
		final int y = menuButton.getElement().getAbsoluteTop() + menuButton.getElement().getClientHeight();
		dropMenu.show(menuItems, x, y);
		event.stopPropagation();
	}

	void hideFrame()
	{
		frame.getElement().getStyle().setZIndex(0);
		menuButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		menuButton.getElement().getStyle().setOpacity(0);
		menuButton.getElement().getStyle().setZIndex(-50);
		borderSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		borderSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dragSection.getElement().getStyle().setOpacity(0);
		dragSection.getElement().getStyle().setZIndex(-50);
		dragSection.getElement().getStyle().setCursor(Cursor.DEFAULT);
		resizeSection.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		resizeSection.getElement().getStyle().setOpacity(0);
		resizeSection.getElement().getStyle().setZIndex(-50);
		resizeSection.getElement().getStyle().setCursor(Cursor.DEFAULT);
	}

	void setDropMenu(final DropMenu dropMenu)
	{
		this.dropMenu = dropMenu;
	}

	void showFrame()
	{
		frame.getElement().getStyle().setZIndex(5);
		menuButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		menuButton.getElement().getStyle().setOpacity(1);
		menuButton.getElement().getStyle().setZIndex(5);
		borderSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		borderSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dragSection.getElement().getStyle().setOpacity(1);
		dragSection.getElement().getStyle().setZIndex(5);
		dragSection.getElement().getStyle().setCursor(Cursor.MOVE);
		resizeSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		resizeSection.getElement().getStyle().setOpacity(1);
		resizeSection.getElement().getStyle().setZIndex(5);
		resizeSection.getElement().getStyle().setCursor(Cursor.S_RESIZE);
	}

	void startDrag(final MouseDownEvent event)
	{
		frame.addStyleName(Resources.INSTANCE.style().dragShadow());
		frame.getElement().getStyle().setZIndex(20);
		menuButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		menuButton.getElement().getStyle().setOpacity(0.6);
		borderSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		borderSection.getElement().getStyle().setOpacity(0.6);
		dragSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dragSection.getElement().getStyle().setOpacity(0.6);
		resizeSection.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		resizeSection.getElement().getStyle().setOpacity(0.6);

		setPanel(null);
	}

	void stopDrag()
	{
		frame.removeStyleName(Resources.INSTANCE.style().dragShadow());
		frame.getElement().getStyle().setZIndex(0);
	}
}