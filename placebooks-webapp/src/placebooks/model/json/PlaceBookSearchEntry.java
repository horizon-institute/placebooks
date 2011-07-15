package placebooks.model.json;

import placebooks.model.PlaceBook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlaceBookSearchEntry extends PlaceBookEntry
{
	@JsonProperty	
	private int score;

	public PlaceBookSearchEntry(final PlaceBook p, final int score)
	{
		super(p);
		this.score = score;
	}

	public int getScore()
	{
		return score;
	}
}
