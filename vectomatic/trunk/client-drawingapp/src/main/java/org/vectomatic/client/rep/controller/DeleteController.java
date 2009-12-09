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
import org.vectomatic.client.rep.command.DeleteShapeCommand;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.view.DrawingView;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Controller to respond to delete shape request and turn
 * them into DeleteShapeCommand
 */
public class DeleteController extends ControllerBase {
	
	public DeleteController(RepApplication app) {
		super(app);
	}
	
	@Override
	public void activate(DrawingView view) {
		ICommand command = new DeleteShapeCommand(_app);
		command.execute();
		_app.getHistory().addCommand(command);
	}
	
	public MenuItem newDeleteContextItem(DrawingView view) {
		return new ControllerContextItem(view, _app.getConstants().deleteShapeCommand(), this);
	}
}
