package placebooks.model.json;

import placebooks.model.PlaceBook;

import java.util.Collection;
import java.util.*; //TMP

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import org.apache.log4j.Logger;


public class Shelf
{
	@JsonIgnore
  	private static final Logger log = Logger.getLogger(Shelf.class.getName());

	@JsonProperty
	private PlaceBookEntry[] entries;

	public Shelf(Collection<PlaceBook> pbs) 
	{
		log.info("Creating JSON Shelf...");
		PlaceBookEntry[] entries = new PlaceBookEntry[pbs.size()];

		int i = 0; 
		for (PlaceBook pb : pbs)
		{
			Set s = (Set)pb.getMetadata().entrySet();
			for (Iterator j = s.iterator(); j.hasNext(); )
			{
				Map.Entry e = (Map.Entry)j.next();
				log.info("Shelf entry: " + e.getKey() + " => " + e.getValue());
			}

			entries[i] = new PlaceBookEntry();
			entries[i].setKey(pb.getKey());
			entries[i].setOwner(pb.getOwner().getEmail());
			entries[i].setTitle(pb.getMetadataValue("title"));
			entries[i].setNumItems(pb.getItems().size());
			entries[i].setDescription(pb.getMetadataValue("description"));
			
			++i;
		}

		this.entries = entries;
	}


}
