/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of libgwtsvg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.dom.svg;
public class OMSVGSymbolElement extends org.vectomatic.dom.svg.OMSVGElement {
  protected OMSVGSymbolElement() {
  }

  // Implementation of the nsIDOMSVGSymbolElement XPCOM interface

  // Implementation of the nsIDOMSVGStylable XPCOM interface
  public final native org.vectomatic.dom.svg.OMSVGAnimatedString getClassName() /*-{
    return this.className;
  }-*/;
  public final native org.vectomatic.dom.css.OMStyleDeclaration getStyle() /*-{
    return this.style;
  }-*/;
  public final native org.vectomatic.dom.css.OMValue getPresentationAttribute(java.lang.String name) /*-{
    return this.getPresentationAttribute(name);
  }-*/;

  // Implementation of the nsIDOMSVGFitToViewBox XPCOM interface
  public final native org.vectomatic.dom.svg.OMSVGAnimatedRect getViewBox() /*-{
    return this.viewBox;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedPreserveAspectRatio getPreserveAspectRatio() /*-{
    return this.preserveAspectRatio;
  }-*/;

  // Implementation of the nsIDOMEventTarget XPCOM interface
  public final native void addEventListener(java.lang.String type, org.vectomatic.dom.events.OMEventListener listener, boolean useCapture) /*-{
    this.addEventListener(type, listener, useCapture);
  }-*/;
  public final native void removeEventListener(java.lang.String type, org.vectomatic.dom.events.OMEventListener listener, boolean useCapture) /*-{
    this.removeEventListener(type, listener, useCapture);
  }-*/;
  public final native boolean dispatchEvent(org.vectomatic.dom.events.OMEvent evt) /*-{
    return this.dispatchEvent(evt);
  }-*/;

}
