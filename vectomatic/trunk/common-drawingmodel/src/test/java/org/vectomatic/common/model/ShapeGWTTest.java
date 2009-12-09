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
package org.vectomatic.common.model;

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;




public class ShapeGWTTest extends GWTTestBase {
	private static final float TOL = 1E-06f;
	private TestShape s1, s2;
	private Point p1;
	private BoundingBox bbox1;
	

	private static class TestShape extends Shape {
		public TestShape(BoundingBox bbox) {
			super();
			_bbox = bbox;
		}

		@Override
		public void acceptVisitor(IShapeVisitor visitor) {
		}

		@Override
		public boolean isSame(Shape shape) {
			return false;
		}
	}
	
	@Override
	public void gwtSetUp() {
		bbox1 = new BoundingBox(0f, 1f, 2f, 3f);
		s1 = new TestShape(bbox1);
		s2 = new TestShape(bbox1);
		p1 = new Point();
	}
	
	public void testConstructor() {
		System.out.println("testConstructor");
		System.out.println(s1.getTransform());
		assertEquals(0f, s1.getRotation(), 0.0f);
		assertEquals(Point.UNIT, s1.getScaling(p1));
		assertEquals(Point.ZERO, s1.getTranslation(p1));
		assertEquals(bbox1, s1.getBoundingBox());
		assertEquals(new TransformMatrix(1f, 0f, -1f, 0f, 1f, -2f), s1.getTransform());
	}
	
	public void testRotation() {
		s1.setRotation((float)Math.PI / 2f);
		System.out.println("testRotation");
		System.out.println(s1.getTransform());
		assertEquals((float)Math.PI / 2f, s1.getRotation(), 0.0f);
		TransformMatrix m1 = new TransformMatrix();
		m1.rotation((float)Math.PI / 2f);
		System.out.println("-------------------------\nm1=");
		System.out.println(m1);
		TransformMatrix m2 = new TransformMatrix();
		m2.translation(-bbox1.getXCenter(), -bbox1.getYCenter());
		System.out.println("-------------------------\nm2=");
		System.out.println(m2);
		m2.preMultiply(m1);
		System.out.println("-------------------------\nm1 * m2");
		System.out.println(m2);
		assertTrue(m2.equals(s1.getTransform(), TOL));
	}
	
	public void testScaling() {
		s1.setScaling(new Point(2f, 3f));
		System.out.println("testScaling");
		System.out.println(s1.getTransform());
		assertEquals(new Point(2f, 3f), s1.getScaling(p1));
		TransformMatrix m1 = new TransformMatrix();
		m1.scaling(2f, 3f);
		System.out.println("-------------------------\nm1=");
		System.out.println(m1);
		TransformMatrix m2 = new TransformMatrix();
		m2.translation(-bbox1.getXCenter(), -bbox1.getYCenter());
		System.out.println("-------------------------\nm2=");
		System.out.println(m2);
		m2.preMultiply(m1);
		System.out.println("-------------------------\nm1 * m2");
		System.out.println(m2);
		assertTrue(m2.equals(s1.getTransform(), TOL));
	}
	
	public void testTranslation() {
		s1.setTranslation(new Point(2f, 3f));
		System.out.println("testTranslation");
		System.out.println(s1.getTransform());
		assertEquals(new Point(2f, 3f), s1.getTranslation(p1));
		TransformMatrix m1 = new TransformMatrix();
		m1.translation(2f, 3f);
		System.out.println("-------------------------\nm1=");
		System.out.println(m1);
		TransformMatrix m2 = new TransformMatrix();
		m2.translation(-bbox1.getXCenter(), -bbox1.getYCenter());
		System.out.println("-------------------------\nm2=");
		System.out.println(m2);
		m2.preMultiply(m1);
		System.out.println("-------------------------");
		System.out.println(m2);
		assertTrue(m2.equals(s1.getTransform(), TOL));
	}
	
	public void testSetTransform() {
		s1.setRotation((float)Math.PI / 3f);
		s1.setTranslation(new Point(4f, 5f));
		s1.setScaling(new Point(1.1f, 0.3f));
		s2.setTransform(s1.getTransform());
		float r = s2.getRotation();
		assertEquals((float)Math.PI / 3f, r, TOL);
		s2.getTranslation(p1);
		assertEquals(4f, p1.x, TOL);
		assertEquals(5f, p1.y, TOL);
		s2.getScaling(p1);
		assertEquals(1.1f, p1.x, TOL);
		assertEquals(0.3f, p1.y, TOL);

		s1.setRotation(4f * (float)Math.PI / 3f);
		s2.setTransform(s1.getTransform());
		r = s2.getRotation();
		assertEquals(4f * (float)Math.PI / 3f, r, TOL);
		
		s1.setRotation((float)Math.PI / 3f);
		s1.setScaling(new Point(-1.1f, 0.3f));
		s2.setTransform(s1.getTransform());
		s2.getScaling(p1);
		assertEquals(1.1f, p1.x, TOL);
		assertEquals(-0.3f, p1.y, TOL);
		r = s2.getRotation();
		assertEquals(4 * (float)Math.PI / 3f, r, TOL);
	}
}
