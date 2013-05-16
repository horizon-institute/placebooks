package placebooks.client.ui;

import org.wornchaos.client.ui.View;

import placebooks.client.controllers.GroupController;
import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookShelf;
import placebooks.client.ui.items.ImageItem;
import placebooks.client.ui.places.PlaceBook;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class KioskPlaceBookGroupView extends PlaceBookPage implements View<Shelf>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, KioskPlaceBookGroupView>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	Label title;

	@UiField
	Label description;

	@UiField
	SimplePanel imagePanel;

	@UiField
	PlaceBookShelf shelf;

	private final GroupController controller = new GroupController();

	public KioskPlaceBookGroupView(final String id)
	{
		controller.load(id);
	}

	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooksLibrary());

		controller.add(this);
		controller.add(shelf);

		return library;
	}

	@Override
	public void itemChanged(final Shelf shelf)
	{
		title.setText(shelf.getGroup().getTitle());
		description.setText(shelf.getGroup().getDescription());

		this.shelf.setType(PlaceBook.Type.kiosk);
		
		if (imagePanel.getWidget() == null)
		{
			final PlaceBookItemController itemController = new PlaceBookItemController(shelf.getGroup().getItem(),
					controller);
			imagePanel.setWidget(new ImageItem(itemController));
		}
		else
		{
			((ImageItem) imagePanel.getWidget()).itemChanged(shelf.getGroup().getItem());
		}
	}
}