package com.googlecode.flexistate.statemachine.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateModel
{
	private final Method exec;
	private final boolean initial;
	private final List<TransitionModel> transitions = new ArrayList<TransitionModel>();

	public StateModel(Method exec, boolean initial)
	{
		this.exec = exec;
		this.initial = initial;
	}

	public void addTransition(TransitionModel mdl)
	{
		transitions.add(mdl);
	}

	public List<TransitionModel> getTransitions()
	{
		return Collections.unmodifiableList(transitions);
	}

	public String getName()
	{
		return exec.getName();
	}

	public boolean isInitial()
	{
		return initial;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("StateModel [exec=").append(exec.getName()).append(", transitions=").append(transitions)
			.append("]");
		return builder.toString();
	}

}
