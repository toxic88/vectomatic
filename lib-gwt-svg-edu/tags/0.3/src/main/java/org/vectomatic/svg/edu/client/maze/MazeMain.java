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
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyleElement;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edu.client.CommonBundle;
import org.vectomatic.svg.edu.client.Intro;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main class of the maze game
 */
public class MazeMain {
	private static final String DIR = "maze";
	private static final String ID_MAZE = "maze";

	interface MazeMainBinder extends UiBinder<VerticalPanel, MazeMain> {
	}
	private static MazeMainBinder mainBinder = GWT.create(MazeMainBinder.class);
	
	@UiField(provided=true)
	static MazeBundle resources = MazeBundle.INSTANCE;
	@UiField(provided=true)
	static CommonBundle common = CommonBundle.INSTANCE;
	static MazeCss style = resources.getCss();
	
	OMSVGDocument document;
	/**
	 * Root element of the maze SVG document
	 */
	OMSVGSVGElement svgRoot;
	/**
	 * The path which delimits the region where the maze will be generated.
	 * It also contains attributes describing the maze characteristics
	 */
	OMSVGPathElement mazeDef;
	/**
	 * A group of maze cells
	 */
	OMSVGGElement cellGroup;
	/**
	 * A path to represent the external boundaries of the maze
	 */
	OMSVGPathElement borderPath;
	/**
	 * A path to represent walls between maze cells
	 */
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
	/**
	 * Game SVG level definitions
	 */
	String[] levels;
	/**
	 * Current index in the levels arrays
	 */
	int level;
	/**
	 * A timer to make the current position in the maze blink 
	 */
	static Timer positionTimer;
	/**
	 * A timer to flash the maze solution when the user requests it
	 */
	static Timer solutionTimer;
	/**
	 * A dialog box to ask for confirmation before leaving the game
	 */
	DialogBox confirmBox;
	/**
	 * The CSS rule which governs the color of the user path 
	 * in the maze (use for the color animation when the player wins)
	 */
	static JavaScriptObject pathRule;
	/**
	 * Use to free the UI at specific times (when displaying help,
	 * when loading a new level, ...)
	 */
	boolean frozen;
	/**
	 * The maze model
	 */
	RectangularMaze maze;
	
	static {
		StyleInjector.inject(style.getText(), true);
		pathRule = getRule("." + style.path());
	}
	
	public void onModuleLoad2(DialogBox confirmBox) {
		if (solutionTimer != null) {
			solutionTimer.cancel();
		}

		// Load the game levels
		levels = resources.levels().getText().split("\\s");
		
		// Initialize the UI with UiBinder
		VerticalPanel panel = mainBinder.createAndBindUi(this);
		this.confirmBox = confirmBox;
		levelList.addItem(MazeConstants.INSTANCE.easy());
		levelList.addItem(MazeConstants.INSTANCE.medium());
		levelList.addItem(MazeConstants.INSTANCE.hard());
	
		int difficulty = 0;
		String difficultyParam = Window.Location.getParameter("difficulty");
		if (difficultyParam != null) {
			if (MazeConstants.INSTANCE.easy().equals(difficultyParam)) {
				difficulty = 0;
			} else if (MazeConstants.INSTANCE.medium().equals(difficultyParam)) {
				difficulty = 1;
			} else if (MazeConstants.INSTANCE.hard().equals(difficultyParam)) {
				difficulty = 2;				
			}
		}
		levelList.setSelectedIndex(difficulty);
		RootPanel.get(Intro.ID_UIROOT).add(panel);
		
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
		
		readMazeDef();
	}
	
	@UiHandler("generateButton")
	public void generate(ClickEvent event) {
		maze.perfectRandomize();
		if (solutionTimer != null) {
			solutionTimer.cancel();
		}
		String dumpParam = Window.Location.getParameter("dump");
		if ((!GWT.isScript()) && (dumpParam != null)) {
			OMSVGSVGElement root2 = (OMSVGSVGElement)svgRoot.cloneNode(true);
			OMSVGRect viewBox = root2.getViewBox().getBaseVal();
			if (viewBox.getWidth() <= viewBox.getHeight()) {
				root2.setWidth(OMSVGLength.SVG_LENGTHTYPE_CM, 21f);
				root2.setHeight(OMSVGLength.SVG_LENGTHTYPE_CM, 29.7f);
			} else {
				root2.setWidth(OMSVGLength.SVG_LENGTHTYPE_CM, 29.7f);
				root2.setHeight(OMSVGLength.SVG_LENGTHTYPE_CM, 21f);
			}
			OMSVGStyleElement styleElement = document.createSVGStyleElement();
			styleElement.setType(SVGConstants.CSS_TYPE);
			styleElement.appendChild(document.createTextNode(style.getText()));
			root2.insertBefore(styleElement, root2.getFirstChild());
			
			GWT.log(root2.getMarkup());
		}
		setFillProperty(pathRule, SVGConstants.CSS_LIGHTGREEN_VALUE);
		update();
	}
	
