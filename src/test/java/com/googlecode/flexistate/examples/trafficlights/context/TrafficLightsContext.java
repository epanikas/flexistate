package com.googlecode.flexistate.examples.trafficlights.context;

public class TrafficLightsContext
{

	private int cycles = 0;

	public void incCycles()
	{
		this.cycles++;
	}

	public int getNumberOfCycles()
	{
		return cycles;
	}

}
