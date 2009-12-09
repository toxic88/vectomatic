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
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.rep.view.DrawingView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Menu item customized to support enable/disable state
 * and call invocation of a Controller
 */
public class ControllerMenuItem extends MenuItem implements Command {
	private IController _controller;
	private boolean _enabled;
	private DrawingView _view;
	public ControllerMenuItem(DrawingView view, String text, IController controller) {
		super(text, (Command)null);
		setCommand(this);
		_view = view;
		_controller = controller;
		_enabled = true;
	}
	public void execute() {
		if (_enabled) {
			_controller.activate(_view);
		}
	}
	public boolean isEnabled() {
		return _enabled;
	}
	public void setEnabled(boolean enabled) {
		if (_enabled != enabled) {
			_enabled = enabled;
			if (enabled) {
				removeStyleName("gwt-MenuItem-disabled");				
			} else {
				addStyleName("gwt-MenuItem-disabled");
			}
		}
	}
}
