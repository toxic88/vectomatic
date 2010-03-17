/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg-edu.
 * 
 * libgwtsvg-edu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg-edu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with libgwtsvg-edu.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edu.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * Game resources
 * @author laaglu
 */
public interface EduImages extends ClientBundle {
	public static final EduImages INSTANCE =  GWT.create(EduImages.class);
	
	@Source("tasto_5_architetto_franc_01.png")
	public ImageResource rightArrow();
	
	@Source("tasto_6_architetto_franc_01.png")
	public ImageResource leftArrow();

	@Source("logo.svg")
	public TextResource logo();

	@Source("pictures.txt")
	public TextResource pictureList();
}
