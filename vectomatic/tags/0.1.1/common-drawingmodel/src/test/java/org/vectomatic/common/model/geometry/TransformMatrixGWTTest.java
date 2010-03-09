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

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;




public class TransformMatrixGWTTest extends GWTTestBase {
	private TransformMatrix m1, m2;
	private static final float TOL = 1E-06f;
	private static final float TOL2 = 2E-06f;
	private static final float TOL3 = 3E-06f;
	
	@Override
	public void gwtSetUp() {
		m1 = new TransformMatrix();
		m2 = new TransformMatrix();
	}

	public void testConstructor() {
		assertEquals(1.0f, m1.m11, 0f);		
		assertEquals(0.0f, m1.m12, 0f);		
		assertEquals(0.0f, m1.m13, 0f);		
		assertEquals(0.0f, m1.m21, 0f);		
		assertEquals(1.0f, m1.m22, 0f);		
		assertEquals(0.0f, m1.m23, 0f);
		m1.m11 = 1f; m1.m12 = 2f; m1.m13 = 3f;
		m1.m21 = 4f; m1.m22 = 5f; m1.m23 = 6f;
		m2 = new TransformMatrix(m1);
		assertEquals(m1, m2);
		m1 = new TransformMatrix(1f, 2f, 3f, 4f, 5f, 6f);
		assertEquals(1.0f, m1.m11, 0f);		
		assertEquals(2.0f, m1.m12, 0f);		
		assertEquals(3.0f, m1.m13, 0f);		
		assertEquals(4.0f, m1.m21, 0f);		
		assertEquals(5.0f, m1.m22, 0f);		
		assertEquals(6.0f, m1.m23, 0f);
	}
	
	public void testEquals() {
		assertFalse(m1.equals(null));
		assertFalse(m1.equals("A"));
		assertTrue(m1.equals(m1));
		assertTrue(m1.equals(m2));
		
		m2.m11 = 2.0f;
		assertFalse(m1.equals(m2));
		m2 = new TransformMatrix();
		m2.m12 = 2.0f;
		assertFalse(m1.equals(m2));
		m2 = new TransformMatrix();
		m2.m13 = 2.0f;
		assertFalse(m1.equals(m2));
		m2 = new TransformMatrix();
		m2.m21 = 2.0f;
		assertFalse(m1.equals(m2));
		m2 = new TransformMatrix();
		m2.m22 = 2.0f;
		assertFalse(m1.equals(m2));
		m2 = new TransformMatrix();
		m2.m23 = 2.0f;
		assertFalse(m1.equals(m2));
		
		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		m2.m11 = 2f;
		m2.m12 = 3f;
		m2.m13 = 4f;
		m2.m21 = 5f;
		m2.m22 = 6f;
		m2.m23 = 7f;
		
		m1.m11 = 2f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m11 = 2f;

		m1.m12 = 3f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m12 = 3f;
		
		m1.m13 = 4f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m13 = 4f;
		
		m1.m21 = 5f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m21 = 5f;

		m1.m22 = 6f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m22 = 6f;

		m1.m23 = 7f + TOL;
		assertTrue(m1.equals(m2, TOL2));
		m1.m23 = 7f;
		
		m1.m11 = 2f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m11 = 2f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m11 = 2f;
		
		m1.m12 = 3f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m12 = 3f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m12 = 3f;

		m1.m13 = 4f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m13 = 4f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m13 = 4f;

		m1.m21 = 5f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m21 = 5f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m21 = 5f;

		m1.m22 = 6f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m22 = 6f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m22 = 6f;

		m1.m23 = 7f + TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m23 = 7f - TOL3;
		assertFalse(m1.equals(m2, TOL2));
		m1.m23 = 7f;
	}
	
	public void testHashCode() {
		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		
		assertEquals(773f, m1.hashCode(), 0f);
	}
	
	public void testCopyTo() {
		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		m1.copyTo(m2);
		assertEquals(m2, m1);
		
		m1.copyTo(m1);
		assertEquals(2.0f, m1.m11, 0f);		
		assertEquals(3.0f, m1.m12, 0f);		
		assertEquals(4.0f, m1.m13, 0f);		
		assertEquals(5.0f, m1.m21, 0f);		
		assertEquals(6.0f, m1.m22, 0f);		
		assertEquals(7.0f, m1.m23, 0f);		
	}
	
