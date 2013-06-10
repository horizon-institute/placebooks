package placebooks.client.ui.items.maps;

import com.google.gwt.core.client.JavaScriptObject;

public class Bounds extends JavaScriptObject
{
	public static final native Bounds create()
	/*-{
		return new $wnd.OpenLayers.Bounds();
	}-*/;

	protected Bounds()
	{
	}

	public final native boolean contains(final Bounds bounds)
	/*-{
		return this.containsBounds(bounds);
	}-*/;

	public final native void extend(final Bounds bounds)
	/*-{
		this.extend(bounds);
	}-*/;

	public final native void extend(final LonLat latlon)
	/*-{
		this.extend(latlon);
	}-*/;

	public final native String toBBox()
	/*-{
		return this.toBBOX();
	}-*/;
	
	public final native Bounds clone()
	/*-{
		return this.clone();
	}-*/;

	public final native float getLeft()
	/*-{
		return this.left;
	}-*/;

	public final native float getTop()
	/*-{
		return this.top;
	}-*/;
	
	public final native float getRight()
	/*-{
		return this.right;
	}-*/;

	public final native float getBottom()
	/*-{
		return this.bottom;
	}-*/;
	
	public final native float getWidth()
	/*-{
		return this.getWidth();
	}-*/;
	
	public final native float getHeight()
	/*-{
		return this.getHeight();
	}-*/;
	
	public final native Bounds transform(Projection source, Projection dest)
	/*-{
		return this.transform(source, dest);
	}-*/;

}
