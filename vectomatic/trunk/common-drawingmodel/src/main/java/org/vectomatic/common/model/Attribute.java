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
package org.vectomatic.common.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a graphical attribute such as line width
 * or line color
 */
public class Attribute implements IsSerializable {
	public static final Attribute LINE_STYLE = new Attribute("stroke.style", null);
	public static final Attribute LINE_OPACITY = new Attribute("stroke.opacity", new FloatAttributeValue(1.0f));
	public static final Attribute LINE_PATTERN = new Attribute("stroke.pattern", null);
	public static final Attribute LINE_CAP = new Attribute("stroke.cap", null);
	public static final Attribute LINE_WIDTH = new Attribute("stroke.width", new FloatAttributeValue(1.0f));
	public static final Attribute FILL_STYLE = new Attribute("fill.style", null);
	public static final Attribute FILL_OPACITY = new Attribute("fill.opacity", new FloatAttributeValue(1.0f));
	private String _id;
	private transient String _name;
	private IAttributeValue _defaultValue;
	
	public Attribute() {
		// For GWT serialization.
	}
	private Attribute(String id, IAttributeValue defaultValue) {
		_id = id;
		_defaultValue = defaultValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			return _id.equals(((Attribute)obj)._id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return _id.hashCode();
	}
	
	/**
	 * Returns the attribute immutable identifier
	 * @return
	 * the attribute immutable identifier
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * Returns the attribute display name
	 * @return
	 * the attribute display name
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Sets the attribute display name
	 * @param name
	 * the attribute display name
	 */
	public void setName(String name) {
		_name = name;
	}
	
	public IAttributeValue getDefaultValue() {
		return _defaultValue;
	}
	
	@Override
	public String toString() {
		return _id;
	}
}
