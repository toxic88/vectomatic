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
package org.vectomatic.common.model.geometry;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a group of shapes (composite
 * design pattern)
 */
public class ShapeGroup extends Shape implements IsSerializable {
	/**
	 * A List of palettes.
	 */
	protected List<Shape> _shapes;
	
	public ShapeGroup() {
		// For GWT serialization
	}
	public ShapeGroup(List<Shape> shapes) {
		super();
		_bbox = new BoundingBox();
		_shapes = new ArrayList<Shape>(shapes);
		updateGroup();
	}
	
	public ShapeGroup(ShapeGroup group) {
		super(group);
		_shapes = group._shapes;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Group(m=");
		buffer.append(getTransform().toString());
		buffer.append(" members={");
		buffer.append(_shapes.toString());
		buffer.append("} atts=");
		buffer.append(_attributes.toString());
		buffer.append(")");
		return buffer.toString();
	}


	@Override
	public boolean isSame(Shape shape) {
		if (shape instanceof ShapeGroup) {
			ShapeGroup group = (ShapeGroup)shape;
			if (getTransform().equals(group.getTransform()) 
			&& _shapes.size() == group._shapes.size() 
			&& _attributes.equals(group._attributes)) {
				for (int i = 0, size = _shapes.size(); i < size; i++) {
					Shape shape1 = _shapes.get(i);
					Shape shape2 = _shapes.get(i);
					if (!shape1.isSame(shape2)) {
						return false;
					}
				}				
				return true;
			}
		}
		return false;
	}

	public void updateGroup() {		
		Point p = new Point();
		if (_shapes.size() > 0) {
			_bbox.xmin = _bbox.ymin = Float.MAX_VALUE;
			_bbox.xmax = _bbox.ymax = -Float.MAX_VALUE;
			for (int i = 0, size = _shapes.size(); i < size; i++) {
				Shape shape = _shapes.get(i);
				for (int j = BoundingBox.PT_NW; j <= BoundingBox.PT_NE; j++) {
					shape.getBoundingBox().getPoint(j, p).transform(shape.getTransform());
					if (p.x < _bbox.xmin) {
						_bbox.xmin = p.x;
					}
					if (p.x > _bbox.xmax) {
						_bbox.xmax = p.x;
					}
					if (p.y < _bbox.ymin) {
						_bbox.ymin = p.y;
					}
					if (p.y > _bbox.ymax) {
						_bbox.ymax = p.y;
					}
				}
			}
			_bbox.getPoint(BoundingBox.PT_C, p);
		} else {
			Point.ZERO.copyTo(p);
		}
		setScaling(Point.UNIT);
		setRotation(0f);
		setTranslation(p);
	}
	
	@Override
	public void acceptVisitor(IShapeVisitor visitor) {
		visitor.visitShapeGroup(this);
	}
	
	public List<Shape> getShapes() {
		return _shapes;
	}
	public void setShapes(List<Shape> shapes) {
		_shapes = shapes;
		updateGroup();
	}
	
	public boolean containsRotatedShape() {
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			if (shape.getRotation() != 0f) {
				return true;
			}
			if (shape instanceof ShapeGroup && ((ShapeGroup)shape).containsRotatedShape()) {
				return true;
			}
		}
		return false;
	}
}
