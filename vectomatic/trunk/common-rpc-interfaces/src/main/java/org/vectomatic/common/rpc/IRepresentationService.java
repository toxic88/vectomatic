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
package org.vectomatic.common.rpc;


import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Service interface to open and save drawing representations
 * @author Lukas Laag
 */
public interface IRepresentationService extends RemoteService {
	/**
	 * Reads the representation of a drawing
	 * @param jsessionid
	 * The session id
	 * @param drawingid
	 * The drawing id
	 * @return
	 * The drawing representation
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the drawing no longer exists
	 */
	Representation openDrawing(String jsessionid, String drawingid) throws DrawingServiceException, AccountServiceException;
	
	/**
	 * Saves the representation of a drawing
	 * @param jsessionid
	 * The session id
	 * @param drawingid
	 * The drawing id
	 * @param representation
	 * The drawing representation
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the drawing no longer exists
	 */
	void saveDrawing(String jsessionid, String drawingid, Representation representation) throws DrawingServiceException, AccountServiceException;

}
