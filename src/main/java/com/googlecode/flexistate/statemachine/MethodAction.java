package com.googlecode.flexistate.statemachine;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;

public class MethodAction
	extends Action
{

	private static final long serialVersionUID = -4098473648836193395L;

	private final Method method;
	private String name;

	public MethodAction()
	{
		method = null;
	}

	public MethodAction(Method method)
	{
		this.method = method;
	}

	public Method getMethod()
	{
		return method;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void execute(EventDispatcher evtDispatcher, ErrorReporter errRep, SCInstance scInstance, Log appLog,
						Collection derivedEvents)
		throws ModelException, SCXMLExpressionException
	{
		QueueingStateMachine<?> stateMachine =
			(QueueingStateMachine<?>) scInstance.getRootContext().get(QueueingStateMachine.STATE_MACHINE_KEY);

		Object delegate = scInstance.getRootContext().get(QueueingStateMachine.DELEGATE_KEY);

		Object event = stateMachine.getEngine().getRootContext().get(QueueingStateMachine.EVENT_KEY);

		if (method != null) {
			InvokationUtils.invoke(delegate, method.getName(), Arrays.asList(stateMachine, event));
		}
		else {
			InvokationUtils.invoke(delegate, name, Arrays.asList(stateMachine, event));
		}
	}
}
