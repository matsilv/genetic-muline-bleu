package geneticalgorithm;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.client.MulinoClient;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.StopSearch;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;
import it.unibo.ai.didattica.mulino.searchutility.TimerThread;

import java.io.IOException;
import java.net.UnknownHostException;



public class GeneticPlayer extends MulinoClient {
	
	private Checker player;
	private Checker opponent;
	private Parametres par;
	
	public GeneticPlayer(Checker player, Checker opponent, Parametres par) throws UnknownHostException,
			IOException {
		
		super(player);
		this.player = player;
		this.opponent = opponent;
		this.par = par;
	}
	
	public void go() throws UnknownHostException, ClassNotFoundException {
		
		String actionString = "";
		Action action = null;
		it.unibo.ai.didattica.mulino.domain.State currentState = null;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		
		System.out.println("You are player " + this.getPlayer().toString() + "!");
		System.out.println("Current state:");
		try {
			currentState = this.read();
		} catch (IOException e3) {
			this.close();
		}
		System.out.println(currentState.toString());
		
		while (currentState != null) {
			
			if(player.equals(Checker.WHITE)) {
				
				// variabile condivisa per saper se il timer è scaduto
				StopSearch stopSearch = new StopSearch();
				
				System.out.println("\n\nPlayer " + player.toString() + ", do your move: ");
				
				// timer thread
				Thread timer = new Thread(new TimerThread(3, stopSearch));
				timer.start(); 
				
				Negascout search = new Negascout(new SuccessorFunction(), 
						player, opponent, stopSearch);
				actionString = search.doSearch(currentState, alpha, beta, par);
				
				if(actionString.isEmpty() || actionString == null)
					break;
				
				action = stringToAction(actionString, currentState.getCurrentPhase());
				try {
					this.write(action);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					currentState = this.read();
				} catch (IOException e1) {
					this.close();
					break;
				}
				if(currentState == null)
					break;
				System.out.println("Effect of your move: ");
				System.out.println(currentState.toString());
				
				System.out.println("Waiting for your opponent move... ");
				try {
					currentState = this.read();
				} catch (IOException e) {
					this.close();
					break;
				}
				if(currentState == null)
					break;
				System.out.println("Your Opponent did his move, and the result is: ");
				System.out.println(currentState.toString());
			}
			
			else {
				
				System.out.println("Waiting for your opponent move...");
				try {
					currentState = this.read();
				} catch (IOException e2) {
					this.close();
					break;
				}
				System.out.println("Your Opponent did his move, and the result is: ");
				System.out.println(currentState.toString());
				System.out.println("Player " + this.getPlayer().toString() + ", do your move: ");
				
				// variabile condivisa per saper se il timer è scaduto
				StopSearch stopSearch = new StopSearch();
				
				// timer thread
				Thread timer = new Thread(new TimerThread(3, stopSearch));
				timer.start();
				
				Negascout search = new Negascout(new SuccessorFunction(), 
						player, opponent, stopSearch);
				actionString = search.doSearch(currentState, alpha, beta, par);
				
				if(actionString.isEmpty() || actionString == null)
					break;
				
				action = stringToAction(actionString, currentState.getCurrentPhase());
				try {
					this.write(action);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					break;
				}
				
				try  {
					currentState = this.read();
				}
				catch (IOException e) {
					this.close();
					break;
				}
				
				System.out.println("Effect of your move: ");
				if(currentState == null)
					break;
				System.out.println(currentState.toString());
			}
		}
		
		System.out.println("Player " + player + " has finished");
		this.close();
	}
	
	private static Action stringToAction(String actionString, Phase fase) {
		if (fase == Phase.FIRST) { // prima fase
			Phase1Action action;
			action = new Phase1Action();
			action.setPutPosition(actionString.substring(0, 2));
			if (actionString.length() == 4)
				action.setRemoveOpponentChecker(actionString.substring(2, 4));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		} else if (fase == Phase.SECOND) { // seconda fase
			Phase2Action action;
			action = new Phase2Action();
			action.setFrom(actionString.substring(0, 2));
			action.setTo(actionString.substring(2, 4));
			if (actionString.length() == 6)
				action.setRemoveOpponentChecker(actionString.substring(4, 6));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		} else { // ultima fase
			PhaseFinalAction action;
			action = new PhaseFinalAction();
			action.setFrom(actionString.substring(0, 2));
			action.setTo(actionString.substring(2, 4));
			if (actionString.length() == 6)
				action.setRemoveOpponentChecker(actionString.substring(4, 6));
			else
				action.setRemoveOpponentChecker(null);
			return action;
		}
	}

}