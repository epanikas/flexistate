package com.googlecode.flexistate.statemachine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InvokationUtils
{
	public static void invoke(final Method m, Object delegate, QueueingStateMachine<?, ?> stateMachine)
	{
		List<Object> params = createParamsArray(m, stateMachine);

		/*
		 * finally create the list of parameters and call the method
		 */
		try {
			m.invoke(delegate, params.toArray());
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

	private static List<Object> createParamsArray(Method m, QueueingStateMachine<?, ?> stateMachine)
	{
		List<Object> params = new ArrayList<Object>();
		Class<?>[] paramTypes = m.getParameterTypes();

		Object event = stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_KEY);
		Object context = stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_CONTEXT_KEY);

		for (Class<?> type : paramTypes) {
			if (event != null && type.isAssignableFrom(event.getClass())) {
				params.add(event);
			}

			if (context != null && type.isAssignableFrom(context.getClass())) {
				params.add(context);
			}

			if (type.isAssignableFrom(stateMachine.getClass())) {
				params.add(stateMachine);
			}
		}

		return params;
	}
}
