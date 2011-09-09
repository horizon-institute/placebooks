package placebooks.client.ui;

import placebooks.client.JavaScriptInjector;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPreview extends Composite
{
	interface PlaceBookPreviewUiBinder extends UiBinder<Widget, PlaceBookPreview>
	{
	}

	private static final PlaceBookPreviewUiBinder uiBinder = GWT.create(PlaceBookPreviewUiBinder.class);

	@UiField
	PlaceBookToolbar toolbar;

	@UiField
	Panel canvasPanel;

	@UiField
	Panel likePanel;

	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	public PlaceBookPreview(final PlaceController placeController, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		canvasPanel.add(canvas);

		toolbar.setPlaceController(placeController);
		toolbar.setShelf(shelf);
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public void setPlaceBook(final PlaceBook placebook, final PlaceBookItemFrameFactory factory)
	{
		canvas.setPlaceBook(placebook, factory, false);

		likePanel.clear();
		
		final Label label = new Label(placebook.getMetadata("title"));
		likePanel.add(label);
		label.getElement().getStyle().setFloat(Float.LEFT);
		label.getElement().getStyle().setFontSize(18, Unit.PX);
		label.getElement().getStyle().setMarginRight(50, Unit.PX);
		
		if (placebook.getState() != null && placebook.getState().equals("PUBLISHED"))
		{
			final String url = PlaceBookService.getHostURL() + "placebooks/a/view/" + placebook.getKey();
			likePanel.getElement().getStyle().setDisplay(Display.BLOCK);
			final HTML html = new HTML("<g:plusone size=\"medium\" annotation=\"bubble\" href=\"" + url
					+ "\"></g:plusone>");
			
			final Frame frame = new Frame(
					"http://www.facebook.com/plugins/like.php?href="
							+ URL.encodeQueryString(url));
			frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
			frame.setHeight("35px");
			frame.getElement().getStyle().setFloat(Float.LEFT);
			likePanel.add(frame);
			likePanel.add(html);

			JavaScriptInjector.add("https://apis.google.com/js/plusone.js");
		}
		
		if(placebook.hasMetadata("title"))
		{
			Window.setTitle("PlaceBooks - " + placebook.getMetadata("title") );
		}
		else
		{
			Window.setTitle("PlaceBooks");
		}
	}
}