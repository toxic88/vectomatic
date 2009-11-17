package com.alonsoruibal.chess.book;

import com.alonsoruibal.chess.Board;


/**
 * Polyglot opening book support
 * @author rui
 */
public interface Book {
	
	/**
	 * Gets a random move from the book taking care of weights 
	 * @param fen
	 * @return
	 */
	public int getMove(Board board);
}
