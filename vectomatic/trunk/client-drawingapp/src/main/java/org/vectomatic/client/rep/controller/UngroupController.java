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

import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.UngroupCommand;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.ShapeGroup;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Controller to respond to ungrouping requests and turn
 * them into UngroupCommand
 */
public class UngroupController extends ControllerBase implements IShapeSelectionListener {
	private ControllerMenuItem _ungroupMenuItem;
	private ControllerContextItem _ungroupContextItem;

	public UngroupController(RepApplication app) {
		super(app);
		_app.getSelection().addShapeSelectionListener(this);
		_ungroupMenuItem = new ControllerMenuItem(_app.getView(), _app.getConstants().ungroupCommand(), this);
		_ungroupContextItem = new ControllerContextItem(_app.getView(), _app.getConstants().ungroupCommand(), this);
		selectionChanged(_app.getSelection());
	}

	@Override
	public void activate(DrawingView view) {
		UngroupCommand ungroupCommand = new UngroupCommand(_app);
		ungroupCommand.execute();
		_app.getHistory().addCommand(ungroupCommand);
	}

	public void selectionChanged(ShapeSelection selection) {
		List<Shape> selectedShapes = selection.getSelectedShapes();
		_ungroupMenuItem.setEnabled(selectedShapes.size() == 1 && (selectedShapes.get(0) instanceof ShapeGroup));
	}

	public MenuItem getUngroupMenuItem() {
		return _ungroupMenuItem;
	}

	public MenuItem getUngroupContextItem() {
		return _ungroupContextItem;
	}

}
