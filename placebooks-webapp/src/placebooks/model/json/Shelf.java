package placebooks.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import placebooks.model.PlaceBook;
import placebooks.model.User;

public class Shelf
{
	@JsonIgnore
	private static final Logger log = Logger.getLogger(Shelf.class.getName());

	@JsonProperty
	private Collection<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();

	@JsonProperty
	private User user;

	public Shelf(final User user, final Collection<PlaceBook> pbs)
	{
		log.info("Creating JSON Shelf...");
		this.user = user;

		for (final PlaceBook pb : pbs)
		{
			for (final Map.Entry<String, String> e : pb.getMetadata().entrySet())
			{
				log.info("Shelf entry: " + e.getKey() + " => " + e.getValue());
			}

			final PlaceBookEntry entry = new PlaceBookEntry();
			entry.setKey(pb.getKey());
			entry.setOwner(pb.getOwner().getEmail());
			entry.setTitle(pb.getMetadataValue("title"));
			entry.setNumItems(pb.getItems().size());
			entry.setDescription(pb.getMetadataValue("description"));
			entry.setPackagePath(pb.getPackagePath());

			entries.add(entry);
		}
	}
}
