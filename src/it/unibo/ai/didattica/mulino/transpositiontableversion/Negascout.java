package it.unibo.ai.didattica.mulino.transpositiontableversion;

import it.unibo.ai.didattica.mulino.actions.Util;
import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.client.Debug;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.Move;
import it.unibo.ai.didattica.mulino.searchutility.StopSearch;
import it.unibo.ai.didattica.mulino.searchutility.Successor;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;
import it.unibo.ai.didattica.mulino.transpositiontableversion.HashEntry.Flag;

import java.util.List;

public class Negascout {
	
	// partiamo da profondità 1
	private int maxDepth = 1;
	
	// valori associati alla vittoria o alla sconfitta
	private static final double maxValue = Double.MAX_VALUE;
	private static final double minValue = Double.MIN_VALUE;

	// oggetto che resituisce gli stati successori
	private SuccessorFunction function;

	// colore del giocatore e dell'avversario
	private State.Checker player;
	private State.Checker opponent;
	
	// oggetto per verificare se è scaduto il timer
	private StopSearch stop;
	
	//transposition table
	private TranspositionTable table;
	
	//numero di valutazione della tabella
	private int tableHits;
	
	// numero di nodi, per il debug
	private int nodes;

	public Negascout(SuccessorFunction function, State.Checker player, State.Checker opponent
			, StopSearch stop) {
		super();
		this.function = function;
		this.player = player;
		this.opponent = opponent;
		this.stop = stop;
		this.nodes = 0;
		
		this.table = new TranspositionTable();
		this.tableHits = 0;
	}
	
	

	// metodo accessibile dall'esterno per effettuare la ricerca
	public String doSearch(State initState, double alpha, double beta) {
		Successor init = new Successor("", initState);
		
		// mossa migliore
		Move move = new Move();

		boolean isPlayerTurn = true;

		// iterative deepening
		String currentAction = "";
		double currentScore = minValue;
		
		// continuo fino a che non scatta il timeout
		while(!stop.getIsTimeout()) {
			
			nodes = 0;
			
			tableHits = 0;
			
			this.table = new TranspositionTable();
			
			this.negascout(move, init, 0, alpha, beta, isPlayerTurn);
			
			//TODO
			// accetto validi solo i valori per i quali è stata completata la ricerca
			if(!stop.getIsTimeout()) {
				currentAction = move.getAction();
				currentScore = move.getScore();
				System.out.println("Depth: " + maxDepth + ", best move: " + currentAction + 
						", best score: " + currentScore + ", number of nodes: " + nodes +
						", table size: " + table.getSize() + ", number of table hits: " + tableHits + "...");
			}
			
			// inutile continuare la ricerca se facciamo una mossa che ci porta alla vittoria
			if(currentScore == maxValue)
				break;
			
			maxDepth++;
		}

		return currentAction;
	}

	private double negascout(Move move, Successor current, int depth, double alpha, double beta, boolean isPlayerTurn) {

		nodes++;
		
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
		
		double a = alpha;
		double b = beta;
		
		if(table.containsValueOf(current.getState())) {
			
			HashEntry he = table.getValueOf(current.getState());
			
			tableHits++;
			
			// questa condizione serve solo per verificare la correttezza della hash
			if(he.getState().equals(current.getState())) {
				if(he.getDepth() <= depth) {
					switch(he.getFlag()) {
					case LOWER_BOUND : 
						a = Math.max(a, he.getScore());
						break;
					case UPPER_BOUND :
						b = Math.min(b, he.getScore());
						break;
					case EXACT_SCORE :
						return he.getScore();
					}
					
					if(a >= b)
						return a;
				}
			}
			else {
				System.out.println(current.getState() + "\n\n");
				System.out.println(he.getState());
			}

		}

		//TODO
		// UNSAFE CAST!
		List<Successor> successors = function.getSuccessors(currentPlayer, currentOpponent, (State) current.getState());
		Successor successor = null;
		String action = null;

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
		
		Flag flag = (a <= alpha ? Flag.LOWER_BOUND : 
					(a >= beta ?  Flag.UPPER_BOUND : 
								  Flag.EXACT_SCORE));
		
		HashEntry entry = new HashEntry((byte)depth, a, flag , current.getState());
		table.setValueOf(current.getState(), entry);

		move.setAction(action);
		//TODO il parametro di setScore() deve essere a o score?
		move.setScore(a);
		return a;
	}

	/*
	 * AGGIUNTO DA MATTEO (22/4) valutazione euristica: ho implementato le due
	 * valutazioni W1 e W2 (si veda la classe Heuristics). Siccome possono
	 * confliggere tra loro, setto una soglia molto alta per la seconda. Assegno
	 * peso 75 alla prima, 25 alla seconda.
	 */
	
	private double evaluate(Successor successor) {
		
		double result = 0;
		
		
		result = 100.0 * Heuristics.evaluateOpenMills(successor, player);
		
		Debug.ACTIVATE_DEBUG = false;
		
		return result;
	}

	// stato di vittoria

	private boolean isGoalState(State state) {

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

	private boolean isLostState(State state) {

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

	private boolean canMove(State state, Checker player) {
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
