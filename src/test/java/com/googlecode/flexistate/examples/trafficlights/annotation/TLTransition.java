package com.googlecode.flexistate.examples.trafficlights.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsEvent;
import com.googlecode.flexistate.examples.trafficlights.enumeration.TrafficLightsState;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TLTransition {

	TrafficLightsEvent event();

	TrafficLightsState target();
}
