package placebooks.client.ui.menuItems;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
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
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public SetSourceURLMenuItem(final SaveContext context, final PlaceBookItemFrame item)
	{
		super("Set URL");
		this.item = item;
		this.context = context;
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
					item.getItemWidget().refresh();
					context.markChanged();
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
