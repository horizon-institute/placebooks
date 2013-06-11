package placebooks.client;

import placebooks.client.ui.pages.places.Group;
import placebooks.client.ui.pages.places.Groups;
import placebooks.client.ui.pages.places.Home;
import placebooks.client.ui.pages.places.Library;
import placebooks.client.ui.pages.places.PlaceBook;
import placebooks.client.ui.pages.places.Search;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ Search.Tokenizer.class, Home.Tokenizer.class, PlaceBook.Tokenizer.class, Library.Tokenizer.class,
					Group.Tokenizer.class, Groups.Tokenizer.class })
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}
