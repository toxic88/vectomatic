/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg-edu.
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
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UI panel to ask for confirmation upon game restart
 */
public class ConfirmBox {
	interface RestartBinder extends UiBinder<VerticalPanel, ConfirmBox> {
	}
	private static RestartBinder restartBinder = GWT.create(RestartBinder.class);
	
	@UiField(provided=true)
	EduConstants constants = EduConstants.INSTANCE;
	@UiField
	Button confirmYesButton;
	@UiField
	Button confirmNoButton;
	DialogBox confirmBox;
	
	public static DialogBox createConfirmBox() {
		return new ConfirmBox().confirmBox;
	}
	
	private ConfirmBox() {
		confirmBox = new DialogBox();
		confirmBox.setTitle(constants.restart());
		confirmBox.setWidget(restartBinder.createAndBindUi(this));
	}
	
	@UiHandler("confirmYesButton")
	public void confirmYes(ClickEvent event) {
		confirmBox.hide();
		reload();
	}
	
	private static native void reload() /*-{
	  $wnd.location.href = $wnd.location.href;
    }-*/;

	@UiHandler("confirmNoButton")
	public void confirmNo(ClickEvent event) {
		confirmBox.hide();
	}

}
