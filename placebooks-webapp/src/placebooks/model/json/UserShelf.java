package placebooks.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import placebooks.model.PlaceBookBinder;
import placebooks.model.User;

public class UserShelf extends Shelf
{
	private User user;

	public UserShelf(final Collection<PlaceBookBinder> pbs, final User user)
	{
		super();
		this.user = user;
		
		final List<ShelfEntry> entries = new ArrayList<ShelfEntry>();
		for (final PlaceBookBinder pb : pbs)
		{
			if(pb.getOwner().getEmail().equals(user.getEmail()))
			{
				entries.add(new PlaceBookBinderEntry(pb));				
			}
			else
			{
				entries.add(new PlaceBookBinderOwnedEntry(pb));
			}
		}
		setEntries(entries);
	}
	
	public User getUser()
	{
		return user;
	}
}

