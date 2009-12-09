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

import com.google.gwt.user.client.rpc.IsSerializable;

public class DrawingServiceException extends Exception implements IsSerializable {
	private static final long serialVersionUID = 1L;
	public static final int INEXISTING_DRAWINGID = 1;
	public static final int QUOTA_EXCEEDED = 2;
	public static final int IMPORT_FORMAT = 3;

	private int _id;
	public DrawingServiceException() {
		super();
	}
	public DrawingServiceException(int id) {
		this();
		_id = id;
	}
	public int getId() {
		return _id;
	}
}
