package com.alonsoruibal.chess.movesort;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.bitboard.BitboardAttacks;
import com.alonsoruibal.chess.bitboard.BitboardUtils;

/**
 * Sort Moves based on heuristics
 * short first GOOD captures (a piece of less value captures other of more value)
 * 
 * TODO SEE captures, maybe with an approximated way
 * 
 */
public class MoveIterator {
//	private static final Logger logger = Logger.getLogger(MoveIterator.class);
	final static int PHASE_TT = 0;
	final static int PHASE_GEN_CAPTURES = 1;
	final static int PHASE_CAPTURES = 2;
	final static int PHASE_GEN_NONCAPTURES = 3;
	final static int PHASE_KILLER1 = 4;
	final static int PHASE_KILLER2 = 5;
	final static int PHASE_NONCAPTURES = 6;
	
	final static int SCORE_LOWEST = Integer.MIN_VALUE;

	
	private Board board;
	private int lastMoveTo;
	private int ttMove;
	private int killer1;
	private int killer2;
	private boolean foundKiller1;
	private boolean foundKiller2;
	
	private int nonCaptureIndex;
	private int captureIndex;
	private long all;
	private long mines;
	private long others;
	private long attacks[] = new long[64]; // Stores slider pieces attacks

	public int captures[] = new int[256]; // Stores captures and queen promotions
	public int capturesScores[] = new int[256];
	public int nonCaptures[] = new int[256]; // Stores non captures and underpromotions
	public int nonCapturesScores[] = new int[256];
		
	private int depth;
	SortInfo sortInfo;
	int phase;
	
	// [Piece moved][Piece captured]
	private static final int PIECE_VALUES[] = {0,100,200,320,330,500,900};
//	private static final int PIECE_VALUES_SUB[] = {0,10,20,32,33,50,90};

	private static final int SCORE_PROMOTION_QUEEN = 2000;
	private static final int SCORE_CAPTURE_LAST_MOVED = 1000;
	private static final int SCORE_UNDERPROMOTION = Integer.MIN_VALUE+1;
	
	public MoveIterator(Board board, SortInfo sortInfo, int depth) {
		this.sortInfo = sortInfo;
		this.board = board;
		this.depth = depth;
	}
	
	public void setBoard(Board board)  {
		this.board = board;
	}

	/**
	 * Generates captures and tactical moves (not underpromotions)
	 */
	public void generateCaptures() {
//		logger.debug(board);
		
		all = board.getAll(); // only for clearity
		mines = board.getMines();
		others = board.getOthers();

		byte index = 0;
		long square = 0x1L;
		while (square != 0) {
			attacks[index] = 0;
			if (board.getTurn() == ((square & board.whites ) != 0)) {
				
				if ((square & board.rooks) != 0) { // Rook
					attacks[index] = BitboardAttacks.getRookAttacks(index, all);
					generateCapturesFromAttacks(Move.ROOK, index, attacks[index] & others); 
				} else if ((square & board.bishops) != 0) { // Bishop
					attacks[index] = BitboardAttacks.getBishopAttacks(index, all);
					generateCapturesFromAttacks(Move.BISHOP, index, attacks[index] & others); 
				} else if ((square & board.queens) != 0) { // Queen
					attacks[index] = BitboardAttacks.getRookAttacks(index, all) | BitboardAttacks.getBishopAttacks(index, all);
					generateCapturesFromAttacks(Move.QUEEN, index, attacks[index] & others); 
				} else if ((square & board.kings) != 0) { // King
					generateCapturesFromAttacks(Move.KING, index, BitboardAttacks.king[index] & others); 
				} else if ((square & board.knights) != 0) { // Knight
					generateCapturesFromAttacks(Move.KNIGHT, index, BitboardAttacks.knight[index] & others); 
				} else if ((square & board.pawns) != 0) { // Pawns
					if ((square & board.whites) != 0) {
						generatePawnCapturesAndGoodPromos(index,
								(BitboardAttacks.pawnUpwards[index] & (others | board.getPassantSquare()))
								| (((square << 8) & all) == 0 ? (square << 8) : 0),
								board.getPassantSquare());
					} else {
						generatePawnCapturesAndGoodPromos(index,
								(BitboardAttacks.pawnDownwards[index] & (others | board.getPassantSquare()))
								| (((square >>> 8) & all) == 0 ? (square >>> 8) : 0),
								board.getPassantSquare());
					}
				}
			}
			square <<= 1;
			index++;
		}
	}
	
