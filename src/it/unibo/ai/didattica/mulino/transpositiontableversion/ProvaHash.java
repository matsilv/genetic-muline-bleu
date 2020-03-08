package it.unibo.ai.didattica.mulino.transpositiontableversion;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.searchutility.StopSearch;
import it.unibo.ai.didattica.mulino.searchutility.SuccessorFunction;

public class ProvaHash {

	public static void main(String[] args) {
		Negascout neg = new Negascout(new SuccessorFunction(), Checker.WHITE, Checker.BLACK, new StopSearch());
		neg.doSearch(new State(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	}

}
