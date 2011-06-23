package placebooks.client.ui.menuItems;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookItemWidgetFrame;
import placebooks.client.ui.widget.MenuItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SetSourceURLMenuItem extends MenuItem
{
	private final PlaceBookItemWidgetFrame item;

	public SetSourceURLMenuItem(final String title, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		this.item = item;
	}

	@Override
	public void run()
	{
		final TextBox textBox = new TextBox();
		textBox.setWidth("300px");
		textBox.setValue(item.getItem().getSourceURL());

		final PopupPanel dialogBox = new PopupPanel(false, true);

		final Button closeButton = new Button("Set URL", new ClickHandler()
		{

			@Override
			public void onClick(final ClickEvent event)
			{
				dialogBox.hide();
				if (!textBox.getValue().equals(item.getItem().getSourceURL()))
				{
					item.getItem().setSourceURL(textBox.getValue());
					item.markChanged();
					item.refresh();
				}
			}
		});

		final Panel panel = new FlowPanel();
		panel.add(textBox);
		panel.add(closeButton);

		dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
		dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setWidget(panel);
		dialogBox.center();
		dialogBox.show();
	}
}
