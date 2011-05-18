package placebooks.model;

import java.util.ArrayList;
import java.util.Collection;

public class PermitList extends Permissions
{
	private Collection<User> allowed = new ArrayList<User>();
	
	@Override
	public boolean canAccess(User owner, User user)
	{
		if(owner.getKey().equals(user))
		{
			return true;
		}
		for(User allowedUser: allowed)
		{
			if(allowedUser.getKey().equals(user.getKey()))
			{
				return true;
			}
		}
		return false;
	}

}
