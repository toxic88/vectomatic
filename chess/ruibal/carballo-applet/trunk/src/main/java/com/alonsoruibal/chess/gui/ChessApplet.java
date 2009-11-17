package com.alonsoruibal.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.alonsoruibal.chess.Config;
import com.alonsoruibal.chess.Move;
import com.alonsoruibal.chess.PropertyConfig;
import com.alonsoruibal.chess.bitboard.BitboardUtils;
import com.alonsoruibal.chess.book.BinaryBook;
import com.alonsoruibal.chess.evaluation.CompleteEvaluator;
import com.alonsoruibal.chess.evaluation.Evaluator;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.movegen.LegalMoveGenerator;
import com.alonsoruibal.chess.movegen.MoveGenerator;
import com.alonsoruibal.chess.search.SearchEngineMT;
import com.alonsoruibal.chess.search.SearchObserver;
import com.alonsoruibal.chess.search.SearchParameters;
import com.alonsoruibal.chess.search.SearchStatusInfo;

/**
 * 
 * @author Alberto Alonso Ruibal
 */
public class ChessApplet extends JApplet implements SearchObserver, ActionListener {
	private static final Logger logger = Logger.getLogger(ChessApplet.class.getSimpleName());

	private static final long serialVersionUID = 5653881094129134036L;
	
	boolean userToMove;
	boolean autoplay;
	
	Evaluator evaluator; 
	MoveGenerator legalMoveGenerator; 
	SearchEngineMT engine;
	BoardJPanel boardPanel;
	SearchParameters searchParameters;
	JPanel global, control, down, info, fen;
	JComboBox combo;
	JTextField fenField;
	String timeString[] = {"1 second", "2 seconds", "5 seconds", "15 seconds", "30 seconds", "60 seconds"}; 
	int timeValues[] = {1000, 2000, 5000, 15000, 30000, 60000};
	int timeDefaultIndex = 2;

	JLabel message;
	
	public void init() {
		evaluator = new CompleteEvaluator(); 
		legalMoveGenerator = new LegalMoveGenerator();
		Config config = new PropertyConfig();
		engine = new SearchEngineMT(config, new BinaryBook(config));
		searchParameters = new SearchParameters();
		searchParameters.setMoveTime(timeValues[timeDefaultIndex]);
		engine.setObserver(this);
		
		userToMove = true;
		autoplay = false;
		
		JButton button;
		control = new JPanel();
		control.setLayout(new FlowLayout());
		button = new JButton("Restart");
		button.setActionCommand("restart");
		button.addActionListener(this);
		control.add(button);

		button = new JButton("Change");
		button.setActionCommand("change");
		button.addActionListener(this);
		control.add(button);

		button = new JButton("Autoplay");
		button.setActionCommand("autoplay");
		button.addActionListener(this);
		control.add(button);
		
		button = new JButton("Back");
		button.setActionCommand("back");
		button.addActionListener(this);
		control.add(button);
		
		combo = new JComboBox(timeString);
		combo.addActionListener(this);
		combo.setSelectedIndex(timeDefaultIndex);
		control.add(combo);
		
		fen = new JPanel();
		fen.setLayout(new FlowLayout());
		fenField = new JTextField();
		fenField.setColumns(30);
		fen.add(fenField);
		button = new JButton("Set Fen");
		button.setActionCommand("fen");
		button.addActionListener(this);
		fen.add(button);
		
		info = new JPanel();
		info.setLayout(new FlowLayout());
		message = new JLabel();
		info.add(message);
		
		down = new JPanel();
		down.setLayout(new BorderLayout());
		down.add("North", fen);
		down.add("South", info);
				
		boardPanel = new BoardJPanel(this);
		global = new JPanel();
		global.setBackground(Color.LIGHT_GRAY);
		global.setLayout(new BorderLayout());
		
		global.add("North", control);
		global.add("Center", boardPanel);
		global.add("South", down);

		update();
		add(global);
	}
	
