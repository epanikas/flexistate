package com.googlecode.flexistate.statemachine.listener;

import java.util.Arrays;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

import com.googlecode.flexistate.statemachine.InvokationUtils;
import com.googlecode.flexistate.statemachine.QueueingStateMachine;

public class Delegatinglistener<TEvent>
	implements SCXMLListener
{

	private final Object delegate;
	private final QueueingStateMachine<TEvent> stateMachine;
	private final Class<TEvent> eventClass;

	public Delegatinglistener(Object delegate, QueueingStateMachine<TEvent> stateMachine, Class<TEvent> eventClass)
	{
		this.delegate = delegate;
		this.stateMachine = stateMachine;
		this.eventClass = eventClass;
	}

	@Override
	public void onEntry(TransitionTarget state)
	{

		Object skipEntry = stateMachine.getEngine().getRootContext().get(QueueingStateMachine.SKIP_ENTRY_KEY);

		if (Boolean.TRUE.equals(skipEntry)) {
			/*
			 * don't execute the entry action, if specified so (probably for initialization purposes)
			 */
			return;
		}

		@SuppressWarnings("unchecked")
		TEvent event = (TEvent) stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_KEY);

		InvokationUtils.invoke(delegate, state.getId(), Arrays.asList(stateMachine, event));
	}

	@Override
	public void onExit(TransitionTarget state)
	{
		// nothing to do
	}

	@Override
	public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition)
	{
		// nothing to do
	}

}
