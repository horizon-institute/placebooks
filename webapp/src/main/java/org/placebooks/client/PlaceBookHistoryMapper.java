package org.placebooks.client;

import org.placebooks.client.ui.pages.GroupPage;
import org.placebooks.client.ui.pages.GroupsPage;
import org.placebooks.client.ui.pages.LibraryPage;
import org.placebooks.client.ui.pages.PlaceBookPage;
import org.placebooks.client.ui.pages.SearchPage;
import org.placebooks.client.ui.pages.WelcomePage;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ SearchPage.Tokenizer.class, WelcomePage.Tokenizer.class, PlaceBookPage.Tokenizer.class, LibraryPage.Tokenizer.class,
					GroupPage.Tokenizer.class, GroupsPage.Tokenizer.class })
public interface PlaceBookHistoryMapper extends PlaceHistoryMapper
{
}
