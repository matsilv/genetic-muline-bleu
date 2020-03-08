package it.unibo.ai.didattica.mulino.domain;

import it.unibo.ai.didattica.mulino.actions.Util;
import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.client.Debug;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.Successor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Heuristics {

	public static final String[] positions = { "a1", "a4", "a7", "b2", "b4", "b6", "c3", "c4", "c5", "d1", "d2", "d3", "d5",
			"d6", "d7", "e3", "e4", "e5", "f2", "f4", "f6", "g1", "g4", "g7" };
	
	public Heuristics() {

		
	}
	
	public static double evaluateOpenMillsAndNumberOfPieces(Successor successor, State.Checker player) {
		Debug.ACTIVATE_DEBUG = false;
		Debug.log("Starting open mills evaluations");
		
		// Shortcut per evitare di usare sempre gli accessor
		State state = (State) successor.getState();
		HashMap<String, Checker> board = state.getBoard();
		Set<String> positions = board.keySet();

		// Statistiche
		int whiteOpenMills = 0;
		int blackOpenMills = 0;

		// Controlliamo che il mulino possa essere chiuso da una pedina gia' in
		// gioco solo se siamo in fase 2
		boolean needNeighbour = state.getCurrentPhase() == Phase.SECOND;

		// VERIFICA PER BIANCO
		Checker inspected = Checker.WHITE;
		{
			// controllo le colonne, salto la colonna d, che considero a parte,
			// perche' ha due mulini
			
			for (char x = 'a'; x <= 'g'; x += 1 + (x == 'c' ? 1 : 0)) {
				Debug.log("evaluating column " + x + " for white player");
				if (millInRegularColumn(x, positions, board, inspected, needNeighbour))
					whiteOpenMills++;
			}

			// controllo la colonna d, prima meta'
			Debug.log("evaluating column d for white player");
			if (millInDColumn(positions, board, inspected, '1', needNeighbour)) {
				whiteOpenMills++;
			}

			// seconda meta'
			Debug.log("evaluating column d for white player - cont'd");
			if (millInDColumn(positions, board, inspected, '5', needNeighbour)) {
				whiteOpenMills++;
			}

			// controllo le righe, salto la riga 4, come prima
			for (char x = '1'; x <= '7'; x += 1 + (x == '3' ? 1 : 0)) {
				Debug.log("evaluating row " + x + " for white player");
				if (millInRegularRow(x, positions, board, inspected, needNeighbour))
					whiteOpenMills++;
			}

			// controllo la riga 4, prima meta'
			Debug.log("evaluating row 4 for white player");
			if (millIn4thRow(positions, board, inspected, 'a', needNeighbour)) {
				whiteOpenMills++;
			}

			// seconda meta'
			Debug.log("evaluating row 4 for white player - cont'd");
			if (millIn4thRow(positions, board, inspected, 'e', needNeighbour)) {
				whiteOpenMills++;
			}
		}
		// VERIFICA PER NERO
		inspected = Checker.BLACK;
		{
			// controllo le colonne, salto la colonna d, che considero a parte,
			// perche' ha due mulini
			for (char x = 'a'; x <= 'g'; x += 1 + (x == 'c' ? 1 : 0)) {
				if (millInRegularColumn(x, positions, board, inspected, needNeighbour))
					blackOpenMills++;
			}

			// controllo la colonna c, prima meta'
			if (millInDColumn(positions, board, inspected, '1', needNeighbour)) {
				blackOpenMills++;
			}

			// seconda meta'
			if (millInDColumn(positions, board, inspected, '5', needNeighbour)) {
				blackOpenMills++;
			}

			// controllo le righe, salto la riga 4, come prima
			for (char x = '1'; x <= '7'; x += 1 + (x == '3' ? 1 : 0)) {
				if (millInRegularRow(x, positions, board, inspected, needNeighbour))
					blackOpenMills++;
			}

			// controllo la colonna c, prima meta'
			if (millIn4thRow(positions, board, inspected, 'a', needNeighbour)) {
				blackOpenMills++;
			}

			// seconda meta'
			if (millIn4thRow(positions, board, inspected, 'e', needNeighbour)) {
				blackOpenMills++;
			}
		}

		// NORMALIZZAZIONE E VALUTAZIONE FINALE
		int myOpenMills = 0;
		int opponentOpenMills = 0;
		int totalOpenMills = 0;
		
		totalOpenMills = whiteOpenMills + blackOpenMills;
		
		if(totalOpenMills == 0)
			return 0;
		
		int myCheckers = 0;
		int opponentCheckers = 0;
		int totalCheckers;
		int whiteCheckers = state.getWhiteCheckers() + state.getWhiteCheckersOnBoard();
		int blackCheckers = state.getBlackCheckers() + state.getBlackCheckersOnBoard();
		
		if (player == Checker.WHITE) {
			myOpenMills = whiteOpenMills;
			opponentOpenMills = blackOpenMills;
			myCheckers = whiteCheckers;
			opponentCheckers = blackCheckers;
		} else {
			myOpenMills = blackOpenMills;
			opponentOpenMills = whiteOpenMills;
			myCheckers = blackCheckers;
			opponentCheckers = whiteCheckers;
		}
		
		totalCheckers = myCheckers + opponentCheckers;
		
		return (myCheckers - opponentCheckers + myOpenMills - opponentOpenMills) * 1.0 / (totalCheckers + totalOpenMills);
	}
	
	
	/*
	 * AGGIUNTO DA MATTEO (22/4) classe di servizio, in cui salvo tutti i metodi
	 * delle euristiche. Tutti i metodi sono normalizzati tra -1 e 1, potranno
	 * essere pesati fuori.
	 */

	/*
	 * Euristica W1/L1
	 * 
	 * In una partita possiamo avere fino a 8 mulini nelle righe (uno per riga,
	 * 2 per la riga centrale) e altrettanti nelle colonne. L'euristica li
	 * conta. Se un avversario ha dei mulini, questo ci pone a rischio di una
	 * sua giocata in cui lui usa il suo mulino per bloccare i nostri, quindi
	 * considero anche i mulini dell'avversario. Il tutto e' normalizzato in
	 * base al numero totale di mulini (averne tanti quanti il mio avversario
	 * da' in uscita zero)
	 */
	public static double evaluateOpenMills(Successor successor, State.Checker player) {
		Debug.ACTIVATE_DEBUG = false;
		Debug.log("Starting open mills evaluations");
		
		// Shortcut per evitare di usare sempre gli accessor
		State state = (State) successor.getState();
		HashMap<String, Checker> board = state.getBoard();
		Set<String> positions = board.keySet();

		// Statistiche
		int whiteOpenMills = 0;
		int blackOpenMills = 0;

		// Controlliamo che il mulino possa essere chiuso da una pedina gia' in
		// gioco solo se siamo in fase 2
		boolean needNeighbour = state.getCurrentPhase() == Phase.SECOND;

		// VERIFICA PER BIANCO
		Checker inspected = Checker.WHITE;
		{
			// controllo le colonne, salto la colonna d, che considero a parte,
			// perche' ha due mulini
			
			for (char x = 'a'; x <= 'g'; x += 1 + (x == 'c' ? 1 : 0)) {
				Debug.log("evaluating column " + x + " for white player");
				if (millInRegularColumn(x, positions, board, inspected, needNeighbour))
					whiteOpenMills++;
			}

			// controllo la colonna d, prima meta'
			Debug.log("evaluating column d for white player");
			if (millInDColumn(positions, board, inspected, '1', needNeighbour)) {
				whiteOpenMills++;
			}

			// seconda meta'
			Debug.log("evaluating column d for white player - cont'd");
			if (millInDColumn(positions, board, inspected, '5', needNeighbour)) {
				whiteOpenMills++;
			}

			// controllo le righe, salto la riga 4, come prima
			for (char x = '1'; x <= '7'; x += 1 + (x == '3' ? 1 : 0)) {
				Debug.log("evaluating row " + x + " for white player");
				if (millInRegularRow(x, positions, board, inspected, needNeighbour))
					whiteOpenMills++;
			}

			// controllo la riga 4, prima meta'
			Debug.log("evaluating row 4 for white player");
			if (millIn4thRow(positions, board, inspected, 'a', needNeighbour)) {
				whiteOpenMills++;
			}

			// seconda meta'
			Debug.log("evaluating row 4 for white player - cont'd");
			if (millIn4thRow(positions, board, inspected, 'e', needNeighbour)) {
				whiteOpenMills++;
			}
		}
		// VERIFICA PER NERO
		inspected = Checker.BLACK;
		{
			// controllo le colonne, salto la colonna d, che considero a parte,
			// perche' ha due mulini
			for (char x = 'a'; x <= 'g'; x += 1 + (x == 'c' ? 1 : 0)) {
				if (millInRegularColumn(x, positions, board, inspected, needNeighbour))
					blackOpenMills++;
			}

			// controllo la colonna c, prima meta'
			if (millInDColumn(positions, board, inspected, '1', needNeighbour)) {
				blackOpenMills++;
			}

			// seconda meta'
			if (millInDColumn(positions, board, inspected, '5', needNeighbour)) {
				blackOpenMills++;
			}

			// controllo le righe, salto la riga 4, come prima
			for (char x = '1'; x <= '7'; x += 1 + (x == '3' ? 1 : 0)) {
				if (millInRegularRow(x, positions, board, inspected, needNeighbour))
					blackOpenMills++;
			}

			// controllo la colonna c, prima meta'
			if (millIn4thRow(positions, board, inspected, 'a', needNeighbour)) {
				blackOpenMills++;
			}

			// seconda meta'
			if (millIn4thRow(positions, board, inspected, 'e', needNeighbour)) {
				blackOpenMills++;
			}
		}

		// NORMALIZZAZIONE E VALUTAZIONE FINALE
		int myOpenMills = 0;
		int opponentOpenMills = 0;
		int totalOpenMills = 0;

		totalOpenMills = whiteOpenMills + blackOpenMills;
		
		if(totalOpenMills == 0)
			return 0;
		
		if (player == Checker.WHITE) {
			myOpenMills = whiteOpenMills;
			opponentOpenMills = blackOpenMills;
		} else {
			myOpenMills = blackOpenMills;
			opponentOpenMills = whiteOpenMills;
		}
		
		return (myOpenMills - opponentOpenMills);
	}
	
	private static boolean adiacentIsInMill(String[] millPositions, String position) {
		boolean found = false;
		int size = millPositions.length;
		
		for(int i=0;i<size; i++) {
			if(millPositions[i] != null && millPositions[i].equals(position)) {
				found = true;
				break;
			}	
		}
		
		return found;
	}

	private static boolean millInDColumn(Set<String> positions, HashMap<String, Checker> board, Checker inspected,
			char index, boolean needNeighbour) {
		int inPosition = 0;
		String empty = null;
		boolean opponent = false;
		
		//AGGIUNTO DA MATTIA 7/05
		// bisogna controllare che la pedina con la quale si potrebbe 
		// chiudere il mulino non sia una di quella che lo formano.
		// Va fatto anche in tutti gli altri metodi
		
		String millPositions[] = new String[3];
		int millIndex = 0;
		
		for (char x = index; x <= (index + 2); x += 1) {

			String current = "d" + x;
			// conta le pedine in posizione

			if (board.get(current) == inspected) {
				inPosition++;
				millPositions[millIndex++] = current;
			}
				
			else if (board.get(current) == Checker.EMPTY)
				empty = current;
			else {
				opponent = true;
				break;
			}
		}

		// verifica che ci siano due caselle occupate e una libera
		if (!opponent && inPosition == 2) {

			if (!needNeighbour)
				return true;

			// verifica che ci sia una pedina utilizzabile per chiudere
			// il mulino
			String[] adj;
			try {
				adj = Util.getAdiacentTiles(empty);
			} catch (WrongPositionException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			boolean found = false;
			for (int i = 0; i < adj.length && !found; i++)
				if (board.get(adj[i]).equals(inspected) && !adiacentIsInMill(millPositions, adj[i]))
					found = true;

			return found;
		}
		return false;
	}

	private static boolean millInRegularColumn(char x, Set<String> positions, HashMap<String, Checker> board,
			Checker inspected, boolean needNeighbour) {
		int inPosition = 0;
		String empty = null;
		boolean opponent = false;
		String millPositions[] = new String[3];
		int millIndex = 0;

		// conta le pedine in posizione
		for (String s : positions) {
			if (s.startsWith("" + x)) {
				if (board.get(s) == inspected) {
					inPosition++;
					millPositions[millIndex++] = s;
				}
					
				else if (board.get(s) == Checker.EMPTY)
					empty = s;
				else {
					opponent = true;
					break;
				}
			}

		}

		// verifica che ci siano due caselle occupate e una libera
		if (!opponent && inPosition == 2) {

			if (!needNeighbour)
				return true;

			// verifica che ci sia una pedina utilizzabile per chiudere
			// il mulino
			String[] adj;
			try {
				adj = Util.getAdiacentTiles(empty);
			} catch (WrongPositionException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			boolean found = false;
			for (int i = 0; i < adj.length && !found; i++)
				if (board.get(adj[i]).equals(inspected) && !adiacentIsInMill(millPositions, adj[i]))
					found = true;
			
			return found;
		}
		return false;
	}

	private static boolean millIn4thRow(Set<String> positions, HashMap<String, Checker> board, Checker inspected,
			char index, boolean needNeighbour) {
		int inPosition = 0;
		String empty = null;
		boolean opponent = false;
		String millPositions[] = new String[3];
		int millIndex = 0;
		
		for (char x = index; x <= (index + 2); x += 1) {

			String current = x + "4";
			// conta le pedine in posizione

			if (board.get(current) == inspected) {
				inPosition++;
				millPositions[millIndex++] = current;
			}
				
			else if (board.get(current) == Checker.EMPTY)
				empty = current;
			else {
				opponent = true;
				break;
			}
		}

		// verifica che ci siano due caselle occupate e una libera
		if (!opponent && inPosition == 2) {

			if (!needNeighbour)
				return true;

			// verifica che ci sia una pedina utilizzabile per chiudere
			// il mulino
			String[] adj;
			try {
				adj = Util.getAdiacentTiles(empty);
			} catch (WrongPositionException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			boolean found = false;
			for (int i = 0; i < adj.length && !found; i++)
				if (board.get(adj[i]).equals(inspected) && !adiacentIsInMill(millPositions, adj[i]))
					found = true;

			return found;
		}
		return false;
	}

	private static boolean millInRegularRow(char x, Set<String> positions, HashMap<String, Checker> board,
			Checker inspected, boolean needNeighbour) {
		int inPosition = 0;
		String empty = null;
		boolean opponent = false;
		String millPositions[] = new String[3];
		int millIndex = 0;

		// conta le pedine in posizione
		for (String s : positions) {
			if (s.endsWith("" + x)) {
				if (board.get(s) == inspected) {
					inPosition++;
					millPositions[millIndex++] = s;
				}
					
				else if (board.get(s) == Checker.EMPTY)
					empty = s;
				else {
					opponent = true;
					break;
				}
			}

		}

		// verifica che ci siano due caselle occupate e una libera
		if (!opponent && inPosition == 2) {

			if (!needNeighbour)
				return true;

			// verifica che ci sia una pedina utilizzabile per chiudere
			// il mulino
			String[] adj;
			try {
				adj = Util.getAdiacentTiles(empty);
			} catch (WrongPositionException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			boolean found = false;
			for (int i = 0; i < adj.length && !found; i++)
				if (board.get(adj[i]).equals(inspected) && !adiacentIsInMill(millPositions, adj[i]))
					found = true;
			
			return found;	
		}
		return false;
	}
	

	/*
	 * Euristica W2/L2
	 * 
	 * Conto quanti movimenti riesco a consentire al mio avversario, con lo
	 * scopo di bloccarli il piu' possibile. L'euristica vale 1 se non sono
	 * possibili movimenti all'avversario nel prossimo turno, poi e'
	 * proporzionalmente decrescente con lo zero al parametro di soglia settato
	 * (si suggerisce soglia 4: 0.75 se e' possibile solo 1, 0.50 se ne sono
	 * possibili solo 2, 0.25 se ne sono possibili 3, 0 altrimenti). Come prima,
	 * si sottrae alla liberta' di movimento avversaria la propria.
	 */
	public static double evaluateBlockedMoves(Successor successor, byte zeroThreshold, State.Checker player) {
		State state = (State) successor.getState();

		// Non ci sono valutazioni da fare in fase I o III
		if (state.getCurrentPhase() == Phase.FINAL)
			return 0;

		// Shortcut per evitare di usare sempre gli accessor
		HashMap<String, Checker> board = state.getBoard();

		// Statistiche
		byte whiteAllowedMoves = 0;
		byte blackAllowedMoves = 0;

		// VERIFICA PER BIANCO
		Checker inspected = Checker.WHITE;
		try {

			List<String> checkerPositions = state.getCheckerPositions(inspected, false);
			for (String position : checkerPositions)

				for (String adj : Util.getAdiacentTiles(position))
					if (board.get(adj).equals(Checker.EMPTY)) {
						
						whiteAllowedMoves ++;
						break;
					}

		} catch (WrongPositionException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		// VERIFICA PER NERO
		inspected = Checker.BLACK;
		try {

			List<String> checkerPositions = state.getCheckerPositions(inspected, false);
			for (String position : checkerPositions)

				for (String adj : Util.getAdiacentTiles(position))
					if (board.get(adj).equals(Checker.EMPTY)) {
						
						blackAllowedMoves++;
						break;
					}

		} catch (WrongPositionException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		// NORMALIZZAZIONE E VALUTAZIONE FINALE
		double myBlockednessScore = 0;
		double opponentBlockednessScore = 0;

		myBlockednessScore = state.getWhiteCheckersOnBoard() - whiteAllowedMoves;
		opponentBlockednessScore = state.getBlackCheckersOnBoard() - blackAllowedMoves;

		// inverto se sta chiedendo il bianco
		if (player == Checker.WHITE) {
			double app = myBlockednessScore;
			myBlockednessScore = opponentBlockednessScore;
			opponentBlockednessScore = app;
		}
		
		// AGGIUNTO DA MATTIA (7/05)
		// non è corretto invertito?
		
		if((myBlockednessScore + opponentBlockednessScore) == 0)
			return 0;
		
		return  (myBlockednessScore - opponentBlockednessScore) * 1.0;
	}

	public static double evaluateBlockedMoves(Successor successor, State.Checker player) {
		return evaluateBlockedMoves(successor, (byte) 4, player);
	}
	
	/*
	 * Euristica W3/L3
	 * 
	 * Conto i pezzi del giocatore e dell'avversario
	 */
	
	public static double evaluateNumberOfPieces(Successor successor, State.Checker player) {
		int whiteCheckers = successor.getState().getWhiteCheckersOnBoard() + successor.getState().getWhiteCheckers();
		int blackCheckers = successor.getState().getBlackCheckersOnBoard() + successor.getState().getBlackCheckers();
		int myCheckers;
		int opponentCheckers;
		int totalCheckers = whiteCheckers + blackCheckers;
		
		if(player.equals(Checker.WHITE)) {
			myCheckers = whiteCheckers;
			opponentCheckers = blackCheckers;
		}
		else if(player.equals(Checker.BLACK)) {
			myCheckers = blackCheckers;
			opponentCheckers = whiteCheckers;
		}
		else throw new IllegalArgumentException("Checker type not valid");
		
		return ((myCheckers - opponentCheckers) * 1.0);
	}
	
	/*
	 * Euristica W4/L4
	 * 
	 * Valuto il numero di double mill
	 */
	
	public static double evaluateDoubleMills(Successor successor, Checker player, 
			List<HashMap<String, Checker>> whiteDoubleMillsConfigurations,
			List<HashMap<String, Checker>> blackDoubleMillsConfigurations) {
		int whiteDoubleMills = 0;
		int blackDoubleMills = 0;
		HashMap<String, Checker> board = successor.getState().getBoard();
		int count = 0;
		
		for(HashMap<String, Checker> m : whiteDoubleMillsConfigurations) {
			count = 0;
			for(String p : positions) {
				if(m.containsKey(p)) {
					if(!m.get(p).equals(board.get(p))) {
						break;
					}
					else count++;	
				}
			}
			
			if(count == 6)
				whiteDoubleMills++;
		}
		
		for(HashMap<String, Checker> m : blackDoubleMillsConfigurations) {
			count = 0;
			for(String p : positions) {
				if(m.containsKey(p)) {
					if(!m.get(p).equals(board.get(p)))
						break;
				}
			}
			
			if(count == 6)
				blackDoubleMills++;
		}
		
		double playerDoubleMills;
		double opponentDoubleMills;
		double totalDoubleMills = whiteDoubleMills + blackDoubleMills;
		
		if(player.equals(Checker.WHITE)) {
			playerDoubleMills = whiteDoubleMills;
			opponentDoubleMills = blackDoubleMills;
		}
		else if(player.equals(Checker.BLACK)) {
			playerDoubleMills = blackDoubleMills;
			opponentDoubleMills = whiteDoubleMills;
		}
		else throw new IllegalArgumentException("Checker type not valid");
		
		if(totalDoubleMills == 0)
			return 0;
		
		return ((playerDoubleMills - opponentDoubleMills) * 1.0 / totalDoubleMills);
	}
	
	/*
	 * Euristica W5/L5
	 * 
	 * L'azione effettuata comporta la chiusura di un mulino.
	 */
	
	public static double evaluateClosedMill(Successor successor, Checker player) {
		int closedMill = 0;
		
		// se siamo in fase uno e lunghezza dell'azione è 4 significa che la mossa comporta
		// la chiusura di un mulino; nelle altre fasi una stringa di lunghezza 6
		// significa chiusura di un mulino
		
		if(successor.getState().getCurrentPhase().equals(Phase.FIRST)) {
			if(successor.getAction().length() == 4)
				closedMill++;
		}
		else {
			if(successor.getAction().length() == 6)
				closedMill++;
		}
		
		return closedMill * 1.0;
		
	}
}

