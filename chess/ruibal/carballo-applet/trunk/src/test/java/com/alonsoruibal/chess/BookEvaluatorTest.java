package com.alonsoruibal.chess;

import java.util.List;

import junit.framework.TestCase;

import com.alonsoruibal.chess.book.BinaryBook;
import com.alonsoruibal.chess.evaluation.CompleteEvaluatorNew;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.evaluation.SimplifiedEvaluator;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.movegen.MagicMoveGenerator;
import com.alonsoruibal.chess.movegen.MoveGenerator;

/**
 * Test the evaluator functions with the non tactical moves of the opening book
 * 
 * calculates the correlation between the evaluator score increment and the move frequency in the opening book
 * 
 * @author rui
 */
public class BookEvaluatorTest extends TestCase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger("BookEvaluatorTest");

	private static int MAX_DEPTH = 5;
	
	Evaluator evaluator;
	MoveGenerator movegen;
	BinaryBook book;
	int positions;
	
	@Override
	protected void setUp() throws Exception {
		movegen = new MagicMoveGenerator();
	}
	
	Correlation corr;
	
	public void testEvaluatorWithBook() {
		
		Board board = new Board();
		board.startPosition();
		book = new BinaryBook(new PropertyConfig());

		corr = new Correlation();
		evaluator = new CompleteEvaluatorNew();
		recursiveTest(board, 0);

		logger.debug("positions=" + positions);
		logger.info("Correlation (Complete)   =" + corr.get());

		corr = new Correlation();
		evaluator = new SimplifiedEvaluator();
		recursiveTest(board, 0);
		logger.info("Correlation (Simplified) =" + corr.get());
	}
	
	private void recursiveTest(Board board, int depth) {
		if (depth >= MAX_DEPTH) return;
		positions++;
//		logger.debug(board);
		int initialScore = evaluator.evaluateBoard(board);
		book.generateMoves(board);
		List<Integer> moves = book.getMoves();
		List<Integer> weights = book.getWeights();
		int i = 0;
		for (Integer move: moves) {
			// Do not do for tactical moves
			if (!Move.isTactical(move) && board.doMove(move)) {
//				logger.debug(board);
				int deltaScore = evaluator.evaluateBoard(board) - initialScore;
				if (board.getTurn()) deltaScore = -deltaScore;
				int x = deltaScore;
				int y = weights.get(i);
//				logger.debug("******************************** "+depth+" " + Move.toStringExt(move) + " : " + x + " <-> " + y);
				corr.add(x, y);
			 
			 	recursiveTest(board, depth + 1);
				board.undoMove();
			}
			i++;
		}
	}
}