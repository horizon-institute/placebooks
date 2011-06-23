package placebooks.client.ui.openlayers;

public abstract class EventHandler
{
	public native static EventHanderFunction createHandler(EventHandler self)
	/*-{
		var handler = function(event)
		{
			self.@placebooks.client.ui.openlayers.EventHandler::handleEvent(Lplacebooks/client/ui/openlayers/Event;)(event);
		}
		return handler;
	}-*/;

	private EventHanderFunction handler;

	public EventHandler()
	{
		this.handler = createHandler(this);
	}

	public EventHanderFunction getFunction()
	{
		return handler;
	}

	protected abstract void handleEvent(Event event);
}
