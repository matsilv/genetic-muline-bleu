package it.unibo.ai.didattica.mulino.threethreadsversion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TimerThread implements Runnable {

	private long timeout;
	private StopSearch stop;
	private Lock lock;
	private Condition condition;
	
	public TimerThread(long timeout, StopSearch stop, Lock lock, Condition condition) {
		this.timeout = timeout;
		this.stop = stop;
		this.lock = lock;
		this.condition = condition;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("TimerThread is going to sleep...");
			Thread.sleep(timeout * 1000);
			System.out.println("TimerThread wakes up...");
			this.notifyTimeout();
		} catch (InterruptedException e) {
			System.out.println("TimerThread interrupted exception...");
			System.exit(4);
		}
		
	}
	
	private void notifyTimeout() {
		lock.lock();
		try {
			stop.setIsTimeout(true);
			condition.signal();
			System.out.println("TimerThread sent signal...");
		}
		finally {
			lock.unlock();
		}
	}

}

