package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookBookPanel extends Composite
{

	private static PlaceBookBookPanelUiBinder uiBinder = GWT.create(PlaceBookBookPanelUiBinder.class);

	interface PlaceBookBookPanelUiBinder extends UiBinder<Widget, PlaceBookBookPanel>
	{
	}
	
	private static final int DEFAULT_COLUMNS = 2;
	private static final int DEFAULT_PAGES = 3;
	
	private int currentPage = 0;
	
	@UiField 
	Panel rootPanel;

	private Canvas canvas;
	
	private HandlerRegistration resizeRegistration;
	
	private final List<PlaceBookPage> pages = new ArrayList<PlaceBookPage>(); 
	
	public PlaceBookBookPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		canvas = Canvas.createIfSupported();
		
		rootPanel.add(canvas);
		
		resizeRegistration = Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void setPlaceBook(final PlaceBook placebook, final PlaceBookItemFrameFactory factory)
	{
		int pageCount = DEFAULT_PAGES;
		try
		{
			pageCount = Integer.parseInt(placebook.getMetadata("pageCount"));
		}
		catch (final Exception e)
		{
		}

		int columns = DEFAULT_COLUMNS;
		try
		{
			columns = Integer.parseInt(placebook.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}
		
		for(int index = 0; index < pageCount; index++)
		{
			PlaceBookPage page = new PlaceBookPage();
			page.setPage(placebook, index, factory, columns);
			
			pages.add(page);
			rootPanel.add(page);
			
			if(index != currentPage)
			{
				page.setVisible(false);
			}
		}
	}
	
	public void setPage(int index)
	{
		PlaceBookPage page = pages.get(currentPage);
		PlaceBookPage newPage = pages.get(index);
		newPage.setVisible(true);
		page.setVisible(false);
		currentPage = index;
	}

	public void updatePlaceBook(final PlaceBook placebook)
	{
		
	}
}
