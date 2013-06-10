package placebooks.client.controllers;

import placebooks.client.logger.Log;
import placebooks.client.parser.JSONParser;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class CachedController<U> extends ControllerBase<U> implements AsyncCallback<U>
{
	private static final Storage storage = Storage.getLocalStorageIfSupported();

	private String id;
	private final String prefix;

	private final JSONParser<U> parser;

	public CachedController()
	{
		this.parser = null;
		this.prefix = null;
	}

	public CachedController(final JSONParser<U> parser, final String prefix)
	{
		this.parser = parser;
		this.prefix = prefix;
	}

	@Override
	public void load(final String id)
	{
		if (hasLoaded()) { return; }
		this.id = id;
		final String key = getKey(getId());
		if (storage != null && key != null)
		{
			final String item = storage.getItem(key);
			if (item != null)
			{
				setItem(parser.parse(com.google.gwt.json.client.JSONParser.parseStrict(item)));
			}
		}
		load(id, this);
	}

	@Override
	public void onFailure(final Throwable caught)
	{
		// TODO Log?
	}

	@Override
	public void onSuccess(final U response)
	{
		setItem(response);
	}

	@Override
	protected void fireChange()
	{
		super.fireChange();
		final String key = getKey(getId());
		if (storage != null && key != null)
		{
			if (getItem() == null)
			{
				storage.removeItem(key);
			}
			else
			{
				final StringBuilder builder = new StringBuilder();
				parser.write(builder, getItem());
				storage.setItem(key, builder.toString());
				Log.info("Stored " + key + ": " + builder.toString());
			}
		}
	}

	@Override
	public void refresh()
	{
		load(getId(), this);
	}

	protected boolean accept(final U item)
	{
		return true;
	}

	protected String getId()
	{
		return id;
	}

	protected abstract void load(String id, final AsyncCallback<U> callback);

	protected String getKey(final String id)
	{
		if (prefix == null) { return null; }
		if (id == null) { return prefix; }
		return prefix + "." + id;
	}
}