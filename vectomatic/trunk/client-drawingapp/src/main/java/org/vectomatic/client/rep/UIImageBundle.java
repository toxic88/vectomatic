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
package org.vectomatic.client.rep;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * GWT image bundle for the application
 */
public interface UIImageBundle extends ImageBundle {

	@Resource("select.png")
	public AbstractImagePrototype selectIcon();

	@Resource("save.png")
	public AbstractImagePrototype saveIcon();

	@Resource("saveDisabled.png")
	public AbstractImagePrototype saveDisabledIcon();

	@Resource("rectangle.png")
	public AbstractImagePrototype rectIcon();

	@Resource("ellipse.png")
	public AbstractImagePrototype ellipseIcon();

	@Resource("polyline.png")
	public AbstractImagePrototype polylineIcon();

	@Resource("editpolyline.png")
	public AbstractImagePrototype editPolylineIcon();

	@Resource("path.png")
	public AbstractImagePrototype pathIcon();

	@Resource("editpath.png")
	public AbstractImagePrototype editPathIcon();

	@Resource("undo.png")
	public AbstractImagePrototype undoIcon();

	@Resource("undoDisabled.png")
	public AbstractImagePrototype undoDisabledIcon();

	@Resource("redo.png")
	public AbstractImagePrototype redoIcon();

	@Resource("redoDisabled.png")
	public AbstractImagePrototype redoDisabledIcon();

	@Resource("chslider.png")
	public AbstractImagePrototype chslider();

	@Resource("chsliderSliding.png")
	public AbstractImagePrototype chsliderSliding();

	@Resource("cvslider.png")
	AbstractImagePrototype cvslider();

	@Resource("cvsliderSliding.png")
	AbstractImagePrototype cvsliderSliding();

	@Resource("up.png")
	public AbstractImagePrototype upIcon();

	@Resource("down.png")
	public AbstractImagePrototype downIcon();

	@Resource("associateTangents.png")
	public AbstractImagePrototype associateTangents();

	@Resource("associateTangentsDisabled.png")
	public AbstractImagePrototype associateTangentsDisabled();

	@Resource("breakPath.png")
	public AbstractImagePrototype breakPath();

	@Resource("breakPathDisabled.png")
	public AbstractImagePrototype breakPathDisabled();

	@Resource("deleteVertex.png")
	public AbstractImagePrototype deleteVertex();

	@Resource("deleteVertexDisabled.png")
	public AbstractImagePrototype deleteVertexDisabled();

	@Resource("dissociateTangents.png")
	public AbstractImagePrototype dissociateTangents();

	@Resource("dissociateTangentsDisabled.png")
	public AbstractImagePrototype dissociateTangentsDisabled();

	@Resource("insertVertex.png")
	public AbstractImagePrototype insertVertex();

	@Resource("insertVertexDisabled.png")
	public AbstractImagePrototype insertVertexDisabled();

	@Resource("jointVertices.png")
	public AbstractImagePrototype jointVertices();

	@Resource("jointVerticesDisabled.png")
	public AbstractImagePrototype jointVerticesDisabled();

	@Resource("joinVerticesWithSegment.png")
	public AbstractImagePrototype joinVerticesWithSegment();

	@Resource("joinVerticesWithSegmentDisabled.png")
	public AbstractImagePrototype joinVerticesWithSegmentDisabled();

	@Resource("makeCurve.png")
	public AbstractImagePrototype makeCurve();

	@Resource("makeCurveDisabled.png")
	public AbstractImagePrototype makeCurveDisabled();

	@Resource("makeLine.png")
	public AbstractImagePrototype makeLine();

	@Resource("makeLineDisabled.png")
	public AbstractImagePrototype makeLineDisabled();

	@Resource("removeSegment.png")
	public AbstractImagePrototype removeSegment();

	@Resource("removeSegmentDisabled.png")
	public AbstractImagePrototype removeSegmentDisabled();

	@Resource("symmetricTangents.png")
	public AbstractImagePrototype symmetricTangents();

	@Resource("symmetricTangentsDisabled.png")
	public AbstractImagePrototype symmetricTangentsDisabled();

	@Resource("exit.png")
	public AbstractImagePrototype exit();
}
