package placebooks.client.ui.dialogs;

import org.wornchaos.client.logger.Log;
import org.wornchaos.controllers.SomethingController;
import org.wornchaos.controllers.ControllerState;
import org.wornchaos.controllers.ControllerStateListener;
import org.wornchaos.views.View;

import placebooks.client.PlaceBooks;
import placebooks.client.controllers.ServerInfoController;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.model.ServerInfo;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.items.PlaceBookItemView;

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

		itemKey.setName("itemKey");

		final String type = item.getItem().getClassName().substring(17, item.getItem().getClassName().length() - 4)
				.toLowerCase();
		upload.setName(type + "." + item.getItem().getKey());

		form.setAction(PlaceBooks.getServer().getHostURL() + "placebooks/a/admin/add_item/upload");
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
					setUploadState(false);

					final PlaceBookItem placebookItem = PlaceBooks.getServer().parse(PlaceBookItem.class, result);
					item.getItem().removeParameter("height");
					item.getItem().setParameter("uploadResize", 1);
					item.getItem().setKey(placebookItem.getKey());
					item.getItem().removeMetadata("tempID");
					placebookItem.removeMetadata("tempID");

					if (placebookItem.getHash() != null)
					{
						item.getItem().setHash(placebookItem.getHash());
						item.getItem().setSourceURL(placebookItem.getSourceURL());
					}
					item.itemChanged(item.getItem());
					hide();
					controller.markChanged();
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
		if (item.getItem().is(ItemType.IMAGE))
		{
			type = uiMessages.image();
			size = value.getImageSize();
		}
		else if (item.getItem().is(ItemType.VIDEO))
		{
			type = uiMessages.video();
			size = value.getVideoSize();
		}
		else if (item.getItem().is(ItemType.AUDIO))
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
			itemKey.setValue(item.getItem().getKey());
			form.submit();
		}
	}

	@UiHandler("upload")
	void fileChanged(final ChangeEvent event)
	{
		refresh();
	}

	@UiHandler("uploadButton")
	void upload(final ClickEvent event)
	{
		setUploadState(true);
		if (item.getItem().getKey() == null)
		{
			controller.add(this);
			if (controller.getState() == ControllerState.saved)
			{
				controller.markChanged();
			}
		}
		else
		{
			itemKey.setValue(item.getItem().getKey());
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
			Log.info("Uploading " + item.getItem().getKey());
		}
		else
		{
			setProgressVisible(false, null);
		}
	}
}