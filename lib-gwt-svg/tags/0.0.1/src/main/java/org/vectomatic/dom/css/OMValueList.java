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
package org.vectomatic.dom.css;
public class OMValueList extends org.vectomatic.dom.css.OMValue {
  protected OMValueList() {
  }

  // Implementation of the nsIDOMCSSValueList XPCOM interface
  public final native int getLength() /*-{
    return this.length;
  }-*/;
  public final native org.vectomatic.dom.css.OMValue item(int index) /*-{
    return this.item(index);
  }-*/;

}
