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

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.svg.edu.client.commons.AsyncXmlLoader;
import org.vectomatic.svg.edu.client.commons.AsyncXmlLoaderCallback;
import org.vectomatic.svg.edu.client.commons.CommonBundle;
import org.vectomatic.svg.edu.client.commons.CommonConstants;
import org.vectomatic.svg.edu.client.commons.LicenseBox;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main class for the puzzle game
 */
public class PuzzleMain implements EntryPoint {
	private static final String DIR = "puzzle";
	interface PuzzleMainBinder extends UiBinder<FlowPanel, PuzzleMain> {
	}
	private static PuzzleMainBinder mainBinder = GWT.create(PuzzleMainBinder.class);

	
	@UiField(provided=true)
	PuzzleBundle resources = PuzzleBundle.INSTANCE;
	@UiField(provided=true)
	CommonBundle common = CommonBundle.INSTANCE;
	PuzzleCss style = resources.getCss();
	@UiField
	SVGPushButton prevButton;
	@UiField
	SVGPushButton nextButton;
	@UiField
	HTML svgContainer;
	@UiField
	ListBox levelList;
	@UiField
	FlowPanel navigationPanel;
	Widget menuWidget;
	private String[] levels;
	private int level;
	/**
	 * To load game levels
	 */
	AsyncXmlLoader loader;
	/**
	 * The source image svg element
	 */
	private OMSVGSVGElement srcSvg;
	OMSVGSVGElement puzzleSvg;
	private Puzzle puzzle;
	int[][] dimensions = {{3, 3}, {4, 4}, {5, 5}, {7, 5}, {8, 6}};
	
	/**
	 * Constructor for standalone game
	 */
	public PuzzleMain() {
	}
	/**
	 * Constructor for integration in a menu
	 */
	public PuzzleMain(Widget menuWidget) {
		this.menuWidget = menuWidget;
	}
	
	/**
	 * Entry point
	 */
	@Override
	public void onModuleLoad() {
		common.css().ensureInjected();
		StyleInjector.inject(style.getText(), true);
		
		// Load the game levels
		levels = resources.levels().getText().split("\\s");
		loader = GWT.create(AsyncXmlLoader.class);
		
		// Initialize the UI with UiBinder
		FlowPanel panel = mainBinder.createAndBindUi(this);
		if (menuWidget == null) {
			menuWidget = LicenseBox.createAboutButton();
		}
		navigationPanel.insert(menuWidget, 0);
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
		RootPanel.get(CommonConstants.ID_UIROOT).add(panel);
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

	@UiHandler("levelList")
	public void levelChange(ChangeEvent event) {
		generate();
	}
	
	private void generate() {
		int[] dimension = dimensions[levelList.getSelectedIndex()];
		puzzle = new Puzzle(srcSvg, dimension[0], dimension[1]);
		puzzle.shuffle();
		OMSVGSVGElement rootSvg = puzzle.getSvgElement();
		
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
		String url = GWT.getModuleBaseURL() + DIR + "/" + levels[level];
		loader.loadResource(url, new AsyncXmlLoaderCallback() {
			@Override
			public void onError(String resourceName, Throwable error) {
				svgContainer.setHTML("Cannot find resource");
			}

			@Override
			public void onSuccess(String resourceName, com.google.gwt.dom.client.Element root) {
				srcSvg = OMNode.convert(root);
				generate();
			}
		});
	}
}
