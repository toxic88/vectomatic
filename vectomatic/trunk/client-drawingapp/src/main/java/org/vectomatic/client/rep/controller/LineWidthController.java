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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.SetAttributeCommand;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.ShapeGroup;

import com.google.gwt.user.client.ui.Widget;

/**
 * Controller to respond to line width modifications and turn
 * them into SetAttributeCommand
 */
public class LineWidthController extends ControllerBase implements IShapeSelectionListener {
	private FloatAttributeValue _defaultLineWidth;
	private LineWidthWell _lineWithWell;
	private Set<FloatAttributeValue> _lineWidths;

	public LineWidthController(RepApplication app) {
		super(app);
		_app.getSelection().addShapeSelectionListener(this);
		_lineWidths = new HashSet<FloatAttributeValue>();
	
		_defaultLineWidth = new FloatAttributeValue(1f);
		_lineWithWell = new LineWidthWell(this);
		_lineWithWell.setLineWidth(_defaultLineWidth);
	}
	
	public Widget getWidget() {
		return _lineWithWell;
	}

	
	public void selectionChanged(ShapeSelection selection) {
		_lineWidths.clear();
		List<Shape> shapes = new ArrayList<Shape>(selection.getSelectedShapes());
		for (int i = 0, size = shapes.size(); i < size; i++) {
			Shape shape = shapes.get(i);
			FloatAttributeValue lineWidth = (FloatAttributeValue)shape.getAttribute(Attribute.LINE_WIDTH);
			if ((lineWidth == null) && (shape instanceof ShapeGroup)) {
				shapes.addAll(((ShapeGroup)shape).getShapes());
				continue;
			}
			_lineWidths.add(lineWidth);
		}
		if (_lineWidths.size() == 0) {
			_lineWithWell.setLineWidth(_defaultLineWidth);
		} else if (_lineWidths.size() == 1) {
			_lineWithWell.setLineWidth(_lineWidths.iterator().next());
		} else {
			_lineWithWell.setLineWidth(null);
		}
	}
	
	/**
	 * Invoked when the user changes the line width in the line width menu
	 * or in the line width editor
	 */
	public void setLineWidth(FloatAttributeValue lineWidth) {
		RepApplication.app.debugPrint("lineWidth change");
		//Determine if the change is real and should generate a corresponding command
		if (_app.getSelection().hasAttributeChanged(Attribute.LINE_WIDTH, lineWidth)) {
			SetAttributeCommand setAttributeCommand = new SetAttributeCommand(_app, Attribute.LINE_WIDTH, lineWidth);
			setAttributeCommand.execute();
			_app.getHistory().addCommand(setAttributeCommand);
		}
		if (_app.getSelection().getSelectedShapes().size() == 0) {
			_defaultLineWidth = lineWidth;
		}
		_lineWithWell.setLineWidth(lineWidth);
	}
	
	public FloatAttributeValue getLineWidth() {
		return _defaultLineWidth;
	}

}
