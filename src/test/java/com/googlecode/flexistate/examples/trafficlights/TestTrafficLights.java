package com.googlecode.flexistate.examples.trafficlights;

import org.apache.commons.scxml.model.State;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.flexistate.Builders;
import com.googlecode.flexistate.FlexiState;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionAction;
import com.googlecode.flexistate.examples.trafficlights.annotation.TLTransitionSet;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEvent;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEventWithContext;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLights;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithAnnotations;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithContext;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithCustomAnnotations;
import com.googlecode.flexistate.examples.trafficlights.service.TrafficLightsWithTransitionAction;

public class TestTrafficLights
{

	@Test
	public void testTrigger()
	{

		/*
		 * when
		 */
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class).build();

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
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.switchOn);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
	}

	@Test
	public void testProcessAllInversed()
	{
		/*
		 * given
		 */
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.switchOn);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
	}

	@Test
	public void testProcessAllBadEvent()
	{
		/*
		 * given
		 */
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(TrafficLightsEvent.timer);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals(TrafficLightsState.off, trafficLights.getState());
	}

	@Test
	public void testProcessAllWithCustomContext()
	{
		/*
		 * given
		 */
		TrafficLightsWithContext trafficLights = new TrafficLightsWithContext();

		FlexiState<TrafficLightsEventWithContext> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEventWithContext.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.switchOn));
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.timer));
		TrafficLightsEventWithContext last = new TrafficLightsEventWithContext(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(last);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals("You can go !!!", last.getGreeting());
	}

	@Test
	public void testCustomAnnotations()
	{
		/*
		 * when
		 */
		TrafficLightsWithCustomAnnotations trafficLights = new TrafficLightsWithCustomAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class)
				.transitionSetAnnotation(TLTransitionSet.class).transitionActionAnnotation(TLTransitionAction.class)
				.build();

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
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class)
				.transitionSetAnnotation(TLTransitionSet.class).transitionActionAnnotation(TLTransitionAction.class)
				.advanceToState("red").build();

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
		TrafficLightsWithAnnotations trafficLights = new TrafficLightsWithAnnotations();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEvent.class)
				.transitionSetAnnotation(TLTransitionSet.class).transitionActionAnnotation(TLTransitionAction.class)
				.advanceAndExecute("red").build();

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

		FlexiState<TrafficLightsEventWithContext> trafficLightsStateMachine =
			Builders.forAnnotatedDelegate(trafficLights, TrafficLightsEventWithContext.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.switchOn));
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.timer));
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.timer));
		TrafficLightsEventWithContext last = new TrafficLightsEventWithContext(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(last);
		trafficLightsStateMachine.processAll();

		/*
		 * should
		 */
		Assert.assertEquals("You can go !!!", last.getGreeting());
	}

	@Test
	public void testSCXML()
	{

		/*
		 * when
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEvent> trafficLightsStateMachine =
			Builders.forDelegateWithXML("traffic-lights.xml", trafficLights, TrafficLightsEvent.class).build();

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
	public void testSCXMLAndTransitionAction()
	{

		/*
		 * when
		 */
		TrafficLights trafficLights = new TrafficLights();

		FlexiState<TrafficLightsEventWithContext> trafficLightsStateMachine =
			Builders.forDelegateWithXML("traffic-lights-with-action.xml", trafficLights,
				TrafficLightsEventWithContext.class).build();

		/*
		 * when
		 */
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.switchOn));
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.timer));
		trafficLightsStateMachine.enqueue(new TrafficLightsEventWithContext(TrafficLightsEvent.timer));
		TrafficLightsEventWithContext last = new TrafficLightsEventWithContext(TrafficLightsEvent.timer);
		trafficLightsStateMachine.enqueue(last);
		trafficLightsStateMachine.processAll();

		/*
		 * should 
		 */
		Assert.assertEquals("You can go !!!", last.getGreeting());
	}

}
