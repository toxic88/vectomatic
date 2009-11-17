package com.alonsoruibal.chess.tt;


/**
 * 
 * Uses part of the board's zobrish key (shifted) as the index 
 * 
 * @author rui
 * 
 */
public class ArrayTranspositionTable implements TranspositionTable {
	
	private long keys[];
	private TranspositionTableInfo infos[];
	private int sizeBits;

	/**
	 * Whe must indicate the number in bits of the size
	 * example: 23 => 2^23 are 8 million entries
	 * @param sizeBits
	 */
	public ArrayTranspositionTable(int sizeBits) {
		this.sizeBits = sizeBits;
		int size = 1 << sizeBits;
		keys = new long[size];
		infos = new TranspositionTableInfo[size]; 
		System.out.println("Created transposition table, size = " + size + " entries");
	}

	public TranspositionTableInfo get(long key) {
		int keyIndex = (int) (key >>> (64-sizeBits));
		// Verifies that is really this board
		long key2 = keys[keyIndex];
		if (key == key2) return infos[keyIndex]; 
		return null;
	}

	/**
	 * In case of collision overwrites
	 */
	public void put(long key, TranspositionTableInfo info) {
		int keyIndex = (int) (key >>> (64-sizeBits));
		keys[keyIndex] = key;
		infos[keyIndex] = info; 
	}
}