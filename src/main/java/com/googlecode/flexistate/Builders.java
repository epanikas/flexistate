package com.googlecode.flexistate;

import com.googlecode.flexistate.statemachine.FlexiStateBuilderImpl;

public class Builders
{

	public static FlexiStateBuilder<String> forAnnotatedDelegate(Object delegate)
	{
		return new FlexiStateBuilderImpl<String>(delegate, String.class);
	}

	/**
	 * Creates a state machine builder for the given event type and the given delegate.
	 * 
	 * @param <TEvent> - the type of the event
	 * @param eventClass - class designating the event type
	 * @param delegate - an object defining the state machine 
	 * @return - a state machine builder 
	 */
	public static <TEvent> FlexiStateBuilder<TEvent> forAnnotatedDelegate(Object delegate, Class<TEvent> eventClass)
	{
		return new FlexiStateBuilderImpl<TEvent>(delegate, eventClass);
	}

	public static <TEvent> FlexiStateBuilder<TEvent> forDelegateWithXML(String scxmlUrl, Object delegate,
																		Class<TEvent> eventClass)
	{
		return new FlexiStateBuilderImpl<TEvent>(scxmlUrl, delegate, eventClass);
	}

}
