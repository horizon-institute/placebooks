package placebooks.client;

import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookHome;
import placebooks.client.ui.PlaceBookLibrary;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.PlaceBookSearch;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlaceBookEditor.Tokenizer.class, PlaceBookSearch.Tokenizer.class, PlaceBookHome.Tokenizer.class,
					PlaceBookPreview.Tokenizer.class, PlaceBookLibrary.Tokenizer.class })
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}