	void clear() {
		if (positionTimer != null) {
			positionTimer.cancel();
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
		p.setClassNameBaseVal(style.wall());
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, wallStroke);
		if (wallPath != null) {
			svgRoot.replaceChild(p, wallPath);
		} else {
			svgRoot.appendChild(p);
		}
		wallPath = p;

		String borderStroke = mazeDef.getAttributeNS(RectangularMaze.VECTOMATIC_NS, RectangularMaze.BORDER_TAG);
		p = document.createSVGPathElement();
		p.setClassNameBaseVal(style.border());
		p.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, borderStroke);
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
		positionTimer = new Timer() {
			@Override
			public void run() {
				if (maze != null) {
					maze.updateCurrent();
				}
			}
		};
		positionTimer.scheduleRepeating(250);
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
		frozen = false;
		levelList.setEnabled(true);
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
		if (maze.gameWon()) {
			freeze();
			solutionTimer = new Timer() {
				private int H = 120, S = 40, V = 93;
				@Override
				public void run() {
					H += 7;
					H = H % 360;
					int R = 0, G = 0, B = 0;
                    int h = (H / 60);
                    int p = (255 * V * (100 - S)) / 10000;
                    int q = (255 * V * (6000 - S * (H - 60 * h))) / 600000;
                    int t = (255 * V * (6000 - S * (60 - (H - 60 * h)))) / 600000;
                    switch(h) {
                            case 0:
                                    R = V * 255 / 100;
                                    G  = t;
                                    B  = p;
                                    break;
                            case 1:
                                    R = q;
                                    G  = V * 255 / 100;
                                    B  = p;
                                    break;
                            case 2:
                                    R = p;
                                    G  = V * 255 / 100;
                                    B  = t;
                                    break;
                            case 3:
                                    R = p;
                                    G  = q;
                                    B  = V * 255 / 100;
                                    break;
                            case 4:
                                    R = t;
                                    G  = p;
                                    B  = V * 255 / 100;
                                    break;
                            case 5:
                                    R = V * 255 / 100;
                                    G = p;
                                    B  = q;
                                    break;
                    }
                    setFillProperty(pathRule, "rgb(" + R + "," + G + "," + B +")");
				}

			};
			solutionTimer.scheduleRepeating(50);
		}
	}
	private static final native JavaScriptObject getRule(String selector) /*-{
	  for (var i = 0; i < $doc.styleSheets.length; i++) {
	    var stylesheet = $doc.styleSheets[i];
	    for (var j = 0; j < stylesheet.cssRules.length; j++) {
	      var rule = stylesheet.cssRules[j];
	      if (rule.selectorText == selector) {
	        return rule;
	      }
	    }
	  }
	  return null;
	}-*/;
	
	private static final native void setFillProperty(JavaScriptObject rule, String color) /*-{
	  if (rule != null) {
	    rule.style.setProperty('fill', color, '');
	  }
	}-*/;
	
	private void freeze() {
		helpButton.setEnabled(false);
		levelList.setEnabled(false);
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		backButton.setEnabled(false);
		frozen = true;
	}

	@UiHandler("helpButton")
	public void help(ClickEvent event) {
		freeze();
		generateButton.setEnabled(false);
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
		level--;
		if (level < 0) {
			level = levels.length - 1;
		}
		readMazeDef();
	}
	@UiHandler("nextButton")
	public void nextButton(ClickEvent event) {
		level++;
		if (level >= levels.length) {
			level = 0;
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
		if (!frozen) {
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
	}
	
	@UiHandler("levelList")
	public void levelChange(ChangeEvent event) {
		clear();
		generate(null);
	}
	
	private String getLevelUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + levels[level];
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

