package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.dialogs.PlaceBookUploadDialog;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class MediaItem extends PlaceBookItemWidget
{
	private final Panel panel;
	private String hash = null;

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
		panel = new SimplePanel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		initWidget(panel);
	}

	@Override
	public void refresh()
	{
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
				final Button button = new Button("Upload", new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(controller, MediaItem.this);
						dialog.show();
					}
				});
				if (item.is(ItemType.IMAGE))
				{
					button.setText("Upload Image");
				}
				else if (item.is(ItemType.VIDEO))
				{
					button.setText("Upload Video");
				}
				else if (item.is(ItemType.AUDIO))
				{
					button.setText("Upload Audio");
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