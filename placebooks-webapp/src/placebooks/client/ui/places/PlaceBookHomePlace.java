package placebooks.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class PlaceBookHomePlace extends Place
{
    public static class Tokenizer implements PlaceTokenizer<PlaceBookHomePlace> {
        @Override
        public String getToken(PlaceBookHomePlace place) {
            return null;
        }

        @Override
        public PlaceBookHomePlace getPlace(String token) {
            return new PlaceBookHomePlace();
        }
    }
}
