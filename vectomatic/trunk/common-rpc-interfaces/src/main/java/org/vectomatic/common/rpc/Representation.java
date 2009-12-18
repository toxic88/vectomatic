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
package org.vectomatic.common.rpc;

import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.style.PaletteList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Representation implements IsSerializable {
	private int _width;						// WIDTH
	private int _height;					// HEIGHT
	private String _description;			// DESCRIPTION
	private String _name;
	private Shape[] _shapeArray;
	private PaletteList _palettes;
	public static final int DEFAULT_DRAWING_WIDTH = 640;
	public static final int DEFAULT_DRAWING_HEIGHT = 400;

	///////////////////////////////////////
	// Constructors
	///////////////////////////////////////
	
	public Representation() {
	}
	
	public Representation(int width, int height, String description) {
		_width = width;
		_height = height;
		_description = description;
	}
	
	///////////////////////////////////////
	// Persistent Properties
	///////////////////////////////////////
	public void setWidth(int width) {
		_width = width;
	}
	public int getWidth() {
		return _width;
	}
	public void setHeight(int height) {
		_height = height;
	}
	public int getHeight() {
		return _height;
	}
	public void setDescription(String description) {
		_description = description;
	}
	public String getDescription() {
		return _description;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}

	///////////////////////////////////////
	// Non Persistent Properties
	///////////////////////////////////////
	
	public void setShapes(Shape[] shapeArray) {
		_shapeArray = shapeArray;
	}
	public Shape[] getShapes() {
		return _shapeArray;
	}
	public void setPalettes(PaletteList palettes) {
		_palettes = palettes;
	}
	public PaletteList getPalettes() {
		return _palettes;
	}
	
	///////////////////////////////////////
	// Debugging
	///////////////////////////////////////
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Representation {width = ");
		buffer.append(_width);
		buffer.append("; height = ");
		buffer.append(_height);
		buffer.append("; description = ");
		buffer.append(_description);
		buffer.append(" }");
		return buffer.toString();
	}

}
