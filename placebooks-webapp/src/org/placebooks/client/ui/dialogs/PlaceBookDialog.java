package org.placebooks.client.ui.dialogs;

import com.google.gwt.user.client.ui.Widget;

public abstract class PlaceBookDialog
{
	private PlaceBookDialogFrame dialog = new PlaceBookDialogFrame();

	public PlaceBookDialog()
	{
	}

	public void center()
	{
		dialog.center();
	}

	public void hide()
	{
		dialog.hide();
	}

	public boolean isShowing()
	{
		return dialog.isShowing();
	}

	public void setAutoHide(final boolean autoHide)
	{
		dialog.setAutoHideEnabled(autoHide);
	}

	public void setError(final String error)
	{
		dialog.setError(error);
	}

	public void setProgressVisible(final boolean visible, final String text)
	{
		dialog.setProgressVisible(visible, text);
		dialog.setAutoHideEnabled(!visible);
	}

	public void setTitle(final String title)
	{
		dialog.setTitle(title);
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
}