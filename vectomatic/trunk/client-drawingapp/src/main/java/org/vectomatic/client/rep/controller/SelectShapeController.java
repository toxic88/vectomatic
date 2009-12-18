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
import java.util.Iterator;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.ICommand;
import org.vectomatic.client.rep.command.TransformShapeCommand;
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.BoundingBox;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Rect;
import org.vectomatic.common.model.geometry.ShapeGroup;
import org.vectomatic.common.model.geometry.TransformMatrix;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.NoneStyle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * Controller to respond to translate/rotate/resize shape requests and
 * turn them into TransformShapeCommand
 */
public class SelectShapeController extends ControllerBase {
	private static final float HANDLE_SIZE = 3f;
	private static final int HANDLE_MOVE = 10;
	private static final int HANDLE_NONE = 11;
		
	/**
	 * The cursor hovers over the selection and changes to reflect
	 * possible actions
	 */
	private static final int MODE_HOVER = 0;
	/**
	 * The user selects shapes using the selection rectangle
	 */
	private static final int MODE_RECT = 1;
	/**
	 * The user translates the selection
	 */
	private static final int MODE_TRANSLATE = 2;
	/**
	 * The user rotates the selection
	 */
	private static final int MODE_ROTATE = 3;
	/**
	 * The user scales the selection along the X axis
	 */
	private static final int MODE_SCALE = 4;
	
	private static final Cursor[] _cursors = {
		Cursor.CURSOR_NW_RESIZE,
		Cursor.CURSOR_SW_RESIZE,
		Cursor.CURSOR_SE_RESIZE,
		Cursor.CURSOR_NE_RESIZE,
		Cursor.CURSOR_N_RESIZE,
		Cursor.CURSOR_W_RESIZE,
		Cursor.CURSOR_S_RESIZE,
		Cursor.CURSOR_E_RESIZE,
		Cursor.CURSOR_MOVE,
		Cursor.CURSOR_ROTATE,
		Cursor.CURSOR_POINTER,
		Cursor.CURSOR_POINTER
	};

	private TransformMatrix _mTmp, _mOld, _mInv;
	private Rect _rect;
	private Point _p0, _p1, _p2, _p3;
	private float _r1, _r2, _r3;
	private int _mode;
	private int _handle;
	private MouseControllerButton _button;
	private DeleteController _deleteController;
	private ContextualMenuVisitor _contextVisitor;
	
	public SelectShapeController(RepApplication app, DeleteController deleteController, ContextualMenuVisitor contextVisitor) {
		super(app);
		_deleteController = deleteController;
		_contextVisitor = contextVisitor;
		_button = new MouseControllerButton(_app.getIcons().selectIcon().createImage(), _app.getConstants().selectCommand(), this);
		_mTmp = new TransformMatrix();
		_mOld = new TransformMatrix();
		_mInv = new TransformMatrix();
		_rect = new Rect();
		_rect.setAttribute(Attribute.LINE_STYLE, Color.BLACK);
		_rect.setAttribute(Attribute.LINE_OPACITY, new FloatAttributeValue(1f));
		_rect.setAttribute(Attribute.FILL_STYLE, NoneStyle.NONE);
		_rect.setAttribute(Attribute.FILL_OPACITY, new FloatAttributeValue(0f));
		_rect.setAttribute(Attribute.LINE_WIDTH, new FloatAttributeValue(1f));

		_p0 = new Point();
		_p1 = new Point();
		_p2 = new Point();
		_p3 = new Point();
	}

	public MouseControllerButton getButton() {
		return _button;
	}

