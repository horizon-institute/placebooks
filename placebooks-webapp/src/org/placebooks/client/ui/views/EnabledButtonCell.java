package org.placebooks.client.ui.views;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public abstract class EnabledButtonCell<T> extends AbstractCell<T>
{
	/**
	 * Construct a new ButtonCell that will use a given {@link SafeHtmlRenderer}.
	 * 
	 * @param renderer
	 *            a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
	 */
	public EnabledButtonCell()
	{
		super("click", "keydown");
	}

	public abstract String getText(T object);

	public abstract boolean isEnabled(T object);

	@Override
	public void onBrowserEvent(final Context context, final Element parent, final T value, final NativeEvent event,
			final ValueUpdater<T> valueUpdater)
	{
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType()))
		{
			final EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) { return; }
			if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget)))
			{
				// Ignore clicks that occur outside of the main element.
				onEnterKeyDown(context, parent, value, event, valueUpdater);
			}
		}
	}

	@Override
	public void render(final Context context, final T data, final SafeHtmlBuilder sb)
	{
		String disabledString = "";
		if (!isEnabled(data))
		{
			disabledString = "disabled=\"disabled\"";
		}

		sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\"" + disabledString + ">");
		if (data != null)
		{
			sb.appendEscaped(getText(data));
		}
		sb.appendHtmlConstant("</button>");
	}

	@Override
	protected void onEnterKeyDown(final Context context, final Element parent, final T value, final NativeEvent event,
			final ValueUpdater<T> valueUpdater)
	{
		if (valueUpdater != null && isEnabled(value))
		{
			valueUpdater.update(value);
		}
	}
}