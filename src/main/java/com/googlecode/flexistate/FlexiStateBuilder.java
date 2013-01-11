package com.googlecode.flexistate;

import java.lang.annotation.Annotation;

public interface FlexiStateBuilder<TEvent>
{

	/**
	 * Specifies custom annotation denoting the transition set
	 * 
	 * @param <T> - the type of the annotation
	 * @param transitionsAnnotation - the class designating the annotation type
	 * @return the current instance of the builder
	 */
	<T extends Annotation> FlexiStateBuilder<TEvent> transitionSetAnnotation(Class<T> transitionsAnnotation);

	/**
	 * Specifies custom annotation denoting the action taken once a transition occurs
	 * 
	 * @param <T> - the type of the annotation
	 * @param transitionActionAnnotation - the class designating the annotation type
	 * @return the current instance of the builder
	 */
	<T extends Annotation> FlexiStateBuilder<TEvent> transitionActionAnnotation(Class<T> transitionActionAnnotation);

	/**
	 * During the initialization advances the state machine that is being built to the specified state silently. 
	 * All the previous states are bypassed, associated actions are all skipped.
	 * 
	 * @param state - the state to advance to
	 * @return the current instance of the builder
	 */
	FlexiStateBuilder<TEvent> advanceToState(String state);

	/**
	 * During the initialization advances the state machine that is being built to the specified state. 
	 * All the previous states are bypassed, and the associated actions are not executed, 
	 * except for the entry action of the destination state. This entry state action - if present - is executed.
	 * 
	 * @param state - the state to advance to
	 * @return the current instance of the builder
	 */
	FlexiStateBuilder<TEvent> advanceAndExecute(String state);

	/**
	 * Builds the state machine according to the previously specified parameters.
	 * 
	 * @return a newly constructed instance of state machine.
	 */
	FlexiState<TEvent> build();

}
