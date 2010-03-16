/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of libgwtsvg-edu.
 * 
 * libgwtsvg-edu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg-edu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with libgwtsvg-edu.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edu.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box to display the license
 * @author laaglu
 */
public class LicenseBox {
	interface LicenseBoxBinder extends UiBinder<VerticalPanel, LicenseBox> {
	}
	private static LicenseBoxBinder licenseBoxBinder = GWT.create(LicenseBoxBinder.class);

	@UiField
	HTML about;
	@UiField
	Button okButton;
	
	DialogBox box;

	LicenseBox() {
		box = new DialogBox();
		box.setAnimationEnabled(true);
		box.setGlassEnabled(true);
		box.setTitle(EduConstants.INSTANCE.license());
		box.setWidget(licenseBoxBinder.createAndBindUi(this));
		about.setHTML(EduConstants.INSTANCE.connectDotsAbout());
	}

	@UiHandler("okButton")
	public void license(ClickEvent event) {
		box.hide();
	}
}
