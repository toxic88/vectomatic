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
package org.vectomatic.svg.edu.client.dots;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGFEColorMatrixElement;
import org.vectomatic.dom.svg.OMSVGFEGaussianBlurElement;
import org.vectomatic.dom.svg.OMSVGFilterElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGNumber;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGPolylineElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMText;
import org.vectomatic.dom.svg.ui.SVGPushButton;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edu.client.CommonBundle;
import org.vectomatic.svg.edu.client.ConfirmBox;
import org.vectomatic.svg.edu.client.Intro;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoseCaptureEvent;
import com.google.gwt.event.dom.client.LoseCaptureHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.HSliderBar;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderListenerAdapter;

/**
 * Main game class
 * @author laaglu
 */
public class DotsMain implements MouseDownHandler, MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseUpHandler, LoseCaptureHandler {
	interface DotsMainBinder extends UiBinder<VerticalPanel, DotsMain> {
	}
	private static DotsMainBinder mainBinder = GWT.create(DotsMainBinder.class);
	
	private static String[] pictures;
	
	enum Mode {
		GAME,
		DESIGN
	};
	private static final String DIR = "connectdots";
	private static final String ID_ALPHA1_FILTER = "pictureAlpha";
	private static final String ID_TRANSITION_FILTER = "pictureTransition";
	private static final String ID_ALPHA2_FILTER = "dotAlpha";

	private static final String STYLE_DOT1 = "dot1";
	private static final String STYLE_DOT2 = "dot2";
	private static final String STYLE_DOT3 = "dot3";
	private static final String STYLE_DOT1S = "dot1s";
	@SuppressWarnings("unused")
	private static final String STYLE_DOT2S = "dot2s";
	private static final String STYLE_DOT1V = "dot1v";
	@SuppressWarnings("unused")
	private static final String STYLE_DOT2V = "dot2v";
	private static final String STYLE_LINE1 = "line1";
	private static final String STYLE_LINE2 = "line2";
	
	@UiField(provided=true)
	CommonBundle common = CommonBundle.INSTANCE;
	@UiField
	HTML svgContainer;
	@UiField
	SVGPushButton prevButton;
	@UiField
	SVGPushButton nextButton;
	@UiField
	SVGPushButton homeButton;

	@UiField
	DecoratorPanel designPanel;
	@UiField
	Button addButton;
	@UiField
	Button removeButton;
	@UiField
	Button saveButton;
	@UiField
	Button testButton;
	@UiField
	CheckBox showLineCheck;
	@UiField
	HSliderBar pictureAlphaSlider;
	@UiField
	Label fileLabel;
	@UiField
	ListBox dotList;
	@UiField
	TextArea textArea;
	private VerticalPanel panel;
	DialogBox confirmBox;
	
