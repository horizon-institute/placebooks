package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
	
	@UiField
	Panel pagesPanel;

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
				resize();
			}
		});
	}
	
	public void resize()
	{
		int margin = 40;
		double height = getOffsetHeight() - margin;
		double width = getOffsetWidth() - margin;
		double bookWidth = height * 3 / 2;		
		if(bookWidth < width)
		{
			// Height is constraint
			double left = (width - bookWidth) / 2;

			pagesPanel.getElement().getStyle().setTop(0, Unit.PX);
			pagesPanel.getElement().getStyle().setLeft(left, Unit.PX);			
			pagesPanel.setWidth(bookWidth + "px");
			pagesPanel.setHeight(height + "px");
		}
		else
		{
			// Width is constraint
			double bookHeight = (width * 2 / 3);
			double top = (height - bookHeight) / 2;

			pagesPanel.getElement().getStyle().setTop(top, Unit.PX);
			pagesPanel.getElement().getStyle().setLeft(0, Unit.PX);			
			pagesPanel.setHeight(bookHeight + "px");
			pagesPanel.setWidth(width + "px");			
		}
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
			pagesPanel.add(page);
			
			if(index != currentPage)
			{
				page.setVisible(false);
			}
		}
		resize();
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
