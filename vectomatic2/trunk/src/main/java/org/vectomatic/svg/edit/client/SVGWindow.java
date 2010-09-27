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
package org.vectomatic.svg.edit.client;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.event.RotationEvent;
import org.vectomatic.svg.edit.client.event.RotationHandler;
import org.vectomatic.svg.edit.client.gxt.AbsoluteLayerLayout;
import org.vectomatic.svg.edit.client.gxt.AbsoluteLayerLayoutData;
import org.vectomatic.svg.edit.client.widget.Compass;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;

/**
 * GXT window class dedicated to displaying and editing
 * a single SVG image. The window has several layers: the
 * bottom layer contains the SVG image itself and the top
 * layer contains widgets to manipulate it (rotation compass
 * and scale slider).
 * @author laaglu
 */
public class SVGWindow extends Window {
	/**
	 * The svg image to display
	 */
	protected OMSVGSVGElement svg;
	/**
	 * A group to apply a visualization transform to the svg
	 * It is the first and only child of the svg
	 */
	protected OMSVGGElement xformGroup;
	/**
	 * The current transform
	 */
	protected OMSVGTransform xform;
	/**
	 * The current scaling the the svg
	 */
	protected float angle;
	/**
	 * The current rotation of the svg
	 */
	protected float scale;
	/**
	 * The SVG rotation compass
	 */
	protected Compass compass;
	/**
	 * The SVG scale slider
	 */
	protected Slider scaleSlider;
	/**
	 * Specifies the SVG window to display
	 * @param svg
	 * The SVG image to display
	 */
	public void setSvg(final OMSVGSVGElement svg) {
		this.svg = svg;
		setPlain(true);
		setMaximizable(true);
		setSize(500, 300);
		setMinWidth(200);
		setMinHeight(170);
		
		// A CSS multi-layer container
		// The lower layer contains the image itself
		// The higher layer contains the control (compass and scale slider)
		LayoutContainer layersContainer = new LayoutContainer();
		GWT.log("borders: " + getBorders());
	    layersContainer.setLayout(new AbsoluteLayerLayout());

	    /////////////////////////////////////////////////
	    // Populate the lower layer
	    /////////////////////////////////////////////////
		LayoutContainer svgContainer = new LayoutContainer();
	    svgContainer.setScrollMode(Style.Scroll.AUTO);
	    svgContainer.setStyleAttribute("background-color", SVGConstants.CSS_WHITE_VALUE);
	    SVGProcessor.normalizeIds(svg);
	    SVGImage image = new SVGImage(svg) {
	    	protected void onAttach() {
	    		GWT.log("onAttach");
	    		OMSVGRect viewBox = svg.getViewBox().getBaseVal();
	    		if (viewBox.getWidth() == 0f || viewBox.getHeight() == 0f) {
		    		GWT.log(svg.getBBox().getDescription());
	    			OMSVGRect bbox = inset(svg.getBBox(), svg.createSVGRect(), -0.1f * svg.getBBox().getWidth(), -0.1f * svg.getBBox().getHeight());
	    			viewBox.setWidth(bbox.getWidth());
	    			viewBox.setHeight(bbox.getHeight());
	    			setScale(scale);
	    		}
	    	}
	    };
	    svgContainer.add(image);
	    layersContainer.add(svgContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_LEFT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		10));
	    
	    // Tweak the image to insert a group immediately below
	    // the root. This group will be used to control the viewing
	    // transform
	    xformGroup = reparent(image.getSvgElement());
		OMSVGTransformList xformList = xformGroup.getTransform().getBaseVal();
		xform = svg.createSVGTransform();
		xformList.appendItem(xform);
	    setScale(1f);

	    /////////////////////////////////////////////////
	    // Populate the higher layer
	    /////////////////////////////////////////////////
		
