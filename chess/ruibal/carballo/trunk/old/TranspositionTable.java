package com.alonsoruibal.chess.tt;


public interface TranspositionTable {

	public abstract TranspositionTableInfo get(long key);

	public abstract void put(long key, TranspositionTableInfo info);

}