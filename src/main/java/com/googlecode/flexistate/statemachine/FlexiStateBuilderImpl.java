package com.googlecode.flexistate.statemachine;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.googlecode.flexistate.FlexiState;
import com.googlecode.flexistate.FlexiStateBuilder;
import com.googlecode.flexistate.annotation.TransitionAction;
import com.googlecode.flexistate.annotation.TransitionSet;
import com.googlecode.flexistate.statemachine.model.StateModel;
import com.googlecode.flexistate.statemachine.model.TransitionModel;

public class FlexiStateBuilderImpl<TEvent>
	implements FlexiStateBuilder<TEvent>
{
	private static final Class<?>[] EMPTY_SIGNATURE = new Class<?>[]{};
	private static final Object[] EMPTY_PARAMETERS = new Object[]{};

	private Class<TEvent> eventClass;

	private Class<? extends Annotation> transitionsAnnotation = TransitionSet.class;
	private Class<? extends Annotation> onTransitionAnnotation = TransitionAction.class;

	private Object delegate;
	private String scxmlUrl;

	private String advanceToState;
	private boolean skipEntryAction;

	public FlexiStateBuilderImpl(Object delegate, Class<TEvent> eventClass)
	{
		this.eventClass = eventClass;
		this.delegate = delegate;
	}

	public FlexiStateBuilderImpl(String scxmlUrl, Object delegate, Class<TEvent> eventClass)
	{
		this.scxmlUrl = scxmlUrl;
		this.eventClass = eventClass;
		this.delegate = delegate;
	}

	@Override
	public <T1 extends Annotation> FlexiStateBuilderImpl<TEvent> transitionSetAnnotation(Class<T1> transitionsAnnotation)
	{
		this.transitionsAnnotation = transitionsAnnotation;
		return this;
	}

	@Override
	public <T extends Annotation> FlexiStateBuilderImpl<TEvent> transitionActionAnnotation(	Class<T> onTransitionAnnotation)
	{
		this.onTransitionAnnotation = onTransitionAnnotation;
		return this;
	}

	@Override
	public FlexiStateBuilderImpl<TEvent> advanceToState(String state)
	{
		this.advanceToState = state;
		this.skipEntryAction = true;
		return this;
	}

	@Override
	public FlexiStateBuilder<TEvent> advanceAndExecute(String state)
	{
		this.advanceToState = state;
		this.skipEntryAction = false;
		return this;
	}

	@Override
	public FlexiState<TEvent> build()
	{

		SCXML scxml = null;

		if (scxmlUrl != null) {

			List<CustomAction> customActions = new ArrayList<CustomAction>();
			CustomAction ca =
				new CustomAction("http://flexistate.googlecode.com/schema/custom-actions", "action-method",
					MethodAction.class);
			customActions.add(ca);

			ErrorHandler errHandler = new SimpleErrorHandler();
			try {
				scxml = SCXMLParser.parse(getClass().getClassLoader().getResource(scxmlUrl), errHandler, customActions);
			}
			catch (IOException ioe) {
				throw new IllegalArgumentException(ioe);
			}
			catch (SAXException sae) {
				throw new IllegalArgumentException(sae);
			}
			catch (ModelException me) {
				throw new IllegalArgumentException(me);
			}
		}
		else {
			Map<String, StateModel> states = null;
			try {
				states = extractModelUsingAnnotations(delegate, transitionsAnnotation, onTransitionAnnotation);
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}

			System.out.println(states);
			scxml = createSCXML(states);
		}
		QueueingStateMachine<TEvent> stateMachine = new QueueingStateMachine<TEvent>(scxml, delegate, eventClass);

		stateMachine.getEngine().getRootContext().set(QueueingStateMachine.STATE_MACHINE_KEY, stateMachine);
		stateMachine.getEngine().getRootContext().set(QueueingStateMachine.DELEGATE_KEY, delegate);

		if (advanceToState != null) {
			scxml.setInitialTarget((TransitionTarget) scxml.getChildren().get(advanceToState));
			if (skipEntryAction) {
				stateMachine.getEngine().getRootContext().set(QueueingStateMachine.SKIP_ENTRY_KEY, Boolean.TRUE);
			}
		}

		/*
		 * finally initialize the machine
		 */
		stateMachine.resetMachine();

		/*
		 * return the context to its initial state
		 */
		stateMachine.getEngine().getRootContext().set(QueueingStateMachine.SKIP_ENTRY_KEY, Boolean.FALSE);

		return stateMachine;
	}

	private static Map<String, StateModel> extractModelUsingAnnotations(Object delegate,
																		Class<? extends Annotation> transitionSetAnnotationClass,
																		Class<? extends Annotation> transitionActionAnnotationClass)
		throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Map<String, StateModel> states = new HashMap<String, StateModel>();

		for (Method m : delegate.getClass().getMethods()) {

			if (m.getAnnotation(com.googlecode.flexistate.annotation.State.class) == null) {
				continue;
			}

			com.googlecode.flexistate.annotation.State stateAnnotation =
				m.getAnnotation(com.googlecode.flexistate.annotation.State.class);

			StateModel currentState = new StateModel(m, stateAnnotation.initial());
			states.put(m.getName(), currentState);

			if (m.getAnnotation(transitionSetAnnotationClass) != null) {
				Annotation transitions = m.getAnnotation(transitionSetAnnotationClass);

				Method mValue = transitions.getClass().getMethod("value");
				Annotation[] transitionAnnotations = (Annotation[]) mValue.invoke(transitions, EMPTY_PARAMETERS);
				for (Annotation ann : transitionAnnotations) {

					Method eventMethod = ann.getClass().getMethod("event", EMPTY_SIGNATURE);
					Method targetMethod = ann.getClass().getMethod("target", EMPTY_SIGNATURE);

					String event = convertToString(eventMethod.invoke(ann, EMPTY_PARAMETERS));
					String target = convertToString(targetMethod.invoke(ann, EMPTY_PARAMETERS));

					currentState.addTransition(new TransitionModel(event, target));
				}
			}
		}

		for (Method m : delegate.getClass().getMethods()) {

			if (m.getAnnotation(transitionActionAnnotationClass) == null) {
				continue;
			}

			Annotation actionAnnotation = m.getAnnotation(transitionActionAnnotationClass);

			Method fromMethod = transitionActionAnnotationClass.getMethod("from", EMPTY_SIGNATURE);
			Method toMethod = transitionActionAnnotationClass.getMethod("to", EMPTY_SIGNATURE);

			String from = convertToString(fromMethod.invoke(actionAnnotation, EMPTY_PARAMETERS));
			String to = convertToString(toMethod.invoke(actionAnnotation, EMPTY_PARAMETERS));

			StateModel sm = states.get(from);
			if (sm == null) {
				throw new IllegalArgumentException("'from' state not found " + from + " for " + actionAnnotation);
			}
			TransitionModel actionTransition = null;

			for (TransitionModel tm : sm.getTransitions()) {
				if (tm.getTarget().equals(to)) {
					actionTransition = tm;
				}
			}

			if (actionTransition == null) {
				throw new IllegalArgumentException("'to' state not found " + to + " for " + actionAnnotation);
			}

			actionTransition.setAction(m);
		}

		return states;
	}

	private static void printArray(String name, Object... arr)
	{

		System.out.println(name + " has " + arr.length + " elems");
		if (arr.length > 0) {
			for (Object a : arr) {
				System.out.println(" --> " + a);
			}
		}
	}

	private SCXML createSCXML(Map<String, StateModel> states)
	{
		SCXML scxml = new SCXML();
		State initialState = null;

		for (StateModel sm : states.values()) {

			State st = new State();
			st.setId(sm.getName());
			if (sm.isInitial()) {
				if (initialState != null) {
					throw new IllegalArgumentException("can't have more than one initial state; already found  "
						+ initialState + ", and then found " + sm);
				}
				initialState = st;
			}
			scxml.addChild(st);
		}

		for (StateModel sm : states.values()) {
			State st = (State) scxml.getChildren().get(sm.getName());

			for (TransitionModel tm : sm.getTransitions()) {
				Transition tr = new Transition();
				tr.setEvent(tm.getEvent());

				if (tm.getActionMethod() != null) {
					tr.addAction(new MethodAction(tm.getActionMethod()));
				}

				State target = (State) scxml.getChildren().get(tm.getTarget());

				@SuppressWarnings("unchecked")
				List<TransitionTarget> targets = tr.getTargets();
				targets.add(target);
				st.addTransition(tr);
			}
		}

		if (initialState == null) {
			throw new IllegalArgumentException("initial state must be specified on the state machine " + delegate);
		}

		scxml.setInitial(initialState.getId());
		scxml.setInitialTarget(initialState);

		return scxml;
	}

	private static String convertToString(Object obj)
	{
		if (obj instanceof String) {
			return (String) obj;
		}
		else if (obj instanceof Enum) {
			return ((Enum<?>) obj).name();
		}

		throw new IllegalArgumentException("unrecognized state " + obj);
	}

}
