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

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.OMText;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Displays the game title
 * @author laaglu
 */
public class Intro implements EntryPoint {
	interface IntroBinder extends UiBinder<VerticalPanel, Intro> {
	}
	private static IntroBinder introBinder = GWT.create(IntroBinder.class);
	
	private static final String ID_CONNECT_TITLE = "connectTitle";
	
	@UiField
	HTML svgContainer;
	@UiField
	Button licenseButton;
	@UiField
	Button startButton;
	private VerticalPanel panel;
	private LicenseBox licenseBox;
	private Main main;

	@Override
	public void onModuleLoad() {
		// Initialize the UI with UiBinder
		panel = introBinder.createAndBindUi(this);
		licenseBox = new LicenseBox();
		main = new Main();
		
		// Parse the game logo
		OMSVGSVGElement logo = OMSVGParser.parse(EduImages.INSTANCE.logo().getText());

		// Add the logo to the UI
		Element div = svgContainer.getElement();
		div.appendChild(logo.getElement());					
		RootPanel.get("uiRoot").add(panel);

		// Set the NLS title on the SVG curved path
		OMSVGDocument doc = logo.getOwnerDocument();
		OMSVGTSpanElement tspan = (OMSVGTSpanElement) doc.getElementById(ID_CONNECT_TITLE);
		OMText text = (OMText) tspan.getFirstChild();
		text.setData(EduConstants.INSTANCE.connectDotsTitle());
	}

	@UiHandler("licenseButton")
	public void license(ClickEvent event) {
		licenseBox.box.center();
		licenseBox.box.show();
	}

	@UiHandler("startButton")
	public void start(ClickEvent event) {
		RootPanel.get("uiRoot").remove(panel);
		main.onModuleLoad2();
	}


}
