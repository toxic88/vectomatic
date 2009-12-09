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

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;




public class PointGWTTest extends GWTTestBase {
	private Point p1, p2;

	@Override
	public void gwtSetUp() {
		p1 = new Point();
		p2 = new Point(1.0f, 2.0f);
	}
	public void testConstructor() {
		assertEquals(0.0f, p1.x, 0.0f);
		assertEquals(0.0f, p1.y, 0.0f);
		assertEquals(1.0f, p2.x, 0.0f);
		assertEquals(2.0f, p2.y, 0.0f);
		assertEquals(0.0f, Point.ZERO.x, 0.0f);
		assertEquals(0.0f, Point.ZERO.y, 0.0f);
		assertEquals(1.0f, Point.UNIT.x, 0.0f);
		assertEquals(1.0f, Point.UNIT.y, 0.0f);
		Point p3 = new Point(p2);
		assertEquals(p2, p3);
	}
	public void testEquals() {
		assertTrue(p1.equals(p1));
		assertFalse(p1.equals(null));
		assertFalse(p1.equals("A"));
		assertFalse(p1.equals(p2));
		p2 = new Point();
		assertTrue(p2.equals(p1));
		p2.x = 1.0f;
		assertFalse(p2.equals(p1));
		p2 = new Point();
		p2.y = 1.0f;
		assertFalse(p2.equals(p1));
	}

	public void testCopyTo() {
		p2.copyTo(p1);
		assertEquals(p2, p1);
		p2.copyTo(p2);
		assertEquals(1.0f, p2.x, 0.0f);
		assertEquals(2.0f, p2.y, 0.0f);
	}
	
	public void testHashCode() {
		assertEquals(75, p2.hashCode());
	}
	
	public void testAdd() {
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.add(p1);
		p2.x = 6.0f; p2.y = 8.0f;
		assertEquals(p2, p1);
		p1.x = 3.0f;
		p1.y = 4.0f;
		p2.x = 1.0f;
		p2.y = 2.0f;
		p1.add(p2);
		p2.x = 4.0f; p2.y = 6.0f;
		assertEquals(p2, p1);
		p1.x = 3.0f;
		p1.y = 4.0f;
		p2.x = 1.0f;
		p2.y = 2.0f;
		p1.add(p2, p2);
		p1.x = 4.0f; p1.y = 6.0f;
		assertEquals(p1, p2);
	}
	
	public void testSubtract() {
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.subtract(p1);
		p2.x = 0.0f; p2.y = 0.0f;
		assertEquals(p2, p1);
		p1.x = 3.0f;
		p1.y = 4.0f;
		p2.x = 1.0f;
		p2.y = 2.0f;
		p1.subtract(p2);
		p2.x = 2.0f; p2.y = 2.0f;
		assertEquals(p2, p1);
		p1.x = 3.0f;
		p1.y = 4.0f;
		p2.x = 1.0f;
		p2.y = 2.0f;
		p1.subtract(p2, p2);
		p1.x = 2.0f; p1.y = 2.0f;
		assertEquals(p1, p2);
	}

	public void testDivide() {
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.divide(p1);
		p2.x = 1.0f; p2.y = 1.0f;
		assertEquals(p2, p1);
		p1.x = 4.0f;
		p1.y = 6.0f;
		p2.x = 2.0f;
		p2.y = 6.0f;
		p1.divide(p2);
		p2.x = 2.0f; p2.y = 1.0f;
		assertEquals(p2, p1);
		p1.x = 4.0f;
		p1.y = 6.0f;
		p2.x = 2.0f;
		p2.y = 6.0f;
		p1.divide(p2, p2);
		p1.x = 2.0f; p1.y = 1.0f;
		assertEquals(p1, p2);
	}
	
	public void testMultiply() {
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.multiply(2f);
		p2.x = 6.0f; p2.y = 8.0f;
		assertEquals(p2, p1);
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.multiply(2f, p2);
		p1.x = 6.0f; p1.y = 8.0f;
		assertEquals(p1, p2);
	}

	public void testNegate() {
		p1.x = 3.0f;
		p1.y = 4.0f;
		p1.negate();
		p2.x = -3.0f; p2.y = -4.0f;
		assertEquals(p2, p1);
		p1.negate(p2);
		p1.x = 3.0f; p1.y = 4.0f;
		assertEquals(p1, p2);
	}
	
	public void testTransform() {
		TransformMatrix m = new TransformMatrix();
		m.m11 = 2f;
		m.m12 = 3f;
		m.m13 = 4f;
		m.m21 = 5f;
		m.m22 = 6f;
		m.m23 = 7f;
		p2.transform(m);
		p1.x = 12f ; p1.y = 24.f;
		assertEquals(p1, p2);
		p2.x = 1f; p2.y = 2f;
		p2.transform(m, p1);
		p2.x = 12f ; p2.y = 24.f;
		assertEquals(p2, p1);
	}
	
	public void testLength() {
		assertEquals((float)Math.sqrt(5.0f), p2.length(), 0.0f);
	}
	
	public void testSquaredLength() {
		assertEquals(5.0f, p2.squaredLength(), 0.0f);
	}
	
	public void testToString() {
		String str = p2.toString();
		System.out.println("p2 = " + str);
		Point p = new Point().parseString(str);
		assertEquals(p2, p);
	}
}
