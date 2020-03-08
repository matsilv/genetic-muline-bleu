package it.unibo.ai.didattica.mulino.searchutility;

// classe contenente la mossa migliore

public class Move {
	private String action;
	private double score;
	
	public Move() {
		this.action = "";
		this.score = 0;
	};
	
	public Move(String action, double value) {
		super();
		this.action = action;
		this.score = value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}	
}