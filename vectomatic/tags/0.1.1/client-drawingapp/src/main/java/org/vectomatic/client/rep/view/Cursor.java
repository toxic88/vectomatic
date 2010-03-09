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
package org.vectomatic.client.rep.view;

/**
 * Enum to represent the different cursors
 * @author Lukas Laag
 */
public enum Cursor {
	CURSOR_POINTER("gwt-Canvas-pointer"),
	CURSOR_NW_RESIZE("gwt-Canvas-nw-resize"),
	CURSOR_N_RESIZE("gwt-Canvas-n-resize"),
	CURSOR_NE_RESIZE("gwt-Canvas-ne-resize"),
	CURSOR_W_RESIZE("gwt-Canvas-w-resize"),
	CURSOR_E_RESIZE("gwt-Canvas-e-resize"),
	CURSOR_SW_RESIZE("gwt-Canvas-sw-resize"),
	CURSOR_S_RESIZE("gwt-Canvas-s-resize"),
	CURSOR_SE_RESIZE("gwt-Canvas-se-resize"),
	CURSOR_MOVE("gwt-Canvas-move"),
	CURSOR_ROTATE("gwt-Canvas-rotate"),
	CURSOR_PICK_COLOR("gwt-Canvas-pickColor"),
	CURSOR_OPEN_POLYLINE("gwt-Canvas-openPolyline"),
	CURSOR_CLOSED_POLYLINE("gwt-Canvas-closedPolyline"),
	CURSOR_CROSSHAIR("gwt-Canvas-crosshair");
	
	private String _cssText;
	private Cursor(String cssText) {
		_cssText = cssText;
	}
	public String getCssText() {
		return _cssText;
	}
}
