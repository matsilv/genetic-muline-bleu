package it.unibo.ai.didattica.mulino.searchutility;

public class TimerThread implements Runnable {

	private long timeout;
	private StopSearch stop;
	
	public TimerThread(long timeout, StopSearch stop) {
		this.timeout = timeout;
		this.stop = stop;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("TimerThread is going to sleep");
			Thread.sleep(timeout * 1000);
			stop.setIsTimeout(true);
			System.out.println("TimerThread wakes up");
		} catch (InterruptedException e) {
			System.out.println("TimerThread interrupted exception");
			System.exit(4);
		}
		
	}

}
