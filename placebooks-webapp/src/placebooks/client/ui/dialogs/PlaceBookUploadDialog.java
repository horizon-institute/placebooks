package placebooks.client.ui.dialogs;

import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.DataStore;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.ServerInfo;
import placebooks.client.model.ServerInfoDataStore;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.elements.PlaceBookController;
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
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookUploadDialog extends PlaceBookDialog
{

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

	private final DataStore<ServerInfo> infoStore = new ServerInfoDataStore();
	
	private final PlaceBookItemWidget item;

	public PlaceBookUploadDialog(final PlaceBookController controller, final PlaceBookItemWidget item)
	{
		setWidget(uiBinder.createAndBindUi(this));

		this.item = item;
		
		setTitle("Upload");
		
		infoStore.get(null, new JSONResponse<ServerInfo>()
		{
			@Override
			public void handleResponse(ServerInfo object)
			{
				if (item.getItem().is(ItemType.IMAGE))
				{
					setTitle("Upload Image");
					infoLabel.setText("Maximum Image File Size: " + object.getImageSize() + "Mb");
				}
				else if (item.getItem().is(ItemType.VIDEO))
				{
					setTitle("Upload Video");
					infoLabel.setText("Maximum Video File Size: " + object.getVideoSize() + "Mb");
				}
				else if (item.getItem().is(ItemType.AUDIO))
				{
					setTitle("Upload Audio");
					infoLabel.setText("Maximum Audio File Size: " + object.getAudioSize() + "Mb");
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
		form.addSubmitHandler(new SubmitHandler()
		{
			@Override
			public void onSubmit(final SubmitEvent event)
			{
				setProgressVisible(true, "Uploading File...");
				uploadButton.setEnabled(false);
				GWT.log("Uploading " + item.getItem().getKey());
			}
		});
		form.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event)
			{
				try
				{
					final String result = event.getResults().replaceAll("(<([^>]+)>)", "");
					GWT.log("Upload Complete: " + result);
					setProgressVisible(false, null);

					final PlaceBookItem placebookItem = PlaceBookService.parse(PlaceBookItem.class, result);
					item.getItem().removeParameter("height");
					item.getItem().setParameter("uploadResize", 1);
					item.update(placebookItem);
					hide();
					controller.markChanged();
				}
				catch (final Exception e)
				{
					setError("Upload Failed");
					refresh();
				}
			}
		});

		refresh();
	}

	@UiHandler("upload")
	void fileChanged(final ChangeEvent event)
	{
		refresh();
	}

	@UiHandler("uploadButton")
	void upload(final ClickEvent event)
	{
		itemKey.setValue(item.getItem().getKey());
		form.submit();
	}

	private void refresh()
	{
		uploadButton.setEnabled(item.getItem().getKey() != null && upload.getFilename() != null
				&& !"Unknown".equals(upload.getFilename()));
	}
}
