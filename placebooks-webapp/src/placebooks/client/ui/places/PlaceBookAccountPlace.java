package placebooks.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class PlaceBookAccountPlace extends Place
{
    public static class Tokenizer implements PlaceTokenizer<PlaceBookAccountPlace> {
        @Override
        public String getToken(PlaceBookAccountPlace place) {
            return null;
        }

        @Override
        public PlaceBookAccountPlace getPlace(String token) {
            return new PlaceBookAccountPlace();
        }
    }
}
