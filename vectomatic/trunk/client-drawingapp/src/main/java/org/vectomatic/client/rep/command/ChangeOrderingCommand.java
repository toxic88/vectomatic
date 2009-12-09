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
import org.vectomatic.common.model.Shape;

/**
 * Command to change the order of the shapes in the display list
 */
public class ChangeOrderingCommand extends CommandBase {
	public static final int BRING_TO_FRONT = 0;
	public static final int SEND_TO_BACK = 1;
	public static final int BRING_FORWARD = 2;
	public static final int SEND_BACKWARD = 3;
	private int _direction;
	private List<Shape> _shapes;
	private List<Float> _orders;
	
	public ChangeOrderingCommand(RepApplication app, int direction) {
		super(app);
		_direction = direction;
		_shapes = new ArrayList<Shape>(_app.getSelection().getSelectedShapes());
	}

	public void execute() {
		switch(_direction) {
			case BRING_TO_FRONT:
				_orders = _app.getModel().bringToFront(_shapes);
				break;
			case SEND_TO_BACK:
				_orders = _app.getModel().sendToBack(_shapes);
				break;
			case BRING_FORWARD:
				_orders = _app.getModel().bringForward(_shapes);
				break;
			case SEND_BACKWARD:
				_orders = _app.getModel().sendBackward(_shapes);
				break;
		}
	}

	public String getDescription() {
		String description = null;
		switch(_direction) {
			case BRING_TO_FRONT:
				description = _app.getConstants().bringToFrontCommand();
				break;
			case SEND_TO_BACK:
				description = _app.getConstants().sendToBackCommand();
				break;
			case BRING_FORWARD:
				description = _app.getConstants().bringForwardCommand();
				break;
			case SEND_BACKWARD:
				description = _app.getConstants().sendBackwardCommand();
				break;
		}
		return description;
	}

	public void unexecute() {
		_app.getModel().reorder(_shapes, _orders);
	}
}
