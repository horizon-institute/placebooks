package org.placebooks.client.ui.pages.views;

import org.placebooks.client.controllers.UserController;
import org.placebooks.client.model.Group;
import org.placebooks.client.model.User;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.views.PlaceBookGroupWidget;
import org.placebooks.client.ui.views.PlaceBookToolbar;
import org.wornchaos.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GroupsView extends PageView implements View<User>
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, GroupsView>
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

		if(item != null && item.getGroups() != null)
		{
			for (final Group group : item.getGroups())
			{
				shelf.add(new PlaceBookGroupWidget(group, null));
			}
		}

		shelf.add(new PlaceBookGroupWidget());
	}
}