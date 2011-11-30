package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

public abstract class PlaceBookPages extends Composite
{
	protected final List<PlaceBookPage> pages = new ArrayList<PlaceBookPage>();

	private PlaceBookBinder placebook;
	
	public PlaceBookPages()
	{
		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				resized();
			}
		});
	}

	public PlaceBookBinder getPlaceBook()
	{
		return placebook;
	}
	
	public abstract void resized();
	
	protected void add(PlaceBookPage page)
	{
		pages.add(page);
		getPagePanel().add(page);
	}
	
	public void setPlaceBook(final PlaceBookBinder newPlaceBook, final PlaceBookItemFrameFactory factory)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;
		getPagePanel().clear();

		int pageIndex = 0;
		for (PlaceBook page: newPlaceBook.getPages())
		{
			final PlaceBookPage pagePanel = new PlaceBookPage(page, pageIndex, factory);

			pageIndex ++;
			
			add(pagePanel);
		}

		//refreshItemPlaceBook();
		resized();
	}

	protected abstract Panel getPagePanel();
	
	public void updatePlaceBook(final PlaceBookBinder newPlaceBook)
	{
//		this.placebook = newPlaceBook;
//
//		for (final PlaceBookItem item : newPlaceBook.getItems())
//		{
//			final PlaceBookItemFrame frame = getFrame(item);
//			if (frame != null)
//			{
//				frame.getItemWidget().update(item);
//			}
//		}
//
//		placebook.clearItems();
//		for (final PlaceBookItemFrame item : items)
//		{
//			placebook.add(item.getItem());
//		}
//
//		//refreshItemPlaceBook();
//		reflow();
	}

	public Iterable<PlaceBookPage> getPages()
	{
		return pages;
	}
}
