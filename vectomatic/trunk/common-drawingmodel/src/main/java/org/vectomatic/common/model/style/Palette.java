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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.common.events.IPaletteListener;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a list of colors
 */
public class Palette implements IsSerializable {
	/**
	 * The name of the palette
	 */
	private String _name;
	/**
	 * A List of colors.
	 */
	private List<Color> _colors;
	private transient List<IPaletteListener> _paletteListeners;
	public static Palette getEGA() { 
		return new Palette("EGA", new Color[] {
			Color.fromRGB(0,0,0),
			Color.fromRGB(168,0,168),
			Color.fromRGB(252,84,84),
			Color.fromRGB(252,84,168),
			Color.fromRGB(252,84,252),
			Color.fromRGB(252,168,252),
			Color.fromRGB(252,252,0),
			Color.fromRGB(252,252,168),
			Color.fromRGB(252,252,252),
			Color.fromRGB(168,252,252),
			Color.fromRGB(0,252,252),
			Color.fromRGB(84,168,252),
			Color.fromRGB(0,0,252),
			Color.fromRGB(0,84,168),
			Color.fromRGB(0,0,84),
			Color.fromRGB(84,84,84),
			Color.fromRGB(0,0,0),
			Color.fromRGB(168,0,168),
			Color.fromRGB(252,84,84),
			Color.fromRGB(252,84,168),
			Color.fromRGB(252,84,252),
			Color.fromRGB(252,168,252),
			Color.fromRGB(252,252,0),
			Color.fromRGB(252,252,168),
			Color.fromRGB(252,252,252),
			Color.fromRGB(168,252,252),
			Color.fromRGB(0,252,252),
			Color.fromRGB(84,168,252),
			Color.fromRGB(0,0,252),
			Color.fromRGB(0,84,168),
			Color.fromRGB(0,0,84),
			Color.fromRGB(84,84,84),
			Color.fromRGB(0,0,0),
			Color.fromRGB(168,0,168),
			Color.fromRGB(252,84,84),
			Color.fromRGB(252,84,168),
			Color.fromRGB(252,84,252),
			Color.fromRGB(252,168,252),
			Color.fromRGB(252,252,0),
			Color.fromRGB(252,252,168),
			Color.fromRGB(252,252,252),
			Color.fromRGB(168,252,252),
			Color.fromRGB(0,252,252),
			Color.fromRGB(84,168,252),
			Color.fromRGB(0,0,252),
			Color.fromRGB(0,84,168),
			Color.fromRGB(0,0,84),
			Color.fromRGB(84,84,84),
			Color.fromRGB(0,0,0),
			Color.fromRGB(168,0,168),
			Color.fromRGB(252,84,84),
			Color.fromRGB(252,84,168),
			Color.fromRGB(252,84,252),
			Color.fromRGB(252,168,252),
			Color.fromRGB(252,252,0),
			Color.fromRGB(252,252,168),
			Color.fromRGB(252,252,252),
			Color.fromRGB(168,252,252),
			Color.fromRGB(0,252,252),
			Color.fromRGB(84,168,252),
			Color.fromRGB(0,0,252),
			Color.fromRGB(0,84,168),
			Color.fromRGB(0,0,84),
			Color.fromRGB(84,84,84)
		});
	}
	
