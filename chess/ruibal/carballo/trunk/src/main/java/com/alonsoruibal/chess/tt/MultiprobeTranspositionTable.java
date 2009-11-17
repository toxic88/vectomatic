 package com.alonsoruibal.chess.tt;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.log.Logger;


/**
 * Transposition table using two keys and multiprobe
 * 
 * Uses part of the board's zobrish key (shifted) as the index 
 * 
 * @author rui
 * 
 */
public class MultiprobeTranspositionTable extends TranspositionTable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger("MultiprobeTranspositionTable");
	
	private final static int MAX_PROBES = 4;
	
	public long keys[];
	public long infos[];

	private int sizeBits;
	private int index;
	private int size;
	private long info;
	private byte generation;

	/**
	 * Whe must indicate the number in bits of the size
	 * example: 23 => 2^23 are 8 million entries
	 * @param sizeBits
	 */
	public MultiprobeTranspositionTable(int sizeBits) {
		this.sizeBits = sizeBits;
		size = 1 << sizeBits;
		keys = new long[size];
		infos = new long[size];

		generation = 0;
		index = -1;
		logger.debug("Created Multiprobe transposition table, size = " + size + " entries " + size * 16 /(1024*1024) + "MB");
	}

	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#setIndex(com.alonsoruibal.chess.Board)
	 */
	public boolean search(Board board) {
		int startIndex = (int) (board.getKey() >>> (64-sizeBits));
		// Verifies that is really this board
		for (index=startIndex; index < startIndex + MAX_PROBES && index < size; index++) {
			if (keys[index] == board.getKey2()) {
				info = infos[index];
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#getBestMove()
	 */
	public int getBestMove() {
		return (int)(info & 0x1fffff);
	}

	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#getNodeType()
	 */
	public int getNodeType() {
		return (int)((info >>> 21) & 0xf);
	}

	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#getGeneration()
	 */
	public byte getGeneration() {
		return (byte)((info >>> 32) & 0xff);
	}
	
	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#getDepthAnalyzed()
	 */
	public byte getDepthAnalyzed() {
		return (byte)((info >>> 40) & 0xff);
	}
	
	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#getScore()
	 */
	public int getScore() {
		return (short)((info >>> 48) & 0xffff);
	}
	
	/**
	 * In case of collision overwrites
	 * replace the eldest
	 * keep pv nodes
	 */
	public void set(Board board, int nodeType, int bestMove, int score, byte depthAnalyzed) {
		long key2 =  board.getKey2();
		int startIndex = (int) (board.getKey() >>> (64-sizeBits));
		
		// Verifies that is really this board
		int oldGenerationIndex = -1; // first index of an old generation entry
		int notPvIndex = -1; // first index of an not PV entry
		index = -1;
		for (int i = startIndex; i < startIndex + MAX_PROBES && i < size; i++) {
			info = infos[i];
//			if (keys[i] == 0 || (keys[i] == key2 && (getGeneration() != generation || getDepthAnalyzed() <= depthAnalyzed))) {
			if (keys[i] == 0 || (keys[i] == key2)) {
				index = i;
				break;
			}
			
			if (oldGenerationIndex == -1 && getGeneration() != generation) oldGenerationIndex = i;
			if (notPvIndex == -1 && getNodeType() != TYPE_EXACT_SCORE) notPvIndex = i;
		}
		if (index == -1 && oldGenerationIndex != -1) index = oldGenerationIndex;
		if (index == -1 && notPvIndex != -1) index = notPvIndex;
		if (index == -1) {
//			if (nodeType != TYPE_EXACT_SCORE) return;
			index = startIndex;
		}
		
		keys[index] = key2;
		info = (bestMove & 0x1fffff) |
		       ((nodeType & 0xf) << 21) |
		       (((long)(generation & 0xff)) << 32) |
			   (((long)(depthAnalyzed & 0xff)) << 40) |
			   (((long)(score & 0xffff)) << 48);

		infos[index] = info;
	}

	// called at the start of each search
	/* (non-Javadoc)
	 * @see com.alonsoruibal.chess.tt.TranspositionTable#newGeneration()
	 */
	public void newGeneration() {
		generation++;
	}
	
	@Override
	public boolean isMyGeneration() {
		return getGeneration() == generation;
	}
}