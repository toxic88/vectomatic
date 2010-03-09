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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.common.model.Shape;

/**
 * Command to paste a shape from the model
 */
public class PasteCommand extends CommandBase {
	private List<Shape> _shapes;

	public PasteCommand(RepApplication app, List<Shape> clonedShapes) {
		super(app);
		_shapes = new ArrayList<Shape>(clonedShapes);
	}

	public String getDescription() {
		return _app.getConstants().pasteCommand();
	}
	
	public void execute() {
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			_app.getModel().addShape(shape);
		}
		_app.getModel().fireModelHasChanged();
		_app.getSelection().select(_shapes);
	}

	public void unexecute() {
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			_app.getModel().removeShape(shape);
		}
		_app.getModel().fireModelHasChanged();
		_app.getSelection().select(new ArrayList<Shape>());
	}
}
