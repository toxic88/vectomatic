package com.alonsoruibal.chess.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Config;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.bitboard.AttackGenerator;
import com.alonsoruibal.chess.book.Book;
import com.alonsoruibal.chess.evaluation.CompleteEvaluator;
import com.alonsoruibal.chess.evaluation.CompleteEvaluatorNew;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.evaluation.SimplifiedEvaluator;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.movesort.MoveIterator;
import com.alonsoruibal.chess.movesort.SortInfo;
import com.alonsoruibal.chess.tt.TranspositionTable;
import com.alonsoruibal.chess.tt.TwoTierTranspositionTable;

/** 
 * 
 * Negascout search engine
 * 
 * @author Alberto Alonso Ruibal
*/
public class SearchEngine implements Runnable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger("SearchEngine");
	
	public static final int MAX_DEPTH = 50;
	
	private final int MAX_EXTENSIONS = 3;
	private final int DEPTH_REMAINING_FOR_EXTENSIONS = 3;
	private final int NUMBER_OF_FIRST_MOVES_NOT_REDUCED = 4; // first x moves not reduced
	private final int REDUCTION_LIMIT = 3; // Number of depths not reduced
	
	private final int CHECK_BOOK_IF_MOVE_LESS_THAN = 20;

	protected SearchParameters searchParameters;
	
	protected boolean isSearching = false;
	private boolean foundOneMove = false; 
	
	private boolean useNegascout;
	private boolean useNullMove;
	private boolean useIID;
	private boolean useExtensions;
	private boolean useLmr;
	private boolean useFutility;
	private int futilityMargin;
	private boolean useAggressiveFutility;
	private int aggressiveFutilityMargin;
	private boolean useAspirationWindow;
	private int halfAspirationWindow;
	
	// time to think to 
	protected long thinkTo = 0;

	private Board board;
	private SearchObserver observer;
	private Evaluator evaluator;
	private Book book;
	private TranspositionTable tt;
	private SortInfo sortInfo;
	private MoveIterator moveIterators[];

	
	private long bestMoveTime; // For testing suites
	private int bestMove, ponderMove;
	private String pv;
	
	// For performance Benching
	private long positionCounter;
	// For move sorting testing
	private static long moveCounter = 0;
	private static long movesWalked = 0;

	// aspiration window
	private static long aspirationWindowProbe = 0;
	private static long aspirationWindowHit = 0;

	// Futility pruning
	private static long futilityProbe = 0;
	private static long futilityHit = 0;

	// Aggresive Futility pruning
	private static long aggressiveFutilityProbe = 0;
	private static long aggressiveFutilityHit = 0;

	// Null Move
	private static long nullMoveProbe = 0;
	private static long nullMoveHit = 0;
	
	// ttpv
	private static long ttProbe = 0;
	private static long ttPvHit = 0;
	private static long ttLBHit = 0;
	private static long ttUBHit = 0;
	
	public SearchEngine(Config config, Book book, AttackGenerator attackGenerator) {
		init(config, book, attackGenerator);
	}
	
	public void destroy() {

		board = null;
		book = null;
		observer = null;
		tt = null;
		evaluator = null;
		sortInfo = null;
		if (moveIterators != null) {
			for (int i=0; i< MAX_DEPTH; i++) {
				moveIterators[i] = null;
			}
		}
		System.gc();
	}
	
	public void init(Config config, Book book, AttackGenerator attackGenerator) {		
		logger.debug(new Date());
		// First Clears memory
		destroy();

		attackGenerator.run();

		board = new Board();
		board.startPosition();
		sortInfo = new SortInfo();
		moveIterators = new MoveIterator[MAX_DEPTH];
		for (int i=0; i< MAX_DEPTH; i++) {
			moveIterators[i] = new MoveIterator(board, sortInfo, i);
		}		

		String evaluatorName = config.getProperty("evaluator");
		logger.debug("Loading evaluator " + evaluatorName);
		if ("simplified".equals(evaluatorName)) {
			evaluator = new SimplifiedEvaluator();
		} else if ("complete".equals(evaluatorName)) {
			evaluator = new CompleteEvaluator();
		} else if ("completenew".equals(evaluatorName)) {
			evaluator = new CompleteEvaluatorNew();
		}

		this.book = book;
		
		useNegascout = config.getBoolean("search.negascout");
		useNullMove = config.getBoolean("search.nullmove");
		useIID = config.getBoolean("search.iid");
		useExtensions = config.getBoolean("search.extensions");
		useLmr = config.getBoolean("search.lmr");
		useFutility = config.getBoolean("search.futility");
		if (useFutility) {
			futilityMargin = Integer.valueOf(config.getProperty("search.futility.margin"));
		}
		useAggressiveFutility = config.getBoolean("search.aggressiveFutility");
		if (useAggressiveFutility) {
			aggressiveFutilityMargin = Integer.valueOf(config.getProperty("search.aggressiveFutility.margin"));
		}
		logger.debug(
				" NegaScout=" + useNegascout +
				" NullMove=" + useNullMove +
				" IID=" + useIID +
				" Extensions=" + useExtensions +
				" LMR=" + useLmr +
				" Futility=" + useFutility +
				" FutilityMargin=" + futilityMargin +
				" AggresiveFutility=" + useAggressiveFutility +
				" AggresiveFutilityMargin=" + aggressiveFutilityMargin);

		useAspirationWindow = config.getBoolean("aspirationWindow.size");
		if (useAspirationWindow) {
			halfAspirationWindow = Integer.valueOf(config.getProperty("aspirationWindow.size")) / 2;
			logger.debug("Using aspiration window, size=" + 2*halfAspirationWindow);
		}

//		int size = 24; // default 256 MB
//		if (config.containsProperty("transpositionTable.size")) {
//			size = BitboardUtils.square2Index(Long.valueOf(config.getProperty("transpositionTable.size"))) + 16;
//		}
		int size = 12;
		tt = new TwoTierTranspositionTable(size);
	}

	public void setObserver(SearchObserver observer) {
		this.observer = observer;
	}

	public Board getBoard() {
		return board;
	}
	
	public int getBestMove() {
		return bestMove;
	}
	
	public long getBestMoveTime() {
		return bestMoveTime;
	}
	
	/**
	 * search horizon node (depth == 0) some kind of quiescent search
	 * @return
	 * @throws TimeExceedException 
	 */
	public int quiescentSearch(byte depth, byte depthRemaining, int alpha, int beta) throws TimeExceedException {
		if (System.currentTimeMillis() > thinkTo && foundOneMove) throw new TimeExceedException();
		positionCounter++;

		int ttMove = 0;

		// checks draw by three fold repetition. and fifty moves rule
		if (board.isDraw()) return 0;
		
		// Node value without captures: we are not forced to capture	
		int score = evaluator.evaluateBoard(board);

		if (!board.getTurn()) score = (short) -score;
		score += evaluator.getRandom();
		
		// TODO Delta Pruning: predicted beta cutoff
//		if (score > beta + 300) return score; 

		// Evaluation functions increases alpha and can originate beta cutoffs
		if (score >= beta) return score;
		if (score > alpha) alpha = score;
		
		MoveIterator moveIterator = moveIterators[depth];
		moveIterator.genMoves(ttMove);
		boolean validOperations = false;

		int move = 0;
		while ((move = moveIterator.next()) != 0) {
			
			if (board.doMove(move)) {
				validOperations = true;
				if (board.getCheck() || Move.isTactical(move)) {
					score = (short)-quiescentSearch((byte)(depth+1), (byte)(depthRemaining-1), (short)(-beta), (short)(-alpha));
				}
				board.undoMove();
				if (score > alpha) {
					alpha = score;
					if (score >= beta) break;
				}
			}
		}
		
		if (!validOperations) alpha = evaluateEndgame(depth);

		return alpha;
	}
	
	/**
	 * Implements negamax inference engine 
	 */
	public int search(byte depth, byte depthRemaining, byte extensions, int alpha, int beta, boolean allowNullMove) throws TimeExceedException {
		if (System.currentTimeMillis() > thinkTo && foundOneMove) throw new TimeExceedException();
		positionCounter++;
		
		// checks draw by treefold rep. and fifty moves rule
		if (board.isDraw()) return 0;
		
		int ttMove = 0;
		int bestMove = 0;
		int bestScore = -Evaluator.VICTORY;
		int score = 0;
		
		ttProbe++;
		if (tt.search(board)) {
			if (tt.getDepthAnalyzed() >= depthRemaining && tt.isMyGeneration()) {
				switch(tt.getNodeType()) {
					case TranspositionTable.TYPE_EXACT_SCORE: ttPvHit++; return tt.getScore();
					case TranspositionTable.TYPE_FAIL_LOW: ttLBHit++; if (tt.getScore() <= alpha) return alpha;
					case TranspositionTable.TYPE_FAIL_HIGH: ttUBHit++; if (tt.getScore() >= beta) return beta;
				}
			}
			ttMove = tt.getBestMove();
		}
		
		if (depthRemaining==0) {
			score = quiescentSearch((byte)(depth +1), (byte)0, alpha, beta);
			tt.save(board, depthRemaining, bestMove, score, alpha, beta);
			return score;
		}
		
		boolean mateThreat = false;
		// Null move pruning and mat threat detection
		// Don't do null move in king and pawn endings
		if (useNullMove && allowNullMove && !board.getCheck() && depth>1 && depthRemaining>3 &&
			(board.getMines() & (board.knights | board.bishops | board.rooks | board.queens)) != 0) {
			nullMoveProbe++;
			board.doMove(0);
			score = -zwsearch((byte) (depth + 1), (byte) (depthRemaining - (depthRemaining > 6 ? 4 : 3)), -beta+1);
			board.undoMove();
			if (score>=beta) {
				nullMoveHit++;
				return score;
			}
			// Detect mate threat to trigger extensions
			if (score < (-Evaluator.VICTORY + 100)) mateThreat = true;
		}

		// Internal Iterative Deepening
		if (useIID && ttMove == 0 && depth>=3 && depthRemaining >= 2) {
			search((byte)(depth + 1), (byte) (depthRemaining/2), extensions, alpha, beta, false);
			if (tt.search(board)) {
				ttMove = tt.getBestMove();
//				if (ttMove == 0) logger.error("IID Move="+Move.toString(ttMove)+ " nodeType=" +tt.getNodeType()+ " depthRemaining="+depthRemaining + " depthAnalyzed=" + tt.getDepthAnalyzed());
			} else logger.error("IID without Move");
		}

		boolean futilityPrune = false;
		
		// Futility pruning
		// to not evaluate two times
		Integer value = null;
		if (useFutility && depthRemaining==1) { // at frontier nodes
			futilityProbe++;
			value = evaluator.evaluateBoard(board);
			if (value < alpha - futilityMargin) {
				futilityHit++;
				futilityPrune = true;
			}
		}
		// Aggressive futility pruning
		if (useAggressiveFutility && depthRemaining==2) { // at pre-frontier nodes
			aggressiveFutilityProbe++;
			if (value == null) value = evaluator.evaluateBoard(board);
			if (value < alpha - aggressiveFutilityMargin) {
				aggressiveFutilityHit++;
				futilityPrune = true;
			}
		}	
		
		int movesDone = 0;
		MoveIterator moveIterator = moveIterators[depth];
		moveIterator.genMoves(ttMove);
		boolean validOperations = false;
		boolean searchNullWindow = false; // initially we don't search null window
		
		int move = 0;
		while ((move = moveIterator.next()) != 0) {

			// Operations are pseudo-legal, doMove checks if they lead to a valid state 
			if (board.doMove(move)) {
				validOperations = true;
				movesDone++;
				
				byte nextDepthRemaining = (byte) (depthRemaining - 1);
				byte nextExtensions = extensions;

				if (useExtensions && extensions<=MAX_EXTENSIONS
					&& (board.getCheck() || Move.isPawnPush(move) || mateThreat)
					&& (nextDepthRemaining <= DEPTH_REMAINING_FOR_EXTENSIONS)) {
					nextDepthRemaining++;
					nextExtensions++;
				}
				
				// if not extended and not check and move not tactical and no castling
				if (!board.getCheck() && !Move.isTactical(move) && !Move.isPawnPush(move) && !Move.isCastling(move) && !mateThreat) { 
					if (futilityPrune) {
						board.undoMove();
						continue;
					}						

					// Late move reductions (LMR)
					if (useLmr
						&& depth > REDUCTION_LIMIT && nextDepthRemaining > 0 
						&& movesDone > NUMBER_OF_FIRST_MOVES_NOT_REDUCED) {
						nextDepthRemaining--;
					}					
				}

				int lowBound = (alpha > bestScore ? alpha : bestScore);
				
				boolean nullWindowSearched = false; // flag indicating if searched null window
				if (useNegascout && searchNullWindow && nextDepthRemaining > 2) {
					// search null window to discard move
					score = -zwsearch((byte) (depth + 1), nextDepthRemaining, -lowBound);
					nullWindowSearched = true;
				}
				
				// If falls over alpha, research the original window to get the exact score
				if (!useNegascout || !nullWindowSearched || score > lowBound) {
					score = -search((byte)(depth + 1), nextDepthRemaining, nextExtensions, -beta, -lowBound, true);
				}
				board.undoMove();

				// Tracks the best move
				if (score > bestScore) {
					bestMove = move;
					bestScore = score;
				}
				
				// alpha/beta cut (fail high)
				if (score >= beta) break;
				searchNullWindow = true; // next, searches null window
			}
		}

		// Checkmate or stalemate
		if (!validOperations) bestScore = evaluateEndgame(depth);

		// Tells MoveSorter the move score
		if (bestScore >= beta) sortInfo.betaCutoff(board, bestMove, depth);

		// Save in the transposition Table (allways replace)
		tt.save(board, depthRemaining, bestMove, bestScore, alpha, beta);
		
		return bestScore;
	}

	/**
	 * Zero window search, return beta-1 or beta
	 */
	public int zwsearch(byte depth, byte depthRemaining, int beta) throws TimeExceedException {
		if (System.currentTimeMillis() > thinkTo && foundOneMove) throw new TimeExceedException();
		positionCounter++;

		// checks draw by treefold rep. and fifty moves rule
		if (board.isDraw()) return 0;
				
		int ttMove = 0;
		int bestMove = 0;
		int bestScore = -Evaluator.VICTORY;
		int score = 0;

		ttProbe++;
		if (tt.search(board)) {
			if (tt.getDepthAnalyzed() >= depthRemaining && tt.isMyGeneration()) {
				switch(tt.getNodeType()) {
					case TranspositionTable.TYPE_EXACT_SCORE: ttPvHit++; return tt.getScore();
					case TranspositionTable.TYPE_FAIL_LOW: ttLBHit++; if (tt.getScore() <= beta-1) return beta-1;
					case TranspositionTable.TYPE_FAIL_HIGH: ttUBHit++; if (tt.getScore() >= beta) return beta;
				}
			}
			ttMove = tt.getBestMove();
		}
		
		if (depthRemaining==0) {
			score = quiescentSearch((byte)(depth +1), (byte)0, beta-1, beta);
			tt.save(board, depthRemaining, bestMove, score, beta-1, beta);
			return score;
		}
		
		MoveIterator moveIterator = moveIterators[depth];
		moveIterator.genMoves(ttMove);
		boolean validOperations = false;
		
		int move = 0;
		while ((move = moveIterator.next()) != 0) {

			// Operations are pseudo-legal, doMove checks if they lead to a valid state 
			if (board.doMove(move)) {
				validOperations = true;
								
				// search null window to discard move
				score = -zwsearch((byte) (depth + 1), (byte) (depthRemaining - 1), 1-beta);
				board.undoMove();

				// Tracks the best move
				if (score > bestScore) {
					bestMove = move;
					bestScore = score;
				}

				// Tries to maximize alpha
				if (score >= beta) break;
			}
		}

		// Check, checkmate or stalemate
		if (!validOperations) bestScore = evaluateEndgame(depth);

		tt.save(board, depthRemaining, bestMove, bestScore, beta-1, beta);
		
		// Tells MoveSorter the move score
		if (bestScore>= beta) {
			sortInfo.betaCutoff(board, bestMove, depth);
			return beta;
		}
		
		return beta-1;
	}

	
	/**
	 * looks for the best movement and move
	*/
	public void go(int timeToThinkMs) {
		searchParameters = new SearchParameters();
		searchParameters.setMoveTime(timeToThinkMs);
		run();
	}
	
	
	/**
	 *  
	 */
	private void searchStats() {
		logger.debug("Asp Win      Hits=" + (100.0 * aspirationWindowHit / aspirationWindowProbe) + "%");
		logger.debug("TT PV        Hits=" + (100.0 * ttPvHit / ttProbe) + "%");
		logger.debug("TT LB        Hits=" + (100.0 * ttLBHit / ttProbe) + "%");
		logger.debug("TT UB        Hits=" + (100.0 * ttUBHit / ttProbe) + "%");
		logger.debug("Futility     Hits=" + (100.0 * futilityHit / futilityProbe) + "%");
		logger.debug("Agg.Futility Hits=" + (100.0 * aggressiveFutilityHit / aggressiveFutilityProbe) + "%");
		logger.debug("Null Move    Hits=" + (100.0 * nullMoveHit / nullMoveProbe) + "%");
		logger.debug("Short inefficiency (moves walked over total, less is better)=" + (100.0 * movesWalked / (moveCounter + 1)) + "%");
	}

	public void run() {
		foundOneMove = false;
		isSearching = true;
		int lowerBound;
		int upperBound;

		
		long startTime = System.currentTimeMillis();
		logger.debug("Board\n" + board);
		
		positionCounter = 0;
		bestMoveTime = 0;
		bestMove = 0;
		ponderMove = 0;
		pv = null;
		
		thinkTo = startTime + searchParameters.calculateMoveTime(board) - 100;
		int score = 0;
		
		if ((book != null) && board.getMoveNumber() < CHECK_BOOK_IF_MOVE_LESS_THAN) {
			int bookMove = book.getMove(board);
			if (bookMove != 0) {
				bestMove = bookMove;
			}
		}
		int savedMoveNumber = board.moveNumber;
		if (bestMove == 0) {
		int lastScore = evaluator.evaluateBoard(board);

		try {
			for (byte depth = 1; depth < MAX_DEPTH; depth += 1) {
				tt.newGeneration();
				
				// Aspiration window 
				// If the score is not in the window (if if the limit is out, so >=) we must research at the same depth
				if (useAspirationWindow) {
					lowerBound = (short) (lastScore - halfAspirationWindow);
					upperBound = (short) (lastScore + halfAspirationWindow);
				} else {
					lowerBound = -Evaluator.VICTORY;
					upperBound = Evaluator.VICTORY;	
				}
					
				while (true) {
					aspirationWindowProbe++;	
					score = search((byte) 0, depth, (byte)0, lowerBound, upperBound, true);
						
					// Open only one side of the aspiration window
					if (score <= lowerBound) {
						lowerBound = -Evaluator.VICTORY;
					} else if (score >= upperBound) {
						upperBound = Evaluator.VICTORY;							
					} else {
						aspirationWindowHit++;
						break;
					}
				}
				
				long time = System.currentTimeMillis();
				long oldBestMove = bestMove;
				getPv();
				if (bestMove != 0) foundOneMove = true;
				
				// update best move time
				if (oldBestMove != bestMove) bestMoveTime = time - startTime;
				SearchStatusInfo info = new SearchStatusInfo();
				info.setDepth(depth);
				info.setTime(time-startTime);
				info.setPv(pv);
				info.setScoreCp(score);
				info.setNodes(positionCounter);
				info.setNps((int) (1000 * positionCounter / ((time-startTime+1))));
				logger.debug(info.toString());
				if (observer != null) observer.info(info);
				
				// if mate found exit
				if ((score < -Evaluator.VICTORY + 1000) || (score > Evaluator.VICTORY - 1000)) break;
			}
		} catch(TimeExceedException e) {
			// puts the board in the initial position
			logger.debug("Time exceed: returning to move " + savedMoveNumber);
		}
		board.undoMove(savedMoveNumber);
		searchStats();
		}
		isSearching = false;
		if (observer != null) observer.bestMove(bestMove, ponderMove);
	}
	
	/**
	 * Gets the principal variation and the best move from the Pv transposition table
	 * 
	 * @return
	 */
	private void getPv() {
		StringBuffer sb = new StringBuffer();
		List<Long> keys = new ArrayList<Long>(); // To not repeat keys
		int i = 0;
		while (i < 256) {
			if (tt.search(board)) {
				if (keys.contains(board.getKey())) break;
				keys.add(board.getKey());
				if (tt.getBestMove() == 0) break;
				if (i == 0) bestMove = tt.getBestMove(); 
				if (i == 1) ponderMove = tt.getBestMove();
				sb.append(Move.toString(tt.getBestMove()));
				sb.append(" ");
				i++;
				board.doMove(tt.getBestMove());
			} else break;
		}
		// Now undo moves
		for (int j=0; j<i; j++) board.undoMove();
		pv = sb.toString();
	}
	
	
	/**
	 * Is better to end before
	 * Not necessary to change sign after
	 */
	public int evaluateEndgame(int depth) {
		if (board.getCheck()) {
			return (-Evaluator.VICTORY + depth);
		} else return 0;
	}
}