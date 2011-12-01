package placebooks.client.ui.elements;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPagesBook extends PlaceBookPages
{
	interface PageStyle extends CssResource
	{
		String page();
	}
	
	interface PlaceBookBookPanelUiBinder extends UiBinder<Widget, PlaceBookPagesBook>
	{
	}

	private static PlaceBookBookPanelUiBinder uiBinder = GWT.create(PlaceBookBookPanelUiBinder.class);

	private int currentPage = 0;

	@UiField
	Panel rootPanel;

	@UiField
	Panel pagesPanel;

	@UiField
	Canvas canvas;
	
	@UiField
	PageStyle style;

	private PlaceBookPage dragging = null;
	
	private double bookWidth;
	private double pageWidth;
	private double pageHeight;
	
	private double margin = 20;

	private Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			if(dragging!= null)
			{
				drawFlip(dragging);
			}			
		}
	};
	
	public PlaceBookPagesBook()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPage(final int index)
	{
		final PlaceBookPage page = pages.get(currentPage);
		final PlaceBookPage newPage = pages.get(index);
		newPage.setVisible(true);
		page.setVisible(false);
		currentPage = index;
	}

	@UiHandler("rootPanel")
	void flipStart(final MouseDownEvent event)
	{
		final int mouseX = event.getRelativeX(pagesPanel.getElement());
		// Make sure the mouse pointer is inside of the book
		if (mouseX < pageWidth)
		{		
			if (mouseX < margin && currentPage - 1 >= 0)
			{
				// We are on the left side, drag the previous page
				dragging = pages.get(currentPage);
				GWT.log("Start drag");				
				event.preventDefault();				
			}
			else if (mouseX > (pageWidth - margin) && currentPage + 1 < pages.size())
			{
				// We are on the right side, drag the current page
				dragging = pages.get(currentPage);
				GWT.log("Start drag");				
				event.preventDefault();				
			}
		}
	}

	@UiHandler("rootPanel")
	void flip(final MouseMoveEvent event)
	{
		final int mouseX = event.getRelativeX(pagesPanel.getElement());
		if(dragging != null)
		{
			dragging.setProgress(Math.max( Math.min( mouseX / pageWidth, 1 ), -1 ));
			//drawFlip(dragging);
			event.preventDefault();
		}
	}

	@Override
	protected void add(PlaceBookPage page)
	{
		super.add(page);
		page.setStyleName(style.page());
	}

	@UiHandler("rootPanel")
	void flipEnd(final MouseUpEvent event)
	{
		if(dragging != null)
		{
			GWT.log("End drag");
			dragging = null;
			// TODO Finish flipping page
		}
	}
	
	private void drawFlip(final PlaceBookPage page)
	{
		// Determines the strength of the fold/bend on a 0-1 range
		final double strength = 1 - Math.abs(page.getProgress());

		// Width of the folded paper
		final double foldWidth = (pageWidth * 0.5) * (1 - page.getProgress());

		// X position of the folded paper
		final double foldX = pageWidth * page.getProgress() + foldWidth;

		// How far outside of the book the paper is bent due to perspective
		final double verticalOutdent = 20 * strength;

		// The maximum widths of the three shadows used
		final double paperShadowWidth = (pageWidth * 0.5) * Math.max(Math.min(1 - page.getProgress(), 0.5), 0);
		final double rightShadowWidth = (pageWidth * 0.5) * Math.max(Math.min(strength, 0.5), 0);
		final double leftShadowWidth = (pageWidth * 0.5) * Math.max(Math.min(strength, 0.5), 0);

		// Mask the page by setting its width to match the foldX
		//page.setFlip(Math.max(foldX, 0));

		final Context2d context = canvas.getContext2d();
		context.clearRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
		context.save();
		context.translate( (bookWidth / 2) + margin, margin );


		// Draw a sharp shadow on the left side of the page
		context.setStrokeStyle("rgba(0,0,0," + (0.05 * strength) + ")");
		context.setLineWidth(30 * strength);
		context.beginPath();
		context.moveTo(foldX - foldWidth, -verticalOutdent * 0.5);
		context.lineTo(foldX - foldWidth, pageHeight + (verticalOutdent * 0.5));
		context.stroke();

		// Right side drop shadow
		final CanvasGradient rightShadowGradient = context.createLinearGradient(foldX, 0, foldX + rightShadowWidth, 0);
		final String rightShadowAlpha = NumberFormat.getFormat("#.################").format(strength * 0.2);
		rightShadowGradient.addColorStop(0, "rgba(0,0,0," + rightShadowAlpha + ")");
		rightShadowGradient.addColorStop(0.8, "rgba(0,0,0,0.0)");

		context.setFillStyle(rightShadowGradient);
		context.beginPath();
		context.moveTo(foldX, 0);
		context.lineTo(foldX + rightShadowWidth, 0);
		context.lineTo(foldX + rightShadowWidth, pageHeight);
		context.lineTo(foldX, pageHeight);
		context.fill();

		// Left side drop shadow
		final CanvasGradient leftShadowGradient = context.createLinearGradient(	foldX - foldWidth - leftShadowWidth, 0,
																				foldX - foldWidth, 0);
		leftShadowGradient.addColorStop(0, "rgba(0,0,0,0.0)");
		final String leftShadowAlpha = NumberFormat.getFormat("#.################").format(strength * 0.15);	
		leftShadowGradient.addColorStop(1, "rgba(0,0,0," + leftShadowAlpha + ")");

		context.setFillStyle(leftShadowGradient);
		context.beginPath();
		context.moveTo(foldX - foldWidth - leftShadowWidth, 0);
		context.lineTo(foldX - foldWidth, 0);
		context.lineTo(foldX - foldWidth, pageHeight);
		context.lineTo(foldX - foldWidth - leftShadowWidth, pageHeight);
		context.fill();

		// Gradient applied to the folded paper (highlights & shadows)
		final CanvasGradient foldGradient = context.createLinearGradient(foldX - paperShadowWidth, 0, foldX, 0);
		foldGradient.addColorStop(0.35, "#fafafa");
		foldGradient.addColorStop(0.73, "#eeeeee");
		foldGradient.addColorStop(0.9, "#fafafa");
		foldGradient.addColorStop(1.0, "#e2e2e2");

		context.setFillStyle(foldGradient);
		context.setStrokeStyle("rgba(0,0,0,0.06)");
		context.setLineWidth(0.5);

		// Draw the folded piece of paper
		context.beginPath();
		context.moveTo(foldX, 0);
		context.lineTo(foldX, pageHeight);
		context.quadraticCurveTo(foldX, pageHeight + (verticalOutdent * 2), foldX - foldWidth, pageHeight
				+ verticalOutdent);
		context.lineTo(foldX - foldWidth, -verticalOutdent);
		context.quadraticCurveTo(foldX, -verticalOutdent * 2, foldX, 0);

		context.fill();
		context.stroke();

		context.restore();
	}

	public void resized()
	{
		final double height = getOffsetHeight();
		final double width = getOffsetWidth();
		final double bookWidth = height * 3 / 2;
		if (bookWidth < width)
		{
			// Height is constraint
			final double left = (width - bookWidth) / 2;

			setPosition(left, 0, bookWidth, height);
		}
		else
		{
			// Width is constraint
			final double bookHeight = (width * 2 / 3);
			final double top = (height - bookHeight) / 2;

			setPosition(0, top, width, bookHeight);
		}
	}

	private void setPosition(final double left, final double top, final double width, final double height)
	{
		this.pageHeight = height - (margin * 2);
		this.pageWidth = width - (margin * 2);
		this.bookWidth = width * 2;

		canvas.getElement().getStyle().setTop(top, Unit.PX);
		canvas.getElement().getStyle().setLeft(left - width, Unit.PX);
		canvas.setWidth(bookWidth + "px");
		canvas.setHeight(height + "px");

		canvas.getCanvasElement().setWidth((int) bookWidth);
		canvas.getCanvasElement().setHeight((int) height);
		
		pagesPanel.getElement().getStyle().setTop(top + margin, Unit.PX);
		pagesPanel.getElement().getStyle().setLeft(left + margin, Unit.PX);
		pagesPanel.setWidth(pageWidth + "px");
		pagesPanel.setHeight(pageHeight + "px");

		for (final PlaceBookPage page : pages)
		{
			page.setSize(width, height);
		}
	}

	@Override
	protected Panel getPagePanel()
	{
		return pagesPanel;		
	}
}