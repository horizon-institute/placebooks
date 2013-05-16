package placebooks.client.controllers;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.parser.JavaScriptObjectParser;

import placebooks.client.PlaceBooks;
import placebooks.client.model.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserController extends AbstractReadOnlyController<User>
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
	protected void load(final String id, final AsyncCallback<User> callback)
	{
		PlaceBooks.getServer().getUser(callback);
	}
}