	public static Palette getGOLD() {
		return new Palette("Gold", new Color[] {
			Color.fromRGB(252,252,128),
			Color.fromRGB(252,252,128),
			Color.fromRGB(252,248,124),
			Color.fromRGB(252,248,124),
			Color.fromRGB(252,244,120),
			Color.fromRGB(248,244,120),
			Color.fromRGB(248,240,116),
			Color.fromRGB(248,240,112),
			Color.fromRGB(248,236,112),
			Color.fromRGB(244,236,108),
			Color.fromRGB(244,232,108),
			Color.fromRGB(244,232,104),
			Color.fromRGB(244,228,104),
			Color.fromRGB(240,228,100),
			Color.fromRGB(240,224, 96),
			Color.fromRGB(240,224, 96),
			Color.fromRGB(240,220, 92),
			Color.fromRGB(236,220, 92),
			Color.fromRGB(236,216, 88),
			Color.fromRGB(236,216, 84),
			Color.fromRGB(236,212, 84),
			Color.fromRGB(236,212, 80),
			Color.fromRGB(232,208, 80),
			Color.fromRGB(232,208, 76),
			Color.fromRGB(232,204, 76),
			Color.fromRGB(232,204, 72),
			Color.fromRGB(228,200, 68),
			Color.fromRGB(228,200, 68),
			Color.fromRGB(228,196, 64),
			Color.fromRGB(228,196, 64),
			Color.fromRGB(224,192, 60),
			Color.fromRGB(224,192, 56),
			Color.fromRGB(224,188, 56),
			Color.fromRGB(224,188, 52),
			Color.fromRGB(220,184, 52),
			Color.fromRGB(220,184, 48),
			Color.fromRGB(220,180, 48),
			Color.fromRGB(220,180, 44),
			Color.fromRGB(220,176, 40),
			Color.fromRGB(216,176, 40),
			Color.fromRGB(216,172, 36),
			Color.fromRGB(216,172, 36),
			Color.fromRGB(216,168, 32),
			Color.fromRGB(212,168, 28),
			Color.fromRGB(212,164, 28),
			Color.fromRGB(212,164, 24),
			Color.fromRGB(212,160, 24),
			Color.fromRGB(208,160, 20),
			Color.fromRGB(208,156, 20),
			Color.fromRGB(208,156, 16),
			Color.fromRGB(208,152, 12),
			Color.fromRGB(204,152, 12),
			Color.fromRGB(204,148,  8),
			Color.fromRGB(204,148,  8),
			Color.fromRGB(204,144,  4),
			Color.fromRGB(200,140,  0),
			Color.fromRGB(196,136,  0),
			Color.fromRGB(196,136,  0),
			Color.fromRGB(196,136,  0),
			Color.fromRGB(196,136,  0),
			Color.fromRGB(192,132,  0),
			Color.fromRGB(192,132,  0),
			Color.fromRGB(192,132,  0),
			Color.fromRGB(192,132,  0),			
		});
	}
	
	public Palette() {
		_name = "";
		_colors = new ArrayList<Color>();
	}
	
	public Palette(String name, Color[] colors) {
		this();
		_name = name;
		for (int i = 0; i < colors.length; i++) {
			addColor(colors[i]);
		}
	}
	
	public Palette(Palette palette) {
		this();
		_name = palette.getName();
		for (int i = 0, size = palette.getSize(); i < size; i++) {
			addColor(palette.getColor(i));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Palette) {
			Palette palette = (Palette)obj;
			return _name.equals(palette._name) && _colors.equals(palette._colors);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return _name.hashCode() + 37 * _colors.hashCode();
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		_name = name;
	}
	
	public Color getColor(int index) {
		return _colors.get(index);
	}
	
	public void setColor(int index, Color color) {
		color.copyTo(getColor(index));
		firePaletteHasChanged();
	}
	
	public void addColor(Color color) {
		_colors.add(new Color(color));
		firePaletteHasChanged();
	}
	
	public void removeColor(int index) {
		_colors.remove(index);
		firePaletteHasChanged();
	}
	
	public int getSize() {
		return _colors.size();
	}
	
	public void addPaletteListener(IPaletteListener listener) {
		if (_paletteListeners == null) {
			_paletteListeners = new ArrayList<IPaletteListener>();
		}
		_paletteListeners.add(listener);
	}
	
	public void removePaletteListener(IPaletteListener listener) {
		if (_paletteListeners != null) {
			_paletteListeners.remove(listener);
		}
	}
	
	public void firePaletteHasChanged() {
		if (_paletteListeners != null) {
			for (int i = 0, size = _paletteListeners.size(); i < size; i++) {
				IPaletteListener listener = _paletteListeners.get(i);
				listener.paletteHasChanged(this);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Palette(name=\"");
		buffer.append(_name);
		buffer.append("\"");
		for (int i = 0, size = _colors.size(); i < size; i++) {
			buffer.append(" ");
			buffer.append(_colors.get(i).toString());
		}
		buffer.append(")");
		return buffer.toString();
	}
}
