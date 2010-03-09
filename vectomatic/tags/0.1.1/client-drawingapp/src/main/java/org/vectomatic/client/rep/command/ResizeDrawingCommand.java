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
package org.vectomatic.client.rep.command;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.RepresentationController;
import org.vectomatic.common.rpc.Representation;

/**
 * Command to resize the drawing
 */
public class ResizeDrawingCommand extends CommandBase {
	private RepresentationController _controller;
	private int _width;
	private int _height;

	public ResizeDrawingCommand(RepApplication app, RepresentationController controller, int width, int height) {
		super(app);
		_controller = controller;
		_width = width;
		_height = height;
	}

	public String getDescription() {
		return _app.getConstants().resizeDrawingCommand();
	}

	public void execute() {
		run();
	}

	public void unexecute() {
		run();
	}
	
	private void run() {
		Representation rep = _controller.getCurrentRep();
		int width = rep.getWidth();
		int height = rep.getHeight();
		rep.setWidth(_width);
		rep.setHeight(_height);
		_width = width;
		_height = height;
	}

}
