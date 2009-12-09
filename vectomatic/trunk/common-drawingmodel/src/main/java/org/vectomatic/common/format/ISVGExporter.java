/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of Vectomatic.
 * 
 * Vectomatic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vectomatic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Vectomatic.  If not, see <http://www.gnu.org/licenses/>
 **********************************************/
package org.vectomatic.common.format;

import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.style.PaletteList;

/**
 * Interface for SVG exporters
 */
public interface ISVGExporter {
	public static final String NS = "http://www.w3.org/2000/svg";
	
	public static final String ELT_SVG = "svg";
	public static final String ELT_RECT = "rect";
	public static final String ELT_ELLIPSE = "ellipse";
	public static final String ELT_POLYLINE = "polyline";
	public static final String ELT_POLYGON = "polygon";
	public static final String ELT_G = "g";
	public static final String ELT_DEFS = "defs";
	public static final String ELT_DESC = "desc";
	public static final String ELT_TITLE = "title";
	public static final String ELT_SOLIDCOLOR = "solidColor";
	public static final String ELT_PATH = "path";
	
	public static final String ATT_VERSION = "version";
	public static final String ATT_X = "x";
	public static final String ATT_Y = "y";
	public static final String ATT_CX = "cx";
	public static final String ATT_CY = "cy";
	public static final String ATT_RX = "rx";
	public static final String ATT_RY = "ry";
	public static final String ATT_WIDTH = "width";
	public static final String ATT_HEIGHT = "height";
	public static final String ATT_POINTS = "points";
	public static final String ATT_TRANSFORM = "transform";
	public static final String ATT_STROKE = "stroke";
	public static final String ATT_STROKEOPACITY = "stroke-opacity";
	public static final String ATT_STROKEWIDTH = "stroke-width";
	public static final String ATT_FILL = "fill";
	public static final String ATT_FILLOPACITY = "fill-opacity";
	public static final String ATT_BASEPROFILE = "baseProfile";
	public static final String ATT_VECTOREFFECT = "vector-effect";
	public static final String ATT_SOLIDCOLOR = "solid-color";
	public static final String ATT_SOLIDOPACITY = "solid-opacity";
	public static final String ATT_ID = "xml:id";
	public static final String ATT_D = "d";
	
	public static final String VAL_NON_SCALING_STROKE="non-scaling-stroke";
	public static final String VAL_MOVE_TO="M";
	public static final String VAL_LINE_TO="L";
	public static final String VAL_CURVE_TO="C";
	public static final String VAL_NONE="none";
	
	public void writeSVG(IOutputStream stream, Shape[] shapes, PaletteList paletteList, int width, int height);
}
