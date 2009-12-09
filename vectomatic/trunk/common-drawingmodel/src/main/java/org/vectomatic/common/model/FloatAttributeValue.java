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
 * Class to represent attribute value of type float point number.
 */
public class FloatAttributeValue implements IAttributeValue, IsSerializable {
	private float _value;
	public FloatAttributeValue() {
	}
	public FloatAttributeValue(float value) {
		_value = value;
	}
	public float getValue() {
		return _value;
	}
	public void setValue(float value) {
		_value = value;
	}
	@Override
	public String toString() {
		return Float.toString(_value);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatAttributeValue) {
			return _value == ((FloatAttributeValue)obj)._value;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return (int)_value;
	}
}
