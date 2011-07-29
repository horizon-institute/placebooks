package placebooks.model.json;

import placebooks.model.PlaceBook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlaceBookSearchEntry extends PlaceBookEntry
{
	@JsonProperty	
	private int score;
	
	private String ownerName;

	public PlaceBookSearchEntry(final PlaceBook p, final int score)
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
