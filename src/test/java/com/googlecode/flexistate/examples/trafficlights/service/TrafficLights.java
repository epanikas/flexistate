package com.googlecode.flexistate.examples.trafficlights.service;

import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEventWithContext;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

public class TrafficLights
{

	private TrafficLightsState state = TrafficLightsState.off;

	public void off()
	{
		state = TrafficLightsState.off;
	}

	public void red()
	{
		state = TrafficLightsState.red;
	}

	public void amber()
	{
		state = TrafficLightsState.amber;
	}

	public void green()
	{
		state = TrafficLightsState.green;
	}

	public void green2Red(TrafficLightsEventWithContext event)
	{
		event.setGreeting("You can go !!!");
	}

	public TrafficLightsState getState()
	{
		return state;
	}

	public boolean isOff()
	{
		return state == TrafficLightsState.off;
	}

	public boolean isOn()
	{
		return state != TrafficLightsState.off;
	}
}
