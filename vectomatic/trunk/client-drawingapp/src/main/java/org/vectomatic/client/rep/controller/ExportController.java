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
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.format.ISVGExporter;
import org.vectomatic.common.format.IOutputStream;
import org.vectomatic.common.format.SVG11Visitor;
import org.vectomatic.common.format.SVG12Visitor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.MenuItem;

public class ExportController extends ControllerBase {
	private ControllerMenuItem _exportSVG11MenuItem;
	private ControllerMenuItem _exportSVG12MenuItem;
	private FormPanel _form;
	private Hidden _hidden;

	public ExportController(RepApplication app, FormPanel form) {
		super(app);
		_form = form;
		form.setMethod(FormPanel.METHOD_POST);
		Element svgexport = DOM.getElementById("svgexport");
		String uri = "";
		if (svgexport != null) {
			uri = svgexport.getAttribute("content");
		}
		
		form.setAction(uri);
		_hidden = new Hidden();
		_hidden.setName("data");
		form.add(_hidden);
		_exportSVG11MenuItem = new ControllerMenuItem(app.getView(), app.getConstants().exportSVG11Command(), new ControllerBase(_app) {
			@Override
			public void activate(DrawingView view) {
				export(new SVG11Visitor());
			}			
		});
		_exportSVG12MenuItem = new ControllerMenuItem(app.getView(), app.getConstants().exportSVG12Command(), new ControllerBase(_app) {
			@Override
			public void activate(DrawingView view) {
				export(new SVG12Visitor());
			}			
		});
	}
	
	private void export(ISVGExporter exporter) {
		final StringBuffer buffer = new StringBuffer();
		exporter.writeSVG(new IOutputStream() {
			public void write(String str) {
				buffer.append(str);
			}
		}, _app.getModel().toShapeArray(), _app.getPalettes(), _app.getView().getWidth(), _app.getView().getHeight());
		_hidden.setValue(buffer.toString());
		_form.submit();
	}
	

	public MenuItem getExportSVG11Item() {
		return _exportSVG11MenuItem;
	}

	public MenuItem getExportSVG12Item() {
		return _exportSVG12MenuItem;
	}


}
