package it.unibo.ai.didattica.mulino.threethreadsversion;

public class StopSearch {
	private boolean isTimeout;
	
	public StopSearch() {
		this.isTimeout = false;
	}
	
	public synchronized boolean getIsTimeout() {
		return this.isTimeout;
	}
	
	public synchronized void setIsTimeout(boolean isTimeout) {
		this.isTimeout = isTimeout;
	}
}
