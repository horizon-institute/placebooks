package placebooks.model.json;

import placebooks.model.PlaceBookBinder;

public class PlaceBookBinderSearchEntry extends PlaceBookBinderEntry
{
	private int score;
	
	private String ownerName;

	public PlaceBookBinderSearchEntry(final PlaceBookBinder p, final int score)
	{
		super(p);
		this.score = score;
		this.ownerName = p.getOwner().getName(); 
	}

	public int getScore()
	{
		return score;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
}
