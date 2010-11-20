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

import com.google.gwt.resources.client.CssResource;

/**
 * CSS definition class
 * @author laaglu
 */
public interface AppCss extends CssResource {
	@ClassName("thumb")
	public String thumb();
	@ClassName("thumb-wrap")
	public String thumbWrap();
	@ClassName("compass-container")
	public String compassContainer();
	@ClassName("compass")
	public String compass();
	@ClassName("scale-slider-container")
	public String scaleSliderContainer();
	@ClassName("scale-slider")
	public String scaleSlider();
}
