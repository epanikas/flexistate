package com.googlecode.flexistate.statemachine;

import java.lang.reflect.Method;
import java.util.List;

public class MethodWtihParameters
{
	private Method method;
	private List<Object> parameters;

	public MethodWtihParameters(Method method, List<Object> parameters)
	{
		this.method = method;
		this.parameters = parameters;
	}

	public Method getMethod()
	{
		return method;
	}

	public List<Object> getParameters()
	{
		return parameters;
	}
}
