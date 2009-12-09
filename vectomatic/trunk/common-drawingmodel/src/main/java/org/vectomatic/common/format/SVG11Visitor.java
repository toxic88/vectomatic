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
package org.vectomatic.common.format;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.CloneShapeVisitor;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.IStyleVisitor;
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
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.IStyle;
import org.vectomatic.common.model.style.NoneStyle;
import org.vectomatic.common.model.style.Palette;
import org.vectomatic.common.model.style.PaletteList;

/**
 * Class to export models to the SVG 1.1 format
 */
public class SVG11Visitor implements IShapeVisitor, ISVGExporter {
	private class Context {
		TransformMatrix m;
		Shape shape;
		public Context() {
			m = new TransformMatrix();
		}
	}
	private IStyleVisitor _defStyleVisitor = new IStyleVisitor() {
		public void visitColor(Color color) {
			String style = color.toString();
			_attributes.clear();
			_attributes.put(ATT_SOLIDCOLOR, style);
			writeStartElement(ELT_SOLIDCOLOR, _attributes, true);
		}

		public void visitNoneStyle(NoneStyle none) {
			_attributes.clear();
			_attributes.put(ATT_SOLIDCOLOR, "rgb(0,0,0)");
			_attributes.put(ATT_SOLIDOPACITY, "0");
			_attributes.put(ATT_ID, "none");
			writeStartElement(ELT_SOLIDCOLOR, _attributes, true);
		}		
	};
	private IStyleVisitor _strokeStyleVisitor = new IStyleVisitor() {
		public void visitColor(Color color) {
			_attributes.put(ATT_STROKE, color.toString());
		}

		public void visitNoneStyle(NoneStyle none) {
			_attributes.put(ATT_STROKE, "url(#none)");
		}		
	};	

	private IStyleVisitor _fillStyleVisitor = new IStyleVisitor() {
		public void visitColor(Color color) {
			_attributes.put(ATT_FILL, color.toString());
		}

		public void visitNoneStyle(NoneStyle none) {
			_attributes.put(ATT_FILL, "url(#none)");
		}		
	};	

	private Context[] _stack;
	private int _index;
	private IOutputStream _stream;
	private Map<String, String> _attributes;
	private CloneShapeVisitor _cloner;
	private Point _p0, _p1;

	public SVG11Visitor() {
		_stack = new Context[8];
		_attributes = new HashMap<String, String>();
		_cloner = new CloneShapeVisitor();
		_p0 = new Point();
		_p1 = new Point();
	}
	


