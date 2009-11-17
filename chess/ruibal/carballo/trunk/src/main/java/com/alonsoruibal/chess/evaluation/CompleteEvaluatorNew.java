package com.alonsoruibal.chess.evaluation;

import com.alonsoruibal.chess.Board;
import com.alonsoruibal.chess.bitboard.BitboardAttacks;
import com.alonsoruibal.chess.bitboard.BitboardUtils;
import com.alonsoruibal.chess.log.Logger;

/**
 * Evaluation is done in centipawns
 * 
 * Material imbalances from Larry KaufMan:
 * http://home.comcast.net/~danheisman/Articles/evaluation_of_material_imbalance.htm
 * 
 * Piece/square values like Fruit/Toga
 *
 * TODO: pawn races
 * TODO: pawn storm
 * TODO: pinned pieces
 * TODO: hanging pieces
 * 
 * TODO: atacks maps, detect board coverage??
 * 
 * @author rui
 */
public class CompleteEvaluatorNew extends Evaluator {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger("CompleteEvaluator");
	
	final static int PAWN                  = 100;
	final static int KNIGHT                = 325;
	final static int BISHOP                = 325;
	final static int ROOK                  = 500;
	final static int QUEEN                 = 975;

	final static int OPENING               = 0;
	final static int ENDGAME               = 1;
	
	// Bishops
//	final static int BISHOP_M_UNITS	       = 6;
//	final static int BISHOP_M[]            = {5,5}; // Mobility units: this value is added for each destination square not occupied by one of our pieces
	final static int BISHOP_MOB[][]        = {{-30,-20,-15,-10,-5,-2,0,5,10,15,20,25,27,28,29},
											  {-30,-20,-15,-10,-5,-2,0,5,10,15,20,25,27,28,29}};	
	final static int BISHOP_PAIR           = 50; // Bonus by having two bishops
	final static int BISHOP_TRAPPED        =-20;

	// Bishops
//	final static int KNIGHT_M_UNITS	       = 4;
//	final static int KNIGHT_M[]            = {4,4};
	final static int KNIGHT_MOB[][]        = {{-20,-12,-8,-4,0,4,8,12,16,18},
											  {-30,-12,-8,-4,0,4,8,12,16,18}};
	//final static int KNIGHT_TRAPPED        =-20;
	final static int KNIGHT_KAUF_BONUS     = 7;

	// Rooks
//	final static int ROOK_M_UNITS	       = 7;
//	final static int ROOK_M[]              = {2,4};
	final static int ROOK_MOB[][]          = {{-15,-11,-10,-8,-6,-4,-2,0,2,4,6,8,10,12,13},
											  {-30,-22,-20,-16,-12,-8,-4,0,4,8,12,16,20,24,26}};
	final static int ROOK_FILE_OPEN        = 20; // No pawns in rook file
	final static int ROOK_FILE_SEMIOPEN    = 10; // Only opposite pawns in rook file
	final static int ROOK_CONNECT[]        = {20,10}; // Rook connects with other rook
	final static int ROOK_KAUF_BONUS       =-12;

	// Queen
//	final static int QUEEN_M_UNITS	       = 13;
//	final static int QUEEN_M[]             = {2,4};
	final static int QUEEN_MOB[][]         = {{-30,-24,-22,-20,-18,-16,-14,-12,-10,-8,-6,-4,-2,0,2,4,6,8,10,12,14,16,18,20,22,24,26,27,28},
											  {-60,-48,-44,-40,-36,-32,-28,-24,-20,-16,-12,-8,-4,0,4,8,12,16,20,24,28,32,36,40,44,48,52,54,56}};
	
	// King safety
	final static int KING_PAWN_NEAR        = 2; // Protection: sums for each pawn near king (opening)
	final static int PAWN_ATTACKS_KING     = 10; // Sums for each piece attacking an square near the king
	final static int ROOK_ATTACKS_KING     = 7;
	final static int KNIGHT_ATTACKS_KING   = 10;
	final static int BISHOP_ATTACKS_KING   = 6;
	final static int QUEEN_ATTACKS_KING    = 8;
	
