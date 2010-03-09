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
package org.vectomatic.common.model.style;

import org.vectomatic.common.GWTTestBase;
import org.vectomatic.common.model.style.Color;


public class ColorGWTTest extends GWTTestBase {

	public void testConstructor() {
		Color c1 = new Color();
		assertEquals(Color.HSV, c1.getSpace());
		assertEquals(Color.HSV, c1.getSpace());
		assertEquals(0, c1.getChannel(Color.H));
		assertEquals(0, c1.getChannel(Color.S));
		assertEquals(0, c1.getChannel(Color.V));
		
		Color c2 = Color.fromHSV(1, 2, 3);
		assertEquals(Color.HSV, c2.getSpace());
		assertEquals(1, c2.getChannel(Color.H));
		assertEquals(2, c2.getChannel(Color.S));
		assertEquals(3, c2.getChannel(Color.V));
		
		Color c3 = Color.fromRGB(1, 2, 3);
		assertEquals(Color.RGB, c3.getSpace());
		assertEquals(1, c3.getChannel(Color.R));
		assertEquals(2, c3.getChannel(Color.G));
		assertEquals(3, c3.getChannel(Color.B));
		
		Color c4 = new Color(c3);
		assertEquals(Color.RGB, c4.getSpace());
		assertEquals(1, c4.getChannel(Color.R));
		assertEquals(2, c4.getChannel(Color.G));
		assertEquals(3, c4.getChannel(Color.B));
	}
	
	public void testEquals() {
		Color c1 = Color.fromRGB(1, 2, 3);
		Color c2 = Color.fromRGB(1, 2, 3);
		assertTrue(c1.equals(c2));
		c2 = Color.fromRGB(0, 2, 3);
		assertFalse(c1.equals(c2));
		c2 = Color.fromRGB(1, 0, 3);
		assertFalse(c1.equals(c2));
		c2 = Color.fromRGB(1, 2, 0);
		assertFalse(c1.equals(c2));
		c2 = Color.fromHSV(1, 2, 3);
		assertFalse(c1.equals(c2));
	}
	
	public void testHashCode() {
		Color c1 = Color.fromRGB(1, 2, 3);
		Color c2 = Color.fromRGB(1, 2, 3);
		assertTrue(c1.hashCode() == c2.hashCode());
		c2 = Color.fromRGB(0, 2, 3);
		assertTrue(c1.hashCode() != c2.hashCode());
		c2 = Color.fromRGB(1, 0, 3);
		assertTrue(c1.hashCode() != c2.hashCode());
		c2 = Color.fromRGB(1, 2, 0);
		assertTrue(c1.hashCode() != c2.hashCode());
		c2 = Color.fromHSV(1, 2, 3);
		assertTrue(c1.hashCode() != c2.hashCode());
	}
	
	public void testChannel() {
		Color c1 = Color.fromRGB(1, 2, 3);
		assertEquals(1, c1.getChannel(Color.R));
		assertEquals(2, c1.getChannel(Color.G));
		assertEquals(3, c1.getChannel(Color.B));
		c1.setChannel(Color.R, 10);
		c1.setChannel(Color.G, 20);
		c1.setChannel(Color.B, 30);
		assertEquals(10, c1.getChannel(Color.R));
		assertEquals(20, c1.getChannel(Color.G));
		assertEquals(30, c1.getChannel(Color.B));

		c1 = Color.fromHSV(1, 2, 3);
		assertEquals(1, c1.getChannel(Color.H));
		assertEquals(2, c1.getChannel(Color.S));
		assertEquals(3, c1.getChannel(Color.V));
		c1.setChannel(Color.H, 10);
		c1.setChannel(Color.S, 20);
		c1.setChannel(Color.V, 30);
		assertEquals(10, c1.getChannel(Color.H));
		assertEquals(20, c1.getChannel(Color.S));
		assertEquals(30, c1.getChannel(Color.V));

		assertEquals(255, Color.getChannelMax(Color.RGB, Color.R));
		assertEquals(255, Color.getChannelMax(Color.RGB, Color.G));
		assertEquals(255, Color.getChannelMax(Color.RGB, Color.B));
		assertEquals(359, Color.getChannelMax(Color.HSV, Color.H));
		assertEquals(100, Color.getChannelMax(Color.HSV, Color.S));
		assertEquals(100, Color.getChannelMax(Color.HSV, Color.V));

		assertEquals("R", Color.getChannelName(Color.RGB, Color.R));
		assertEquals("G", Color.getChannelName(Color.RGB, Color.G));
		assertEquals("B", Color.getChannelName(Color.RGB, Color.B));
		assertEquals("H", Color.getChannelName(Color.HSV, Color.H));
		assertEquals("S", Color.getChannelName(Color.HSV, Color.S));
		assertEquals("V", Color.getChannelName(Color.HSV, Color.V));
		
		c1 = Color.fromRGB(1, 2, 3);
		assertEquals(Color.RGB, c1.getSpace());
		c1.setSpace(Color.HSV);
		assertEquals(Color.HSV, c1.getSpace());	
	}
	
	public void testConvertToColorSpace() {
		
	}
}
