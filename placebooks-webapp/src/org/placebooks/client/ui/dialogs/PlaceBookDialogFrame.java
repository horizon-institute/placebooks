package org.placebooks.client.ui.dialogs;

import org.placebooks.client.ui.widgets.ProgressPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

class PlaceBookDialogFrame extends PopupPanel
{
	interface DialogStyle extends CssResource
	{
		String dialog();

		String dialogGlass();
	}

	interface PlaceBookDialogUiBinder extends UiBinder<Widget, PlaceBookDialogFrame>
	{
	}

	private static PlaceBookDialogUiBinder uiBinder = GWT.create(PlaceBookDialogUiBinder.class);

	@UiField
	ProgressPanel progress;

	@UiField
	DialogStyle style;

	@UiField
	Label titleLabel;

	@UiField
	Label errorLabel;

	@UiField
	Panel content;

	PlaceBookDialogFrame()
	{
		setWidget(uiBinder.createAndBindUi(this));

		setGlassEnabled(true);
		setAnimationEnabled(true);

		setStyleName(style.dialog());
		setGlassStyleName(style.dialogGlass());
		setAutoHideEnabled(true);

		errorLabel.setVisible(false);
	}

	public void setProgressVisible(final boolean visible, final String text)
	{
		progress.setVisible(visible);
		progress.setText(text);
	}

	@Override
	public void setTitle(final String title)
	{
		titleLabel.setText(title);
	}

	void setContent(final Widget contentWidget)
	{
		content.clear();
		content.add(contentWidget);
	}

	void setError(final String error)
	{
		errorLabel.setText(error);
		errorLabel.setVisible(error != null);
	}
}