	@Override
	public void render(DrawingView view) {
		Shape rootShape = _app.getSelection().getRootShape();
		if (rootShape != null) {
			BoundingBox bbox = rootShape.getBoundingBox();
			rootShape.getTransform().copyTo(_mTmp);

			/*if (_mode == MODE_ROTATE) {
				view.beginPath();
				bbox.getPoint(BoundingBox.PT_C, _p0).transform(m);
				view.moveTo(_p0.x, _p0.y);
				// Radius of the feedback circle is 80% of the bbox smaller dimension
				float r = 0.8f * 0.5f * Math.min(bbox.getWidth(), bbox.getHeight());
				RepApplication.app.debugArea.setText(Float.toString(r));
				view.moveTo(_p0.x, _p0.y);
				view.lineTo(_p0.x + r * (float)Math.cos (_r2), _p0.y + r * (float)Math.sin (_r2));
				view.arc(_p0.x, _p0.y, r, _r2, _r3, true);
				view.moveTo(_p0.x + r * (float)Math.cos (_r3), _p0.y + r * (float)Math.sin (_r3));
				view.lineTo(_p0.x, _p0.y);
				view.stroke();
			}*/

			rootShape.acceptVisitor(view.getRenderer());
			Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
			Color.BLACK.acceptVisitor(view.getFillStyleVisitor());
			view.setGlobalAlpha(1f);
			view.setLineWidth(1f);
			view.beginPath();
			bbox.getPoint(BoundingBox.PT_NE, _p0).transform(_mTmp);
			view.moveTo(_p0.x, _p0.y);
			for (int i = BoundingBox.PT_NW; i <= BoundingBox.PT_NE; i++) {
				bbox.getPoint(i, _p0).transform(_mTmp);
				view.lineTo(_p0.x, _p0.y);
			}
			bbox.getPoint(BoundingBox.PT_E, _p0);
			bbox.getPoint(BoundingBox.PT_E, _p0).transform(_mTmp);
			view.moveTo(_p0.x, _p0.y);
			bbox.getPoint(BoundingBox.PT_R, _p0);
			_p0.transform(_mTmp);
			view.lineTo(_p0.x, _p0.y);
			view.stroke();
			view.beginPath();
			bbox.getPoint(BoundingBox.PT_R, _p0);
			_p0.transform(_mTmp);
			view.arc(_p0.x, _p0.y, HANDLE_SIZE, 0f, (float)(2 * Math.PI), true);
			view.fill();
			for (int i = BoundingBox.PT_NW; i <= BoundingBox.PT_E; i++) {
				bbox.getPoint(i, _p0).transform(_mTmp);
				view.fillRect(_p0.x - HANDLE_SIZE, _p0.y - HANDLE_SIZE, HANDLE_SIZE *  2, HANDLE_SIZE * 2);
			}
		}
		if (_mode == MODE_RECT) {
			Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
			Color.BLACK.acceptVisitor(view.getFillStyleVisitor());
			view.setGlobalAlpha(1f);
			view.getRenderer().visitRect(_rect);
		}
	}
	
