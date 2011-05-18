package placebooks.client.ui;

import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

public class SaveTimer extends Timer
{
	private static final int saveDelay = 2000;

	private PlaceBook placebook;

	public void markChanged()
	{
		cancel();
		schedule(saveDelay);
	}

	@Override
	public void run()
	{
		GWT.log("Timer run");
		// savingPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		PlaceBookService.savePlaceBook(placebook, new RequestCallback()
		{
			@Override
			public void onError(final Request request, final Throwable exception)
			{
				GWT.log(exception.getMessage(), exception);
			}

			@Override
			public void onResponseReceived(final Request request, final Response response)
			{
				GWT.log(response.getStatusCode() + " Response: " + response.getText());
				final PlaceBook result = PlaceBook.parse(response.getText());
				if (result != null)
				{
					placebook.setKey(result.getKey());
				}
				
				for(int index = 0; index < placebook.getItems().length(); index++)
				{
					PlaceBookItem item = placebook.getItems().get(index);
					if(item.getKey() == null)
					{
						for(int rindex = 0; rindex < result.getItems().length(); rindex++)
						{
							PlaceBookItem ritem = result.getItems().get(rindex);
							if(item.getParameter("panel") == ritem.getParameter("panel") && item.getParameter("order") == ritem.getParameter("order"))
							{
								item.setKey(ritem.getKey());
							}
						}
					}
				}
			}
		});
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		cancel();
		this.placebook = placebook;
	}
}