	// Pawns
	final static int PAWN_DEFENDED         = 5;  // Pawn defended by own pawn
	final static int PAWN_ISOLATED         =-15; // -20;
	final static int PAWN_DOUBLED          =-10; // Penalty for each pawn in a doubled rank
	final static int PAWN_PASSER[]         = {0, 10, 20, 40, 60, 100, 150, 0}; // Depends of the rank 
//	final static int PAWN_CANDIDATE_PASSER[] = {0, 5, 7, 9, 11, 13, 15, 0}; // Depends of the rank 
	
	// Tempo
	public final static int TEMPO          = 10; // Add to moving side score
	
	private final static int knightOutpost[] = {
	      0,   0,   0,   0,   0,   0,   0,   0,
		  0,   0,   0,   0,   0,   0,   0,   0,
		  0,   0,   7,   9,   9,   7,   0,   0,
		  0,   5,  10,  20,  20,  10,   5,   0,
		  0,   5,  10,  20,  20,  10,   5,   0,
		  0,   0,   7,   9,   9,   7,   0,   0,
		  0,   0,   0,   0,   0,   0,   0,   0,
		  0,   0,   0,   0,   0,   0,   0,   0
	};
	
	// The pair of values are {opening, endgame}
	private final static int PawnFileValue[]       = {5,0};
	private final static int KnightCenterValue[]   = {5,5};
	private final static int KnightRankValue[]     = {5,0};
	private final static int KnightBackRankValue[] = {0,0};
	private final static int KnightTrappedValue[]  = {-100,0};
	private final static int BishopCenterValue[]   = {2,3};
	private final static int BishopBackRankValue[] = {-10,0};
	private final static int BishopDiagonalValue[] = {4,0};
	private final static int RookFileValue[]       = {3,0};
	private final static int QueenCenterValue[]    = {0,4};
	private final static int QueenBackRankValue[]  = {-5,0};
	private final static int KingCenterValue[]     = {0,12};
	private final static int KingFileValue[]       = {10,0};
	private final static int KingRankValue[]       = {10,0};

	private final static int PawnFile[]   = {-3, -1, +0, +1, +1, +0, -1, -3};
	private final static int KnightLine[] = {-4, -2, +0, +1, +1, +0, -2, -4};
	private final static int KnightRank[] = {-2, -1, +0, +1, +2, +3, +2, +1};
	private final static int BishopLine[] = {-3, -1, +0, +1, +1, +0, -1, -3};
	private final static int RookFile[]   = {-2, -1, +0, +1, +1, +0, -1, -2};
	private final static int QueenLine[]  = {-3, -1, +0, +1, +1, +0, -1, -3};
	private final static int KingLine[]   = {-3, -1, +0, +1, +1, +0, -1, -3};
	private final static int KingFile[]   = {+3, +4, +2, +0, +0, +2, +4, +3};
	private final static int KingRank[]   = {+1, +0, -2, -3, -4, -5, -6, -7};
	
	// Values are rotated for whites, so when white is playing is like shown in the code
	private final static int pawnIndexValue[][] = new int[64][2];
	private final static int knightIndexValue[][] = new int[64][2];
	private final static int bishopIndexValue[][] = new int[64][2];
	private final static int rookIndexValue[][] = new int[64][2];
	private final static int queenIndexValue[][] = new int[64][2];
	private final static int kingIndexValue[][] = new int[64][2];
	
