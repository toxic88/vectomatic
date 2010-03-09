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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.GroupCommand;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.client.rep.view.DrawingView;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Controller to respond to grouping requests and turn
 * them into GroupCommand
 */
public class GroupController extends ControllerBase implements IShapeSelectionListener {
	private ControllerMenuItem _groupMenuItem;
	private ControllerContextItem _groupContextItem;

	public GroupController(RepApplication app) {
		super(app);
		_app.getSelection().addShapeSelectionListener(this);
		_groupMenuItem = new ControllerMenuItem(_app.getView(), app.getConstants().groupCommand(), this);
		_groupContextItem = new ControllerContextItem(_app.getView(), app.getConstants().groupCommand(), this);
		selectionChanged(_app.getSelection());
	}

	@Override
	public void activate(DrawingView view) {
		GroupCommand groupCommand = new GroupCommand(_app);
		groupCommand.execute();
		_app.getHistory().addCommand(groupCommand);
	}

	public void selectionChanged(ShapeSelection selection) {
		_groupMenuItem.setEnabled((selection.getSelectedShapes().size() > 1));
	}

	public MenuItem getGroupMenuItem() {
		return _groupMenuItem;
	}

	public MenuItem getGroupContextItem() {
		return _groupContextItem;
	}
}
