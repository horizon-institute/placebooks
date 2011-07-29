package placebooks.client.ui;

import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookToolbarLogin.ShelfListener;
import placebooks.client.ui.places.PlaceBookBrowsePlace;
import placebooks.client.ui.places.PlaceBookEditorNewPlace;
import placebooks.client.ui.places.PlaceBookHomePlace;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookToolbar extends FlowPanel
{
	private final PlaceBookToolbarLogin login = new PlaceBookToolbarLogin();
	private PlaceController placeController;

	private final PlaceBookToolbarItem homeItem = new PlaceBookToolbarItem("HOME", null, new ClickHandler()
	{
		@Override
		public void onClick(final ClickEvent event)
		{
			placeController.goTo(new PlaceBookHomePlace(login.getShelf()));

		}
	});
	
	private final PlaceBookToolbarItem createItem = new PlaceBookToolbarItem("CREATE", Resources.INSTANCE.add(), new ClickHandler()
	{
		@Override
		public void onClick(final ClickEvent event)
		{
			placeController.goTo(new PlaceBookEditorNewPlace(login.getShelf().getUser()));
		}
	});
	
	private final PlaceBookToolbarItem libraryItem = new PlaceBookToolbarItem("MY LIBRARY", Resources.INSTANCE.book(), new ClickHandler()
	{

		@Override
		public void onClick(final ClickEvent event)
		{
			placeController.goTo(new PlaceBookBrowsePlace(login.getShelf()));
		}
	});
	
	public PlaceBookToolbar()
	{
		super();
		setStyleName(Resources.INSTANCE.style().toolbar());

		add(homeItem);
		add(createItem);
		add(libraryItem);
		add(login);
		
		createItem.setEnabled(false);
		libraryItem.setEnabled(false);
	}

	public PlaceController getPlaceController()
	{
		return placeController;
	}
	
	public PlaceBookToolbarLogin getLogin()
	{
		return login;
	}
	
	public void setUser(final User user)
	{
		login.setUser(user);
		createItem.setEnabled(user != null);
		libraryItem.setEnabled(user != null);		
	}

	public void setPlaceController(final PlaceController placeController)
	{
		login.setPlaceController(placeController);
		this.placeController = placeController;
	}

	public void setShelf(final Shelf shelf)
	{
		login.setShelf(shelf);
	}

	public void setShelfListener(final ShelfListener shelfListener)
	{
		login.setShelfListener(shelfListener);
	}
}
