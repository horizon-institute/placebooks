package org.placebooks.services.model;


public class PeoplesCollectionTrailCentroid
{
	protected String id;
	protected double x;
	protected double y;

	public PeoplesCollectionTrailCentroid()
	{
	}

	public PeoplesCollectionTrailCentroid(final String id, final double x, final double y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public String GetID()
	{
		return id;
	}

	public double GetX()
	{
		return x;
	}

	public double GetY()
	{
		return y;
	}
}