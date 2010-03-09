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
 * Class to represent a 2D bounding box
 */
public class BoundingBox implements IsSerializable {
	public static final BoundingBox UNIT_BOX = new BoundingBox(-1f, -1f, 1f, 1f);

	public static final int PT_NW = 0;
	public static final int PT_SW = 1;
	public static final int PT_SE = 2;
	public static final int PT_NE = 3;
	public static final int PT_N = 4;
	public static final int PT_W = 5;
	public static final int PT_S = 6;
	public static final int PT_E = 7;
	public static final int PT_C = 8;
	public static final int PT_R = 9;
	public float xmin, xmax, ymin, ymax;
	
	public BoundingBox() {
	}
	
	public BoundingBox(BoundingBox bbox) {
		this.xmin = bbox.xmin;
		this.ymin = bbox.ymin;
		this.xmax = bbox.xmax;
		this.ymax = bbox.ymax;
	}

	public BoundingBox(float xmin, float ymin, float xmax, float ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BoundingBox) {
			BoundingBox bbox = (BoundingBox)o;
			return (xmin == bbox.xmin) && (xmax == bbox.xmax) && (ymin == bbox.ymin) && (ymax == bbox.ymax);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int)(17 * xmin) + (int)(19 * ymin) + (int)(23 * xmax) + (int)(29 * ymax);
	}

	public BoundingBox union(BoundingBox bbox) {
		return union(bbox, this);
	}
	
	public BoundingBox union(BoundingBox bbox, BoundingBox dest) {
		dest.xmin = Math.min(xmin, bbox.xmin);
		dest.ymin = Math.min(ymin, bbox.ymin);
		dest.xmax = Math.max(xmax, bbox.xmax);
		dest.ymax = Math.max(ymax, bbox.ymax);
		return dest;
	}
	
	public Point getPoint(int pt, Point p) {
		switch (pt) {
			case PT_NW:
				p.x = xmin;
				p.y = ymin;
				break;
			case PT_SW:
				p.x = xmin;
				p.y = ymax;
				break;
			case PT_SE:
				p.x = xmax;
				p.y = ymax;
				break;
			case PT_NE:
				p.x = xmax;
				p.y = ymin;
				break;
			case PT_N:
				p.x = (xmin + xmax) * 0.5f;
				p.y = ymin;
				break;
			case PT_W:
				p.x = xmin;
				p.y = (ymin + ymax) * 0.5f;
				break;
			case PT_S:
				p.x = (xmin + xmax) * 0.5f;
				p.y = ymax;
				break;
			case PT_E:
				p.x = xmax;
				p.y = (ymin + ymax) * 0.5f;
				break;
			case PT_C:
				p.x = (xmin + xmax) * 0.5f;
				p.y = (ymin + ymax) * 0.5f;
				break;
			case PT_R:
				p.x = xmax + 0.25f * getWidth();
				p.y = (ymin + ymax) * 0.5f;
				break;
			}
		return p;
	}
	
	public float getXCenter() {
		return (xmin + xmax) * 0.5f;
	}
	public float getYCenter() {
		return (ymin + ymax) * 0.5f;
	}
	
	public float getWidth() {
		return xmax - xmin;
	}
	public float getHeight() {
		return ymax - ymin;
	}
	
	public boolean containsPoint(Point p) {
		return (p.x >= xmin && p.x <= xmax && p.y >= ymin && p.y <= ymax);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[(");
		buffer.append(xmin);
		buffer.append(" ,");
		buffer.append(ymin);
		buffer.append(")(");
		buffer.append(xmax);
		buffer.append(" ,");
		buffer.append(ymax);
		buffer.append(")]");
		return buffer.toString();
	}
	
	public BoundingBox parseString(String str) {
		int ix1 = 2;
		int ix2 = str.indexOf(' ');
		xmin = Float.parseFloat(str.substring(ix1, ix2));
		ix1 = ix2 + 2;
		ix2 = str.indexOf(')', ix1);
		ymin = Float.parseFloat(str.substring(ix1, ix2));
		ix1 = ix2 + 2;
		ix2 = str.indexOf(' ', ix1);
		xmax = Float.parseFloat(str.substring(ix1, ix2));
		ix1 = ix2 + 2;
		ix2 = str.indexOf(')', ix1);
		ymax = Float.parseFloat(str.substring(ix1, ix2));
		return this;
	}
}
