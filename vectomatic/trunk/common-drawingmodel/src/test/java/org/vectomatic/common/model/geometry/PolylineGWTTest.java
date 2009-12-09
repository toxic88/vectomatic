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

import java.util.Arrays;

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.style.Color;


public class PolylineGWTTest extends GWTTestBase {
	public void testConstructor() {
		Point[] pointArray = new Point[4];
		pointArray[0] = new Point(0f, 0f);
		pointArray[1] = new Point(1f, 0f);
		pointArray[2] = new Point(1f, 1f);
		pointArray[3] = pointArray[0];
		Polyline polyline = new Polyline(pointArray, pointArray.length);
		System.out.println(polyline);
		assertTrue(Arrays.equals(polyline.getVertices(), pointArray));
	}

	public void testIsSame() {
		Point[] pointArray1 = new Point[4];
		pointArray1[0] = new Point(0f, 0f);
		pointArray1[1] = new Point(1f, 0f);
		pointArray1[2] = new Point(1f, 1f);
		pointArray1[3] = pointArray1[0];
		Point[] pointArray2 = new Point[4];
		pointArray2[0] = new Point(0f, 0f);
		pointArray2[1] = new Point(1f, 0f);
		pointArray2[2] = new Point(1f, 2f);
		pointArray2[3] = pointArray1[0];
		Point[] pointArray3 = new Point[3];
		pointArray3[0] = new Point(0f, 0f);
		pointArray3[1] = new Point(1f, 0f);
		pointArray3[2] = new Point(1f, 1f);
		Polyline polyline1 = new Polyline(pointArray1, pointArray1.length);
		Polyline polyline2 = new Polyline(pointArray1, pointArray1.length);
		assertTrue(polyline1.isSame(polyline2));
		
		polyline2 = new Polyline(pointArray1, pointArray1.length);
		polyline2.setTranslation(new Point(2f, 3f));
		assertFalse(polyline1.isSame(polyline2));
		
		polyline2 = new Polyline(pointArray1, pointArray1.length);
		polyline2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		polyline2.setTranslation(new Point(0, 0));
		assertFalse(polyline1.isSame(polyline2));
		
		polyline1 = new Polyline(pointArray1, pointArray1.length);
		polyline1.setTranslation(new Point(2f, 3f));
		polyline1.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		polyline2 = new Polyline(pointArray1, pointArray1.length);
		polyline2.setTranslation(new Point(2f, 3f));
		polyline2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		assertTrue(polyline1.isSame(polyline2));
		
		polyline1 = new Polyline(pointArray1, pointArray1.length);
		polyline2 = new Polyline(pointArray2, pointArray2.length);
		assertFalse(polyline1.isSame(polyline2));

		polyline1 = new Polyline(pointArray1, pointArray1.length);
		polyline2 = new Polyline(pointArray3, pointArray3.length);
		assertFalse(polyline1.isSame(polyline2));
		
		polyline1 = new Polyline(pointArray1, pointArray1.length);
		Ellipse ellipse1 = new Ellipse();
		assertFalse(polyline1.isSame(ellipse1));

	}

}
