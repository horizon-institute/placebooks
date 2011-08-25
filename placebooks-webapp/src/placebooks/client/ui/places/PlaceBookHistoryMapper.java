package placebooks.client.ui.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlaceBookEditorNewPlace.Tokenizer.class, PlaceBookEditorPlace.Tokenizer.class,
					PlaceBookHomePlace.Tokenizer.class, PlaceBookPreviewPlace.Tokenizer.class,
					PlaceBookLibraryPlace.Tokenizer.class, PlaceBookLibraryPlace.Tokenizer.class,
					PlaceBookSearchPlace.Tokenizer.class, PlaceBookAppInstallPlace.Tokenizer.class })
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}
