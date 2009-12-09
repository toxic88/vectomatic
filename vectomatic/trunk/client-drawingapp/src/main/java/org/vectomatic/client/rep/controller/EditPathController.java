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

// Commands
//
// Associate tangents
// Dissociate tangents
// Make tangents associated and symetric
// Insert control point
// Remove control point
// 
// Add a segment between two endpoints
// Join two endpoints to create a continuous path
// Remove a segment
// Cut a path at the selected point
//
// Turn segment to line
// Turn segment to spline

public class EditPathController extends ControllerBase {

	private MouseControllerButton _button;
	public EditPathController(RepApplication app) {
		super(app);
		_button = new MouseControllerButton(_app.getIcons().editPathIcon().createImage(), _app.getConstants().editPathCommand(), this);
	}
	// 0/ Single click on segment + mousemove: change control points so that the spline segment goes through the mouse point
	// 1/ Double-click on segment: insert control point
	// 2/ Delete with selected vertices: remove selected control points
	// 3/ mouseMove point + CTRL + ALT: constrain point movement in the direction of tangent
	// 4/ mouseMove handle + CTRL: rotate by increments
	// 5/ mouseMove handle + ALT: preserve tangent length
	// 6/ mouseMove handle + CTRL: rotate by increments
	// 7/ mouseMove handle + MAJ: rotate both handles (done automatically if the two tangents are aligned)
	// 8/ mouseDown + CTRL: toggle node type
	// 9/ mouseDown + SHIFT: add to selection

	public MouseControllerButton getButton() {
		return _button;
	}
}
