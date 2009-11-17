package com.alonsoruibal.chess.search;

import com.alonsoruibal.chess.Config;
import com.alonsoruibal.chess.bitboard.AttackTableGenerator;
import com.alonsoruibal.chess.book.Book;

public class SearchEngineMT extends SearchEngine {
	public SearchEngineMT(Config config, Book book) {
		super(config, book, new AttackTableGenerator());
	}

	/**
	 * Stops thinking
	 */
	public void stop() {
		thinkTo = 0;
		while (isSearching == true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Threaded version
	 */
	public void go(SearchParameters searchParameteres) {
		if (!isSearching) {
			this.searchParameters = searchParameteres;
			Thread thread = new Thread(this);
			thread.start();
		}
	}
}
