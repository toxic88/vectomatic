package com.alonsoruibal.chess.search;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.book.Book;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.movegen.MoveGenerator;
import com.alonsoruibal.chess.tt.NodeType;
import com.alonsoruibal.chess.tt.TT;
import com.alonsoruibal.chess.tt.TranspositionTableInfo;

/** 
 * TODO: use a transposition table to store the principal variant.
 * 
 * 
 * Negasocut search engine
 * 
 *  From Wikipedia:
 * 
 *  function negascout(node, depth, α, β)
 *   if node is a terminal node or depth = 0
 *       return the heuristic value of node
 *   b := β                                          (* initial window is (-β, -α) *)
 *   foreach child of node
 *       a := -negascout (child, depth-1, -b, -α)
 *       if a>α
 *           α := a
 *       if α≥β
 *           return α                                (* Beta cut-off *)
 *       if α≥b                                      (* check if null-window failed high*)
 *          α := -negascout(child, depth-1, -β, -α)  (* full re-search *)
 *          if α≥β
 *              return α                             (* Beta cut-off *)    
 *       b := α+1                                    (* set new null window *)             
 *   return α
 *
 * 
 * 
 * @author Alberto Alonso Ruibal
*/
public class SearchEngine implements Runnable {
	// UCI parameters
	// Remaining time
	int wtime, btime;
	// Time increment per move
	int winc, binc;
	// Moves to the next time control
	int movesToGo;
	// Analize x plyes only
	int depth;
	// Search only this number of nodes
	int nodes;
	// seatch for mate in mate moves
	int mate;
	// search movetime seconds
	int moveTime;
	// think infinite
	boolean infinite;
	
	private final int MAX_DEPTH = 50;
//	private final int MAX_EXTENSIONS = 5; 
//	
//	private final int FULL_DEPTH_MOVES = 4; // first x moves not reduced
//	private final int REDUCTION_LIMIT = 3; // Number of depths not reduced

	
	private boolean useBook = true;
	boolean useAspirationWindow = false;
	int halfAspirationWindow = 10;
	
	// time to think to 
	private long thinkTo = 0;

	private Board board;
	private SearchObserver observer;
	private MoveGenerator moveGenerator;
	private MoveSorter moveSorter;
	private Evaluator evaluator;
//	private TranspositionTable tt;
	private TT tt;
	
	private long bestMoveTime; // For testing suites
	private int bestMove, ponderMove;
	private String pv;
	private int moves[] = new int[8092]; // Common buffer to generate moves
	
	// For performance Benching
	private long positionCounter;
	// For move sorting testing
	private static long moveCounter = 0;
	private static long movesWalked = 0;
	// scout performance
	private static long scouts = 0;
	private static long scoutsFailed = 0;
	
	public SearchEngine(MoveGenerator moveGenerator, Evaluator evaluator) {
		this.moveGenerator = moveGenerator;
		this.evaluator = evaluator;
		this.moveSorter = new MoveSorter();
//		this.tt = new ArrayTranspositionTable(25); // 35 =>7ply en 33  20=>27  16 =>34 (BasicELOTest)
		this.tt = new TT(23);
		board = new Board();
		board.startPosition();
	}
	
	public SearchObserver getObserver() {
		return observer;
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

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}
	
