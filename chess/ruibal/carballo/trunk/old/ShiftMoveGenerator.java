package com.alonsoruibal.chess.movegen;

import java.util.ArrayList;
import java.util.List;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.bitboard.BitboardAttacks;
import com.alonsoruibal.chess.bitboard.BitboardUtils;

/**
 * without magic numbers
 * @author Alberto Alonso Ruibal
 */
public class ShiftMoveGenerator implements MoveGenerator {

	// To use only during analysis loop
	boolean pieceIsWhite;
	long mines;
	long opposites;
	long allPieces;
	
	// Pseudo-legal Ops
	List<Move> ops;
	
	/**
	 * 
	 */
	public List<Move> generateMoves(Board workMemory) {
		ops = new ArrayList<Move>();

		allPieces = workMemory.whites | workMemory.blacks; // only for clearity
		
		long i = 0x8000000000000000L;
		while (i != 0) {
			pieceIsWhite  = ((i & workMemory.whites ) != 0);
			mines = (workMemory.getTurn() ? workMemory.whites : workMemory.blacks);
			opposites = (workMemory.getTurn() ? workMemory.blacks : workMemory.whites);
			boolean isKing = ((i & workMemory.kings) != 0);
			int flags = 0;
			if ((i & workMemory.rooks) != 0) flags |= Move.FLAG_ROOK;
			if ((i & workMemory.bishops) != 0) flags |= Move.FLAG_BISHOP;
			if ((i & workMemory.queens) != 0) flags |= Move.FLAG_QUEEN;
			if ((i & workMemory.kings) != 0) flags |= Move.FLAG_KING;
			// Rook / Queen / king
			if ((i & (workMemory.rooks | workMemory.queens | workMemory.kings)) != 0) {	
				generateMoves(workMemory, i, +8, BitboardUtils.b_u, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, -8, BitboardUtils.b_d, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, -1, BitboardUtils.b_r, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, +1, BitboardUtils.b_l, (isKing ? 1 : 8), false, false, flags);
			}
			// Bishop / Queen
			if ((i & (workMemory.bishops | workMemory.queens | workMemory.kings)) != 0) {
				generateMoves(workMemory, i, +9, BitboardUtils.b_u | BitboardUtils.b_l, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, +7, BitboardUtils.b_u | BitboardUtils.b_r, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, -7, BitboardUtils.b_d | BitboardUtils.b_l, (isKing ? 1 : 8), false, false, flags);
				generateMoves(workMemory, i, -9, BitboardUtils.b_d | BitboardUtils.b_r, (isKing ? 1 : 8), false, false, flags);
			}
			// knight
			if ((i & workMemory.knights) != 0) {
				generateMoves(workMemory, i, +17, BitboardUtils.b2_u | BitboardUtils.b_l, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, +15, BitboardUtils.b2_u | BitboardUtils.b_r, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, -15, BitboardUtils.b2_d | BitboardUtils.b_l, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, -17, BitboardUtils.b2_d | BitboardUtils.b_r, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, +10, BitboardUtils.b_u  | BitboardUtils.b2_l, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, +6,  BitboardUtils.b_u  | BitboardUtils.b2_r, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, -6,  BitboardUtils.b_d  | BitboardUtils.b2_l, 1, false, false, Move.FLAG_KNIGHT);
				generateMoves(workMemory, i, -10, BitboardUtils.b_d  | BitboardUtils.b2_r, 1, false, false, Move.FLAG_KNIGHT);
			}
			// Pawns
			if ((i & workMemory.pawns) != 0) {
				if ((i & workMemory.whites) != 0) {
					// Two squares if it is in he first row	
					generateMoves(workMemory, i, 8, BitboardUtils.b_u, (((i & BitboardUtils.b2_d) !=0) ? 2 : 1), true, false, Move.FLAG_PAWN);
					// Temporally set passant flags as opposites
					opposites |= workMemory.flags & Board.FLAGS_PASSANT;
					generateMoves(workMemory, i, 7, BitboardUtils.b_r, 1, false, true, Move.FLAG_PAWN);
					generateMoves(workMemory, i, 9, BitboardUtils.b_l, 1, false, true, Move.FLAG_PAWN);
					opposites &= ~(workMemory.flags & Board.FLAGS_PASSANT);
				} else {
					// Two squares if it is in he first row	
					generateMoves(workMemory, i, -8, BitboardUtils.b_d, (((i & BitboardUtils.b2_u) !=0) ? 2 : 1), true, false, Move.FLAG_PAWN);
					// Temporally set passant flags as opposites
					opposites |= workMemory.flags & Board.FLAGS_PASSANT;
					generateMoves(workMemory, i, -7, BitboardUtils.b_l, 1, false, true, Move.FLAG_PAWN);
					generateMoves(workMemory, i, -9, BitboardUtils.b_r, 1, false, true, Move.FLAG_PAWN);
					opposites &= ~(workMemory.flags & Board.FLAGS_PASSANT);
				}
			}
			i >>>= 1;
		} 

		long myKing = workMemory.kings & mines;
		boolean check = BitboardAttacks.isSquareAttacked(workMemory, myKing, workMemory.getTurn());
		
		// Castling: disabled when in check or squares attacked
		if ((((allPieces & (workMemory.getTurn() ? 0x06L : 0x0600000000000000L)) |
			(workMemory.flags & (workMemory.getTurn() ? Board.FLAG_WHITE_DISABLE_KINGSIDE_CASTLING : Board.FLAG_BLACK_DISABLE_KINGSIDE_CASTLING))) == 0) && 
			  (!check) &&
			  (!BitboardAttacks.isSquareAttacked(workMemory, myKing >>> 1, workMemory.getTurn())) &&
			  (!BitboardAttacks.isSquareAttacked(workMemory, myKing >>> 2, workMemory.getTurn()))) {
			ops.add(new Move(i, (i>>>2), Move.FLAG_KINGSIDE_CASTLING | Move.FLAG_KING));	
		}
		if ((((allPieces & (workMemory.getTurn() ? 0x70L : 0x7000000000000000L)) |
			  (workMemory.flags & (workMemory.getTurn() ? Board.FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING : Board.FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING))) == 0) && 
			  (!check) &&
			  (!BitboardAttacks.isSquareAttacked(workMemory, myKing << 1, workMemory.getTurn())) &&
			  (!BitboardAttacks.isSquareAttacked(workMemory, myKing << 2, workMemory.getTurn()))) {
			ops.add(new Move(i, (i<<2), Move.FLAG_QUEENSIDE_CASTLING | Move.FLAG_KING));	
		}
		return ops;
	}

