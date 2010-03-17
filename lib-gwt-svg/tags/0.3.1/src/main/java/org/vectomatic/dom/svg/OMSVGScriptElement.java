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
package org.vectomatic.dom.svg;

import org.vectomatic.dom.svg.impl.SVGScriptElement;
import org.vectomatic.dom.svg.itf.ISVGExternalResourcesRequired;
import org.vectomatic.dom.svg.itf.ISVGURIReference;

public class OMSVGScriptElement extends OMSVGElement implements ISVGURIReference, ISVGExternalResourcesRequired {
  protected OMSVGScriptElement(SVGScriptElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGScriptElement W3C IDL interface
  public final String getType() {
    return ((SVGScriptElement)ot).getType();
  }
  public final void setType(java.lang.String value) {
    ((SVGScriptElement)ot).setType(value);
  }

  // Implementation of the svg::SVGURIReference W3C IDL interface
  public final OMSVGAnimatedString getHref() {
    return ((SVGScriptElement)ot).getHref();
  }

  // Implementation of the svg::SVGExternalResourcesRequired W3C IDL interface
  public final OMSVGAnimatedBoolean getExternalResourcesRequired() {
    return ((SVGScriptElement)ot).getExternalResourcesRequired();
  }

}
