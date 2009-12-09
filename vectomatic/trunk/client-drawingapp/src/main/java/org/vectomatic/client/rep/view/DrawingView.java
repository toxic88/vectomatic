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
package org.vectomatic.client.rep.view;

import java.util.Iterator;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.Compass;
import org.vectomatic.client.rep.controller.IController;
import org.vectomatic.client.rep.controller.RepresentationController;
import org.vectomatic.common.model.DrawingModel;
import org.vectomatic.common.model.IStyleVisitor;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.NoneStyle;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelVelocity;
import com.google.gwt.user.client.ui.Widget;
import com.gwt.components.client.Canvas;
import com.gwtext.client.widgets.Resizable;
import com.gwtext.client.widgets.ResizableConfig;
import com.gwtext.client.widgets.event.ResizableListenerAdapter;

/**
 * Class to render the geometry. This is where most
 * of the action occurs. Events are caught and routed
 * the to controllers. Rendering is done.
 */
public class DrawingView extends Canvas {
	private static final int MARGIN = 5;
	private float _r;
	private Point _s;
	private Point _t;
	private transient boolean _dirty;
	private transient TransformMatrix _m;
	private TransformMatrix _mInv;
	
	private RepresentationController _repController;
	private IController _controller;
	private RenderVisitor _renderer;
	private PickVisitor _picker;
	private Cursor _cursor;
	private DrawingModel _model;
	private Compass _compass;
	private boolean _compassMode;
	private IStyleVisitor _strokeStyleVisitor = new IStyleVisitor() {

		public void visitColor(Color color) {
			String style = color.toString();
			if (!style.equals(getStrokeStyle())) {
				setStrokeStyle(style);
			}
		}

		public void visitNoneStyle(NoneStyle none) {
			// TODO Auto-generated method stub
			
		}
		
	};
	private IStyleVisitor _fillStyleVisitor = new IStyleVisitor() {

		public void visitColor(Color color) {
			String style = color.toString();
			if (!style.equals(getFillStyle())) {
				setFillStyle(style);
			}
		}

		public void visitNoneStyle(NoneStyle none) {
			// TODO Auto-generated method stub
			
		}
		
	};
		