	/**
	 * Implements negamax inference engine 
	 */
	public int search(int depth, int depthRemaining, int extensions,  int alpha, int beta, int indexFrom) throws TimeExceedException {
		// Pseudo-Legal Moves

		int initialAlpha = alpha;
		if (System.currentTimeMillis() > thinkTo) throw new TimeExceedException();
		
		// Transposition table
//		TranspositionTableInfo info = tt.get(board.getKey());
//		if ((info != null) && (info.getDepthAnalyzed() >= depthRemaining)) {
//			switch(info.getNodeType()) {
//				case EXACT_SCORE: return info.getScore();
//				case FAIL_LOW: if (info.getScore()<=alpha) return info.getScore();
//				case FAIL_HIGH: if (info.getScore()>=beta) return info.getScore();
//			}
//		}
		
//		int ttBestMove = -1;
		
		if ((tt.setIndex(board.getKey())) && (tt.getDepthAnalyzed() >= depthRemaining)) {
			switch(tt.getNodeType()) {
				case EXACT_SCORE: return tt.getScore();
				case FAIL_LOW: if (tt.getScore()<=alpha) return tt.getScore();
				case FAIL_HIGH: if (tt.getScore()>=beta) return tt.getScore();
			}
		}

		int indexTo = moveGenerator.generateMoves(board, moves, indexFrom);
		if (depthRemaining>0) moveCounter += indexTo - indexFrom;
		// sort moves
		moveSorter.sortMoves(moves, indexFrom, indexTo, depth, tt.getBestMove());

		int nodeBestMove = -1;
		int movesDone = 0;
		boolean validOperations = false;
//		int b = beta; // First time we search the full window
		boolean leafNode = true;
		boolean betaCutoff = false;

		// Moves are sorted the worse first
		for (int i=indexTo-1; i>=indexFrom; i--) {
			if (depthRemaining>0) movesWalked ++;

			// Operations are pseudo-legal, we must check if they lead to a valid state 
			if (board.doMove(moves[i])) {

				positionCounter++;
				validOperations = true;
				
				if (depthRemaining > 0) {
//					System.out.println("depth="+depth+" move="+ Move.toString(moves[i]));
					movesDone++;
					leafNode = false;
					int nextDepth = depthRemaining - 1;
					int nextExtensions = extensions;

					// TREE EXTENSION / PRUNING
//					if (!board.isQuiescent() || !Move.isQuiescent(moves[i])) {
//						if ((nextDepth <= 1) && (extensions<=MAX_EXTENSIONS)) {
//							nextDepth++;
//							nextExtensions++;
//						}
//					}
//					else { // Late move reductions
//						if (depth > REDUCTION_LIMIT  && movesDone > FULL_DEPTH_MOVES) {
//							nextDepth--;
//						}
//					}
					
					// search null window
//					int a = -search(depth + 1, nextDepth, nextExtensions, -b, -alpha);
					int a = -search(depth + 1, nextDepth, nextExtensions, -beta, -alpha, indexTo);
					
//					System.out.println("value="+ a);

					scouts++;
					
					// Tries to maximize alpha
					if (a > alpha) {
						alpha = a;
						nodeBestMove = moves[i];
					}
					
					// alfa/beta cut (fail high)
					if (alpha >= beta) {
						betaCutoff = true;
					} 
//					else if (alpha >= b) { // Search the original window
//						alpha = -search(depth + 1, nextDepth, nextExtensions, -beta, -alpha);
//						scoutsFailed++;
//						nodeBestMove = moves[i];
//						// alfa/beta cut (fail high)
//						if (alpha >= beta) betaCutoff = true;
//					}

					// Tells MoveSorter the move score
					if (betaCutoff) moveSorter.moveScore(board, moves[i], depth, betaCutoff);
					
					// Keeps track of the best move
//					if (alpha>= initialAlpha) {
//						
//						System.out.println("new bestmove " + Move.toString(nodeBestMove));
//					}
					
//					b = alpha + 1;
				}
				board.undoMove();
				if (betaCutoff) break;
			}
		}

		// Check, checkmate or draw
		if (!validOperations) {
			alpha = evaluateEndgame(board);
			if (!board.getTurn()) alpha = -alpha;
		} else if (leafNode) { // If leaf node then eval
			// Check fifty move rule TODO check in the board
			if (board.fiftyMovesRule >= 50) {
				alpha = 0;
			} else {
				// TODO draw by threefold repetition	
				alpha = evaluator.evaluateBoard(board) + evaluator.getRandom();
				if (!board.getTurn()) alpha = -alpha;
//				System.out.println("eval=" + alpha);
			}
		}
		// Save in the transposition Table
//		if (depthRemaining > 0) {
//			info = new TranspositionTableInfo();
//			info.setBestMove(nodeBestMove); // policy is replace always
//			if (alpha <= initialAlpha) {
//				info.setNodeType(NodeType.FAIL_LOW);
//				info.setScore(initialAlpha);
//			} else if (alpha >= beta) {
//				info.setNodeType(NodeType.FAIL_HIGH);
//				info.setScore(beta);
//			} else {
//				info.setNodeType(NodeType.EXACT_SCORE);
//				info.setScore(alpha);
//			}
//			info.setDepthAnalyzed(depthRemaining);
//			tt.put(board.getKey(), info);
			
		if (alpha <= initialAlpha) {
			tt.set(board.getKey(), NodeType.FAIL_LOW, nodeBestMove, initialAlpha, depthRemaining);
		} else if (alpha >= beta) {
			tt.set(board.getKey(), NodeType.FAIL_HIGH, nodeBestMove, beta, depthRemaining);
		} else {
			tt.set(board.getKey(), NodeType.EXACT_SCORE, nodeBestMove, alpha, depthRemaining);
		}
		
//		}
		
//		if (alpha > initialAlpha && alpha < beta) {
//			System.out.println("depth="+ depth + " key=" + board.getKey() +
//					" move=" + Move.toString(nodeBestMove) + " " + board.moveNumber +
//					" alpha=" + alpha);
//			System.out.println(board);
//		}
		
		return alpha;
	}
	
