package com.alonsoruibal.chess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.alonsoruibal.chess.evaluation.CompleteEvaluatorNew;
import com.alonsoruibal.chess.log.Logger;

/**
 * @author rui
 */
public class PgnTest extends TestCase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger("PgnTest");	
	
	private final static int EVAL_SIZE = 20;
	
   public void processPgnFile(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Board board = new Board();
		int lineCount = 0;
		int gameCount = 0;
		int moveCount = 0;
		
		Correlation corr[] = new Correlation[EVAL_SIZE];
		CompleteEvaluatorNew eval[] = new CompleteEvaluatorNew[EVAL_SIZE];
		for (int i= 0; i<EVAL_SIZE; i++) {
			corr[i] = new Correlation();
			eval[i] = new CompleteEvaluatorNew();
			// Whe change one parameter in each one of the evaluators
			//eval[i].KNIGHT_M_UNITS = i;
		}

		int result = 0;
		
		try {
			String line;
			while ((line = br.readLine()) != null) {
				lineCount++;
//				logger.debug("Test = " + line);
				
				if (line.indexOf("[Event")>=0) {
					board.startPosition();
					gameCount++;
				}
				// keeps track of who won the game
				if (line.indexOf("[Result")>=0) {
					if (line.indexOf("1-0")>0) {
						result = 1;
					} else if (line.indexOf("0-1")>0) {
						result = -1;
					} else {
						result = 0;
					}
				}
				
				// Moves
				if (line.indexOf("[") == 0) {
//					logger.debug(line);
				} else {
					
					StringTokenizer st = new StringTokenizer(line, " ");
					while(st.hasMoreTokens()) {
						String el = st.nextToken().trim();

						if ("1/2-1/2".equals(el)) {
						} else if ("1-0".equals(el)) {
						} else if ("0-1".equals(el)) {
						} else {
							// Move 1.
							if (el.indexOf(".")>=0) {
								el = el.substring(el.indexOf(".") + 1);
							}
							if (el.length()>0) {
//								logger.debug("el='"+el+"'");

								int move = Move.getFromString(board, el);
								if (move == 0 || move == -1) {
									logger.error("Move not Parsed:\n"+board.toString());
									System.exit(-1);
								}
								int evalsIni[] = new int[EVAL_SIZE];
								for (int i= 0; i<EVAL_SIZE; i++) {
									evalsIni[i] = eval[i].evaluateBoard(board);
								}
								
								if (!board.doMove(move)) {
									logger.debug("move="+el + " " + Move.toStringExt(move) + " " + board.getTurn());								
									logger.debug("Position Not Valid:\n" + board.toString());

									System.exit(-1);
								}
								if (!Move.isTactical(move)) {
									for (int i= 0; i<EVAL_SIZE; i++) {
										int evalFin = eval[i].evaluateBoard(board);
										corr[i].add(result, evalFin-evalsIni[i]);
									}
								}

								moveCount ++;
							}
						}
					}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("LineCount = " + lineCount);
		logger.debug("GameCount = " + gameCount);
		logger.debug("MoveCount = " + moveCount);
		int maxIndex=-1;
		double maxValue = -999.9;
		for (int i= 0; i<corr.length; i++) {
			if (corr[i].get() > maxValue) {
				maxIndex = i;
				maxValue = corr[i].get();
			}
			logger.debug("Correlation "+i+"= " + corr[i].get());
		}
		logger.debug("Best Index = " + maxIndex);
	}
   
   public void test2600() {
	   processPgnFile(this.getClass().getResourceAsStream("/2600.pgn"));
   }
}