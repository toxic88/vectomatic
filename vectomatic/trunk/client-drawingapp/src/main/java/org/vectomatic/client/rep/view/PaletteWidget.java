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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.controller.ColorEditor;
import org.vectomatic.common.events.IPaletteListener;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.Palette;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget class to represent a palette
 */
public class PaletteWidget extends Widget implements IPaletteListener, ChangeListener, SourcesChangeEvents {
	private Palette _palette;
	private int _selectedColorIndex;
	private int _columnCount;
	private int _divWidth;
	private int _divHeight;
	private int _margin;
	private List<Element> _subdivs;
	private ColorEditor _colorEditor;
	private ChangeListenerCollection _changeListeners;

	PaletteWidget(Palette palette, ColorEditor colorEditor) {
		super();
		_colorEditor = colorEditor;
		Element div = DOM.createDiv();
		setElement(div);
		sinkEvents(Event.ONMOUSEDOWN | Event.ONDBLCLICK);
		_subdivs = new ArrayList<Element>();
		_divWidth = 12;
		_divHeight = 12;
		_margin = 5;
		_columnCount = 10;
		_selectedColorIndex = -1;
		setPalette(palette);
	}
	
	public Palette getPalette() {
		return _palette;
	}
	
	public void paletteHasChanged(Palette palette) {
		refresh();
	}
	
	public void setPalette(Palette palette) {
		if (_palette != null) {
			_palette.removePaletteListener(this);
		}
		_palette = palette;
		if (_palette != null) {
			_palette.addPaletteListener(this);
		}
		refresh();
		if (_selectedColorIndex == -1) {
			selectColor(0);
		}
	}
	
	private void refresh() {

		Element div = getElement();
		
		// Adjust the number of subdivs so that it matches the number
		// of colors in the palette.
		int subdivCount = _subdivs.size();
		int colorCount = (_palette == null) ? 0 : _palette.getSize();
		
		if (subdivCount > colorCount) {
			for (int i = subdivCount - 1; i >= colorCount; i--) {
				Element subdiv = _subdivs.get(i);
				DOM.removeChild(div, subdiv);
				_subdivs.remove(i);
			}
		} else if (subdivCount < colorCount) {
			for (int i = subdivCount; i < colorCount; i++) {
				int row = i / _columnCount;
				int col = i % _columnCount;
				int x = _margin + (col * (_divWidth + _margin));
				int y = _margin + (row * (_divHeight + _margin));
				Element subdiv = DOM.createDiv();
				DOM.setStyleAttribute(subdiv, "position", "absolute");
				DOM.setStyleAttribute(subdiv, "width", _divWidth + "px");
				DOM.setStyleAttribute(subdiv, "height", _divHeight + "px");
				DOM.setStyleAttribute(subdiv, "left", x + "px");
				DOM.setStyleAttribute(subdiv, "top",  y + "px");
				DOM.setElementAttribute(subdiv, "class", "palette");
				DOM.appendChild(div, subdiv);
				_subdivs.add(subdiv);
			}			
		}

		// Set the subdiv color according to the palette
		for (int i = 0; i < colorCount; i++) {
			Color color = _palette.getColor(i);
			Element subdiv = _subdivs.get(i);
			DOM.setStyleAttribute(subdiv, "backgroundColor", color.toString());
		}
		DOM.setStyleAttribute(div, "position", "relative");
		DOM.setStyleAttribute(div, "width", _margin + _columnCount * (_divWidth + _margin) + "px");
		DOM.setStyleAttribute(div, "height", _margin + ((colorCount / _columnCount) + ((colorCount % _columnCount != 0) ? 1 : 0)) * (_divHeight + _margin) + "px");
	}

	@Override
	public void onBrowserEvent(Event event) {
		Element div = getElement();
		super.onBrowserEvent(event);
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
				int x = DOM.eventGetClientX(event) - getAbsoluteLeft(div);
				int col = x / (_divWidth + _margin);
				int y = DOM.eventGetClientY(event) - getAbsoluteTop(div);
				int row = y / (_divHeight + _margin);
				if (col < _columnCount) {
					int index = _selectedColorIndex;
					selectColor(row * _columnCount + col);
					if ((index != _selectedColorIndex) && (_changeListeners != null)) {
						_changeListeners.fireChange(this);
					}
				}
				break;
			case Event.ONDBLCLICK:
				if (_palette != null && _selectedColorIndex != -1 && _colorEditor != null) {
					_colorEditor.setColor(_palette.getColor(_selectedColorIndex), this);
					_colorEditor.show();
				}
				break;
		}
	}
	
	public void selectColor(int index) {
		if (index != _selectedColorIndex) {
			if ((0 <= index) && (index < _subdivs.size())) {
				// Reset border of previously selected color
				if ((0 <= _selectedColorIndex) && (_selectedColorIndex < _subdivs.size())) {
					Element subdiv = _subdivs.get(_selectedColorIndex);
					DOM.setElementAttribute(subdiv, "class", "palette");
				}
				// Set border of newly selected color
				Element subdiv = _subdivs.get(index);
				DOM.setElementAttribute(subdiv, "class", "palette-selected");
				_selectedColorIndex = index;
			}
		}
	}
	
	public int getSelectedColorIndex() {
		return _selectedColorIndex;
	}
	
	public Color getSelectedColor() {
		return (_selectedColorIndex == -1) ? null : _palette.getColor(_selectedColorIndex);
	}
	
	
	public static native int getAbsoluteTop(Element elem) /*-{
	    var top = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      top -= curr.scrollTop;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      top += elem.offsetTop;
	      elem = elem.offsetParent;
	    }
	    return top;
	}-*/;
  
	public static native int getAbsoluteLeft(Element elem) /*-{
	    var left = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      left -= curr.scrollLeft;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      left += elem.offsetLeft;
	      elem = elem.offsetParent;
	    }
	    return left;
	}-*/;

	public void onChange(Widget sender) {
		_colorEditor.getColor(_palette.getColor(_selectedColorIndex));
		_palette.setColor(_selectedColorIndex, _palette.getColor(_selectedColorIndex));
	}

	public void addChangeListener(ChangeListener listener) {
		if (_changeListeners == null) {
			_changeListeners = new ChangeListenerCollection();
		}
		_changeListeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		if (_changeListeners != null) {
			_changeListeners.remove(listener);
		}
	}
}
