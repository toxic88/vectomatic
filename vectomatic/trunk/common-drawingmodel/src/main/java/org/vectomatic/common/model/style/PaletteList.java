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
package org.vectomatic.common.model.style;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a list of palettes
 */
public class PaletteList implements IsSerializable {
	/**
	 * A List of palettes.
	 */
	private List<Palette> _palettes;
	public PaletteList() {
		_palettes = new ArrayList<Palette>();
	}
	
	public void addPalette(Palette palette) {
		_palettes.add(palette);
	}

	public void fromPaletteList(PaletteList paletteList) {
		_palettes.clear();
		_palettes.addAll(paletteList._palettes);
	}

	public Palette newPalette() {
		Palette palette = new Palette();
		_palettes.add(palette);
		return palette;
	}
	
	public Palette clonePalette(int index) {
		Palette clone = new Palette(getPalette(index));
		_palettes.add(clone);
		return clone;
	}
	
	public void removePalette(int index) {
		_palettes.remove(index);
	}
	
	public Palette getPalette(int index) {
		return _palettes.get(index);
	}
	
	public int size() {
		return _palettes.size();
	}
	
	public void clear() {
		_palettes.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PaletteList) {
			return _palettes.equals(((PaletteList)obj)._palettes);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return _palettes.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PaletteList(");
		for (int i = 0, size = _palettes.size(); i < size; i++) {
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(_palettes.get(i).toString());
		}
		buffer.append(")");
		return buffer.toString();
	}

}
