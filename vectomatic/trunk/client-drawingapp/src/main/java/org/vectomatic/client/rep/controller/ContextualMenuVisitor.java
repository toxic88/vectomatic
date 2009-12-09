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

import org.vectomatic.client.rep.command.ChangeOrderingCommand;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.IShapeVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Path;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.geometry.ShapeGroup;

import com.google.gwt.user.client.ui.MenuBar;

/**
 * Shape visitor implementation to set the items in
 * the contextual menu depending on the selection
 */
public class ContextualMenuVisitor implements IShapeVisitor {
	private MenuBar _shapeBar;
	private MenuBar _shapeGroupBar;
	private MenuBar _multiSelBar;
	private MenuBar _bar;
	
	public MenuBar getContextualMenu(ShapeSelection selection) {
		List<Shape> selectedShapes = selection.getSelectedShapes();
		if (selectedShapes.size() == 0) {
			_bar = null;
		} else if (selectedShapes.size() > 1) {
			_bar = _multiSelBar;
		} else {
			selectedShapes.get(0).acceptVisitor(this);
		}
		return _bar;
	}
	
	public ContextualMenuVisitor(
			DeleteController deleteController,
			UngroupController ungroupController,
			GroupController groupController, 
			OrderingController orderingController,
			DrawingView view) {
		_shapeBar = new MenuBar(true);
		_shapeBar.addItem(deleteController.newDeleteContextItem(view));
		_shapeBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_TO_FRONT));
		_shapeBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_TO_BACK));
		_shapeBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_FORWARD));
		_shapeBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_BACKWARD));
		
		_shapeGroupBar = new MenuBar(true);
		_shapeGroupBar.addItem(deleteController.newDeleteContextItem(view));
		_shapeGroupBar.addItem(ungroupController.getUngroupContextItem());
		_shapeGroupBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_TO_FRONT));
		_shapeGroupBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_TO_BACK));
		_shapeGroupBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_FORWARD));
		_shapeGroupBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_BACKWARD));
		
		_multiSelBar = new MenuBar(true);
		_multiSelBar.addItem(deleteController.newDeleteContextItem(view));
		_multiSelBar.addItem(groupController.getGroupContextItem());
		_multiSelBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_TO_FRONT));
		_multiSelBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_TO_BACK));
		_multiSelBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.BRING_FORWARD));
		_multiSelBar.addItem(orderingController.newContextItem(view, ChangeOrderingCommand.SEND_BACKWARD));
	}
	
	public void visitEllipse(Ellipse ellipse) {
		_bar = _shapeBar;	
	}

	public void visitPolyline(Polyline polyline) {
		_bar = _shapeBar;	
	}

	public void visitRect(Rect rect) {
		_bar = _shapeBar;	
	}

	public void visitShapeGroup(ShapeGroup group) {
		_bar = _shapeGroupBar;	
	}

	public void visitPath(Path path) {
		// TODO Auto-generated method stub
		
	}

}
