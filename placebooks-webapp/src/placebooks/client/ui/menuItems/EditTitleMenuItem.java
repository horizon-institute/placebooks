package placebooks.client.ui.menuItems;

import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class EditTitleMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public EditTitleMenuItem(final SaveContext context, final PlaceBookItemFrame item)
	{
		super("Edit Title");
		this.item = item;
		this.context = context;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void run()
	{
		final Panel panel = new FlowPanel();
		final PopupPanel dialogBox = new PopupPanel(true, true);
		final TextBox title = new TextBox();
		title.setText(item.getItem().getMetadata("title", ""));
		final Button uploadButton = new Button("Set Title", new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				item.getItem().setMetadata("title", title.getText());
				context.markChanged();
				dialogBox.hide();
			}
		});

		panel.add(title);
		panel.add(uploadButton);

		dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
		dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setWidget(panel);
		dialogBox.center();
		dialogBox.show();
	}
}
