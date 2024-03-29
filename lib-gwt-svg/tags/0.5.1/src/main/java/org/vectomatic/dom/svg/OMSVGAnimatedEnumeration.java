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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Used for attributes whose value must be a constant from a particular enumeration
 * and which can be animated.
 */
public class OMSVGAnimatedEnumeration extends JavaScriptObject {
  protected OMSVGAnimatedEnumeration() {
  }

  // Implementation of the svg::SVGAnimatedEnumeration W3C IDL interface
  /**
   * The base value of the given attribute before applying any animations.
   */
  public final native short getBaseVal() /*-{
    return this.baseVal;
  }-*/;
  /**
   * The base value of the given attribute before applying any animations.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised on an   attempt
   * to change the value of a <a href="svgdom.html#ReadOnlyNodes">read only
   * attribute</a>.
   */
  public final native void setBaseVal(short value) throws JavaScriptException /*-{
    this.baseVal = value;
  }-*/;
  /**
   * If the given attribute or property is being animated, contains the current
   * animated value of the attribute or property. If the given attribute or
   * property is not currently being animated, contains the same value as {@link
   * org.vectomatic.dom.svg.OMSVGAnimatedEnumeration#getBaseVal()}.
   */
  public final native short getAnimVal() /*-{
    return this.animVal;
  }-*/;

}