	@Override
	public void start() {
		autoplay=false;
		userToMove=true;
	}

	@Override
	public void stop() {
		logger.debug("Stop!");
		engine.stop();
		autoplay=false;
		userToMove=false;
	}
	
	@Override
	public void destroy() {
		logger.debug("Destroy!");
		if (engine!= null) {
			engine.destroy();
			engine = null;
		}
		System.gc();
	}

	public void userMove(int fromIndex, int toIndex) {
		if (!userToMove) return;
		boardPanel.unhighlight();
		int move = Move.getFromString(engine.getBoard(), BitboardUtils.index2Algebraic(fromIndex) + BitboardUtils.index2Algebraic(toIndex));
		System.out.println("move="+Move.toString(move));
		// Verify legality and play
		int moves[];
		moves = new int[256];
		int moveCount = legalMoveGenerator.generateMoves(engine.getBoard(), moves, 0);
		Move.printMoves(moves, 0, moveCount);
		boolean moveOk = false;
		for (int i = 0; i < moveCount; i++) {
			if (move == moves[i]) moveOk = true;
		}
		if (moveOk) {
			engine.getBoard().doMove(move);
			update();
			userToMove = false;
			boardPanel.setAcceptInput(userToMove);
			engine.go(searchParameters);
		} else {
			update();
		}
    }

	public void bestMove(int bestMove, int ponder) {
		if (userToMove) return;
		boardPanel.unhighlight();
		boardPanel.highlight(Move.getFromIndex(bestMove), Move.getToIndex(bestMove));
		engine.getBoard().doMove(bestMove);
		update();
		if (autoplay && (engine.getBoard().isEndGame() == 0)) engine.go(searchParameters);
		else {
			userToMove = true;
			boardPanel.setAcceptInput(userToMove);
		}
	}
	
	private void update() {
		//System.out.println(engine.getBoard());
		boardPanel.setFen(engine.getBoard().getFen());
		fenField.setText(engine.getBoard().getFen());
//		List<Move> moves = legalMoveGenerator.generateMoves(engine.getBoard());
//		if (moves.size() == 0) {
//			System.out.println("End Game");
//		}
		System.out.println("value="+ evaluator.evaluateBoard(engine.getBoard()));
		switch (engine.getBoard().isEndGame()) {
		case 1 :
			message.setText("Whites win");
			break;
		case -1:
			message.setText("Blacks win");
			break;
		case 99:
			message.setText("Draw");
			break;
		default:
			if (engine.getBoard().getMoveNumber() == 0) message.setText("(c) 2008 Alberto Alonso Ruibal http://www.alonsoruibal.com");
			else if (engine.getBoard().getTurn()) message.setText("Whites move");
			else message.setText("Blacks move");
		}
		invalidate();
		validate();
		repaint();
	}

	public void info(SearchStatusInfo info) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent oAE) {
		if ("restart".equals(oAE.getActionCommand())) {
			boardPanel.unhighlight();
			userToMove = true;
			autoplay = false;
			engine.stop();
			engine.getBoard().startPosition();
			update();
		} else if ("change".equals(oAE.getActionCommand())) {
			userToMove = false; 
			engine.go(searchParameters);
		} else if ("autoplay".equals(oAE.getActionCommand())) {
			userToMove = false; 
			autoplay = true;
			engine.go(searchParameters);
		} else if ("back".equals(oAE.getActionCommand())) {
			boardPanel.unhighlight();
			userToMove = true; 
			autoplay = false;
			engine.stop();
			engine.getBoard().undoMove();
			update();
		} else if ("fen".equals(oAE.getActionCommand())) {
			boardPanel.unhighlight();
			userToMove = true; 
			autoplay = false;
			engine.stop();
			engine.getBoard().setFen(fenField.getText());
			update();
		} else {
			searchParameters.setMoveTime(timeValues[combo.getSelectedIndex()]);
		}
	}
}