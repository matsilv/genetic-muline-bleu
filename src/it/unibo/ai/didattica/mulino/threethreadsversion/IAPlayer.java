package it.unibo.ai.didattica.mulino.threethreadsversion;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.actions.Phase1Action;
import it.unibo.ai.didattica.mulino.actions.Phase2Action;
import it.unibo.ai.didattica.mulino.actions.PhaseFinalAction;
import it.unibo.ai.didattica.mulino.algorithms.Negascout;
import it.unibo.ai.didattica.mulino.client.MulinoClient;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;
import it.unibo.ai.didattica.mulino.searchutility.StopSearch;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;
import it.unibo.ai.didattica.mulino.searchutility.TimerThread;

import java.io.IOException;
import java.net.UnknownHostException;



public class IAPlayer extends MulinoClient {

	public IAPlayer(Checker player) throws UnknownHostException,
			IOException {
		super(player);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		
		String type = "w";
		Checker player = null;
		Checker opponent = null;
		long timeout = 60;
		
		/*if(args.length != 2) {
			System.out.println("Usage: color < w | b > timeout < integer >");
			System.exit(1);
		}
		else {
			type = args[0];
			try {
				timeout = Long.parseLong(args[1]);
			}
			catch(NumberFormatException e) {
				System.out.println("Timeout must be a number");
				System.exit(2);
			}
		}*/
		
		if(type.equals("b")) {
			player = Checker.BLACK;
			opponent = Checker.WHITE;
		}
		else if(type.equals("w")) {
			player = Checker.WHITE;
			opponent = Checker.BLACK;
		}
		else {
			System.out.println("Checker type not valid");
			System.exit(3);
		}
		
		String actionString = "";
		Action action = null;
		State currentState = null;
		double alpha = -100000;
		double beta = 100000;
		
		if (player == State.Checker.WHITE) {
			MulinoClient client = new IAPlayer(State.Checker.WHITE);
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			System.out.println("Current state:");
			currentState = client.read();
			System.out.println(currentState.toString());
			while (true) {
				
				// variabile condivisa per saper se il timer è scaduto
				StopSearch stopSearch = new StopSearch();
				
				System.out.println("\n\nPlayer " + client.getPlayer().toString() + ", do your move: ");
				
				// timer thread
				Thread timer = new Thread(new TimerThread(timeout - 2, stopSearch));
				timer.start(); 
				
				Negascout search = new Negascout(new SuccessorFunction(), 
						player, opponent, stopSearch);
				actionString = search.doSearch(currentState, alpha, beta);
				
				action = stringToAction(actionString, currentState.getCurrentPhase());
				client.write(action);
				
				currentState = client.read();
				System.out.println("Effect of your move: ");
				System.out.println(currentState.toString());
				
				System.out.println("Waiting for your opponent move... ");
				currentState = client.read();
				System.out.println("Your Opponent did his move, and the result is: ");
				System.out.println(currentState.toString());
			}
		} else {
			MulinoClient client = new IAPlayer(State.Checker.BLACK);
			currentState = client.read();
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			System.out.println("Current state:");
			System.out.println(currentState.toString());
			while (true) {
				System.out.println("Waiting for your opponent move...");
				currentState = client.read();
				System.out.println("Your Opponent did his move, and the result is: ");
				System.out.println(currentState.toString());
				System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
				
				// variabile condivisa per saper se il timer è scaduto
				StopSearch stopSearch = new StopSearch();
				
				// timer thread
				Thread timer = new Thread(new TimerThread(timeout - 2, stopSearch));
				timer.start();
				
				Negascout search = new Negascout(new SuccessorFunction(), 
						player, opponent, stopSearch);
				actionString = search.doSearch(currentState, alpha, beta);
				
				action = stringToAction(actionString, currentState.getCurrentPhase());
				client.write(action);
				
				currentState = client.read();
				System.out.println("Effect of your move: ");
				System.out.println(currentState.toString());
			}
		}
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
