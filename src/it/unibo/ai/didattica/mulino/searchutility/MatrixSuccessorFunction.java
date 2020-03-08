package it.unibo.ai.didattica.mulino.searchutility;

import it.unibo.ai.didattica.mulino.actions.Util;
import it.unibo.ai.didattica.mulino.actions.WrongPositionException;
import it.unibo.ai.didattica.mulino.domain.MatrixState;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;

import java.util.ArrayList;
import java.util.List;

public class MatrixSuccessorFunction {

	public List<Successor> getSuccessors(Checker currentPlayer, Checker opponent, MatrixState currentState, int depth) {
		List<Successor> successors = new ArrayList<Successor>();
		Phase phase = currentState.getCurrentPhase();

		switch (phase) {
		case FIRST:
			successors = getFirstPhaseSuccessors(currentPlayer, opponent, currentState, depth);
			break;
		case SECOND:
			successors = getSecondPhaseSuccessors(currentPlayer, opponent, currentState, depth);
			break;
		case FINAL:
			successors = getFinalPhaseSuccessors(currentPlayer, opponent, currentState, depth);
			break;
		default:
			throw new IllegalStateException("Unknown Phase");
		}

		return successors;
	}

	private List<Successor> getFirstPhaseSuccessors(Checker currentPlayer, Checker opponent, MatrixState currentState,
			int depth) {
		List<Successor> successors = new ArrayList<Successor>();
		byte[][][] matrix = currentState.getGameMatrix();

		for (int x; x < 3; x++) {
			for (int y; y < 3; y++) {

				// ignora il "buco centrale" nella matrice
				if (x == 2 && y == 2)
					continue;

				for (int z; z < 3; z++) {
					if (currentState.getGameMatrix()[x][y][z] != MatrixState.empty)
						continue;
					MatrixState newState = currentState.cloneState();

					// posiziono la pedina e aggiorno il numero di pedine sulla
					// scacchiera e da posizionare
					newState.placeChecker(currentState.getCurrentPlayer(), x, y, z);

					// dopo aver posizionato la pedina, se il giocatore è il
					// nero e non ha più pedine da disporre
					// allora termina la fase 1
					if ((newState.getCheckers()[MatrixState.black]
							- newState.getCheckersOnBoard()[MatrixState.black]) <= 0)
						newState.setCurrentPhase(Phase.SECOND);

					//TODO da qui!!!
					
					// se abbiamo un mulino, consideriamo tutte le possibilità
					// di rimozione
					if (Util.hasCompletedTriple(newState, p, currentPlayer)) {
						for (String position : opponentPositions) {

							// faccio un nuovo clone del nuovo stato per ogni
							// possibile pedina avversaria da eliminare

							State temp = newState.clone();

							// elimino la pedina dell'avversario e aggiorno il
							// numero di pedine

							temp.getBoard().put(position, Checker.EMPTY);
							temp.setCheckersOnBoard(opponent, false);

							successors.add(new Successor(p + position, temp, depth + 1));
						}
					} else {
						successors.add(new Successor(p, newState, depth + 1));
					}
				}
			}
		}
		return successors;
	}

	private List<Successor> getSecondPhaseSuccessors(Checker currentPlayer, Checker opponent, MatrixState currentState,
			int depth) {
		List<Successor> successors = new ArrayList<Successor>();
		List<String> emptyPositions = currentState.getCheckerPositions(State.Checker.EMPTY, false);
		List<String> playerPositions = currentState.getCheckerPositions(currentPlayer, false);
		List<String> opponentPositions = currentState.getCheckerPositions(opponent, true);

		for (String from : playerPositions) {
			for (String to : emptyPositions) {
				try {
					if (Util.areAdiacent(from, to)) {

						State newState = currentState.clone();

						// sposto la pedina

						newState.getBoard().put(from, State.Checker.EMPTY);
						newState.getBoard().put(to, currentPlayer);

						if (Util.hasCompletedTriple(newState, to, currentPlayer)) {
							for (String p : opponentPositions) {

								// creo un clone del nuovo stato per ogni
								// possibile pedina avversaria da eliminare

								State temp = newState.clone();

								// elimino la pedina dell'avversario e aggiorno
								// il numero di pedine sulla scacchiera

								temp.getBoard().put(p, State.Checker.EMPTY);
								temp.setCheckersOnBoard(opponent, false);

								// se all'avversario sono rimaste 3 pedine si
								// passa alla fase finale

								if (temp.getCheckersOnBoard(opponent) == 3)
									temp.setCurrentPhase(Phase.FINAL);

								successors.add(new Successor(from + to + p, temp, depth + 1));
							}
						} else {
							successors.add(new Successor(from + to, newState, depth + 1));
						}
					}
				} catch (WrongPositionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return successors;
	}

	private List<Successor> getFinalPhaseSuccessors(Checker currentPlayer, Checker opponent, MatrixState currentState,
			int depth) {
		List<Successor> successors = new ArrayList<Successor>();
		List<String> emptyPositions = currentState.getCheckerPositions(Checker.EMPTY, false);
		List<String> playerPositions = currentState.getCheckerPositions(currentPlayer, false);
		List<String> opponentPositions = currentState.getCheckerPositions(opponent, true);

		boolean isSecondPhase = false;

		// se il giocatore corrente ha più di 3 pedine allora per lui valgono le
		// regole delle 2 fase

		if (currentState.getCheckersOnBoard(currentPlayer) > 3)
			isSecondPhase = true;

		for (String from : playerPositions) {
			for (String to : emptyPositions) {
				State newState = currentState.clone();

				// se è nella fase finale

				if (!isSecondPhase) {

					// sposto la pedina in qualsiasi altra posizione vuota

					newState.getBoard().put(from, Checker.EMPTY);
					newState.getBoard().put(to, currentPlayer);

					if (Util.hasCompletedTriple(newState, to, currentPlayer)) {
						for (String p : opponentPositions) {

							// creo un clone del nuovo stato per ogni possibile
							// pedina avversaria da eliminare

							State temp = newState.clone();

							// elimino la pediana dell'avversario e aggiorno il
							// numero di pedine sulla scacchiera

							temp.getBoard().put(p, Checker.EMPTY);
							temp.setCheckersOnBoard(opponent, false);

							successors.add(new Successor(from + to + p, temp, depth + 1));
						}
					} else {
						successors.add(new Successor(from + to, newState, depth + 1));
					}
				}
				// è nella 2 fase
				else {
					try {
						if (Util.areAdiacent(from, to)) {
							newState.getBoard().put(from, Checker.EMPTY);
							newState.getBoard().put(to, currentPlayer);

							if (Util.hasCompletedTriple(newState, to, currentPlayer)) {
								for (String p : opponentPositions) {

									// creo un clone del nuovo stato per ogni
									// possibile pedina avversaria da eliminare

									State temp = newState.clone();

									// elimino la pediana dell'avversario e
									// aggiorno il numero di pedine sulla
									// scacchiera

									temp.getBoard().put(p, Checker.EMPTY);
									temp.setCheckersOnBoard(opponent, false);

									successors.add(new Successor(from + to + p, temp, depth + 1));
								}
							} else {
								successors.add(new Successor(from + to, newState, depth + 1));
							}
						}
					} catch (WrongPositionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return successors;
	}
}
