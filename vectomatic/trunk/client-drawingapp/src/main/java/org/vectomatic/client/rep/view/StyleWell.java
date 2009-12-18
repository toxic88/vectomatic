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

import org.vectomatic.common.model.IStyleVisitor;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.IStyle;
import org.vectomatic.common.model.style.NoneStyle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to represent the style
 */
public class StyleWell extends Widget implements ChangeListener {
	private IStyle _style;
	private PopupPanel _menu;
	private IStyleVisitor _styleVisitor = new IStyleVisitor() {

		public void visitColor(Color color) {
			String style = color.toString();
			DOM.setStyleAttribute(getElement(), "backgroundColor", style);
		}

		public void visitNoneStyle(NoneStyle none) {
			DOM.setStyleAttribute(getElement(), "backgroundColor", "rgb(255, 255, 255)");
		}
		
	};

	public StyleWell() {
		setElement(DOM.createDiv());
		setStyleName("colorEditor-styleWell");
		sinkEvents(Event.ONCLICK);
	}

	public IStyle getStyle() {
		return _style;
	}

	public void setStyle(IStyle color, IStyleMenu menu) {
		_style = color;
		_menu = (PopupPanel)menu;
		update();
	}

	public void update() {
		if (_style != null) {
			_style.acceptVisitor(_styleVisitor);
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			if (_menu != null) {
			    int left = getAbsoluteLeft();
			    int top = getAbsoluteTop() + getOffsetHeight();
			    _menu.setPopupPosition(left, top);
				_menu.show();
			}
			break;
		}
	}

	public void onChange(Widget sender) {
		update();		
	}
}
