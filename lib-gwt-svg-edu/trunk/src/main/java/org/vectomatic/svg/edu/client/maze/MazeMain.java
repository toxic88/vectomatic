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
package org.vectomatic.svg.edu.client.maze;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edu.client.CommonBundle;
import org.vectomatic.svg.edu.client.ConfirmBox;
import org.vectomatic.svg.edu.client.Intro;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MazeMain {
	private static final String DIR = "maze";
	private static final String ID_MAZE = "maze";

	interface MazeMainBinder extends UiBinder<VerticalPanel, MazeMain> {
	}
	private static MazeMainBinder mainBinder = GWT.create(MazeMainBinder.class);
	
	@UiField(provided=true)
	MazeBundle resources = MazeBundle.INSTANCE;
	@UiField(provided=true)
	CommonBundle common = CommonBundle.INSTANCE;
	MazeCss style = resources.getCss();
	OMSVGDocument document;
	OMSVGSVGElement svgRoot;
	OMSVGPathElement mazeDef;
	OMSVGGElement cellGroup;
	OMSVGPathElement borderPath;
	OMSVGPathElement wallPath;
	
	@UiField
	FocusPanel focusPanel;
	@UiField
	SVGPushButton generateButton;
	@UiField
	SVGPushButton leftButton;
	@UiField
	SVGPushButton rightButton;
	@UiField
	SVGPushButton upButton;
	@UiField
	SVGPushButton downButton;
	@UiField
	SVGPushButton backButton;
	@UiField
	SVGPushButton helpButton;
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

	String[] levels;
	int currentLevel;
	Timer timer;
	DialogBox confirmBox;
		
	RectangularMaze maze;
	public void onModuleLoad2() {
		StyleInjector.inject(style.getText());
		
		// Load the game levels
		levels = resources.levels().getText().split("\\s");

		
		// Initialize the UI with UiBinder
		VerticalPanel panel = mainBinder.createAndBindUi(this);
		confirmBox = ConfirmBox.createConfirmBox();
		levelList.addItem(MazeConstants.INSTANCE.easy());
		levelList.addItem(MazeConstants.INSTANCE.medium());
		levelList.addItem(MazeConstants.INSTANCE.hard());
		levelList.setSelectedIndex(0);
		RootPanel.get(Intro.ID_UIROOT).add(panel);
		readMazeDef();
	}
	
	@UiHandler("generateButton")
	public void generate(ClickEvent event) {
		maze.perfectRandomize();
		update();
	}
	
	void clear() {
		if (timer != null) {
			timer.cancel();
		}
		OMSVGGElement g = document.createSVGGElement();
		if (cellGroup != null) {
			svgRoot.replaceChild(g, cellGroup);
		} else {
			svgRoot.appendChild(g);
		}
		cellGroup = g;

		String wallStroke = mazeDef.getAttributeNS(RectangularMaze.VECTOMATIC_NS, RectangularMaze.WALL_TAG);
		OMSVGPathElement p = document.createSVGPathElement();
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, wallStroke);
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1");
		if (wallPath != null) {
			svgRoot.replaceChild(p, wallPath);
		} else {
			svgRoot.appendChild(p);
		}
		wallPath = p;

		String borderStroke = mazeDef.getAttributeNS(RectangularMaze.VECTOMATIC_NS, RectangularMaze.BORDER_TAG);
		p = document.createSVGPathElement();
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, borderStroke);
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1");
		if (borderPath != null) {
			svgRoot.replaceChild(p, borderPath);
		} else {
			svgRoot.appendChild(p);
		}
		borderPath = p;

		String[] res = mazeDef.getAttributeNS(RectangularMaze.VECTOMATIC_NS, RectangularMaze.RES_TAG + (levelList.getSelectedIndex() + 1)).split("x");
		int colCount = Integer.parseInt(res[0]);
		int rowCount = Integer.parseInt(res[1]);
		maze = RectangularMaze.createMaze(colCount, rowCount, document, mazeDef, cellGroup, borderPath, wallPath);
		// A timer to make the current position blink
		timer = new Timer() {
			@Override
			public void run() {
				if (maze != null) {
					maze.updateCurrent();
				}
			}
		};
		timer.scheduleRepeating(250);
	}
	
	@UiHandler("leftButton")
	public void left(MouseDownEvent event) {
		maze.left();
		update();
	}
	@UiHandler("rightButton")
	public void right(MouseDownEvent event) {
		maze.right();
		update();
	}
	@UiHandler("upButton")
	public void up(MouseDownEvent event) {
		maze.up();
		update();
	}
	@UiHandler("downButton")
	public void down(MouseDownEvent event) {
		maze.down();
		update();
	}
	@UiHandler("backButton")
	public void backButton(MouseDownEvent event) {
		maze.back();
		update();
	}
	private void update() {
		leftButton.setEnabled(maze.canGoLeft());
		rightButton.setEnabled(maze.canGoRight());
		upButton.setEnabled(maze.canGoUp());
		downButton.setEnabled(maze.canGoDown());
		backButton.setEnabled(maze.canGoBack());
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				// Do this asynchronously while the button
				// is no longer the focus
				focusPanel.setFocus(true);
			}			
		});
	}
	@UiHandler("helpButton")
	public void help(ClickEvent event) {
		helpButton.setEnabled(false);
		generateButton.setEnabled(false);
		levelList.setEnabled(false);
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		backButton.setEnabled(false);
		maze.displaySolution(true);
		Timer solutionTimer = new Timer() {
			@Override
			public void run() {
				maze.displaySolution(false);
				helpButton.setEnabled(true);
				generateButton.setEnabled(true);
				levelList.setEnabled(true);
				update();
			}
		};
		solutionTimer.schedule(3000);
	}
	@UiHandler("prevButton")
	public void prevButton(ClickEvent event) {
		currentLevel--;
		if (currentLevel < 0) {
			currentLevel = levels.length - 1;
		}
		readMazeDef();
	}
	@UiHandler("nextButton")
	public void nextButton(ClickEvent event) {
		currentLevel++;
		if (currentLevel >= levels.length) {
			currentLevel = 0;
		}
		readMazeDef();
	}
	@UiHandler("homeButton")
	public void homeButton(ClickEvent event) {
        confirmBox.center();
        confirmBox.show();
	}
	
	@UiHandler("focusPanel")
	public void onKeyPress(KeyPressEvent event) {
		switch (event.getCharCode()) {
			case KeyCodes.KEY_DOWN:
				maze.down();
				break;
			case KeyCodes.KEY_RIGHT:
				maze.right();
				break;
			case KeyCodes.KEY_UP:
				maze.up();
				break;
			case KeyCodes.KEY_LEFT:
				maze.left();
				break;
			case ' ':
				maze.back();
				break;
			default:
				GWT.log("key code:" + event.getCharCode());
		}
		update();
	}
	
	@UiHandler("levelList")
	public void levelChange(ChangeEvent event) {
		clear();
		generate(null);
	}
	
	private String getLevelUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + levels[currentLevel];
	}

	
	public void readMazeDef() {
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
				OMSVGSVGElement svg = OMSVGParser.parse(response.getText());
				// Add the SVG to the HTML page
				Element div = svgContainer.getElement();
				if (svgRoot != null) {
					div.replaceChild(svg.getElement(), svgRoot.getElement());
				} else {
					div.appendChild(svg.getElement());					
				}
				svgRoot = svg;
				document = svgRoot.getOwnerDocument();
				mazeDef = (OMSVGPathElement) document.getElementById(ID_MAZE);
				cellGroup = null;
				wallPath = null;
				borderPath = null;
				clear();
				generate(null);
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

