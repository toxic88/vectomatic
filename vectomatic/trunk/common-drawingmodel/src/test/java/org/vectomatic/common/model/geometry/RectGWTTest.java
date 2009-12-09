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
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.style.Color;


public class RectGWTTest extends GWTTestBase {
	public void testConstructor() {
		Rect rect = new Rect();
		System.out.println(rect);
		assertEquals(BoundingBox.UNIT_BOX, rect.getBoundingBox());
	}
	
	public void testIsSame() {
		Rect rect1 = new Rect();
		Rect rect2 = new Rect();
		assertTrue(rect1.isSame(rect2));
		
		rect2 = new Rect();
		rect2.setTranslation(new Point(2f, 3f));
		assertFalse(rect1.isSame(rect2));
		
		rect2 = new Rect();
		rect2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		rect2.setTranslation(new Point(0, 0));
		assertFalse(rect1.isSame(rect2));
		
		rect1 = new Rect();
		rect1.setTranslation(new Point(2f, 3f));
		rect1.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		rect2 = new Rect();
		rect2.setTranslation(new Point(2f, 3f));
		rect2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		assertTrue(rect1.isSame(rect2));
		
		rect1 = new Rect();
		Ellipse ellipse1 = new Ellipse();
		assertFalse(rect1.isSame(ellipse1));

	}
}