	static {
		// Initialize Piece square values Fruit/Toga style 
		int i;
		
		for (int phase = 0; phase<2; phase++) {
			for (i = 0; i < 64; i++) {
				int rank = i >> 3;
				int file = 7 - i & 7;
			
				pawnIndexValue[i][phase] = PawnFile[file] * PawnFileValue[phase];
				knightIndexValue[i][phase] = KnightLine[file] * KnightCenterValue[phase] + KnightLine[rank] * KnightCenterValue[phase] + KnightRank[rank] * KnightRankValue[phase];
				bishopIndexValue[i][phase] = BishopLine[file] * BishopCenterValue[phase] + BishopLine[rank] * BishopCenterValue[phase];
				rookIndexValue[i][phase] = RookFile[file] * RookFileValue[phase];
				queenIndexValue[i][phase] = QueenLine[file] * QueenCenterValue[phase] + QueenLine[rank] * QueenCenterValue[phase];
				kingIndexValue[i][phase] = KingFile[file] * KingFileValue[phase]  + KingRank[rank] * KingRankValue[phase] + KingLine[file] * KingCenterValue[phase] + KingLine[rank] * KingCenterValue[phase];
			}

			knightIndexValue[56][phase] += KnightTrappedValue[phase]; // H8 
			knightIndexValue[63][phase] += KnightTrappedValue[phase]; // A8
		
			for (i = 0; i < 8; i++) {
				queenIndexValue[i][phase] += QueenBackRankValue[phase];
				knightIndexValue[i][phase] += KnightBackRankValue[phase];
				bishopIndexValue[i][phase] += BishopBackRankValue[phase];
				bishopIndexValue[(i << 3) | i][phase] += BishopDiagonalValue[phase];
				bishopIndexValue[((i << 3) | i) ^ 070][phase] += BishopDiagonalValue[phase];
			}
		}
		// Pawn opening corrections
		pawnIndexValue[19][OPENING] += 10; // E3
		pawnIndexValue[20][OPENING] += 10; // D3
		pawnIndexValue[27][OPENING] += 25; // E4
		pawnIndexValue[28][OPENING] += 25; // D4
		pawnIndexValue[35][OPENING] += 10; // E5
		pawnIndexValue[36][OPENING] += 10; // D5
		
//		logger.debug("***PAWN");
//		printPcsq(pawnIndexValue);
//		logger.debug("***KNIGHT");
//		printPcsq(knightIndexValue);
//		logger.debug("***BISHOP");
//		printPcsq(bishopIndexValue);
//		logger.debug("***ROOK");
//		printPcsq(rookIndexValue);
//		logger.debug("***QUEEN");
//		printPcsq(queenIndexValue);
//		logger.debug("***KING");
//		printPcsq(kingIndexValue);
//		logger.debug("PCSQ tables generated");
	}
	
//	private static void printPcsq(int pcsq[][]) {
//		StringBuffer sb = new StringBuffer();
//		for (int k=0; k<2; k++) {
//			if (k==0) sb.append("Opening:\n");
//			else sb.append("Endgame:\n");
//			for (int i = 0; i<64; i++) {
//				String aux = "     " + pcsq[i][k];
//				aux = aux.substring(aux.length()-5);
//				sb.append(aux);
//				if (i%8 != 7) {
//					sb.append(",");
//				} else {
//					sb.append("\n");
//				}
//			}
//		}
//		logger.debug(sb.toString());
//	}
	
