package geneticalgorithm;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class MyMain {

	static final int N = 4;
	static final int P = 25;
	static final int MAX = 50;
	static final int NUM = 6;
	static ServerSocket serverSocket;
	static Socket connectionSocket;
	static ObjectInputStream in;
	static ObjectOutputStream out;
	
	public static void main(String args[]) {
		
		Parametres[] population = new Parametres[N];
		Parametres[] newPopulation = new Parametres[N];
		Random par = new Random();
		Random r = new Random();
		Random r2 = new Random();
		Result result = new Result();
		
		// initializePopulation(population, par);
		
		setPopulation(population);
		
		System.out.println("Initial population: ");
		
		for(int i=0;i<N;i++) {
			
			System.out.println("Son n." + i + ":");
			System.out.println(population[i].toString());
		}
		
		BufferedWriter bw = null;
		
		try {
			FileWriter file = new FileWriter("/home/mattia/Scrivania/risultati.txt", true);
			bw = new BufferedWriter(file);
			bw.write("This is the file with the results.");
			bw.newLine();
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		
		try {
			doTournament(population, new Result(), bw);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* while(true) {
			
			try {

				bw.newLine();
				bw.newLine();
				bw.write("New population: ");
				bw.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			for(int i=0;i<N;i++) {
				
				int first, second;
				
				first = r.nextInt(N);
				second = r.nextInt(N);
				
				Parametres parent_1 = population[first];
				Parametres parent_2 = population[second];
				
				Parametres son = new Parametres();
				
				int[] openMills = crossover(getBitsOf(parent_1.getOpenMill()), 
						getBitsOf(parent_2.getOpenMill()));
				int[] numberOfPieces = crossover(getBitsOf(parent_1.getNumberOfPieces()), 
						getBitsOf(parent_2.getNumberOfPieces()));
				int[] blockedMoves = crossover(getBitsOf(parent_1.getBlockedMoves()), 
						getBitsOf(parent_2.getBlockedMoves()));
				
				for(int j=0;j<NUM;j++) {
					int p = r2.nextInt(100);
					
					if(p < P)
						invert(openMills, j);
				}
				
				for(int j=0;j<NUM;j++) {
					int p = r2.nextInt(100);
					
					if(p < P)
						invert(numberOfPieces, j);
				}
				
				for(int j=0;j<NUM;j++) {
					int p = r2.nextInt(100);
					
					if(p < P)
						invert(blockedMoves, j);
				}
				
				son.setOpenMill(getNumberOf(openMills));
				son.setNumberOfPieces(getNumberOf(numberOfPieces));
				son.setBlockedMoves(getNumberOf(blockedMoves));
				
				newPopulation[i] = son;
				
				System.out.println("Son n." + i + ":");
				System.out.println(newPopulation[i].toString());
			}
			
			copy(population, newPopulation);
			
			Parametres winPop[] = new Parametres[N];
			
			int dim = 0;
			
			for(int i=0;i<N/2;i++) {
				doMatch(population[i], population[N - i - 1], result, winPop, dim);
				dim++;
			}
			
			for(int i=N/2;i<N;i++) {
				winPop[i] = population[i - N / 2];
			}
			
			copy(population, winPop);
			
			for(int i=0;i<N;i++) {
				
				try {
					bw.write("Son n." + i + ":");
					bw.write(population[i].toString());
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}*/
	}
	
	static void initializePopulation(Parametres[] population, Random par) {
	
		for(int i=0;i<N;i++) {
			
			population[i] = new Parametres();
			population[i].setBlockedMoves(par.nextInt(MAX));
			population[i].setNumberOfPieces(par.nextInt(MAX));
			population[i].setOpenMill(par.nextInt(MAX));
		}
	}
	
	static void setPopulation(Parametres[] population) {
		population[0] = new Parametres();
		population[0].setBlockedMoves(38);
		population[0].setNumberOfPieces(27);
		population[0].setOpenMill(5);
		
		population[1] = new Parametres();
		population[1].setBlockedMoves(26);
		population[1].setNumberOfPieces(18);
		population[1].setOpenMill(7);
		
		population[2] = new Parametres();
		population[2].setBlockedMoves(30);
		population[2].setNumberOfPieces(21);
		population[2].setOpenMill(38);
		
		population[3] = new Parametres();
		population[3].setBlockedMoves(18);
		population[3].setNumberOfPieces(8);
		population[3].setOpenMill(15);
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
		
		for(int i=0;i<NUM/2;i++) {
			son[i] = first[i];
		}
		
		for(int i=NUM/2;i<NUM;i++) {
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
	
	static void doMatch(Parametres first, Parametres second, Result result, Parametres[] winPop, int dim) {

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
						
			GeneticThread player1 = new GeneticThread(Checker.WHITE, Checker.BLACK, first);
			
			GeneticThread player2 = new GeneticThread(Checker.BLACK, Checker.WHITE, second);
	
			player1.start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			player2.start();
			
			System.out.println("ServerSocket opened");
			
			/*serverSocket = new ServerSocket(5803);
			connectionSocket = serverSocket.accept();
			System.out.println("Connection established");
			in = new ObjectInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
			out = new ObjectOutputStream(connectionSocket.getOutputStream());*/
			//int result = in.readInt();
			
			while(!result.getOk());
			
			switch(result.getResult()) {
				case 0 : System.out.println("White has win");
						System.out.println(first.toString());
						winPop[dim] = first;
						break;
				case 1 : System.out.println("Black has win");
						System.out.println(second.toString());
						winPop[dim] = second;
						break;
				case 2 : System.out.println("Stale"); 
						winPop[dim] = first;
						break;
				default : System.out.println("An error occured");
			}			
			
			result.setOk(false);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doTournament(Parametres[] pop, Result result, BufferedWriter bw) throws IOException {
		
		int count = 0;
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				
				if(i == j)
					continue;
				
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
				
				System.out.println("Match n." + count + ".");
				
				while(!result.getOk());
				
				bw.write("First: " + pop[i].toString() + " VS second: " + pop[j].toString());
				bw.newLine();
				
				switch(result.getResult()) {
				case 0 : pop[i].hasWin(); bw.write("First has win!"); break;
				case 1 : pop[j].hasWin(); bw.write("Second has win!"); break;
				case 2 : pop[i].hasStale(); pop[j].hasStale(); bw.write("Stale."); break;
				default : break;
				}
				
				result.setOk(false);
				
				bw.flush();
				
				System.out.println();
				System.out.println("Partial classification.");
				
				for(int k=0;k<N;k++)
					System.out.println(pop[k].toString());
				
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		bw.newLine();
		
		for(int i=0;i<N;i++) {
			bw.write(pop[i].toString());
			bw.newLine();
		}
		
		bw.flush();
	}		
}
