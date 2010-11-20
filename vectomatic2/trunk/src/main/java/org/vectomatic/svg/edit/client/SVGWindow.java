/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGIconTreeNode;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.RotationEvent;
import org.vectomatic.svg.edit.client.event.RotationHandler;
import org.vectomatic.svg.edit.client.gxt.AbsoluteLayerLayout;
import org.vectomatic.svg.edit.client.gxt.AbsoluteLayerLayoutData;
import org.vectomatic.svg.edit.client.widget.Compass;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * GXT window class dedicated to displaying and editing
 * a single SVG image. The window has several layers: the
 * bottom layer contains the SVG image itself and the top
 * layer contains widgets to manipulate it (rotation compass
 * and scale slider).
 * @author laaglu
 */
public class SVGWindow extends Window {
	/**
	 * The SVG model backing this window
	 */
	private SVGModel model;
	/**
	 * The SVG rotation compass
	 */
	protected Compass compass;
	/**
	 * The SVG scale slider
	 */
	protected Slider scaleSlider;

	/**
	 * Constructor
	 * @param model
	 * The SVG model to display
	 */
	public SVGWindow(final SVGModel model) {
		super();
		this.model = model;
		setPlain(true);
		setMaximizable(true);
		setSize(500, 300);
		setMinWidth(200);
		setMinHeight(170);
		
	    /////////////////////////////////////////////////
		// A CSS multi-layer container
	    // The container hierarchy is as follows:
	    // splitterPanel (LayoutContainer + BorderLayout)
	    //   tree (TreePanel)
	    //   layersContainer (LayoutContainer + AbsoluteLayerLayout)
	    //     svgContainer (LayoutContainer)
	    //       image (SVGImage)
	    //     compass (SVGImage)
	    //     scaleSlider (Slider)
	    /////////////////////////////////////////////////
	    LayoutContainer splitterPanel = new LayoutContainer();
	    splitterPanel.setLayout(new BorderLayout());

	    LayoutContainer layersContainer = new LayoutContainer();
		GWT.log("borders: " + getBorders());
	    layersContainer.setLayout(new AbsoluteLayerLayout());
	    
	    // Create the SVG view
		LayoutContainer svgContainer = new LayoutContainer();
	    svgContainer.setScrollMode(Style.Scroll.AUTO);
	    svgContainer.setStyleAttribute("background-color", SVGConstants.CSS_WHITE_VALUE);
	    SVGImage image = new SVGImage(model.getDocumentRoot()) {
	    	protected void onAttach() {
	    		GWT.log("onAttach");
	    		model.onAttach();
	    	}
	    };
	    svgContainer.add(image);
	    layersContainer.add(svgContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_LEFT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		10));
	    
	    // Create the tree view
	    TreeStore<SVGIconTreeNode> treeStore = new TreeStore<SVGIconTreeNode>();
	    treeStore.add(model.getNavigationTree(), true);
		TreePanel<SVGIconTreeNode> tree = new TreePanel<SVGIconTreeNode>(treeStore);
	    tree.setIconProvider(new ModelIconProvider<SVGIconTreeNode>() {
			@Override
			public AbstractImagePrototype getIcon(SVGIconTreeNode model) {
				return model.getIcon();
			}
	    	
	    });
	    tree.setCheckable(true);
	    tree.setWidth(150);  
	    tree.setDisplayProperty(SVGModel.TITLE_PROPERTY.getPropertyId());
	    tree.setTrackMouseOver(true);
	    tree.addListener(Events.OnMouseOver, new Listener<TreePanelEvent<SVGIconTreeNode>>() {
	        public void handleEvent(TreePanelEvent<SVGIconTreeNode> be) {
	        	SVGIconTreeNode node = be.getItem();
	        	if (node != null) {
	        		model.highlightElement(node.getElement());
	        	}
	        }
	    });
	    tree.setStyleAttribute("background-color", SVGConstants.CSS_WHITE_VALUE);
	    
