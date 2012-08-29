package placebooks.client.ui.dialogs;

import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.DataStore;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.ServerInfo;
import placebooks.client.model.ServerInfoDataStore;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookSaveItem.SaveState;
import placebooks.client.ui.elements.PlaceBookSaveStateListener;
import placebooks.client.ui.items.PlaceBookItemWidget;

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

public class PlaceBookUploadDialog extends PlaceBookDialog implements PlaceBookSaveStateListener
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	interface UploadDialogUiBinder extends UiBinder<Widget, PlaceBookUploadDialog>
	{
	}

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
	
	private final DataStore<ServerInfo> infoStore = new ServerInfoDataStore();
	
	private final PlaceBookItemWidget item;

	private final PlaceBookController controller;
	
	public PlaceBookUploadDialog(final PlaceBookController controller, final PlaceBookItemWidget item)
	{
		setWidget(uiBinder.createAndBindUi(this));

		this.item = item;
		this.controller = controller;
		
		setTitle(uiMessages.upload());
		
		infoStore.get(null, new JSONResponse<ServerInfo>()
		{
			@Override
			public void handleResponse(ServerInfo object)
			{
				String type = null;
				int size = 0;
				if (item.getItem().is(ItemType.IMAGE))
				{
					type = uiMessages.image();
					size = object.getImageSize();
				}
				else if (item.getItem().is(ItemType.VIDEO))
				{
					type = uiMessages.video();
					size = object.getVideoSize();
				}
				else if (item.getItem().is(ItemType.AUDIO))
				{
					type = uiMessages.audio();
					size = object.getAudioSize();
				}
				if(type != null)
				{
					setTitle(uiMessages.upload(type));
					infoLabel.setText(uiMessages.maxSize(type, size));
				}
			}
		});

		itemKey.setName("itemKey");

		final String type = item.getItem().getClassName().substring(17, item.getItem().getClassName().length() - 4)
				.toLowerCase();
		upload.setName(type + "." + item.getItem().getKey());

		form.setAction(PlaceBookService.getHostURL() + "/placebooks/a/admin/add_item/upload");
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
					GWT.log("Upload Complete: " + result);
					setUploadState(false);

					final PlaceBookItem placebookItem = PlaceBookService.parse(PlaceBookItem.class, result);
					item.getItem().removeParameter("height");
					item.getItem().setParameter("uploadResize", 1);
					item.update(placebookItem);
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

	private void setUploadState(final boolean uploading)
	{
		this.uploading = uploading;
		if(uploading)
		{
			setProgressVisible(true, uiMessages.uploading());
			uploadButton.setEnabled(false);
			GWT.log("Uploading " + item.getItem().getKey());
		}
		else
		{
			setProgressVisible(false, null);			
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
		if(item.getItem().getKey() == null) 
		{
			controller.getSaveItem().add(this);			
			if(controller.getSaveItem().getState() == SaveState.saved)
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
		uploadButton.setEnabled(upload.getFilename() != null
				&& !"Unknown".equals(upload.getFilename()));
	}

	@Override
	public void saveStateChanged(SaveState state)
	{
		if(uploading && state == SaveState.saved)
		{
			controller.getSaveItem().remove(this);
			itemKey.setValue(item.getItem().getKey());	
			form.submit();
		}
	}
}