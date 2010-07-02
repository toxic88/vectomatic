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

import com.google.gwt.core.client.JavaScriptObject;

public class OMSVGAnimatedPoints extends JavaScriptObject {
  protected OMSVGAnimatedPoints() {
  }

  // Implementation of the svg::SVGAnimatedPoints W3C IDL interface
  public final native OMSVGPointList getPoints() /*-{
    return this.points ? @org.vectomatic.dom.svg.OMSVGPointList::new(Lcom/google/gwt/core/client/JavaScriptObject;)(this.points) : null;
  }-*/;
  public final native OMSVGPointList getAnimatedPoints() /*-{
    return this.animatedPoints ? @org.vectomatic.dom.svg.OMSVGPointList::new(Lcom/google/gwt/core/client/JavaScriptObject;)(this.animatedPoints) : null;
  }-*/;

}