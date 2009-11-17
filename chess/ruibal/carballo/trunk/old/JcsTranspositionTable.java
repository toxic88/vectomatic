package com.alonsoruibal.chess.tt;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

import com.alonsoruibal.chess.Board;

/**
 * TODO
 * 
 * @author rui
 * 
 */
public class JcsTranspositionTable implements TranspositionTable {

	private static final String cacheRegionName = "transpositionTable";
	private JCS cache = null;

	public JcsTranspositionTable() {
		try {
			cache = JCS.getInstance(cacheRegionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TranspositionTableInfo get(Board board) {
		return (TranspositionTableInfo) cache.get(board.getKey());
	}

	public void put(Board board, TranspositionTableInfo info) {
		try {
			cache.put(board.getKey(), info);
		} catch (CacheException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
