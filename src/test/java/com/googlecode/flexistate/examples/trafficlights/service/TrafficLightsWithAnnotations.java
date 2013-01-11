package com.googlecode.flexistate.examples.trafficlights.service;

import com.googlecode.flexistate.annotation.ExecuteOn;
import com.googlecode.flexistate.annotation.State;
import com.googlecode.flexistate.annotation.StateMachine;
import com.googlecode.flexistate.annotation.Transition;
import com.googlecode.flexistate.annotation.TransitionSet;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

@StateMachine
public class TrafficLightsWithAnnotations
{

	private TrafficLightsState state = TrafficLightsState.off;

	@State(initial = true)
	@TransitionSet({@Transition(event = "switchOn", target = "red")})
	public void off()
	{
		state = TrafficLightsState.off;
	}

	@State
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "amber")})
	public void red()
	{
		state = TrafficLightsState.red;
	}

	@State(ExecuteOn.exit)
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "green")})
	public void amber()
	{
		state = TrafficLightsState.amber;
	}

	@State
	@TransitionSet({@Transition(event = "switchOff", target = "off"),@Transition(event = "timer", target = "red")})
	public void green()
	{
		state = TrafficLightsState.green;
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
