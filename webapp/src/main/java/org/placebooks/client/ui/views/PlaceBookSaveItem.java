package org.placebooks.client.ui.views;

import org.placebooks.client.Resources;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.widgets.ToolbarItem;
import org.wornchaos.client.controllers.ControllerState;
import org.wornchaos.client.controllers.ControllerStateListener;

import com.google.gwt.core.client.GWT;

public class PlaceBookSaveItem extends ToolbarItem implements ControllerStateListener
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	@Override
	public void stateChanged(final ControllerState state)
	{
		switch (state)
		{
			case saved:
				setText(uiMessages.saved());
				hideImage();
				setEnabled(false);
				break;

			case not_saved:
				setText(uiMessages.save());
				hideImage();
				setEnabled(true);
				break;

			case saving:
				setText(uiMessages.saving());
				setImage(Resources.IMAGES.progress2());
				setEnabled(false);
				break;

			case save_error:
				setText(uiMessages.saveError());
				setImage(Resources.IMAGES.error());
				setEnabled(true);
				break;

			default:
				break;
		}
	}
}