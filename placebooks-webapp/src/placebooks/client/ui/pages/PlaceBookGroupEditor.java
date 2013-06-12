package placebooks.client.ui.pages;

import placebooks.client.AbstractAsyncCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.controllers.GroupController;
import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.items.ImageItem;
import placebooks.client.ui.pages.places.Group;
import placebooks.client.ui.views.PlaceBookShelf;
import placebooks.client.ui.views.PlaceBookToolbar;
import placebooks.client.ui.views.View;

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

public class PlaceBookGroupEditor extends Page implements View<Shelf>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookGroupEditor>
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

	public PlaceBookGroupEditor(final String id)
	{
		controller.load(id);
	}

	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle("Edit Group");

		shelf.setDeleteCallback(new AbstractAsyncCallback<PlaceBookEntry>()
		{
			@Override
			public void onSuccess(final PlaceBookEntry result)
			{
				controller.getItem().remove(result);
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
			final PlaceBookItemController itemController = new PlaceBookItemController(shelf.getGroup().getItem(),
					controller, true);
			imagePanel.setWidget(new ImageItem(itemController));
		}
		else
		{
			((ImageItem) imagePanel.getWidget()).itemChanged(shelf.getGroup().getItem());
		}

		if (PlaceBooks.getPlace() instanceof Group && shelf.getGroup().getId() != null)
		{
			final Group place = (Group) PlaceBooks.getPlace();
			if (!place.getId().equals(shelf.getGroup().getId()))
			{
				History.newItem(placebooks.client.PlaceBooks.getToken(new Group(shelf.getGroup().getId(),
						Group.Type.edit)), false);
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