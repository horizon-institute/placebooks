package placebooks.model;

public class PermitFriends extends Permissions
{
	@Override
	public boolean canAccess(final User owner, final User user)
	{
		if(owner.getKey().equals(user))
		{
			return true;
		}
		for(User friend: owner.getFriends())
		{
			if(friend.getKey().equals(user.getKey()))
			{
				return true;
			}
		}
		return false;
	}
}
