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

import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.TransformMatrix;
import org.vectomatic.common.model.style.Color;

import com.google.gwt.user.client.Timer;

/**
 * Class to manage the 2D compass which enables rotating and scaling the
 * drawing view
 */
public class Compass {
	public static final int NONE = -1;
	public static final int TRANSLATE_N = 0;
	public static final int TRANSLATE_S = 1;
	public static final int TRANSLATE_W = 2;
	public static final int TRANSLATE_E = 3;
	public static final int ROTATE = 4;
	public static final int SCALE = 5;
	private static final float A0 = (float)(Math.PI * 2 / 360 * 7);
	private static final float A1 = (float)(Math.PI * 2 / 360 * 12);
	private static final float A2 = (float)(Math.PI * 2 / 360 * 4);
	private static final float W = 0.15f;
	private static final Point[] WHEEL = new Point[10];
	static {
		WHEEL[0] = new Point(
			(float)(Math.cos(Math.PI * 1.5 - A0) * (1 - 0.5* W)),
			(float)(Math.sin(Math.PI * 1.5 - A0) * (1 - 0.5* W)));			
		WHEEL[1] = new Point(
			(float)(Math.cos(Math.PI * 1.5 - A1)),
			(float)(Math.sin(Math.PI * 1.5 - A1)));
		WHEEL[2] = new Point(
			(float)(Math.cos(Math.PI * 1.5 + A1)),
			(float)(Math.sin(Math.PI * 1.5 + A1)));
		WHEEL[3] = new Point(
			(float)(Math.cos(Math.PI * 1.5 + A0) * (1 - 0.5* W)),
			(float)(Math.sin(Math.PI * 1.5 + A0) * (1 - 0.5* W)));
		WHEEL[4] = new Point(
			(float)(Math.cos(Math.PI * 1.5 + A1)) * (1 - W),
			(float)(Math.sin(Math.PI * 1.5 + A1)) * (1 - W));
		WHEEL[5] = new Point(
			(float)(Math.cos(Math.PI * 1.5 - A1)) * (1 - W),
			(float)(Math.sin(Math.PI * 1.5 - A1)) * (1 - W));
		WHEEL[6] = new Point(
			(float)(Math.cos(Math.PI * 1.5 - A0 + A2)) * (1 - W),
			(float)(Math.sin(Math.PI * 1.5 - A0 + A2)) * (1 - W));
		WHEEL[7] = new Point(
			(float)(Math.cos(Math.PI * 1.5 - A0 + A2)) * (1 - W),
			-(float)(Math.sqrt(1 - Math.cos(Math.PI * 1.5 - A0 + A2) * (1 - W) * Math.cos(Math.PI * 1.5 - A0 + A2) * (1 - W))));
		WHEEL[8] = new Point(
			(float)(Math.cos(Math.PI * 1.5 + A0 - A2)) * (1 - W),
			(float)(Math.sin(Math.PI * 1.5 + A0 - A2)) * (1 - W));
		WHEEL[9] = new Point(
			(float)(Math.cos(Math.PI * 1.5 + A0 - A2)) * (1 - W),
			-(float)(Math.sqrt(1 - Math.cos(Math.PI * 1.5 - A0 + A2) * (1 - W) * Math.cos(Math.PI * 1.5 - A0 + A2) * (1 - W))));
	}
	private static final int DELAY = 100;

	private DrawingView _view;
	private Point[] _wheel;
	private TransformMatrix _m, _mTmp;
	private Point _p1, _p2, _c;
	private float _r, _radius, _r0, _r1, _s;
	private float[] _x, _y;
	private int _pick;
	private boolean _isMouseDown;
	private Timer _northTimer = new Timer() {
		@Override
		public void run() {
			_p1.x = 0f;
			_p1.y = 10f;
			translate(_p1);
			if (_pick == TRANSLATE_N && _isMouseDown) {
				schedule(DELAY);
			}
		}		
	};
	private Timer _southTimer = new Timer() {
		@Override
		public void run() {
			_p1.x = 0f;
			_p1.y = -10f;
			translate(_p1);
			if (_pick == TRANSLATE_S && _isMouseDown) {
				schedule(DELAY);
			}
		}		
	};
	private Timer _westTimer = new Timer() {
		@Override
		public void run() {
			_p1.x = 10f;
			_p1.y = 0f;
			translate(_p1);
			if (_pick == TRANSLATE_W && _isMouseDown) {
				schedule(DELAY);
			}
		}		
	};
	private Timer _eastTimer = new Timer() {
		@Override
		public void run() {
			_p1.x = -10f;
			_p1.y = 0f;
			translate(_p1);
			if (_pick == TRANSLATE_E && _isMouseDown) {
				schedule(DELAY);
			}
		}		
	};
	
	public Compass(DrawingView view) {
		_c = new Point();
		_view = view;
		_p1 = new Point();
		_p2 = new Point();
		_m = new TransformMatrix();
		_mTmp = new TransformMatrix();
		_wheel = new Point[WHEEL.length];
		for (int i = 0; i < _wheel.length;i++) {
			_wheel[i] = new Point();
		}
		_x = new float[29];
		_y = new float[29];
	}
	
