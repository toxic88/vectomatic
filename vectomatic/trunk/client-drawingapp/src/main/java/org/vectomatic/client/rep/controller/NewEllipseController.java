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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.command.NewShapeCommand;
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Ellipse;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;

/**
 * Controller to respond to new ellipse requests and turn
 * them into NewShapeCommand
 */
public class NewEllipseController extends ControllerBase {
	
	private enum State {
		S0 {
			@Override
			State processActivate(NewEllipseController controller, DrawingView view) {
				controller._ellipse = null;
				controller._app.getSelection().select(new ArrayList<Shape>());
				view.setCursor(Cursor.CURSOR_POINTER);
				return S1;
			}
		},
		S1 {
			@Override
			State processMouseDown(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
				controller._ellipse = new Ellipse();
				controller._ellipse.setAttribute(Attribute.LINE_STYLE, controller._app.getLineStyleController().getStyle());
				controller._ellipse.setAttribute(Attribute.LINE_OPACITY, controller._app.getLineStyleController().getOpacity());
				controller._ellipse.setAttribute(Attribute.FILL_STYLE, controller._app.getFillStyleController().getStyle());
				controller._ellipse.setAttribute(Attribute.FILL_OPACITY, controller._app.getFillStyleController().getOpacity());
				controller._ellipse.setAttribute(Attribute.LINE_WIDTH, controller._app.getLineWidthController().getLineWidth());

				controller._ellipse.setTranslation(view.toModelCoordinates(p));
				controller._ellipse.setRotation(- view.getRotation());
				controller._m.rotation(view.getRotation());
				p.copyTo(controller._p0);
				return S2;
			}
		},
		S2 {
			@Override
			State processMouseMove(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p).add(controller._p0).multiply(0.5f);
				controller._ellipse.setTranslation(p);
				controller._ellipse.setScaling(p.subtract(controller._p0).transform(controller._m));
				return S2;
			}

			@Override
			State processMouseUp(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
				processMouseMove(controller, view, p, modifiers);
				return S3.processDeactivate(controller, view);
			}

		},
		S3 {
			@Override
			State processDeactivate(NewEllipseController controller, DrawingView view) {
				// Discard shapes which are smaller than 5 pixels
				if (controller._ellipse.getScaling(controller._p0).length() > view.convertToReferenceLength(5)) {
					ICommand command = new NewShapeCommand(controller._app, controller._ellipse);
					command.execute();
					controller._app.getHistory().addCommand(command);
				}
				return S0.processActivate(controller, view);
			}
		};
		
		State processActivate(NewEllipseController controller, DrawingView view) {
			throw new IllegalStateException("activate");
		}
		State processMouseDown(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processMouseUp(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processMouseMove(NewEllipseController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processDeactivate(NewEllipseController controller, DrawingView view) {
			return this;
		}
	}
	
	/**
	 * Ellipse currently being created, if any
	 */
	private Ellipse _ellipse;
	private Point _p0;
	private TransformMatrix _m;
	private MouseControllerButton _button;
	private State _state;

	public NewEllipseController(RepApplication app) {
		super(app);
		_p0 = new Point();
		_m = new TransformMatrix();
		_button = new MouseControllerButton(_app.getIcons().ellipseIcon().createImage(), _app.getConstants().newEllipseCommand(), this);
	}

	public MouseControllerButton getButton() {
		return _button;
	}

	@Override
	public void render(DrawingView view) {
		if (_state == State.S2) {
			view.getRenderer().visitEllipse(_ellipse);
		}
	}
	
	@Override
	public void activate(DrawingView view) {
		_state = State.S0;
		_state = _state.processActivate(this, view);
	}

	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseDown(this, view, p, modifiers);
	}
	
	@Override
	public void mouseMove(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseMove(this, view, p, modifiers);
	}

	@Override
	public void mouseUp(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseUp(this, view, p, modifiers);
	}
	
	@Override
	public void deactivate(DrawingView view) {
		_state = _state.processDeactivate(this, view);
	}
}
