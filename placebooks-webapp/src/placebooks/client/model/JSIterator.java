package placebooks.client.model;

import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSIterator<T extends JavaScriptObject> implements Iterator<T>
{
	private final JsArray<T> array;
	private int index = 0;
	
	public JSIterator(final JsArray<T> jsarray)
	{
		this.array = jsarray;
	}
	
	@Override
	public boolean hasNext()
	{
		return index < array.length();
	}

	@Override
	public T next()
	{
		T result = array.get(index);
		index++;
		return result;
	}

	@Override
	public void remove()
	{
				
	}
}
