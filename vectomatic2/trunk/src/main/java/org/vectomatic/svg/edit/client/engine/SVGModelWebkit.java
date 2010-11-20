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
package org.vectomatic.svg.edit.client.engine;

import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Subclass of SVGModel to bypass Chrome/Webkit bug 
 * <a href="http://code.google.com/p/chromium/issues/detail?id=55010">#55010</a>.
 * @author laaglu
 */
public class SVGModelWebkit extends SVGModel {
	@Override
	public void setRotation(float angle) {
		super.setRotation(angle);
		String value = displayXform.getDescription();
//    	VectomaticApp2.log("desc = " + value);
		displayGroup.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, value);
		selectionGroup.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, value);
	}
}
