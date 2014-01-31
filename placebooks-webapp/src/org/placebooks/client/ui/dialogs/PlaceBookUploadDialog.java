package org.placebooks.client.ui.dialogs;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.controllers.ServerInfoController;
import org.placebooks.client.model.ServerInfo;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.items.PlaceBookItemView;
import org.wornchaos.client.controllers.ControllerState;
import org.wornchaos.client.controllers.ControllerStateListener;
import org.wornchaos.client.controllers.SomethingController;
import org.wornchaos.logger.Log;
import org.wornchaos.views.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookUploadDialog extends PlaceBookDialog implements ControllerStateListener, View<ServerInfo>
{
	interface UploadDialogUiBinder extends UiBinder<Widget, PlaceBookUploadDialog>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static UploadDialogUiBinder uiBinder = GWT.create(UploadDialogUiBinder.class);

	@UiField
	Label infoLabel;

	@UiField
	FormPanel form;

	@UiField
	Hidden itemKey;

	@UiField
	Hidden type;
	
	@UiField
	FileUpload upload;

	@UiField
	Button uploadButton;

	@UiField
	TextArea copyright;

	private boolean uploading = false;

	private final PlaceBookItemView item;

	private final SomethingController<?> controller;

	public PlaceBookUploadDialog(final SomethingController<?> controller, final PlaceBookItemView item)
	{
		setWidget(uiBinder.createAndBindUi(this));

		this.item = item;
		this.controller = controller;

		setTitle(uiMessages.upload());

		ServerInfoController.getController().add(this);

		type.setValue("test");
		
		if(item != null && item.getItem() != null && item.getItem().getType() != null)
		{
			type.setValue(item.getItem().getType().name());
		}

		form.setAction(PlaceBooks.getServer().getHostURL() + "upload_item");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event)
			{
				try
				{
					final String result = event.getResults().replaceAll("(<([^>]+)>)", "");
					Log.info("Upload Complete: " + result);
					
					if(result.startsWith("Error"))
					{
						String[] lines = result.split("\\n");
						setUploadState(false);						
						setError(lines[0]);
					}
					else if(result == null || result.trim().isEmpty())
					{
						setUploadState(false);
						
						item.getItem().setHash(null);
						item.refresh();
						item.getController().markChanged();
						hide();						
					}
					else
					{
						setUploadState(false);
	
						item.getItem().setHash(result);
						item.refresh();
						item.getController().markChanged();
						hide();
					}
				}
				catch (final Exception e)
				{
					setError(uiMessages.uploadFailed());
					refresh();
				}
			}
		});

		refresh();
	}

	@Override
	public void itemChanged(final ServerInfo value)
	{
		String type = null;
		int size = 0;
		if (item.getItem().is(Type.ImageItem))
		{
			type = uiMessages.image();
			size = value.getImageSize();
		}
		else if (item.getItem().is(Type.VideoItem))
		{
			type = uiMessages.video();
			size = value.getVideoSize();
		}
		else if (item.getItem().is(Type.AudioItem))
		{
			type = uiMessages.audio();
			size = value.getAudioSize();
		}
		if (type != null)
		{
			setTitle(uiMessages.upload(type));
			infoLabel.setText(uiMessages.maxSize(type, size));
		}
	}

	@Override
	public void stateChanged(final ControllerState state)
	{
		if (uploading && state == ControllerState.saved)
		{
			controller.remove(this);
			itemKey.setValue(item.getItem().getId());
			form.submit();
		}
	}

	@UiHandler("upload")
	void fileChanged(final ChangeEvent event)
	{
		setError(null);
		refresh();
	}

	@UiHandler("uploadButton")
	void upload(final ClickEvent event)
	{
		setUploadState(true);
		if (item.getItem().getId() == null)
		{
			controller.add(this);
			if (controller.getState() == ControllerState.saved)
			{
				controller.markChanged();
			}
		}
		else
		{
			itemKey.setValue(item.getItem().getId());
			form.submit();
		}
	}

	private void refresh()
	{
		uploadButton.setEnabled(upload.getFilename() != null && !"Unknown".equals(upload.getFilename()));
	}

	private void setUploadState(final boolean uploading)
	{
		this.uploading = uploading;
		if (uploading)
		{
			setProgressVisible(true, uiMessages.uploading());
			uploadButton.setEnabled(false);
			Log.info("Uploading " + item.getItem().getId());
		}
		else
		{
			setProgressVisible(false, null);
		}
	}
}