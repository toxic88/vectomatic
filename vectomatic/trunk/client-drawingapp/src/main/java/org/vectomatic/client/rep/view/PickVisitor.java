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
package org.vectomatic.client.rep.view;

import static org.vectomatic.common.model.geometry.Ellipse.K;

import java.util.Iterator;
import java.util.List;

import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.BezierSegment;
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.LineSegment;
import org.vectomatic.common.model.geometry.Path;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.geometry.Segment;
import org.vectomatic.common.model.geometry.ShapeGroup;
import org.vectomatic.common.model.geometry.TransformMatrix;

/**
 * Shape visitor implementation to perform pick correlation
 * on the 2D model
 */
public class PickVisitor implements IShapeVisitor {
	private DrawingView _view;
	private Point _p, _p0, _p1, _p2, _p3;
	private TransformMatrix _m;
	private TransformMatrix[] _stack;
	private int _index;
	private boolean _picked;
	
	public PickVisitor(DrawingView view) {
		_view = view;
		_m = new TransformMatrix();
		_stack = new TransformMatrix[8];
		_p0 = new Point();
		_p1 = new Point();
		_p2 = new Point();
		_p3 = new Point();
	}

	/**
	 * Returns the picked shape
	 * @param p
	 * A picking point, in model coordinates
	 * @param shapes
	 * A list of shapes to pick from
	 * @return
	 * The picked shape, or null if none is picked.
	 */
	public Shape pick(Point p, Iterator<Shape> shapes) {
		_p = p;
		_picked = false;
		while(shapes.hasNext()) {
			Shape shape = shapes.next();
			shape.acceptVisitor(this);
			if (_picked) {
				return shape;
			}
		}
		return null;		
	}


	private TransformMatrix pushTransform(TransformMatrix m) {
		TransformMatrix currentTransform;
		if (_index == 0) {
			_stack[_index] = m;
			currentTransform = m;
		} else {
			// Grow the stack if too small
			if (_index >= _stack.length) {
				TransformMatrix[] stack = new TransformMatrix[2 * _stack.length];
				for (int i = 0; i < _stack.length; i++) {
					stack[i] = _stack[i];
				}
				_stack = stack;
			}
			// Allocate a stack slot if not allocated yet. Otherwise reuse it
			if (_stack[_index] == null) {
				_stack[_index] = new TransformMatrix();
			}
			currentTransform = m.preMultiply(_stack[_index - 1], _stack[_index]);
		}
		_index++;
		return currentTransform;
	}
	
	private void popTransform() {
		_index--;
	}
	
	public void visitEllipse(Ellipse ellipse) {
		BoundingBox bbox = ellipse.getBoundingBox();
		TransformMatrix m = pushTransform(ellipse.getTransform());
		_view.beginPath();
		_p0.x = -1f;
		_p0.y = 0f;
		_p0.transform(m, _p1);
		_view.moveTo(_p1.x, _p1.y);

		_p0.x = -1f;
		_p0.y = -K;
		_p0.transform(m, _p1);
		_p0.x = -K;
		_p0.y = -1f;
		_p0.transform(m, _p2);
		_p0.x = 0f;
		_p0.y = -1f;
		_p0.transform(m, _p3);
		_view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);
		
