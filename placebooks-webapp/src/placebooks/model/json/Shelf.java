package placebooks.model.json;

import placebooks.model.PlaceBook;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import org.apache.log4j.Logger;


public class Shelf
{

	@JsonIgnore
  	private static final Logger log = Logger.getLogger(Shelf.class.getName());

	@JsonProperty
	private String[] keys;

	@JsonProperty
	private String[] owners;

	@JsonProperty
	private String[] titles;

	public Shelf(Collection<PlaceBook> pbs) 
	{
		log.info("Creating JSON Shelf...");
		String[] keys = new String[pbs.size()];
		String[] owners = new String[pbs.size()];
		String[] titles = new String[pbs.size()];

		int i = 0; 
		for (PlaceBook pb : pbs)
		{
			keys[i] = pb.getKey();
			owners[i] = pb.getOwner().getEmail();
			String title = pb.getMetadata("title");
			if (title == null)
				titles[i] = "[null]";
			++i;
		}

		setKeys(keys);
		setOwners(owners);
		setTitles(titles);
	}

	public String[] getKeys()
	{
		return keys;
	}
	public void setKeys(String[] keys)
	{
		this.keys = keys;
	}
	public String[] getOwners()
	{
		return owners;
	}
	public void setOwners(String[] owners)
	{
		this.owners = owners;
	}
	public String[] getTitles()
	{
		return titles;
	}
	public void setTitles(String[] titles)
	{
		this.titles = titles;
	}

}
