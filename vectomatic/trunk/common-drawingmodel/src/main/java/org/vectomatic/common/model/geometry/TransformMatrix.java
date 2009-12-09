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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent 2D transforms
 */
public class TransformMatrix implements IsSerializable {
	public float m11, m12, m13, m21, m22, m23;
	
	public TransformMatrix() {
		m11 = m22 = 1f;
	}
	
	public TransformMatrix(TransformMatrix m) {
		m11 = m.m11; m12 = m.m12; m13 = m.m13;
		m21 = m.m21; m22 = m.m22; m23 = m.m23;
	}
	public TransformMatrix(float m11, float m12, float m13, float m21, float m22, float m23) {
		this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m21 = m21; this.m22 = m22; this.m23 = m23;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TransformMatrix) {
			TransformMatrix t = (TransformMatrix)o;
			return (m11 == t.m11) && (m12 == t.m12) && (m13 == t.m13) 
			    && (m21 == t.m21) && (m22 == t.m22) && (m23 == t.m23);
		}
		return false;
	}
	
	public boolean equals(TransformMatrix m, float tol) {
		return (m11 - tol < m.m11) && (m11 + tol > m.m11)
		 && (m12 - tol < m.m12) && (m12 + tol > m.m12)
		 && (m13 - tol < m.m13) && (m13 + tol > m.m13)
		 && (m21 - tol < m.m21) && (m21 + tol > m.m21)
		 && (m22 - tol < m.m22) && (m22 + tol > m.m22)
		 && (m23 - tol < m.m23) && (m23 + tol > m.m23);
	}
	
	@Override
	public int hashCode() {
		return (int)(17 * m11) + (int)(19 * m12) + (int)(23 * m13)
		     + (int)(29 * m21) + (int)(31 * m22) + (int)(37 * m23);
	}
	
	public TransformMatrix copyTo(TransformMatrix dest) {
		dest.m11 = m11; dest.m12 = m12; dest.m13 = m13;
		dest.m21 = m21; dest.m22 = m22; dest.m23 = m23;
		return dest;
	}
	
	public TransformMatrix rotation(float r) {
		m11 = (float)Math.cos(r); m12 = -(float)Math.sin(r); m13 = 0f;
		m21 = (float)Math.sin(r); m22 = (float)Math.cos(r);  m23 = 0f;
		return this;
	}

	public TransformMatrix scaling(Point p) {
		return scaling(p.x, p.y);
	}

	public TransformMatrix scaling(float x, float y) {
		m11 = x;  m12 = 0f; m13 = 0f;
		m21 = 0f; m22 = y;  m23 = 0f;
		return this;
	}

	public TransformMatrix translation(Point p) {
		return translation(p.x, p.y);
	}
	
	public TransformMatrix translation(float x, float y) {
		m11 = 1f; m12 = 0f; m13 = x;
		m21 = 0f; m22 = 1f; m23 = y;
		return this;
	}

	public TransformMatrix preMultiply(TransformMatrix t) {
		return preMultiply(t, this);
	}
	
	public TransformMatrix preMultiply(TransformMatrix t, TransformMatrix dest) {
		float n11 = t.m11 * m11 + t.m12 * m21;
		float n21 = t.m21 * m11 + t.m22 * m21;
		float n12 = t.m11 * m12 + t.m12 * m22;
		float n22 = t.m21 * m12 + t.m22 * m22;
		float n13 = t.m11 * m13 + t.m12 * m23 + t.m13;
		float n23 = t.m21 * m13 + t.m22 * m23 + t.m23;
		dest.m11 = n11; dest.m12 = n12; dest.m13 = n13;
		dest.m21 = n21; dest.m22 = n22; dest.m23 = n23;
		return dest;
	}
	
	public TransformMatrix invert() {
		return invert(this);
	}
	
	public TransformMatrix invert(TransformMatrix dest) {
		float d = m11 * m22 - m12 * m21;
		if (d == 0f)  {
			return null;
		}
		d = 1f / d;
		float n13 = d * (m12 * m23 - m22 * m13);
		float n23 = d * (m13 * m21 - m11 * m23);
		dest.m11 =  d * m22; dest.m12 = -d * m12; dest.m13 = n13;
		dest.m21 = -d * m21; dest.m22 =  d * m11; dest.m23 = n23;
		return dest;
	}
		
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(m11);
		buffer.append(" ");
		buffer.append(m12);
		buffer.append(" ");
		buffer.append(m13);
		buffer.append(" ");
		buffer.append(m21);
		buffer.append(" ");
		buffer.append(m22);
		buffer.append(" ");
		buffer.append(m23);
		buffer.append("]");
		return buffer.toString();
	}
	
	public TransformMatrix parseString(String str) {
		String[] strArray = str.substring(1, str.length() - 1).split(" ");
		m11 = Float.parseFloat(strArray[0]); m12 = Float.parseFloat(strArray[1]); m13 = Float.parseFloat(strArray[2]);
		m21 = Float.parseFloat(strArray[3]); m22 = Float.parseFloat(strArray[4]); m23 = Float.parseFloat(strArray[5]);
		return this;
	}
}
