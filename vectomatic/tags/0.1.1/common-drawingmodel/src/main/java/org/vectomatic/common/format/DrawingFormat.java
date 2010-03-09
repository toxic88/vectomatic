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
 * along with Vectomatic.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.common.format;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent graphic formats
 */
public class DrawingFormat {
	public static final DrawingFormat SVG11 = new DrawingFormat("image/svg+xml", "svg11", ".svg");
	public static final DrawingFormat SVG12 = new DrawingFormat("image/svg+xml", "svg12", ".svg");
	public static final DrawingFormat PNG = new DrawingFormat("image/png", "png", ".png");
	public static final DrawingFormat NATIVE = new DrawingFormat("text/xml", "native", ".xml");
	private static Map<String, DrawingFormat> _descriptionToFormat;
	static {
		_descriptionToFormat = new HashMap<String, DrawingFormat>();
		_descriptionToFormat.put(SVG11.getDescription(), SVG11);
		_descriptionToFormat.put(SVG12.getDescription(), SVG12);
		_descriptionToFormat.put(PNG.getDescription(), PNG);
		_descriptionToFormat.put(NATIVE.getDescription(), NATIVE);
	}
	private String _mimeType;
	private String _description;
	private String _extension;

	private DrawingFormat(String mimeType, String description, String extension) {
		_mimeType = mimeType;
		_description = description;
		_extension = extension;
	}
	public static DrawingFormat fromDescription(String description) {
		return description != null ? _descriptionToFormat.get(description) : null;
	}
	public String getMimeType() {
		return _mimeType;
	}
	public String getDescription() {
		return _description;
	}
	public String getExtension() {
		return _extension;
	}
}
