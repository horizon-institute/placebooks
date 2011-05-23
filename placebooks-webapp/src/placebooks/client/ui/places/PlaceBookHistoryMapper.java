package placebooks.client.ui.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({EditorPlace.Tokenizer.class, PlaceBookListPlace.Tokenizer.class})
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}

