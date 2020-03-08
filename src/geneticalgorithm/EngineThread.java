package geneticalgorithm;

import java.io.IOException;

public class EngineThread extends Thread {

	private Result result;
	
	public EngineThread(Result result) {
		this.result = result;
	}
	
	public void run() {
		Engine engine = new Engine(5);
		try {
			engine.run(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
}
