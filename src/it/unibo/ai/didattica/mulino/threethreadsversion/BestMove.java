package it.unibo.ai.didattica.mulino.threethreadsversion;

public class BestMove {
	private String action;
	private double score;
	
	public BestMove() {
		this.action = "";
		this.score = Double.MIN_VALUE;
	}
	
	public synchronized void setAction(String action) {
		this.action = action;
	}
	
	public synchronized void setScore(double score) {
		this.score = score;
	}
	
	public synchronized String getAction() {
		return this.action;
	}
	
	public synchronized double getScore() {
		return this.score;
	}
}
