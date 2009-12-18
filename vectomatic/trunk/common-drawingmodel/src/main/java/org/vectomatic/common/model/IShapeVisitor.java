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
 * Visitor design pattern for the Shape class hierarchy
 */
public interface IShapeVisitor {
	public void visitRect(Rect rect);
	public void visitEllipse(Ellipse ellipse);
	public void visitPolyline(Polyline polyline);
	public void visitShapeGroup(ShapeGroup group);
	public void visitPath(Path path);
}
