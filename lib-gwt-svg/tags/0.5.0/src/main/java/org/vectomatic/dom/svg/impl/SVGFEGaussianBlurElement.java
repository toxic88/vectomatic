/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.vectomatic.dom.svg.impl;

import org.vectomatic.dom.svg.OMSVGAnimatedLength;
import org.vectomatic.dom.svg.OMSVGAnimatedNumber;
import org.vectomatic.dom.svg.OMSVGAnimatedString;

import com.google.gwt.core.client.JavaScriptException;

/**
 * The {@link org.vectomatic.dom.svg.impl.SVGFEGaussianBlurElement} interface
 * corresponds to the <a href='http://www.w3.org/TR/SVG11/filters.html#feGaussianBlurElement'
 * title='feGaussianBlur element specification'>feGaussianBlur</a> element.
 */
public class SVGFEGaussianBlurElement extends SVGElement {
  protected SVGFEGaussianBlurElement() {
  }

  // Implementation of the svg::SVGFEGaussianBlurElement W3C IDL interface
  /**
   * Corresponds to attribute <code>in</code> on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feGaussianBlurElement'
   * title='feGaussianBlur element specification'>feGaussianBlur</a> element.
   */
  public final native OMSVGAnimatedString getIn1() /*-{
    return this.in1;
  }-*/;
  /**
   * Corresponds to attribute <code>stdDeviation</code> on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feGaussianBlurElement'
   * title='feGaussianBlur element specification'>feGaussianBlur</a> element.
   * Contains the X component of attribute <code>stdDeviation</code>.
   */
  public final native OMSVGAnimatedNumber getStdDeviationX() /*-{
    return this.stdDeviationX;
  }-*/;
  /**
   * Corresponds to attribute <code>stdDeviation</code> on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feGaussianBlurElement'
   * title='feGaussianBlur element specification'>feGaussianBlur</a> element.
   * Contains the Y component (possibly computed automatically) of attribute
   * <code>stdDeviation</code>.
   */
  public final native OMSVGAnimatedNumber getStdDeviationY() /*-{
    return this.stdDeviationY;
  }-*/;
  /**
   * Sets the values for attribute <code>stdDeviation</code>.
   * @param stdDeviationX The X component of attribute <code>stdDeviation</code>.
   * @param stdDeviationY The Y component of attribute <code>stdDeviation</code>.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised on an attempt
   * to change the value of a <a href="svgdom.html#ReadOnlyNodes">read only
   * attribute</a>.
   */
  public final native void setStdDeviation(float stdDeviationX, float stdDeviationY) throws JavaScriptException /*-{
    this.setStdDeviation(stdDeviationX, stdDeviationY);
  }-*/;

  // Implementation of the svg::SVGFilterPrimitiveStandardAttributes W3C IDL interface
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getX()}
   * on the given element.
   */
  public final native OMSVGAnimatedLength getX() /*-{
    return this.x;
  }-*/;
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getY()}
   * on the given element.
   */
  public final native OMSVGAnimatedLength getY() /*-{
    return this.y;
  }-*/;
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getWidth()}
   * on the given element.
   */
  public final native OMSVGAnimatedLength getWidth() /*-{
    return this.width;
  }-*/;
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getHeight()}
   * on the given element.
   */
  public final native OMSVGAnimatedLength getHeight() /*-{
    return this.height;
  }-*/;
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getResult()}
   * on the given element.
   */
  public final native OMSVGAnimatedString getResult() /*-{
    return this.result;
  }-*/;

}
