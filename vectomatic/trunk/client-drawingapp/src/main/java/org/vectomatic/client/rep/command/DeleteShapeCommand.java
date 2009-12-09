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
package org.vectomatic.client.rep.command;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.TransformMatrix;

/**
 * Command to delete a shape from the model
 */
public class DeleteShapeCommand extends CommandBase {
	private TransformMatrix _m;
	private List<Shape> _shapes;
	
	public DeleteShapeCommand(RepApplication app) {
		super(app);
		_shapes = new ArrayList<Shape>(_app.getSelection().getSelectedShapes());
		_m = new TransformMatrix(_app.getSelection().getRootShape().getTransform());
	}

	public String getDescription() {
		return _app.getConstants().deleteShapeCommand();
	}

	public void execute() {
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			_app.getModel().removeShape(shape);
		}
		_app.getSelection().select(new ArrayList<Shape>());
	}

	public void unexecute() {
		TransformMatrix mTmp = new TransformMatrix();
		TransformMatrix mInv = new TransformMatrix();
		_m.invert(mInv);
		_m.preMultiply(mInv, mTmp);
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			_app.getModel().addShape(shape);
			shape.setTransform(shape.getTransform().preMultiply(mInv, mTmp));
		}
		_app.getSelection().select(_shapes);
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(_m, mTmp));
		}
		_app.getSelection().getRootShape().setTransform(_m);
	}

}
