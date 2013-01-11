package com.googlecode.flexistate.examples.trafficlights.enumeration;

import com.googlecode.flexistate.statemachine.Event;

public class TrafficLightsEventWithContext
	implements Event
{

	private final TrafficLightsEvent event;
	private String greeting;

	public TrafficLightsEventWithContext(TrafficLightsEvent event)
	{
		this.event = event;
	}

	@Override
	public String getEventName()
	{
		return event.name();
	}

	public void setGreeting(String greeting)
	{
		this.greeting = greeting;
	}

	public String getGreeting()
	{
		return greeting;
	}

}
