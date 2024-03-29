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
package org.vectomatic.svg.edu.client.push;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGClipPathElement;
import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.OMSVGUseElement;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edu.client.CommonBundle;
import org.vectomatic.svg.edu.client.Intro;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PushMain implements MouseDownHandler {
	private static final String DIR = "push";
	private static final String ID_CLIP_PATH = "cp";
	private static final String ID_CLIP_RECT = "cpr";
	private static final String ID_IMAGE = "puzzle";
	private static final String ID_TILE = "t";
	private static final int MARGIN = 3;
	
	@UiField(provided=true)
	PushBundle resources = PushBundle.INSTANCE;
	@UiField(provided=true)
	CommonBundle common = CommonBundle.INSTANCE;
	PushCss style = resources.getCss();
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
	private int currentLevel;
	private DialogBox confirmBox;
	/**
	 * The source image svg element
	 */
	private OMSVGSVGElement srcSvg;
	/**
	 * The puzzle root svg element
	 */
	private OMSVGSVGElement pushSvg;
	/**
	 * BBox of the puzzle image
	 */
	private OMSVGRect bbox;
	/**
	 * A matrix of tile ids reflecting the game state
	 */
	private int[][] game;
	/**
	 *  The tile id of the tile representing the hole
	 */
	private int hole;
	/**
	 * Width of the puzzle border
	 */
	int xcount;
	/**
	 * Number of puzzle tiles in a column
	 */
	int ycount;
	/**
	 * The game tiles (upper left has index 0,
	 * lower right has coordinate length - 1)
	 * There are xcount * ycount -1 tiles (-1
	 * is for the hole)
	 */
	private OMSVGUseElement[] tiles;
	/**
	 * True when the game has begun
	 */
	private boolean playing;
	/**
	 * Use to free the UI at specific times (when displaying help,
	 * when loading a new level, ...)
	 */
	boolean frozen;
	/**
	 * Timer to briefly display the puzzle assembled
	 */
	private Timer waitTimer;
	/**
	 * Time to time scrambles apart
	 */
	private Timer scrambleTimer;
	/**
	 * Class to display an animation when the player wins
	 */
	private int animCount;
	private Animation animation = new Animation() {
		@Override
		protected void onUpdate(double progress) {
			for (int i = 0; i < xcount; i++) {
				for (int j = 0; j < ycount; j++) {
					int index = i * ycount + j;
					if (index != hole) {
						OMSVGTransformList xformList = tiles[index].getTransform().getBaseVal();
						xformList.clear();
						OMSVGTransform r = pushSvg.createSVGTransform();
						r.setRotate((float)(360 * progress * ((animCount % 2 == 0) ? 1f : -1f)), (i + 0.5f) * bbox.getWidth() / xcount, (j + 0.5f) * bbox.getHeight() / ycount);
						xformList.appendItem(r);
					}
				}
			}
		}
		@Override
		protected void onComplete() {
			super.onComplete();
			animCount++;
			if (animCount < 5) {
				DeferredCommand.addCommand(new Command() {
					@Override
					public void execute() {
						animation.run(1000, Duration.currentTimeMillis() + 500);
					}					
				});
			} else {
				playing = true;
				GWT.log(getDescription());
			}
		}
	};
	
	interface PushMainBinder extends UiBinder<VerticalPanel, PushMain> {
	}
	private static PushMainBinder mainBinder = GWT.create(PushMainBinder.class);
	
	private ResizeHandler resizeHandler = new ResizeHandler() {
		@Override
		public void onResize(ResizeEvent event) {
			if (pushSvg != null) {
				float w = Window.getClientWidth() * 0.9f;
				float h = Window.getClientHeight() * 0.8f;
				pushSvg.getStyle().setSVGProperty("width", Float.toString(w));
				pushSvg.getStyle().setSVGProperty("height", Float.toString(h));
			}		
		}
	};

	public void onModuleLoad2(DialogBox confirmBox) {
		StyleInjector.inject(style.getText(), true);
		
		// Load the game levels
		levels = resources.levels().getText().split("\\s");
		
		// Initialize the UI with UiBinder
		VerticalPanel panel = mainBinder.createAndBindUi(this);
		this.confirmBox = confirmBox;
		levelList.addItem(PushConstants.INSTANCE.easy());
		levelList.addItem(PushConstants.INSTANCE.medium());
		levelList.addItem(PushConstants.INSTANCE.hard());
		levelList.setSelectedIndex(0);
		Window.addResizeHandler(resizeHandler);
		RootPanel.get(Intro.ID_UIROOT).add(panel);
		readPushDef();
	}
	
	@UiHandler("prevButton")
	public void prevButton(ClickEvent event) {
		currentLevel--;
		if (currentLevel < 0) {
			currentLevel = levels.length - 1;
		}
		readPushDef();
	}
	@UiHandler("nextButton")
	public void nextButton(ClickEvent event) {
		currentLevel++;
		if (currentLevel >= levels.length) {
			currentLevel = 0;
		}
		readPushDef();
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
		OMSVGDocument document = OMSVGParser.currentDocument();
		OMSVGSVGElement rootSvg = document.createSVGSVGElement();
		rootSvg.addMouseDownHandler(this);
		OMSVGDefsElement defs = document.createSVGDefsElement();
		rootSvg.appendChild(defs);
		
		// Copy the source SVG in a dedicated group inside
		// the defs
		OMSVGGElement imgGroup = document.createSVGGElement();
		imgGroup.setId(ID_IMAGE);
		for (OMNode node : srcSvg.getChildNodes()) {
			imgGroup.appendChild(node.cloneNode(true));
		}
		defs.appendChild(imgGroup);
		
		OMSVGRect viewBox = srcSvg.getViewBox().getBaseVal();
		float width = viewBox.getWidth();
		float height = viewBox.getHeight();
		bbox = rootSvg.createSVGRect();
		viewBox.assignTo(bbox);
		
		// Compute the number of tiles
		if (width < height) {
			xcount = levelList.getSelectedIndex() + 3;
			ycount = (int)(xcount * height / width);
		} else {
			ycount = levelList.getSelectedIndex() + 3;
			xcount = (int)(ycount * width / height);
		}
		hole = xcount * ycount - 1;
		
		
		// Create a thick border with rounded corners around the
		// drawing (15% of the original drawing size, corner radius
		// 2.5% of the original drawing size)
		// Add a 3 pixel margin around the tiles
		float borderWidth = (int)(0.075f * width);
		float borderHeight = (int)(0.075f * height);
		float borderRx = (int)(0.025f * width);
		float borderRy = (int)(0.025f * height);
		OMSVGRectElement borderOut = document.createSVGRectElement(
				viewBox.getX() - borderWidth - MARGIN, 
				viewBox.getY() - borderHeight - MARGIN,
				viewBox.getWidth() + 2 * (borderWidth + MARGIN), 
				viewBox.getHeight() + 2 * (borderHeight + MARGIN), 
				borderRx,
				borderRy);
		borderOut.setClassNameBaseVal(style.borderOut());
		OMSVGRectElement borderIn = document.createSVGRectElement(
				viewBox.getX() - MARGIN, 
				viewBox.getY() - MARGIN,
				viewBox.getWidth() + 2 * MARGIN, 
				viewBox.getHeight() + 2 * MARGIN, 
				borderRx,
				borderRy);
		borderIn.setClassNameBaseVal(style.borderIn());
		rootSvg.appendChild(borderOut);
		rootSvg.appendChild(borderIn);
//		rootSvg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PERCENTAGE, 65f);
//		rootSvg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PERCENTAGE, 65f);
		rootSvg.setViewBox(
				viewBox.getX() - borderWidth - MARGIN, 
				viewBox.getY() - borderHeight - MARGIN,
				viewBox.getWidth() + 2 * (borderWidth + MARGIN), 
				viewBox.getHeight() + 2 * (borderHeight + MARGIN));
		
		// Create the tile clip-path
		// <clipPath id="cp">
	 	//  <rect id="cpr" x="0" y="0" width="130" height="130" rx="10" ry="10"/>
	 	// </clipPath>
		OMSVGClipPathElement clipPath = document.createSVGClipPathElement();
		clipPath.setId(ID_CLIP_PATH);
		OMSVGRectElement clipRect = document.createSVGRectElement(
				viewBox.getX(),
				viewBox.getY(),
				width / xcount,
				height / ycount,
				borderRx,
				borderRy);
		clipRect.setId(ID_CLIP_RECT);
		clipPath.appendChild(clipRect);
		defs.appendChild(clipPath);
		
		// Create the tiles
		tiles = new OMSVGUseElement[xcount * ycount];
		game = new int[xcount][];
		for (int i = 0; i < xcount; i++) {
			game[i] = new int[ycount];
			for (int j = 0; j < ycount; j++) {
				int index = i * ycount + j;
				if (index != hole) {
					// Create the tile definition
					// Each tile definition has the following structure
					// <g id="tileXXX">
					//  <g style="clip-path:url(#cp)">
					//   <g transform="translate(-260,0)">
					//    <use x="0" y="0" xlink:href="#puzzle"/>
					//   </g>
					//  </g>
					//  <use x="0" y="0" xlink:href="#cp1r" style="fill:none;stroke:black;"/>
					// </g>		
					OMSVGGElement tileDef = document.createSVGGElement();
					tileDef.setId(ID_TILE + index);
					OMSVGGElement tileClipPath = document.createSVGGElement();
					tileClipPath.getStyle().setSVGProperty(SVGConstants.CSS_CLIP_PATH_PROPERTY, "url(#" + ID_CLIP_PATH + ")");
					OMSVGGElement tileTransform = document.createSVGGElement();
					OMSVGTransform xform = rootSvg.createSVGTransform();
					xform.setTranslate(
							viewBox.getX() - i * width / xcount, 
							viewBox.getY() - j * height / ycount);
					tileTransform.getTransform().getBaseVal().appendItem(xform);
					OMSVGUseElement imgUse = document.createSVGUseElement();
					imgUse.getX().getBaseVal().setValue(viewBox.getX());
					imgUse.getY().getBaseVal().setValue(viewBox.getY());
					imgUse.getHref().setBaseVal("#" + ID_IMAGE);
					OMSVGUseElement tileBorder = document.createSVGUseElement();
					tileBorder.getX().getBaseVal().setValue(viewBox.getX());
					tileBorder.getY().getBaseVal().setValue(viewBox.getY());
					tileBorder.getHref().setBaseVal("#" + ID_CLIP_RECT);
					tileBorder.setClassNameBaseVal(style.tileBorder());
					tileDef.appendChild(tileClipPath);
					tileClipPath.appendChild(tileTransform);
					tileTransform.appendChild(imgUse);
					tileDef.appendChild(tileBorder);
					defs.appendChild(tileDef);
					
					// Create the tile itself
					// <use x="130" y="260" xlink:href="#tileXXX"/>
					tiles[index] = document.createSVGUseElement();
					tiles[index].getHref().setBaseVal("#" + ID_TILE + index);
					rootSvg.appendChild(tiles[index]);
				}
				setTile(i, j, index);
			}
		}

		// Add the SVG to the HTML page
		Element div = svgContainer.getElement();
		if (pushSvg != null) {
			div.replaceChild(rootSvg.getElement(), pushSvg.getElement());
		} else {
			div.appendChild(rootSvg.getElement());					
		}
		pushSvg = rootSvg;
		resizeHandler.onResize(null);
		if (!GWT.isScript()) {
			GWT.log(pushSvg.getMarkup());
		}

		// Display the puzzle in order for 1 sec, then scramble it
		waitTimer = new Timer() {
			public void run() {
				scrambleTimer = new Timer() {
					int repeatCount;
					@Override
					public void run() {
						int tileCount = xcount * ycount;
						List<Integer> array = new ArrayList<Integer>();
						for (int i = 0; i < tileCount; i++) {
							array.add(i);
						}
						// Shuffle the tiles
						for (int i = 0; i < xcount; i++) {
							for (int j = 0; j < ycount; j++) {
								setTile(i, j, array.remove(Random.nextInt(tileCount--)));
							}
						}
						repeatCount++;
						if (repeatCount >= 5) {
							playing = true;
							cancel();
						}
					}
				};
				if ("true".equals(Window.Location.getParameter("win"))) {
					winAnimation();
				} else {
					scrambleTimer.scheduleRepeating(200);
				}
			}
		};
		waitTimer.schedule(1000);
	}
	
	public boolean gameOver() {
		for (int i = 0; i < xcount; i++) {
			for (int j = 0; j < ycount; j++) {
				if (!(getTile(i, j) == i * ycount + j)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setTile(int x, int y, int value) {
		game[x][y] = value;
		if (value != hole) {
			tiles[value].getX().getBaseVal().setValue(x * bbox.getWidth() / xcount);
			tiles[value].getY().getBaseVal().setValue(y * bbox.getHeight() / ycount);
		}
	}
	public int getTile(int x, int y) {
		return game[x][y];
	}
	
	private String getDescription() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < xcount; i++) {
			builder.append("| ");
			for (int j = 0; j < ycount; j++) {
				builder.append(" ");
				builder.append(getTile(i, j));
			}
			builder.append(" |\n");
		}
		return builder.toString();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (playing) {
			OMSVGPoint coords = getTileCoordinates(event);
			if (coords != null) {
				GWT.log("mouseDown: " + coords.getDescription());
				int x = (int) coords.getX();
				int y = (int) coords.getY();
				boolean shifted = false;
				for (int i = 0; i < xcount; i++) {
					if (game[i][y] == hole) {
						if (x < i) {
							for (int j = i; j > x; j--) {
								setTile(j, y, getTile(j - 1, y));
							}
						} else {
							for (int j = i; j < x; j++) {
								setTile(j, y, getTile(j + 1, y));
							}
						}
						setTile(x, y, hole);
						GWT.log(getDescription());
						shifted = true;
					}
				}
				if (!shifted) {
					for (int i = 0; i < ycount; i++) {
						if (game[x][i] == hole) {
							if (y < i) {
								for (int j = i; j > y; j--) {
									setTile(x, j, getTile(x, j - 1));
								}
							} else {
								for (int j = i; j < y; j++) {
									setTile(x, j, getTile(x, j + 1));
								}
							}
							setTile(x, y, hole);
							GWT.log(getDescription());
						}
					}
				}
				if (gameOver()) {
					winAnimation();
				}
				event.stopPropagation();
			}
		}
	}
	
	public OMSVGPoint getTileCoordinates(MouseEvent e) {
		OMSVGPoint p = pushSvg.createSVGPoint(e.getClientX(), e.getClientY());
		OMSVGMatrix m = pushSvg.getScreenCTM().inverse();
		p = p.matrixTransform(m).substract(pushSvg.createSVGPoint(bbox.getX(), bbox.getY())).product(pushSvg.createSVGPoint(xcount / bbox.getWidth(), ycount / bbox.getHeight())).floor();
		return pushSvg.createSVGRect(0, 0, xcount - 1, ycount - 1).contains(p) ? p : null;
	}
	
	public void winAnimation() {
		playing = false;
		animCount = 0;
		animation.run(1000, Duration.currentTimeMillis() + 500);
	}


	private String getLevelUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + levels[currentLevel];
	}

	public void readPushDef() {
		playing = false;
		if (waitTimer != null) {
			waitTimer.cancel();
		}
		if (scrambleTimer != null) {
			scrambleTimer.cancel();
		}
		if (animation != null) {
			animation.cancel();
		}
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