	public TransformMatrix pushShape(Shape shape, boolean isTailShape) {
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

		// Compute the shape transform matrix
		TransformMatrix m;
		if (_index == 0) {
			m = _stack[_index].m = shape.getTransform();
		} else {
			m = shape.getTransform().preMultiply(_stack[_index - 1].m, _stack[_index].m);
		}
		_stack[_index].shape = shape;
		_index++;

		if (isTailShape) {
			_attributes.clear();
			IStyle strokeStyle = ((IStyle)getAttribute(Attribute.LINE_STYLE));
			if (strokeStyle != null) {
				strokeStyle.acceptVisitor(_strokeStyleVisitor);
				float opacity = ((FloatAttributeValue)getAttribute(Attribute.LINE_OPACITY)).getValue();
				if (opacity < 1.0f) {
					_attributes.put(ATT_STROKEOPACITY, Float.toString(opacity));
				}
				_attributes.put(ATT_STROKEWIDTH, getAttribute(Attribute.LINE_WIDTH).toString());
			}
			IStyle fillStyle = ((IStyle)getAttribute(Attribute.FILL_STYLE));
			if (fillStyle != null) {
				fillStyle.acceptVisitor(_fillStyleVisitor);
				float opacity = ((FloatAttributeValue)getAttribute(Attribute.FILL_OPACITY)).getValue();
				if (opacity < 1.0f) {
					_attributes.put(ATT_FILLOPACITY, Float.toString(opacity));
				}
			}

			
			// The transformation of the shape must be decomposed
			// in two parts to isolate the scaling which affects the line
			// stroke in SVG1.1
			// M0...Mn = [T(_t) . R(_r)] . [S(_s) . T(- _bbox.center)]
			shape.acceptVisitor(_cloner);
			Shape tmp = _cloner.getClone();
			tmp.setTransform(m);
			TransformMatrix r = new TransformMatrix().rotation(tmp.getRotation());
			TransformMatrix T = new TransformMatrix().translation(tmp.getTranslation(new Point()));
			TransformMatrix m1 = r.preMultiply(T, new TransformMatrix());
			TransformMatrix s = new TransformMatrix().scaling(tmp.getScaling(new Point()));
			TransformMatrix t = new TransformMatrix().translation(tmp.getBoundingBox().getPoint(BoundingBox.PT_C, new Point()).negate());
			TransformMatrix m2 = t.preMultiply(s, new TransformMatrix());

			StringBuffer buffer = new StringBuffer();
			buffer.append("matrix(");
			buffer.append(m1.m11);
			buffer.append(" ");
			buffer.append(m1.m21);
			buffer.append(" ");
			buffer.append(m1.m12);
			buffer.append(" ");
			buffer.append(m1.m22);
			buffer.append(" ");
			buffer.append(m1.m13);
			buffer.append(" ");
			buffer.append(m1.m23);
			buffer.append(")");
			_attributes.put(ATT_TRANSFORM, buffer.toString());
			return m2;
		}
		return m;
	}
	
	
	private void popShape() {
		_index--;
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
	



	public void writeSVG(IOutputStream stream, Shape[] shapes, PaletteList paletteList, int width, int height) {
		_stream = stream;
		_stream.write("<?xml version=\"1.0\"?>\n");

		_attributes.clear();
		_attributes.put(ATT_WIDTH, width + "px");
		_attributes.put(ATT_HEIGHT, height + "px");
		_attributes.put("xmlns", NS);
		writeStartElement(ELT_SVG, _attributes, false);
		
		// Serialize the representation metadata
		writeStartElement(ELT_DESC, new HashMap<String,String>(), false);
		writeCharacters("SVG 1.1 export generated by vectomatic.org");
		writeEndElement(ELT_DESC);
		
		// Serialize the palettes
		writeStartElement(ELT_DEFS, new HashMap<String,String>(), false);
		if (paletteList != null) {
			for (int i = 0, isize = paletteList.size(); i < isize; i++) {
				writeStartElement(ELT_G, new HashMap<String,String>(), false);
				Palette palette = paletteList.getPalette(i);
				writeStartElement(ELT_DESC, new HashMap<String,String>(), false);
				writeCharacters(palette.getName());
				writeEndElement(ELT_DESC);
				for (int j = 0, jsize = palette.getSize(); j < jsize; j++) {
					palette.getColor(j).acceptVisitor(_defStyleVisitor);
				}
				writeEndElement(ELT_G);
			}			
		}
		
		// Serialize the special color 'none'
		NoneStyle.NONE.acceptVisitor(_defStyleVisitor);
		writeEndElement(ELT_DEFS);
		
		// Serialize the shapes
		if (shapes != null) {
			for (int i = 0; i < shapes.length; i++) {
				shapes[i].acceptVisitor(this);
			}
		}
		writeEndElement(ELT_SVG);
	}
	
	public void visitEllipse(Ellipse ellipse) {
		TransformMatrix m = pushShape(ellipse, true);
		ellipse.getBoundingBox().getPoint(BoundingBox.PT_C, _p0).transform(m);
		ellipse.getBoundingBox().getPoint(BoundingBox.PT_SE, _p1).transform(m);
		_attributes.put(ATT_CX, Float.toString(_p0.x));
		_attributes.put(ATT_CY, Float.toString(_p0.y));
		_attributes.put(ATT_RX, Float.toString(Math.abs(_p1.x - _p0.x)));
		_attributes.put(ATT_RY, Float.toString(Math.abs(_p1.y - _p0.y)));
		writeStartElement(ELT_ELLIPSE, _attributes, true);
		popShape();
	}

	public void visitPolyline(Polyline polyline) {
		TransformMatrix m = pushShape(polyline, true);
		Point[] pts = polyline.getVertices();
		StringBuffer vertexBuffer = new StringBuffer();
		for (int i = 0, size = pts.length - (polyline.isClosed() ? 1 : 0); i < size; i++) {
			if (i > 0) {
				vertexBuffer.append(" ");
			}
			pts[i].transform(m, _p0);
			vertexBuffer.append(_p0.x);
			vertexBuffer.append(",");
			vertexBuffer.append(_p0.y);
		}
		_attributes.put(ATT_POINTS, vertexBuffer.toString());
		if (polyline.isClosed()) {
			writeStartElement(ELT_POLYGON, _attributes, true);
		} else {
			writeStartElement(ELT_POLYLINE, _attributes, true);
		}
		popShape();
	}

	public void visitRect(Rect rect) {
		TransformMatrix m = pushShape(rect, true);
		rect.getBoundingBox().getPoint(BoundingBox.PT_NW, _p0).transform(m);
		rect.getBoundingBox().getPoint(BoundingBox.PT_SE, _p1).transform(m);
		_attributes.put(ATT_X, Float.toString(Math.min(_p0.x, _p1.x)));
		_attributes.put(ATT_Y, Float.toString(Math.min(_p0.y, _p1.y)));
		_attributes.put(ATT_WIDTH, Float.toString(Math.abs(_p1.x - _p0.x)));
		_attributes.put(ATT_HEIGHT, Float.toString(Math.abs(_p1.y - _p0.y)));
		writeStartElement(ELT_RECT, _attributes, true);
		popShape();
	}
	
	public void visitPath(Path path) {
		TransformMatrix m = pushShape(path, true);

		StringBuffer dValue = new StringBuffer();
		List<Segment> segments = path.getSegments();
		for (int i = 0, size = segments.size(); i < size; i++) {
			Segment segment = segments.get(i);
			if (i == 0) {
				segment.getStartPoint().transform(m, _p0);
				dValue.append(VAL_MOVE_TO);
				dValue.append(" ");
				dValue.append(_p0.x);
				dValue.append(" ");
				dValue.append(_p0.y);
			}
			dValue.append(" ");
			if (segment instanceof LineSegment) {
				segment.getEndPoint().transform(m, _p0);
				dValue.append(VAL_LINE_TO);
				dValue.append(" ");
				dValue.append(_p0.x);
				dValue.append(" ");
				dValue.append(_p0.y);
			} else {
				BezierSegment bezierSegment = (BezierSegment)segment;
				dValue.append(VAL_CURVE_TO);
				dValue.append(" ");
				bezierSegment.getStartControlPoint().transform(m, _p0);
				dValue.append(_p0.x);
				dValue.append(" ");
				dValue.append(_p0.y);
				dValue.append(" ");
				bezierSegment.getEndControlPoint().transform(m, _p0);
				dValue.append(_p0.x);
				dValue.append(" ");
				dValue.append(_p0.y);
				dValue.append(" ");
				bezierSegment.getEndPoint().transform(m, _p0);
				dValue.append(_p0.x);
				dValue.append(" ");
				dValue.append(_p0.y);
			}
		}
		_attributes.put(ATT_D, dValue.toString());
		
		if (!path.isClosed()) {
			_attributes.put(ATT_FILL, VAL_NONE);
			_attributes.remove(ATT_FILLOPACITY);
		}

		writeStartElement(ELT_PATH, _attributes, true);
		popShape();

	}

	public void visitShapeGroup(ShapeGroup group) {
		pushShape(group, false);
		List<Shape> shapes = group.getShapes();
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			shape.acceptVisitor(this);
		}
		popShape();
	}
	

	private void writeStartElement(String name, Map<String, String> attributes, boolean empty) {
		_stream.write("<");
		_stream.write(name);
		Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			String attName = entry.getKey();
			String attValue = entry.getValue();
			_stream.write(" ");
			_stream.write(attName);
			_stream.write("=\"");
			_stream.write(attValue);
			_stream.write("\"");
		}
		if (empty) {
			_stream.write("/");
		}
		_stream.write(">");
		if (empty) {
			_stream.write("\n");
		}
	}
	
	private void writeEndElement(String name) {
		_stream.write("</");
		_stream.write(name);
		_stream.write(">\n");
	}
	
	private void writeCharacters(String str) {
		_stream.write(str.replaceAll("&", "&amp;").replaceAll("<", "&lt;"));
	}
}
