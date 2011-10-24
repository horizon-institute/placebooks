package placebooks.client.ui;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlaceBookEditor.Tokenizer.class,
					PlaceBookHome.Tokenizer.class, PlaceBookPreview.Tokenizer.class,
					PlaceBookLibrary.Tokenizer.class, PlaceBookLibrary.Tokenizer.class,
					PlaceBookSearch.Tokenizer.class, PlaceBookAppInstall.Tokenizer.class })
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}
