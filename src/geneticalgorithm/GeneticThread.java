package geneticalgorithm;

import it.unibo.ai.didattica.mulino.domain.State.Checker;

import java.io.IOException;
import java.net.UnknownHostException;

public class GeneticThread extends Thread {

	private Checker player;
	private Checker opponent;
	private Parametres par;
	
	public GeneticThread(Checker player, Checker opponent, 
			Parametres par) throws UnknownHostException,
			IOException {
		
		this.player = player;
		this.opponent = opponent;
		this.par = par;
	}
	
	public void run(){
		
		GeneticPlayer gp;
		try {
			gp = new GeneticPlayer(player, opponent, par);
			gp.go();
		} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
