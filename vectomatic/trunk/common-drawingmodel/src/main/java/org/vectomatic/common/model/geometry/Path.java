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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a path. A path is made of
 * a list of line segments and Bezier spline segments
 */
public class Path extends Shape implements IsSerializable {
	private List<Segment> _segments;
	
	public Path() {
		// For GWT serialization
	}

	public Path(List<Segment> segments) {
		super();
		_bbox = new BoundingBox();
		_segments = new ArrayList<Segment>(segments);
		_updateBoundingBox();
	}

	public Path(Path path) {
		super(path);
		_segments = new ArrayList<Segment>();
		List<Segment> segments = path.getSegments();
		for (int i = 0, size = segments.size(); i < size; i++) {
			_segments.add(segments.get(i).clone());
		}
	}

	@Override
	public void acceptVisitor(IShapeVisitor visitor) {
		visitor.visitPath(this);
	}

	@Override
	public boolean isSame(Shape shape) {
		if (shape instanceof Path) {
			Path path = (Path)shape;
			return getTransform().equals(path.getTransform()) 
				&& _attributes.equals(path._attributes)
				&& _segments.equals(path._segments);
		}
		return false;
	}

	public void addSegment(Segment segment) {
		_segments.add(segment);
	}
	
	public List<Segment> getSegments() {
		return _segments;
	}
	
	public boolean isClosed() {
		if (_segments.size() >= 2) {
			return _segments.get(0).getStartPoint().equals(_segments.get(_segments.size() - 1).getEndPoint());
		}
		return false;
	}
	
	private void _updateBoundingBox() {
		_bbox = new BoundingBox(_segments.get(0).getBoundingBox());
		for (int i = 1, size = _segments.size(); i < size; i++) {
			_bbox.union(_segments.get(i).getBoundingBox());
		}
		_bbox.getPoint(BoundingBox.PT_C, _t);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Path(m=");
		buffer.append(getTransform().toString());
		buffer.append(" bbox=");
		buffer.append(_bbox.toString());
		buffer.append(" segments=");
		buffer.append(_segments);
		buffer.append(" atts=");
		buffer.append(_attributes.toString());
		buffer.append(")");
		return buffer.toString();
	}
}
