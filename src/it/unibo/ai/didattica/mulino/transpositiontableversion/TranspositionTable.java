package it.unibo.ai.didattica.mulino.transpositiontableversion;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;

import java.util.HashMap;
import java.util.Random;

public class TranspositionTable {
	private HashMap<Long, HashEntry> values; 
	private long[][] randomTable = new long[24][3];
	
	public TranspositionTable() {
		this.values = new HashMap<Long, HashEntry>();
		this.initRandomTable();
	}
	
	private void initRandomTable() {
		Random r = new Random();
		
		for(int i=0;i<24;i++) {
			for(int j=0;j<3;j++) {
				long b = r.nextLong();
				this.randomTable[i][j] = b;
			}
		}
	}
	
	private long boardHash(HashMap<String, Checker> board, String[] positions) {
		long result = 0;
		Checker checker;
		int index;
		
		int i = 0;
		for(String p : positions) {
			checker = board.get(p);
			index = checker.ordinal();
			result = result ^ this.randomTable[i][index];
			i++;
		}
		return result;
	}
	
	private long hashCodeOfState(State state) {
		final int prime = 31;
		long result = 1;
		result = prime * result + state.getBlackCheckers();
		result = prime * result + state.getBlackCheckersOnBoard();
		result = prime * result + this.boardHash(state.getBoard(), state.positions);
		result = prime * result + ((state.getCurrentPhase() == null) ? 0 : state.getCurrentPhase().hashCode());
		result = prime * result + state.getWhiteCheckers();
		result = prime * result + state.getWhiteCheckersOnBoard();
		return result;
	}
	
	public HashEntry getValueOf(State state) {
		long hashCode = this.hashCodeOfState(state);
		return values.get(hashCode);
	}
	
	public void setValueOf(State state, HashEntry value) {
		values.put(this.hashCodeOfState(state), value);
	}
	
	public boolean containsValueOf(State state) {
		return this.values.containsKey(this.hashCodeOfState(state));
	}
	
	public int getSize() {
		return this.values.size();
	}
}
