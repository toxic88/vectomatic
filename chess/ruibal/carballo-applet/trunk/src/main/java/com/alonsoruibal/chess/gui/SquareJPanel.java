package com.alonsoruibal.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

public class SquareJPanel extends JPanel {
	private static final long serialVersionUID = -4865276927847037885L;

	private int index;
	private boolean highlighted;

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		setColor();
	}

	public int getIndex() {
		return index;
	}

	public SquareJPanel(int index) {
		super(new BorderLayout());
		this.index = index;
		this.highlighted = false;
		
		setColor();
	}

	private void setColor() {
		if (highlighted) {
			setBackground(Color.yellow);		
		} else {
			int row = (index / 8) % 2;
			if (row == 0)
				setBackground( index % 2 == 0 ? Color.white : Color.lightGray );
			else
				setBackground( index % 2 == 0 ? Color.lightGray : Color.white );	
		}
	}
}
