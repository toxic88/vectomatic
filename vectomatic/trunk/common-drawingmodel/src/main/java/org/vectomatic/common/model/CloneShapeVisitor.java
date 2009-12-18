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
package org.vectomatic.common.model;

import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Path;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.geometry.ShapeGroup;

/**
 * Shape visitor implementation to clone shapes
 */
public class CloneShapeVisitor implements IShapeVisitor {
	Shape _clone;
	public void visitEllipse(Ellipse ellipse) {
		_clone = new Ellipse(ellipse);
	}

	public void visitPolyline(Polyline polyline) {
		_clone = new Polyline(polyline);
	}

	public void visitRect(Rect rect) {
		_clone = new Rect(rect);
	}

	public void visitShapeGroup(ShapeGroup group) {
		_clone = new ShapeGroup(group);
	}
	
	public void visitPath(Path path) {
		_clone = new Path(path);
		
	}

	public Shape getClone() {
		return _clone;
	}

}
