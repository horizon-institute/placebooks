package placebooks.model;


public class PermitPublic extends Permissions
{
	@Override
	public boolean canAccess(final User owner, final User user)
	{
		return true;
	}
}
