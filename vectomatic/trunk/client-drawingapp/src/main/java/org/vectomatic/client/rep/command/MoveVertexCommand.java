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
 * Command to move a vertex in a polyline
 */
public class MoveVertexCommand extends CommandBase {
	private EditPolylineController _editPolylineController;
	private Polyline _polyline;
	private List<Point> _vertices;
	private Set<Integer> _vertexIndices;
	
	public MoveVertexCommand(RepApplication app, EditPolylineController editPolylineController, Polyline polyline, Polyline clone, Set<Integer> vertexIndices) {
		super(app);
		_editPolylineController = editPolylineController;
		_polyline = polyline;
		_vertexIndices = new HashSet<Integer>(vertexIndices);
		_vertices = new ArrayList<Point>(_vertexIndices.size());
		Point[] cloneVertices = clone.getVertices();
		Iterator<Integer> iterator = _vertexIndices.iterator();
		while (iterator.hasNext()) {
			int index = iterator.next().intValue();
			Point vertex = new Point(cloneVertices[index]);
			_vertices.add(vertex);
		}
	}
	
	public String getDescription() {
		return _app.getConstants().moveVertexCommand();
	}

	public void execute() {
		_changeVertices();
	}

	public void unexecute() {
		_changeVertices();
	}
	
	private void _changeVertices() {
		Point[] vertices = _polyline.getVertices();
		int index = 0;
		boolean closed = _polyline.isClosed();
		Iterator<Integer> iterator = _vertexIndices.iterator();
		while (iterator.hasNext()) {
			int pos = iterator.next().intValue();
			vertices[pos].swap(_vertices.get(index));
			if (pos == 0 && closed) {
				vertices[0].copyTo(vertices[vertices.length - 1]);
			}
			index++;
		}
		_polyline.setVertices(vertices);
		_app.getSelector().selectController(_editPolylineController.getButton());
		_editPolylineController.selectPolyline(_polyline, _vertexIndices);
	}
}
