package com.alonsoruibal.chess;

import junit.framework.TestCase;

import com.alonsoruibal.chess.book.BinaryBook;
import com.alonsoruibal.chess.book.Book;

/**
 * @author rui
 */
public class BookTest extends TestCase {

	public void testBook() {
		Board board = new Board();
		board.startPosition();
		Book book = new BinaryBook(new PropertyConfig());
		int move = book.getMove(board);
		while (move != -1) {
			System.out.println(move);
			board.doMove(move);
			System.out.println(board);
			move = book.getMove(board);
		}
	}
}