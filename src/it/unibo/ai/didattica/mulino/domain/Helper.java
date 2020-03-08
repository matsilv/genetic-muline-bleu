package it.unibo.ai.didattica.mulino.domain;
import java.util.HashMap;
import it.unibo.ai.didattica.mulino.domain.Key;

public class Helper {
	static HashMap<String, Key> keys;
	static {
		keys = new HashMap<>();
		Helper.keys.put("a1", new Key(0,0,2));
		Helper.keys.put("a4", new Key(0,1,2));
		Helper.keys.put("a7", new Key(0,2,2));
		Helper.keys.put("b2", new Key(0,0,1));
		Helper.keys.put("b4", new Key(0,1,1));
		Helper.keys.put("b6", new Key(0,2,2));
		Helper.keys.put("c3", new Key(0,0,0));
		Helper.keys.put("c4", new Key(0,1,0));
		Helper.keys.put("c5", new Key(0,2,0));
		Helper.keys.put("d1", new Key(1,0,2));
		Helper.keys.put("d2", new Key(1,0,1));
		Helper.keys.put("d3", new Key(1,0,0));
		Helper.keys.put("d5", new Key(1,2,0));
		Helper.keys.put("d6", new Key(1,2,1));
		Helper.keys.put("d7", new Key(1,2,2));
		Helper.keys.put("e3", new Key(2,0,0));
		Helper.keys.put("e4", new Key(2,1,0));
		Helper.keys.put("e5", new Key(2,2,0));
		Helper.keys.put("f2", new Key(2,0,1));
		Helper.keys.put("f4", new Key(2,1,1));
		Helper.keys.put("f6", new Key(2,2,2));
		Helper.keys.put("g1", new Key(2,0,2));
		Helper.keys.put("g4", new Key(2,1,2));
		Helper.keys.put("g7", new Key(2,2,2));
	}
	
	public static Key getKey(String coordinate){
		return keys.get(coordinate);
	}

}
