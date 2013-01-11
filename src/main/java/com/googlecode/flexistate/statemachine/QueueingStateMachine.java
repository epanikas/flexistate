package com.googlecode.flexistate.statemachine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.SimpleDispatcher;
import org.apache.commons.scxml.env.SimpleErrorReporter;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;

import com.googlecode.flexistate.FlexiState;
import com.googlecode.flexistate.statemachine.listener.Delegatinglistener;
import com.googlecode.flexistate.statemachine.listener.TransitionSensorListener;

public class QueueingStateMachine<TEvent>
	implements FlexiState<TEvent>
{

	public static final String EVENT_KEY = "_EVENT_KEY";
	public static final String SKIP_ENTRY_KEY = "_SKIP_ENTRY_KEY";
	public static final String STATE_MACHINE_KEY = "_EXECUTING_STATE_MACHINE_KEY";
	public static final String DELEGATE_KEY = "_DELEGATE_KEY";

	private ConcurrentLinkedQueue<TEvent> eventsQueue = new ConcurrentLinkedQueue<TEvent>();

	private TransitionSensorListener transitionListener = new TransitionSensorListener();

	private final SCXML stateMachine;
	private SCXMLExecutor engine;
	private Log log = LogFactory.getLog(this.getClass());

	public QueueingStateMachine(SCXML stateMachine, final Object delegate, Class<TEvent> eventClass)
	{
		this.stateMachine = stateMachine;
		initialize(stateMachine, new JexlContext(), new JexlEvaluator(), new Delegatinglistener<TEvent>(delegate, this,
			eventClass));
	}

	public QueueingStateMachine(SCXML stateMachine, final SCXMLListener listener)
	{
		this.stateMachine = stateMachine;
		initialize(stateMachine, new JexlContext(), new JexlEvaluator(), listener);
	}

	@Override
	public void enqueue(TEvent event)
	{
		doEnqueue(event);
	}

	@Override
	public boolean processAll()
	{
		boolean res = false;
		while (processSingleEvent()) {
			res = true;
		}

		return res;
	}

	@Override
	public boolean trigger(TEvent event)
	{
		doEnqueue(event);
		return processSingleEvent();
	}

	private final void doEnqueue(TEvent event)
	{
		eventsQueue.add(event);
	}

	private boolean processSingleEvent()
	{

		TEvent item = eventsQueue.peek();
		if (item == null) {
			/*
			 * nothing to do here
			 */
			return false;
		}

		getEngine().getRootContext().set(EVENT_KEY, item);

		transitionListener.reset();

		String event = "";
		if (item instanceof Event) {
			event = ((Event) item).getEventName();
		}
		else if (item instanceof Enum<?>) {
			event = ((Enum<?>) item).name();
		}
		else {
			event = item.toString();
		}
		fireEvent(event);

		if (transitionListener.isExecutedTransition()) {
			eventsQueue.remove();
			return true;
		}

		return false;
	}

	private void initialize(final SCXML stateMachine, final Context rootCtx, final Evaluator evaluator,
							final SCXMLListener listener)
	{
		engine = new SCXMLExecutor(evaluator, new SimpleDispatcher(), new SimpleErrorReporter());
		engine.setStateMachine(stateMachine);
		engine.setSuperStep(true);
		engine.setRootContext(rootCtx);
		engine.addListener(stateMachine, listener);

		engine.addListener(stateMachine, transitionListener);

	}

	public boolean fireEvent(final String event)
	{
		TriggerEvent[] evts = {new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT, null)};
		try {
			engine.triggerEvents(evts);
		}
		catch (ModelException me) {
			logError(me);
		}
		return engine.getCurrentStatus().isFinal();
	}

	@Override
	public SCXML getStateMachine()
	{
		return stateMachine;
	}

	@Override
	public SCXMLExecutor getEngine()
	{
		return engine;
	}

	public boolean resetMachine()
	{
		try {
			engine.reset();
		}
		catch (ModelException me) {
			logError(me);
			return false;
		}
		return true;
	}

	protected void logError(final Exception exception)
	{
		if (log.isErrorEnabled()) {
			log.error(exception.getMessage(), exception);
		}
	}

	@Override
	public Queue<TEvent> getEventQueue()
	{
		throw new UnsupportedOperationException("getEventQueue not implemented");
	}

	@Override
	public void flushEventQueue()
	{
		eventsQueue.clear();
	}

}
