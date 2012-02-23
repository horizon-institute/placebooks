package placebooks.client.ui.openlayers;

public abstract class EventHandler
{
	public native static EventHandlerFunction createHandler(EventHandler self)
	/*-{
		var controller = function(event)
		{
			self.@placebooks.client.ui.openlayers.EventHandler::handleEvent(Lplacebooks/client/ui/openlayers/Event;)(event);
		}
		return controller;
	}-*/;

	private EventHandlerFunction handler;

	public EventHandler()
	{
		this.handler = createHandler(this);
	}

	public EventHandlerFunction getFunction()
	{
		return handler;
	}

	protected abstract void handleEvent(Event event);
}