	/**
	 * Generates underpromotions and non tactical moves
	 */
	public void generateNonCaptures() {
		all = board.getAll(); // only for clearity
		mines = board.getMines();
		others = board.getOthers();

		byte index = 0;
		long square = 0x1L;
		while (square != 0) {
			if (board.getTurn() == ((square & board.whites ) != 0)) {
				if ((square & board.rooks) != 0) { // Rook
					generateNonCapturesFromAttacks(Move.ROOK, index, attacks[index] & ~all); 
				} else if ((square & board.bishops) != 0) { // Bishop
					generateNonCapturesFromAttacks(Move.BISHOP, index, attacks[index] & ~all); 
				} else if ((square & board.queens) != 0) { // Queen
					generateNonCapturesFromAttacks(Move.QUEEN, index, attacks[index] & ~all); 
				} else if ((square & board.kings) != 0) { // King
					generateNonCapturesFromAttacks(Move.KING, index, BitboardAttacks.king[index] & ~all); 
				} else if ((square & board.knights) != 0) { // Knight
					generateNonCapturesFromAttacks(Move.KNIGHT, index, BitboardAttacks.knight[index] & ~all); 
				} if ((square & board.pawns) != 0) { // Pawns
					if ((square & board.whites) != 0) {
						generatePawnNonCapturesAndBadPromos(index,
								(BitboardAttacks.pawnUpwards[index] & others)
								| (((square << 8) & all) == 0 ? (square << 8) : 0)
								| ((square & BitboardUtils.b2_d) != 0 && (((square << 8) | (square << 16)) & all) == 0 ? (square << 16) : 0));
					} else {
						generatePawnNonCapturesAndBadPromos(index,
								(BitboardAttacks.pawnDownwards[index] & others)
								| (((square >>> 8) & all) == 0 ? (square >>> 8) : 0)
								| ((square & BitboardUtils.b2_u) != 0 && (((square >>> 8) | (square >>> 16)) & all) == 0 ? (square >>> 16) : 0));
					}
				}
			}
			square <<= 1;
			index++;
		} 

		square = board.kings & mines; // my king
		Byte myKingIndex = null;
		// Castling: disabled when in check or squares attacked
		if ((((all & (board.getTurn() ? 0x06L : 0x0600000000000000L)) == 0  &&
			  (board.getTurn() ? board.getWhiteKingsideCastling() : board.getBlackKingsideCastling())))) {
			myKingIndex = BitboardUtils.square2Index(square);
			if (!board.getCheck() &&
				!BitboardAttacks.isIndexAttacked(board, (byte) (myKingIndex-1), board.getTurn()) &&
				!BitboardAttacks.isIndexAttacked(board, (byte) (myKingIndex-2), board.getTurn()))
				addNonCapturesAndBadPromos(Move.KING, myKingIndex, myKingIndex-2, 0, false, Move.TYPE_KINGSIDE_CASTLING);	
		}
		if ((((all & (board.getTurn() ? 0x70L : 0x7000000000000000L)) == 0 &&
				(board.getTurn() ? board.getWhiteQueensideCastling() : board.getBlackQueensideCastling())))) {
			if (myKingIndex == null) myKingIndex = BitboardUtils.square2Index(square);
			if (!board.getCheck() &&
				!BitboardAttacks.isIndexAttacked(board, (byte) (myKingIndex+1), board.getTurn()) &&
				!BitboardAttacks.isIndexAttacked(board, (byte) (myKingIndex+2), board.getTurn()))
				addNonCapturesAndBadPromos(Move.KING, myKingIndex, myKingIndex+2, 0, false, Move.TYPE_QUEENSIDE_CASTLING);	
		}
	}
	
	/**
	 * Generates moves from an attack mask
	 */
	private final void generateCapturesFromAttacks(int pieceMoved, int fromIndex, long attacks) { 
		while (attacks != 0) {
			long to = BitboardUtils.lsb(attacks);
			addCapturesAndGoodPromos(pieceMoved, fromIndex, BitboardUtils.square2Index(to), to, true, 0);
			attacks ^= to;
		}
	}
	
	private final void generateNonCapturesFromAttacks(int pieceMoved, int fromIndex, long attacks) { 
		while (attacks != 0) {
			long to = BitboardUtils.lsb(attacks);
			addNonCapturesAndBadPromos(pieceMoved, fromIndex, BitboardUtils.square2Index(to), to, false, 0);
			attacks ^= to;
		}
	}
	
