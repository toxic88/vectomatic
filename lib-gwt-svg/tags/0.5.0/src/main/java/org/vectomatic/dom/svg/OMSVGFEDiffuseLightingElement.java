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

package org.vectomatic.dom.svg;

import org.vectomatic.dom.svg.impl.SVGFEDiffuseLightingElement;
import org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes;

import com.google.gwt.dom.client.TagName;

/**
 * The {@link org.vectomatic.dom.svg.OMSVGFEDiffuseLightingElement} interface
 * corresponds to the <a href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
 * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
 */
@TagName("feDiffuseLighting")
public class OMSVGFEDiffuseLightingElement extends OMSVGElement implements ISVGFilterPrimitiveStandardAttributes {
  protected OMSVGFEDiffuseLightingElement(SVGFEDiffuseLightingElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGFEDiffuseLightingElement W3C IDL interface
  /**
   * Corresponds to attribute <code>in</code> on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
   * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
   */
  public final OMSVGAnimatedString getIn1() {
    return ((SVGFEDiffuseLightingElement)ot).getIn1();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.OMSVGFEDiffuseLightingElement#getSurfaceScale()}
   * on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
   * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
   */
  public final OMSVGAnimatedNumber getSurfaceScale() {
    return ((SVGFEDiffuseLightingElement)ot).getSurfaceScale();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.OMSVGFEDiffuseLightingElement#getDiffuseConstant()}
   * on the given <a href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
   * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
   */
  public final OMSVGAnimatedNumber getDiffuseConstant() {
    return ((SVGFEDiffuseLightingElement)ot).getDiffuseConstant();
  }
  /**
   * Corresponds to attribute <code>kernelUnitLength</code> on the given <a
   * href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
   * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
   */
  public final OMSVGAnimatedNumber getKernelUnitLengthX() {
    return ((SVGFEDiffuseLightingElement)ot).getKernelUnitLengthX();
  }
  /**
   * Corresponds to attribute <code>kernelUnitLength</code> on the given <a
   * href='http://www.w3.org/TR/SVG11/filters.html#feDiffuseLightingElement'
   * title='feDiffuseLighting element specification'>feDiffuseLighting</a> element.
   */
  public final OMSVGAnimatedNumber getKernelUnitLengthY() {
    return ((SVGFEDiffuseLightingElement)ot).getKernelUnitLengthY();
  }

  // Implementation of the svg::SVGFilterPrimitiveStandardAttributes W3C IDL interface
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getX()}
   * on the given element.
   */
  public final OMSVGAnimatedLength getX() {
    return ((SVGFEDiffuseLightingElement)ot).getX();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getY()}
   * on the given element.
   */
  public final OMSVGAnimatedLength getY() {
    return ((SVGFEDiffuseLightingElement)ot).getY();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getWidth()}
   * on the given element.
   */
  public final OMSVGAnimatedLength getWidth() {
    return ((SVGFEDiffuseLightingElement)ot).getWidth();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getHeight()}
   * on the given element.
   */
  public final OMSVGAnimatedLength getHeight() {
    return ((SVGFEDiffuseLightingElement)ot).getHeight();
  }
  /**
   * Corresponds to attribute {@link org.vectomatic.dom.svg.itf.ISVGFilterPrimitiveStandardAttributes#getResult()}
   * on the given element.
   */
  public final OMSVGAnimatedString getResult() {
    return ((SVGFEDiffuseLightingElement)ot).getResult();
  }

}
