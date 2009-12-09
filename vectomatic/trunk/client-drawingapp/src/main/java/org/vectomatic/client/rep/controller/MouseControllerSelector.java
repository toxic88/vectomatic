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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.view.DrawingView;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to manage an array of buttons which
 * determine which controller is catching events on the 2D view
 */
public class MouseControllerSelector implements ClickListener {
	private DrawingView _view;
	private List<MouseControllerButton> _buttons;


	public MouseControllerSelector(DrawingView view) {
		_view = view;
		_buttons = new ArrayList<MouseControllerButton>();
	}
	
	public void add(MouseControllerButton button) {
		_buttons.add(button);
		button.addClickListener(this);
	}
	
	
	/**
	 * Invoked when a MouseControllerButton is clicked by the user
	 */
	public void onClick(Widget sender) {
		for (int i = 0, size = _buttons.size(); i < size; i ++) {
			MouseControllerButton button = _buttons.get(i);
			if (button == sender) {
				if (button.isDown()) {
					// Select the new button
					_view.getController().deactivate(_view);
					_view.setController(button.controller);
					button.controller.activate(_view);
				} else {
					 // The button is already selected
					button.setDown(true);
				}
			} else if (button.isDown()) {
				// Deselect the previously selected button
				button.setDown(false);
			}
		}
	}
	
	public void selectController(MouseControllerButton button) {
		if (!button.isDown()) {
			if (_view.getController() != null) {
				_view.getController().deactivate(_view);				
			}
			for (int i = 0, size = _buttons.size(); i < size; i ++) {
				MouseControllerButton currentButton = _buttons.get(i);
				if (currentButton.isDown()) {
					currentButton.setDown(false);
					break;
				}
			}
			button.setDown(true);
			_view.setController(button.controller);
			button.controller.activate(_view);
		}
	}
}
