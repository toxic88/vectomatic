package com.alonsoruibal.chess.evaluation;

import com.alonsoruibal.chess.Board;

public abstract class Evaluator {

	public final static int VICTORY    = Short.MAX_VALUE - 1;
//	private final static Random random = new Random(System.currentTimeMillis());

	
	/**
	 * Board evaluator
	 */
	public abstract int evaluateBoard(Board board);


	/**
	 * Is better to end before
	 */
	public int getRandom() {
		return 0;
//		return ((random.nextInt() & 0xf)) - 8;
	}

}