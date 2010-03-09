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
package org.vectomatic.client.rep;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.events.IClipBoardListener;

/**
 * Application clipboard class
 */
public class ClipBoard {
	private Object _content;
	private List<IClipBoardListener> _clipBoardListeners;

	public void addShapeSelectionListener(IClipBoardListener listener) {
		if (_clipBoardListeners == null) {
			_clipBoardListeners = new ArrayList<IClipBoardListener>();
		}
		_clipBoardListeners.add(listener);
	}
	
	public void removeClipBoardListener(IClipBoardListener listener) {
		if (_clipBoardListeners != null) {
			_clipBoardListeners.remove(listener);
		}
	}
	
	public void fireClipBoardHasChanged() {
		if (_clipBoardListeners != null) {
			for (int i = 0, size = _clipBoardListeners.size(); i < size; i++) {
				IClipBoardListener listener = _clipBoardListeners.get(i);
				listener.clipBoardChanged(this);
			}
		}
	}

	public Object getContent() {
		return _content;
	}
	public void setContent(Object content) {
		_content = content;
		fireClipBoardHasChanged();
	}
}
