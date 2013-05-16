package placebooks.client.ui.elements;

import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class Canvas extends Widget
{
	public Canvas()
	{
		setElement(Document.get().createCanvasElement());
	}

	/**
	 * Returns the attached Canvas Element.
	 * 
	 * @return the Canvas Element
	 */
	public CanvasElement getCanvasElement()
	{
		return getElement().cast();
	}

	/**
	 * Gets the rendering context that may be used to draw on this canvas.
	 * 
	 * @param contextId
	 *            the context id as a String
	 * @return the canvas rendering context
	 */
	public Context getContext(final String contextId)
	{
		return getCanvasElement().getContext(contextId);
	}

	/**
	 * Returns a 2D rendering context.
	 * 
	 * This is a convenience method, see {@link #getContext(String)}.
	 * 
	 * @return a 2D canvas rendering context
	 */
	public Context2d getContext2d()
	{
		return getCanvasElement().getContext2d();
	}
}
