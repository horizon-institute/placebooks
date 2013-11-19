package org.placebooks.client.ui.pages.views;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.controllers.GroupController;
import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.model.Entry;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.ui.items.ImageItem;
import org.placebooks.client.ui.pages.GroupPage;
import org.placebooks.client.ui.views.PlaceBookShelf;
import org.placebooks.client.ui.views.PlaceBookToolbar;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GroupEditor extends PageView implements View<Shelf>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, GroupEditor>
	{
	}

	// private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	TextBox title;

	@UiField
	TextArea description;

	@UiField
	SimplePanel imagePanel;
	
	@UiField
	PlaceBookToolbar toolbar;	

	@UiField
	PlaceBookShelf shelf;

	private Widget focussed = null;

	private final GroupController controller = new GroupController();

	public GroupEditor(final String id)
	{
		controller.load(id);
	}

	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle("Edit Group");

		shelf.setDeleteCallback(new AsyncCallback<Entry>()
		{
			@Override
			public void onSuccess(final Entry result)
			{
				controller.getItem().getEntries().remove(result);
				controller.markChanged();
			}
		});

		controller.add(this);
		controller.add(shelf);

		return library;
	}

	@Override
	public void itemChanged(final Shelf shelf)
	{
		if (!hasFocus(title.getElement()))
		{
			title.setText(shelf.getGroup().getTitle());
		}
		if (!hasFocus(description.getElement()))
		{
			description.setText(shelf.getGroup().getDescription());
		}

		if (imagePanel.getWidget() == null)
		{
			final ItemController itemController = new ItemController(shelf.getGroup().getImage(),
					controller, true);
			imagePanel.setWidget(new ImageItem(itemController));
		}
		else
		{
			((ImageItem) imagePanel.getWidget()).itemChanged(shelf.getGroup().getImage());
		}

		if (PlaceBooks.getPage() instanceof GroupPage && shelf.getGroup().getId() != null)
		{
			final GroupPage place = (GroupPage) PlaceBooks.getPage();
			if (!place.getId().equals(shelf.getGroup().getId()))
			{
				History.newItem(org.placebooks.client.PlaceBooks.getToken(new GroupPage(shelf.getGroup().getId(),
						GroupPage.Type.edit)), false);
			}
		}
	}

	@UiHandler({ "title", "description" })
	void edited(final KeyUpEvent event)
	{
		controller.getItem().getGroup().setTitle(title.getText());
		controller.getItem().getGroup().setDescription(description.getText());
		controller.markChanged();
	}

	protected native boolean hasFocus(Element element) /*-{
														return element.ownerDocument.activeElement == element;
														}-*/;
}