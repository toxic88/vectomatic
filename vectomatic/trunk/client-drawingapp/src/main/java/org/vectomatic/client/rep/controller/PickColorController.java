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
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.style.IStyle;

/**
 * Controller to respond to color picking requests and set
 * the current color accordingly
 */
public class PickColorController extends ControllerBase {
	private StyleController _styleController;
	private IController _viewController;

	public PickColorController(RepApplication app, StyleController styleController) {
		super(app);
		_styleController = styleController;
	}

	@Override
	public void activate(DrawingView view) {
		_viewController = view.getController();
		_viewController.deactivate(view);
		view.setController(this);
		view.setCursor(Cursor.CURSOR_PICK_COLOR);
	}
	
	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		view.toModelCoordinates(p);
		Shape shape = view.getPicker().pick(p, _app.getModel().reverseIterator());
		if (shape != null) {
			_styleController.setStyle((IStyle)shape.getAttribute(_styleController.getStyleAttribute()));
		}
		view.setController(_viewController);
		_viewController.activate(view);
	}
}
