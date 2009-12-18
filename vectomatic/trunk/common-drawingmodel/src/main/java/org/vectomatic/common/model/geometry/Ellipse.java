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
 * Class to represent an ellipse
 */
public class Ellipse extends Shape implements IsSerializable {
	public static final float K = ((float)Math.sqrt(2) - 1) * 4 / 3;
	public Ellipse() {
		super();
		_bbox = BoundingBox.UNIT_BOX;		
	}
	public Ellipse(Ellipse ellipse) {
		super(ellipse);
	}
	@Override
	public void acceptVisitor(IShapeVisitor visitor) {
		visitor.visitEllipse(this);
	}
	@Override
	public boolean isSame(Shape shape) {
		if (shape instanceof Ellipse) {
			Ellipse ellipse = (Ellipse)shape;
			return getTransform().equals(ellipse.getTransform()) 
				&& _attributes.equals(ellipse._attributes);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Ellipse(m=");
		buffer.append(getTransform().toString());
		buffer.append(" atts=");
		buffer.append(_attributes.toString());
		buffer.append(")");
		return buffer.toString();
	}
}
