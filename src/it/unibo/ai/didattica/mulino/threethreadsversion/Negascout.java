package it.unibo.ai.didattica.mulino.threethreadsversion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.searchutility.Successor;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;

public class Negascout {

	// oggetto che resituisce gli stati successori
	private SuccessorFunction function;

	// colore del giocatore e dell'avversario
	private State.Checker player;
	private State.Checker opponent;
	
	// mossa migliore
	private BestMove bestMove;
	
	private long timeout;
	
	// gestione wait e notify di alto livello
	private Lock lock;
	private Condition condition;

	public Negascout(SuccessorFunction function, State.Checker player, State.Checker opponent, long timeout) {
		super();
		this.function = function;
		this.player = player;
		this.opponent = opponent;
		bestMove = new BestMove();
		this.timeout = timeout;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}

	// metodo accessibile dall'esterno per effettuare la ricerca
	public String doSearch(State initState) {
		Successor init = new Successor("", initState);

		boolean isPlayerTurn = true;
		
		// variabile condivisa per saper se il timer è scaduto
		StopSearch stopSearch = new StopSearch();
		
		// timer thread
		Thread timer = new Thread(new TimerThread(timeout - 2, stopSearch, lock, condition));
		timer.start(); 
		
		Thread negaThread = new Thread(new NegascoutThread(stopSearch, isPlayerTurn, bestMove, init, player, opponent, function));
		negaThread.start();
		
		this.waitingForNegascout(stopSearch);
		
		System.out.println("Timeout...");

		return bestMove.getAction();
	}
	
	private void waitingForNegascout(StopSearch stopSearch) {
		lock.lock();
		try {
			while(!stopSearch.getIsTimeout()) {
				System.out.println("Waiting for iterative deepening...");
				condition.await();
			}
		} catch (InterruptedException e) {
			System.out.println("InterruptedException occurred...");
		}
		finally {
			lock.unlock();
		}

	}
}
