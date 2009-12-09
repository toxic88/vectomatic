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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.CloneShapeVisitor;
import org.vectomatic.common.model.Shape;

/**
 * Controller to respond to copy request
 */
public class CopyController extends ControllerBase implements IShapeSelectionListener {
	private ControllerMenuItem _copyMenuItem;
	private CloneShapeVisitor _cloner;
	
	public CopyController(RepApplication app) {
		super(app);
		_app.getSelection().addShapeSelectionListener(this);
		_copyMenuItem = new ControllerMenuItem(_app.getView(), _app.getConstants().copyCommand(), this);
		selectionChanged(_app.getSelection());
		_cloner = new CloneShapeVisitor();
	}
	
	@Override
	public void activate(DrawingView view) {
		List<Shape> selectedShapes = _app.getSelection().getSelectedShapes();
		List<Shape> clonedShapes = new ArrayList<Shape>();
		for (int i = 0, size = selectedShapes.size(); i < size; i++) {
			Shape shape = selectedShapes.get(i);
			shape.acceptVisitor(_cloner);
			clonedShapes.add(_cloner.getClone());
		}
		_app.getClipboard().setContent(clonedShapes);
	}
	
	public ControllerMenuItem getCopyMenuItem() {
		return _copyMenuItem;
	}
	public void selectionChanged(ShapeSelection selection) {
		_copyMenuItem.setEnabled(_app.getSelection().getSelectedShapes().size() > 0);	
	}
}
