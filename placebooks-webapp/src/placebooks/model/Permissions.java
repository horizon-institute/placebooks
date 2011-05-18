package placebooks.model;


public abstract class Permissions
{
	public abstract boolean canAccess(final User owner, final User user);
}
