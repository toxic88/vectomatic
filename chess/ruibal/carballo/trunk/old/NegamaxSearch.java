package com.alonsoruibal.chess.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.book.Book;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.movegen.MoveGenerator;
import com.alonsoruibal.chess.tt.ArrayTranspositionTable;
import com.alonsoruibal.chess.tt.NodeType;
import com.alonsoruibal.chess.tt.TranspositionTable;
import com.alonsoruibal.chess.tt.TranspositionTableInfo;

/** 
 * TODO: in quiescent search we are forced to capture, this leads to erroneous results
 * TODO: "null" move appears as the last move
 * 
 * @author Alberto Alonso Ruibal
*/
public class NegamaxSearch {
	// UCI parameters
	// Remaining time
	int wtime, btime;
	// Time increment per move
	int winc, binc;
	// Moves to the next time control
	int movestogo;
	// Analize x plyes only
	int depth;
	// Search only this number of nodes
	int nodes;
	// seatch for mate in mate moves
	int mate;
	// search movetime seconds
	int movetime;
	// think infinite
	boolean infinite;
	
	private final int MAX_DEPTH = 50;
	private final int MAX_EXTENSIONS = 5;
	private final int PRUNING_DEPTH = 2;
	
	private boolean useBook = true;
	boolean useAspirationWindow = false;
	int halfAspirationWindow = 10;
	
	// time to think to 
	private long thinkTo = 0;

	
	private MoveGenerator moveGenerator;
	private MoveSorter moveSorter;
	private Evaluator evaluator;
	private TranspositionTable tt;
	private SearchObserver observer; 
	
	private HashMap<Integer, List<Move>> movesFound; // Best list of operations at movenumber x
	private long bestMoveTime; // For testing suites
	private Move bestMove;
	private List<Move> bestMoves;
	
	// For performance Benching
	private long positionCounter;
	// For move sorting testing
	private long moveCounter;
	private long movesWalked;

	
	public NegamaxSearch(MoveGenerator moveGenerator, Evaluator evaluator) {
		this.moveGenerator = moveGenerator;
		this.evaluator = evaluator;
		this.moveSorter = new MoveSorter();
		this.tt = new ArrayTranspositionTable(20); // 35 =>7ply en 33  20=>27  16 =>34 (BasicELOTest)
		this.observer = new SearchObserver();
		movesFound = new HashMap<Integer,List<Move>>(); // TODO: must be better
		for (int depth=0; depth < MAX_DEPTH; depth++) movesFound.put(depth, new ArrayList<Move>());
	}
	
	public List<Move> getBestMoves() {
		return bestMoves;
	}
	
	public Move getBestMove() {
		return bestMove;
	}
	
	public long getBestMoveTime() {
		return bestMoveTime;
	}

	/**
	 * Implements negamax inference engine 
	 */
	public int search(Board board, int depth, int depthRemaining, int extensions,  int alpha, int beta) throws TimeExceedException {
		int initialAlpha = alpha;
		if (System.currentTimeMillis() > thinkTo) throw new TimeExceedException();

		// Clear best Moves at this level
		movesFound.get(depth).clear();
		
		// Transposition table
		TranspositionTableInfo info = tt.get(board);
		if ((info != null) && (info.getDepthAnalyzed() >= depthRemaining)) {
			boolean exit = false;
			switch(info.getNodeType()) {
				case EXACT_SCORE: exit = true; break;
				case FAIL_LOW: if (info.getScore()<=alpha) exit = true; break;
				case FAIL_HIGH: if (info.getScore()>=beta) exit = true; break;
			}
			if (exit) {
				movesFound.get(depth).add(info.getBestMove());
				return info.getScore();
			}
		}
		
		// Pseudo-Legal Moves
		List<Move> moves = moveGenerator.generateMoves(board);
		if (depthRemaining>0) moveCounter += moves.size();
		// sort moves
		moveSorter.sortMoves(board, moves, depth, (info != null ? info.getBestMove(): null));

		Move bestMove = null;
		boolean validOperations = false;
		boolean leafNode = true;
		for (Move move : moves) {
			if (depthRemaining>0) movesWalked ++;
			Board newBoard = board.clone();
			newBoard.doMove(move);
			// Operations are pseudo-legal, we must check if they lead to a valid state 
			if (newBoard.isValid()) {
				positionCounter++;
				validOperations = true;
				if (depthRemaining > 0) {
					leafNode = false;
					int nextDepth = depthRemaining - 1;
					int nextExtensions = extensions;

//					// TREE EXTENSION
//					if ((nextDepth == 0) && (extensions<=MAX_EXTENSIONS)  && (!board.isQuiescent() || !move.isQuiescent())) {
//						nextDepth++;
//						nextExtensions++;
//					}
//					// PRUNING
//					if (nextDepth > PRUNING_DEPTH && board.isQuiescent() && move.isQuiescent()) {
//						nextDepth--;
//					}
					
					int aux = -search(newBoard, depth + 1, nextDepth, nextExtensions, -beta, -alpha);
					
					// Tries to maximize alpha
					if (aux > alpha) {
						alpha = aux;
						bestMove = move;
						// Clear best Moves at this level
						movesFound.get(depth).clear();
						movesFound.get(depth).add(move);
						movesFound.get(depth).addAll(movesFound.get(depth+1));
					}
					
					// Tells MoveSorter the move score
					moveSorter.moveScore(board, move, depth, aux, (alpha >= beta));
					
					// alfa/beta cut (fail high)
					if (alpha >= beta) break; 
				}
			}
		}
		
		if (alpha < beta) { // is this is not a fail high
			// Check, checkmate or draw
			if (!validOperations) {
				alpha = evaluator.evaluateEndgame(board);
				if (!board.getTurn()) alpha = -alpha;
			} else if (leafNode) { // If leaf node then eval
				// Check fifty move rule
				if (board.fiftyMovesRule >= 50) {
					alpha = 0;
				} else {
					// TODO draw by threefold repetition	
					alpha = evaluator.evaluateBoard(board) + evaluator.getRandom();
				}
				if (!board.getTurn()) alpha = -alpha;
			}
		}
		
		// Save in the transposition Table
		info = new TranspositionTableInfo();
		info.setBestMove(bestMove);
		if (alpha <= initialAlpha) {
			info.setNodeType(NodeType.FAIL_LOW);
			info.setScore(initialAlpha);
		} else if (alpha >= beta) {
			info.setNodeType(NodeType.FAIL_HIGH);
			info.setScore(beta);
		} else {
			info.setNodeType(NodeType.EXACT_SCORE);
			info.setScore(alpha);
		}
		info.setDepthAnalyzed(depthRemaining);
		tt.put(board, info);
		
		return alpha;
	}
	
