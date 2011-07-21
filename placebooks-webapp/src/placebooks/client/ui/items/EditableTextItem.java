package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.RichTextArea;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

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
		textPanel.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				TextArea area = new TextArea();
				area.getElement().getStyle().setLeft(-1000, Unit.PX);
				area.setFocus(true);
				RootPanel.get().add(area);
				
			}
		});
	}

	@Override
	public void refresh()
	{
		textPanel.getElement().setInnerHTML(item.getText());
	}
}
