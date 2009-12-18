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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.DeleteVertexCommand;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.command.MoveVertexCommand;
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.events.IDrawingModelListener;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.CloneShapeVisitor;
import org.vectomatic.common.model.DrawingModel;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Polyline;
import org.vectomatic.common.model.geometry.TransformMatrix;
import org.vectomatic.common.model.style.Color;

import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * Controller to respond to polyline edition requests and turn
 * them into MoveVertexCommand and DeleteVertexCommand
 */
public class EditPolylineController extends ControllerBase implements IDrawingModelListener {
	private static final int VERTEX_SIZE = 3;
	private CloneShapeVisitor _cloner;
	private MouseControllerButton _button;
	private Set<Integer> _vertexIndices;
	private Polyline _polyline;
	private Polyline _clone;
	private Point _p0;
	private TransformMatrix _mInv;
	private int _pickedVertex;
	
	public EditPolylineController(RepApplication app) {
		super(app);
		_cloner = new CloneShapeVisitor();
		_vertexIndices = new HashSet<Integer>();
		_p0 = new Point();
		_mInv = new TransformMatrix();
		_button = new MouseControllerButton(_app.getIcons().editPolylineIcon().createImage(), _app.getConstants().editPolylineCommand(), this);
		_app.getModel().addDrawingModelListener(this);
	}
	
	public MouseControllerButton getButton() {
		return _button;
	}
	
	@Override
	public void activate(DrawingView view) {
		view.setCursor(Cursor.CURSOR_POINTER);
		_app.getSelection().select(new ArrayList<Shape>());
		selectPolyline(null, null);
		_pickedVertex = -1;
	}

	@Override
	public void deactivate(DrawingView view) {
		if (_pickedVertex != -1) {
			Point[] vertices = _polyline.getVertices();
			Point[] cloneVertices = _clone.getVertices();
			if (!vertices[_pickedVertex].equals(cloneVertices[_pickedVertex])) {
				ICommand command = new MoveVertexCommand(_app, this, _polyline, _clone, _vertexIndices);
				command.execute();
				_app.getHistory().addCommand(command);
			}
			_pickedVertex = -1;			
		}
	}

	@Override
	public void render(DrawingView view) {
		if (_clone != null) {
			view.getRenderer().visitPolyline(_clone);
			Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
			Point[] cloneVertices = _clone.getVertices();
			int length = cloneVertices.length - (_polyline.isClosed() ?  1 : 0);
			for (int i = 0; i < length; i++) {
				cloneVertices[i].transform(_clone.getTransform(), _p0);
				if (_vertexIndices.contains(new Integer(i))) {
					Color.GREEN.acceptVisitor(view.getFillStyleVisitor());
				} else {
					Color.WHITE.acceptVisitor(view.getFillStyleVisitor());
				}
				view.fillRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
				view.strokeRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
			}
		}
	}

	@Override
	public void keyDown(DrawingView view, char keyCode, int modifiers) {
		if (keyCode == KeyboardListener.KEY_DELETE ||  keyCode == KeyboardListener.KEY_BACKSPACE && _app.getSelection().getSelectedShapes().size() > 0) {
			if (_polyline != null) {
				int size = _vertexIndices.size();
				if (size > 0) {
					int vertexCount = _polyline.getVertices().length - size;
					int minVertex = _polyline.isClosed() ? 4 : 2;
					if (vertexCount >= minVertex) {
						ICommand command = new DeleteVertexCommand(_app, this, _polyline, _vertexIndices);
						command.execute();
						_app.getHistory().addCommand(command);
					}
				}
			}
		}
	}
	
	public void selectPolyline(Polyline polyline, Set<Integer> vertexIndices) {
		if (polyline == null) {
			_polyline = null;
			_clone = null;
			_vertexIndices.clear();
		} else {
			_polyline = polyline;
			_cloner.visitPolyline(_polyline);
			_clone = (Polyline)_cloner.getClone();
			_clone.setAttribute(Attribute.FILL_OPACITY, new FloatAttributeValue(0.2f));
			_polyline.getTransform().invert(_mInv);
			_vertexIndices.clear();
			_vertexIndices.addAll(vertexIndices);
		}
	}

	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		view.toModelCoordinates(p);
		if (_polyline == null) {
			Shape shape = view.getPicker().pick(p, _app.getModel().reverseIterator());
			if (shape instanceof Polyline) {
				selectPolyline((Polyline)shape, new HashSet<Integer>());
			}
		} else {
			_pickedVertex = pickVertex(p);
			if (_pickedVertex == -1) {
				Shape shape = view.getPicker().pick(p, _app.getModel().reverseIterator());
				if (shape instanceof Polyline) {
					selectPolyline((Polyline)shape, new HashSet<Integer>());
				} else {
					selectPolyline(null, new HashSet<Integer>());
				}
			} else {
				Integer vertex = new Integer(_pickedVertex);
				boolean ctrlPressed = (modifiers & KeyboardListener.MODIFIER_CTRL) != 0;
				if (ctrlPressed) {
					if (_vertexIndices.contains(vertex)) {
						_vertexIndices.remove(vertex);						
					} else {
						_vertexIndices.add(vertex);
					}
				} else {
					_vertexIndices.add(vertex);
				}
			}
		}
	}

	@Override
	public void mouseMove(DrawingView view, Point p, int modifiers) {
		view.toModelCoordinates(p);
		if (_pickedVertex != -1) {
			if (_vertexIndices.size() > 0) {
				p.transform(_mInv, _p0);
				Point[] vertices = _polyline.getVertices();
				_p0.subtract(vertices[_pickedVertex]);
				Point[] cloneVertices = _clone.getVertices();
				boolean closed = _polyline.isClosed();
				Iterator<Integer> iterator = _vertexIndices.iterator();
				while (iterator.hasNext()) {
					int index = iterator.next().intValue();
					vertices[index].add(_p0, cloneVertices[index]);
					if (index == 0 && closed) {
						vertices[vertices.length - 1].add(_p0, cloneVertices[vertices.length - 1]);
					}
				}
			}
		} else if (_polyline != null) {
			float tol = VERTEX_SIZE;
			p.transform(_mInv, _p0);
			Point[] vertices = _polyline.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				if ((vertices[i].x - tol < _p0.x) && (_p0.x < vertices[i].x + tol)
				 && (vertices[i].y - tol < _p0.y) && (_p0.y < vertices[i].y + tol)) {
					view.setCursor(Cursor.CURSOR_MOVE);
					return;
				}
			}
			view.setCursor(Cursor.CURSOR_POINTER);
		}
	}

	@Override
	public void mouseUp(DrawingView view, Point p, int modifiers) {
		deactivate(view);
	}
	
	private int pickVertex(Point p) {
		int pickedVertex = -1;
		p.transform(_mInv, _p0);
		Point[] vertices = _polyline.getVertices();
		float tol = VERTEX_SIZE;
		for (int i = 0; i < vertices.length; i++) {
			if ((vertices[i].x - tol < _p0.x) && (_p0.x < vertices[i].x + tol)
			 && (vertices[i].y - tol < _p0.y) && (_p0.y < vertices[i].y + tol)) {
				pickedVertex = i;
				break;
			}
		}
		return pickedVertex;
	}

	public void modelHasChanged(DrawingModel model) {
		if (_polyline != null && (!model.contains(_polyline))) {
			selectPolyline(null, null);
		}
	}
}
