package placebooks.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class PlaceBookToolbarItem extends FlowPanel
{
	private final InlineLabel label = new InlineLabel();
	private final Image image = new Image();

	public PlaceBookToolbarItem(final String text)
	{
		add(label);
		label.setText(text);
	}
	
}
