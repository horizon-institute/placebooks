package placebooks.client.ui.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Widget;

public abstract class PlaceBookDialog
{
	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookDialog.css")
		Style style();
	}

	interface Style extends CssResource
	{
		String dialog();

		String dialogGlass();
	}

	private static final Bundle STYLES = GWT.create(Bundle.class);

	private PlaceBookDialogFrame dialog = new PlaceBookDialogFrame();

	public PlaceBookDialog()
	{
		STYLES.style().ensureInjected();

		dialog.setGlassEnabled(true);
		dialog.setAnimationEnabled(true);

		dialog.setStyleName(STYLES.style().dialog());
		dialog.setGlassStyleName(STYLES.style().dialogGlass());
		dialog.setAutoHideEnabled(true);		
	}
	
	public void setWidget(final Widget widget)
	{
		dialog.setContent(widget);
	}
	
	public void show()
	{
		dialog.show();
		dialog.center();
	}
	
	public void hide()
	{
		dialog.hide();
	}
	
	public void setTitle(final String title)
	{
		dialog.setTitle(title);
	}
	
	public void setError(final String error)
	{
		dialog.setError(error);
	}	
	
	public void center()
	{
		dialog.center();		
	}
}