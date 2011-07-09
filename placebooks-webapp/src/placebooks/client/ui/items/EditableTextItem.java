package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.RichTextArea;

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
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				fireFocusChanged(true);				
			}
		}, ClickEvent.getType());
		textPanel.addFocusHandler(new com.google.gwt.event.dom.client.FocusHandler()
		{
			@Override
			public void onFocus(FocusEvent event)
			{
				fireFocusChanged(true);
				event.stopPropagation();
			}
		});
		textPanel.addBlurHandler(new BlurHandler()
		{
			
			@Override
			public void onBlur(BlurEvent event)
			{
				fireFocusChanged(false);
				
			}
		});
		textPanel.addKeyUpHandler(new KeyUpHandler()
		{
			@Override
			public void onKeyUp(KeyUpEvent event)
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
}
