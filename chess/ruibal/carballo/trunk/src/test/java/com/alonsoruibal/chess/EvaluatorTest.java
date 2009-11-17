package com.alonsoruibal.chess;

import junit.framework.TestCase;

import com.alonsoruibal.chess.evaluation.CompleteEvaluator;
import com.alonsoruibal.chess.evaluation.CompleteEvaluatorNew;
import com.alonsoruibal.chess.evaluation.Evaluator;

/**
 * @author rui
 */
public class EvaluatorTest extends TestCase {

	Evaluator evaluator;

	@Override
	protected void setUp() throws Exception {
		evaluator = new CompleteEvaluatorNew();
	}
	
	public void testEvaluatorSimmetry() {
		String fen = "r2q1rk1/ppp2ppp/2n2n2/1B1pp1B1/1b1PP1b1/2N2N2/PPP2PPP/R2Q1RK1 w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		System.out.print(board);
		int value = evaluator.evaluateBoard(board);
		System.out.println("value = " + value);
		assertEquals(CompleteEvaluator.TEMPO, value);
	}

	public void testPassedPawn() {
		String fen = "7k/7p/P7/8/8/6p1/7P/7K w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		System.out.print(board);
		int value = evaluator.evaluateBoard(board);
		System.out.println("value = " + value);
		assertTrue(value>0);
	}

	public void testKingSafety() {
		String fen = "r6k/1R6/8/8/8/8/8/7K w QKqk - 0 0";
		Board board = new Board();
		board.setFen(fen);
		System.out.print(board);
		int value = evaluator.evaluateBoard(board);
		System.out.println("value = " + value);
		assertTrue(value>0);
	}
	
}