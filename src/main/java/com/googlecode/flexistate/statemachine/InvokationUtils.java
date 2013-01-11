package com.googlecode.flexistate.statemachine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InvokationUtils
{

	private static List<Object> createParamsArray(Method m, List<Object> availableParams)
	{
		List<Object> params = new ArrayList<Object>();
		Class<?>[] paramTypes = m.getParameterTypes();

		for (Class<?> type : paramTypes) {
			for (Object param : availableParams) {
				if (param != null && type.isAssignableFrom(param.getClass())) {
					params.add(param);
				}
			}
		}

		return params;
	}

	private static List<MethodWtihParameters> findMethodCandidates(Object delegate, String methodName,
																	List<Object> availableParams)
	{
		Class<?> clas = delegate.getClass();
		Method[] methods = clas.getDeclaredMethods();
		List<MethodWtihParameters> candidates = new ArrayList<MethodWtihParameters>();
		for (Method m : methods) {
			List<Object> paramsContainer = new ArrayList<Object>();
			if (isCandidate(m, methodName, availableParams, paramsContainer)) {
				candidates.add(new MethodWtihParameters(m, paramsContainer));
			}
		}

		return candidates;
	}

	private static boolean isCandidate(Method m, String methodName, List<Object> availableParams,
										List<Object> paramsContainer)
	{
		if (m.getName().equals(methodName) == false) {
			return false;
		}

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length == 0) {
			return true;
		}

		paramsContainer.addAll(createParamsArray(m, availableParams));

		/*
		 * we found match for every type
		 */
		return paramsContainer.size() == paramTypes.length;
	}

	public static void invoke(final Object delegate, final Method method, List<Object> availableParams)
	{
		List<Object> params = createParamsArray(method, availableParams);

		/*
		 * finally create the list of parameters and call the method
		 */
		try {
			method.invoke(delegate, params.toArray());
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

	public static void invoke(final Object delegate, final String methodName, List<Object> availableParams)
	{

		List<MethodWtihParameters> methods =
			InvokationUtils.findMethodCandidates(delegate, methodName, availableParams);

		if (methods.size() == 0) {
			throw new IllegalArgumentException("no method found for " + methodName);
		}

		/*
		 * now find the best match (max number of arguments)
		 */
		MethodWtihParameters candidate = methods.get(0);
		for (MethodWtihParameters m : methods) {
			if (candidate.getParameters().size() < m.getParameters().size()) {
				candidate = m;
			}
		}

		/*
		 * finally create the list of parameters and call the method
		 */
		try {
			candidate.getMethod().invoke(delegate, candidate.getParameters().toArray());
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

}
