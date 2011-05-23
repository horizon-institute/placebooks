package placebooks.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class PlaceBookListPlace extends Place
{
    public static class Tokenizer implements PlaceTokenizer<PlaceBookListPlace> {
        @Override
        public String getToken(PlaceBookListPlace place) {
            return null;
        }

        @Override
        public PlaceBookListPlace getPlace(String token) {
            return new PlaceBookListPlace();
        }
    }
}
