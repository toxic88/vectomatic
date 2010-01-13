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
public class OMSVGFESpotLightElement extends org.vectomatic.dom.svg.OMSVGElement {
  protected OMSVGFESpotLightElement() {
  }

  // Implementation of the nsIDOMSVGFESpotLightElement XPCOM interface
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getX() /*-{
    return this.x;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getY() /*-{
    return this.y;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getZ() /*-{
    return this.z;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getPointsAtX() /*-{
    return this.pointsAtX;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getPointsAtY() /*-{
    return this.pointsAtY;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getPointsAtZ() /*-{
    return this.pointsAtZ;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getSpecularExponent() /*-{
    return this.specularExponent;
  }-*/;
  public final native org.vectomatic.dom.svg.OMSVGAnimatedNumber getLimitingConeAngle() /*-{
    return this.limitingConeAngle;
  }-*/;

}
