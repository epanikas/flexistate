package com.googlecode.flexistate;

import com.googlecode.flexistate.statemachine.FlexiStateBuilderImpl;

public class Builders
{

	/**
	 * Creates a state machine builder for the given types of event and context, as well as for the given delegate
	 * 
	 * @param <TEvent> - type of the event, should be an enum
	 * @param <TEventContext> - type of the context
	 * @param eventClass - class designating the type of the event
	 * @param contextClass - class designating the type of the context
	 * @param delegate - an object defining the state machine 
	 * @return - a state machine builder 
	 */
	public static <TEvent extends Enum<TEvent>, TEventContext> FlexiStateBuilder<TEvent, TEventContext> forEventWithContext(Class<TEvent> eventClass,
																															Class<TEventContext> contextClass,
																															Object delegate)
	{
		return new FlexiStateBuilderImpl<TEvent, TEventContext>(eventClass, contextClass, delegate);
	}

	/**
	 * Creates a state machine builder for the given event type and the given delegate. The event context type is set to Void and is passed as null.
	 * 
	 * @param <TEvent> - the type of the event
	 * @param eventClass - class designating the event type
	 * @param delegate - an object defining the state machine 
	 * @return - a state machine builder 
	 */
	public static <TEvent extends Enum<TEvent>> FlexiStateBuilder<TEvent, Void> forEvent(Class<TEvent> eventClass,
																							Object delegate)
	{
		return new FlexiStateBuilderImpl<TEvent, Void>(eventClass, Void.class, delegate);
	}

}
