package it.unibo.ai.didattica.mulino.client;

import it.unibo.ai.didattica.mulino.domain.Heuristics;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.Successor;

public class Test {

	public static void main(String[] args) {
		
		State state = new State();
		state.getBoard().put("a1", Checker.WHITE);
		state.getBoard().put("a4", Checker.WHITE);
		state.getBoard().put("a7", Checker.WHITE);
		
		state.getBoard().put("b2", Checker.EMPTY);
		state.getBoard().put("b4", Checker.BLACK);
		state.getBoard().put("b6", Checker.BLACK);
		
		state.getBoard().put("c3", Checker.BLACK);
		state.getBoard().put("c4", Checker.WHITE);
		state.getBoard().put("c5", Checker.EMPTY);
		
		state.getBoard().put("d1", Checker.BLACK);
		state.getBoard().put("d2", Checker.EMPTY);
		state.getBoard().put("d3", Checker.EMPTY);
		state.getBoard().put("d4", Checker.EMPTY);
		state.getBoard().put("d5", Checker.EMPTY);
		state.getBoard().put("d6", Checker.EMPTY);
		state.getBoard().put("d7", Checker.EMPTY);
		
		state.getBoard().put("e3", Checker.BLACK);
		state.getBoard().put("e4", Checker.WHITE);
		state.getBoard().put("e5", Checker.EMPTY);
		
		state.getBoard().put("f2", Checker.BLACK);
		state.getBoard().put("f4", Checker.BLACK);
		state.getBoard().put("f6", Checker.EMPTY);
		
		state.getBoard().put("g1", Checker.EMPTY);
		state.getBoard().put("g4", Checker.EMPTY);
		state.getBoard().put("g7", Checker.WHITE);
		
		state.setBlackCheckers(0);
		state.setWhiteCheckers(0);
		state.setBlackCheckersOnBoard(7);
		state.setWhiteCheckersOnBoard(6);
		
		state.setCurrentPhase(Phase.SECOND);
		
		System.out.println(state.toString());
		
		System.out.println(Heuristics.evaluateBlockedMoves(new Successor("d7a7", state), Checker.WHITE));

	}

}
