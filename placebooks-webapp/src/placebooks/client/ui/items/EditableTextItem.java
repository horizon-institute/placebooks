package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.RichTextArea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class EditableTextItem extends PlaceBookItemWidget
{
	private RichTextArea textPanel = new RichTextArea("");

	EditableTextItem(final PlaceBookItem item)
	{
		super(item);
		initWidget(textPanel);
		textPanel.setStyleName(Resources.INSTANCE.style().textitem());
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
				item.setText(textPanel.getElement().getInnerHTML());
				fireResized();
				fireChanged();
			}
		});
	}

	@Override
	public void refresh()
	{
		textPanel.getElement().setInnerHTML(item.getText());
	}

	@Override
	public String resize()
	{
		super.resize();
		if (getParent() != null)
		{
			double panelWidth = 300;
			if(getParent().getParent() != null && getParent().getParent().getParent() != null)
			{
				final String panelWidthString = getParent().getParent().getParent().getElement().getStyle().getWidth();				
				if(panelWidthString != null && panelWidthString.endsWith("%"))
				{
					double percent = Double.parseDouble(panelWidthString.substring(0, panelWidthString.length() - 1));
					panelWidth = (900d * percent) / 100d;
				}
			}
			final double scale = getParent().getOffsetWidth() / panelWidth;
			textPanel.getElement().setAttribute("style",
												"width: "+ panelWidth +"px; -webkit-transform-origin: 0% 0%; -webkit-transform: scale("
														+ scale + ")");
			return (getOffsetHeight() * scale) + "px";
		}
		return null;
	}
}
