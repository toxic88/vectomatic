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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Base class for geometric shapes
 * @author Lukas Laag
 */
public abstract class Shape implements IsSerializable {
	protected transient DrawingModel _model;
	protected int _id;
	protected float _r;
	protected Point _s;
	protected Point _t;
	protected transient boolean _dirty;
	protected transient TransformMatrix _m;
	protected BoundingBox _bbox;
	/**
	 * A map of attribute, attribute values.
	 */
	protected Map<Attribute, IAttributeValue> _attributes;
	
	public Shape() {
		_t = new Point();
		_s = new Point(Point.UNIT);
		_m = new TransformMatrix();
		_attributes = new HashMap<Attribute, IAttributeValue>();
		_dirty = true;
	}
	
	public Shape(Shape shape) {
		_t = new Point(shape._t);
		_r = shape._r;
		_s = new Point(shape._s);
		_m = new TransformMatrix(shape._m);
		_dirty = shape._dirty;
		_bbox = new BoundingBox(shape._bbox);
		_attributes = new HashMap<Attribute, IAttributeValue>(shape._attributes);
	}
	
	public float getRotation() {
		return _r;
	}
	
	public void setRotation(float r) {
		_r = r;
		_dirty = true;
	}
	
	public Point getScaling(Point s) {
		_s.copyTo(s);
		return s;
	}
	
	public void setScaling(Point s) {
		s.copyTo(_s);
		_dirty = true;
	}
	
	public Point getTranslation(Point t) {
		_t.copyTo(t);
		return t;
	}
	
	public void setTranslation(Point t) {
		t.copyTo(_t);
		_dirty = true;
	}
	
	public TransformMatrix getTransform() {
		if (_dirty) {
			updateTransform();
		}
		return _m;
	}

	public void setTransform(TransformMatrix m) {
		m.copyTo(_m);
		_s.x = (float)Math.sqrt(_m.m11 * _m.m11 + _m.m21 * _m.m21);
		_s.y = (float)Math.sqrt(_m.m12 * _m.m12 + _m.m22 * _m.m22);
		_r = (float)(Math.acos(_m.m11 / _s.x));
		if (_m.m21 < 0f) {
			_r = 2f * (float)Math.PI - _r;
		}
		if (_m.m11 * _m.m22 < 0f) {
			_s.y = -_s.y;
		}
		float cos = (float)Math.cos(_r);
		float sin = (float)Math.sin(_r);
		float x1 = _bbox.getXCenter();
		float y1 = _bbox.getYCenter();
		_t.x = _m.m13 + _s.x * x1 * cos - _s.y * y1 * sin;
		_t.y = _m.m23 + _s.x * x1 * sin + _s.y * y1 * cos;
		_dirty = false;
	}
	
	protected void updateTransform() {
		// Compute the transform matrix
		// T(_t) . R(_r) . S(_s) . T(- _bbox.center)
		float cos = (float)Math.cos(_r);
		float sin = (float)Math.sin(_r);
		float x1 = _bbox.getXCenter();
		float y1 = _bbox.getYCenter();
		_m.m11 = _s.x * cos; _m.m12 = -_s.y * sin; _m.m13 = - _s.x * x1 * cos + _s.y * y1 * sin + _t.x;
		_m.m21 = _s.x * sin; _m.m22 =  _s.y * cos; _m.m23 = - _s.x * x1 * sin - _s.y * y1 * cos + _t.y;
		_dirty = false;
	}
	
	
	public BoundingBox getBoundingBox() {
		return _bbox;
	}
	
	public DrawingModel getModel() {
		return _model;
	}
	
	public void setModel(DrawingModel model) {
		_model = model;
	}
	
	public IAttributeValue getAttribute(Attribute attr) {
		return _attributes.get(attr);
	}
	
	public IAttributeValue setAttribute(Attribute attr, IAttributeValue value) {
		return _attributes.put(attr, value);
	}
	
	public IAttributeValue clearAttribute(Attribute attr) {
		return _attributes.remove(attr);
	}
	
	public Set<Attribute> getDefinedAttributes() {
		return _attributes.keySet();
	}
	
	public boolean definesAttribute(Attribute attr) {
		return _attributes.containsKey(attr);
	}
	
	public void copyAttributes(Shape src) {
		_attributes.clear();
		_attributes.putAll(src._attributes);
	}
	
	public abstract void acceptVisitor(IShapeVisitor visitor);
	
	/**
	 * Returns true if the specified shape has the same
	 * geometrical and styling properties as this shape,
	 * false otherwise.
	 * @param shape
	 * A shape
	 * @return
	 * True if the specified shape has the same
	 * geometrical and styling properties as this shape,
	 * false otherwise.
	 */
	public abstract boolean isSame(Shape shape);

}

