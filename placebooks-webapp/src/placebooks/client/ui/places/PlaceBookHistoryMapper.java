package placebooks.client.ui.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({PlaceBookEditorPlace.Tokenizer.class, PlaceBookAccountPlace.Tokenizer.class, PlaceBookPreviewPlace.Tokenizer.class})
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}

