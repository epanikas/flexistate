package com.googlecode.flexistate.statemachine.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

import com.googlecode.flexistate.annotation.StateMethod;
import com.googlecode.flexistate.statemachine.QueueingStateMachine;

public class Delegatinglistener<TEvent extends Enum<TEvent>, TEventContext>
	implements SCXMLListener
{

	private final Object delegate;
	private final QueueingStateMachine<TEvent, TEventContext> stateMachine;
	private final Class<TEvent> eventClass;
	private final Class<TEventContext> contextClass;

	private static class MethodWtihParameters
	{
		private Method method;
		private List<Object> parameters;

		public MethodWtihParameters(Method method, List<Object> parameters)
		{
			this.method = method;
			this.parameters = parameters;
		}

	}

	public Delegatinglistener(Object delegate, QueueingStateMachine<TEvent, TEventContext> stateMachine,
					Class<TEvent> eventClass, Class<TEventContext> contextClass)
	{
		this.delegate = delegate;
		this.stateMachine = stateMachine;
		this.eventClass = eventClass;
		this.contextClass = contextClass;
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

		invoke(state.getId());
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

	private void invoke(final String methodName)
	{
		List<MethodWtihParameters> methods = findMethodCandidates(methodName);

		if (methods.size() == 0) {
			throw new IllegalArgumentException("no method found for " + methodName);
		}

		/*
		 * now find the best match (max number of arguments)
		 */
		MethodWtihParameters candidate = methods.get(0);
		for (MethodWtihParameters m : methods) {
			if (candidate.parameters.size() < m.parameters.size()) {
				candidate = m;
			}
		}

		/*
		 * finally create the list of parameters and call the method
		 */
		try {
			candidate.method.invoke(delegate, candidate.parameters.toArray());
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
		catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private List<MethodWtihParameters> findMethodCandidates(String methodName)
	{
		Class<?> clas = delegate.getClass();
		Method[] methods = clas.getDeclaredMethods();
		List<MethodWtihParameters> candidates = new ArrayList<Delegatinglistener.MethodWtihParameters>();
		for (Method m : methods) {
			List<Object> paramsContainer = new ArrayList<Object>();
			if (isCandidate(m, methodName, paramsContainer)) {
				candidates.add(new MethodWtihParameters(m, paramsContainer));
			}
		}

		return candidates;
	}

	private boolean isCandidate(Method m, String methodName, List<Object> paramsContainer)
	{
		if (m.getName().equals(methodName) == false) {
			return false;
		}

		if (m.getAnnotation(StateMethod.class) == null) {
			return false;
		}

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length == 0) {
			return true;
		}

		@SuppressWarnings("unchecked")
		TEvent event = (TEvent) stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_KEY);
		@SuppressWarnings("unchecked")
		TEventContext context =
			(TEventContext) stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_CONTEXT_KEY);

		for (Class<?> type : paramTypes) {
			if (type.isAssignableFrom(eventClass)) {
				paramsContainer.add(event);
			}

			if (type.isAssignableFrom(contextClass)) {
				paramsContainer.add(context);
			}

			if (type.isAssignableFrom(stateMachine.getClass())) {
				paramsContainer.add(stateMachine);
			}
		}

		/*
		 * we found match for every type
		 */
		return paramsContainer.size() == paramTypes.length;
	}
}
