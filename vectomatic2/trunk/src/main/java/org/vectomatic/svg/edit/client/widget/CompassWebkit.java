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
package org.vectomatic.svg.edit.client.widget;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.event.RotationEvent;


/**
 * Workaround for bug http://code.google.com/p/chromium/issues/detail?id=55010
 * @author laaglu
 */
public class CompassWebkit extends Compass {
	public void setRotation(int angle) {
		this.angle = angle;
		textGroup.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, SVGConstants.SVG_ROTATE_ATTRIBUTE + "(" + angle + ",50,50)");
		fireEvent(new RotationEvent(angle)); 
	}
}
