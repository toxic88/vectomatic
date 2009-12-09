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
package org.vectomatic.client.rep.command;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.IAttributeValue;
import org.vectomatic.common.model.Shape;

/**
 * Command to set a graphical attribute on a shape
 */
public class SetAttributeCommand extends CommandBase {
	private Attribute _attr;
	private List<Shape> _shapes;
	private List<IAttributeValue> _values;

	public SetAttributeCommand(RepApplication app, Attribute attr, IAttributeValue value) {
		super(app);
		_attr = attr;
		_shapes = new ArrayList<Shape>(_app.getSelection().getSelectedShapes());
		_values = new ArrayList<IAttributeValue>();
		for (int i  = 0, count = _shapes.size(); i < count; i++) {
			_values.add(value);
		}
	}
	
	public String getDescription() {
		return _app.getConstants().setAttributeCommand() + _attr.getName();
	}


	public void execute() {
		applyAttributeChange();
	}

	public void unexecute() {
		applyAttributeChange();
	}

	private void applyAttributeChange() {
		for (int i  = 0, count = _shapes.size(); i < count; i++) {
			Shape shape = _shapes.get(i);
			_values.set(i, shape.setAttribute(_attr, _values.get(i)));
		}
		_shapes.get(0).getModel().fireModelHasChanged();
		_app.getSelection().fireSelectionHasChanged();
	}
}
