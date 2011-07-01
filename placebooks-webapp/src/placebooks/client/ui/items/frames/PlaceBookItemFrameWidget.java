package placebooks.client.ui.items.frames;

import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PlaceBookItemFrameWidget extends PlaceBookItemFrame
{
	interface PlaceBookItemFrameWidgetUiBinder extends UiBinder<Widget, PlaceBookItemFrameWidget>
	{
	}

	private static final PlaceBookItemFrameWidgetUiBinder uiBinder = GWT.create(PlaceBookItemFrameWidgetUiBinder.class);

	@UiField
	MousePanel dragSection;

	@UiField
	MousePanel frame;

	@UiField
	Panel menuButton;

	@UiField
	MousePanel resizeSection;
	
	public PlaceBookItemFrameWidget()
	{
		super();

		uiBinder.createAndBindUi(this);
	}
}