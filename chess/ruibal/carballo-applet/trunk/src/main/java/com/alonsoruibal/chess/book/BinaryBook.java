package com.alonsoruibal.chess.book;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.Config;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.movegen.LegalMoveGenerator;
import com.alonsoruibal.chess.movegen.MoveGenerator;


/**
 * Polyglot opening book support
 * @author rui
 */
public class BinaryBook implements Book {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger("Book");
	
	private String bookName;
	
	List<Integer> moves = new ArrayList<Integer>(); 
	List<Integer> weights = new ArrayList<Integer>();
	long totalWeight;
	
	public BinaryBook(Config config) {
		bookName = config.getProperty("book");
		logger.debug("Using opening book " + bookName);
	}

	public List<Integer> getMoves() {
		return moves;
	}

	public List<Integer> getWeights() {
		return weights;
	}


	
	/**
	 * "move" is a bit field with the following meaning (bit 0 is the least significant bit)
	 *
	 * 		bits                meaning
	 * ===================================
     * 0,1,2               to file
     * 3,4,5               to row
     * 6,7,8               from file
	 * 9,10,11             from row
	 * 12,13,14            promotion piece
     * "promotion piece" is encoded as follows
     * none       0
     * knight     1
     * bishop     2
     * rook       3
     * queen      4
	 * @param move
	 * @return
	 */
	private String int2MoveString(int move) {
		StringBuffer sb = new StringBuffer();
		sb.append((char)('a' + ((move >> 6) & 0x7)));
		sb.append(((move >> 9) & 0x7) +1);
		sb.append((char)('a' + (move & 0x7)));
		sb.append(((move >> 3) & 0x7) +1);
		if (((move >> 12) & 0x7) != 0) sb.append("nbrq".charAt(((move >> 12) & 0x7) - 1));
		return sb.toString();
	}

	
	public void generateMoves(Board board) {
		InputStream bookIs = this.getClass().getResourceAsStream(bookName);
	MoveGenerator legalMoveGenerator = new LegalMoveGenerator();
	int legalMoves[] = new int[256];
	int legalMoveCount = legalMoveGenerator.generateMoves(board, legalMoves, 0);

	long key2Find = board.getKey();
	totalWeight = 0;
	moves = new ArrayList<Integer>(); 
	weights = new ArrayList<Integer>(); 
	DataInput in;
	try {
		in = new DataInputStream(new BufferedInputStream(bookIs));
		while (true) {
			long key = in.readLong();
			int moveInt = in.readShort();
			int weight = in.readShort();
			in.readInt(); // Unused learn field
			
			if (key == key2Find) {
				int move = Move.getFromString(board, int2MoveString(moveInt));
				// Add only if it is legal
				if (Move.contains(move, legalMoves, legalMoveCount)) {
					moves.add(move);
					weights.add(weight);
					totalWeight += weight;
				}
			}
		}
	} catch (Exception e) {}
	}
	
	/**
	 * Gets a random move from the book taking care of weights 
	 * @param fen
	 * @return
	 */
	public int getMove(Board board) {
		generateMoves(board);
		Random random = new Random(System.currentTimeMillis());
		long randomWeight = (new Double(random.nextDouble() * totalWeight)).longValue();
		for (int i = 0; i < moves.size(); i++) {
			randomWeight -= weights.get(i);
			if (randomWeight<=0) return moves.get(i);
		}
		return 0;
	}

}
