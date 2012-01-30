package placebooks.model.json;

import java.util.Collection;

import placebooks.model.PlaceBookBinder;
import placebooks.model.User;

public class UserShelf extends Shelf
{
	private User user;

	public UserShelf(final Collection<PlaceBookBinder> pbs, final User user)
	{
		super(pbs.toArray(new PlaceBookBinder[0]));
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
}

