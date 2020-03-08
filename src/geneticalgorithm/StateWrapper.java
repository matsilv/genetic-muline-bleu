package geneticalgorithm;

import it.unibo.ai.didattica.mulino.domain.State;

public class StateWrapper {

	private State state;
	
	public StateWrapper(State state) {
		this.state = state;
	}
	
	public synchronized State getState() {
		return this.state;
	}
	
	public synchronized void modifyState(State state) {
		this.state = state;
	}
}