	public int evaluateBoard(Board board) {
		int pieces = 0;

		long attacks[] = {0,0}; // Squares attacked by each side
		
		long all = board.getAll();
		long pieceAttacks;
		int auxInt;
		int bishopCount[] = {0,0};
		
		int materialValue[] = {0,0};
		int pawnMaterialValue[] = {0,0};
		int mobilityOpeningValue[] = {0,0};
		int mobilityEndgameValue[] = {0,0};
		int positionalOpeningValue[] = {0,0};
		int positionalEndgameValue[] = {0,0};
		int kingSafetyValue[] = {0,0};
		int pawnsValue[] = {0,0};
		
		// Squares surrounding King
		long squaresNearKing[] = {BitboardAttacks.king[BitboardUtils.square2Index(board.whites & board.kings)],
								BitboardAttacks.king[BitboardUtils.square2Index(board.blacks & board.kings)]};

		// From material imbalances (Larry Kaufmann):
		// A further refinement would be to raise the knight's value by 1/16 and lower the rook's value by 1/8
		// for each pawn above five of the side being valued, with the opposite adjustment for each pawn short of five
		int whitePawnsCount = BitboardUtils.popCount(board.pawns & board.whites);
		int blackPawnsCount = BitboardUtils.popCount(board.pawns & board.blacks);
		int knightKaufBonus[] = {KNIGHT_KAUF_BONUS * (whitePawnsCount-5), KNIGHT_KAUF_BONUS * (blackPawnsCount-5)};
		int rookKaufBonus[] = {ROOK_KAUF_BONUS * (whitePawnsCount-5), ROOK_KAUF_BONUS * (blackPawnsCount-5)};
		
		long square = 1;
		byte index = 0;
		while (square != 0) {
			boolean isWhite = ((board.whites & square) != 0);
			int color = (isWhite ? 0 : 1);
			long mines = (isWhite ? board.whites : board.blacks);
			long others = (isWhite ? board.blacks : board.whites);
			int pcsqIndex = (isWhite ? index : 63 - index);
			
			if ((square & all) != 0) {
				pieces++;
				int rank = index >> 3;
				int file = 7 - index & 7;
			
				if ((square & board.pawns  ) != 0) {
					pawnMaterialValue[color] += PAWN;
					positionalOpeningValue[color] += pawnIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += pawnIndexValue[pcsqIndex][ENDGAME];
					
					pieceAttacks = (isWhite ? BitboardAttacks.pawnUpwards[index] : BitboardAttacks.pawnDownwards[index]);
					attacks[color] |= pieceAttacks;
					
					if ((pieceAttacks & squaresNearKing[1-color]) != 0) kingSafetyValue[1-color] -= PAWN_ATTACKS_KING;
					
					if (BitboardUtils.popCount(pieceAttacks & board.pawns & mines) != 0) {
						pawnsValue[color] += PAWN_DEFENDED;
//						logger.debug("Pawn Defended");
					}
					// Doubled pawn detection
					if ((BitboardUtils.FILE[file] & board.pawns & mines) != square) {
						pawnsValue[color] += PAWN_DOUBLED;
//						logger.debug("Pawn Doubled");
					}
					// Isolated pawn
					if ((BitboardUtils.FILE_ADJACENTS[file] & board.pawns & mines) == 0){					
						pawnsValue[color] += PAWN_ISOLATED;
//						logger.debug("Pawn isolated");
					}
					// Passed Pawns
					if (( (BitboardUtils.FILE[file] | BitboardUtils.FILE_ADJACENTS[file]) &
						(isWhite ? BitboardUtils.RANKS_UPWARDS[rank] : BitboardUtils.RANKS_DOWNWARDS[rank])
						& board.pawns) == 0) {
						pawnsValue[color] += PAWN_PASSER[(isWhite ? rank : 7-rank)];		
//						logger.debug("Passed Pawn " + PAWN_PASSER[(isWhite ? rank : 7-rank)]);
					}
//					else if (( (BitboardUtils.FILE[file] | BitboardUtils.FILE_ADJACENTS[file]) &
//						(isWhite ? BitboardUtils.RANKS_UPWARDS[rank+1] : BitboardUtils.RANKS_DOWNWARDS[rank-1])
//						& board.pawns) == 0) {
//						pawnsValue[color] += PAWN_CANDIDATE_PASSER[(isWhite ? rank : 7-rank)];
//					}
				} else if ((square & board.knights) != 0) {
					materialValue[color] += KNIGHT + knightKaufBonus[color];
					positionalOpeningValue[color] += knightIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += knightIndexValue[pcsqIndex][ENDGAME];
					
					pieceAttacks = BitboardAttacks.knight[index];
					attacks[color] |= pieceAttacks;

					auxInt = BitboardUtils.popCount(pieceAttacks & ~mines);
					mobilityOpeningValue[color] += KNIGHT_MOB[OPENING][auxInt];
					mobilityEndgameValue[color] += KNIGHT_MOB[ENDGAME][auxInt];
					
					if ((pieceAttacks & squaresNearKing[1-color]) != 0) kingSafetyValue[1-color] -= KNIGHT_ATTACKS_KING;
					
//					if (((square & BitboardUtils.r4) != 0) && BitboardUtils.popCount(pieceAttacks & ~mines & ~board.pawns) == 0)
//						mobilityOpeningValue[color] += KNIGHT_TRAPPED;
					// Knight outpost: no opposite pawns can attack the square and is defended by one of our pawns
					if ((( BitboardUtils.FILE_ADJACENTS[file] &
						(isWhite ? BitboardUtils.RANKS_UPWARDS[rank] : BitboardUtils.RANKS_DOWNWARDS[rank])
						& board.pawns & others) == 0) &&
						(((isWhite ? BitboardAttacks.pawnDownwards[index] : BitboardAttacks.pawnUpwards[index]) & board.pawns & mines) !=0))
						positionalOpeningValue[color] += knightOutpost[pcsqIndex];
				
				} else if ((square & board.bishops) != 0) {
					materialValue[color] += BISHOP;
					positionalOpeningValue[color] += bishopIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += bishopIndexValue[pcsqIndex][ENDGAME];
					
					pieceAttacks = BitboardAttacks.getBishopAttacks(index, all);
					attacks[color] |= pieceAttacks;

					auxInt = BitboardUtils.popCount(pieceAttacks & ~mines);
					mobilityOpeningValue[color] += BISHOP_MOB[OPENING][auxInt];
					mobilityEndgameValue[color] += BISHOP_MOB[ENDGAME][auxInt];
					
					if ((pieceAttacks & squaresNearKing[1-color]) != 0) kingSafetyValue[1-color] -= BISHOP_ATTACKS_KING;
					
					if (((square & BitboardUtils.r4) != 0) && BitboardUtils.popCount(pieceAttacks & ~mines & ~board.pawns) == 0)
						mobilityOpeningValue[color] += BISHOP_TRAPPED; 
					bishopCount[color] ++;
					if (bishopCount[color] == 2) materialValue[color] += BISHOP_PAIR; 
				
				} else if ((square & board.rooks  ) != 0) {
					materialValue[color] += ROOK + rookKaufBonus[color];;
					positionalOpeningValue[color] += rookIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += rookIndexValue[pcsqIndex][ENDGAME];

					pieceAttacks = BitboardAttacks.getRookAttacks(index, all);
					attacks[color] |= pieceAttacks;
					auxInt = BitboardUtils.popCount(pieceAttacks & ~mines);
					mobilityOpeningValue[color] += ROOK_MOB[OPENING][auxInt];
					mobilityEndgameValue[color] += ROOK_MOB[ENDGAME][auxInt];
					
					if ((pieceAttacks & squaresNearKing[1-color]) != 0) kingSafetyValue[1-color] -= ROOK_ATTACKS_KING;
					
					if ((pieceAttacks & mines & (board.rooks)) != 0) {
						positionalOpeningValue[color] += ROOK_CONNECT[OPENING];
						positionalEndgameValue[color] += ROOK_CONNECT[ENDGAME];
					}
					pieceAttacks = BitboardUtils.FILE[file];
					if ((pieceAttacks & board.pawns) == 0)
						positionalOpeningValue[color] += ROOK_FILE_OPEN;
					else if ((pieceAttacks & board.pawns & mines) == 0)
						positionalOpeningValue[color] += ROOK_FILE_SEMIOPEN;

				} else if ((square & board.queens ) != 0) {
					positionalOpeningValue[color] += queenIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += queenIndexValue[pcsqIndex][ENDGAME];
					materialValue[color] += QUEEN;
					
					pieceAttacks = BitboardAttacks.getRookAttacks(index, all) | BitboardAttacks.getBishopAttacks(index, all);
					attacks[color] |= pieceAttacks;
					auxInt = BitboardUtils.popCount(pieceAttacks & ~mines);
					mobilityOpeningValue[color] += QUEEN_MOB[OPENING][auxInt];
					mobilityEndgameValue[color] += QUEEN_MOB[ENDGAME][auxInt];
					
					if ((pieceAttacks & squaresNearKing[1-color]) != 0) kingSafetyValue[1-color] -= QUEEN_ATTACKS_KING;
					
				} else if ((square & board.kings  ) != 0) {
					pieceAttacks = BitboardAttacks.king[index];
					attacks[color] |= pieceAttacks;
					positionalOpeningValue[color] += kingIndexValue[pcsqIndex][OPENING];
					positionalEndgameValue[color] += kingIndexValue[pcsqIndex][ENDGAME];
					
					if ((square & (isWhite ? BitboardUtils.RANK[OPENING] : BitboardUtils.RANK[7])) != 0)
						kingSafetyValue[color] += KING_PAWN_NEAR * BitboardUtils.popCount(pieceAttacks & mines & board.pawns);
				}		
			}
			square <<= 1;
			index++;
		}

		int value = 0;
		value += materialValue[0] - materialValue[1];
		value += pawnMaterialValue[0] - pawnMaterialValue[1];
		value += pawnsValue[0] - pawnsValue[1];
		value += kingSafetyValue[0] - kingSafetyValue[1];
		// Ponder opening and Endgame value: opening=> gamephase = 255 / ending => gamephase ~=0
		int gamePhase = (256 * ((materialValue[0] + materialValue[1])) / 5000);
		if (gamePhase > 256) gamePhase = 256; // Security
		value += (gamePhase * (positionalOpeningValue[0] - positionalOpeningValue[1]
				+ mobilityOpeningValue[0] - mobilityOpeningValue[1])) >> 8; // divide by 256
		value += ((256 - gamePhase) * (positionalEndgameValue[0] - positionalEndgameValue[1]
		        + mobilityEndgameValue[0] - mobilityEndgameValue[1])) >> 8;
		// Piece attacks
//		value += BitboardUtils.popCount(attacks[0]&~attacks[1]) - BitboardUtils.popCount(attacks[1]&~attacks[0]);
		// Tempo
		value += (board.getTurn() ? TEMPO : -TEMPO);

//	    logger.debug("materialValue          = " + (materialValue[OPENING] - materialValue[ENDGAME]));
//		logger.debug("pawnMaterialValue      = " + (pawnMaterialValue[OPENING] - pawnMaterialValue[ENDGAME]));
//		logger.debug("mobilityOpeningValue   = " + (mobilityOpeningValue[OPENING] - mobilityOpeningValue[ENDGAME]));
//		logger.debug("mobilityEndgameValue   = " + (mobilityEndgameValue[OPENING] - mobilityEndgameValue[ENDGAME]));
//		logger.debug("pawnsValue             = " + (pawnsValue[OPENING] - pawnsValue[ENDGAME]));
//		logger.debug("kingSafetyValue        = " + (kingSafetyValue[OPENING] - kingSafetyValue[ENDGAME]));
//		logger.debug("positionalOpeningValue = " + (positionalOpeningValue[OPENING] - positionalOpeningValue[ENDGAME]));
//		logger.debug("positionalEndgameValue = " + (positionalEndgameValue[OPENING] - positionalEndgameValue[ENDGAME]));
//		logger.debug("gamePhase              = " + gamePhase);
//		logger.debug("tempo                  = " + (board.getTurn() ? TEMPO : -TEMPO));
//		logger.debug("value                  = " + value);
		return value;
	}
}