package org.placebooks.model.json;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.placebooks.model.PlaceBook;
import org.placebooks.model.PlaceBookBinder;
import org.placebooks.model.PlaceBookItem;

public class Shelf
{
	private static final Logger log = Logger.getLogger(Shelf.class.getName());

	private Collection<ShelfEntry> entries = new ArrayList<ShelfEntry>();

	public Shelf()
	{
	}

	public Shelf(final Collection<? extends ShelfEntry> entries)
	{
		this.entries.clear();
		this.entries.addAll(entries);
	}

	public Shelf(final Iterable<PlaceBookBinder> placebookBinders)
	{
		log.info("Creating JSON Shelf for PlaceBookBinders...");

		for (final PlaceBookBinder pb : placebookBinders)
		{
			// for (final Map.Entry<String, String> e :
			// pb.getMetadata().entrySet())
			// {
			// log.info("Shelf entry: " + e.getKey() + " => " + e.getValue());
			// }

			final ShelfEntry entry = new PlaceBookBinderEntry(pb);
			entries.add(entry);
		}
	}

	public Shelf(final PlaceBook ps[])
	{
		log.info("Creating JSON Shelf for PlaceBooks...");

		for (final PlaceBook p : ps)
		{
			final ShelfEntry entry = new PlaceBookEntry(p);
			entries.add(entry);
		}
	}

	public Shelf(final PlaceBookBinder pbs[])
	{
		log.info("Creating JSON Shelf for PlaceBookBinders...");

		for (final PlaceBookBinder pb : pbs)
		{
			// StringBuilder logString = new StringBuilder();
			// for (final Map.Entry<String, String> e : pb.getMetadata().entrySet())
			// {
			// logString.append("[" + e.getKey() + "] => [" + e.getValue() + "] ");
			// }
			// log.debug("Shelf entries: " + logString.toString());
			final ShelfEntry entry = new PlaceBookBinderEntry(pb);
			entries.add(entry);
		}
	}

	public Shelf(final PlaceBookItem ps[])
	{
		log.info("Creating JSON Shelf for PlaceBookItems...");

		for (final PlaceBookItem p : ps)
		{
			// StringBuilder logString = new StringBuilder();
			// for (final Map.Entry<String, String> e : p.getMetadata().entrySet())
			// {
			// logString.append("[" + e.getKey() + "] => [" + e.getValue() + "] ");
			// }
			// log.debug("Shelf entries: " + logString.toString());
			final ShelfEntry entry = new PlaceBookItemEntry(p);
			entries.add(entry);
		}
	}
	
	public Iterable<ShelfEntry> getEntries()
	{
		return entries;
	}

	public void setEntries(final Collection<ShelfEntry> entries)
	{
		this.entries.clear();
		this.entries.addAll(entries);
	}
}
