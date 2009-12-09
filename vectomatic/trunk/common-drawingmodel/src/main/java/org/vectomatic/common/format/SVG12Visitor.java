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
import java.util.Stack;

import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.IAttributeValue;
import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.IStyleVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.BezierSegment;
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
 * Class to export models to the SVG 1.2 format
 */
public class SVG12Visitor implements IShapeVisitor, ISVGExporter {
	private Stack<Shape> _stack;
	private IOutputStream _stream;
	private Map<String, String> _attributes;
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

	public SVG12Visitor() {
		_stack = new Stack<Shape>();
		_attributes = new HashMap<String,String>();
	}
	
	public void writeSVG(IOutputStream stream, Shape[] shapes, PaletteList paletteList, int width, int height) {
		_stream = stream;
		_stream.write("<?xml version=\"1.0\"?>\n");

		_attributes.clear();
		_attributes.put(ATT_WIDTH, width + "px");
		_attributes.put(ATT_HEIGHT, height + "px");
		_attributes.put(ATT_VERSION, "1.2");
		_attributes.put(ATT_BASEPROFILE, "tiny");
		_attributes.put("xmlns", NS);
		writeStartElement(ELT_SVG, _attributes, false);
		
		// Serialize the representation metadata
		writeStartElement(ELT_DESC, new HashMap<String,String>(), false);
		writeCharacters("SVG 1.2 export generated by vectomatic.org");
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
		pushShape(ellipse);
		_attributes.put(ATT_CX, "0");
		_attributes.put(ATT_CY, "0");
		_attributes.put(ATT_RX, "1");
		_attributes.put(ATT_RY, "1");
		writeStartElement(ELT_ELLIPSE, _attributes, true);
		popShape();
	}

	public void visitPolyline(Polyline polyline) {
		pushShape(polyline);
		Point[] pts = polyline.getVertices();
		StringBuffer vertexBuffer = new StringBuffer();
		for (int i = 0, size = pts.length - (polyline.isClosed() ? 1 : 0); i < size; i++) {
			if (i > 0) {
				vertexBuffer.append(" ");
			}
			vertexBuffer.append(pts[i].x);
			vertexBuffer.append(",");
			vertexBuffer.append(pts[i].y);
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
		pushShape(rect);
		_attributes.put(ATT_X, "-1");
		_attributes.put(ATT_Y, "-1");
		_attributes.put(ATT_WIDTH, "2");
		_attributes.put(ATT_HEIGHT, "2");
		writeStartElement(ELT_RECT, _attributes, true);
		popShape();
	}

	public void visitPath(Path path) {
		pushShape(path);

		StringBuffer dValue = new StringBuffer();
		List<Segment> segments = path.getSegments();
		for (int i = 0, size = segments.size(); i < size; i++) {
			Segment segment = segments.get(i);
			if (i == 0) {
				dValue.append(VAL_MOVE_TO);
				dValue.append(" ");
				dValue.append(segment.getStartPoint().x);
				dValue.append(" ");
				dValue.append(segment.getStartPoint().y);
			}
			dValue.append(" ");
			if (segment instanceof LineSegment) {
				dValue.append(VAL_LINE_TO);
				dValue.append(" ");
				dValue.append(segment.getEndPoint().x);
				dValue.append(" ");
				dValue.append(segment.getEndPoint().y);
			} else {
				BezierSegment bezierSegment = (BezierSegment)segment;
				dValue.append(VAL_CURVE_TO);
				dValue.append(" ");
				dValue.append(bezierSegment.getStartControlPoint().x);
				dValue.append(" ");
				dValue.append(bezierSegment.getStartControlPoint().y);
				dValue.append(" ");
				dValue.append(bezierSegment.getEndControlPoint().x);
				dValue.append(" ");
				dValue.append(bezierSegment.getEndControlPoint().y);
				dValue.append(" ");
				dValue.append(bezierSegment.getEndPoint().x);
				dValue.append(" ");
				dValue.append(bezierSegment.getEndPoint().y);
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
		pushShape(group);
		writeStartElement(ELT_G, _attributes, false);
		List<Shape> shapes = group.getShapes();
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			shape.acceptVisitor(this);
		}
		writeEndElement(ELT_G);
		popShape();
	}
	
	private String getTransform(Shape shape) {
		StringBuffer buffer = new StringBuffer();
		TransformMatrix m = shape.getTransform();
		buffer.append("matrix(");
		buffer.append(m.m11);
		buffer.append(" ");
		buffer.append(m.m21);
		buffer.append(" ");
		buffer.append(m.m12);
		buffer.append(" ");
		buffer.append(m.m22);
		buffer.append(" ");
		buffer.append(m.m13);
		buffer.append(" ");
		buffer.append(m.m23);
		buffer.append(")");
		return buffer.toString();
	}
	
	private void pushShape(Shape shape) {
		_attributes.clear();
		_attributes.put(ATT_TRANSFORM, getTransform(shape));
		_attributes.put(ATT_VECTOREFFECT, VAL_NON_SCALING_STROKE);
		_stack.push(shape);
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
	}
	
	private void popShape() {
		_stack.pop();
	}
	
	private IAttributeValue getAttribute(Attribute attr) {
		IAttributeValue attrValue = null;
		for (int i = 0; i < _stack.size(); i++) {
			if (_stack.get(i).definesAttribute(attr)) {
				attrValue = _stack.get(i).getAttribute(attr);
				break;
			}
		}
		return attrValue;
	}
	
	private void writeStartElement(String name, Map<String,String> attributes, boolean empty) {
		_stream.write("<");
		_stream.write(name);
		Iterator<Map.Entry<String,String>> iterator = attributes.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String,String> entry = iterator.next();
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
