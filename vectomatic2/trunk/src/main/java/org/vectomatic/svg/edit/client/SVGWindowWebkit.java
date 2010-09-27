package org.vectomatic.svg.edit.client;

import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.core.client.GWT;

public class SVGWindowWebkit extends SVGWindow {
	public SVGWindowWebkit() {
		super();
		GWT.log("SVGWindowWebkit");
	}
	@Override
	public void setRotation(float angle) {
		this.angle = angle;
		OMSVGRect rect = svg.getViewBox().getBaseVal();
		OMSVGPoint center = svg.createSVGPoint(rect.getCenterX(), rect.getCenterY());
		OMSVGMatrix m1 = svg.createSVGMatrix().translate(center.getX(), center.getY());
    	OMSVGMatrix m2 = svg.createSVGMatrix().rotate(this.angle);
    	OMSVGMatrix m3 = svg.createSVGMatrix().translate(-center.getX(), -center.getY());
    	OMSVGMatrix m = m1.multiply(m2).multiply(m3);
    	String value = SVGConstants.SVG_MATRIX_VALUE + "(" + m.getA() + "," + m.getB() + "," + m.getC() + "," + m.getD() + "," + m.getE() + "," + m.getF() + ")";
    	GWT.log("xform=" + value);
    	xformGroup.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, value);
	}
}
