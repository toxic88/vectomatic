/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of libgwtsvg-samples.
 * 
 * libgwtsvg-samples is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg-samples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with libgwtsvg-samples.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.samples.client;

import org.vectomatic.svg.samples.client.events.EventSample;
import org.vectomatic.svg.samples.client.parser.ParserSample;
import org.vectomatic.svg.samples.client.shapes.ShapesSample;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitPanelHelper;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class Main implements EntryPoint {
	interface MainBinder extends UiBinder<DecoratorPanel, Main> {
	}
	private static MainBinder binder = GWT.create(MainBinder.class);

	/**
	 * The type passed into the
	 * {@link org.vectomatic.svg.samples.generator.SourceGenerator}.
	 */
	private static final class GeneratorInfo {
	}
	
	@UiField
	HorizontalSplitPanel splitPanel;
	@UiField
	Tree tree;
	@UiField
	SimplePanel sampleContainer;

	public int getHeight() {
		return (Window.getClientHeight() - 150);
	}

	@Override
	public void onModuleLoad() {
	    // Generate the source code for the examples
	    GWT.create(GeneratorInfo.class);
	    DecoratorPanel panel = binder.createAndBindUi(this);
	    
		// Handle resizing issues.
		ResizeHandler resizeHandler = new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				String width = (Window.getClientWidth() - 20) + "px";
				String height = getHeight() + "px";
				splitPanel.setSize(width, height);
				updateSplitPanel();
			}
		};
		Window.addResizeHandler(resizeHandler);
		resizeHandler.onResize(null);
		splitPanel.setSplitPosition("20%");

		// Populate the sample tree
	    TreeItem shapesSample = tree.addItem("shapes");
	    shapesSample.setUserObject(new ShapesSample());
	    TreeItem eventSample = tree.addItem("events");
	    eventSample.setUserObject(new EventSample());
	    TreeItem parserSample = tree.addItem("parser");
	    parserSample.setUserObject(new ParserSample());
	    tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				SampleBase sample = (SampleBase) event.getSelectedItem().getUserObject();
				Panel samplePanel = sample.getPanel();
				sampleContainer.setWidget(samplePanel);
			}
	    	
	    });
	    tree.setSelectedItem(shapesSample);
	    RootPanel.get("uiRoot").add(panel);
	}
	
	private void updateSplitPanel() {
		Style style = SplitPanelHelper.getStyle(splitPanel);
		String rawWidth = style.getWidth();
		GWT.log("Main.updateSplitPanel(" + rawWidth + ")", null);
		if (rawWidth != null && rawWidth.length() > 0) {
			// Process events with size in pixels only
			int index = rawWidth.indexOf(Style.Unit.PX.name().toLowerCase());
			if (index != -1) {
				try {
					int width = Integer.valueOf(rawWidth.substring(0, index));
					int height = getHeight();
					int size = Math.min(width, height);
				} catch(NumberFormatException e) {
					GWT.log("Incorrect width: " + rawWidth, e);
				}
			}
		}
	}

}
