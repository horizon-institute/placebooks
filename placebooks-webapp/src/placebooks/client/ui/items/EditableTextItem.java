package placebooks.client.ui.items;

import placebooks.client.Resources;
import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.ui.elements.RichTextArea;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class EditableTextItem extends PlaceBookItemView
{
	private RichTextArea textPanel = new RichTextArea("");
	private final FlowPanel rootPanel = new FlowPanel();
	private final Image markerImage = new Image();

	EditableTextItem(final PlaceBookItemController controller)
	{
		super(controller);

		rootPanel.add(markerImage);
		rootPanel.add(textPanel);

		initWidget(rootPanel);
		textPanel.setStyleName(Resources.STYLES.style().textitem());
		textPanel.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				event.stopPropagation();
				fireFocusChanged(true);
			}
		}, ClickEvent.getType());
		textPanel.addFocusHandler(new com.google.gwt.event.dom.client.FocusHandler()
		{
			@Override
			public void onFocus(final FocusEvent event)
			{
				fireFocusChanged(true);
				event.stopPropagation();
			}
		});
		textPanel.addBlurHandler(new BlurHandler()
		{

			@Override
			public void onBlur(final BlurEvent event)
			{
				fireFocusChanged(false);

			}
		});
		textPanel.addKeyUpHandler(new KeyUpHandler()
		{
			@Override
			public void onKeyUp(final KeyUpEvent event)
			{
				getItem().setText(textPanel.getElement().getInnerHTML());
				fireResized();
				fireChanged();
			}
		});

		textPanel.getElement().setInnerHTML(getItem().getText());
	}

	@Override
	public void refresh()
	{
		if (getItem().showMarker())
		{
			markerImage.setResource(getItem().getMarkerImage());
			markerImage.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
			markerImage.getElement().getStyle().setProperty("margin", "0 8px 0 0");
		}
		markerImage.setVisible(getItem().showMarker());
	}

	@Override
	public String resize()
	{
		super.resize();
		if (getParent() != null)
		{
			double panelWidth = 300;
			if (getParent().getParent() != null && getParent().getParent().getParent() != null)
			{
				final String panelWidthString = getParent().getParent().getParent().getElement().getStyle().getWidth();
				if (panelWidthString != null && panelWidthString.endsWith("%"))
				{
					final double percent = Double
							.parseDouble(panelWidthString.substring(0, panelWidthString.length() - 1));
					panelWidth = (900d * percent) / 100d;
				}
			}
			final double scale = getParent().getOffsetWidth() / panelWidth;
			textPanel.getElement()
					.setAttribute(	"style",
									"width: " + panelWidth
											+ "px; -webkit-transform-origin: 0% 0%; -webkit-transform: scale(" + scale
											+ "); -moz-transform-origin: 0% 0%; -moz-transform: scale(" + scale + ")");
			return (getOffsetHeight() * scale) + "px";
		}
		return null;
	}
}