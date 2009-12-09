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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * About panel for the application
 */
public class AboutPanel extends DialogBox {
	public AboutPanel() {
		super(false, true);
		UIConstants constants = RepApplication.app._constants;
		setText(constants.aboutPanel());
		FlexTable contentTable = new FlexTable();
		contentTable.setWidget(0, 0, new Label(constants.aboutLabel1()));
		contentTable.setWidget(1, 0, new Label(constants.aboutLabel2()));
		contentTable.setWidget(2, 0, new Label(constants.aboutLabel3()));
		contentTable.setWidget(3, 0, new Label(constants.aboutLabel4()));
		contentTable.setWidget(4, 0, new Label(constants.aboutLabel5()));
		
		Button okButton = new Button(constants.okButton());
		okButton.setWidth("150px");
		okButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				hide();
			}				
		});
		contentTable.setWidget(5, 0, okButton);
		setWidget(contentTable);
	}
}
