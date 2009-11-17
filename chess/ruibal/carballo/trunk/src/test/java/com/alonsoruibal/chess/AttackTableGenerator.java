package com.alonsoruibal.chess;

import com.alonsoruibal.chess.bitboard.AttackGenerator;
import com.alonsoruibal.chess.bitboard.BitboardAttacks;
import com.alonsoruibal.chess.bitboard.BitboardUtils;
import com.alonsoruibal.chess.log.Logger;

public class AttackTableGenerator implements AttackGenerator  {
	private static final Logger logger = Logger.getLogger("AttackTableGenerator");
	
	public void run() {
		logger.debug("Generating attack tables...");
		BitboardAttacks.rook = new long[64] ;
		BitboardAttacks.rookMask = new long[64];
		BitboardAttacks.rookMagic = new long[64][];
		BitboardAttacks.bishop = new long[64];
		BitboardAttacks.bishopMask = new long[64];
		BitboardAttacks.bishopMagic = new long[64][];
		BitboardAttacks.knight = new long[64];
		BitboardAttacks.king = new long[64];
		BitboardAttacks.pawnDownwards = new long[64];
		BitboardAttacks.pawnUpwards = new long[64];

		long square = 1;
		byte i = 0;
		while (square != 0) {
			BitboardAttacks.rook[i] = squareAttackedAuxSlider(square, +8, BitboardUtils.b_u)
					| squareAttackedAuxSlider(square, -8, BitboardUtils.b_d)
					| squareAttackedAuxSlider(square, -1, BitboardUtils.b_r)
					| squareAttackedAuxSlider(square, +1, BitboardUtils.b_l);

			BitboardAttacks.rookMask[i] = squareAttackedAuxSliderMask(square, +8, BitboardUtils.b_u)
					| squareAttackedAuxSliderMask(square, -8, BitboardUtils.b_d)
					| squareAttackedAuxSliderMask(square, -1, BitboardUtils.b_r)
					| squareAttackedAuxSliderMask(square, +1, BitboardUtils.b_l);

			BitboardAttacks.bishop[i] = squareAttackedAuxSlider(square, +9, BitboardUtils.b_u | BitboardUtils.b_l) 
					| squareAttackedAuxSlider(square, +7, BitboardUtils.b_u | BitboardUtils.b_r)
					| squareAttackedAuxSlider(square, -7, BitboardUtils.b_d | BitboardUtils.b_l)
					| squareAttackedAuxSlider(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);
			BitboardAttacks.bishopMask[i] = squareAttackedAuxSliderMask(square, +9, BitboardUtils.b_u | BitboardUtils.b_l) 
					| squareAttackedAuxSliderMask(square, +7, BitboardUtils.b_u | BitboardUtils.b_r)
					| squareAttackedAuxSliderMask(square, -7, BitboardUtils.b_d | BitboardUtils.b_l)
					| squareAttackedAuxSliderMask(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			BitboardAttacks.knight[i] = squareAttackedAux(square, +17, BitboardUtils.b2_u | BitboardUtils.b_l)
					| squareAttackedAux(square, +15, BitboardUtils.b2_u | BitboardUtils.b_r)
					| squareAttackedAux(square, -15, BitboardUtils.b2_d | BitboardUtils.b_l)
					| squareAttackedAux(square, -17, BitboardUtils.b2_d | BitboardUtils.b_r)
					| squareAttackedAux(square, +10, BitboardUtils.b_u | BitboardUtils.b2_l)
					| squareAttackedAux(square, +6, BitboardUtils.b_u | BitboardUtils.b2_r)
					| squareAttackedAux(square, -6, BitboardUtils.b_d | BitboardUtils.b2_l)
					| squareAttackedAux(square, -10, BitboardUtils.b_d | BitboardUtils.b2_r);

			BitboardAttacks.pawnUpwards[i] = squareAttackedAux(square, 7, BitboardUtils.b_u | BitboardUtils.b_r)
					| squareAttackedAux(square, 9, BitboardUtils.b_u | BitboardUtils.b_l);

			BitboardAttacks.pawnDownwards[i] = squareAttackedAux(square, -7, BitboardUtils.b_d | BitboardUtils.b_l)
					| squareAttackedAux(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			BitboardAttacks.king[i] = squareAttackedAux(square, +8, BitboardUtils.b_u)
					| squareAttackedAux(square, -8, BitboardUtils.b_d)
					| squareAttackedAux(square, -1, BitboardUtils.b_r)
					| squareAttackedAux(square, +1, BitboardUtils.b_l)
					| squareAttackedAux(square, +9, BitboardUtils.b_u | BitboardUtils.b_l)
					| squareAttackedAux(square, +7, BitboardUtils.b_u | BitboardUtils.b_r)
					| squareAttackedAux(square, -7, BitboardUtils.b_d | BitboardUtils.b_l)
					| squareAttackedAux(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			// And now generate magics			
			int rookPositions = (1 << BitboardAttacks.rookShiftBits[i]);
			BitboardAttacks.rookMagic[i] = new long[rookPositions];
		    for (int j = 0; j < rookPositions; j++) {
		      long pieces = generatePieces(j, BitboardAttacks.rookShiftBits[i], BitboardAttacks.rookMask[i]);
		      int magicIndex = magicTransform(pieces, BitboardAttacks.rookMagicNumber[i], BitboardAttacks.rookShiftBits[i]);
		      BitboardAttacks.rookMagic[i][magicIndex] = getRookShiftAttacks(square, pieces);
		    }
			
			int bishopPositions = (1 << BitboardAttacks.bishopShiftBits[i]);
			BitboardAttacks.bishopMagic[i] = new long[bishopPositions];
		    for (int j = 0; j < bishopPositions; j++) {
		      long pieces = generatePieces(j, BitboardAttacks.bishopShiftBits[i], BitboardAttacks.bishopMask[i]);
		      int magicIndex = magicTransform(pieces, BitboardAttacks.bishopMagicNumber[i], BitboardAttacks.bishopShiftBits[i]);
		      BitboardAttacks.bishopMagic[i][magicIndex] = getBishopShiftAttacks(square, pieces);
		    }
		    
			square <<= 1;
			i++;
		}
		logger.debug("Attack tables generated...");
	}
	
	
	public static int magicTransform(long b, long magic, byte bits) {
		  return (int)((b * magic) >>> (64 - bits));
	}

	private static long squareAttackedAux(long square, int shift, long border) {
		if ((square & border) == 0) {
			if (shift > 0) square <<= shift; else square >>>= -shift;
			return square;
		}
		return 0;
	}
	
	private static long squareAttackedAuxSlider(long square, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) square <<= shift; else square >>>= -shift;
			ret |= square;
		}
		return ret;
	}

	private static long squareAttackedAuxSliderMask(long square, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) square <<= shift; else square >>>= -shift;
			if ((square & border) == 0) ret |= square;
		}
		return ret;
	}

	/** 
	 * witout magic bitboards, too expensive, but neccesary for magic generation
	 */
	static long getRookShiftAttacks(long square, long all) {
		return checkSquareAttackedAux(square, all, +8, BitboardUtils.b_u) |
			checkSquareAttackedAux(square, all, -8, BitboardUtils.b_d) |
			checkSquareAttackedAux(square, all, -1, BitboardUtils.b_r) |
			checkSquareAttackedAux(square, all, +1, BitboardUtils.b_l);
	}
	
	static long getBishopShiftAttacks(long square, long all) {
		return checkSquareAttackedAux(square, all, +9, BitboardUtils.b_u | BitboardUtils.b_l) |
			checkSquareAttackedAux(square, all, +7, BitboardUtils.b_u | BitboardUtils.b_r) |
			checkSquareAttackedAux(square, all, -7, BitboardUtils.b_d | BitboardUtils.b_l) |
			checkSquareAttackedAux(square, all, -9, BitboardUtils.b_d | BitboardUtils.b_r);
	}
	
	/**
	 * Attacks for sliding pieces
	 */
	private static long checkSquareAttackedAux(long square, long all, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift>0) square <<= shift; else square >>>= -shift;
			ret |= square;
			// If we collide with other piece
			if ((square & all) != 0) break;
		}
		return ret;
	}
	
	/**
	 * Fills pieces from a mask. Neccesary for magic generation
	 * variable bits is the mask bytes number
	 * index goes from 0 to 2^bits
	 */
	static long generatePieces(int index, int bits, long mask) {
		  int i;
		  long lsb;
		  long result = 0L;
		  for (i = 0; i < bits; i++) {
			lsb = mask & (-mask);
		    mask ^= lsb; // Deactivates lsb bit of the mask to get next bit next time
		    if ((index & (1 << i)) != 0) result |= lsb; // if bit is set to 1
		  }
		  return result;
	}
}
