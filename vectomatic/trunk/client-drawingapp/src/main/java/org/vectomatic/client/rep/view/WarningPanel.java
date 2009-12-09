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
package org.vectomatic.client.rep.view;

import org.vectomatic.client.UIConstants;
import org.vectomatic.client.rep.RepApplication;

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
 * Class to implement a warning panel
 */
public class WarningPanel extends DialogBox {
	private Button _warningButton;
	private Label _warningLabel;
	private ClickListener _listener;
	public WarningPanel() {
		super(false, true);
		UIConstants constants = RepApplication.app._constants;
		setText(constants.warning());
		_warningLabel = new Label();
		_warningLabel.setWidth("400px");
		_warningButton = new Button();
		_warningButton.setWidth("150px");
		Button cancelButton = new Button(constants.cancelButton());
		cancelButton.setWidth("150px");
		cancelButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				hide();
			}				
		});
		FlexTable layoutTable = new FlexTable();
		FlexCellFormatter cellFormatter = layoutTable.getFlexCellFormatter();
		layoutTable.setWidget(0, 0, _warningLabel);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(cancelButton);
		flowPanel.add(_warningButton);
		layoutTable.setWidget(1, 0, flowPanel);
		cellFormatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(layoutTable);
	}
	
	public void show(String labelText, String buttonText, ClickListener listener) {
		_warningLabel.setText(labelText);
		_warningButton.setText(buttonText);
		if (_listener != listener) {
			_warningButton.removeClickListener(_listener);
			_listener = listener;
			_warningButton.addClickListener(listener);
		}
		show();
	}
}