	/**
	 * looks for the best movement and move
	*/
	public void go(Board board, int timeToThinkMs) {
		this.board = board;
		moveTime = timeToThinkMs;
		run();
	}
	
	/**
	 * Threaded version
	 */
	public void go() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private void searchInfo() {
		System.out.println("Short efficiency=" + (100.0 * (moveCounter - movesWalked) / (moveCounter + 1)) + "%");
		System.out.println("Scout efficiency=" + (100.0 * scouts / (scouts + scoutsFailed)) + "%");
	}

	public void run() {
		positionCounter = 0;
		bestMoveTime = -1;
		bestMove = -1;
		ponderMove = -1;
		pv = null;
		long startTime = System.currentTimeMillis();
		thinkTo = startTime + moveTime - 100;
		int score = 0;
		
		if (useBook) {
			int bookMove = Book.getMove(board.getClass().getResourceAsStream("/book_small.bin"), board);
			if (bookMove != -1) {
				bestMove = bookMove;
			}
		}
		int savedMoveNumber = board.moveNumber;
		if (bestMove == -1) {
		try {
			for (int depth = 1; depth < MAX_DEPTH; depth += 1) {

				// Aspiration window: better advancing 2ply each time, if not, lots of variations and always researching
				int halfAspirationWindow = 50;
				if (useAspirationWindow) {
					int lastScore = evaluator.evaluateBoard(board);
					score = search(0, depth, 0, lastScore - halfAspirationWindow, lastScore + halfAspirationWindow, 0);
					// If the score is not in the window (if if the limit is out, so >=) we must research at the same depth
					boolean research = (score <= (lastScore - halfAspirationWindow)) ||
						(score >= (lastScore + halfAspirationWindow)); //|| movesFound.get(0).size() == 0; ????
					System.out.println("score = " + score + " lastScore = " + lastScore + " re-search = "+ research);
					if (research) {
						score = search(0, depth, 0, -Evaluator.VICTORY, Evaluator.VICTORY, 0);						
					}
					lastScore = score;
				} else {
					score = search(0, depth, 0, -Evaluator.VICTORY, Evaluator.VICTORY, 0);					
				}
				
				long time = System.currentTimeMillis();
				long oldBestMove = bestMove;
				getPv();		
				// update best move time
				if (oldBestMove != bestMove) bestMoveTime = time - startTime;
					
				System.out.println(depth + "ply -> " + pv  + "/ score=" + score + " / " + 
					positionCounter + " nodes in " + (time-startTime) + "ms: " + 1000 * positionCounter / ((time-startTime+1)) + "Nps");
	
				// if mate found exit
				if ((score < -Evaluator.VICTORY + 1000) || (score > Evaluator.VICTORY - 1000)) break;
			}
		} catch(TimeExceedException e) {
		}
		board.undoMove(savedMoveNumber);
		searchInfo();
		}
		if (observer != null) observer.bestMove(bestMove, ponderMove);
	}
	
	/**
	 * Gets the principal variation and the best move from the transposition table
	 * 
	 * @return
	 */
//	private void getPv() {
//		StringBuffer sb = new StringBuffer();
//		int i = 0;
//		while (i < 256) {
//			TranspositionTableInfo info = tt.get(board.getKey());
//			if (info != null) {
////				System.out.println(i + " key=" + board.getKey() + " move=" + Move.toString(info.getBestMove()));
//				if (info.getBestMove() == -1) break;
//				if (i == 0) bestMove = info.getBestMove(); 
//				if (i == 1) ponderMove = info.getBestMove();
//				sb.append(Move.toString(info.getBestMove()));
//				sb.append(" ");
//				i++;
//				board.doMove(info.getBestMove());
//			} else break;
//		}
////		System.out.println(board.toString());
//		// Now undo moves
//		for (int j=0; j<i; j++) board.undoMove();
//
//		pv = sb.toString();
//	}
	
	private void getPv() {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < 256) {
			if (tt.setIndex(board.getKey())) {
				if (tt.getBestMove() == -1) break;
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
	 */
	public int evaluateEndgame(Board board) {
		if (board.isCheck()) {
			return (board.getTurn() ? -Evaluator.VICTORY + board.moveNumber : Evaluator.VICTORY - board.moveNumber);
		} else return 0;
	}
}