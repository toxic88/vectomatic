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

/**
 * Controller to respond to requests to resize the drawing
 */
import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.command.ResizeDrawingCommand;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.rpc.Representation;

import com.google.gwt.user.client.ui.MenuItem;

public class ResizeController extends ControllerBase {
	private RepresentationController _repController;
	private ResizeDrawingPanel _panel;
	
	public ResizeController(RepApplication app, RepresentationController repController) {
		super(app);
		_repController = repController;
		_panel = new ResizeDrawingPanel(this);
	}
	
	@Override
	public void activate(DrawingView view) {
		Representation rep = _repController.getCurrentRep();
		_panel.show(rep.getWidth(), rep.getHeight());
	}
	
	public MenuItem getResizeMenuItem(DrawingView view) {
		return new ControllerContextItem(view, _app.getConstants().resizeDrawingCommand(), this);
	}

	public void resize(int width, int height) {
		Representation rep = _repController.getCurrentRep();
		if (rep.getWidth() != width || rep.getHeight() != height) {
			ICommand command = new ResizeDrawingCommand(_app, _repController, width, height);
			command.execute();
			_app.getHistory().addCommand(command);
		}
	}

}