	/**
	 * Index of the currently displayed image in the pictures array
	 */
	private int pictureIndex;
	/**
	 * The SVG document. The document has the following structure
	 * <tt>
	 * <svg>           // rootSvg
	 *  <svg/>         // pictureSvg
	 *  <svg>          // dotSvg
	 *   <g>           // lineGroup
	 *    <polyline>   // polyline
	 *   </g>
	 *   <g>           // dotGroup
	 *    <g/>         // first dot
	 *    <g/>         // N-th dot
	 *   </g>
	 *  </svg>
	 * </svg>
	 * </tt>
	 */
	private OMSVGDocument doc;
	private OMSVGSVGElement rootSvg;
	private OMSVGSVGElement pictureSvg;
	private OMSVGSVGElement dotSvg;
	private OMSVGGElement dotGroup;
	private OMSVGGElement lineGroup;
	OMSVGPolylineElement polyline;
	/**
	 * The vertices of the polyline
	 */
	OMSVGPointList points;
	/**
	 * The dots in dotGroup
	 */
	private List<OMSVGGElement> dots;
	/**
	 * The dot currently being moved
	 */
	private OMSVGGElement currentDot;
	/**
	 * The index in dotGroup of dot currently being moved
	 */
	private int currentDotIndex;
	/**
	 * The alpha channel in the filter applied to pictureSvg in edit mode
	 */
	private OMSVGNumber pictureAlpha1;
	/**
	 * The alpha channel in the filter applied to pictureSvg in game mode
	 */
	private OMSVGNumber pictureAlpha2;
	/**
	 * The Gaussian blur filter applied to pictureSvg
	 */
	private OMSVGFEGaussianBlurElement gaussianBlur;
	/**
	 * The alpha channel in the filter applied to dotSvg
	 */
	private OMSVGNumber dotAlpha;
	/**
	 * The mousedown point
	 */
	private OMSVGPoint mouseDownPoint;
	/**
	 * The position of the current dot at the time of mousedown
	 */
	private OMSVGPoint p0;
	/**
	 * The current mode
	 */
	private Mode mode;
	/**
	 * The index of the last dot found by the player
	 */
	private int maxIndex;

	
	public void onModuleLoad2() {
		
		// Initialize the UI with UiBinder
		panel = mainBinder.createAndBindUi(this);
		confirmBox = ConfirmBox.createConfirmBox();
		designPanel.setVisible(false);
		RootPanel.get(Intro.ID_UIROOT).add(panel);
		Element div = svgContainer.getElement();
		div.getStyle().setWidth(100, Style.Unit.PCT);
		div.getStyle().setHeight(100, Style.Unit.PCT);
		
		// Handle resizing issues.
		ResizeHandler resizeHandler = new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updatePictureSize();
			}
		};
		Window.addResizeHandler(resizeHandler);
		
		pictureAlphaSlider.addSliderListener(new SliderListenerAdapter() {
			@Override
			public void onValueChanged(SliderBar slider, double curValue) {
				setPictureAlpha((float)curValue);
			}			
		});

		dots = new ArrayList<OMSVGGElement>();
		
		// Create the root SVG structure elements
		doc = OMSVGParser.createDocument();
		rootSvg = doc.createSVGSVGElement();
		pictureSvg = doc.createSVGSVGElement();
		dotSvg = doc.createSVGSVGElement();
		dotGroup = doc.createSVGGElement();
		lineGroup = doc.createSVGGElement();

		// Create the SVG filters
		OMSVGDefsElement defs = doc.createSVGDefsElement();
		OMSVGFilterElement alpha1Filter = doc.createSVGFilterElement();
		alpha1Filter.setId(ID_ALPHA1_FILTER);
		OMSVGFEColorMatrixElement feColorMatrix1 = doc.createSVGFEColorMatrixElement();
		feColorMatrix1.getIn1().setBaseVal(OMSVGFilterElement.IN_SOURCE_GRAPHIC);
		feColorMatrix1.getType().setBaseVal(OMSVGFEColorMatrixElement.SVG_FECOLORMATRIX_TYPE_MATRIX);
		pictureAlpha1 = feColorMatrix1.getValues().getBaseVal().appendItems(rootSvg, new float[]{
				1f, 0f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f, 0f,
				0f, 0f, 1f, 0f, 0f,
				0f, 0f, 0f, 1f, 0f,
		})[18];
		
		OMSVGFilterElement transitionFilter = doc.createSVGFilterElement();
		transitionFilter.setId(ID_TRANSITION_FILTER);
		gaussianBlur = doc.createSVGFEGaussianBlurElement();
		gaussianBlur.setStdDeviation(0, 0);
		gaussianBlur.getIn1().setBaseVal(OMSVGFilterElement.IN_SOURCE_GRAPHIC);
		gaussianBlur.getResult().setBaseVal("blur");
		
		gaussianBlur = doc.createSVGFEGaussianBlurElement();
		gaussianBlur.setStdDeviation(0, 0);
		gaussianBlur.getIn1().setBaseVal(OMSVGFilterElement.IN_SOURCE_GRAPHIC);
		gaussianBlur.getResult().setBaseVal("blur");
		
		OMSVGFEColorMatrixElement feColorMatrix2 = doc.createSVGFEColorMatrixElement();
		feColorMatrix2.getIn1().setBaseVal("blur");
		feColorMatrix2.getType().setBaseVal(OMSVGFEColorMatrixElement.SVG_FECOLORMATRIX_TYPE_MATRIX);
		pictureAlpha2 = feColorMatrix2.getValues().getBaseVal().appendItems(rootSvg, new float[]{
				1f, 0f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f, 0f,
				0f, 0f, 1f, 0f, 0f,
				0f, 0f, 0f, 1f, 0f,
		})[18];

		OMSVGFilterElement alpha3Filter = doc.createSVGFilterElement();
		alpha3Filter.setId(ID_ALPHA2_FILTER);
		OMSVGFEColorMatrixElement feColorMatrix3 = doc.createSVGFEColorMatrixElement();
		feColorMatrix3.getIn1().setBaseVal(OMSVGFilterElement.IN_SOURCE_GRAPHIC);
		feColorMatrix3.getType().setBaseVal(OMSVGFEColorMatrixElement.SVG_FECOLORMATRIX_TYPE_MATRIX);
		dotAlpha = feColorMatrix3.getValues().getBaseVal().appendItems(rootSvg, new float[]{
				1f, 0f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f, 0f,
				0f, 0f, 1f, 0f, 0f,
				0f, 0f, 0f, 1f, 0f,
		})[18];


		// Compose the root SVG structure
		rootSvg.appendChild(pictureSvg);
		rootSvg.appendChild(defs);
		defs.appendChild(alpha1Filter);
		alpha1Filter.appendChild(feColorMatrix1);
		defs.appendChild(transitionFilter);
		transitionFilter.appendChild(gaussianBlur);
		transitionFilter.appendChild(feColorMatrix2);
		defs.appendChild(alpha3Filter);
		alpha3Filter.appendChild(feColorMatrix3);
		rootSvg.appendChild(dotSvg);
		dotSvg.appendChild(lineGroup);
		dotSvg.appendChild(dotGroup);

		// Add the SVG to the HTML page
		div.appendChild(rootSvg.getElement());					

		// Read the picture list
		pictures = DotsResources.INSTANCE.pictureList().getText().split("\\s");
		updateLevel();
	}
	
	/**
	 * UiBinder factory method to instantiate HSliderBar 
	 * @return
	 */
	@UiFactory
	HSliderBar makeHSliderBar() {
		HSliderBar sliderBar = new HSliderBar(0, 1);
		sliderBar.setStepSize(0.1);
		sliderBar.setCurrentValue(0);
		return sliderBar;
	}

	private void updatePictureSize() {
		float width = Window.getClientWidth() - 50;
		float height = Window.getClientHeight() - 50;
		if (rootSvg != null) {
			rootSvg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PX, width * 0.8f);
			rootSvg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PX, height * 0.8f);
			OMSVGMatrix m = dotSvg.getScreenCTM().inverse();
			updateScales(m.getA(), m.getD());
			panel.setCellWidth(svgContainer, width * 0.8f + "px");
			panel.setCellHeight(svgContainer, height * 0.8f + "px");
		}
	}
	
	private void updateLevel() {
		fileLabel.setText(pictures[pictureIndex]);
		
		maxIndex = -1;
		dots.clear();
		dotList.clear();

		// The data come in two files: a picture file and a dot file
		// In design mode, both must be read
		// In game mode, the dot file is read first and the picture file
		// is read later if the player succeeds
		readMode();
		if (mode == Mode.DESIGN) {
			readPicture(true);
		} else {
			readDots();
		}
	}
	
	private String getPictureUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + pictures[pictureIndex];
	}
	
	private String getDotsUrl() {
		return GWT.getModuleBaseURL() + DIR + "/" + pictures[pictureIndex] + ".dots";
	}
	
	private void readMode() {
		// Get the mode
		String modeString = Window.Location.getParameter("mode");
		mode = "design".equals(modeString) ? Mode.DESIGN : Mode.GAME;
	}
	
	public void readPicture(final boolean readDots) {
		RequestBuilder pictureBuilder = new RequestBuilder(RequestBuilder.GET, getPictureUrl());
		pictureBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				svgContainer.setHTML("Cannot find resource");
			}

			/**
			 * Parse the SVG artwork and launch the parse of the dot structure
			 */
			private void onSuccess(Request request, Response response) {
				// Parse the document
				OMSVGSVGElement svg = OMSVGParser.parse(response.getText());
				
				// Position the filter
				pictureAlpha2.setValue(0f);
				svg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_TRANSITION_FILTER));
				
				// Insert the SVG root element into the HTML UI
				rootSvg.replaceChild(svg, pictureSvg);
				pictureSvg = svg;
					
				// Send the dots request
				if (readDots) {
					readDots();
				} else {
					transition(null);
				}
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
			GWT.log("Cannot fetch " + getPictureUrl(), e);
		}
	}
	
	public void readDots() {
		// Add a bogus query to bypass the browser cache as advised by:
		// https://developer.mozilla.org/En/Using_XMLHttpRequest#Bypassing_the_cache
		String url = getDotsUrl();
		url += (url.indexOf("?") == -1) ? ("?ts=" + System.currentTimeMillis()) : ("&ts=" + + System.currentTimeMillis());
		final RequestBuilder dotsBuilder = new RequestBuilder(RequestBuilder.GET, getDotsUrl());
		dotsBuilder.setCallback(new RequestCallback() {
			/**
			 * Create a blank dots structure
			 */
			public void onError(Request request, Throwable exception) {
				if (mode == Mode.GAME) {
					mode = Mode.DESIGN;
					readPicture(true);
					return;
				}
				OMSVGSVGElement svg = doc.createSVGSVGElement();
				svg.setAttribute(SVGConstants.XMLNS_PREFIX, SVGConstants.SVG_NAMESPACE_URI);
				svg.setAttribute(SVGConstants.XMLNS_PREFIX + ":" + SVGConstants.XLINK_PREFIX, SVGConstants.XLINK_NAMESPACE_URI);
				rootSvg.replaceChild(svg, dotSvg);
				dotSvg = svg;

				// Synchronize the the dot coordinates with the picture coordinates
				if (mode == Mode.DESIGN) {
					dotSvg.setViewBox(pictureSvg.getViewBox().getBaseVal());
				}
				
				// Reset the polyline
				lineGroup = doc.createSVGGElement();
				dotSvg.appendChild(lineGroup);
				polyline = doc.createSVGPolylineElement();
				polyline.setClassNameBaseVal(STYLE_LINE2);			
				points = polyline.getPoints();
				lineGroup.appendChild(polyline);
				
				// Reset the dots
				dotGroup = doc.createSVGGElement();
				dotSvg.appendChild(dotGroup);
			}
			
			/**
			 * Create a dots structure populated by the SVG data
			 */
			private void onSuccess(Request request, Response response) {
				OMSVGSVGElement svg = OMSVGParser.parse(response.getText());
				rootSvg.replaceChild(svg, dotSvg);
				dotSvg = svg;

				// Synchronize the the dot coordinates with the picture coordinates
				if (mode == Mode.DESIGN) {
					dotSvg.setViewBox(pictureSvg.getViewBox().getBaseVal());
				}
				
				dotGroup = (OMSVGGElement) dotSvg.getFirstChild();
				// Reset the polyline
				lineGroup = doc.createSVGGElement();
				dotSvg.appendChild(lineGroup);
				polyline = doc.createSVGPolylineElement();
				polyline.setClassNameBaseVal(STYLE_LINE2);			
				points = polyline.getPoints();
				lineGroup.appendChild(polyline);
				
				// Parse the dots to recreate the polyline
				OMNodeList<OMSVGGElement> childNodes = dotGroup.getChildNodes();
				for (int i = 0, size = childNodes.getLength(); i < size; i++) {
					OMSVGGElement g1 = childNodes.getItem(i);
					g1.addMouseDownHandler(DotsMain.this);
					if (mode == Mode.DESIGN) {
						g1.addMouseMoveHandler(DotsMain.this);
						g1.addMouseUpHandler(DotsMain.this);
					}
					g1.addMouseOverHandler(DotsMain.this);
					g1.addMouseOutHandler(DotsMain.this);
					dots.add(g1);
					dotList.addItem(toDotName(i));
					if (mode == Mode.DESIGN) {
						OMSVGMatrix m = g1.getTransform().getBaseVal().getItem(0).getMatrix();
						points.appendItem(dotSvg.createSVGPoint(m.getE(), m.getF()));
					}
				}
			}

			public void onResponseReceived(Request request, Response response) {

				if (response.getStatusCode() == Response.SC_OK) {
					onSuccess(request, response);
				} else {
					if (mode == Mode.GAME) {
						mode = Mode.DESIGN;
						readPicture(true);
						return;
					}
					onError(request, null);
				}

				if (mode == Mode.DESIGN) {
					pictureSvg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_ALPHA1_FILTER));
					setPictureAlpha(1f);
					showLineCheck.setValue(false);
					pictureAlphaSlider.setCurrentValue(1);			
				} else {
					pictureSvg.getStyle().setVisibility(Visibility.HIDDEN);
					polyline.setClassNameBaseVal(STYLE_LINE1);
				}
				dotAlpha.setValue(1f);
				dotSvg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_ALPHA2_FILTER));
				designPanel.setVisible(mode == Mode.DESIGN);

				// Resize to the size of the window
				updatePictureSize();
				updateUI();
			}
		});
		// Send the dots request
		try {
			dotsBuilder.send();
		} catch (RequestException e) {
			GWT.log("Cannot fetch " + dotsBuilder.getUrl(), e);
		}
	
	}
	
	@UiHandler("prevButton")
	public void previousPicture(ClickEvent event) {
		pictureIndex--;
		if (pictureIndex < 0) {
			pictureIndex = pictures.length - 1;
		}
		updateLevel();
	}
	
	@UiHandler("nextButton")
	public void nextPicture(ClickEvent event) {
		pictureIndex++;
		if (pictureIndex >= pictures.length) {
			pictureIndex = 0;
		}
		updateLevel();
	}
	
	@UiHandler("homeButton")
	public void homeButton(ClickEvent event) {
        confirmBox.center();
        confirmBox.show();
	}

	private OMSVGPoint getLocalCoordinates(MouseEvent<? extends EventHandler> e) {
		OMSVGPoint p = dotSvg.createSVGPoint(e.getClientX(), e.getClientY());
		OMSVGMatrix m = dotSvg.getScreenCTM().inverse();
		return p.matrixTransform(m);
	}
	
	@UiHandler("addButton")
	public void addDot(ClickEvent event) {
		int pIndex = dots.size();
		OMSVGRect viewBox = dotSvg.getViewBox().getBaseVal();
		// Position the new points in a circle centered at the view box
		// with a radius of 20%
		float r = Math.min(viewBox.getWidth(), viewBox.getHeight()) * 0.2f;
		float x = ((float)Math.cos(pIndex / 16d * 2d * Math.PI)) * r + viewBox.getCenterX();
		float y = ((float)Math.sin(pIndex / 16d * 2d * Math.PI)) * r + viewBox.getCenterY();
		OMSVGGElement g1 = createPoint(pIndex + 1, x, y);
		dots.add(g1);
		dotGroup.appendChild(g1);
		dotList.addItem(toDotName(pIndex));
		// Autoselect the new point
		dotList.setSelectedIndex(dotList.getItemCount() - 1);
		updateUI();	
	}

	@UiHandler("removeButton")
	public void removeDot(ClickEvent event) {
		int index = dotList.getSelectedIndex();
		OMSVGGElement g1 = dots.remove(index);
		dotGroup.removeChild(g1);
		dotList.removeItem(index);
		points.removeItem(index);
		updateUI();
		renumber();
	}

	@UiHandler("saveButton")
	public void save(ClickEvent event) {
		dotSvg.removeChild(lineGroup);
		dotSvg.removeAttribute(SVGConstants.SVG_STYLE_ATTRIBUTE);
		textArea.setText(dotSvg.getMarkup());
		dotSvg.insertBefore(lineGroup, dotGroup);
		dotSvg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_ALPHA2_FILTER));
	}

	@UiHandler("showLineCheck")
	public void toggleShowLine(ClickEvent event) {
		polyline.setClassNameBaseVal(showLineCheck.getValue() ? STYLE_LINE1 : STYLE_LINE2);
	}
		
	@UiHandler("dotList")
	void onChange(ChangeEvent event) {
		updateUI();
	}
	
	@UiHandler("testButton")
	public void transition(ClickEvent event) {
		pictureAlpha2.setValue(0f);
		gaussianBlur.setStdDeviation(10f, 10f);
		polyline.setClassNameBaseVal(STYLE_LINE1);
		pictureSvg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_TRANSITION_FILTER));
		if (points.getNumberOfItems() > 0) {
			points.appendItem(points.getItem(0).assignTo(dotSvg.createSVGPoint()));
		}
		Animation transition = new Animation() {
			@Override
			protected void onUpdate(double progress) {
				pictureAlpha2.setValue((float)progress);
				float stdDev = 10f * (1f - (float)progress);
				gaussianBlur.setStdDeviation(stdDev, stdDev);
				dotAlpha.setValue(1f - (float)progress);
			}
			@Override
			protected void onComplete() {
				if (mode == Mode.DESIGN) {
					polyline.setClassNameBaseVal(showLineCheck.getValue() ? STYLE_LINE1 : STYLE_LINE2);
					pictureSvg.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(ID_ALPHA1_FILTER));
					dotAlpha.setValue(1f);
					if (points.getNumberOfItems() > 0) {
						points.removeItem(points.getNumberOfItems() - 1);
					}
				} else {
					pictureAlpha2.setValue(1f);
					gaussianBlur.setStdDeviation(0.00001f, 0.00001f);
					dotAlpha.setValue(0f);		
				}
			}
		};
		pictureSvg.getStyle().setVisibility(Visibility.VISIBLE);
		transition.run(2000, Duration.currentTimeMillis() + 1000);
	}
	
	private void updateUI() {
		textArea.setText("");
		removeButton.setEnabled(dotList.getSelectedIndex() != -1);
	}

	private OMSVGGElement createPoint(int pIndex, float x, float y) {
		OMSVGGElement g1 = doc.createSVGGElement();
		OMSVGTransform translation = rootSvg.createSVGTransform();
		translation.setTranslate(x, y);
		g1.getTransform().getBaseVal().appendItem(translation);

		OMSVGGElement g2 = doc.createSVGGElement();
		OMSVGTransform scaling = rootSvg.createSVGTransform();
		OMSVGMatrix m = dotSvg.getScreenCTM().inverse();
		scaling.setScale(m.getA(), m.getD());
		g2.getTransform().getBaseVal().appendItem(scaling);

		OMSVGCircleElement circle1 = doc.createSVGCircleElement(0f, 0f, 5f);
		circle1.setClassNameBaseVal(STYLE_DOT1);
		
		OMSVGCircleElement circle2 = doc.createSVGCircleElement(0f, 0f, 3f);
		circle2.setClassNameBaseVal(STYLE_DOT2);
		
		OMSVGTextElement text = doc.createSVGTextElement(0f, 16f, OMSVGLength.SVG_LENGTHTYPE_PX, Integer.toString(pIndex));
		text.setClassNameBaseVal(STYLE_DOT3);
		
		g1.appendChild(g2);
		g2.appendChild(circle1);
		g2.appendChild(circle2);
		g2.appendChild(text);
		
		g1.addMouseDownHandler(this);
		g1.addMouseMoveHandler(this);
		g1.addMouseUpHandler(this);
		g1.addMouseOverHandler(this);
		g1.addMouseOutHandler(this);
		
		OMSVGPoint p = dotSvg.createSVGPoint(x, y);
		points.appendItem(p);

		return g1;
	}
	
	private void updateScales(float sx, float sy) {
		for (OMSVGGElement g1 : dots) {
			OMSVGGElement g2 = (OMSVGGElement)g1.getFirstChild();
			OMSVGTransform scaling = g2.getTransform().getBaseVal().getItem(0);
			scaling.setScale(sx, sy);
		}
		polyline.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, Float.toString(sx));
	}
	
	private void setPictureAlpha(float value) {
		pictureAlpha1.setValue(value);
	}

	private void renumber() {
		for (int i = 0, size = dots.size(); i < size; i++) {
			OMText data = (OMText)dots.get(i).getFirstChild().getLastChild().getFirstChild();
			data.setData(Integer.toString(i + 1));
			dotList.setItemText(i, toDotName(i));
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		mouseDownPoint = getLocalCoordinates(event);
		currentDot = (OMSVGGElement) event.getSource();
		currentDotIndex = dots.indexOf(currentDot);
		if (mode == Mode.DESIGN) {
			OMSVGMatrix m = currentDot.getTransform().getBaseVal().getItem(0).getMatrix();
			p0 = dotSvg.createSVGPoint(m.getE(), m.getF());
			DOMHelper.setCaptureElement(currentDot, this);
			event.stopPropagation();
		} else {
			if (currentDotIndex == maxIndex + 1) {
				maxIndex++;
				setDotClassName(currentDot, STYLE_DOT1V);
				OMSVGMatrix m = currentDot.getTransform().getBaseVal().getItem(0).getMatrix();
				points.appendItem(dotSvg.createSVGPoint(m.getE(), m.getF()));
				if (maxIndex + 1 == dots.size()) {
					// Level is succcessfully completed
					readPicture(false);
				}
			}
			currentDot = null;
		}
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (currentDot != null) {
			OMSVGPoint p1 = getLocalCoordinates(event).substract(mouseDownPoint).add(p0);
			// Update the dot position
			OMSVGMatrix m = currentDot.getTransform().getBaseVal().getItem(0).getMatrix();
			m.setE(p1.getX());
			m.setF(p1.getY());
			// Update the polyline
			p1.assignTo(points.getItem(currentDotIndex));
		}
		event.stopPropagation();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (currentDot != null) {
			DOMHelper.releaseCaptureElement();
			currentDot = null;
			currentDotIndex = -1;
		}
		event.stopPropagation();
	}
	
	@Override
	public void onLoseCapture(LoseCaptureEvent event) {
		if (currentDot != null) {
			DOMHelper.releaseCaptureElement();
			currentDot = null;
			currentDotIndex = -1;
		}
		event.stopPropagation();
	}

	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		OMSVGGElement dot = (OMSVGGElement)event.getSource();
		if (mode == Mode.DESIGN || !STYLE_DOT1V.equals(getDotClassName(dot))) {
			setDotClassName(dot, STYLE_DOT1);
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		OMSVGGElement dot = (OMSVGGElement)event.getSource();
		if (mode == Mode.DESIGN || !STYLE_DOT1V.equals(getDotClassName(dot))) {
			setDotClassName((OMSVGGElement)event.getSource(), STYLE_DOT1S);
		}
	}
	
	private void setDotClassName(OMSVGGElement dot, String className) {
		OMSVGCircleElement circle1 = (OMSVGCircleElement) dot.getFirstChild().getFirstChild();
		circle1.setClassNameBaseVal(className);
		OMSVGCircleElement circle2 = (OMSVGCircleElement) circle1.getNextSibling();
		circle2.setClassNameBaseVal(className.replace('1', '2'));
	}
	private String getDotClassName(OMSVGGElement dot) {
		OMSVGCircleElement circle1 = (OMSVGCircleElement) dot.getFirstChild().getFirstChild();
		return circle1.getClassName().getBaseVal();
	}
	
	private static String toDotName(int pIndex) {
		return DotsConstants.INSTANCE.dot() + " #" + (pIndex + 1);
	}
	
	@UiFactory
	PushButton createPushButton(ImageResource image) {
		return new PushButton(new Image(image));
	}

}
