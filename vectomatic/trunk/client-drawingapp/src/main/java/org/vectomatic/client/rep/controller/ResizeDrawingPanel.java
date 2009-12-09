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

import org.vectomatic.client.UIConstants;
import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.view.Spinner;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Widget class to enter drawing dimensions
 */
public class ResizeDrawingPanel extends DialogBox {
	private Spinner _widthSpinner;
	private Spinner _heightSpinner;
	
	public ResizeDrawingPanel(final ResizeController resizeController) {
		super(false, true);
		UIConstants constants = RepApplication.app._constants;
		setText(constants.dimensionsPanel());
		Label widthLabel = new Label(constants.widthLabel());
		_widthSpinner = new Spinner(1, 1024, 100);
		Label heightLabel = new Label(constants.heightLabel());
		_heightSpinner = new Spinner(1, 1024, 100);
		FlexTable contentTable = new FlexTable();
		contentTable.setWidget(0, 0, widthLabel);
		contentTable.setWidget(0, 1, _widthSpinner);
		contentTable.setWidget(1, 0, heightLabel);
		contentTable.setWidget(1, 1, _heightSpinner);
		
		Button okButton = new Button(constants.okButton());
		okButton.setWidth("150px");
		okButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				resizeController.resize(_widthSpinner.getValue(), _heightSpinner.getValue());
				hide();
			}				
		});
		Button cancelButton = new Button(constants.cancelButton());
		cancelButton.setWidth("150px");
		cancelButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				hide();
			}				
		});
		FlexTable layoutTable = new FlexTable();
		FlexCellFormatter cellFormatter = layoutTable.getFlexCellFormatter();
		layoutTable.setWidget(0, 0, contentTable);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(cancelButton);
		flowPanel.add(okButton);
		layoutTable.setWidget(1, 0, flowPanel);
		cellFormatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(layoutTable);
	}

	public void show(int width, int height) {
		_widthSpinner.setValue(width);
		_heightSpinner.setValue(height);
		show();
	}
}
