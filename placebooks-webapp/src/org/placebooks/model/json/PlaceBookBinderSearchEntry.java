package org.placebooks.model.json;

import org.placebooks.model.PlaceBookBinder;

public class PlaceBookBinderSearchEntry extends PlaceBookBinderOwnedEntry
{
	private int score;

	public PlaceBookBinderSearchEntry(final PlaceBookBinder p, final int score)
	{
		super(p);
		this.score = score;
	}

	public int getScore()
	{
		return score;
	}
}
