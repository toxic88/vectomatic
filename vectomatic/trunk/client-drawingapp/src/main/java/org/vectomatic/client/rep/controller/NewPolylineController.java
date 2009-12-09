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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.command.NewShapeCommand;
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Polyline;

/**
 * Controller to respond to new polyline requests and turn
 * them into NewShapeCommand
 */
public class NewPolylineController extends ControllerBase {
	private Point[] _pts;
	private int _count;
	private MouseControllerButton _button;

	public NewPolylineController(RepApplication app) {
		super(app);
		_pts = new Point[10];
		for (int i = 0; i < _pts.length; i++) {
			_pts[i] = new Point();
		}
		_button = new MouseControllerButton(_app.getIcons().polylineIcon().createImage(), _app.getConstants().newPolylineCommand(), this);
	}
	
	public MouseControllerButton getButton() {
		return _button;
	}

	@Override
	public void activate(DrawingView view) {
		_count = 0;
		view.setCursor(Cursor.CURSOR_CROSSHAIR);
	}

	private void addPoint(Point p) {
		if (_count == _pts.length) {
			// double the buffer capacity
			Point[] pts = new Point[2 * _pts.length];
			for (int i = 0; i < _pts.length; i++) {
				pts[i] = _pts[i];
			}
			for (int i = _pts.length; i < pts.length; i++) {
				pts[i] = new Point();
			}
			_pts = pts;
		}
		p.copyTo(_pts[_count]);
		_count++;
	}
	
	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		view.toModelCoordinates(p);
		if (_count == 0) {
			p.copyTo(_pts[0]);
			p.copyTo(_pts[1]);
			_count = 2;
		} else {
			// add a new vertex to the polyline
			addPoint(p);
			
			// end of polyline test
			float tol = view.convertToReferenceLength(3);
			if (_pts[_count - 2].subtract(_pts[0], p).length() < tol) {
				// closed polyline if pt(N-1) == pt(0) with a certain tolerance
				_pts[0].copyTo(_pts[_count - 2]);
				deactivate(view);
			} else if (_pts[_count - 2].subtract(_pts[_count - 3], p).length() < tol) {
				// open polyline if pt(N) == pt(N-1) (double click of the user)
				_count--;
				deactivate(view);
			}
		}
	}

	@Override
	public void mouseMove(DrawingView view, Point p, int modifiers) {
		view.toModelCoordinates(p);
		if (_count > 1) {
			p.copyTo(_pts[_count - 1]);
			if (_count > 2) {
				float tol = view.convertToReferenceLength(3);
				if (_pts[_count - 1].subtract(_pts[0], p).length() < tol) {
					view.setCursor(Cursor.CURSOR_CLOSED_POLYLINE);
				} else if (_pts[_count - 1].subtract(_pts[_count - 2], p).length() < tol) {
					view.setCursor(Cursor.CURSOR_OPEN_POLYLINE);
				} else {
					view.setCursor(Cursor.CURSOR_CROSSHAIR);					
				}
			}
		}
	}


	@Override
	public void render(DrawingView view) {
		if (_count > 0) {
//			view.beginPath();
			_app.getLineStyleController().getStyle().acceptVisitor(view.getStrokeStyleVisitor());

			view.setLineWidth(_app.getLineWidthController().getLineWidth().getValue());
			view.moveTo(_pts[0].x, _pts[0].y);
			for (int i = 1; i < _count; i++) {
				view.lineTo(_pts[i].x, _pts[i].y);
			}
//			view.closePath();
			view.stroke();
		}
	}

	@Override
	public void deactivate(DrawingView view) {
		if (_count > 2) {
			Polyline polyline = new Polyline(_pts, _count - 1);
			polyline.setAttribute(Attribute.LINE_STYLE, _app.getLineStyleController().getStyle());
			polyline.setAttribute(Attribute.LINE_OPACITY, _app.getLineStyleController().getOpacity());
			polyline.setAttribute(Attribute.FILL_STYLE, _app.getFillStyleController().getStyle());
			polyline.setAttribute(Attribute.FILL_OPACITY, _app.getFillStyleController().getOpacity());
			polyline.setAttribute(Attribute.LINE_WIDTH, _app.getLineWidthController().getLineWidth());
			ICommand command = new NewShapeCommand(_app, polyline);
			command.execute();
			_app.getHistory().addCommand(command);
			activate(view);
		}
	}
}
