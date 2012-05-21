package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.dialogs.PlaceBookUploadDialog;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class MediaItem extends PlaceBookItemWidget
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	private final Panel panel;
	private String hash = null;
	private final Image markerImage = new Image();

	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();
		}
	};

	MediaItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);
		panel = new FlowPanel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.getElement().getStyle().setPosition(Position.RELATIVE);		
		
		markerImage.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				controller.goToPage(getItem().getParameter("mapPage"));				
			}
		});
		
		initWidget(panel);
	}

	@Override
	public void refresh()
	{
		if(getItem().showMarker())
		{
			markerImage.setResource(getItem().getMarkerImage());
			markerImage.getElement().getStyle().setPosition(Position.ABSOLUTE);
			markerImage.getElement().getStyle().setLeft(0, Unit.PX);
			markerImage.getElement().getStyle().setTop(0, Unit.PX);
			markerImage.getElement().getStyle().setZIndex(1);					
			markerImage.setVisible(true);
		}
		else
		{
			markerImage.setVisible(false);
		}
		
		if (item.getHash() == null)
		{
			if (hash != null)
			{
				panel.clear();
			}
			if (!panel.iterator().hasNext())
			{
				final FlowPanel uploadPanel = new FlowPanel();
				uploadPanel.setWidth("100%");
				uploadPanel.getElement().getStyle().setBackgroundColor("#000");
				uploadPanel.getElement().getStyle().setProperty("textAlign", "center");
				final Button button = new Button(uiMessages.upload(), new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(controller, MediaItem.this);
						dialog.show();
					}
				});
				String type = null;
				if (item.is(ItemType.IMAGE))
				{
					type = uiMessages.image();
				}
				else if (item.is(ItemType.VIDEO))
				{
					type = uiMessages.video();
				}
				else if (item.is(ItemType.AUDIO))
				{
					type = uiMessages.audio();
				}
				if(type != null)
				{
					button.setText(uiMessages.upload(type));
				}

				uploadPanel.add(button);
				panel.add(uploadPanel);
			}
		}
		else
		{
			if (hash == null)
			{
				panel.clear();
				panel.add(markerImage);
				panel.add(getMediaWidget());
			}

			if (!item.getHash().equals(hash))
			{
				setURL(item.getURL());
			}
		}
		checkSize();
		this.hash = item.getHash();
	}

	protected void checkHeightParam()
	{
		getMediaWidget().setWidth("100%");
		if (getItem().hasParameter("height"))
		{
			getMediaWidget().setHeight("100%");
		}
		else
		{
			getMediaWidget().setHeight("auto");
		}
	}

	protected void checkSize()
	{
		checkHeightParam();
		if (getMediaHeight() == 0)
		{
			loadTimer.schedule(1000);
		}
		else
		{
			loadTimer.cancel();
			fireResized();
		}
	}

	protected abstract int getMediaHeight();

	protected abstract Widget getMediaWidget();

	protected abstract void setURL(final String url);
}