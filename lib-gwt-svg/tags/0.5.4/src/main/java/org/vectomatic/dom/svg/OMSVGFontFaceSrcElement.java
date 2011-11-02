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

import org.vectomatic.dom.svg.impl.SVGFontFaceSrcElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.dom.client.TagName;

/**
 * <p>The {@link org.vectomatic.dom.svg.OMSVGFontFaceSrcElement} interface
 * corresponds to the <a href='http://www.w3.org/TR/SVG11/fonts.html#FontFaceSrcElement'
 * title='font-face-src element specification'>font-face-src</a> element.</p>
 * <p>Object-oriented access to the attributes of the <a href='http://www.w3.org/TR/SVG11/fonts.html#FontFaceSrcElement'
 * title='font-face-src element specification'>font-face-src</a> element via
 * the SVG DOM is not available.</p>
 */
@TagName("font-face-src")
public class OMSVGFontFaceSrcElement extends OMSVGElement {
  public OMSVGFontFaceSrcElement() {
    this((SVGFontFaceSrcElement)DOMHelper.createElementNS(DOMHelper.getCurrentDocument(), SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_FONT_FACE_SRC_TAG).cast());
  }

  protected OMSVGFontFaceSrcElement(SVGFontFaceSrcElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGFontFaceSrcElement W3C IDL interface

}
