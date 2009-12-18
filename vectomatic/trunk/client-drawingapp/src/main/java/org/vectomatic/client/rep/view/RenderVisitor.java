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
package org.vectomatic.client.rep.view;

import static org.vectomatic.common.model.geometry.Ellipse.K;

import java.util.List;

import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.FloatAttributeValue;
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
import org.vectomatic.common.model.style.IStyle;

/**
 * Shape visitor implementation to perform rendering
 * of the 2D model
 */
public class RenderVisitor implements IShapeVisitor {
	private class Context {
		TransformMatrix m;
		Shape shape;
		public Context() {
			m = new TransformMatrix();
		}
	}
	
	private DrawingView _view;
	private Point _p0, _p1, _p2, _p3;
	private Context[] _stack;
	private int _index;

	public RenderVisitor(DrawingView view) {
		_view = view;
		_stack = new Context[8];
		_p0 = new Point();
		_p1 = new Point();
		_p2 = new Point();
		_p3 = new Point();
	}
	
	public TransformMatrix pushShape(Shape shape) {
		// Grow the context stack if too small
		if (_index >= _stack.length) {
			Context[] stack = new Context[2 * _stack.length];
			for (int i = 0; i < _stack.length; i++) {
				stack[i] = _stack[i];
			}
			_stack = stack;
		}
		
		// Allocate a stack slot if not allocated yet. Otherwise reuse it
		if (_stack[_index] == null) {
			_stack[_index] = new Context();
		}
		
		TransformMatrix m;
		if (_index == 0) {
			m = _stack[_index].m = shape.getTransform();
		} else {
			m = shape.getTransform().preMultiply(_stack[_index - 1].m, _stack[_index].m);
		}
		_stack[_index].shape = shape;
		_index++;
		return m;
	}
	
	public void popShape() {
		_index--;
	}
	
	public void visitEllipse(Ellipse ellipse) {
		BoundingBox bbox = ellipse.getBoundingBox();
		TransformMatrix m = pushShape(ellipse);
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
		if (applyFillStyle()) {
			_view.fill();
		}
		if (applyStrokeStyle()) {
			_view.stroke();
		}
		popShape();
	}

	public void visitPolyline(Polyline polyline) {
		TransformMatrix m = pushShape(polyline);
		Point[] pts = polyline.getVertices();

		_view.beginPath();
		pts[0].transform(m, _p0);
		_view.moveTo(_p0.x, _p0.y);
		for (int i = 1; i < pts.length; i++) {
			pts[i].transform(m, _p0);
			_view.lineTo(_p0.x, _p0.y);
		}
		if (polyline.isClosed()) {
			_view.closePath();
			if (applyFillStyle()) {
				_view.fill();
			}
		}
		if (applyStrokeStyle()) {
			_view.stroke();
		}
		
		popShape();
	}

	public void visitRect(Rect rect) {
		TransformMatrix m = pushShape(rect);

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
	
		if (applyFillStyle()) {
			_view.fill();
		}
		if (applyStrokeStyle()) {
			_view.stroke();
		}
		popShape();
	}

	public void visitShapeGroup(ShapeGroup group) {
		pushShape(group);
		List<Shape> shapes = group.getShapes();
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			shape.acceptVisitor(this);
		}
		popShape();
	}

	public void visitPath(Path path) {
		TransformMatrix m = pushShape(path);
		_view.beginPath();
		List<Segment> segments = path.getSegments();
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
		if (path.isClosed()) {
			_view.closePath();
			if (applyFillStyle()) {
				_view.fill();
			}
		}
		if (applyStrokeStyle()) {
			_view.stroke();
		}
		
		popShape();
	}

	private boolean applyStrokeStyle() {
		boolean doStroke = false;
		IStyle strokeStyle = (IStyle)getAttribute(Attribute.LINE_STYLE);
		if (strokeStyle != null && strokeStyle.getKind() != IStyle.NONE) {
			float opacity = ((FloatAttributeValue)getAttribute(Attribute.LINE_OPACITY)).getValue();
			if (opacity > 0.0f) {
				doStroke = true;
				strokeStyle.acceptVisitor(_view.getStrokeStyleVisitor());
				float viewOpacity = _view.getGlobalAlpha();
				if (viewOpacity != opacity) {
					_view.setGlobalAlpha(opacity);
				}
				float lineWidth = ((FloatAttributeValue)getAttribute(Attribute.LINE_WIDTH)).getValue();
				if (_view.getLineWidth() != lineWidth) {
					_view.setLineWidth(lineWidth);
				}
			}
		}
		return doStroke;
	}
	
	private boolean applyFillStyle() {
		boolean doFill = false;
		IStyle fillStyle = (IStyle)getAttribute(Attribute.FILL_STYLE);
		if (fillStyle != null && fillStyle.getKind() != IStyle.NONE) {
			float opacity = ((FloatAttributeValue)getAttribute(Attribute.FILL_OPACITY)).getValue();
			if (opacity > 0.0f) {
				doFill = true;
				fillStyle.acceptVisitor(_view.getFillStyleVisitor());
				float viewOpacity = _view.getGlobalAlpha();
				if (viewOpacity != opacity) {
					_view.setGlobalAlpha(opacity);
				}
			}
		}
		return doFill;
	}
	
	private Object getAttribute(Attribute attr) {
		Object attrValue = null;
		for (int i = 0; i < _index; i++) {
			if (_stack[i].shape.definesAttribute(attr)) {
				attrValue = _stack[i].shape.getAttribute(attr);
				break;
			}
		}
		return attrValue;
	}

}
