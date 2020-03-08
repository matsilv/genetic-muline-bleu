package it.unibo.ai.didattica.mulino.domain;

import java.util.Arrays;

import it.unibo.ai.didattica.mulino.domain.State.Phase;

public class MatrixState {

	private byte[][][] gameMatrix;
	private byte[] checkersOnBoard;
	private byte[] checkers;

	// Costanti per indicare il giocatore corrente e le pedine
	public static final byte white = 0;
	public static final byte black = 1;
	public static final byte empty = 2;

	private byte activeMills = 0;
	
	private Phase currentPhase = Phase.FIRST;
	private byte currentPlayer = white;

	public byte[] getCheckersOnBoard() {
		return checkersOnBoard;
	}

	public void setCheckersOnBoard(byte[] checkersOnBoard) {
		this.checkersOnBoard = checkersOnBoard;
	}

	public byte[] getCheckers() {
		return checkers;
	}

	public void setCheckers(byte[] checkers) {
		this.checkers = checkers;
	}

	public void setCurrentPhase(Phase currentPhase) {
		this.currentPhase = currentPhase;
	}

	public void setCurrentPlayer(byte currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(checkers);
		result = prime * result + Arrays.hashCode(checkersOnBoard);
		result = prime * result + ((currentPhase == null) ? 0 : currentPhase.hashCode());
		result = prime * result + currentPlayer;
		result = prime * result + Arrays.deepHashCode(gameMatrix);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatrixState other = (MatrixState) obj;
		if (!Arrays.equals(checkers, other.checkers))
			return false;
		if (!Arrays.equals(checkersOnBoard, other.checkersOnBoard))
			return false;
		if (currentPhase != other.currentPhase)
			return false;
		if (currentPlayer != other.currentPlayer)
			return false;
		if (!Arrays.deepEquals(gameMatrix, other.gameMatrix))
			return false;
		return true;
	}

	public byte[][][] getGameMatrix() {
		return gameMatrix;
	}

	public void setGameMatrix(byte[][][] gameMatrix) {
		this.gameMatrix = gameMatrix;
	}

