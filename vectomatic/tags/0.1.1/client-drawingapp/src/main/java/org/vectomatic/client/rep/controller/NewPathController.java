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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.NewShapeCommand;
import org.vectomatic.client.rep.view.Cursor;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.geometry.BezierSegment;
import org.vectomatic.common.model.geometry.LineSegment;
import org.vectomatic.common.model.geometry.Path;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Segment;
import org.vectomatic.common.model.style.Color;

/**
 * Controller to respond to new path requests and turn
 * them into NewShapeCommand
 */
public class NewPathController extends ControllerBase {
	private static final int VERTEX_SIZE = 3;
	private static final int CONTROL_POINT_SIZE = 3;
	private static final float T = 0.5f;
	private enum State {
		S0 { 
			@Override
			State processActivate(NewPathController controller, DrawingView view)  {
				view.setCursor(Cursor.CURSOR_CROSSHAIR);
				controller._segments.clear();
				return S1;
			}
		},
		S1 { 
			@Override
			State processMouseDown(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p0);
				controller._p0.copyTo(controller._p1);
				return S2;
			} 
		},
		S2 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p1);
				return this;
			}		
			@Override
			State processMouseUp(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p1);
				if (controller._p1.distance(controller._p0) > T) {
					controller._p1.copyTo(controller._p2);
					controller._p1.copyTo(controller._p3);
					// The user wants to draw a spline tangent
					return S3;
				}
				// The user wants to draw a line segment
				return S4;
			}
		},
		S3 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p2);
				controller._p2.copyTo(controller._p3);
				if (controller._segments.size() > 0) {
					float tol = view.convertToReferenceLength(VERTEX_SIZE * 2);
					if (controller._p2.distance(controller._p0) < tol) {
						view.setCursor(Cursor.CURSOR_OPEN_POLYLINE);
					} else if (controller._p2.distance(controller._segments.get(0).getStartPoint()) < tol) {
						view.setCursor(Cursor.CURSOR_CLOSED_POLYLINE);
					} else {
						view.setCursor(Cursor.CURSOR_CROSSHAIR);
					}
				}
				return S3;
			}
			@Override
			State processMouseDown(NewPathController controller, DrawingView view, Point p, int modifiers) {
				return S7;
			}
		},
		S4 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p1);
				if (controller._segments.size() > 0) {
					float tol = view.convertToReferenceLength(VERTEX_SIZE * 2);
					if (controller._p1.distance(controller._p0) < tol) {
						view.setCursor(Cursor.CURSOR_OPEN_POLYLINE);
					} else if (controller._p1.distance(controller._segments.get(0).getStartPoint()) < tol) {
						view.setCursor(Cursor.CURSOR_CLOSED_POLYLINE);
					} else {
						view.setCursor(Cursor.CURSOR_CROSSHAIR);
					}
				}
				return this;
			}		
			@Override
			State processMouseDown(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p2);
				controller._p2.copyTo(controller._p3);
				return S5;
			}
		},
		S5 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p, controller._p2);
				if (controller._p2.distance(controller._p3) > T) {
					return S6;
				}
				return S5;
			}		
			@Override
			State processMouseUp(NewPathController controller, DrawingView view, Point p, int modifiers) {
				float tol = view.convertToReferenceLength(VERTEX_SIZE * 2);
				boolean pathHasSegments = controller._segments.size() > 0;
				boolean done = false;
				if (pathHasSegments && controller._p1.distance(controller._p0) < tol) {
					// Open path
					done = true;
				} else {
					if (controller._p1.distance(controller._p0) > tol) {
						if (pathHasSegments && controller._p1.distance(controller._segments.get(0).getStartPoint()) < tol) {
							// Closed path
							controller._segments.add(new LineSegment(new Point[]{controller._p0, controller._segments.get(0).getStartPoint()}));
							done = true;
						} else {
							// Point is viable
							controller._segments.add(new LineSegment(new Point[]{controller._p0, controller._p1}));
						}
					}
				}
				if (done) {
					return S8.processDeactivate(controller, view);
				}
				controller._p1.copyTo(controller._p0);
				controller._p2.copyTo(controller._p1);
				return S4;
			}
		},
		S6 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p).symetricPoint(controller._p3, controller._p2);
				if (p.distance(controller._p3) > T) {
					return S6;
				}
				return S5;
			}		
			@Override
			State processMouseUp(NewPathController controller, DrawingView view, Point p, int modifiers) {
				return S7.processMouseUp(controller, view, p, modifiers);
			}
		},
		S7 {
			@Override
			State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
				view.toModelCoordinates(p).symetricPoint(controller._p3, controller._p2);
				return S7;
			}		
			@Override
			State processMouseUp(NewPathController controller, DrawingView view, Point p, int modifiers) {
				float tol = view.convertToReferenceLength(VERTEX_SIZE * 2);
				boolean pathHasSegments = controller._segments.size() > 0;
				boolean done = false;
				if (pathHasSegments && controller._p2.distance(controller._p0) < tol) {
					// Open path
					done = true;
				} else {
					if (controller._p2.distance(controller._p0) > tol) {
						if (pathHasSegments && (controller._p2.distance(controller._segments.get(0).getStartPoint()) < tol || controller._p3.distance(controller._segments.get(0).getStartPoint()) < tol)) {
							// Closed path
							controller._segments.add(new BezierSegment(new Point[]{controller._p0, controller._p1, controller._p2, controller._segments.get(0).getStartPoint()}));
							done = true;
						} else {
							// Point is viable
							controller._segments.add(new BezierSegment(new Point[]{controller._p0, controller._p1, controller._p2, controller._p3}));
						}
					}
				}
				if (done) {
					return S8.processDeactivate(controller, view);
				}
				controller._p3.copyTo(controller._p0);
				controller._p2.symetricPoint(controller._p3, controller._p1);
				return controller._p2.equals(controller._p3) ? S4 : S3;
			}
		},
		S8 {
			State processDeactivate(NewPathController controller, DrawingView view) {
				if (controller._segments.size() > 0) {
					Path path = new Path(controller._segments);
					path.setAttribute(Attribute.LINE_STYLE, controller._app.getLineStyleController().getStyle());
					path.setAttribute(Attribute.LINE_OPACITY, controller._app.getLineStyleController().getOpacity());
					path.setAttribute(Attribute.FILL_STYLE, controller._app.getFillStyleController().getStyle());
					path.setAttribute(Attribute.FILL_OPACITY, controller._app.getFillStyleController().getOpacity());
					path.setAttribute(Attribute.LINE_WIDTH, controller._app.getLineWidthController().getLineWidth());
					NewShapeCommand command = new NewShapeCommand(controller._app, path);
					command.execute();
					controller._app.getHistory().addCommand(command);
				}
				return S0.processActivate(controller, view);
			}
		};
		State processActivate(NewPathController controller, DrawingView view) {
			throw new IllegalStateException("activate");
		}
		State processMouseDown(NewPathController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processMouseUp(NewPathController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processMouseMove(NewPathController controller, DrawingView view, Point p, int modifiers) {
			return this;
		}
		State processKeyDown(NewPathController controller, int key, int modifiers) {
			return this;
		}
		State processDeactivate(NewPathController controller, DrawingView view) {
			throw new IllegalStateException("deactivate");
		}
	};
	private Point _p0, _p1, _p2, _p3, _pTmp;
	private State _state;
	private List<Segment> _segments;
	private MouseControllerButton _button;

	public NewPathController(RepApplication app) {
		super(app);
		_p0 = new Point();
		_p1 = new Point();
		_p2 = new Point();
		_p3 = new Point();
		_pTmp = new Point();
		_segments = new ArrayList<Segment>();
		_button = new MouseControllerButton(_app.getIcons().pathIcon().createImage(), _app.getConstants().newPathCommand(), this);
	}
	
	@Override
	public void mouseDown(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseDown(this, view, p, modifiers);
	}
	@Override
	public void mouseUp(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseUp(this, view, p, modifiers);
	}
	@Override
	public void mouseMove(DrawingView view, Point p, int modifiers) {
		_state = _state.processMouseMove(this, view, p, modifiers);
	}
	@Override
	public void keyDown(DrawingView view, char keyCode, int modifiers) {
		_state = _state.processKeyDown(this, keyCode, modifiers);
	}
	@Override
	public void activate(DrawingView view) {
		_state = State.S0;
		_state = _state.processActivate(this, view);
	}
	@Override
	public String toString() {
		return _state.toString();
	}
	
	@Override
	public void render(DrawingView view) {
		//RepApplication.app.debugArea.setText(_state.toString());
		_app.getLineStyleController().getStyle().acceptVisitor(view.getStrokeStyleVisitor());
		for (int i = 0, size = _segments.size(); i < size; i++) {
			Segment segment = _segments.get(i);
			if (i == 0) {
				view.beginPath();
				view.moveTo(segment.getStartPoint().x, segment.getStartPoint().y);
			}
			if (segment instanceof LineSegment) {
				view.lineTo(segment.getEndPoint().x, segment.getEndPoint().y);
			} else {
				BezierSegment bezierSegment = (BezierSegment)segment;
				view.bezierCurveTo(
						bezierSegment.getStartControlPoint().x, bezierSegment.getStartControlPoint().y, 
						bezierSegment.getEndControlPoint().x, bezierSegment.getEndControlPoint().y, 
						bezierSegment.getEndPoint().x, bezierSegment.getEndPoint().y);
			}
		}
		view.stroke();
		Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
		Color.BLACK.acceptVisitor(view.getFillStyleVisitor());
		view.setLineWidth(1f);
		switch(_state) {
			case S2:
			case S4:
			case S5:
				view.moveTo(_p0.x, _p0.y);
				view.lineTo(_p1.x, _p1.y);
				view.stroke();
				Color.WHITE.acceptVisitor(view.getFillStyleVisitor());
				view.fillRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
				Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
				view.strokeRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
				view.beginPath();
				view.moveTo(_p1.x - VERTEX_SIZE, _p1.y);
				view.lineTo(_p1.x + VERTEX_SIZE, _p1.y);
				view.moveTo(_p1.x, _p1.y - VERTEX_SIZE);
				view.lineTo(_p1.x, _p1.y + VERTEX_SIZE);
				view.stroke();
				break;
			case S3:
				view.moveTo(_p0.x, _p0.y);
				view.lineTo(_p1.x, _p1.y);			
				view.moveTo(_p0.x, _p0.y);
				view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);
				view.stroke();
				view.beginPath();
				view.arc(_p1.x, _p1.y, CONTROL_POINT_SIZE, 0, (float)(2 * Math.PI), true);
				view.fill();
				Color.WHITE.acceptVisitor(view.getFillStyleVisitor());
				view.fillRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
				Color.BLACK.acceptVisitor(view.getStrokeStyleVisitor());
				view.strokeRect(_p0.x - VERTEX_SIZE, _p0.y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
				break;
			case S6:
			case S7:
				view.moveTo(_p0.x, _p0.y);
				view.bezierCurveTo(_p1.x, _p1.y, _p2.x, _p2.y, _p3.x, _p3.y);
				view.moveTo(_p2.x, _p2.y);
				_p2.symetricPoint(_p3, _pTmp);
				view.lineTo(_pTmp.x, _pTmp.y);
				view.stroke();
				view.beginPath();
				view.arc(_p2.x, _p2.y, CONTROL_POINT_SIZE, 0, (float)(2 * Math.PI), true);
				view.arc(_pTmp.x, _pTmp.y, CONTROL_POINT_SIZE, 0, (float)(2 * Math.PI), true);
				view.fill();
				break;
		}
	}
	
	@Override
	public void deactivate(DrawingView view) {
		_state = State.S8.processDeactivate(this, view);
	}

	public MouseControllerButton getButton() {
		return _button;
	}
}
