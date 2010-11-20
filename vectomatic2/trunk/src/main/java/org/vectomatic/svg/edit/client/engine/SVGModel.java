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
package org.vectomatic.svg.edit.client.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.impl.SVGDescElement;
import org.vectomatic.dom.svg.impl.SVGDocument;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGTitleElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * Model class for an SVG document edited by the application.
 * The document has the following structure:
 * <dl>
 * <dt>displayGroup</dt><dd>Contains all the elements from the original SVG, id-normalized.</dd>
 * <dt>selectionGroup</dt><dd>Contains a visibility-hidden clone of the previous group. It is used to display the selection.</dd>
 * </dl>
 * @author laaglu
 */
public class SVGModel {
	/**
	 * The root of the SVG document
	 */
	protected OMSVGSVGElement svg;
	/**
	 * A group containing all the elements from the original SVG, id-normalized. 
	 * Is also servers to apply a visualization transform change the display
	 * of the document
	 */
	protected OMSVGGElement displayGroup;
	/**
	 * A group to represent the selection
	 */
	protected OMSVGGElement selectionGroup;
	/**
	 * The visualization transform of the display group
	 */
	protected OMSVGTransform displayXform;
	/**
	 * The visualization transform of the selection group
	 * (same value as displayXform).
	 */
	protected OMSVGTransform selectionXform;
	/**
	 * The current scaling the displayGroup
	 */
	protected float angle;
	/**
	 * The current rotation of the displayGroup
	 */
	protected float scale;
	/**
	 * True if selection mode is activated, false otherwise
	 */
	protected boolean selectionMode;
	/**
	 * The highlighted element in the displayGroup
	 * in any
	 */
	protected SVGElement highlightedElement;
	/**
	 * The selected element in the selectionGroup
	 */
	protected SVGElement selectionElement;
	/**
	 * A map used to generate node names for nodes
	 * which do not have a title element.
	 */
	protected Map<String, Integer> tagNameToTagCount;
	/**
	 * Tag names of definition elements which contain graphical
	 * elements but are not displayed directly
	 */
	protected static Set<String> definitionElementNames;
	/**
	 * Tag names of elements which can nest desc, title and metadata elements
	 */
	protected static Set<String> descTitleMetadataElementNames;
	/**
	 * Tag names of graphical elements
	 */
	protected static Set<String> graphicalElementNames;
	
	public static SVGTreeNode.XPathProperty<String> TITLE_PROPERTY;
	public static SVGTreeNode.XPathProperty<String> DESC_PROPERTY;
	public static SVGTreeNode.MappedProperty<Boolean, String> VISIBILITY_PROPERTY;

