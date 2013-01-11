package com.googlecode.flexistate.examples.trafficlights.service;

import com.googlecode.flexistate.annotation.ExecutionType;
import com.googlecode.flexistate.annotation.StateMachine;
import com.googlecode.flexistate.annotation.StateMethod;
import com.googlecode.flexistate.annotation.Transition;
import com.googlecode.flexistate.annotation.TransitionSet;
import com.googlecode.flexistate.examples.trafficlights.context.TrafficLightsContext;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

@StateMachine
public class TrafficLightsWithContext
{

	private TrafficLightsState state;

	@StateMethod(initial = true)
	@TransitionSet({@Transition(event = "switchOn", target = "red")})
	public void off()
	{
		state = TrafficLightsState.off;
	}

	@StateMethod
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "amber")})
	public void red()
	{
		state = TrafficLightsState.red;
	}

	@StateMethod(executeOn = ExecutionType.exit)
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "green")})
	public void amber()
	{
		state = TrafficLightsState.amber;
	}

	@StateMethod
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "red")})
	public void green(TrafficLightsContext context)
	{
		state = TrafficLightsState.green;
		context.incCycles();
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
