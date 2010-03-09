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
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.style.Color;


public class EllipseGWTTest extends GWTTestBase {
	public void testConstructor() {
		Ellipse ellipse = new Ellipse();
		System.out.println(ellipse);
		assertEquals(BoundingBox.UNIT_BOX, ellipse.getBoundingBox());
	}

	public void testIsSame() {
		Ellipse ellipse1 = new Ellipse();
		Ellipse ellipse2 = new Ellipse();
		assertTrue(ellipse1.isSame(ellipse2));
		
		ellipse2 = new Ellipse();
		ellipse2.setTranslation(new Point(2f, 3f));
		assertFalse(ellipse1.isSame(ellipse2));
		
		ellipse2 = new Ellipse();
		ellipse2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		ellipse2.setTranslation(new Point(0, 0));
		assertFalse(ellipse1.isSame(ellipse2));
		
		ellipse1 = new Ellipse();
		ellipse1.setTranslation(new Point(2f, 3f));
		ellipse1.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		ellipse2 = new Ellipse();
		ellipse2.setTranslation(new Point(2f, 3f));
		ellipse2.setAttribute(Attribute.FILL_STYLE, Color.BLUE);
		assertTrue(ellipse1.isSame(ellipse2));
		
		ellipse1 = new Ellipse();
		Rect rect1 = new Rect();
		assertFalse(ellipse1.isSame(rect1));
	}
}
