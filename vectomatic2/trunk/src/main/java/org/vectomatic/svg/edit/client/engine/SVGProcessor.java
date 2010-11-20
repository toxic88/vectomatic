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

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.impl.Attr;
import org.vectomatic.dom.svg.impl.NamedNodeMap;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Class to normalize id and idrefs in an SVG document
 * @author laaglu
 */
public class SVGProcessor {
	static int docId;
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			IdRefTokenizer tokenizer = new IdRefTokenizer();
			StringBuilder builder = new StringBuilder();
			tokenizer.tokenize(args[i]);
			IdRefTokenizer.IdRefToken token;
			while ((token = tokenizer.nextToken()) != null) {
				String txt = (token.getKind() == IdRefTokenizer.IdRefToken.DATA) ? token.getValue() : ("{" + token.getValue() + "}");
				builder.append(txt);
			}
			System.out.println("\"" + args[i] + "\" ==> \"" + builder.toString() + "\"");
		}
	}
	
	public static int normalizeIds(OMSVGElement srcSvg) {
		docId++;
		// Collect all the original element ids and replace them with a
		// normalized id
		int idIndex = 0;
		Map<String, Element> idToElement = new HashMap<String, Element>();
		Map<String, String> idToNormalizedId = new HashMap<String, String>();
		List<Element> queue = new ArrayList<Element>();
		queue.add(srcSvg.getElement());
		while (queue.size() > 0) {
			Element element = queue.remove(0);
			String id = element.getId();
			if (id != null) {
				idToElement.put(id, element);
				String normalizedId = "d" + docId + "_" + idIndex++;
				idToNormalizedId.put(id, normalizedId);
				element.setId(normalizedId);
			}
			NodeList<Node> childNodes =  element.getChildNodes();
			for (int i = 0, length = childNodes.getLength(); i < length; i++) {
				Node childNode = childNodes.getItem(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					queue.add((Element)childNode.cast());
				}
			}
		}
		
		// Change all the attributes which are URI references
		Set<String> attNames = new HashSet<String>(Arrays.asList(
			new String[] { "clip-path",
			 "mask",
			 "marker-start",
			 "marker-mid",
			 "marker-end",
			 "fill",
			 "stroke",
			 "filter",
			 "cursor",
			 "style"}));
		queue.add(srcSvg.getElement());
		IdRefTokenizer tokenizer = GWT.create(IdRefTokenizer.class);
		while (queue.size() > 0) {
			Element element = queue.remove(0);
			if (DOMHelper.hasAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE)) {
				String idRef = DOMHelper.getAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE).substring(1);
				String normalizeIdRef = idToNormalizedId.get(idRef);
				DOMHelper.setAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE, "#" + normalizeIdRef);
			}
			NamedNodeMap<Attr> attrs = DOMHelper.getAttributes(element);
			for (int i = 0, length = attrs.getLength(); i < length; i++) {
				Attr attr = attrs.item(i);
				if (attNames.contains(attr.getName())) {
					StringBuilder builder = new StringBuilder();
					tokenizer.tokenize(attr.getValue());
					IdRefTokenizer.IdRefToken token;
					while ((token = tokenizer.nextToken()) != null) {
						String value = token.getValue();
						if (token.getKind() == IdRefTokenizer.IdRefToken.DATA) {
							builder.append(value);
						} else {
							value = idToNormalizedId.get(value);
							builder.append(value == null ? token.getValue() : value);
						}
					}
					attr.setValue(builder.toString());
				}
			}
			NodeList<Node> childNodes =  element.getChildNodes();
			for (int i = 0, length = childNodes.getLength(); i < length; i++) {
				Node childNode = childNodes.getItem(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					queue.add((Element)childNode.cast());
				}
			}
		}
		return docId;
	}
}

