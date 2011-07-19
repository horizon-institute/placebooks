/**
 * 
 */
package placebooks.model;

/**
 * @author pszmp
 * Interface for external content that is updateable from a remote source in this
 * system the remote source is considered canonical in that any updates overwrite the 
 * exiting item - however these items will never be added to a placebook, only copied to 
 * new items.  Therefore this updates the palate only.
 */
public interface IUpdateableExternal
{
	public  String getExternalID();
	
	/**
	 * Implement this to allow the items to be updated and saved properly
	 */
	public abstract IUpdateableExternal saveUpdatedItem();
	
	public void update(IUpdateableExternal updateItem);
}
