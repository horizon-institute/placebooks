package placebooks.client.ui.dialogs;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
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
	
	public PlaceBookUploadDialog(final PlaceBookItemFrame item)
	{
		setWidget(uiBinder.createAndBindUi(this));
		
		if (item.getItem().is(ItemType.IMAGE))
		{
			setTitle("Upload Image");
			infoLabel.setText("Maximum Image File Size: 1Mb");
		}
		else if (item.getItem().is(ItemType.VIDEO))
		{
			setTitle("Upload Video");			
			infoLabel.setText("Maximum Video File Size: 25Mb");
		}
		else if (item.getItem().is(ItemType.AUDIO))
		{
			setTitle("Upload Audio");			
			infoLabel.setText("Maximum Audio File Size: 10Mb");
		}
		
		itemKey.setName("itemKey");
		itemKey.setValue(item.getItem().getKey());

		final String type = item.getItem().getClassName().substring(17, item.getItem().getClassName().length() - 4)
				.toLowerCase();
		upload.setName(type + "." + item.getItem().getKey());
		
		uploadButton.setEnabled(false);
		
		form.setAction(PlaceBookService.getHostURL() + "/placebooks/a/admin/add_item/upload");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitHandler(new SubmitHandler()
		{
			@Override
			public void onSubmit(final SubmitEvent event)
			{
				infoLabel.setVisible(true);
				infoLabel.setText("Uploading File...");
				uploadButton.setEnabled(false);
				GWT.log("Uploading File");
			}
		});
		form.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event)
			{
				GWT.log("Upload Complete: " + event.getResults());
				GWT.log("Upload Complete: " + event.toDebugString());
				item.getItemWidget().refresh();
				item.getRootPanel().getElement().getStyle().setOpacity(0.5);
				item.getRootPanel().getElement().getStyle().setBackgroundColor("#000");
				PlaceBookService.getPlaceBookItem(item.getItem().getKey(), new AbstractCallback()
				{
					@Override
					public void failure(final Request request, final Response response)
					{
						infoLabel.setText("Upload Failed");
						final PlaceBookItem placebookItem = PlaceBookService.parse(PlaceBookItem.class, response.getText());
						item.getRootPanel().getElement().getStyle().clearOpacity();
						item.getRootPanel().getElement().getStyle().clearBackgroundColor();
						item.getItemWidget().update(placebookItem);
					}

					@Override
					public void success(final Request request, final Response response)
					{
						final PlaceBookItem placebookItem = PlaceBookService.parse(PlaceBookItem.class, response.getText());
						item.getRootPanel().getElement().getStyle().clearOpacity();
						item.getRootPanel().getElement().getStyle().clearBackgroundColor();
						item.getItemWidget().update(placebookItem);
						hide();
					}
				});
			}
		});

	}
	
	@UiHandler("uploadButton")
	void upload(ClickEvent event)
	{
		form.submit();
	}
	
	@UiHandler("upload")
	void fileChanged(final ChangeEvent event)
	{
		uploadButton.setEnabled(true);
	}
}
