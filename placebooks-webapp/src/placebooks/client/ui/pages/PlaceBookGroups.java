package placebooks.client.ui.pages;

import placebooks.client.AbstractAsyncCallback;
import placebooks.client.controllers.UserController;
import placebooks.client.model.PlaceBookGroup;
import placebooks.client.model.User;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.views.PlaceBookGroupWidget;
import placebooks.client.ui.views.PlaceBookToolbar;
import placebooks.client.ui.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookGroups extends Page implements View<User>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookGroups>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	FlowPanel shelf;

	@UiField
	PlaceBookToolbar toolbar;
	
	@Override
	public Widget createView()
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle(uiMessages.placebooksLibrary());

		UserController.getController().add(this);
		UserController.getController().refresh();

		return library;
	}

	@Override
	public void itemChanged(final User item)
	{
		shelf.clear();

		for (final PlaceBookGroup group : item.getGroups())
		{
			shelf.add(new PlaceBookGroupWidget(group, new AbstractAsyncCallback<PlaceBookGroup>()
			{
				@Override
				public void onSuccess(final PlaceBookGroup response)
				{
					// TODO Auto-generated method stub

				}
			}));
		}

		shelf.add(new PlaceBookGroupWidget());
	}
}