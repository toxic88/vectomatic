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
package org.vectomatic.svg.edu.client.puzzle;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.svg.edu.client.CommonBundle;
import org.vectomatic.svg.edu.client.ConfirmBox;
import org.vectomatic.svg.edu.client.Intro;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main class for the puzzle game
 */
public class PuzzleMain {
	private static final String DIR = "puzzle";
	interface PuzzleMainBinder extends UiBinder<VerticalPanel, PuzzleMain> {
	}
	private static PuzzleMainBinder mainBinder = GWT.create(PuzzleMainBinder.class);

	
	@UiField(provided=true)
	PuzzleBundle resources = PuzzleBundle.INSTANCE;
	@UiField(provided=true)
	CommonBundle common = CommonBundle.INSTANCE;
	PuzzleCss style = resources.getCss();
	@UiField
	SVGPushButton homeButton;
	@UiField
	SVGPushButton prevButton;
	@UiField
	SVGPushButton nextButton;
	@UiField
	HTML svgContainer;
	@UiField
	ListBox levelList;
	private String[] levels;
	private int level;
	private DialogBox confirmBox;
	/**
	 * The source image svg element
	 */
	private OMSVGSVGElement srcSvg;
	OMSVGSVGElement puzzleSvg;
	private Puzzle puzzle;
	int[][] dimensions = {{3, 3}, {4, 4}, {5, 5}, {7, 5}, {8, 6}};

	public void onModuleLoad2() {
		StyleInjector.inject(style.getText(), true);
		
		// Load the game levels
		levels = resources.levels().getText().split("\\s");
		
		// Initialize the UI with UiBinder
		VerticalPanel panel = mainBinder.createAndBindUi(this);
		confirmBox = ConfirmBox.createConfirmBox();
		for (int i = 1; i <= dimensions.length; i++) {
			levelList.addItem(PuzzleConstants.INSTANCE.level() + " " + i);
		}
		levelList.setSelectedIndex(0);
		String levelParam = Window.Location.getParameter("level");
		if (levelParam != null) {
			try {
				int value = Integer.parseInt(levelParam);
				if (value >= 0 && value < levels.length) {
					level = value;
				}
			} catch(NumberFormatException e) {
				GWT.log("Cannot parse level=" + levelParam, e);
			}
		}

		RootPanel.get(Intro.ID_UIROOT).add(panel);
		readPuzzleDef();
	}

	@UiHandler("prevButton")
	public void prevButton(ClickEvent event) {
		level--;
		if (level < 0) {
			level = levels.length - 1;
		}
		readPuzzleDef();
	}
	@UiHandler("nextButton")
	public void nextButton(ClickEvent event) {
		level++;
		if (level >= levels.length) {
			level = 0;
		}
		readPuzzleDef();
	}
	@UiHandler("homeButton")
	public void homeButton(ClickEvent event) {
        confirmBox.center();
        confirmBox.show();
	}

	@UiHandler("levelList")
	public void levelChange(ChangeEvent event) {
		generate();
	}
	
	private void generate() {
		int[] dimension = dimensions[levelList.getSelectedIndex()];
		puzzle = new Puzzle(srcSvg, dimension[0], dimension[1]);
		puzzle.shuffle();
		OMSVGSVGElement rootSvg = puzzle.getSvgElement();
		rootSvg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PERCENTAGE, 90f);
		rootSvg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PERCENTAGE, 90f);
//		rootSvg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PX, 600f);
//		rootSvg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PX, 800f);
		
		// Add the SVG to the HTML page
		Element div = svgContainer.getElement();
		if (puzzleSvg != null) {
			div.replaceChild(rootSvg.getElement(), puzzleSvg.getElement());
		} else {
			div.appendChild(rootSvg.getElement());					
		}
		puzzleSvg = rootSvg;
	}
	
	private String getLevelUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + levels[level];
	}

	public void readPuzzleDef() {
		String url = getLevelUrl();
		url += (url.indexOf("?") == -1) ? ("?ts=" + System.currentTimeMillis()) : ("&ts=" + + System.currentTimeMillis());
		RequestBuilder pictureBuilder = new RequestBuilder(RequestBuilder.GET, url);
		pictureBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				svgContainer.setHTML("Cannot find resource");
			}

			/**
			 * Parse the SVG artwork and launch the maze generation process
			 */
			private void onSuccess(Request request, Response response) {
				// Parse the document
				srcSvg = OMSVGParser.parse(response.getText());
				generate();
			}
			
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					onSuccess(request, response);
				} else {
					onError(request, null);
				}
			}
		});
		// Send the picture request
		try {
			pictureBuilder.send();
		} catch (RequestException e) {
			GWT.log("Cannot fetch " + url, e);
		}
	}
}
