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

import org.vectomatic.svg.edu.client.dots.DotsMain;
import org.vectomatic.svg.edu.client.maze.MazeMain;
import org.vectomatic.svg.edu.client.push.PushMain;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Displays the game title
 * @author laaglu
 */
public class Intro implements EntryPoint {
	public static final String ID_UIROOT = "uiRoot";
	interface IntroBinder extends UiBinder<VerticalPanel, Intro> {
	}
	private static IntroBinder introBinder = GWT.create(IntroBinder.class);
	
	@UiField
	Hyperlink licenseLink;
	@UiField
	Button dotsButton;
	@UiField
	Button mazeButton;
	@UiField
	Button pushButton;
	private VerticalPanel panel;
	private LicenseBox licenseBox;

	@Override
	public void onModuleLoad() {
		// Initialize the UI with UiBinder
		panel = introBinder.createAndBindUi(this);
		licenseBox = new LicenseBox();
		RootPanel.get("uiRoot").add(panel);
	}

	@UiHandler("licenseLink")
	public void license(ClickEvent event) {
		licenseBox.box.center();
		licenseBox.box.show();
	}

	@UiHandler("dotsButton")
	public void startDots(ClickEvent event) {
		RootPanel.get(ID_UIROOT).remove(panel);
        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
              Window.alert(EduConstants.INSTANCE.loadError());
            }

            public void onSuccess() {
            	DotsMain main = new DotsMain();
        		main.onModuleLoad2();
            }
          });
	}

	@UiHandler("mazeButton")
	public void startMaze(ClickEvent event) {
		RootPanel.get(ID_UIROOT).remove(panel);
        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
              Window.alert(EduConstants.INSTANCE.loadError());
            }

            public void onSuccess() {
            	MazeMain main = new MazeMain();
        		main.onModuleLoad2();
            }
          });
	}

	@UiHandler("pushButton")
	public void startPush(ClickEvent event) {
		RootPanel.get(ID_UIROOT).remove(panel);
        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
              Window.alert(EduConstants.INSTANCE.loadError());
            }

            public void onSuccess() {
            	PushMain main = new PushMain();
        		main.onModuleLoad2();
            }
          });
	}
}
