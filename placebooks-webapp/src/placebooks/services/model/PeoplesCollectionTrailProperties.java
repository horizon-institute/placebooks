package placebooks.services.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;


/**
 * Class to encapsulate response from Peoples Collection API for Trail Properties
 * @author pszmp
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailProperties
{

	PeoplesCollectionTrailCentroid centroid;
	int[] items;
	String title;
	String titlecym;

	String description;
	String descriptioncym;

	String difficulty;
	String trailtype;

	double distance;
	int userid;

	String tags;
	String tagscym;

	int trailid;

	public PeoplesCollectionTrailProperties()
	{

	}

	public PeoplesCollectionTrailProperties(PeoplesCollectionTrailCentroid centroid, int[] items, String title, String titlecym, String description, String descriptioncym, String difficulty, String trailtype, double distance, int userid, String tags, String tagscym, int trailid)
	{
		this.centroid = centroid;
		this.items = items;
		this.title = title;
		this.titlecym = titlecym;

		this.description = description;
		this.descriptioncym = descriptioncym;

		this.difficulty = difficulty;
		this.trailtype = trailtype;

		this.distance = distance;
		this.userid = userid;

		this.tags = tags;
		this.tagscym = tagscym;

		this.trailid = trailid;
	}
	public PeoplesCollectionTrailCentroid GetCentroid()
	{
		return centroid;
	}

	public int[] GetItems()
	{
		return items;
	}

	public String GetTitle()
	{
		return title;
	}

	public String GetTitleCym()
	{
		return titlecym;
	}

	public String GetDescription()
	{
		return description; 
	}

	public String GetDecriptionCym()
	{
		return descriptioncym;        
	}

	public String GetDifficulty()
	{
		return difficulty;
	}
	public String GetTrailType()
	{
		return trailtype;
	}

	public double GetDistance()
	{
		return distance;
	}

	public int GetUserId()
	{
		return userid;
	}
	
	public int GetTrailId()
	{
		return trailid;
	}

	public String GetTags()
	{
		return tags;
	}

	public String GetTagsCym()
	{
		return tagscym;
	}
}
