package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.user.client.ui.Composite;

public abstract class PlaceBookItemFrame2 extends Composite
{
	protected PlaceBookItemWidget itemWidget;
	
	public void setItemWidget(PlaceBookItemWidget itemWidget)
	{
		this.itemWidget = itemWidget;
		// TODO Attach to ui
	}
	
	public PlaceBookItem getItem()
	{
		return itemWidget.getItem();
	}
	
	public PlaceBookItemWidget getItemWidget()
	{
		return itemWidget;
	}
	
	protected abstract void onSetItem();
}