	// funzione di utilità per convertire i valori della matrice in char
	private String boardget(String pos) {
		Key k = Helper.getKey(pos);
		int num = gameMatrix[k.x][k.y][k.z];
		switch (num) {
		case 1:
			return "W";
		case -1:
			return "B";
		case 0:
			return "O";
		default:
			throw new IllegalArgumentException("Out of bound");
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("7 " + boardget("a7") + "--------" + boardget("d7") + "--------" + boardget("g7") + "\n");
		result.append("6 |--" + boardget("b6") + "-----" + boardget("d6") + "-----" + boardget("f6") + "--|\n");
		result.append("5 |--|--" + boardget("c5") + "--" + boardget("d5") + "--" + boardget("e5") + "--|--|\n");
		result.append("4 " + boardget("a4") + "--" + boardget("b4") + "--" + boardget("c4") + "     " + boardget("e4")
				+ "--" + boardget("f4") + "--" + boardget("g4") + "\n");
		result.append("3 |--|--" + boardget("c3") + "--" + boardget("d3") + "--" + boardget("e3") + "--|--|\n");
		result.append("2 |--" + boardget("b2") + "-----" + boardget("d2") + "-----" + boardget("f2") + "--|\n");
		result.append("1 " + boardget("a1") + "--------" + boardget("d1") + "--------" + boardget("g1") + "\n");
		result.append("  a  b  c  d  e  f  g\n");
		result.append("Phase: " + currentPhase.toString() + ";\n");
		result.append("White Checkers: " + checkers[white] + ";\n");
		result.append("Black Checkers: " + checkers[black] + ";\n");
		result.append("White Checkers On Board: " + checkersOnBoard[white] + ";\n");
		result.append("Black Checkers On Board: " + checkersOnBoard[black] + ";\n");
		return result.toString();
	}

	private MatrixState() {
		gameMatrix = new byte[3][3][3];
		checkersOnBoard = new byte[2];
		checkers = new byte[2];
		checkersOnBoard[white] = 0;
		checkersOnBoard[black] = 0;
		checkers[white] = 9;
		checkers[black] = 9;
	}

	private MatrixState(byte[] checkersOnBoard, byte[] checkers, byte[][][] gameMatrix, Phase currentPhase,
			byte currentPlayer) {
		this.checkers = checkers;
		this.checkersOnBoard = checkersOnBoard;
		this.gameMatrix = gameMatrix;
		this.currentPhase = currentPhase;
		this.currentPlayer = currentPlayer;
	}

	/*
	 * AGGIUNTO DA MATTEO (21/4) Metodo per aggiornare lo stato in
	 * seguito al piazzamento di una pedina (in fase 1)
	 */
	public void placeChecker(byte value, int x, int y, int z) {

		// Controllo se la mossa è permessa
		if (x == 2 && y == 2)
			throw new IllegalArgumentException("Illegal position (central hole)");
		if (gameMatrix[x][y][z] != empty)
			throw new IllegalArgumentException("Illegal move (square not free)");
		if (value != currentPlayer)
			throw new IllegalArgumentException("Illegal move (not your turn)");
		if (checkers[value] - checkersOnBoard[value] <= 0)
			throw new IllegalArgumentException("Illegal move (no more checkers to position)");
			
		// modifico lo stato con il posizionamento
		gameMatrix[x][y][z] = value;
		currentPlayer = value == white ? black : white;
		checkersOnBoard[value] += 1;
		detectMills(value);
	}
	
	

	private void detectMills(byte player) {
		// TODO
	}

	/*
	 * AGGIUNTO DA MATTEO (21/4) Metodo per aggiornare lo stato in
	 * seguito allo spostamento di una pedina (in fase 2 o 3)
	 */
	public void moveChecker(byte value, int xFrom, int yFrom, int zFrom, int xTo, int yTo, int zTo) {
// TODO
//		// Controllo se la mossa è permessa
//		if (x == 2 && y == 2)
//			throw new IllegalArgumentException("Illegal position (central hole)");
//		if (gameMatrix[x][y][z] != empty)
//			throw new IllegalArgumentException("Illegal move (square not free)");
//		if (value != currentPlayer)
//			throw new IllegalArgumentException("Illegal move (not your turn)");
//		if (checkers[value] - checkersOnBoard[value] <= 0)
//			throw new IllegalArgumentException("Illegal move (no more checkers to position)");
//			
//		// modifico lo stato con il posizionamento
//		gameMatrix[x][y][z] = value;
//		currentPlayer = value == white ? black : white;
//		checkersOnBoard[value] += 1;		
	}
	
	/*
	 * AGGIUNTO DA MATTEO (21/4) Metodo per aggiornare lo stato in
	 * seguito alla rimozione di una pedina (dopo mulino).
	 * !!! USARE SEMPRE DOPO placeChekcer o moveChecker !!!
	 */
	public void removeChecker(int x, int y, int z){
		// Controllo se la mossa è permessa
				if (x == 2 && y == 2)
					throw new IllegalArgumentException("Illegal position (central hole)");
				if (gameMatrix[x][y][z] != currentPlayer)
					throw new IllegalArgumentException("Illegal move (checker not present or belonging to the wrong player)");
				
				// Modifico lo stato
				gameMatrix[x][y][z] = empty;
				checkersOnBoard[currentPlayer] -= 1;
				checkers[currentPlayer] -= 1;
	}

	public Phase getCurrentPhase() {
		return currentPhase;
	}

	public byte getCurrentPlayer() {
		return currentPlayer;
	}

	public MatrixState cloneState() {
		byte[][][] numatrix = new byte[3][][];
		for (int i = 0; i < 3; i++) {
			numatrix[i] = new byte[3][];
			for (int j = 0; j < 3; i++) {
				byte[] aMatrix = numatrix[i][j];
				int aLength = aMatrix.length;
				numatrix[i][j] = new byte[aLength];
				System.arraycopy(aMatrix, 0, numatrix[i][j], 0, aLength);
			}
		}
		return new MatrixState(checkersOnBoard.clone(), checkers.clone(), numatrix, currentPhase, currentPlayer);
	}

}