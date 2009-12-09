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
package org.vectomatic.common.model.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a line segment in Path
 */
public class LineSegment extends Segment implements IsSerializable {
	public LineSegment() {
		// For GWT serialization
	}

	public LineSegment(Point[] pts) {
		super(pts);
		assert(pts.length == 2);
	}
	public LineSegment(LineSegment segment) {
		super(segment);
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LineSegment{");
		buffer.append(_pts[0]);
		buffer.append(", ");
		buffer.append(_pts[1]);
		buffer.append("}");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LineSegment) {
			LineSegment segment = (LineSegment)obj;
			for (int i = 0; i < _pts.length; i++) {
				if (!_pts[i].equals(segment._pts[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (int i = 0; i < _pts.length; i++) {
			code += _pts[i].hashCode();
		}
		return code;
	}

	
	@Override
	public void nearestPointOnSegment(Point p, Point dest) {
		// Let q be the projection of p on (ab)
		float bax = _pts[1].x - _pts[0].x;
		float bay = _pts[1].y - _pts[0].y;
		float pax = p.x - _pts[0].x;
		float pay = p.y - _pts[0].y;
		float bpx = _pts[1].x - p.x;
		float bpy = _pts[1].y - p.y;
		if (pax * bax + pay * bay < 0) {
			// q is beyond a. return |pa|
			_pts[0].copyTo(dest);
		} else if (bpx * bax + bpy * bay < 0) {
			// q is beyond b. return |pb|
			_pts[1].copyTo(dest);
		} else {	
			float a = _pts[1].y - _pts[0].y;
			float b = _pts[0].x - _pts[1].x;
			float c = _pts[1].x * _pts[0].y - _pts[0].x * _pts[1].y;
	
			float d = b * p.x -a * p.y;
			dest.x = (b * d - a * c) / (a * a + b * b);
			dest.y = (- a * d - b * c) / (a * a + b * b);
		}
	}
	
	@Override
	public Segment clone() {
		return new LineSegment(this);
	}
}
