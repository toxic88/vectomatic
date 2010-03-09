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
package org.vectomatic.client.rep.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.EditPolylineController;
import org.vectomatic.common.model.geometry.Point;
import org.vectomatic.common.model.geometry.Polyline;

/**
 * Command to delete a vertex from a polyline
 */
public class DeleteVertexCommand extends CommandBase {
	private EditPolylineController _editPolylineController;
	private Polyline _polyline;
	private List<Point> _vertices;
	private Set<Integer> _vertexIndices;
	
	public DeleteVertexCommand(RepApplication app, EditPolylineController editPolylineController, Polyline polyline, Set<Integer> vertexIndices) {
		super(app);
		_editPolylineController = editPolylineController;
		_polyline = polyline;
		_vertexIndices = new HashSet<Integer>(vertexIndices);
		_vertices = new ArrayList<Point>(_vertexIndices.size());
		Point[] vertices = _polyline.getVertices();
		Iterator<Integer> iterator = _vertexIndices.iterator();
		while (iterator.hasNext()) {
			int index = iterator.next().intValue();
			Point vertex = new Point(vertices[index]);
			_vertices.add(vertex);
		}
	}
	
	public String getDescription() {
		return _app.getConstants().deleteVertexCommand();
	}

	public void execute() {
		Point[] vertices = _polyline.getVertices();
		boolean isClosed = _polyline.isClosed();
		List<Point> newVertices = new ArrayList<Point>();
		for (int i = 0, size = vertices.length - (isClosed ? 1 : 0); i < size; i++) {
			if (!_vertexIndices.contains(new Integer(i))) {
				newVertices.add(vertices[i]);
			}
		}
		if (isClosed) {
			newVertices.add(newVertices.get(0));
		}
		_polyline.setVertices(newVertices.toArray(new Point[newVertices.size()]));
		_app.getSelector().selectController(_editPolylineController.getButton());
		_editPolylineController.selectPolyline(_polyline, new HashSet<Integer>());
	}

	public void unexecute() {
		Point[] vertices = _polyline.getVertices();
		boolean isClosed = _polyline.isClosed();
		Point[] newVertices = new Point[vertices.length + _vertexIndices.size()];
		int index = 0;
		Iterator<Integer> iterator = _vertexIndices.iterator();
		while (iterator.hasNext()) {
			newVertices[iterator.next().intValue()] = _vertices.get(index);
			index++;
		}
		index = 0;
		for (int i = 0, size = vertices.length - (isClosed ? 1 : 0); i < size; i++) {
			while (newVertices[index] != null) {				
				index++;
			}
			newVertices[index] = vertices[i];
		}
		if (isClosed) {
			newVertices[newVertices.length - 1] = newVertices[0];
		}
		_polyline.setVertices(newVertices);
		_app.getSelector().selectController(_editPolylineController.getButton());
		_editPolylineController.selectPolyline(_polyline, _vertexIndices);
	}
}
