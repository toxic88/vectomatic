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

import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a polyline
 */
public class Polyline extends Shape implements IsSerializable {
	private Point[] _pts;
	
	public Polyline() {
		// For GWT serialization
	}
	public Polyline(Point[] pts, int count) {
		super();
		_pts = new Point[count];
		for (int i = 0; i < count; i++) {
			_pts[i] = new Point(pts[i]);
		}
		_bbox = new BoundingBox();
		_updateBoundingBox();
		updateTransform();
	}

	public Polyline(Polyline polyline) {
		super(polyline);
		_pts = new Point[polyline._pts.length];
		for (int i = 0; i < _pts.length; i++) {
			_pts[i] = new Point(polyline._pts[i]);
		}
	}
	
	public boolean isClosed() {
		return _pts[0].equals(_pts[_pts.length - 1]);
	}
	
	@Override
	public void acceptVisitor(IShapeVisitor visitor) {
		visitor.visitPolyline(this);
	}
	
	@Override
	public boolean isSame(Shape shape) {
		if (shape instanceof Polyline) {
			Polyline polyline = (Polyline)shape;
			if (getTransform().equals(polyline.getTransform())) {
				if (_pts.length == polyline._pts.length) {
					for (int i = 0; i < _pts.length; i++) {
						if (!_pts[i].equals(polyline._pts[i])) {
							return false;
						}
					}
					return _attributes.equals(polyline._attributes);
				}
			}
		}
		return false;
	}

	
	public Point[] getVertices() {
		return _pts;
	}
	
	public void setVertices(Point[] pts) {
		_pts = pts;
		Point P0 = pts[0].transform(_m, new Point());
		Point P1 = pts[1].transform(_m, new Point());
		_updateBoundingBox();
		Point C = _bbox.getPoint(BoundingBox.PT_C, new Point());
		float cos = (float)Math.cos(_r);
		float sin = (float)Math.sin(_r);
		
		float d = sin * (pts[1].y - pts[0].y) * sin * (pts[0].x - pts[1].x) - cos * (pts[0].y - pts[1].y) * cos * (pts[0].x - pts[1].x);
		_s.x = ((P1.x - P0.x) * cos * (pts[0].y - pts[1].y) - (P1.y - P0.y) * sin * (pts[1].y - pts[0].y)) / d;
		_s.y = ((P0.x - P1.x) * sin * (pts[0].x - pts[1].x) - (P0.y - P1.y) * cos * (pts[0].x - pts[1].x)) / d; 
		
		_t.x = P0.x - _s.x * cos * (pts[0].x - C.x) - _s.y * sin * (C.y - pts[0].y);
		_t.y = P0.y - _s.x * sin * (pts[0].x - C.x) - _s.y * cos * (pts[0].y - C.y);
		_dirty = true;
		updateTransform();
	}
	
	private void _updateBoundingBox() {
		_bbox.xmin = _pts[0].x;
		_bbox.ymin = _pts[0].y;
		_bbox.xmax = _pts[0].x;
		_bbox.ymax = _pts[0].y;
		for (int i = 1; i < _pts.length; i++) {
			if (_pts[i].x < _bbox.xmin) {
				_bbox.xmin = _pts[i].x;
			}
			if (_pts[i].y < _bbox.ymin) {
				_bbox.ymin = _pts[i].y;
			}
			if (_pts[i].x > _bbox.xmax) {
				_bbox.xmax = _pts[i].x;
			}
			if (_pts[i].y > _bbox.ymax) {
				_bbox.ymax = _pts[i].y;
			}
		}
		_bbox.getPoint(BoundingBox.PT_C, _t);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Polyline(m=");
		buffer.append(getTransform().toString());
		buffer.append(" bbox=");
		buffer.append(_bbox.toString());
		buffer.append(" vertices=");
		for (int i = 0; i < _pts.length; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(_pts[i].toString());
		}
		buffer.append(" atts=");
		buffer.append(_attributes.toString());
		buffer.append(")");
		return buffer.toString();
	}
}
