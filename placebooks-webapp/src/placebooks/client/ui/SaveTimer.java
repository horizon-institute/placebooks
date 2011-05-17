package placebooks.client.ui;

import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

public class SaveTimer extends Timer
{
	private static final int saveDelay = 2000;
	
	private PlaceBook placebook;

	public void setPlaceBook(PlaceBook placebook)
	{
		cancel();
		this.placebook = placebook;
	}
	
	@Override
	public void run()
	{
		GWT.log("Timer run");
		// savingPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		PlaceBookService.savePlaceBook(placebook, new RequestCallback()
		{
			@Override
			public void onError(final Request arg0, final Throwable arg1)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onResponseReceived(final Request request, final Response response)
			{
				GWT.log("Response Code: " + response.getStatusCode());
				GWT.log(response.getText());
				// savingPanel.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
	}
	
	public void markChanged()
	{
		cancel();
		schedule(saveDelay);
	}
}
