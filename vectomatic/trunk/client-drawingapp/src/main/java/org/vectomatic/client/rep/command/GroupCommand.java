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
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.ShapeGroup;
import org.vectomatic.common.model.geometry.TransformMatrix;

/**
 * Command to group shapes
 */
public class GroupCommand extends CommandBase {
	private ShapeGroup _group;

	public GroupCommand(RepApplication app) {
		super(app);
		// Use the selection group to obtain a group with the proper
		// bounding box and position matrix
		_group = new ShapeGroup((ShapeGroup)_app.getSelection().getRootShape());
		_group.clearAttribute(Attribute.FILL_OPACITY);
		
		// Replace its contents with the actually select model shapes
		_group.getShapes().clear();
		_group.getShapes().addAll(_app.getSelection().getSelectedShapes());
	}
	
	public String getDescription() {
		return _app.getConstants().groupCommand();
	}

	public void execute() {
		_app.getModel().addShape(_group);
		List<Shape> shapes = _group.getShapes();
		TransformMatrix groupInv = new TransformMatrix(_group.getTransform()).invert();
		TransformMatrix mTmp = new TransformMatrix();
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(groupInv, mTmp));
			_app.getModel().removeShape(shape);
		}
		List<Shape> selectedShapes = new ArrayList<Shape>();
		selectedShapes.add(_group);
		_app.getSelection().select(selectedShapes);
	}

	public void unexecute() {
		_app.getModel().removeShape(_group);
		List<Shape> shapes = _group.getShapes();
		TransformMatrix mTmp = new TransformMatrix();
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(_group.getTransform(), mTmp));
			_app.getModel().addShape(shape);
		}
		_app.getSelection().select(_group.getShapes());
	}
}