	static {
		descTitleMetadataElementNames = new HashSet<String>(Arrays.asList(new String[] {
			SVGConstants.SVG_USE_TAG,
			SVGConstants.SVG_IMAGE_TAG,
			SVGConstants.SVG_SWITCH_TAG,
			SVGConstants.SVG_PATH_TAG,
			SVGConstants.SVG_RECT_TAG,
			SVGConstants.SVG_CIRCLE_TAG,
			SVGConstants.SVG_ELLIPSE_TAG,
			SVGConstants.SVG_LINE_TAG,
			SVGConstants.SVG_POLYLINE_TAG,
			SVGConstants.SVG_POLYGON_TAG,
			SVGConstants.SVG_COLOR_PROFILE_TAG,
			SVGConstants.SVG_LINEAR_GRADIENT_TAG,
			SVGConstants.SVG_RADIAL_GRADIENT_TAG,
			SVGConstants.SVG_CLIP_PATH_TAG,
			SVGConstants.SVG_FILTER_TAG,
			SVGConstants.SVG_CURSOR_TAG,
			SVGConstants.SVG_VIEW_TAG,
			SVGConstants.SVG_ANIMATE_TAG,
			SVGConstants.SVG_SET_TAG,
			SVGConstants.SVG_ANIMATE_MOTION_TAG,
			SVGConstants.SVG_M_PATH_TAG,
			SVGConstants.SVG_ANIMATE_COLOR_TAG,
			SVGConstants.SVG_ANIMATE_TRANSFORM_TAG,
			SVGConstants.SVG_FONT_TAG,
			SVGConstants.SVG_FONT_FACE_TAG,
			SVGConstants.SVG_SVG_TAG,
			SVGConstants.SVG_G_TAG,
			SVGConstants.SVG_DEFS_TAG,
			SVGConstants.SVG_SYMBOL_TAG,
			SVGConstants.SVG_TEXT_TAG,
			SVGConstants.SVG_T_SPAN_TAG,
			SVGConstants.SVG_T_REF_TAG,
			SVGConstants.SVG_TEXT_PATH_TAG,
			SVGConstants.SVG_MARKER_TAG,
			SVGConstants.SVG_PATTERN_TAG,
			SVGConstants.SVG_MASK_TAG,
			SVGConstants.SVG_A_TAG,
			SVGConstants.SVG_GLYPH_TAG,
			SVGConstants.SVG_MISSING_GLYPH_TAG
		}));
		definitionElementNames = new HashSet<String>(Arrays.asList(new String[] {
			SVGConstants.SVG_SYMBOL_TAG,
			SVGConstants.SVG_DEFS_TAG,
			SVGConstants.SVG_PATTERN_TAG,
			SVGConstants.SVG_MARKER_TAG,
			SVGConstants.SVG_CLIP_PATH_TAG,
			SVGConstants.SVG_MASK_TAG,
			SVGConstants.SVG_GLYPH_TAG,
			SVGConstants.SVG_MISSING_GLYPH_TAG
		}));
		graphicalElementNames = new HashSet<String>(Arrays.asList(new String[] {
				SVGConstants.SVG_CIRCLE_TAG,
				SVGConstants.SVG_ELLIPSE_TAG,
				SVGConstants.SVG_IMAGE_TAG,
				SVGConstants.SVG_LINE_TAG,
				SVGConstants.SVG_PATH_TAG,
				SVGConstants.SVG_POLYLINE_TAG,
				SVGConstants.SVG_POLYGON_TAG,
				SVGConstants.SVG_RECT_TAG,
				SVGConstants.SVG_TEXT_TAG,
				SVGConstants.SVG_T_REF_TAG,
				SVGConstants.SVG_T_SPAN_TAG,
				SVGConstants.SVG_USE_TAG
		}));
		
		TITLE_PROPERTY = new SVGTreeNode.XPathProperty<String>("./svg:title/text()");
	    DESC_PROPERTY = new SVGTreeNode.XPathProperty<String>("./svg:desc/text()");
	    SVGTreeNode.CssProperty<String> cssVisibility = new SVGTreeNode.CssProperty<String>("visibility");
	    Map<Boolean, String> booleanToVisibility = new HashMap<Boolean, String>();
	    booleanToVisibility.put(Boolean.TRUE, SVGConstants.CSS_VISIBLE_VALUE);
	    booleanToVisibility.put(Boolean.FALSE, SVGConstants.CSS_HIDDEN_VALUE);
	    Map<String, Boolean> visibilityToBoolean = new HashMap<String, Boolean>();
	    visibilityToBoolean.put(SVGConstants.CSS_VISIBLE_VALUE, Boolean.TRUE);
	    visibilityToBoolean.put(null, Boolean.TRUE);
	    visibilityToBoolean.put("", Boolean.TRUE);
	    visibilityToBoolean.put(SVGConstants.CSS_HIDDEN_VALUE, Boolean.FALSE);
	    VISIBILITY_PROPERTY = new SVGTreeNode.MappedProperty<Boolean, String>(cssVisibility, booleanToVisibility, visibilityToBoolean);
	}

