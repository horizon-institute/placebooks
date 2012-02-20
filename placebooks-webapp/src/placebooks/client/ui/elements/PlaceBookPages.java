package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;

import com.google.gwt.core.client.GWT;
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

	public void createPage()
	{

	}

	public boolean deleteCurrentPage()
	{
		return false;
	}

	public Iterable<PlaceBookPage> getPages()
	{
		return pages;
	}

	public PlaceBookBinder getPlaceBook()
	{
		return placebook;
	}

	public abstract void resized();

	public void setPlaceBook(final PlaceBookBinder newPlaceBook, final PlaceBookController controller)
	{
		this.placebook = newPlaceBook;
		this.controller = controller;
		getPagePanel().clear();
		pages.clear();

		int pageIndex = 0;
		for (final PlaceBook page : newPlaceBook.getPages())
		{
			final PlaceBookPage pagePanel = new PlaceBookPage(page, controller, pageIndex, getDefaultColumnCount());

			pageIndex++;

			add(pagePanel);
		}

		if (pageIndex == 0)
		{
			createPage();
		}

		resized();
	}

	public void update(final PlaceBookBinder newPlaceBook)
	{
		this.placebook = newPlaceBook;

		for (final PlaceBook page : newPlaceBook.getPages())
		{
			final PlaceBookPage pageUI = getPage(page);
			if (pageUI != null)
			{
				pageUI.update(page);
			}
			else
			{
				GWT.log("Page not matched!");
			}
		}
	}

	protected void add(final PlaceBookPage page)
	{
		pages.add(page);
		getPagePanel().add(page);
	}

	protected int getDefaultColumnCount()
	{
		return 3;
	}

	protected abstract Panel getPagePanel();

	protected void remove(final PlaceBookPage page)
	{
		pages.remove(page);
		getPagePanel().remove(page);
	}

	private PlaceBookPage getPage(final PlaceBook page)
	{
		for (final PlaceBookPage pbPage : pages)
		{
			if (page.getId().equals(pbPage.getPlaceBook().getId()))
			{
				return pbPage;
			}
			else if (page.hasMetadata("tempID")
					&& page.getMetadata("tempID").equals(pbPage.getPlaceBook().getMetadata("tempID"))) { return pbPage; }
		}
		return null;
	}
}