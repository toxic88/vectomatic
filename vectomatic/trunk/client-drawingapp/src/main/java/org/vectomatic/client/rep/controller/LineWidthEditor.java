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
package org.vectomatic.client.rep.controller;

import org.vectomatic.client.UIConstants;
import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.view.Spinner;
import org.vectomatic.common.model.FloatAttributeValue;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Widget class to specify a custom line width
 */
public class LineWidthEditor extends DialogBox {
	private LineWidthController _controller;
	private Spinner _lineWidthSpinner;
	
	public LineWidthEditor(LineWidthController controller) {
		super(false, true);
		_controller = controller;
		
		UIConstants constants = RepApplication.app._constants;
		setText(constants.lineWidthEditor());

		_lineWidthSpinner = new Spinner(1, 10, 5);
		Button okButton = new Button(constants.okButton());
		okButton.setWidth("150px");
		okButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				hide();
				try {
					_controller.setLineWidth(new FloatAttributeValue(_lineWidthSpinner.getValue()));
				} catch(NumberFormatException e) {
				}
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
		layoutTable.setWidget(0, 0, _lineWidthSpinner);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(cancelButton);
		flowPanel.add(okButton);
		layoutTable.setWidget(1, 0, flowPanel);
		cellFormatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(layoutTable);
	}
	
	@Override
	public void show() {
		FloatAttributeValue lineWidth = _controller.getLineWidth();
		_lineWidthSpinner.setValue(lineWidth == null ? 5 : (int)lineWidth.getValue());
		super.show();
	}

}
