package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

public abstract class PlaceBookPages extends Composite
{
	protected final List<PlaceBookPage> pages = new ArrayList<PlaceBookPage>();

	private PlaceBookBinder placebook;
	protected PlaceBookController controller;
	
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
	
	protected void remove(PlaceBookPage page)
	{
		pages.remove(page);
		getPagePanel().remove(page);
	}
	
	protected int getDefaultColumnCount()
	{
		return 3;
	}
	
	public void setPlaceBook(final PlaceBookBinder newPlaceBook, final PlaceBookController controller)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;
		this.controller = controller;
		getPagePanel().clear();

		int pageIndex = 0;
		for (PlaceBook page: newPlaceBook.getPages())
		{
			final PlaceBookPage pagePanel = new PlaceBookPage(page, controller, pageIndex, getDefaultColumnCount());

			pageIndex ++;
			
			add(pagePanel);
		}

		resized();
	}

	protected abstract Panel getPagePanel();
	
	private PlaceBookPage getPage(final PlaceBook page)
	{
		for(PlaceBookPage pbPage: pages)
		{
			if(page.getId().equals(pbPage.getPlaceBook().getId()))
			{
				return pbPage;
			}
			else if(page.hasMetadata("tempID") && page.getMetadata("tempID").equals(pbPage.getPlaceBook().getMetadata("tempID")))
			{
				return pbPage;
			}
		}
		return null;
	}
	
	public void update(final PlaceBookBinder newPlaceBook)
	{
		this.placebook = newPlaceBook;

		for (final PlaceBook page : newPlaceBook.getPages())
		{
			final PlaceBookPage pageUI = getPage(page);
			if(pageUI != null)
			{
				pageUI.update(page);
			}
		}

		resized();
	}

	public Iterable<PlaceBookPage> getPages()
	{
		return pages;
	}

	public void deleteCurrentPage()
	{
	}

	public void createPage()
	{
		
	}
}