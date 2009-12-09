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

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.geometry.ShapeGroup;
import org.vectomatic.common.model.style.Color;


public class ShapeGroupGWTTest extends GWTTestBase {
	public void testConstructor() {
		Rect rect = new Rect();
		Ellipse ellipse = new Ellipse();
		ellipse.setTranslation(new Point(2f, 3f));
		List<Shape> shapeList = new ArrayList<Shape>();
		shapeList.add(rect);
		shapeList.add(ellipse);
		ShapeGroup group = new ShapeGroup(shapeList);
		System.out.println(group);
	}
	public void testIsSame() {
		Rect rect1 = new Rect();
		Ellipse ellipse1 = new Ellipse();
		ellipse1.setTranslation(new Point(2f, 3f));
		List<Shape> shapeList1 = new ArrayList<Shape>();
		shapeList1.add(rect1);
		shapeList1.add(ellipse1);

		ShapeGroup group1 = new ShapeGroup(shapeList1);
		ShapeGroup group2 = new ShapeGroup(shapeList1);
		assertTrue(group1.isSame(group2));
		
		group2 = new ShapeGroup(shapeList1);
		group2.setTranslation(new Point(2f, 3f));
		assertFalse(group1.isSame(group2));
		
		group2 = new ShapeGroup(shapeList1);
		group2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		group2.setTranslation(new Point(0, 0));
		assertFalse(group1.isSame(group2));
		
		group1 = new ShapeGroup(shapeList1);
		group1.setTranslation(new Point(2f, 3f));
		group1.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		group2 = new ShapeGroup(shapeList1);
		group2.setTranslation(new Point(2f, 3f));
		group2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		assertTrue(group1.isSame(group2));
		
		group1 = new ShapeGroup(shapeList1);
		Ellipse ellipse2 = new Ellipse();
		assertFalse(group1.isSame(ellipse2));
		
		List<Shape> shapeList2 = new ArrayList<Shape>();
		shapeList2.add(ellipse1);
		shapeList2.add(ellipse2);
		group1 = new ShapeGroup(shapeList1);
		group2 = new ShapeGroup(shapeList2);
		assertFalse(group1.isSame(ellipse2));
	}
	
}