	    BorderLayoutData layoutData = new BorderLayoutData(LayoutRegion.WEST, 150, 100, 250);  
	    layoutData.setMargins(new Margins(0, 5, 0, 0));  
	    layoutData.setSplit(true);  
	    layoutData.setCollapsible(true);  
	    splitterPanel.add(tree, layoutData);
	    splitterPanel.add(layersContainer, new BorderLayoutData(LayoutRegion.CENTER));

	    /////////////////////////////////////////////////
	    // Populate the higher layer
	    /////////////////////////////////////////////////
		
		// Create the compass
	    compass = GWT.create(Compass.class);
	    final OMSVGSVGElement compassSvg = compass.getSvgElement();
	    compassSvg.getStyle().setWidth(100, Unit.PX);
	    compassSvg.getStyle().setHeight(100, Unit.PX);
	    compass.addRotationHandler(new RotationHandler() {
	    	@Override
	    	public void onRotate(RotationEvent event) {
	    		model.setRotation(event.getAngle());
	    	}	
	    });
		LayoutContainer compassContainer = new LayoutContainer();
		AppCss css = AppBundle.INSTANCE.css();
		compassContainer.addStyleName(css.compassContainer());
		SVGImage compassImage = new SVGImage(compassSvg);
		compassImage.addClassNameBaseVal(css.compass());
		compassContainer.add(compassImage);
	    layersContainer.add(compassContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		20));
	    
		// Create the scale slider
		LayoutContainer sliderContainer = new LayoutContainer();
		sliderContainer.addStyleName(css.scaleSliderContainer());
		scaleSlider = new Slider() {
			@Override
	    	protected String onFormatValue(int value) {
				return Integer.toString((int)(model.getScale() * 100)) + "%";
	    	}

		};
		scaleSlider.addStyleName(css.scaleSlider());
		sliderContainer.add(scaleSlider);
		scaleSlider.setHeight(100);
		scaleSlider.setMinValue(0);
		scaleSlider.setMaxValue(100);
		scaleSlider.setIncrement(1);
		scaleSlider.setValue(50);
		scaleSlider.setVertical(true);
		layersContainer.add(sliderContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		20));
		scaleSlider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				// Convert from slider unit to transform unit
				int value = be.getNewValue();
				float scale;
				if (value >= 50) {
					scale = 1f + (value - 50f) / 10f * 4 / 5;
				} else {
					scale = 1f / (1f + (49 - value) / 10f * 4 / 5);
				}
				model.setScale(scale);
			}	    	
	    });
	    
	    ToolBar toolBar = new ToolBar();
	    ToggleButton selectButton = new ToggleButton();
	    selectButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.cursor()));
	    selectButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				model.setSelectionMode(((ToggleButton)ce.getButton()).isPressed());
			}
	    });
	    toolBar.add(selectButton);
	    setTopComponent(toolBar);
	    
		setLayout(new FitLayout());
		add(splitterPanel, new FitData(4));
	}
	
	@Override
	protected void moveDrag(DragEvent de) {
		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
		if (de.getY() < windowBarHeight) {
			de.setY(windowBarHeight);
		}
	}
	
	public SVGModel getModel() {
		return model;
	}

	/**
	 * Sets the scaling of the main image through the scale slider.
	 * @param scale
	 * The scale (50 means scale 1:1)
	 */
	public void setScaleSlider(int value) {
		scaleSlider.setValue(value);
	}

	/**
	 * Sets the rotation of the main image through the
	 * compass widget.
	 * @param angleDeg
	 * The angle (in degrees)
	 */
	public void setRotationCompass(int angleDeg) {
		compass.setRotation(angleDeg);
	}	
	/* GWT bug ?
	 * line 234: The method endDrag(DragEvent) in the type Window is not applicable for the arguments (DragEvent, boolean)*/
//	protected void endDrag(DragEvent de, boolean canceled) {
//		GWT.log("endDrag" + de.getX() + " " + de.getY());
//		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
//		if (de.getY() < windowBarHeight) {
//			de.setY(windowBarHeight);
//		}
//		super.endDrag(de, canceled);
//	}
}

