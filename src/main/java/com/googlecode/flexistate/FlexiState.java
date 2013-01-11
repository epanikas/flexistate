package com.googlecode.flexistate;

import java.util.Queue;

import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.model.SCXML;

public interface FlexiState<TEvent>
{
	/**
	 * adds the event to the state machine's event queue
	 * 
	 * @param event
	 * @param context
	 */
	void enqueue(TEvent event);

	/**
	 * adds the event the state machine's event queue, and processes it immediately  
	 * 
	 * @param event
	 * @param context
	 * @return true - if a transition has occurred, false - otherwise
	 */
	boolean trigger(TEvent event);

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
	Queue<TEvent> getEventQueue();

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
