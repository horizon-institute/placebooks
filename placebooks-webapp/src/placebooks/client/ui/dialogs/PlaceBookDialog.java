package placebooks.client.ui.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class PlaceBookDialog extends PopupPanel
{
	interface Style extends CssResource
	{
		String dialog();
	    String dialogGlass();
	}

	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookDialog.css")
		Style style();
	}
	
	private static final Bundle STYLES = GWT.create(Bundle.class);
	
	public PlaceBookDialog(final boolean autoHide)
	{
		STYLES.style().ensureInjected();
		
		setGlassEnabled(true);
		setAnimationEnabled(true);
		//setWidget(widget);

		setStyleName(STYLES.style().dialog());
		setGlassStyleName(STYLES.style().dialogGlass());
		setAutoHideEnabled(autoHide);

		//center();
	}
}
