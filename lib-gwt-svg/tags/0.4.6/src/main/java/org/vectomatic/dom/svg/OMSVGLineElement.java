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

import org.vectomatic.dom.svg.events.ActivateEvent;
import org.vectomatic.dom.svg.events.ActivateHandler;
import org.vectomatic.dom.svg.events.FocusInEvent;
import org.vectomatic.dom.svg.events.FocusInHandler;
import org.vectomatic.dom.svg.events.FocusOutEvent;
import org.vectomatic.dom.svg.events.FocusOutHandler;
import org.vectomatic.dom.svg.events.HasGraphicalHandlers;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGLineElement;
import org.vectomatic.dom.svg.itf.ISVGExternalResourcesRequired;
import org.vectomatic.dom.svg.itf.ISVGLangSpace;
import org.vectomatic.dom.svg.itf.ISVGStylable;
import org.vectomatic.dom.svg.itf.ISVGTests;
import org.vectomatic.dom.svg.itf.ISVGTransformable;

import com.google.gwt.dom.client.TagName;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

@TagName("line")
public class OMSVGLineElement extends OMSVGElement implements HasGraphicalHandlers, ISVGTests, ISVGLangSpace, ISVGExternalResourcesRequired, ISVGStylable, ISVGTransformable {
  protected OMSVGLineElement(SVGLineElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGLineElement W3C IDL interface
  public final OMSVGAnimatedLength getX1() {
    return ((SVGLineElement)ot).getX1();
  }
  public final OMSVGAnimatedLength getY1() {
    return ((SVGLineElement)ot).getY1();
  }
  public final OMSVGAnimatedLength getX2() {
    return ((SVGLineElement)ot).getX2();
  }
  public final OMSVGAnimatedLength getY2() {
    return ((SVGLineElement)ot).getY2();
  }

  // Implementation of the svg::SVGTransformable W3C IDL interface
  public final OMSVGAnimatedTransformList getTransform() {
    return ((SVGLineElement)ot).getTransform();
  }

  // Implementation of the svg::SVGLocatable W3C IDL interface
  public final OMSVGElement getNearestViewportElement() {
    return (OMSVGElement)convert(((SVGLineElement)ot).getNearestViewportElement());
  }
  public final OMSVGElement getFarthestViewportElement() {
    return (OMSVGElement)convert(((SVGLineElement)ot).getFarthestViewportElement());
  }
  public final OMSVGRect getBBox() {
    return ((SVGLineElement)ot).getBBox();
  }
  public final OMSVGMatrix getCTM() {
    return ((SVGLineElement)ot).getCTM();
  }
  public final OMSVGMatrix getScreenCTM() {
    return ((SVGLineElement)ot).getScreenCTM();
  }
  public final OMSVGMatrix getTransformToElement(OMSVGElement element) {
    return ((SVGLineElement)ot).getTransformToElement(((SVGElement)element.ot));
  }

  // Implementation of the svg::SVGLangSpace W3C IDL interface
  public final String getXmllang() {
    return ((SVGLineElement)ot).getXmllang();
  }
  public final void setXmllang(java.lang.String value) {
    ((SVGLineElement)ot).setXmllang(value);
  }
  public final String getXmlspace() {
    return ((SVGLineElement)ot).getXmlspace();
  }
  public final void setXmlspace(java.lang.String value) {
    ((SVGLineElement)ot).setXmlspace(value);
  }

  // Implementation of the svg::SVGTests W3C IDL interface
  public final OMSVGStringList getRequiredFeatures() {
    return ((SVGLineElement)ot).getRequiredFeatures();
  }
  public final OMSVGStringList getRequiredExtensions() {
    return ((SVGLineElement)ot).getRequiredExtensions();
  }
  public final OMSVGStringList getSystemLanguage() {
    return ((SVGLineElement)ot).getSystemLanguage();
  }
  public final boolean hasExtension(String extension) {
    return ((SVGLineElement)ot).hasExtension(extension);
  }

  // Implementation of the svg::SVGExternalResourcesRequired W3C IDL interface
  public final OMSVGAnimatedBoolean getExternalResourcesRequired() {
    return ((SVGLineElement)ot).getExternalResourcesRequired();
  }

  @Override
  public final HandlerRegistration addLoadHandler(LoadHandler handler) {
    return addDomHandler(handler, LoadEvent.getType());
  }
  @Override
  public final HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return addDomHandler(handler, MouseDownEvent.getType());
  }
  @Override
  public final HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return addDomHandler(handler, MouseUpEvent.getType());
  }
  @Override
  public final HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
    return addDomHandler(handler, MouseOverEvent.getType());
  }
  @Override
  public final HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
    return addDomHandler(handler, MouseOutEvent.getType());
  }
  @Override
  public final HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return addDomHandler(handler, MouseMoveEvent.getType());
  }
  @Override
  public final HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }
  @Override
  public HandlerRegistration addFocusInHandler(FocusInHandler handler) {
    return addDomHandler(handler, FocusInEvent.getType());
  }
  @Override
  public HandlerRegistration addFocusOutHandler(FocusOutHandler handler) {
    return addDomHandler(handler, FocusOutEvent.getType());
  }
  @Override
  public HandlerRegistration addActivateHandler(ActivateHandler handler) {
    return addDomHandler(handler, ActivateEvent.getType());
  }
}
