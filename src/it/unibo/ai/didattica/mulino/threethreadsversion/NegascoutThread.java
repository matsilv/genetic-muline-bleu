package it.unibo.ai.didattica.mulino.threethreadsversion;

import java.util.List;

import it.unibo.ai.didattica.mulino.actions.Util;
import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.client.Debug;
import it.unibo.ai.didattica.mulino.domain.Heuristics;
import it.unibo.ai.didattica.mulino.domain.IState;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.Move;
import it.unibo.ai.didattica.mulino.searchutility.Successor;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;

public class NegascoutThread implements Runnable {
	private StopSearch stop;
	private boolean isPlayerTurn;
	private BestMove bestMove;
	private Successor init;
	private final double alpha = -100000;
	private final double beta = 100000;
	private int maxDepth = 1;
	private SuccessorFunction function;
	private Checker player;
	private Checker opponent;
	
	// valori associati alla vittoria o alla sconfitta
	private static final double maxValue = Double.MAX_VALUE;
	private static final double minValue = Double.MIN_VALUE;
	
	public NegascoutThread(StopSearch stop, boolean isPlayerTurn, BestMove bestMove, Successor init,
			Checker player, Checker opponent, SuccessorFunction function) {
		this.stop = stop;
		this.isPlayerTurn = isPlayerTurn;
		this.bestMove = bestMove;
		this.init = init;
		this.function = function;
		this.player = player;
		this.opponent = opponent;
	}

	@Override
	public void run() {
		System.out.println("NegascoutThread start execution...");
		
		Move move = new Move();
		
		while(!stop.getIsTimeout()) {
			
			this.negascout(move, init, 0, alpha, beta, isPlayerTurn);
			
			//TODO
			// accetto validi solo i valori per i quali è stata completata la ricerca
			if(!stop.getIsTimeout()) {
				bestMove.setAction(move.getAction());
				bestMove.setScore(move.getScore());
				
				System.out.println("Depth: " + maxDepth + ", best move: " + move.getAction() + 
						", best score: " + move.getScore());
			}
			
			// inutile continuare la ricerca se facciamo una mossa che ci porta alla vittoria
			if(move.getScore() == maxValue)
				break;
			
			maxDepth++;
		}
	}
	
	private double negascout(Move move, Successor current, int depth, double alpha, double beta, boolean isPlayerTurn) {

		//TODO
		// è corretto controllare in questo punto che sia scattato il timeout?
		if(stop.getIsTimeout())
			return minValue;
		
		State.Checker currentPlayer = null;
		State.Checker currentOpponent = null;

		if (isPlayerTurn) {
			currentPlayer = player;
			currentOpponent = opponent;
		} else {
			currentPlayer = opponent;
			currentOpponent = player;
		}
		
		if (isGoalState(current.getState())) {
			if (isPlayerTurn)
				return maxValue;
			else
				return -maxValue;
		} else if (isLostState(current.getState())) {
			if (isPlayerTurn)
				return minValue;
			else
				return -minValue;
		} else if (depth == this.maxDepth) {
			if (isPlayerTurn)
				return evaluate(current);
			else
				return -evaluate(current);
		}

		//TODO
		// UNSAFE CAST!
		List<Successor> successors = function.getSuccessors(currentPlayer, currentOpponent, (State) current.getState());
		Successor successor = null;
		String action = null;

		double a = alpha;
		double b = beta;
		double score;

		for (int i = 0; i < successors.size(); i++) {
			successor = successors.get(i);
			
			score = -negascout(move, successor, depth + 1, -b, -a, !isPlayerTurn);

			if (i == 0)
				action = successor.getAction();
			else {
				if (a < score && score < beta)
					score = -negascout(move, successor, depth + 1, -beta, -score, !isPlayerTurn);
			}

			if (score > a) {
				a = score;
				action = successor.getAction();
			}

			if (a >= beta)
				break;

			b = a + 1;
		}

		move.setAction(action);
		//TODO
		// il parametro di setScore() deve essere a o score?
		move.setScore(a);
		return a;
	}
	
	/*
	 * AGGIUNTO DA MATTEO (22/4) valutazione euristica: ho implementato le due
	 * valutazioni W1 e W2 (si veda la classe Heuristics). Siccome possono
	 * confliggere tra loro, setto una soglia molto alta per la seconda. Assegno
	 * peso 75 alla prima, 25 alla seconda.
	 */
	private static int weightW1 = 100;
	// private static int weightW2 = 0;
	 private static int evaluationCounter = 0;
	private double evaluate(Successor successor) {
		Debug.ACTIVATE_DEBUG = false;
		Debug.log("Starting evaluation " + evaluationCounter);
		double val = weightW1*Heuristics.evaluateOpenMills(successor, player);
		Debug.log("Starting evaluation " + (evaluationCounter++));
		return val;
	}
	
	// stato di vittoria

	private boolean isGoalState(IState state) {

		State currentState = null;

		if (state instanceof State)
			currentState = (State) state;
		else
			throw new IllegalArgumentException("state must be of type State");

		int numCheckersOpponent = currentState.getCheckersOnBoard(opponent);

		if ((numCheckersOpponent == 2 && currentState.getCheckers(opponent) == 0) || (!canMove(state, opponent)))
			return true;
		else
			return false;
	}

	// stato di sconfitta

	private boolean isLostState(IState state) {

		State currentState = null;

		if (state instanceof State)
			currentState = (State) state;
		else
			throw new IllegalArgumentException("state must be of type State");

		int numCheckersPlayer = currentState.getCheckersOnBoard(player);

		if ((numCheckersPlayer == 2 && currentState.getCheckers(player) == 0) || (!canMove(state, player)))
			return true;
		else
			return false;
	}
	
	// aggiunto da MATTIA (21/04): restituisce vero se il giocatore può muovere
	// almeno una pedina

	private boolean canMove(IState state, Checker player) {
		State currentState = null;

		if (state instanceof State)
			currentState = (State) state;
		else
			throw new IllegalArgumentException("state must be of type State");

		List<String> emptyPositions = currentState.getCheckerPositions(Checker.EMPTY, false);
		List<String> playerPositions = currentState.getCheckerPositions(player, false);

		// se sono nella fase 1 o 2 controllo che ci sia una posizione libera
		// adiacente
		// altrimenti se sono in fase 3 ci sarà sempre una posizione libera

		//AGGIUNTO DA MATTEO 24/2
		// in realta', se siamo in fase uno basta avere un buco libero, non serve che sia adiacente
		if (state.getCurrentPhase().equals(Phase.FIRST))
			return !emptyPositions.isEmpty();
		
		if (!state.getCurrentPhase().equals(Phase.FINAL)) {
			for (String emptyPos : emptyPositions) {
				for (String playerPos : playerPositions) {
					try {
						if (Util.areAdiacent(emptyPos, playerPos))
							return true;
					} catch (WrongPositionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;
		} else {
			return true;
		}
	}
}