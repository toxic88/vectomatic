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
package org.vectomatic.client.rep.controller;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * View class to display line widths
 */
public class LineWidthWidget extends Widget {
	private int _lineWidth;
	public LineWidthWidget(int lineWidth, boolean hovering) {
		_lineWidth = lineWidth;
		Element element = DOM.createElement("div");
		setStyleName(element, (hovering) ? "lineWidthWidgetSelectable" : "lineWidthWidget");
		DOM.setStyleAttribute(element, "borderTopWidth", lineWidth + "px");
		DOM.setStyleAttribute(element, "borderTopColor", "black");
		DOM.setStyleAttribute(element, "borderTopStyle", "solid");
		setElement(element);
	}
	
	public int getLineWidth() {
		return _lineWidth;
	}
}
