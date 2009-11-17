package com.alonsoruibal.chess.uci;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.alonsoruibal.chess.Config;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.PropertyConfig;
import com.alonsoruibal.chess.book.BinaryBook;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.search.SearchEngineMT;
import com.alonsoruibal.chess.search.SearchObserver;
import com.alonsoruibal.chess.search.SearchParameters;
import com.alonsoruibal.chess.search.SearchStatusInfo;

/**
 * UCI Interface
 * TODO search params, ponder, etc
 * 
 * @author rui
 */
public class Uci implements SearchObserver {

	SearchEngineMT engine;
	
	public Uci() {
		Logger.noLog = true; // Disable logging
		Config config = new PropertyConfig();
		engine = new SearchEngineMT(config, new BinaryBook(config));
		engine.setObserver(this);
	}
	
	void loop() {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			while (true) {
				String in = reader.readLine();
				StringTokenizer st = new StringTokenizer(in);
				String command = st.nextToken().toLowerCase();

				if ("uci".equals(command)) {
					System.out.println("id name Carballo Chess Engine");
					System.out.println("id author Alberto Alonso Ruibal");
					// No options at the moment
					System.out.println("uciok");
				} else if ("quit".equals(command)) {
					System.exit(0);
				} else if ("isready".equals(command)) {
					System.out.println("readyok");
				} else if ("go".equals(command)) {
					//...
					SearchParameters searchParameters = new SearchParameters();
					while (st.hasMoreTokens()) {
						String arg1 = st.nextToken();
						if ("searchmoves".equals(arg1)) {
							// TODO
						} else if ("ponder".equals(arg1)) {
							searchParameters.setPonder(true);
						} else if ("wtime".equals(arg1)) {
							searchParameters.setBtime(Integer.valueOf(st.nextToken()));
						} else if ("btime".equals(arg1)) {
							searchParameters.setWtime(Integer.valueOf(st.nextToken()));
						} else if ("winc".equals(arg1)) {
							searchParameters.setWinc(Integer.valueOf(st.nextToken()));
						} else if ("binc".equals(arg1)) {
							searchParameters.setBinc(Integer.valueOf(st.nextToken()));
						} else if ("movestogo".equals(arg1)) {
							searchParameters.setMovesToGo(Integer.valueOf(st.nextToken()));
						} else if ("depth".equals(arg1)) {
							searchParameters.setDepth(Integer.valueOf(st.nextToken()));
						} else if ("nodes".equals(arg1)) {
							searchParameters.setNodes(Integer.valueOf(st.nextToken()));
						} else if ("mate".equals(arg1)) {
							searchParameters.setMate(Integer.valueOf(st.nextToken()));
						} else if ("movetime".equals(arg1)) {
							searchParameters.setMoveTime(Integer.valueOf(st.nextToken()));
						} else if ("infinite".equals(arg1)) {
							searchParameters.setInfinite(true);
						}
					}
					engine.go(searchParameters);

				} else if ("stop".equals(command)) {
					engine.stop();
					
				} else if ("ucinewgame".equals(command)) {
					engine.getBoard().startPosition();
				} else if ("position".equals(command)) {
					if (st.hasMoreTokens()) {
						String arg1 = st.nextToken();
						if ("startpos".equals(arg1)) {
							engine.getBoard().startPosition();							
						} else {
							engine.getBoard().setFen(arg1);
						}
					}
					if (st.hasMoreTokens()) {
						String arg1 = st.nextToken();
						if ("moves".equals(arg1)) {
							while (st.hasMoreTokens()) {
								int move = Move.getFromString(engine.getBoard(), st.nextToken());
								engine.getBoard().doMove(move);
							}
						}
					}
					
				} else if ("debug".equals(command)) {
				} else if ("ponderhit".equals(command)) {
					// TODO ponder not supported
				} else if ("setoption".equals(command)) {
				} else if ("register".equals(command)) {
					// not used
				} else {
					System.out.println("Command not recognized: " + in);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bestMove(int bestMove, int ponder) {
		StringBuffer sb = new StringBuffer();
		sb.append("bestmove ");
		sb.append(Move.toString(bestMove));
		if (ponder != -1) {
			sb.append(" ponder ");
			sb.append(Move.toString(ponder));			
		}
		System.out.println(sb.toString());
	}

	public void info(SearchStatusInfo info) {
		System.out.print("info ");
		System.out.println(info.toString());		
	}
	
	public static void main(String args[]) {
		Uci uci = new Uci();
		uci.loop();
	}
}
