/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of Vectomatic.
 * 
 * Vectomatic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vectomatic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Vectomatic.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.CommandHistory;
import org.vectomatic.client.rep.events.ICommandHistoryListener;
import org.vectomatic.client.rep.view.DrawingView;

/**
 * Controller to undo the latest command
 */
public class UndoController extends ControllerBase implements ICommandHistoryListener {
	private ControllerPushButton _undoButton;
	private ControllerMenuItem _undoMenuItem;
	public UndoController(RepApplication app) {
		super(app);
		_app.getHistory().addCommandHistoryListener(this);
		_undoButton = new ControllerPushButton(_app.getView(), _app.getIcons().undoIcon().createImage(), _app.getIcons().undoDisabledIcon().createImage(), _app.getConstants().undoCommand(), this);
		_undoMenuItem = new ControllerMenuItem(_app.getView(), _app.getConstants().undoCommand(), this);
		commandHistoryChange(_app.getHistory());
	}
	@Override
	public void activate(DrawingView view) {
		_app.getHistory().undo();
	}
	
	public void commandHistoryChange(CommandHistory commandHistory) {
		boolean canUndo = _app.getHistory().canUndo();
		_undoButton.setEnabled(canUndo);
		_undoMenuItem.setEnabled(canUndo);
		String text = _app.getConstants().undoCommand();
		if (canUndo) {
			text += " " + _app.getHistory().getUndoCommand().getDescription();
			_undoMenuItem.setText(text);
		} else {
			_undoMenuItem.setText(text);
		}
	}
	public ControllerPushButton getUndoButton() {
		return _undoButton;
	}
	public ControllerMenuItem getUndoMenuItem() {
		return _undoMenuItem;
	}
}