	/**
	 * looks for the best movement and move
	*/
	public void go(Board board, long timeToThinkMs) {
		positionCounter = 0;
		moveCounter = 0;
		movesWalked = 0;
		bestMoveTime = -1;
		bestMove = null;
		long startTime = System.currentTimeMillis();
		thinkTo = startTime + timeToThinkMs - 100;
		int score = 0;
		
		if (useBook) {
			Move bookMove = Book.getMove(board.getClass().getResourceAsStream("/book_small.bin"), board);
			if (bookMove != null) {
				board.doMove(bookMove);
				return;
			}
		}
		
		try {
			for (int depth = 1; depth < MAX_DEPTH; depth += 1) {

				// Aspiration window: better advancing 2ply each time, if not, lots of variations and always researching
				int halfAspirationWindow = 50;
				if (useAspirationWindow) {
					int lastScore = evaluator.evaluateBoard(board);
					score = search(board, 0, depth, 0, lastScore - halfAspirationWindow, lastScore + halfAspirationWindow);
					// If the score is not in the window (if if the limit is out, so >=) we must research at the same depth
					boolean research = (score <= (lastScore - halfAspirationWindow)) ||
						(score >= (lastScore + halfAspirationWindow)); //|| movesFound.get(0).size() == 0; ????
					System.out.println("score = " + score + " lastScore = " + lastScore + " re-search = "+ research);
					if (research) {
						score = search(board, 0, depth, 0, -Evaluator.VICTORY, Evaluator.VICTORY);						
					}
					lastScore = score;
				} else {
					score = search(board, 0, depth, 0, -Evaluator.VICTORY, Evaluator.VICTORY);					
				}
				
				if (movesFound.get(0).size() > 0) {
					bestMoves = new ArrayList<Move>(movesFound.get(0));
					long time = System.currentTimeMillis();
					System.out.println(depth + "ply -> " + bestMoves + " / score=" + score + " / " + 
						positionCounter + " nodes in " + (time-startTime) + "ms: " + 1000 * positionCounter / ((time-startTime+1)) + "Nps" +
					    " / Short eff=" + (100 * (moveCounter - movesWalked) / (moveCounter + 1)) + "%");

					// update best 1st move and time
					if (!bestMoves.get(0).equals(bestMove)) {
						bestMove = bestMoves.get(0);
						bestMoveTime = time - startTime;
					}
					// if mate found exit
					if ((score < -Evaluator.VICTORY + 1000) || (score > Evaluator.VICTORY - 1000)) break;
				} else {
					System.out.println("ERROR: no moves found at level 0");
				}
			}
		} catch(TimeExceedException e) {
		}
		
		if (bestMoves.size()>0) board.doMove(bestMoves.get(0));
		else System.out.println("Error: no best move");
		return;
	}
}