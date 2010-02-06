/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of libgwtsvg.
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

import org.vectomatic.dom.svg.impl.SVGTitleElement;
import org.vectomatic.dom.svg.itf.ISVGLangSpace;
import org.vectomatic.dom.svg.itf.ISVGStylable;

public class OMSVGTitleElement extends OMSVGElement implements ISVGLangSpace, ISVGStylable {
  protected OMSVGTitleElement(SVGTitleElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGTitleElement W3C IDL interface

  // Implementation of the svg::SVGLangSpace W3C IDL interface
  public final String getXmllang() {
    return ((SVGTitleElement)ot).getXmllang();
  }
  public final void setXmllang(java.lang.String value) {
    ((SVGTitleElement)ot).setXmllang(value);
  }
  public final String getXmlspace() {
    return ((SVGTitleElement)ot).getXmlspace();
  }
  public final void setXmlspace(java.lang.String value) {
    ((SVGTitleElement)ot).setXmlspace(value);
  }

}
