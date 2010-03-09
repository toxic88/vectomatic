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
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Path;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.geometry.Rect;

/**
 * Command to add a new shape to the model
 */
public class NewShapeCommand extends CommandBase {
	private Shape _shape;

	public NewShapeCommand(RepApplication app, Shape shape) {
		super(app);
		_shape = shape;
	}
	
	public String getDescription() {
		if (_shape instanceof Ellipse) {
			return _app.getConstants().newEllipseCommand();
		} else if (_shape instanceof Rect) {
			return _app.getConstants().newRectCommand();
		} else if (_shape instanceof Polyline) {
			return _app.getConstants().newPolylineCommand();
		} else if (_shape instanceof Path) {
			return _app.getConstants().newPathCommand();			
		}
		throw new IllegalStateException();
	}
	
	public void unexecute() {
		_app.getModel().removeShape(_shape);
		_app.getModel().fireModelHasChanged();
	}
	
	public void execute() {
		_app.getModel().addShape(_shape);
		_app.getModel().fireModelHasChanged();
	}
}
