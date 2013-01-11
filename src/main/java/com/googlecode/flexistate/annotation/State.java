package com.googlecode.flexistate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface State {

	boolean initial() default false;

	//	String name() default "";

	ExecuteOn value() default ExecuteOn.entry;
}
