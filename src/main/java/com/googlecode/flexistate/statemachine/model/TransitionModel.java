package com.googlecode.flexistate.statemachine.model;

import java.lang.reflect.Method;

public class TransitionModel
{
	private final String target;
	private final String event;
	private Method action;

	public TransitionModel(String event, String to)
	{
		this.target = to;
		this.event = event;
	}

	public String getTarget()
	{
		return target;
	}

	public String getEvent()
	{
		return event;
	}

	public void setAction(Method m)
	{
		this.action = m;
	}

	public Method getActionMethod()
	{
		return action;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("TransitionModel [on " + event + " -> " + target).append("]");
		return builder.toString();
	}

}
