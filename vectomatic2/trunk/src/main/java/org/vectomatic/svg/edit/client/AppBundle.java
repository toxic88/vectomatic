/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Application resource bundle class
 * @author laaglu
 */
public interface AppBundle extends ClientBundle {
	AppBundle INSTANCE = GWT.create(AppBundle.class);
	@Source("app.css")
	AppCss css();
	@Source("altum_angelfish_01.svg")
	SVGResource fish();
	@Source("french_fries_juliane_kr_r.svg")
	SVGResource fries();
	@Source("compass.svg")
	SVGResource compass();
}