	public DrawingView(RepresentationController repController, DrawingModel model, int width, int height) {
		super(width, height);
		saveContext();
		_t = new Point();
		_s = new Point(Point.UNIT);
		_m = new TransformMatrix();

		_repController = repController;
		_model = model;
		_mInv = new TransformMatrix();
		_compass = new Compass(this);
		_compass.setPosition(new Point(width - 70, 50));
		_compass.setRadius(45);
		_renderer = new RenderVisitor(this);
		_picker = new PickVisitor(this);
		addMouseListener(new MouseListenerAdapter() {
			Point p = new Point();
			@Override
			public void onMouseDown(Widget sender, int x, int y) {
				p.x = x;
				p.y = y;
				//RepApplication.app.debugArea.setText(toModelCoordinates(p, new Point()).toString());
				if (_compass.pick(p) != Compass.NONE) {
					_compassMode = true;
					_compass.mouseDown(p);
				} else {
					_compassMode = false;
					//p.transform(_mInv);
					RepApplication.app.debugPrint("x=" + p.x + " y=" + p.y);
					_controller.mouseDown(DrawingView.this, p, modifiers);
				}
			}
			@Override
			public void onMouseMove(Widget sender, int x, int y) {
				p.x = x;
				p.y = y;
				if (_compassMode) {
					_compass.mouseMove(p);
				} else {
					_compass.pick(p);
					//p.transform(_mInv);
					_controller.mouseMove(DrawingView.this, p, modifiers);					
				}
			}
			@Override
			public void onMouseUp(Widget sender, int x, int y) {
				p.x = x;
				p.y = y;
				if (_compassMode) {
					_compass.mouseUp();
					_compassMode = false;
				} else {
					//p.transform(_mInv);
					_controller.mouseUp(DrawingView.this, p, modifiers);
				}
			}
		});
		addKeyboardListener(new KeyboardListener() {
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				_controller.keyDown(DrawingView.this,keyCode, modifiers);
			}
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				_controller.keyPress(DrawingView.this,keyCode, modifiers);
			}
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				_controller.keyUp(DrawingView.this,keyCode, modifiers);
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			Point p = new Point();
			public void onMouseWheel(Widget sender, MouseWheelVelocity velocity) {
				int delta = 10 * velocity.getDeltaY();
				if ((modifiers & KeyboardListener.MODIFIER_CTRL) != 0) {
					if (delta > 0) {
						float s = _compass.getScaling();
						_compass.setScaling(s + 0.1f * (1 - s));
					} else {
						float s = _compass.getScaling();
						_compass.setScaling(s - 0.1f * s);
					}
				} else {
					if ((modifiers & KeyboardListener.MODIFIER_ALT) != 0) {
						p.x = delta;
						p.y = 0f;
					} else {
						p.x = 0f;
						p.y = delta;
					}
					_compass.translate(p);
				}
			}
		});
	}

	public float getRotation() {
		return _r;
	}
	
	public void setRotation(float r) {
		_r = r;
		_dirty = true;
	}
	
	public Point getScaling(Point s) {
		_s.copyTo(s);
		return s;
	}
	
	public void setScaling(Point s) {
		s.copyTo(_s);
		_dirty = true;
	}
	
	public Point getTranslation(Point t) {
		_t.copyTo(t);
		return t;
	}
	
	public void setTranslation(Point t) {
		t.copyTo(_t);
		_dirty = true;
	}
	
	public TransformMatrix getTransform() {
		if (_dirty) {
			updateTransform();
		}
		return _m;
	}
	public TransformMatrix getInverseTransform() {
		return _mInv;
	}
	
	protected void updateTransform() {
		float cos = (float)Math.cos(_r);
		float sin = (float)Math.sin(_r);
		float x1 = _t.x - 0.5f * getWidth();
		float y1 = _t.y - 0.5f * getHeight();
		_m.m11 = _s.x * cos; _m.m12 = -_s.y * sin; _m.m13 = _s.x * cos  * x1 - _s.y * sin * y1 + 0.5f * getWidth();
		_m.m21 = _s.x * sin; _m.m22 =  _s.y * cos; _m.m23 = _s.x * sin  * x1 + _s.y * cos * y1 + 0.5f * getHeight();
		_dirty = false;
	}
	
	public void reset() {
		setTranslation(Point.ZERO);
		_compass.setRotation(0f);
		_compass.setScaling(0.5f);
	}

	public void render() {
		// Restore a clean context with no transformation of any kind set
		// Clear the screen area
		restoreContext();
		clearRect(0, 0, getWidth(), getHeight());
		
		// Set-up a coordinate system transformed according to
		// the specifications defined by the compass
		saveContext();
		translate(0.5f * getWidth(), 0.5f * getHeight());
		rotate(_r);
		scale(_s.x, _s.y);
		translate(-0.5f * getWidth() + _t.x, -0.5f * getHeight() + _t.y);
		getTransform().invert(_mInv);
		
		// Draw the shaded canvas outline
		int width = _repController.getCurrentRep().getWidth();
		int height = _repController.getCurrentRep().getHeight();
		setFillStyle(Color.DARK_GRAY.toString());
		fillRect(MARGIN, height, width, MARGIN);
		fillRect(width, MARGIN, MARGIN, height);
		setStrokeStyle(Color.LIGHT_GRAY.toString());
		strokeRect(0, 0, width, height);

		// Draw the model geometry
		Iterator<Shape> iterator = _model.iterator();
		while (iterator.hasNext()) {
			Shape shape = iterator.next();
			shape.acceptVisitor(_renderer);
		}
		// Draw extra transient geometry currently owned by the current controller
		getController().render(this);
		
		// Restore the clean context with no transformation of any kind set
		// Use this context to draw the compass
		restoreContext();
		_compass.render();
		
		// Save the clean context for the next frame
		saveContext();
	}
	
	/**
	 * Converts a distance in the screen coordinate system
	 * into a distance in the reference coordinate system
	 * @param pixels
	 * @return
	 */
	public float convertToReferenceLength(int screenLength) {
		Point p0 = new Point(0, 0);
		p0.transform(_mInv);
		Point p1 = new Point(screenLength, 0);
		p1.transform(_mInv);
		return p1.subtract(p0).length();
	}
	
	public Point vectorToRefCoordinates(Point v) {
		return vectorToRefCoordinates(v, v);
	}
	
	public Point vectorToRefCoordinates(Point v, Point dest) {
		v.transform(_mInv, dest);
		dest.x -= _mInv.m13;
		dest.y -= _mInv.m23;
		return dest;
	}
	
	/**
	 * Converts a point from screen coordinates to model coordinates
	 * @param p
	 * The point to transfom (in screen coordinates)
	 * @return
	 * The transformed point
	 */
	public Point toModelCoordinates(Point p) {
		return toModelCoordinates(p, p);
	}
	
	public Point toModelCoordinates(Point p, Point dest) {
		return p.transform(_mInv, dest);
	}
	
	public Point toScreenCoordinates(Point p) {
		return toScreenCoordinates(p, p);
	}

	/**
	 * Converts a point from model coordinates to screen coordinates
	 * @param p
	 * The point to transfom (in model coordinates)
	 * @return
	 * The transformed point
	 */
	public Point toScreenCoordinates(Point p, Point dest) {
		return p.transform(_m, dest);
	}

	public void setCursor(Cursor cursor) {
		if (_cursor != cursor) {
			if (_cursor != null) {
				removeStyleName(_cursor.getCssText());
			}
			if (cursor != null) {
				addStyleName(cursor.getCssText());
			}
			_cursor = cursor;
		}
	}

	public void setController(IController controller) {
		_controller = controller;
	}

	public IController getController() {
		return _controller;
	}
	
	public RenderVisitor getRenderer() {
		return _renderer;
	}
	
	public PickVisitor getPicker() {
		return _picker;
	}
	
	public IStyleVisitor getStrokeStyleVisitor() {
		return _strokeStyleVisitor;
	}
	public IStyleVisitor getFillStyleVisitor() {
		return _fillStyleVisitor;
	}
	
    @Override
	protected void onLoad() {
		ResizableConfig config = new ResizableConfig();
		config.setPinned(true);
		config.setHandles(Resizable.SOUTH_EAST);
		config.setMinWidth(320);
		config.setMinHeight(200);
		final Resizable resizable = new Resizable(getElement(), config);
		resizable.addListener(new ResizableListenerAdapter() {
			public void onResize(Resizable self, int width, int height) {
				setWidth(width);
				setHeight(height);
				// Add an extra saveContext here. This is because resizing causes
				// all the push contexts to be lost (and DrawingView expects to have
				// a context pushed when render is invoked)
				saveContext();
				_compass.setPosition(new Point(width - 70, 50));
				_compass.setRadius(45);
			}
		});    	
    }
}
