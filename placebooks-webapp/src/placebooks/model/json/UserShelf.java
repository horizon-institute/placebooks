package placebooks.model.json;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonProperty;

import placebooks.model.PlaceBook;
import placebooks.model.User;

public class UserShelf extends Shelf
{
	@JsonProperty
	private User user;

	public UserShelf(final Collection<PlaceBook> pbs, final User user)
	{
		super(pbs.toArray(new PlaceBook[0]));
		this.user = user;
	}
	
	public User getUser()
	{
		return user;
	}
}

