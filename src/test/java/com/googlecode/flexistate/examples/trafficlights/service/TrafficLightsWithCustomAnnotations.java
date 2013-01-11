package com.googlecode.flexistate.examples.trafficlights.service;

import com.googlecode.flexistate.annotation.ExecutionType;
import com.googlecode.flexistate.annotation.StateMachine;
import com.googlecode.flexistate.annotation.StateMethod;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransition;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionAction;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionSet;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEvent;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

@StateMachine
public class TrafficLightsWithCustomAnnotations
{

	private TrafficLightsState state;

	@StateMethod(initial = true)
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOn, target = TrafficLightsState.red)})
	public void off()
	{
		state = null;
	}

	@StateMethod
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOff, target = TrafficLightsState.off),
					@TLTransition(event = TrafficLightsEvent.timer, target = TrafficLightsState.amber)})
	public void red()
	{
		state = TrafficLightsState.red;
	}

	@StateMethod(executeOn = ExecutionType.exit)
	@TLTransitionSet({@TLTransition(event = TrafficLightsEvent.switchOff, target = TrafficLightsState.off),
					@TLTransition(event = TrafficLightsEvent.timer, target = TrafficLightsState.green)})
	public void amber()
	{
		state = TrafficLightsState.amber;
	}

	@StateMethod
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
