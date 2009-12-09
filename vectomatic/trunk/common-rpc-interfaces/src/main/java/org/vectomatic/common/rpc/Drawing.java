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
package org.vectomatic.common.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Drawing implements IsSerializable {
	public static final String ID_ATTRIBUTE = "id";
	public static final String IDS_ATTRIBUTE = "ids";
	public static final String FORMAT_ATTRIBUTE = "format";
	private String _id;						// ID
	private String _name;					// NAME
	private boolean _published;				// PUBLISHED
	private Date _modificationDate;
	
	///////////////////////////////////////
	// Constructors
	///////////////////////////////////////
	
	public Drawing() {
	}
	
	public Drawing(String id, String name, boolean published, Date modificationDate) {
		_id = id;
		_name = name;
		_published = published;
		_modificationDate = modificationDate;
	}

	///////////////////////////////////////
	// Identity
	///////////////////////////////////////

	@Override
	public boolean equals(Object obj)  {
		if (obj instanceof Drawing) {
			// id is assigned so it can be used for identity
			return _id.equals(((Drawing)obj)._id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return _id.hashCode();
	}
	

	///////////////////////////////////////
	// Persistent Properties
	///////////////////////////////////////

	public void setId(String id) {
		_id = id;
	}
	public String getId() {
		return _id;
	}
	public void setName(String name) {
		_name = name;
	}
	public String getName() {
		return _name;
	}
	public void setPublished(boolean published) {
		_published = published;
	}
	public boolean isPublished() {
		return _published;
	}

	///////////////////////////////////////
	// Non Persistent Properties
	///////////////////////////////////////
	
	public void setModificationDate(Date modificationDate) {
		_modificationDate = modificationDate;
	}
	public Date getModificationDate() {
		return _modificationDate;
	}

	///////////////////////////////////////
	// Debugging
	///////////////////////////////////////

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Drawing {id = ");
		buffer.append(_id);
		buffer.append("; name = ");
		buffer.append(_name);
		buffer.append("; published = ");
		buffer.append(_published);
		buffer.append(" }");
		return buffer.toString();
	}

}
