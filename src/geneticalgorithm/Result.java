package geneticalgorithm;

public class Result {

	private boolean ok;
	private int result;
	
	public Result() {
		ok = false;
		result = -1;
	}
	
	public synchronized void setOk(boolean value) {
		ok = value;
	}
	
	public synchronized boolean getOk() {
		return ok;
	}
	
	public synchronized void setResult(int value) {
		result = value;
	}
	
	public synchronized int getResult() {
		return result;
	}
}
