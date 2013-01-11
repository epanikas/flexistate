package com.googlecode.flexistate.examples.trafficlights.service;

import com.googlecode.flexistate.annotation.StateMachine;
import com.googlecode.flexistate.annotation.State;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransition;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionAction;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionSet;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEvent;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

@StateMachine
public class TrafficLightsWithCustomAnnotations
{

	private TrafficLightsState state;

	@State(initial = true)
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOn, target = TrafficLightsState.red)})
	public void off()
	{
		state = null;
	}

	@State
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOff, target = TrafficLightsState.off),
						@TLTransition(event = TrafficLightsEvent.timer, target = TrafficLightsState.amber)})
	public void red()
	{
		state = TrafficLightsState.red;
	}

	@State
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOff, target = TrafficLightsState.off),
						@TLTransition(event = TrafficLightsEvent.timer, target = TrafficLightsState.green)})
	public void amber()
	{
		state = TrafficLightsState.amber;
	}

	@State
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOff, target = TrafficLightsState.off),
						@TLTransition(event = TrafficLightsEvent.timer, target = TrafficLightsState.red)})
	public void green()
	{
		state = TrafficLightsState.green;
	}

	@TLTransitionAction(from = TrafficLightsState.green, to = TrafficLightsState.red)
	public void green2Red()
	{
		System.out.println("New cycle...");
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
