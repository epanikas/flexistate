package com.googlecode.flexistate.statemachine;

public class EventWithContext<TEvent, TEventContext>
{
	private final TEvent event;
	private final TEventContext eventContext;

	public EventWithContext(TEvent event, TEventContext eventContext)
	{
		this.event = event;
		this.eventContext = eventContext;
	}

	public TEvent getEvent()
	{
		return event;
	}

	public TEventContext getEventContext()
	{
		return eventContext;
	}

}
