package placebooks.client.ui.dialogs;

import com.google.gwt.user.client.ui.Widget;

public abstract class PlaceBookDialog
{
	private PlaceBookDialogFrame dialog = new PlaceBookDialogFrame();

	public PlaceBookDialog()
	{	
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