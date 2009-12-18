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


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDrawingServiceAsync {
	void getDrawings(String jsessionid, AsyncCallback<Drawing[]> callback);
	
	void newDrawing(String jsessionid, String name, AsyncCallback<Drawing> callback);	
	
	void renameDrawing(String jsessionid, String drawingid, String newName, AsyncCallback<Void> callback);
	
	void deleteDrawings(String jsessionid, String[] drawingids, AsyncCallback<Void> callback) ;
	
	void duplicateDrawing(String jsessionid, String drawingid, String newName, AsyncCallback<Drawing> callback);
	
	void publishDrawing(String jsessionid, String drawingid, boolean published, AsyncCallback<Void> callback);
}
