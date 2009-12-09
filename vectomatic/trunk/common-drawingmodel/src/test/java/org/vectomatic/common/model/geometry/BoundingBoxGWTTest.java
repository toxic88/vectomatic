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
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Point;




public class BoundingBoxGWTTest extends GWTTestBase {
	private BoundingBox b1, b2;
	
	@Override
	public void gwtSetUp() {
		b1 = new BoundingBox();
		b2 = new BoundingBox(1f, 2f, 3f, 4f);
	}
	
	public void testConstructor() {
		assertEquals(0f, b1.xmin, 0f);		
		assertEquals(0f, b1.ymin, 0f);		
		assertEquals(0f, b1.xmax, 0f);		
		assertEquals(0f, b1.ymax, 0f);		
		assertEquals(-1f, BoundingBox.UNIT_BOX.xmin, 0f);		
		assertEquals(-1f, BoundingBox.UNIT_BOX.ymin, 0f);		
		assertEquals(1f, BoundingBox.UNIT_BOX.xmax, 0f);		
		assertEquals(1f, BoundingBox.UNIT_BOX.ymax, 0f);		
		assertEquals(1f, b2.xmin, 0f);		
		assertEquals(2f, b2.ymin, 0f);		
		assertEquals(3f, b2.xmax, 0f);		
		assertEquals(4f, b2.ymax, 0f);		
	}
	
	public void testEquals() {
		assertTrue(b2.equals(b2));
		assertFalse(b2.equals(null));
		assertFalse(b2.equals("A"));
		assertFalse(b2.equals(b1));
		BoundingBox bbox = new BoundingBox();
		assertTrue(b1.equals(bbox));
		bbox.xmin = -1f;
		assertFalse(b1.equals(bbox));
		bbox = new BoundingBox();
		bbox.xmax = -1f;
		assertFalse(b1.equals(bbox));
		bbox = new BoundingBox();
		bbox.ymin = -1f;
		assertFalse(b1.equals(bbox));
		bbox = new BoundingBox();
		bbox.ymax = -1f;
		assertFalse(b1.equals(bbox));
	}
	
	public void testHashCode() {
		assertEquals(240f, b2.hashCode(), 0f);
	}
	
	public void testUnion() {
		BoundingBox bbox = new BoundingBox(-1f, -2f, 0f, 0f);
		bbox.union(b1);
		b2.xmin = -1f; b2.ymin = -2f; b2.xmax = 0f; b2.ymax = 0f;
		assertEquals(b2, bbox);
		bbox = new BoundingBox(1f, 1f, 2f, 2f);
		bbox.union(b1);
		b2.xmin = 0f; b2.ymin = 0f; b2.xmax = 2f; b2.ymax = 2f;
		assertEquals(b2, bbox);
		bbox = new BoundingBox(1f, 1f, 2f, 2f);
		bbox.union(b1, b1);
		assertEquals(b2, b1);
	}
	
	public void testGetPoint() {
		Point p1 = new Point();
		Point p2 = new Point();
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_NW, p1);
		p2.x = -1f; p2.y = -1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_NE, p1);
		p2.x = 1f; p2.y = -1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_SW, p1);
		p2.x = -1f; p2.y = 1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_SE, p1);
		p2.x = 1f; p2.y = 1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_N, p1);
		p2.x = 0f; p2.y = -1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_W, p1);
		p2.x = -1f; p2.y = 0f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_E, p1);
		p2.x = 1f; p2.y = 0f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_S, p1);
		p2.x = 0f; p2.y = 1f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_C, p1);
		p2.x = 0f; p2.y = 0f;
		assertEquals(p2, p1);
		BoundingBox.UNIT_BOX.getPoint(BoundingBox.PT_R, p1);
		p2.x = 1.5f; p2.y = 0f;
		assertEquals(p2, p1);
	}
	public void testGetXCenter() {
		assertEquals(2f, b2.getXCenter(), 0f);
	}
	public void testGetYCenter() {
		assertEquals(3f, b2.getYCenter(), 0f);
	}
	public void testGetWidth() {
		assertEquals(2f, b2.getWidth(), 0f);
	}
	public void testGetHeight() {
		assertEquals(2f, BoundingBox.UNIT_BOX.getHeight(), 0f);
	}
	public void testContainsPoint() {
		Point p = new Point();
		assertTrue(BoundingBox.UNIT_BOX.containsPoint(p));
		p.x = -1.1f; p.y = 0f;
		assertFalse(BoundingBox.UNIT_BOX.containsPoint(p));
		p.x = 1.1f; p.y = 0f;
		assertFalse(BoundingBox.UNIT_BOX.containsPoint(p));
		p.x = 0f; p.y = -1.1f;
		assertFalse(BoundingBox.UNIT_BOX.containsPoint(p));
		p.x = 0f; p.y = 1.1f;
		assertFalse(BoundingBox.UNIT_BOX.containsPoint(p));
	}
	public void testToString() {
		String str = b2.toString();
		System.out.println("b2 = " + str);
		BoundingBox bbox = new BoundingBox().parseString(str);
		assertEquals(b2, bbox);
	}
}
