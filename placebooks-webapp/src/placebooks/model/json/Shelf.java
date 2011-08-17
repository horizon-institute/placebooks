package placebooks.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;

public class Shelf
{
	private static final Logger log = Logger.getLogger(Shelf.class.getName());

	@JsonProperty
	private Collection<ShelfEntry> entries = new ArrayList<ShelfEntry>();

	public Shelf()
	{
	}

	public Shelf(final PlaceBook pbs[])
	{
		log.info("Creating JSON Shelf for PlaceBooks...");

		for (final PlaceBook pb : pbs)
		{
			for (final Map.Entry<String, String> e : 
				 pb.getMetadata().entrySet())
			{
				log.info("Shelf entry: " + e.getKey() + " => " + e.getValue());
			}

			final ShelfEntry entry = new PlaceBookEntry(pb);
			entries.add(entry);
		}
	}

	public Shelf(final PlaceBookItem ps[])
	{
		log.info("Creating JSON Shelf for PlaceBookItems...");

		for (final PlaceBookItem p : ps)
		{
			for (final Map.Entry<String, String> e : 
				 p.getMetadata().entrySet())
			{
				log.info("Shelf entry: " + e.getKey() + " => " + e.getValue());
			}

			final ShelfEntry entry = new PlaceBookItemEntry(p);
			entries.add(entry);
		}
	}


	public void setEntries(final Collection<ShelfEntry> entries)
	{
		this.entries.clear();
		this.entries.addAll(entries);
	}
}
