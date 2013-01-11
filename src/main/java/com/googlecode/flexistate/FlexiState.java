package com.googlecode.flexistate;

import java.util.Queue;

import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.model.SCXML;

import com.googlecode.flexistate.statemachine.EventWithContext;

public interface FlexiState<TEvent extends Enum<TEvent>, TEventContext>
{
	/**
	 * adds the event to the state machine's event queue (the associated context is set to null)
	 * 
	 * @param event
	 * @param context
	 */
	void enqueue(TEvent event);

	/**
	 * adds the event and its context to the state machine's event queue
	 * 
	 * @param event
	 * @param context
	 */
	void enqueue(TEvent event, TEventContext context);

	/**
	 * adds the event the state machine's event queue, and processes it immediately  (the associated context is set to null)
	 * 
	 * @param event
	 * @param context
	 * @return true - if a transition has occurred, false - otherwise
	 */
	boolean trigger(TEvent event);

	/**
	 * same as the previous, but the custom event context will be passed with the event
	 * 
	 * @param event
	 * @param context
	 * @return
	 */
	boolean trigger(TEvent event, TEventContext context);

	/**
	 * processes all the events in the event queue until the queue is empty, or no more transitions are made
	 * 
	 * @return true - if at least one transition has occurred, false otherwise
	 */
	boolean processAll();

	/**
	 * clears the event queue of the machine, without processing the events
	 */
	void flushEventQueue();

	/**
	 * returns the unmodifiable snapshot of the current state of the machine's event queue
	 * 
	 * @return
	 */
	Queue<EventWithContext<TEvent, TEventContext>> getEventQueue();

	/**
	 * get the underlying SCXML state machine
	 * 
	 * @return
	 */
	SCXML getStateMachine();

	/**
	 * get the underlying state machine engine
	 * 
	 * @return
	 */
	SCXMLExecutor getEngine();

}
