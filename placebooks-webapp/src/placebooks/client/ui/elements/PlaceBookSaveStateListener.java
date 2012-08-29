package placebooks.client.ui.elements;

import placebooks.client.ui.elements.PlaceBookSaveItem.SaveState;

public interface PlaceBookSaveStateListener
{
	public void saveStateChanged(SaveState state);
}
