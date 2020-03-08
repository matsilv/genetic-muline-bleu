package it.unibo.ai.didattica.mulino.transpositiontableversion;

import it.unibo.ai.didattica.mulino.domain.State;

public class HashEntry {
	public enum Flag {
		UPPER_BOUND("Upper"), LOWER_BOUND("Lower"), EXACT_SCORE("Exact");
		private final String name;

		private Flag(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}
	
	private byte depth;
	private double score;
	private Flag flag;
	// per debug
	private State state;
	
	public HashEntry(byte depth, double score, Flag flag ,State state ) {
		
		this.depth = depth;
		this.score = score;
		this.flag = flag;
		this.state = state;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(byte depth) {
		this.depth = depth;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public Flag getFlag() {
		return flag;
	}
	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
}