	private final void generatePawnCapturesAndGoodPromos(int fromIndex, long attacks, long passant) {
		while (attacks != 0) {
			long to = BitboardUtils.lsb(attacks);
			if ((to & passant) != 0) { 
				addCapturesAndGoodPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, true, Move.TYPE_PASSANT);
			} else {
				boolean capture = (to & others) != 0; 
				if ((to & (BitboardUtils.b_u | BitboardUtils.b_d)) != 0) {
					addCapturesAndGoodPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, Move.TYPE_PROMOTION_QUEEN);
				} else if (capture) {
					addCapturesAndGoodPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, 0);					
				}				
			}
			attacks ^= to;
		}
	}

	private final void generatePawnNonCapturesAndBadPromos(int fromIndex, long attacks) { 
		while (attacks != 0) {
			long to = BitboardUtils.lsb(attacks);
			boolean capture = (to & others) != 0;
			if ((to & (BitboardUtils.b_u | BitboardUtils.b_d)) != 0) {
				addNonCapturesAndBadPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, Move.TYPE_PROMOTION_KNIGHT);
				addNonCapturesAndBadPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, Move.TYPE_PROMOTION_ROOK);
				addNonCapturesAndBadPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, Move.TYPE_PROMOTION_BISHOP);
			} else if (!capture) {
				addNonCapturesAndBadPromos(Move.PAWN, fromIndex, BitboardUtils.square2Index(to), to, capture, 0);
			}
			attacks ^= to;
		}
	}
	
	private void addNonCapturesAndBadPromos(int pieceMoved, int fromIndex, int toIndex, long to, boolean capture, int moveType) {
		int move = Move.genMove(fromIndex, toIndex, pieceMoved, capture, moveType);
		if (move == killer1) {
			foundKiller1 = true;
		} else if (move == killer2) {
			foundKiller2 = true;
		} else if (move != ttMove) {
			// Score non captures
			int score = sortInfo.getMoveScore(move);
			if (moveType == Move.TYPE_PROMOTION_KNIGHT ||
				moveType == Move.TYPE_PROMOTION_ROOK ||
				moveType == Move.TYPE_PROMOTION_BISHOP) score -= SCORE_UNDERPROMOTION;
			
//			System.out.println("* " + score + " - " + Move.toStringExt(move));
			nonCaptures[nonCaptureIndex] = move;
			nonCapturesScores[nonCaptureIndex] = score;
			nonCaptureIndex++;
		}
	}

	private void addCapturesAndGoodPromos(int pieceMoved, int fromIndex, int toIndex, long to, boolean capture, int moveType) {
		int move = Move.genMove(fromIndex, toIndex, pieceMoved, capture, moveType);
		if (move != ttMove) {	
			// Score captures
			int pieceCaptured = 0;
			
			if ((to & board.knights) != 0) pieceCaptured = Move.KNIGHT;
			else if ((to & board.bishops) != 0) pieceCaptured = Move.BISHOP;
			else if ((to & board.rooks) != 0) pieceCaptured = Move.ROOK;
			else if ((to & board.queens) != 0) pieceCaptured = Move.QUEEN;
			else if (capture) pieceCaptured = Move.PAWN;
			
			int score =  PIECE_VALUES[pieceCaptured] - PIECE_VALUES[pieceMoved];
			if (moveType == Move.TYPE_PROMOTION_QUEEN) score += SCORE_PROMOTION_QUEEN;
			if (toIndex == lastMoveTo) score += SCORE_CAPTURE_LAST_MOVED;

//			System.out.println("* " + score + " - " + Move.toStringExt(move));
			captures[captureIndex] = move;
			capturesScores[captureIndex] = score;
			captureIndex++;
		}
	}

	/**
	 * Moves are sorted ascending (best moves at the end)
	 */
	public void genMoves(int ttMove) {
		this.ttMove = ttMove;
		foundKiller1 = false;
		foundKiller2 = false; 
		
		killer1 = sortInfo.killerMove1[depth];
		killer2 = sortInfo.killerMove2[depth];
		lastMoveTo = Move.getToIndex(board.getLastMove());
		
		phase = 0;
		captureIndex = 0;
		nonCaptureIndex = 0;
	}

	public int next() {
		int maxScore, bestIndex;
		switch(phase) {
		case PHASE_TT:
			phase++;
			if (ttMove != 0) {
				return ttMove;
			}
		case PHASE_GEN_CAPTURES:
			phase++;
			generateCaptures();
			// Sort Captures
		case PHASE_CAPTURES:
			maxScore = SCORE_LOWEST;
			bestIndex = -1;
			for (int i = 0; i<captureIndex; i++) {
				if (capturesScores[i] > maxScore) {
					maxScore = capturesScores[i];
					bestIndex = i;
				}
			}
			if (bestIndex != -1) {
				capturesScores[bestIndex] = SCORE_LOWEST;
				return captures[bestIndex];
			}
			phase++;
		case PHASE_GEN_NONCAPTURES:
			phase++;
			generateNonCaptures();
		case PHASE_KILLER1:
			phase++;
			if (foundKiller1) {
				return killer1;
			}
		case PHASE_KILLER2:
			phase++;
			if (foundKiller2) {
				return killer2;
			}
		case PHASE_NONCAPTURES:
			maxScore = SCORE_LOWEST;
			bestIndex = -1;
			for (int i = 0; i<nonCaptureIndex; i++) {
				if (nonCapturesScores[i] > maxScore) {
					maxScore = nonCapturesScores[i];
					bestIndex = i;
				}
			}
			if (bestIndex != -1) {
				nonCapturesScores[bestIndex] = SCORE_LOWEST;
				return nonCaptures[bestIndex];
			}
		}
		return 0;
	}
}