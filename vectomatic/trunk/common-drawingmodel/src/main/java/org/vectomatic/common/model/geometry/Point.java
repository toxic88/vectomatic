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
 * Class to represent a 2D point
 */
public class Point implements IsSerializable {
	public static final Point UNIT = new Point(1f, 1f);
	public static final Point ZERO = new Point();
	public float x;
	public float y;
	
	public Point() {
	}
	
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point)o;
			return x == p.x && y == p.y;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		//return Float.floatToIntBits(x) + 37 * Float.floatToIntBits(y);
		return (int)x + 37 * (int)y;
	}

	public Point copyTo(Point dest) {
		dest.x = this.x;
		dest.y = this.y;
		return dest;
	}

	public Point add(Point p) {
		return add(p, this);
	}

	public Point add(Point p, Point dest) {
		dest.x = x + p.x;
		dest.y = y + p.y;
		return dest;
	}

	public Point subtract(Point p) {
		return subtract(p, this);
	}

	public Point subtract(Point p, Point dest) {
		dest.x = x - p.x;
		dest.y = y - p.y;
		return dest;
	}

	public Point divide(Point p) {
		return divide(p, this);
	}

	public Point divide(Point p, Point dest) {
		dest.x = x / p.x;
		dest.y = y / p.y;
		return dest;
	}
	
	public Point multiply(float f) {
		return multiply(f, this);
	}
	
	public Point multiply(float f, Point dest) {
		dest.x = f * x;
		dest.y = f * y;
		return dest;
	}
	
	public Point negate() {
		return negate(this);
	}

	public Point negate(Point dest) {
		dest.x = -x;
		dest.y = -y;
		return dest;
	}

	public float squaredLength() {
		return x * x + y * y;
	}

	public float length() {
		return (float)Math.sqrt(squaredLength());
	}
	
	public Point transform(TransformMatrix t) {
		return transform(t, this);
	}

	public Point transform(TransformMatrix t, Point dest) {
		float px = t.m11 * x + t.m12 * y + t.m13;
		float py = t.m21 * x + t.m22 * y + t.m23;
		dest.x = px;
		dest.y = py;
		return dest;
	}
	
	public Point swap(Point p) {
		float px = p.x;
		float py = p.y;
		p.x = x;
		p.y = y;
		x = px;
		y = py;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public Point parseString(String str) {
		x = Float.parseFloat(str.substring(1, str.indexOf(',')));
		y = Float.parseFloat(str.substring(2 + str.indexOf(','), str.length() - 1));
		return this;
	}
	
	public float distance(Point p) {
		// TODO: Junit
		return (float)Math.sqrt(squareDistance(p));
	}
	public float squareDistance(Point p) {
		// TODO: Junit
		return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y);
	}
	public Point symetricPoint(Point p) {
		// TODO: Junit
		return symetricPoint(p, this);
	}
	public Point symetricPoint(Point p, Point dest) {
		// TODO: Junit
		dest.x = 2 * p.x - x;
		dest.y = 2 * p.y - y;
		return dest;
	}
	
	public float dotProduct(Point p) {
		// TODO: Junit
		return x * p.x + y * p.y;
	}

}
