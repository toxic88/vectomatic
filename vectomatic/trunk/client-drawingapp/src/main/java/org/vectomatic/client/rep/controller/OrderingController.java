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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.ChangeOrderingCommand;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Shape;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Controller to respond to shape ordering requests and turn
 * them into ChangeOrderingCommand
 */
public class OrderingController implements IShapeSelectionListener {
	private RepApplication _app;
	private Map<String,List<ControllerMenuItem>> _directionToMenuItemList;
	private String[] _descriptions;

	public OrderingController(RepApplication app) {
		_app = app;
		_app.getSelection().addShapeSelectionListener(this);
		_directionToMenuItemList = new HashMap<String,List<ControllerMenuItem>>();
		_descriptions = new String[] {
				_app.getConstants().bringToFrontCommand(),
				_app.getConstants().sendToBackCommand(),
				_app.getConstants().bringForwardCommand(),
				_app.getConstants().sendBackwardCommand()
		};
		selectionChanged(_app.getSelection());
	}

	public void selectionChanged(ShapeSelection selection) {
		List<Shape> selectedShapes = selection.getSelectedShapes();
		for (int i = 0; i < 4; i++) {
			boolean enabled = false;
			switch(i) {
				case ChangeOrderingCommand.BRING_TO_FRONT:
					enabled = _app.getModel().canBringToFront(selectedShapes);
					break;
				case ChangeOrderingCommand.SEND_TO_BACK:
					enabled = _app.getModel().canSendToBack(selectedShapes);
					break;
				case ChangeOrderingCommand.BRING_FORWARD:
					enabled = _app.getModel().canBringForward(selectedShapes);
					break;
				case ChangeOrderingCommand.SEND_BACKWARD:
					enabled = _app.getModel().canSendBackward(selectedShapes);
					break;
			}
			List<ControllerMenuItem> menuItemList = _directionToMenuItemList.get(_descriptions[i]);
			if (menuItemList != null) {
				for (int j = 0, size = menuItemList.size(); j < size; j++) {
					menuItemList.get(j).setEnabled(enabled);
				}
			}
		}
	}

	public MenuItem newMenuItem(final DrawingView view, final int direction) {
		List<ControllerMenuItem> menuItemList = _directionToMenuItemList.get(_descriptions[direction]);
		if (menuItemList == null) {
			menuItemList = new ArrayList<ControllerMenuItem>();
			_directionToMenuItemList.put(_descriptions[direction], menuItemList);
		}
		ControllerMenuItem menuItem = new ControllerMenuItem(view, _descriptions[direction], new ControllerBase(_app) {
			@Override
			public void activate(DrawingView view) {
				ChangeOrderingCommand changeOrderingCommand = new ChangeOrderingCommand(_app, direction);
				changeOrderingCommand.execute();
				_app.getHistory().addCommand(changeOrderingCommand);
			}
		});
		menuItem.setEnabled(false);
		menuItemList.add(menuItem);
		return menuItem;
	}

	public MenuItem newContextItem(DrawingView view, final int direction) {
		List<ControllerMenuItem> menuItemList = _directionToMenuItemList.get(_descriptions[direction]);
		if (menuItemList == null) {
			menuItemList = new ArrayList<ControllerMenuItem>();
			_directionToMenuItemList.put(_descriptions[direction], menuItemList);
		}
		ControllerContextItem menuItem = new ControllerContextItem(view, _descriptions[direction], new ControllerBase(_app) {
			@Override
			public void activate(DrawingView view) {
				ChangeOrderingCommand changeOrderingCommand = new ChangeOrderingCommand(_app, direction);
				changeOrderingCommand.execute();
				_app.getHistory().addCommand(changeOrderingCommand);
			}
		});
		menuItem.setEnabled(false);
		menuItemList.add(menuItem);
		return menuItem;
	}

}
