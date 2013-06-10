package placebooks.client.ui.pages.places;

import placebooks.client.ui.pages.KioskPlaceBookGroupView;
import placebooks.client.ui.pages.PlaceBookGroupEditor;
import placebooks.client.ui.pages.PlaceBookGroupView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class Group extends PlaceBookPlace
{
	public enum Type 
	{
		edit, kiosk
	}
	
	@Prefix("group")
	public static class Tokenizer implements PlaceTokenizer<Group>
	{
		@Override
		public Group getPlace(final String token)
		{
			for(Type type: Type.values())
			{
				String tag = type.name() + ":";
				if(token.startsWith(tag))
				{
					return new Group(token.substring(tag.length()), type);
				}
			}
			return new Group(token);
		}

		@Override
		public String getToken(final Group place)
		{
			if (place.type != null) { return place.type.name() + ":" + place.id; }
			return place.id;
		}
	}

	private final String id;

	private final Type type;

	public Group(final String id)
	{
		this.id = id;
		this.type = null;
	}
	
	public Group(final String id, final Type type)
	{
		this.id = id;
		this.type = type;
	}

	@Override
	public Activity createActivity()
	{
		if (type == Type.edit) { return new PlaceBookGroupEditor(id); }
		if (type == Type.kiosk) { return new KioskPlaceBookGroupView(id); }		
		return new PlaceBookGroupView(id);
	}

	public String getId()
	{
		return id;
	}
}
