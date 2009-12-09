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
import org.vectomatic.common.rpc.Representation;

/**
 * Controller to respond to open / save requests. Not active
 * in the present version (no server side yet)
 */
public class RepresentationController extends ControllerBase implements ICommandHistoryListener {
	private Representation _representation;
	private boolean _persisted;
	private ControllerPushButton _saveButton;

	public RepresentationController(RepApplication app) {
		super(app);
		_app.getHistory().addCommandHistoryListener(this);
		
		_representation = new Representation(Representation.DEFAULT_DRAWING_WIDTH, Representation.DEFAULT_DRAWING_HEIGHT, "");
		
		_saveButton = new ControllerPushButton(_app.getView(), _app.getIcons().saveIcon().createImage(), _app.getIcons().saveDisabledIcon().createImage(), _app.getConstants().saveCommand(), new ControllerBase(_app) {
			@Override
			public void activate(DrawingView view) {
				save();
			}
		}); 
				
		refresh();
	}
	
	public void commandHistoryChange(CommandHistory commandHistory) {
		refresh();	
	}
	
	private void refresh() {
		_saveButton.setEnabled(_persisted && _app.getHistory().needsSaving());
	}
	
	public void open() {
	}
	
	public void save() {
	}
	
	public ControllerPushButton getSaveButton() {
		return _saveButton;
	}

	public Representation getCurrentRep() {
		return _representation;
	}
	
}
