A lightweight framework to easily convert a Java POJO service into an FSM using annotations

example:

1. define service POJO

{{{
public enum TrafficLightsState {
	off, red, amber, green;
}
}}}

{{{

public class TrafficLights {

	private TrafficLightsState state;

	public void off() {
		state = TrafficLightsState.off;
	}

	public void red() {
		state = TrafficLightsState.red;
	}

	public void amber() {
		state = TrafficLightsState.amber;
	}

	public void green() {
		state = TrafficLightsState.green;
	}

	public TrafficLightsState getState() {
		return state;
	}
}
}}}

2. describe a Finite State Machine (FSM) using Java annotations on a previously defined POJO

{{{

...

@StateMachine
public class TrafficLights {

	...

	@State(initial = true)
	@Transitions({@Transition(event = "on", target = "red")})
	public void off()...

	@State
	@Transitions({@Transition(event = "off", target = "off"), @Transition(event = "timer", target = "amber")})
	public void red()...

	@State
	@Transitions({@Transition(event = "off", target = "off"), @Transition(event = "timer", target = "green")})
	public void amber()...

	@State
	@Transitions({@Transition(event = "off", target = "off"), @Transition(event = "timer", target = "red")})
	public void green() ... 

	...
}

}}}

3. Finally create and use a finite state machine based on the previously defined POJO service

{{{
public enum TrafficLightsEvent {
	on, off, timer;
}
}}}

{{{

TrafficLights trafficLights = new TrafficLights();

FlexiState<TrafficLightsEvent, Void> trafficLightsStateMachine =
	FlexiStateBuilder.forEvent(TrafficLightsEvent.class).withDelegate(trafficLights).build();

trafficLightsStateMachine.enqueueAndProcess(TrafficLightsEvent.on);
Assert.assertEquals(TrafficLightsState.red, trafficLights.getState());

trafficLightsStateMachine.enqueueAndProcess(TrafficLightsEvent.timer);
Assert.assertEquals(TrafficLightsState.amber, trafficLights.getState());

trafficLightsStateMachine.enqueueAndProcess(TrafficLightsEvent.timer);
Assert.assertEquals(TrafficLightsState.green, trafficLights.getState());
}}}
