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
 * Service interface to manage drawings
 * @author Lukas Laag
 */
public interface IDrawingService extends RemoteService {
	/**
	 * Returns all the drawings owned by the session
	 * @param jsessionid
	 * The session id
	 * @return
	 * All the drawings owned by the session
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 */
	Drawing[] getDrawings(String jsessionid) throws AccountServiceException;
	
	/**
	 * Creates a new drawing
	 * @param jsessionid
	 * The session id
	 * @param name
	 * The drawing name (usually: "Untitled")
	 * @return
	 * A new drawing
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the server quota is exceeded
	 */
	Drawing newDrawing(String jsessionid, String name) throws AccountServiceException, DrawingServiceException;
	
	/**
	 * Renames a drawing
	 * @param jsessionid
	 * The session id
	 * @param drawingid
	 * The drawing id
	 * @param newName
	 * The drawing new name
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the drawing no longer exists
	 */
	void renameDrawing(String jsessionid, String drawingid, String newName) throws AccountServiceException, DrawingServiceException;
	
	/**
	 * Deletes drawings
	 * @param jsessionid
	 * The session id
	 * @param drawingids
	 * An array of drawing ids. Invalid drawing ids are ignored
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 */
	void deleteDrawings(String jsessionid, String[] drawingids) throws AccountServiceException;
	
	/**
	 * Duplicates a drawing
	 * @param jsessionid
	 * The session id
	 * @param drawingid
	 * The drawing id
	 * @param newName
	 * The duplicate name (usually: "Copy of")
	 * @return
	 * The drawing duplicate.
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the drawing no longer exists or the server quota has been exceeded
	 */
	Drawing duplicateDrawing(String jsessionid, String drawingid, String newName) throws AccountServiceException, DrawingServiceException;
	
	/**
	 * Publishes / unpublishes a drawing
	 * @param jsessionid
	 * The session id
	 * @param drawingid
	 * The drawing id
	 * @param boolean
	 * true to publish the drawing, false to unpublish it
	 * @throws AccountServiceException
	 * If a session related error occurs
	 * @throws InternalServerException
	 * If the server code fails
	 * @throws DrawingServiceException
	 * If the drawing no longer exists
	 */
	void publishDrawing(String jsessionid, String drawingid, boolean published) throws AccountServiceException, DrawingServiceException;
}