	/**
	 * Binds this SVG model to the specified SVG 'svg' element
	 * @param svg an SVG 'svg' element
	 */
	public void setSvgElement(OMSVGSVGElement svg) {
		this.svg = svg;

		tagNameToTagCount = new HashMap<String, Integer>();
		
	    SVGProcessor.normalizeIds(svg);

	    // Tweak the image to insert a group immediately below
	    // the root. This group will be used to control the viewing
	    // transform
	    displayGroup = reparent(svg);
	    createTitleDesc((SVGElement)displayGroup.getElement().cast());
		OMSVGTransformList xformList = displayGroup.getTransform().getBaseVal();
		displayXform = svg.createSVGTransform();
		xformList.appendItem(displayXform);
	    setScale(1f);
	    displayGroup.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (selectionMode) {
					highlightElement((SVGElement)event.getNativeEvent().getEventTarget().cast());
				}
				event.preventDefault();
				event.stopPropagation();
			}
	    });
	    
	    selectionGroup = (OMSVGGElement) displayGroup.cloneNode(true);
	    SVGProcessor.normalizeIds(selectionGroup);
	    selectionGroup.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_HIDDEN_VALUE);
	    svg.appendChild(selectionGroup);
	    xformList = selectionGroup.getTransform().getBaseVal();
	    if (xformList.getNumberOfItems() == 1) {
	    	// Normal behavior
	    	selectionXform = selectionGroup.getTransform().getBaseVal().getItem(0);
	    } else {
	    	// Webkit bug
		    selectionXform = svg.createSVGTransform();
			xformList.appendItem(selectionXform);
	    }
	}
	
	/**
	 * Removes all the child nodes of the svg document and
	 * puts them in the displayGroup
	 * @param svg the svg document root
	 * @return the displayGroup
	 */
	protected OMSVGGElement reparent(OMSVGSVGElement svg) {
		OMSVGGElement g = OMSVGParser.currentDocument().createSVGGElement();
		Element gElement = g.getElement();
		Element svgElement = svg.getElement();
		Node node;
		while((node = svgElement.getFirstChild()) != null) {
			gElement.appendChild(svgElement.removeChild(node));
		}
		svgElement.appendChild(gElement);
		return g;
	}


	/**
	 * Returns the root of the SVG document
	 * @return the root of the SVG document
	 */
	public OMSVGSVGElement getDocumentRoot() {
		return svg;
	}
	
	/**
	 * Returns the scaling of the SVG.
	 * @return The scale (1 means scale 1:1, 2 means scale 2:1)
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the scaling of the SVG.
	 * @param scale
	 * The scale (1 means scale 1:1, 2 means scale 2:1)
	 */
	public void setScale(float scale) {
		this.scale = scale;
		OMSVGRect rect = svg.getViewBox().getBaseVal();
        svg.getStyle().setWidth(rect.getWidth() * scale, Unit.PX);
        svg.getStyle().setHeight(rect.getHeight() * scale, Unit.PX);
	}
	
	/**
	 * Sets the rotation of the SVG.
	 * @param angle
	 * The angle (in degrees)
	 */
	public void setRotation(float angle) {
		this.angle = angle;
		OMSVGRect rect = svg.getViewBox().getBaseVal();
		OMSVGPoint center = svg.createSVGPoint(rect.getCenterX(), rect.getCenterY());
		displayXform.setRotate(angle, rect.getCenterX(), rect.getCenterY());
		selectionXform.setRotate(angle, rect.getCenterX(), rect.getCenterY());
	}
	
	public OMSVGGElement getDisplayGroup() {
		return displayGroup;
	}
	
	public void onAttach() {
		OMSVGRect viewBox = svg.getViewBox().getBaseVal();
		// Create a viewbox for model which do not define one.
		// The viewbox is created to be 10% larger that the bbox
		if (viewBox.getWidth() == 0f || viewBox.getHeight() == 0f) {
    		GWT.log(svg.getBBox().getDescription());
			OMSVGRect bbox = svg.getBBox().inset(svg.createSVGRect(), -0.1f * svg.getBBox().getWidth(), -0.1f * svg.getBBox().getHeight());
			viewBox.setWidth(bbox.getWidth());
			viewBox.setHeight(bbox.getHeight());
			setScale(scale);
		}
	}
	
	public void setSelectionMode(boolean selectionMode) {
		this.selectionMode = selectionMode;
		float opacity = this.selectionMode ? 0.25f : 1f;
		displayGroup.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, Float.toString(opacity));
	}
	
	public void highlightElement(SVGElement element) {
		if (selectionMode) {
			if (highlightedElement != element) {
				// Unhighlight previously highlighted element
				if (selectionElement != null) {
					OMSVGStyle style = selectionElement.getStyle().cast();
					style.setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_HIDDEN_VALUE);
				}
				
				// Retrieve the xpath of the element to highlight.
				highlightedElement = element;
				if (highlightedElement != null) {
					String xpath = DOMHelper.getXPath(highlightedElement, displayGroup.getElement());
	//				GWT.log("xpath=" + xpath);
					
					// Make the corresponding element in the selectGroup visible.
					OMSVGElement selectGroupElement = (OMSVGElement) DOMHelper.evaluateXPath(selectionGroup, "." + xpath, new SVGPrefixResolver()).next();
	//				GWT.log("selectGroupElement=" + selectGroupElement.getMarkup() + " mouseOverElement=" + element.getMarkup());
					selectionElement = selectGroupElement.getElement().cast();
					OMSVGStyle style  = selectionElement.getStyle().cast();
					style.setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
					style.setSVGProperty(SVGConstants.CSS_POINTER_EVENTS_PROPERTY, SVGConstants.CSS_NONE_VALUE);
				}
			}
		}

	}
	
	public SVGIconTreeNode getNavigationTree() {
		return buildTreeNode(
				(SVGElement)displayGroup.getElement().cast(), 
				Arrays.asList(new SVGTreeNode.IProperty[]{TITLE_PROPERTY, DESC_PROPERTY, VISIBILITY_PROPERTY}));
	}
	
	private SVGIconTreeNode buildTreeNode(SVGElement element, List<SVGTreeNode.IProperty> properties) {
		SVGIconTreeNode node = new SVGIconTreeNode(element, properties);
		NodeList<Node> childNodes = element.getChildNodes();
		for (int i = 0, length = childNodes.getLength(); i < length; i++) {
			Node child = childNodes.getItem(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = child.cast();
				if (SVGConstants.SVG_NAMESPACE_URI.equals(DOMHelper.getNamespaceURI(childElement))
				 && hasGraphicalElements((SVGElement)childElement.cast())) {
					node.add(buildTreeNode((SVGElement)childElement.cast(), properties));
				}
			}
		}
		return node;
	}
	
	public String generateName(SVGElement element) {
		String localName = DOMHelper.getLocalName(element);
		Integer count = tagNameToTagCount.get(localName);
		if (count == null) {
			count = 0;
		}
		tagNameToTagCount.put(localName, count + 1);
		return localName + (count + 1);
	}
	
	/**
	 * Returns true if the subtree rooted at the specified element
	 * contains at least one graphical element which is not
	 * part of a definition element
	 * @param element
	 * @return
	 */
	public static boolean hasGraphicalElements(SVGElement element) {
		if (graphicalElementNames.contains(DOMHelper.getLocalName(element))) {
			return true;
		}
		NodeList<Node> childNodes = element.getChildNodes();
		for (int i = 0, length = childNodes.getLength(); i < length; i++) {
			Node child = childNodes.getItem(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (SVGConstants.SVG_NAMESPACE_URI.equals(DOMHelper.getNamespaceURI((Element)child.cast()))
				 && !definitionElementNames.contains(DOMHelper.getLocalName(child))
				 && hasGraphicalElements((SVGElement)child.cast())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if the specified SVG element can nest a title and desc
	 * element as per the SVG 1.1 specification, false otherwise
	 * @param element An SVG element
	 * @return true if the specified SVG element can nest a title and desc
	 * element as per the SVG 1.1 specification, false otherwise
	 */
	public static boolean hasTitleDesc(SVGElement element) {
		return descTitleMetadataElementNames.contains(DOMHelper.getLocalName(element));
	}
	
	/**
	 * Completes the specified SVG subtree so that all the proper SVG elements have
	 * an associated title and desc elements
	 * @param root The SVG subtree to complete
	 */
	public void createTitleDesc(SVGElement root) {
		// Perform a DFS of the tree to collect all the SVG elements
		// which can have a title and desc but do not yet have one
		List<SVGElement> incompleteElements = new ArrayList<SVGElement>();
		Stack<SVGElement> stack = new Stack<SVGElement>();
		stack.push(root);
		while (stack.size() > 0) {
			SVGElement element = stack.pop();
			if (hasTitleDesc(element) 
			 && (DOMHelper.evaluateNodeXPath(element, TITLE_PROPERTY.getXPath(), SVGPrefixResolver.INSTANCE) == null
			 || DOMHelper.evaluateNodeXPath(element, DESC_PROPERTY.getXPath(), SVGPrefixResolver.INSTANCE) == null)) {
				incompleteElements.add(element);
			}
			NodeList<Node> childNodes = element.getChildNodes();
			for (int i = 0, length = childNodes.getLength(); i < length; i++) {
				Node child = childNodes.getItem(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = child.cast();
					if (SVGConstants.SVG_NAMESPACE_URI.equals(DOMHelper.getNamespaceURI(childElement))) {
						stack.push((SVGElement)childElement.cast());
					}
				}
			}
		}
		
		// Create the missing desc and titles
		SVGDocument document = root.getOwnerDocument().cast();
		for (SVGElement element : incompleteElements) {
			SVGTitleElement title = DOMHelper.evaluateNodeXPath(element, TITLE_PROPERTY.getXPath(), SVGPrefixResolver.INSTANCE);
			if (title == null) {
				title = DOMHelper.createElementNS(document, SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_TITLE_TAG).cast();
				title.appendChild(document.createTextNode(generateName(element)));
				Node firstChild = element.getFirstChild();
				if (firstChild == null) {
					element.appendChild(title);
				} else {
					element.insertBefore(title, firstChild);
				}
			}
			SVGDescElement desc = DOMHelper.evaluateNodeXPath(element, DESC_PROPERTY.getXPath(), SVGPrefixResolver.INSTANCE);
			if (desc == null) {
				desc = DOMHelper.createElementNS(document, SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_DESC_TAG).cast();
				desc.appendChild(document.createTextNode(""));
				element.insertAfter(desc, title);
			}
		}
	}	
}
