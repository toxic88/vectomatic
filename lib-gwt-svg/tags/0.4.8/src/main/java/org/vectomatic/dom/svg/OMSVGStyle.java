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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TagName;

@TagName("style")
public class OMSVGStyle extends Style {
	protected OMSVGStyle() {
	}
	public final native void clearSVGProperty(String name) /*-{
		setPropertyImpl(name, "");
	}-*/;

	public final String getSVGProperty(String name) {
		return getPropertyImpl(name);
	}

	public final void setSVGProperty(String name, String value) {
		setPropertyImpl(name, value);
	}

	private native String getPropertyImpl(String name) /*-{
	    return this.getPropertyValue(name);
	}-*/;

	private native void setPropertyImpl(String name, String value) /*-{
	    this.setProperty(name, value, '');
	}-*/;
}