	@Override
	public void activate(DrawingView view) {
		_mode = MODE_HOVER;
		_app.getSelection().select(new ArrayList<Shape>());
		view.setCursor(_cursors[_handle]);
	}

	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		p.copyTo(_p1);
		view.toModelCoordinates(p);
		Event event = DOM.eventGetCurrentEvent();
		if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
			MenuBar bar = null;
			if (view.getPicker().pick(p, _app.getSelection().iterator()) == null) {
				Shape shape = view.getPicker().pick(p, _app.getModel().reverseIterator());
				if (shape != null) {
					List<Shape> newSelection = new ArrayList<Shape>();
					newSelection.add(shape);
					_app.getSelection().select(newSelection);				
					bar = _contextVisitor.getContextualMenu(_app.getSelection());
				}
			} else {
				bar = _contextVisitor.getContextualMenu(_app.getSelection());
			}
			if (bar != null) {
				ControllerContextItem.popup.setWidget(bar);
				ControllerContextItem.popup.setPopupPosition(DOM.eventGetClientX(event), DOM.eventGetClientY(event));
				ControllerContextItem.popup.show();
			}
		} else {
			boolean ctrlPressed = (modifiers & KeyboardListener.MODIFIER_CTRL) != 0;
			Shape rootShape = _app.getSelection().getRootShape();
			
			_handle = getHandle(view, p);
			switch(_handle) {
				case BoundingBox.PT_NW:
				case BoundingBox.PT_SW:
				case BoundingBox.PT_SE:
				case BoundingBox.PT_NE:
				case BoundingBox.PT_N:
				case BoundingBox.PT_W:
				case BoundingBox.PT_S:
				case BoundingBox.PT_E:
					_mode = MODE_SCALE;
					rootShape.getTransform().copyTo(_mOld);
					// Store the first point clicked
					rootShape.getScaling(_p1);
					rootShape.getTransform().invert(_mInv);
					break;
					
				case BoundingBox.PT_R:
					_mode = MODE_ROTATE;
					rootShape.getTransform().copyTo(_mOld);
					
					// Store the shape original rotation and the original transform matrix
					_r1 = rootShape.getRotation();
					rootShape.getTransform().invert(_mInv);
					
					// Compute the vector from the selection bounding box center
					// to the mousedown point
					p.transform(_mInv).subtract(rootShape.getBoundingBox().getPoint(BoundingBox.PT_C, _p0));
					
					// Compute the angle (PT_R, PT_C, p)
					_r2 = (float)(Math.acos(p.x / p.length()));
					if (p.y < 0) {
						_r2 = -_r2;
					}
					_r3 = _r2;
					break;
				case HANDLE_MOVE:
					if (ctrlPressed) {
						List<Shape> oldSelection = _app.getSelection().getSelectedShapes();
						List<Shape> newSelection = new ArrayList<Shape>(oldSelection);
						newSelection.remove(view.getPicker().pick(p, _app.getSelection().iterator()));
						_app.getSelection().select(newSelection);
						_mode = MODE_HOVER;
					} else {
						_mode = MODE_TRANSLATE;
						rootShape.getTransform().copyTo(_mOld);
						// Store the first point clicked
						p.copyTo(_p1);
						// Store the vector from the clicked point to the
						// bounding box center
						p.subtract(rootShape.getTranslation(_p2), _p2);
					}
					break;
				case HANDLE_NONE:
					{
						Shape shape = view.getPicker().pick(p, _app.getModel().reverseIterator());
						if (shape == null) {
							RepApplication.app.debugPrint("rect");
							if (!ctrlPressed) {
								_app.getSelection().select(new ArrayList<Shape>());
							}
							_mode = MODE_RECT;
							_rect.setTranslation(p);
							_rect.setRotation(-view.getRotation());
							_rect.setScaling(Point.UNIT);
							view.getScaling(_p3);
						} else {
							RepApplication.app.debugPrint("select");
							if (!ctrlPressed) {
								_app.getSelection().select(new ArrayList<Shape>());
							}
							List<Shape> oldSelection = _app.getSelection().getSelectedShapes();
							List<Shape> newSelection = new ArrayList<Shape>(oldSelection);
							newSelection.add(shape);
							rootShape = _app.getSelection().select(newSelection);
							_mode = MODE_TRANSLATE;
							rootShape.getTransform().copyTo(_mOld);
							// Store the first point clicked
							p.copyTo(_p1);
							// Store the vector from the clicked point to the
							// bounding box center
							p.subtract(rootShape.getTranslation(_p2), _p2);
						}
		
					}
					break;
			}
		}
	}
	
	private int getHandle(DrawingView view, Point p) {
		Shape rootShape = _app.getSelection().getRootShape();
		if (rootShape != null) {
			BoundingBox bbox = rootShape.getBoundingBox();
			
			// Test click in N,E,S,W,NE,NW,SW,SE control points
			for (int i = BoundingBox.PT_NW; i <= BoundingBox.PT_E; i++) {
				bbox.getPoint(i, _p0).transform(rootShape.getTransform());
				if (p.x >= _p0.x - HANDLE_SIZE && p.x <= _p0.x + HANDLE_SIZE
				 && p.y >= _p0.y - HANDLE_SIZE && p.y <= _p0.y + HANDLE_SIZE) {
					return i;
				}
			}
			
			// Test click in R control point
			bbox.getPoint(BoundingBox.PT_R, _p0).transform(rootShape.getTransform());
			if (p.subtract(_p0, _p0).length() <= HANDLE_SIZE) {
				return BoundingBox.PT_R;
			}

			// Test click in one of the selected shapes.
			if (view.getPicker().pick(p, _app.getSelection().iterator()) != null) {
				return HANDLE_MOVE;
			}
		}
		return HANDLE_NONE;
	}
	
	@Override
	public void mouseMove(DrawingView view, Point p, int modifiers) {
		p.copyTo(_p0);
		view.toModelCoordinates(p);
		Shape rootShape = _app.getSelection().getRootShape();
		switch(_mode) {
			case MODE_RECT:
				{
					_p0.add(_p1, _p2).multiply(0.5f).subtract(_p1);
					_p2.x /= _p3.x;
					_p2.y /= _p3.y;
					_rect.setScaling(_p2);
					float cos = (float)Math.cos(view.getRotation());
					float sin = (float)Math.sin(view.getRotation());
					p.x = p.x - _p2.x * cos - _p2.y * sin;
					p.y = p.y + _p2.x * sin - _p2.y * cos;
					_rect.setTranslation(p);			
				}
				break;
			case MODE_HOVER:
				{
					_handle = getHandle(view, p);
					view.setCursor(_cursors[_handle]);
				}
				break;
			case MODE_TRANSLATE:
				{
					rootShape.setTranslation(p.subtract(_p2));
				}
				break;
			case MODE_ROTATE:
				{
					// Compute the vector from the selection bounding box center
					// to the mousedown point
					p.transform(_mInv).subtract(rootShape.getBoundingBox().getPoint(BoundingBox.PT_C, _p0));
					
					// Compute the angle (PT_R, PT_C, p)
					float d = p.length();
					if (d > 0f) {
						_r3 = (float)(Math.acos(p.x / d));
						if (p.y < 0) {
							_r3 = -_r3;
						}
						float dr = _r3 - _r2;
						rootShape.getScaling(_p0);
						if (_p0.x * _p0.y < 0) {
							dr = - dr;
						}
						rootShape.setRotation(_r1 + dr);
					}
				}
				break;
			case MODE_SCALE:
				{
					BoundingBox bbox = rootShape.getBoundingBox();
					rootShape.getScaling(_p2);
					bbox.getPoint(BoundingBox.PT_C, _p3);
					bbox.getPoint(_handle, _p0).subtract(_p3);
					p.transform(_mInv).subtract(_p3);
					switch(_handle) {
						case BoundingBox.PT_NW:
						case BoundingBox.PT_SW:
						case BoundingBox.PT_SE:
						case BoundingBox.PT_NE:
						case BoundingBox.PT_N:
						case BoundingBox.PT_S:
							_p2.y = _p1.y * p.y / _p0.y;
					}
					switch(_handle) {
						case BoundingBox.PT_NW:
						case BoundingBox.PT_SW:
						case BoundingBox.PT_SE:
						case BoundingBox.PT_NE:
						case BoundingBox.PT_W:
						case BoundingBox.PT_E:
							_p2.x = _p1.x * p.x / _p0.x;
					}
					if (rootShape instanceof ShapeGroup && ((ShapeGroup)rootShape).containsRotatedShape()) {
						// scaling(a,b) and rotation(r) are not commutative if a != b
						// such a transformation would skew the shape
						_p2.x = Math.max(_p2.x, _p2.y);
						_p2.y = _p2.x;
					}
					rootShape.setScaling(_p2);
				}
				break;
		}
	}

	@Override
	public void mouseUp(DrawingView view, Point p, int modifiers) {
		mouseMove(view, p, modifiers);
		deactivate(view);
	}
	
	@Override
	public void keyDown(DrawingView view, char keyCode, int modifiers) {
		RepApplication.app.debugPrint("keyDown: " + keyCode);
		if (keyCode == KeyboardListener.KEY_DELETE ||  keyCode == KeyboardListener.KEY_BACKSPACE && _app.getSelection().getSelectedShapes().size() > 0) {
			_deleteController.activate(view);
		}
	}
	
	@Override
	public void deactivate(DrawingView view) {
		Shape rootShape = _app.getSelection().getRootShape();
		switch(_mode) {
			case MODE_RECT:
				{
					List<Shape> newSelection = new ArrayList<Shape>();
					// Skip selection if the selection rect has a 0
					// width or height
					_rect.getScaling(_p0);
					if (_p0.x != 0f && _p0.y != 0f) {
						// Select all the shapes contained in the selection rect
						Iterator<Shape> iterator = _app.getModel().iterator();
						RepApplication.app.debugPrint("size = " + _app.getModel().count());
						while (iterator.hasNext()) {
							Shape shape = iterator.next();
							BoundingBox bbox = shape.getBoundingBox();
							boolean inSelectionRect = true;
							
							// Transform the bounding box vertices into the selection rect coordinate system
							shape.getTransform().preMultiply(_rect.getTransform().invert(_mTmp), _mTmp);
							for (int i = BoundingBox.PT_NW; i <= BoundingBox.PT_NE; i++) {
								if (!_rect.getBoundingBox().containsPoint(bbox.getPoint(i, _p1).transform(_mTmp))) {
									inSelectionRect = false;
									break;
								}
							}
							if (inSelectionRect) {
								newSelection.add(shape);
							}
						}
					}
					_app.getSelection().select(newSelection);
				}
				break;
			case MODE_TRANSLATE:
				{
					// Compute the translation in _p0
					rootShape.getTranslation(_p0).subtract(_p1).add(_p2);
					if (_p0.squaredLength() > 0f) {
						// Compute the transform T = M(k+1).Inv(M(k))
						_mOld.invert(_mTmp).preMultiply(rootShape.getTransform());
						ICommand command = new TransformShapeCommand(_app, _mTmp, _mOld, TransformShapeCommand.TRANSLATE, _app.getSelector(), this);
						command.execute();
						_app.getHistory().addCommand(command);
					}
				}
				break;
			case MODE_ROTATE:
				{
					float dr = _r3 - _r2;
					if (dr != 0f) {
						// Compute the transform T = M(k+1).Inv(M(k))
						_mOld.invert(_mTmp).preMultiply(rootShape.getTransform());
						ICommand command = new TransformShapeCommand(_app, _mTmp, _mOld, TransformShapeCommand.ROTATE, _app.getSelector(), this);
						command.execute();
						_app.getHistory().addCommand(command);
					}
				}
				break;
			case MODE_SCALE:
				{
					rootShape.getScaling(_p2);
					if (!_p1.equals(_p2)) {
						// Compute the transform T = M(k+1).Inv(M(k))
						_mOld.invert(_mTmp).preMultiply(rootShape.getTransform());
						ICommand command = new TransformShapeCommand(_app, _mTmp, _mOld, TransformShapeCommand.SCALE, _app.getSelector(), this);
						command.execute();
						_app.getHistory().addCommand(command);
					}
				}
				break;
		}
		_mode = MODE_HOVER;
		RepApplication.app.debugPrint("end deactivate1");
		RepApplication.app.debugPrint("end deactivate2");
	}
}
