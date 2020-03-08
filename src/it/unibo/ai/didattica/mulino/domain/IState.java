package it.unibo.ai.didattica.mulino.domain;

import it.unibo.ai.didattica.mulino.domain.State.Checker;
import it.unibo.ai.didattica.mulino.domain.State.Phase;

public interface IState {

	
	int getWhiteCheckersOnBoard();

	int getBlackCheckersOnBoard();

	int getBlackCheckers();

	int getWhiteCheckers();
	
	Phase getCurrentPhase();
	
	Checker getCurrentPlayer();
}
