package it.unibo.ai.didattica.mulino.client;

public class Debug {
	public static boolean ACTIVATE_DEBUG = true;
	
	public static void log(String string) {	
		if (ACTIVATE_DEBUG)
			System.out.println(string);
	}
}
