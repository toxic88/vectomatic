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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.ClipBoard;
import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.PasteCommand;
import org.vectomatic.client.rep.events.IClipBoardListener;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.CloneShapeVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Point;

/**
 * Controller to respond to paste requests and turn
 * them into PasteCommand
 */
public class PasteController extends ControllerBase implements IClipBoardListener {
	private ControllerMenuItem _pasteMenuItem;
	private CloneShapeVisitor _cloner;
	private int _pasteCount;
	
	public PasteController(RepApplication app) {
		super(app);
		_app.getClipboard().addShapeSelectionListener(this);
		_pasteMenuItem = new ControllerMenuItem(_app.getView(), app.getConstants().pasteCommand(), this);
		_cloner = new CloneShapeVisitor();
		clipBoardChanged(_app.getClipboard());
	}
	
	@Override
	public void activate(DrawingView view) {
		_pasteCount++;
		// Clone the clipboard shapes
		List<Shape> clipBoardShapes = (List<Shape>)_app.getClipboard().getContent();
		List<Shape> clonedShapes = new ArrayList<Shape>();
		float d = view.convertToReferenceLength(5);
		Point p1 = new Point();
		Point p2 = new Point(_pasteCount * d, _pasteCount * d);
		for (int i = 0, size = clipBoardShapes.size(); i < size; i++) {
			Shape shape = clipBoardShapes.get(i);
			shape.acceptVisitor(_cloner);
			Shape clonedShape = _cloner.getClone();
			// Move the clone slightly with respect to the original
			// so that the 2 do not exactly overlap
			clonedShape.setTranslation(clonedShape.getTranslation(p1).add(p2));
			clonedShapes.add(clonedShape);
		}
		// Create a new paste command
		PasteCommand pasteCommand = new PasteCommand(_app, clonedShapes);
		
		// Execute the command to add the shapes from the model
		pasteCommand.execute();
		_app.getHistory().addCommand(pasteCommand);
	}
	
	public ControllerMenuItem getPasteMenuItem() {
		return _pasteMenuItem;
	}

	public void clipBoardChanged(ClipBoard clipBoard) {
		_pasteMenuItem.setEnabled(_app.getClipboard().getContent() != null);
		_pasteCount = 0;
	}

}
