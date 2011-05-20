package placebooks.model;


public class PermitPrivate extends Permissions
{
	@Override
	public boolean canAccess(final User owner, final User user)
	{
		return owner.getKey().equals(user.getKey());
	}
}
