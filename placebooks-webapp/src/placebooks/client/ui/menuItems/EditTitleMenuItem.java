package placebooks.client.ui.menuItems;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.dialogs.PlaceBookDialog;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public class EditTitleMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public EditTitleMenuItem(final DragController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.editTitle());
		this.item = item;
		this.controller = controller;
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
		final PlaceBookDialog dialogBox = new PlaceBookDialog()
		{
		};
		final TextBox title = new TextBox();
		title.setText(item.getItem().getMetadata("title", ""));
		final Button uploadButton = new Button(uiMessages.setTitle(), new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				item.getItem().setMetadata("title", title.getText());
				controller.markChanged();
				dialogBox.hide();
				item.updateFrame();
			}
		});

		panel.add(title);
		panel.add(uploadButton);

		dialogBox.setTitle(uiMessages.editTitle());
		dialogBox.setWidget(panel);
		dialogBox.show();

		title.setFocus(true);
	}
}
