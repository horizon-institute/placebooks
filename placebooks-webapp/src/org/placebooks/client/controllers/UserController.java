package org.placebooks.client.controllers;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Shelf;
import org.placebooks.client.model.User;
import org.wornchaos.client.controllers.ControllerBase;
import org.wornchaos.client.server.AsyncCallback;

public class UserController extends ControllerBase<User>
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

	private final AsyncCallback<User> callback = new AsyncCallback<User>()
	{
		@Override
		public void onFailure(final Throwable caught)
		{
			setItem(null);
		}

		@Override
		public void onSuccess(final User user)
		{
			setItem(user);
		}
	};

	@Override
	public void load(final String id)
	{
		PlaceBooks.getServer().getUser(getCallback());
	}

	public void login(final String username, final String password, final AsyncCallback<Shelf> callback)
	{
		PlaceBooks.getServer().login(username, password, true, new AsyncCallback<Shelf>()
		{

			@Override
			public void onSuccess(final Shelf shelf)
			{
				setItem(shelf.getUser());
				callback.onSuccess(shelf);
			}
		});
	}

	public void logout(final AsyncCallback<String> callback)
	{
		PlaceBooks.getServer().logout(new AsyncCallback<String>()
		{
			@Override
			public void onSuccess(final String value)
			{
				setItem(null);
				callback.onSuccess(value);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				setItem(null);
				super.onFailure(caught);
			}
			
			
		});
	}

	@Override
	public void refresh()
	{
		load(null);
	}

	@Override
	public void setItem(final User user)
	{
		super.setItem(user);
	}

	@Override
	protected AsyncCallback<User> getCallback()
	{
		return callback;
	}
}