	public void setPosition(Point c) {
		c.copyTo(_c);
		update();
	}
	
	public Point getPosition() {
		return _c;
	}
	public float getRadius() {
		return _radius;
	}
	public void setRadius(float radius) {
		_radius = radius;
		update();
	}
	
	private void update() {
		_r = 1f;
		_s = 1f;
		float D = _radius * 0.3f;
		float H = _radius * 0.15f;
		float E = _radius * 0.02f;
		_x[0] = _c.x - D; _y[0] = _c.y - D - E;	// N
		_x[1] = _c.x - D; _y[1] = _c.y - D - H;
		_x[2] = _c.x; _y[2] = _c.y -D - 2 * H;
		_x[3] = _c.x + D; _y[3] = _c.y - D - H;
		_x[4] = _c.x + D; _y[4] = _c.y - D - E;
		_x[5] = _c.x - D; _y[5] = _c.y + D + E;	// S
		_x[6] = _c.x - D; _y[6] = _c.y + D + H;
		_x[7] = _c.x; _y[7] = _c.y + D + 2 * H;
		_x[8] = _c.x + D; _y[8] = _c.y + D + H;
		_x[9] = _c.x + D; _y[9] = _c.y + D + E;
		_x[10] = _c.x - D - E; _y[10] = _c.y - D; // W
		_x[11] = _c.x - D - H; _y[11] = _c.y - D;
		_x[12] = _c.x - D - 2 * H; _y[12] = _c.y;
		_x[13] = _c.x - D - H; _y[13] = _c.y + D;
		_x[14] = _c.x - D - E; _y[14] = _c.y + D;
		_x[15] = _c.x + D + E; _y[15] = _c.y - D; // E
		_x[16] = _c.x + D + H; _y[16] = _c.y - D;
		_x[17] = _c.x + D + 2 * H; _y[17] = _c.y;
		_x[18] = _c.x + D + H; _y[18] = _c.y + D;
		_x[19] = _c.x + D + E; _y[19] = _c.y + D;
		_x[20] = _c.x + _radius + H; _y[20] = _c.y - _radius; // slider
		_x[21] = _c.x + _radius + 2 * H; _y[21] = _c.y - _radius;
		_x[22] = _c.x + _radius + 2 * H; _y[22] = _c.y + _radius;
		_x[23] = _c.x + _radius + H; _y[23] = _c.y + _radius;
		_x[24] = _c.x + _radius + H - E; // slider knob
		_x[25] = _c.x + _radius + 2 * H + E;
		_x[26] = _c.x + _radius + 3 * H;
		_x[27] = _c.x + _radius + 2 * H + E;
		_x[28] = _c.x + _radius + H - E;
		setRotation(0f);
		setScaling(0.5f);
		_pick = NONE;		
	}
	
	public void translate(Point p) {
		_view.vectorToRefCoordinates(p);
		_view.getTranslation(_p2);
		_p2.add(p);
		_view.setTranslation(_p2);		
	}
	
	public float getRotation() {
		return _r;
	}
	
	public void setRotation(float r) {
		if (_r != r) {
			_r = r;
			_m.rotation(r).preMultiply(_mTmp.scaling(_radius, _radius)).preMultiply(_mTmp.translation(_c.x, _c.y));
			for (int i = 0; i < WHEEL.length;i++) {
				WHEEL[i].transform(_m, _wheel[i]);
			}
			_view.setRotation(r);
		}
	}
	
	public void setScaling(float s) {
		if (_s != s) {
			_s = s;
			float H = 0.15f;
			float y = s * ((2 - H) *_radius);
			float h = _radius * H;
			_y[24] = _c.y - _radius + y;
			_y[25] = _c.y - _radius + y;
			_y[26] = _c.y - _radius + y + h * 0.5f;
			_y[27] = _c.y - _radius + y + h;
			_y[28] = _c.y - _radius + y + h;
			_p1.x = (s <= 0.5f) ? (0.125f + 1.75f * s) : ( 1 + (s - 0.5f) * 14f); _p1.y = _p1.x;
			_view.setScaling(_p1);
		}
	}
	public float getScaling() {
		return _s;
	}
	
	public int pick(Point p) {
		_pick = NONE;
		float d = p.subtract(_c, _p1).length();
		if (p.x > _x[0] && p.x < _x[3] && p.y > _y[2] && p.y < _y[0]) {
			_pick = TRANSLATE_N;
		} else if (p.x > _x[5] && p.x < _x[8] && p.y < _y[7] && p.y > _y[5]) {
			_pick = TRANSLATE_S;
		} else if (p.x > _x[12] && p.x < _x[10] && p.y > _y[10] && p.y < _y[13]) {
			_pick = TRANSLATE_W;
		} else if (p.x < _x[17] && p.x > _x[15] && p.y > _y[15] && p.y < _y[18]) {
			_pick = TRANSLATE_E;
		} else if (d < _radius && d > (1 - W) * _radius) {
			_pick = ROTATE;
		} else if (p.x > _x[24] && p.x < _x[26] && p.y > _y[24] && p.y < _y[27]) {
			_pick = SCALE;
		}
		return _pick;
	}
	
