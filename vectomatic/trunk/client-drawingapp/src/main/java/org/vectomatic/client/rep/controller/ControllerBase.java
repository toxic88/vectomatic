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
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.geometry.Point;

/***
 * Base class for IController implementations
 * @author Lukas Laag
 */
public class ControllerBase implements IController {
	protected RepApplication _app;

	public ControllerBase(RepApplication app) {
		_app = app;
	}

	public void activate(DrawingView view) {
	}

	public void deactivate(DrawingView view) {
	}

	public void render(DrawingView view) {
	}

	public void keyDown(DrawingView view, char keyCode, int modifiers) {
	}

	public void keyPress(DrawingView view, char keyCode, int modifiers) {
	}

	public void keyUp(DrawingView view, char keyCode, int modifiers) {
	}

	public void mouseDown(DrawingView view, Point p, int modifiers) {
	}

	public void mouseMove(DrawingView view, Point p, int modifiers) {
	}

	public void mouseUp(DrawingView view, Point p, int modifiers) {
	}


}
