package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookEditor.SaveContext;

public class DeletePlaceBookMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final SaveContext context;

	public DeletePlaceBookMenuItem(final SaveContext context, final PlaceBookCanvas canvas)
	{
		super("Delete PlaceBook");
		this.canvas = canvas;
		this.context = context;
	}

	@Override
	public void run()
	{
		// TODO
	}
}
