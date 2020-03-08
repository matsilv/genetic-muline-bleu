package geneticalgorithm;

import it.unibo.ai.didattica.mulino.domain.State.Checker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class Main {

	static final int N = 4;
	static final int P = 25;
	static final int MAX = 50;
	static final int NUM = 6;
	
	public static void main(String[] args) {
		
		Parametres[] population = new Parametres[N];
		population[0] = new Parametres(15, 13, 4);
		population[1] = new Parametres(50, 50, 50);
		int count = 24;
		
		BufferedWriter bw = null;
		
		try {
			FileWriter file = new FileWriter("/home/mattia/Scrivania/risultati2.txt", true);
			bw = new BufferedWriter(file);
			bw.newLine();
			bw.newLine();
			bw.write("For victory 5 points, for stale 1 point, for lose -5 points.");
			bw.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		
		while(true) {
			
			Parametres parent_1 = population[0];
			Parametres parent_2 = population[1];
			
			int[] openMills = crossover(getBitsOf(parent_1.getOpenMill()), 
					getBitsOf(parent_2.getOpenMill()));
			int[] numberOfPieces = crossover(getBitsOf(parent_1.getNumberOfPieces()), 
					getBitsOf(parent_2.getNumberOfPieces()));
			int[] blockedMoves = crossover(getBitsOf(parent_1.getBlockedMoves()), 
					getBitsOf(parent_2.getBlockedMoves()));
			
			Random r2 = new Random();
			
			for(int j=0;j<3;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(openMills, j);
			}
			
			for(int j=0;j<4;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(numberOfPieces, j);
			}
			
			for(int j=0;j<4;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(blockedMoves, j);
			}
			
			population[2] = new Parametres();
			
			population[2].setOpenMill(getNumberOf(openMills));
			population[2].setNumberOfPieces(getNumberOf(numberOfPieces));
			population[2].setBlockedMoves(getNumberOf(blockedMoves));
			
			openMills = crossover(getBitsOf(parent_2.getOpenMill()), 
					getBitsOf(parent_1.getOpenMill()));
			numberOfPieces = crossover(getBitsOf(parent_2.getNumberOfPieces()), 
					getBitsOf(parent_1.getNumberOfPieces()));
			blockedMoves = crossover(getBitsOf(parent_2.getBlockedMoves()), 
					getBitsOf(parent_1.getBlockedMoves()));
			
			for(int j=0;j<4;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(openMills, j);
			}
			
			for(int j=0;j<4;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(numberOfPieces, j);
			}
			
			for(int j=0;j<4;j++) {
				int p = r2.nextInt(100);
				
				if(p < P)
					invert(blockedMoves, j);
			}
			
			population[3] = new Parametres();
			
			population[3].setOpenMill(getNumberOf(openMills));
			population[3].setNumberOfPieces(getNumberOf(numberOfPieces));
			population[3].setBlockedMoves(getNumberOf(blockedMoves));
			

			openMills = crossover(getBitsOf(parent_2.getOpenMill()), 
					getBitsOf(parent_1.getOpenMill()));
			numberOfPieces = crossover(getBitsOf(parent_2.getNumberOfPieces()), 
					getBitsOf(parent_1.getNumberOfPieces()));
			blockedMoves = crossover(getBitsOf(parent_2.getBlockedMoves()), 
					getBitsOf(parent_1.getBlockedMoves()));		
			
			try {
				bw.write("Tournament n." + count + ".");
				bw.newLine();
				bw.flush();
				doTournament(population, new Result(), bw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			Parametres[] best = getBest(population);
			
			population[0] = new Parametres();
			population[1] = new Parametres();
			
			population[0].setBlockedMoves(best[0].getBlockedMoves());
			population[0].setNumberOfPieces(best[0].getNumberOfPieces());
			population[0].setOpenMill(best[0].getOpenMill());
			
			population[1].setBlockedMoves(best[1].getBlockedMoves());
			population[1].setNumberOfPieces(best[1].getNumberOfPieces());
			population[1].setOpenMill(best[1].getOpenMill());
			
			count++;
		}		
	}
	
	public static Parametres[] getBest(Parametres[] pop) {
		Parametres best[] = new Parametres[N/2];
		best[0] = new Parametres();
		best[1] = new Parametres();
		
		for(int i=0;i<N;i++) {
			if(pop[i].getPoints() > best[0].getPoints()) {
				
				best[1].setBlockedMoves(best[0].getBlockedMoves());
				best[1].setNumberOfPieces(best[0].getNumberOfPieces());
				best[1].setOpenMill(best[0].getOpenMill());
				best[1].setPoints(best[0].getPoints());
				
				best[0].setBlockedMoves(pop[i].getBlockedMoves());
				best[0].setNumberOfPieces(pop[i].getNumberOfPieces());
				best[0].setOpenMill(pop[i].getOpenMill());
				best[0].setPoints(pop[i].getPoints());
			}
			else if(pop[i].getPoints() > best[1].getPoints()) {
				
				best[1].setBlockedMoves(pop[i].getBlockedMoves());
				best[1].setNumberOfPieces(pop[i].getNumberOfPieces());
				best[1].setOpenMill(pop[i].getOpenMill());
				best[1].setPoints(pop[i].getPoints());

			}
		}
		
		return best;
	}
	
	public static void doTournament(Parametres[] pop, Result result, BufferedWriter bw) throws IOException {
		
		int count = 0;
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				
				if(i == j)
					continue;
				
				bw.write("Match n." + count++ + ".");
				bw.newLine();
				bw.flush();
				
				bw.write(pop[i].toString() + " VS " + pop[j].toString() + ".");
				bw.flush();
				bw.newLine();
				
				EngineThread engine = new EngineThread(result);
				
				engine.start();
				
				Thread t = new Thread();
				try {
					t.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
								
					GeneticThread player1 = new GeneticThread(Checker.WHITE, Checker.BLACK, pop[i]);
					
					GeneticThread player2 = new GeneticThread(Checker.BLACK, Checker.WHITE, pop[j]);
			
					player1.start();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					player2.start();
					
					System.out.println("ServerSocket opened.");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while(!result.getOk());
				
				switch(result.getResult()) {
				case 0 : pop[i].hasWin(); pop[j].hasLost();
				bw.write(pop[i].toString() + " WIN!");
				bw.newLine(); bw.flush();				
				break;
				case 1 : pop[j].hasWin(); pop[i].hasLost();
				bw.write(pop[j].toString() + " WIN!"); 
				bw.newLine(); bw.flush();
				break;
				case 2 : pop[i].hasStale(); pop[j].hasStale();
				bw.write(pop[i].toString() + " " + pop[j].toString() + " STALE!");
				bw.newLine(); bw.flush(); break;
				default : break;
				}
				
				result.setOk(false);
				
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		bw.newLine();
		bw.write("Classification: ");
		
		for(int i=0;i<N;i++) {
			bw.write(pop[i].toString());
			bw.newLine();
		}
		
		bw.flush();
	}
	
static int[] getBitsOf(int num) {
		
		int[] res = new int[NUM];
		int dim = 0;
		int val;
		
		while(num > 0) {
			
			val = num % 2;
			res[dim++] = val;
			num /= 2;
		}
		
		return res;
	}
	
	static String stringOfBits(int[] num) {
		String res = "";
		
		for(int i=0;i<NUM;i++) {
			res += num[i];
		}
		
		return res;
	}
	
	static int getNumberOf(int[] bits) {
		
		int num = 0;
		int pow = 1;
		
		for(int i=0;i<NUM;i++) {
			
			num += bits[i] * pow;
			pow *= 2;
		}
		
		return num;
	}
	
	static int[] crossover(int[] first, int[] second) {
		int[] son = new int[NUM];
		
		for(int i=0;i<4;i++) {
			son[i] = first[i];
		}
		
		for(int i=4;i<NUM;i++) {
			son[i] = second[i];
		}
		
		return son;
	}
	
	static void invert(int[] son, int index) {
		if(son[index] == 0)
			son[index] = 1;
		else son[index] = 0;
	}
	
	static void copy(Parametres[] oldP, Parametres[] newP) {
		
		for(int i=0;i<N;i++) {
			oldP[i].setBlockedMoves(newP[i].getBlockedMoves());
			oldP[i].setNumberOfPieces(newP[i].getNumberOfPieces());
			oldP[i].setOpenMill(newP[i].getOpenMill());
		}
	}
}