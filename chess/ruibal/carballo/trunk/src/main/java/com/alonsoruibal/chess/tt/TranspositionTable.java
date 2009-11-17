package com.alonsoruibal.chess.tt;

import com.alonsoruibal.chess.Board;

public abstract class TranspositionTable {

	public final static int TYPE_EXACT_SCORE = 0;
	public final static int TYPE_FAIL_LOW = 1;
	public final static int TYPE_FAIL_HIGH = 2;
	
	/**
	 * Returns true if key matches with key stored
	 * @param key
	 * @return
	 */
	public abstract boolean search(Board board);

	public abstract int getBestMove();

	public abstract int getNodeType();

	public abstract byte getGeneration();

	public abstract boolean isMyGeneration();
	
	public abstract byte getDepthAnalyzed();

	public abstract int getScore();

	public void save(Board board, byte depthAnalyzed, int bestMove, int score, int lowerBound, int upperBound) {
		if (score <= lowerBound) {
			set(board, TranspositionTable.TYPE_FAIL_LOW, bestMove, lowerBound, depthAnalyzed);
		} else if (score >= upperBound) {
			set(board, TranspositionTable.TYPE_FAIL_HIGH, bestMove, upperBound, depthAnalyzed);
		} else { 	
			set(board, TranspositionTable.TYPE_EXACT_SCORE, bestMove, score, depthAnalyzed);
		}
	}

	public abstract void set(Board board, int nodeType, int bestMove,
			int score, byte depthAnalyzed);

	// called at the start of each search
	public abstract void newGeneration();

}