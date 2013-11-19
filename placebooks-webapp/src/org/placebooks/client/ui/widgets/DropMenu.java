package org.placebooks.client.ui.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class DropMenu extends FlowPanel implements HasMouseOverHandlers, HasMouseOutHandlers
{
	private boolean showing = true;

	private HandlerRegistration nativePreviewHandlerRegistration;

	public DropMenu()
	{
		super();
		hide();

		addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				hide();
			}
		}, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public void hide()
	{
		if (showing)
		{
			showing = false;
			getElement().getStyle().setProperty("clip", "rect(0px, 500px, 0px, 0px)");
			updateHandlers();
		}
	}

	public void show(final int x, final int y)
	{
		if (!showing)
		{
			showing = true;
			getElement().getStyle().setTop(y, Unit.PX);
			getElement().getStyle().setLeft(x, Unit.PX);
			updateHandlers();
			getElement().getStyle().setProperty("clip",
												"rect(0px, 500px, " + (getElement().getClientHeight() + 10)
														+ "px, 0px)");			
		}
	}

	/**
	 * Does the event target this popup?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets the popup
	 */
	private boolean eventTargetsMenu(final NativeEvent event)
	{
		final EventTarget target = event.getEventTarget();
		if (Element.is(target)) { return getElement().isOrHasChild(Element.as(target)); }
		return false;
	}

	/**
	 * Preview the {@link NativePreviewEvent}.
	 * 
	 * @param event
	 *            the {@link NativePreviewEvent}
	 */
	private void previewNativeEvent(final NativePreviewEvent event)
	{
		// If the event has been canceled or consumed, ignore it
		if (event.isCanceled() || event.isConsumed()) { return; }

		// If the event targets the popup or the partner, consume it
		final Event nativeEvent = Event.as(event.getNativeEvent());
		final int type = nativeEvent.getTypeInt();
		switch (type)
		{
			case Event.ONMOUSEDOWN:
			{
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null)
				{
					event.consume();
					return;
				}

				if (!eventTargetsMenu(nativeEvent))
				{
					hide();
				}

				return;
			}

			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONCLICK:
			case Event.ONDBLCLICK:
			{
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null)
				{
					event.consume();
					return;
				}
				break;
			}
		}
	}

	/**
	 * Register or unregister the handlers used by {@link PopupPanel}.
	 */
	private void updateHandlers()
	{
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null)
		{
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}

		// Create handlers if showing.
		if (showing)
		{
			nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler()
			{
				@Override
				public void onPreviewNativeEvent(final NativePreviewEvent event)
				{
					previewNativeEvent(event);
				}
			});
		}
	}
}