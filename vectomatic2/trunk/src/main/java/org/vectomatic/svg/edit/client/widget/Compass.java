/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.widget;

import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.event.HasRotationHandlers;
import org.vectomatic.svg.edit.client.event.RotationEvent;
import org.vectomatic.svg.edit.client.event.RotationHandler;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An compass widget implemented using an SVG image.
 * @author laaglu
 */
public class Compass implements HasRotationHandlers {
	private OMSVGTransform rotation;
	private boolean rotate;
	private int angle;
	private OMSVGSVGElement compass;
	private HandlerManager handlerManager;
	
	/**
	 * Constructor.
	 * @param svg
	 * The root svg element.
	 * @param R
	 * The radius of the compass.
	 * @param p
	 * The center of the compass.
	 */
	public Compass() {
		compass = AppBundle.INSTANCE.compass().getSvg();
		OMSVGPathElement textPath = (OMSVGPathElement)DOMHelper.evaluateXPath(compass, ".//svg:path[@id='textPath']", new SVGPrefixResolver()).next();
		SVGProcessor.normalizeIds(compass);
		rotation = textPath.getTransform().getBaseVal().getItem(0);
		compass.addMouseDownHandler(new MouseDownHandler()  {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				rotate = true;
				DOMHelper.setCaptureElement(compass, null);
				event.preventDefault();
				event.stopPropagation();
			}
		});
		compass.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (rotate) {
					OMSVGPoint p = getLocalCoordinates(event).substract(compass.createSVGPoint(50,50));
//					GWT.log(p.getDescription() + " " + Float.toString(-p.getY() / p.length()));
					double a = Math.acos(-p.getY() / p.length());
					if (p.getX() < 0) {
						a = 2 * Math.PI - a;
					}
//					GWT.log(p.getX() + " " + p.getY());
//					GWT.log((p.getX() / p.length()) + " " + (p.getY() / p.length()) + " " + (a * 360 / (2 * Math.PI)));
					
					int angle2 = (((int)toDeg(a)) / 6) * 6;
					if (angle2 != angle) {
						angle = angle2;
						rotation.setRotate(angle - 6, 50, 50);
						fireEvent(new RotationEvent(angle)); 
					}
					event.preventDefault();
					event.stopPropagation();
				}
			}
		});
		compass.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (rotate) {
					rotate = false;
					DOMHelper.releaseCaptureElement();
					event.preventDefault();
					event.stopPropagation();
				}
			}
		});
	}
	
	public OMSVGSVGElement getSvgElement() {
		return compass;
	}

	private static final float toDeg(double rad) {
		return (float)(rad * 180 / Math.PI);
	}

    /**
     * Returns the coordinates of a mouse event, converted
     * to the compass coordinate system
     * @param e
     * A mouse event
     * @return
     * The coordinates of the mouse event, converted
     * to the compass coordinate system
     */
    public OMSVGPoint getLocalCoordinates(MouseEvent<? extends EventHandler> e) {
        OMSVGPoint p = compass.createSVGPoint(e.getClientX(), e.getClientY());
        OMSVGMatrix m = compass.getScreenCTM().inverse();
        return p.matrixTransform(m);
    }
    



	@Override
	public void fireEvent(GwtEvent<?> event) {
		if (handlerManager != null) {
			handlerManager.fireEvent(event);
		}
	}

	@Override
	public HandlerRegistration addRotationHandler(RotationHandler handler) {
		return ensureHandlers().addHandler(RotationEvent.getType(), handler);
	}
	
	private HandlerManager ensureHandlers() {
		if (handlerManager == null) {
			handlerManager = new HandlerManager(this);
		}
		return handlerManager;
	}

}
