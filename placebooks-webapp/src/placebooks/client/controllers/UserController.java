package placebooks.client.controllers;

import org.wornchaos.client.controller.CachedController;
import org.wornchaos.client.parser.JavaScriptObjectParser;

import placebooks.client.PlaceBooks;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserController extends CachedController<User>
{
	private static final UserController instance = new UserController();

	public static UserController getController()
	{
		return instance;
	}

	public static User getUser()
	{
		return instance.getItem();
	}

	public UserController()
	{
		super(new JavaScriptObjectParser<User>(), "user");
	}

	@Override
	public void onFailure(final Throwable caught)
	{
		super.onFailure(caught);
		setItem(null);
	}

	@Override
	public void setItem(User value)
	{
		super.setItem(value);
	}

	@Override
	protected void load(final String id, final AsyncCallback<User> callback)
	{
		PlaceBooks.getServer().getUser(callback);
	}

	public void login(String username, String password, final AsyncCallback<Shelf> callback)
	{
		PlaceBooks.getServer().login(username, password,
										new AsyncCallback<Shelf>()
										{
											@Override
											public void onFailure(final Throwable caught)
											{
												callback.onFailure(caught);
											}

											@Override
											public void onSuccess(final Shelf shelf)
											{
												callback.onSuccess(shelf);
												setItem(shelf.getUser());
											}
										});		
	}
	
	
	public void logout(final RequestCallback callback)
	{
		PlaceBooks.getServer().logout(new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				setItem(null);		
				callback.onResponseReceived(request, response);
			}
			
			@Override
			public void onError(Request request, Throwable exception)
			{
				callback.onError(request, exception);
			}
		});		
	}
}
