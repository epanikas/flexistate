package com.googlecode.flexistate.statemachine.listener;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

public class TransitionSensorListener
	implements SCXMLListener
{

	private boolean executedTransition = false;

	@Override
	public void onEntry(TransitionTarget state)
	{
		// we don't care
	}

	@Override
	public void onExit(TransitionTarget state)
	{
		// we don't care
	}

	@Override
	public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition)
	{
		executedTransition = true;
	}

	public boolean isExecutedTransition()
	{
		return executedTransition;
	}

	public void reset()
	{
		executedTransition = false;
	}
}