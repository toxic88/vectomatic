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
 * along with Vectomatic.  If not, see <http://www.gnu.org/licenses/>
 **********************************************/
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.CommandHistory;
import org.vectomatic.client.rep.events.ICommandHistoryListener;
import org.vectomatic.client.rep.view.DrawingView;

/**
 * Controller to redo a previously undone command
 */
public class RedoController extends ControllerBase implements ICommandHistoryListener {
	private ControllerPushButton _redoButton;
	private ControllerMenuItem _redoMenuItem;

	public RedoController(RepApplication app) {
		super(app);
		_app.getHistory().addCommandHistoryListener(this);
		_redoButton = new ControllerPushButton(app.getView(), _app.getIcons().redoIcon().createImage(), _app.getIcons().redoDisabledIcon().createImage(), _app.getConstants().redoCommand(), this);
		_redoMenuItem = new ControllerMenuItem(app.getView(), _app.getConstants().redoCommand(), this);
		commandHistoryChange(_app.getHistory());
	}
	@Override
	public void activate(DrawingView view) {
		_app.getHistory().redo();
	}

	public void commandHistoryChange(CommandHistory commandHistory) {
		boolean canRedo = _app.getHistory().canRedo();
		_redoButton.setEnabled(canRedo);
		_redoMenuItem.setEnabled(canRedo);
		String text = _app.getConstants().redoCommand();
		if (canRedo) {
			text += " " + _app.getHistory().getRedoCommand().getDescription();
			_redoMenuItem.setText(text);
		} else {
			_redoMenuItem.setText(text);
		}
	}
	public ControllerPushButton getRedoButton() {
		return _redoButton;
	}
	public ControllerMenuItem getRedoMenuItem() {
		return _redoMenuItem;
	}
}