		_p0.x = K;
		_p0.y = -1f;
		_p0.transform(m, _p1);
		_p0.x = 1f;
		_p0.y = -K;
		_p0.transform(m, _p2);
		_p0.x = 1f;
		_p0.y = 0f;
		_p0.transform(m, _p3);
		_view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);

		_p0.x = 1f;
		_p0.y = K;
		_p0.transform(m, _p1);
		_p0.x = K;
		_p0.y = 1f;
		_p0.transform(m, _p2);
		_p0.x = 0f;
		_p0.y = 1f;
		_p0.transform(m, _p3);
		_view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);
		
		_p0.x = -K;
		_p0.y = bbox.ymax;
		_p0.transform(m, _p1);
		_p0.x = -1f;
		_p0.y = K;
		_p0.transform(m, _p2);
		_p0.x = -1f;
		_p0.y = 0f;
		_p0.transform(m, _p3);
		_view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);

		_view.closePath();
		_picked = _view.isPointInPath(_p.x, _p.y);
		popTransform();
	}

	public void visitPolyline(Polyline polyline) {
		TransformMatrix m = pushTransform(polyline.getTransform());
		Point[] pts = polyline.getVertices();

		_view.beginPath();
		pts[0].transform(m, _p0);
		_view.moveTo(_p0.x, _p0.y);
		for (int i = 1; i < pts.length; i++) {
			pts[i].transform(m, _p0);
			_view.lineTo(_p0.x, _p0.y);
		}
		if (!polyline.isClosed()) {
			float d = _view.convertToReferenceLength(3);
			_m.translation(d, d);
			m.preMultiply(_m, _m);
			for (int i = pts.length - 1; i >= 0; i--) {
				pts[i].transform(_m, _p0);
				_view.lineTo(_p0.x, _p0.y);
			}
			pts[0].transform(_m, _p0);
			_view.lineTo(_p0.x, _p0.y);
		}
		_view.closePath();
		_picked = _view.isPointInPath(_p.x, _p.y);
		
		popTransform();
	}

	public void visitRect(Rect rect) {
		TransformMatrix m = pushTransform(rect.getTransform());

		_view.beginPath();
		_p0.x = -1f;
		_p0.y = -1f;
		_p0.transform(m, _p1);
		_view.moveTo(_p1.x, _p1.y);

		_p0.x = 1f;
		_p0.y = -1f;
		_p0.transform(m, _p2);
		_view.lineTo(_p2.x, _p2.y);
		
		_p0.x = 1f;
		_p0.y = 1f;
		_p0.transform(m, _p2);
		_view.lineTo(_p2.x, _p2.y);
		
		_p0.x = -1f;
		_p0.y = 1f;
		_p0.transform(m, _p2);
		_view.lineTo(_p2.x, _p2.y);
		
		_view.lineTo(_p1.x, _p1.y);
		
		_view.closePath();
		_picked = _view.isPointInPath(_p.x, _p.y);
		popTransform();
	}

	public void visitShapeGroup(ShapeGroup group) {
		pushTransform(group.getTransform());
		List<Shape> shapes = group.getShapes();
		for (int i = 0, size = shapes.size(); i < size && !_picked; i++) {
			Shape shape = shapes.get(i);
			shape.acceptVisitor(this);
		}
		popTransform();
	}	
	
	public void visitPath(Path path) {
		List<Segment> segments = path.getSegments();
		if (path.isClosed()) {
			TransformMatrix m = pushTransform(path.getTransform());
			_view.beginPath();
			for (int i = 0, size = segments.size(); i < size; i++) {
				Segment segment = segments.get(i);
				if (i == 0) {
					_view.beginPath();
					segment.getStartPoint().transform(m, _p0);
					_view.moveTo(_p0.x, _p0.y);
				}
				if (segment instanceof LineSegment) {
					segment.getEndPoint().transform(m, _p0);
					_view.lineTo(_p0.x, _p0.y);
				} else {
					BezierSegment bezierSegment = (BezierSegment)segment;
					bezierSegment.getStartControlPoint().transform(m, _p0);
					bezierSegment.getEndControlPoint().transform(m, _p1);
					bezierSegment.getEndPoint().transform(m, _p2);
					_view.bezierCurveTo(_p0.x, _p0.y, _p1.x, _p1.y, _p2.x, _p2.y);
				}
			}
			_view.closePath();
			_picked = _view.isPointInPath(_p.x, _p.y);
			popTransform();	
			
		} else {
			TransformMatrix m = pushTransform(path.getTransform().invert(new TransformMatrix()));
			float dmax = _view.convertToReferenceLength(3);
			dmax *= dmax;
			_p.transform(m, _p0);
			for (int i = 0, size = segments.size(); i < size; i++) {
				Segment segment = segments.get(i);
				if (segment.squaredDistanceToPoint(_p0) < dmax) {
					_picked = true;
					break;
				}
			}			
			popTransform();	
		}
	}
}
