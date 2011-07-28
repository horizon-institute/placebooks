package placebooks.client.ui.menuItems;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

public class UploadMenuItem extends MenuItem
{
	private final PlaceBookItemFrame item;

	public UploadMenuItem(final PlaceBookItemFrame item)
	{
		super("Upload");

		this.item = item;
	}

	@Override
	public void run()
	{
		final Panel panel = new FlowPanel();
		final FormPanel form = new FormPanel();
		final FileUpload upload = new FileUpload();
		final Hidden hidden = new Hidden("itemKey", item.getItem().getKey());
		final PopupPanel dialogBox = new PopupPanel(true, true);
		final String type = item.getItem().getClassName().substring(17, item.getItem().getClassName().length() - 4)
				.toLowerCase();
		upload.setName(type + "." + item.getItem().getKey());

		final Button uploadButton = new Button("Upload", new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				form.submit();
			}
		});

		form.setAction(GWT.getHostPageBaseURL() + "/placebooks/a/admin/add_item/upload");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(panel);
		form.addSubmitHandler(new SubmitHandler()
		{
			@Override
			public void onSubmit(final SubmitEvent event)
			{
				GWT.log("Uploading File");

			}
		});
		form.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event)
			{
				GWT.log("Upload Complete: " + event.getResults());
				item.getItemWidget().refresh();
				PlaceBookService.getPlaceBookItem(item.getItem().getKey(), new AbstractCallback()
				{
					@Override
					public void success(final Request request, final Response response)
					{
						final PlaceBookItem placebookItem = PlaceBookItem.parse(response.getText());
						item.getItemWidget().update(placebookItem);
						dialogBox.hide();						
					}
				});
			}
		});

		panel.add(upload);
		panel.add(hidden);
		panel.add(uploadButton);

		dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
		dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setWidget(form);
		dialogBox.center();
		dialogBox.show();
	}
}