	public void render() {
		_view.beginPath();
		_view.moveTo(_wheel[0].x, _wheel[0].y);
		_view.lineTo(_wheel[1].x, _wheel[1].y);
		_view.arc(_c.x, _c.y, _radius, (float)(Math.PI * 1.5 - A1) + _r, (float)(Math.PI * 1.5 + A1) + _r, true);
		_view.lineTo(_wheel[3].x, _wheel[3].y);
		_view.lineTo(_wheel[4].x, _wheel[4].y);
		_view.arc(_c.x, _c.y, _radius * (1 - W), (float)(Math.PI * 1.5 + A1) + _r, (float)(Math.PI * 1.5 - A1) + _r, false);
		_view.lineTo(_wheel[0].x, _wheel[0].y);
		_view.moveTo(_wheel[6].x, _wheel[6].y);
		_view.lineTo(_wheel[7].x, _wheel[7].y);
		_view.lineTo(_wheel[8].x, _wheel[8].y);
		_view.lineTo(_wheel[9].x, _wheel[9].y);
		_view.setStrokeStyle(Color.BLACK.toString());
		_view.setLineJoin("bevel");
		_view.setFillStyle("#C3D9FF");
		if (_pick == ROTATE) {
			_view.fill();
		}
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[0], _y[0]);
		_view.lineTo(_x[1], _y[1]);
		_view.lineTo(_x[2], _y[2]);
		_view.lineTo(_x[3], _y[3]);
		_view.lineTo(_x[4], _y[4]);
		_view.lineTo(_x[0], _y[0]);
		if (_pick == TRANSLATE_N) {
			_view.fill();
		}
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[5], _y[5]);
		_view.lineTo(_x[6], _y[6]);
		_view.lineTo(_x[7], _y[7]);
		_view.lineTo(_x[8], _y[8]);
		_view.lineTo(_x[9], _y[9]);
		_view.lineTo(_x[5], _y[5]);
		if (_pick == TRANSLATE_S) {
			_view.fill();
		}
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[10], _y[10]);
		_view.lineTo(_x[11], _y[11]);
		_view.lineTo(_x[12], _y[12]);
		_view.lineTo(_x[13], _y[13]);
		_view.lineTo(_x[14], _y[14]);
		_view.lineTo(_x[10], _y[10]);
		if (_pick == TRANSLATE_W) {
			_view.fill();
		}
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[15], _y[15]);
		_view.lineTo(_x[16], _y[16]);
		_view.lineTo(_x[17], _y[17]);
		_view.lineTo(_x[18], _y[18]);
		_view.lineTo(_x[19], _y[19]);
		_view.lineTo(_x[15], _y[15]);
		if (_pick == TRANSLATE_E) {
			_view.fill();
		}
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[20], _y[20]);
		_view.lineTo(_x[21], _y[21]);
		_view.lineTo(_x[22], _y[22]);
		_view.lineTo(_x[23], _y[23]);
		_view.lineTo(_x[20], _y[20]);
		_view.stroke();
		_view.beginPath();
		_view.moveTo(_x[24], _y[24]);
		_view.lineTo(_x[25], _y[25]);
		_view.lineTo(_x[26], _y[26]);
		_view.lineTo(_x[27], _y[27]);
		_view.lineTo(_x[28], _y[28]);
		_view.lineTo(_x[24], _y[24]);
		if (_pick != SCALE) {
			_view.setFillStyle("rgb(255,255,255)");
		}
		_view.fill();
		_view.stroke();
	}

	
	public void mouseDown(Point p) {
		_isMouseDown = true;
		switch(_pick) {
			case ROTATE:
				_r0 = _r;
				p.subtract(_c, _p1);
				_r1 = (float)(Math.acos(_p1.x / _p1.length()));
				if (p.y > _c.y) {
					_r1 = (float)(2 * Math.PI - _r1);
				}
				break;
			case TRANSLATE_N:
				_northTimer.run();
				break;
			case TRANSLATE_S:
				_southTimer.run();
				break;
			case TRANSLATE_W:
				_westTimer.run();
				break;
			case TRANSLATE_E:
				_eastTimer.run();
				break;
		}
	}

	public void mouseMove(Point p) {
		if (_isMouseDown) {
			if (_pick == ROTATE) {
				p.subtract(_c, _p1);
				float r2 = (float)(Math.acos(_p1.x / _p1.length()));
				if (p.y > _c.y) {
					r2 = (float)(2 * Math.PI - r2);
				}
				setRotation(_r0 - r2 + _r1);
				//_area.setText("r1 = " + _r1 + " r2 = " + r2 + " acos = " + (_p.x / _p.length()) + " p = " + p);
			} else if (_pick == SCALE) {
				float s = 0f;
				if (p.y > _c.y - _radius) {
					if (p.y < _c.y + _radius) {
						s = 0.5f * (p.y - (_c.y - _radius)) / _radius;
					} else {
						s = 1f;
					}
				}
				setScaling(s);
			}
		}
	}

	public void mouseUp() {
		_isMouseDown = false;
	}

}