		// Create the compass
	    compass = GWT.create(Compass.class);
	    final OMSVGSVGElement compassSvg = compass.getSvgElement();
	    compassSvg.getStyle().setWidth(100, Unit.PCT);
	    compassSvg.getStyle().setHeight(100, Unit.PCT);
	    compass.addRotationHandler(new RotationHandler() {
	    	@Override
	    	public void onRotate(RotationEvent event) {
	    		setRotation(event.getAngle());
	    	}	
	    });
	    layersContainer.add(new SVGImage(compassSvg), new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		40,
	    		5,
	    		100,
	    		100,
	    		20));
	    
		// Create the scale slider
		scaleSlider = new Slider() {
			@Override
	    	protected String onFormatValue(int value) {
				return Integer.toString((int)(scale * 100)) + "%";
	    	}

		};
		scaleSlider.setHeight(100);
		scaleSlider.setMinValue(0);
		scaleSlider.setMaxValue(100);
		scaleSlider.setIncrement(1);
		scaleSlider.setValue(50);
		scaleSlider.setVertical(true);
		layersContainer.add(scaleSlider, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		20,
	    		5,
	    		20,
	    		100,
	    		20));
	    scaleSlider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				// Convert from slider unit to transform unit
				int value = be.getNewValue();
				if (value >= 50) {
					scale = 1f + (value - 50f) / 10f * 4 / 5;
				} else {
					scale = 1f / (1f + (49 - value) / 10f * 4 / 5);
				}
				setScale(scale);
			}	    	
	    });
	    
		setLayout(new FitLayout());
		add(layersContainer, new FitData(4));
	}
	
	private static OMSVGRect inset(OMSVGRect src, OMSVGRect dest, float x, float y) {
		dest.setX(src.getX() + x);
		dest.setY(src.getY() + y);
		dest.setWidth(src.getWidth() - x * 2);
		dest.setHeight(src.getHeight() - y * 2);
		return dest;
	}
	
	/**
	 * Sets the scaling of the main image through the scale slider.
	 * @param scale
	 * The scale (50 means scale 1:1)
	 */
	public void setScaleSlider(int value) {
		scaleSlider.setValue(value);
	}
	
	/**
	 * Sets the scaling of the main image.
	 * @param scale
	 * The scale (1 means scale 1:1, 2 means scale 2:1)
	 */
	protected void setScale(float scale) {
		this.scale = scale;
		OMSVGRect rect = svg.getViewBox().getBaseVal();
        svg.getStyle().setWidth(rect.getWidth() * scale, Unit.PX);
        svg.getStyle().setHeight(rect.getHeight() * scale, Unit.PX);
	}

	
	/**
	 * Sets the rotation of the main image through the
	 * compass widget.
	 * @param angleDeg
	 * The angle (in degrees)
	 */
	public void setRotationCompass(int angleDeg) {
		compass.setRotation(angleDeg);
	}
	
	/**
	 * Sets the rotation of the main image.
	 * @param angle
	 * The angle (in degrees)
	 */
	protected void setRotation(float angle) {
		this.angle = angle;
		OMSVGRect rect = svg.getViewBox().getBaseVal();
		OMSVGPoint center = svg.createSVGPoint(rect.getCenterX(), rect.getCenterY());
		OMSVGMatrix m1 = svg.createSVGMatrix().translate(center.getX(), center.getY());
    	OMSVGMatrix m2 = svg.createSVGMatrix().rotate(this.angle);
    	OMSVGMatrix m3 = svg.createSVGMatrix().translate(-center.getX(), -center.getY());
    	OMSVGMatrix m = m1.multiply(m2).multiply(m3);
		xform.setMatrix(m);
	}
	
	private OMSVGGElement reparent(OMSVGSVGElement svg) {
		OMSVGGElement g = OMSVGParser.currentDocument().createSVGGElement();
		Element gElement = g.getElement();
		Element svgElement = svg.getElement();
		Node node;
		while((node = svgElement.getFirstChild()) != null) {
			gElement.appendChild(svgElement.removeChild(node));
		}
		svgElement.appendChild(gElement);
		return g;
	}
	
	@Override
	protected void moveDrag(DragEvent de) {
		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
		if (de.getY() < windowBarHeight) {
			de.setY(windowBarHeight);
		}
	}
	/* GWT bug ?
	 * line 234: The method endDrag(DragEvent) in the type Window is not applicable for the arguments (DragEvent, boolean)*/
//	protected void endDrag(DragEvent de, boolean canceled) {
//		GWT.log("endDrag" + de.getX() + " " + de.getY());
//		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
//		if (de.getY() < windowBarHeight) {
//			de.setY(windowBarHeight);
//		}
//		super.endDrag(de, canceled);
//	}
}

