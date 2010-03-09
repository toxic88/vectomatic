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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Base class for path segments (line segment, bezier spline segments)
 * @author Lukas Laag
 */
public abstract class Segment implements IsSerializable {
	protected Point[] _pts;
	protected BoundingBox _bbox; // bounding box of the segment

	public Segment() {
		// For GWT serialization
	}

	/**
	 * Constructor
	 * @param pts
	 * An array of points defining the segment
	 */
	public Segment(Point[] pts) {
		_pts = new Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			_pts[i] = new Point(pts[i]);
		}
		_bbox = new BoundingBox();
		_updateBoundingBox();
	}
	
	/**
	 * Copy constuctor
	 * @param segment
	 * A segment to duplicate
	 */
	public Segment(Segment segment) {
		_pts = new Point[segment._pts.length];
		for (int i = 0; i < _pts.length; i++) {
			_pts[i] = new Point(segment._pts[i]);
		}
		_bbox = new BoundingBox(segment._bbox);
	}
		
	/**
	 * Returns the segment first point
	 * @return
	 */
	public Point getStartPoint() {
		return _pts[0];
	}
	
	/**
	 * Return the segment last point
	 * @return
	 */
	public Point getEndPoint() {
		return _pts[_pts.length - 1];
	}
	
	/**
	 * Gets the bounding box of the segment
	 * @return
	 * the bounding box of the segment
	 */
	public BoundingBox getBoundingBox() {
		return _bbox;
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
	}

	
	/**
	 * Returns the squared distance from an arbitrary point p
	 * to the nearest point on the segment
	 * @param p
	 * An arbitrary point p
	 * @return
	 * The squared distance from the closest point on the segment to p
	 */
	public float squaredDistanceToPoint(Point p) {
		Point nearestPoint = new Point();
		nearestPointOnSegment(p, nearestPoint);
		return nearestPoint.squareDistance(p);
	}
	
	/**
	 * Returns the closest point on the segment to the specified point
	 * @param p
	 * An arbitrary point p
	 * @param dest
	 * The closest point on the segment to p
	 */
	public abstract void nearestPointOnSegment(Point p, Point dest);
	
	public abstract Segment clone();

	public Point[] getVertices() {
		return _pts;
	}

}
