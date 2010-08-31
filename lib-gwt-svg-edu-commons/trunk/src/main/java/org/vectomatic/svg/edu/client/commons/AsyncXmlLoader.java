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
 * 
 **********************************************/
package org.vectomatic.svg.edu.client.commons;

/**
 * Class to define an API to load XML resources asynchronously
 */
public interface AsyncXmlLoader {
	/**
	 * Initiates a request to load an XML resource
	 * @param resourceUrl
	 * The resource to load
	 * @param callback
	 * A callback invoked to process the resource once it is loaded
	 */
	public void loadResource(String resourceUrl, AsyncXmlLoaderCallback callback);
}
