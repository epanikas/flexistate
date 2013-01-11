package com.googlecode.flexistate.examples.trafficlights;

import junit.framework.Assert;

import org.apache.commons.scxml.model.State;
import org.junit.Test;

import com.googlecode.flexistate.Builders;
import com.googlecode.flexistate.FlexiState;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionAction;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionSet;
import com.googlecode.flexistate.examples.trafficlights.context.TrafficLightsContext;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEvent;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLights;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithContext;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithCustomAnnotations;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithTransitionAction;

public class TestTrafficLights
{

	@Test
	public void testProcessSingle()
	{

		/*
		 * when
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
			Builders.forEvent(TrafficLightsEvent.class, trafficLights).build();

		/*
		 * should 
		 */
		Assert.assertEquals(TrafficLightsState.off, trafficLights.getState());

		trafficLightsStateMachine.trigger(TrafficLightsEvent.switchOn);
		Assert.assertEquals(TrafficLightsState.red, trafficLights.getState());

		trafficLightsStateMachine.trigger(TrafficLightsEvent.timer);
		Assert.assertEquals(TrafficLightsState.amber, trafficLights.getState());

		trafficLightsStateMachine.trigger(TrafficLightsEvent.timer);
		Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
	}

	@Test
	public void testProcessAll()
	{
		/*
		 * given
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
			Builders.forEvent(TrafficLightsEvent.class, trafficLights).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.switchOn, null);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer, null);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer, null);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
	}

	@Test
	public void testProcessAllWithCustomContext()
	{
		/*
		 * given
		 */
		TrafficLightsWithContext trafficLights = new TrafficLightsWithContext();
		TrafficLightsContext context = new TrafficLightsContext();

		FlexiState<TrafficLightsEvent, TrafficLightsContext> trafficLightsStateMachine =
			Builders.forEventWithContext(TrafficLightsEvent.class, TrafficLightsContext.class, trafficLights).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.switchOn);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer, context);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(1, context.getNumberOfCycles());
	}

	@Test
	public void testCustomAnnotations()
	{
		/*
		 * when
		 */
		TrafficLightsWithCustomAnnotations trafficLights = new TrafficLightsWithCustomAnnotations();

		FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
			Builders.forEvent(TrafficLightsEvent.class, trafficLights).transitionSetAnnotation(TLTransitionSet.class)
				.transitionActionAnnotation(TLTransitionAction.class).build();

		/*
		 * should
		 */
		trafficLightsStateMachine.trigger(TrafficLightsEvent.switchOn);
		Assert.assertEquals(TrafficLightsState.red, trafficLights.getState());

		trafficLightsStateMachine.trigger(TrafficLightsEvent.timer);
		Assert.assertEquals(TrafficLightsState.amber, trafficLights.getState());

		trafficLightsStateMachine.trigger(TrafficLightsEvent.timer);
		Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
	}

	@Test
	public void testAdvanceToState()
	{
		/*
		 * given
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
			Builders.forEvent(TrafficLightsEvent.class, trafficLights).transitionSetAnnotation(TLTransitionSet.class)
				.transitionActionAnnotation(TLTransitionAction.class).advanceToState("red").build();

		/*
		 * when
		 */
		State currentState =
			(State) trafficLightsStateMachine.getEngine().getCurrentStatus().getStates().iterator().next();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.red, TrafficLightsState.valueOf(currentState.getId()));
		Assert.assertEquals(TrafficLightsState.off, trafficLights.getState());

	}

	@Test
	public void testAdvanceAndExecute()
	{
		/*
		 * given
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
			Builders.forEvent(TrafficLightsEvent.class, trafficLights).transitionSetAnnotation(TLTransitionSet.class)
				.transitionActionAnnotation(TLTransitionAction.class).advanceAndExecute("red").build();

		/*
		 * when
		 */
		State currentState =
			(State) trafficLightsStateMachine.getEngine().getCurrentStatus().getStates().iterator().next();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.red, TrafficLightsState.valueOf(currentState.getId()));
		Assert.assertEquals(TrafficLightsState.red, trafficLights.getState());

	}

	@Test
	public void testTransitionAction()
	{
		/*
		 * given
		 */
		TrafficLightsWithTransitionAction trafficLights = new TrafficLightsWithTransitionAction();

		FlexiState<TrafficLightsEvent, TrafficLightsContext> trafficLightsStateMachine =
			Builders.forEventWithContext(TrafficLightsEvent.class, TrafficLightsContext.class, trafficLights).build();

		TrafficLightsContext context = new TrafficLightsContext();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.switchOn);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer, context);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(1, context.getNumberOfCycles());
	}
}