	public void testTranslation() {
		m1.translation(2.0f, 3.0f);
		m2.m11 = 1f; m2.m12 = 0f; m2.m13 = 2f;
		m2.m21 = 0f; m2.m22 = 1f; m2.m23 = 3f;
		assertEquals(m2, m1);
		m1.translation(new Point(2.0f, 3.0f));
		m2.m11 = 1f; m2.m12 = 0f; m2.m13 = 2f;
		m2.m21 = 0f; m2.m22 = 1f; m2.m23 = 3f;
		assertEquals(m2, m1);
	}
	
	public void testRotation() {
		float r = (float)(Math.PI / 3.0);
		m1.rotation(r);
		m2.m11 = (float)Math.cos(r); m2.m12 = -(float)Math.sin(r); m2.m13 = 0f;
		m2.m21 = (float)Math.sin(r); m2.m22 =  (float)Math.cos(r); m2.m23 = 0f;
		assertEquals(m2, m1);
	}
	
	public void testScaling() {
		m1.scaling(2.0f, 3.0f);
		m2.m11 = 2f; m2.m12 = 0f; m2.m13 = 0f;
		m2.m21 = 0f; m2.m22 = 3f; m2.m23 = 0f;
		assertEquals(m2, m1);
		m1.scaling(new Point(2.0f, 3.0f));
		m2.m11 = 2f; m2.m12 = 0f; m2.m13 = 0f;
		m2.m21 = 0f; m2.m22 = 3f; m2.m23 = 0f;
		assertEquals(m2, m1);
	}
	
	public void testPreMultiply() {
		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		m1.preMultiply(m1);
		m2.m11 = 19f;
		m2.m12 = 24f;
		m2.m13 = 33f;
		m2.m21 = 40f;
		m2.m22 = 51f;
		m2.m23 = 69f;
		assertEquals(m2, m1);

		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		m2.m11 = 1f;
		m2.m12 = 2f;
		m2.m13 = 3f;
		m2.m21 = 4f;
		m2.m22 = 5f;
		m2.m23 = 6f;
		m1.preMultiply(m2);
		m2.m11 = 12f;
		m2.m12 = 15f;
		m2.m13 = 21f;
		m2.m21 = 33f;
		m2.m22 = 42f;
		m2.m23 = 57f;
		assertEquals(m2, m1);
		
		m1.m11 = 2f;
		m1.m12 = 3f;
		m1.m13 = 4f;
		m1.m21 = 5f;
		m1.m22 = 6f;
		m1.m23 = 7f;
		m2.m11 = 1f;
		m2.m12 = 2f;
		m2.m13 = 3f;
		m2.m21 = 4f;
		m2.m22 = 5f;
		m2.m23 = 6f;
		m1.preMultiply(m2, m2);
		m1.m11 = 12f;
		m1.m12 = 15f;
		m1.m13 = 21f;
		m1.m21 = 33f;
		m1.m22 = 42f;
		m1.m23 = 57f;
		assertEquals(m1, m2);
	}
	
	public void testToString() {
		m1.m11 = 1f;
		m1.m12 = 2f;
		m1.m13 = 3f;
		m1.m21 = 4f;
		m1.m22 = 5f;
		m1.m23 = 6f;
		String str = m1.toString();
		System.out.println("m1 = " + str);
		TransformMatrix m = new TransformMatrix().parseString(str);
		assertEquals(m1, m);
	}
	
	public void testInvert() {
		m1.translation(2.f, 3.f);
		m1.preMultiply(m2.rotation((float)(Math.PI / 3)));
		m1.preMultiply(m2.scaling(0.2f, 4.0f));
		m1.preMultiply(m2.translation(4.4f, 2.2f));
		System.out.println(m1.toString());
		m1.invert(m2);
		System.out.println(m2.toString());
		m1.preMultiply(m2, m1);
		System.out.println(m1.toString());
		assertEquals(1.0f, m1.m11, TOL);
		assertEquals(0.0f, m1.m12, TOL);
		assertEquals(0.0f, m1.m13, TOL);
		assertEquals(0.0f, m1.m21, TOL);
		assertEquals(1.0f, m1.m22, TOL);
		assertEquals(0.0f, m1.m23, TOL);
		m1.m11 = 0.0f;
		m1.m12 = 0.0f;
		m1.m13 = 0.0f;
		m1.m21 = 0.0f;
		m1.m22 = 0.0f;
		m1.m23 = 0.0f;
		assertNull(m1.invert());
		m1.m11 = 31.5f;
		m1.m12 = -0.0f;
		m1.m13 = 132.5f;
		m1.m21 = 0.0f;
		m1.m22 = 35.0f;
		m1.m23 = 130.0f;
		m1.invert(m2);
		System.out.println("m2=" + m2.toString());
		m1.preMultiply(m2, m1);
		System.out.println("m1=" + m1.toString());
		
	}
}
