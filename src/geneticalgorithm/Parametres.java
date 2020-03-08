package geneticalgorithm;

public class Parametres {
	private int openMill;
	private int numberOfPieces;
	private int blockedMoves;
	private int points;
	
	public Parametres() {
		this.points = 0;
		this.blockedMoves = 0;
		this.numberOfPieces = 0;
		this.openMill = 0;
	}
	
	public Parametres(int openMill, int numberOfPieces, int blockedMoves) {
		super();
		this.openMill = openMill;
		this.numberOfPieces = numberOfPieces;
		this.blockedMoves = blockedMoves;
		this.points = 0;
	}

	public int getOpenMill() {
		return openMill;
	}

	public void setOpenMill(int openMill) {
		this.openMill = openMill;
	}

	public int getNumberOfPieces() {
		return numberOfPieces;
	}

	public void setNumberOfPieces(int numberOfPieces) {
		this.numberOfPieces = numberOfPieces;
	}

	public int getBlockedMoves() {
		return blockedMoves;
	}

	public void setBlockedMoves(int blockedMoves) {
		this.blockedMoves = blockedMoves;
	}
	
	public String toString() {
		String res = "";
		
		res += "open mill: " + this.openMill + ", number of pieces: " + this.numberOfPieces +
				", blocked moves: " + this.blockedMoves + ", total points: " + this.points;
		
		return res;
	}
	
	public void hasWin() {
		this.points += 5;
	}
	
	public void hasStale() {
		this.points += 1;
	}
	
	public void hasLost() {
		this.points -= 5;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	
}