	/**
	 * Iterative method to generate moves
	 * 
	 * @param square
	 * @param shift
	 * @param border
	 * @param repeat
	 * @param flags: piece flags
	 */
	private final void generateMoves(Board workMemory, long startPos, int shift, long border, int repeats, boolean cannotCapture, boolean onlyIfCapture, int flags) { 
		long square = startPos;
		while ((square & border) == 0) {
			if (shift>0) square <<= shift;
			else square >>>= -shift;
			
			// If we collide with other piece (or other piece and cannot capture), this is blocking
			if ((square & mines) != 0) return;

				boolean capturing = false;
				int score = 0;
				// Capturing
				if ((square & opposites) != 0) {
					if (cannotCapture) return;
					flags |= Move.FLAG_CAPTURE;
					capturing = true;
				}
				if ((workMemory.getTurn() == pieceIsWhite) && ((!onlyIfCapture) || capturing)) {
					score += ((square & BitboardUtils.c4) != 0 ? 50 : 0 ) + ((square & BitboardUtils.c16) != 0 ? 10 : 0 ) + ((square & BitboardUtils.c16) != 0 ? 5 : 0 );
					// Promotions ugly!
					if (((startPos & workMemory.pawns) != 0) && ((workMemory.getTurn() ? (square & BitboardUtils.b_u) : (square & BitboardUtils.b_d)) != 0)) {
						ops.add(new Move(startPos, square, flags | Move.FLAG_PROMOTION_QUEEN));
						ops.add(new Move(startPos, square, flags | Move.FLAG_PROMOTION_KNIGHT));
						ops.add(new Move(startPos, square, flags | Move.FLAG_PROMOTION_ROOK));
						ops.add(new Move(startPos, square, flags | Move.FLAG_PROMOTION_BISHOP));
					} else {
						ops.add(new Move(startPos, square, flags));
					}
					if (capturing) return;
				}
			
			// If we don't need more repeating exit
			if ((--repeats) <= 0) return;
		}
	}
	

}