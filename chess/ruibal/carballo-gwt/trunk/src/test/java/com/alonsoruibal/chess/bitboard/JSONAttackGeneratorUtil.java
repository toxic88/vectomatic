/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of carballo-gwt.
 * 
 * carballo-gwt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * carballo-gwt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with carballo-gwt.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package com.alonsoruibal.chess.bitboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JSONAttackGeneratorUtil {

	public static void main(String args[]) throws IOException {
		run();
		PrintWriter writer = (args.length > 0) ? new PrintWriter(new FileWriter(new File(args[0]))) : new PrintWriter(new OutputStreamWriter(System.out));
		try {
			writer.println("window.__attack={");
			print("rook", BitboardAttacks.rook, writer);
			writer.println(",");
			print("rookMask", BitboardAttacks.rookMask, writer);
			writer.println(",");
			print("rookMagic", BitboardAttacks.rookMagic, writer);
			writer.println(",");
			print("bishop", BitboardAttacks.bishop, writer);
			writer.println(",");
			print("bishopMask", BitboardAttacks.bishopMask, writer);
			writer.println(",");
			print("bishopMagic", BitboardAttacks.bishopMagic, writer);
			writer.println(",");
			print("knight", BitboardAttacks.knight, writer);
			writer.println(",");
			print("king", BitboardAttacks.king, writer);
			writer.println(",");
			print("pawnDownwards", BitboardAttacks.pawnDownwards, writer);
			writer.println(",");
			print("pawnUpwards", BitboardAttacks.pawnUpwards, writer);
			writer.println("};");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public static void run() {
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
	
	static int count;
	private static void print(long value, PrintWriter writer) {
		double[] array = LongLib.typeChange(value);
		writer.print("[" + new Double(array[0]).longValue() + "," + new Double(array[1]).longValue() + "]");
		count++;
	}
	
	private static void print(String name, long[] array, PrintWriter writer) {
		writer.print("\"");
		writer.print(name);
		writer.print("\":[");
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				writer.print(",");
			}
			print(array[i], writer);
		}
		writer.print("]");
	}
	private static void print(String name, long[][] array, PrintWriter writer) {
		writer.print("\"");
		writer.print(name);
		writer.print("\":[");
		for (int i = 0; i < array.length; i++) {
			writer.print("[");
			for (int j = 0; j < array[i].length; j++) {
				if (j > 0) {
					writer.print(",");
				}
				print(array[i][j], writer);
			}
			writer.print("]");
			if (i < array.length) {
				writer.println(", ");
			}
		}
		writer.print("]");
	}
}
