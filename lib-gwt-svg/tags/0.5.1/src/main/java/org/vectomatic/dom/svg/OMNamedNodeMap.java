/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */
package org.vectomatic.dom.svg;

import java.util.Iterator;

import org.vectomatic.dom.svg.impl.NamedNodeMap;
import org.w3c.dom.DOMException;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Node;

/**
 * Wrapper class for DOM NamedNodeMap
 * @author laaglu
 */
public class OMNamedNodeMap<T extends OMNode> implements Iterable<T> {
	private NamedNodeMap<? extends Node> ot;
	/**
	 * Constructor
	 * @param map The wrapped map
	 */
	protected OMNamedNodeMap(NamedNodeMap<? extends Node> map) {
		this.ot = map;
	}
	
	/**
	 * Returns the wrapped {@org.vectomatic.dom.svg.impl.NamedNodeMap}
	 * @return the wrapped {@org.vectomatic.dom.svg.impl.NamedNodeMap}
	 */
	public NamedNodeMap<? extends Node> getNamedNodeMap() {
		return ot;
	}
	
    /**
     * Retrieves a node specified by name.
     * @param name The <code>nodeName</code> of a node to retrieve.
     * @return A <code>Node</code> (of any type) with the specified 
     *   <code>nodeName</code>, or <code>null</code> if it does not identify 
     *   any node in this map.
     */
	public final T getNamedItem(String name) {
		Node node = ot.getNamedItem(name);
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * Adds a node using its <code>nodeName</code> attribute. If a node with 
     * that name is already present in this map, it is replaced by the new 
     * one. Replacing a node by itself has no effect.
     * <br>As the <code>nodeName</code> attribute is used to derive the name 
     * which the node must be stored under, multiple nodes of certain types 
     * (those that have a "special" string value) cannot be stored as the 
     * names would clash. This is seen as preferable to allowing nodes to be 
     * aliased.
     * @param arg A node to store in this map. The node will later be 
     *   accessible using the value of its <code>nodeName</code> attribute.
     * @return If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created this map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     *   <br>HIERARCHY_REQUEST_ERR: Raised if an attempt is made to add a node 
     *   doesn't belong in this NamedNodeMap. Examples would include trying 
     *   to insert something other than an Attr node into an Element's map 
     *   of attributes, or a non-Entity node into the DocumentType's map of 
     *   Entities.
     */
	public final T setNamedItem(T arg) throws JavaScriptException {
		Node node = ot.setNamedItem(arg.getNode());
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * Removes a node specified by name. When this map contains the attributes 
     * attached to an element, if the removed attribute is known to have a 
     * default value, an attribute immediately appears containing the 
     * default value as well as the corresponding namespace URI, local name, 
     * and prefix when applicable.
     * @param name The <code>nodeName</code> of the node to remove.
     * @return The node removed from this map if a node with such a name 
     *   exists.
     * @exception DOMException
     *   NOT_FOUND_ERR: Raised if there is no node named <code>name</code> in 
     *   this map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     */
	public final T removeNamedItem(String name) throws JavaScriptException {
		Node node = ot.removeNamedItem(name);
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * Returns the <code>index</code>th item in the map. If <code>index</code> 
     * is greater than or equal to the number of nodes in this map, this 
     * returns <code>null</code>.
     * @param index Index into this map.
     * @return The node at the <code>index</code>th position in the map, or 
     *   <code>null</code> if that is not a valid index.
     */
	public final T item(int index) {
		Node node = ot.item(index);
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * The number of nodes in this map. The range of valid child node indices 
     * is <code>0</code> to <code>length-1</code> inclusive.
     * @return The number of nodes in this map
     */
	public final int getLength() {
		return ot.getLength();
	}

    /**
     * Retrieves a node specified by local name and namespace URI.
     * <br>Per [<a href='http://www.w3.org/TR/1999/REC-xml-names-19990114/'>XML Namespaces</a>]
     * , applications must use the value null as the namespaceURI parameter 
     * for methods if they wish to have no namespace.
     * @param namespaceURI The namespace URI of the node to retrieve.
     * @param localName The local name of the node to retrieve.
     * @return A <code>Node</code> (of any type) with the specified local 
     *   name and namespace URI, or <code>null</code> if they do not 
     *   identify any node in this map.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: May be raised if the implementation does not 
     *   support the feature "XML" and the language exposed through the 
     *   Document does not support XML Namespaces (such as [<a href='http://www.w3.org/TR/1999/REC-html401-19991224/'>HTML 4.01</a>]). 
     */
	public final T getNamedItemNS(String namespaceURI, String localName) throws JavaScriptException {
		Node node = ot.getNamedItemNS(namespaceURI, localName);
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * Adds a node using its <code>namespaceURI</code> and 
     * <code>localName</code>. If a node with that namespace URI and that 
     * local name is already present in this map, it is replaced by the new 
     * one. Replacing a node by itself has no effect.
     * <br>Per [<a href='http://www.w3.org/TR/1999/REC-xml-names-19990114/'>XML Namespaces</a>]
     * , applications must use the value null as the namespaceURI parameter 
     * for methods if they wish to have no namespace.
     * @param arg A node to store in this map. The node will later be 
     *   accessible using the value of its <code>namespaceURI</code> and 
     *   <code>localName</code> attributes.
     * @return If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created this map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     *   <br>HIERARCHY_REQUEST_ERR: Raised if an attempt is made to add a node 
     *   doesn't belong in this NamedNodeMap. Examples would include trying 
     *   to insert something other than an Attr node into an Element's map 
     *   of attributes, or a non-Entity node into the DocumentType's map of 
     *   Entities.
     *   <br>NOT_SUPPORTED_ERR: May be raised if the implementation does not 
     *   support the feature "XML" and the language exposed through the 
     *   Document does not support XML Namespaces (such as [<a href='http://www.w3.org/TR/1999/REC-html401-19991224/'>HTML 4.01</a>]). 
     */
	public final T setNamedItemNS(T arg) throws JavaScriptException {
		Node node = ot.setNamedItemNS(arg.getNode());
		return (node != null) ? OMNode.<T>convert(node) : null;
	}

    /**
     * Removes a node specified by local name and namespace URI. A removed 
     * attribute may be known to have a default value when this map contains 
     * the attributes attached to an element, as returned by the attributes 
     * attribute of the <code>Node</code> interface. If so, an attribute 
     * immediately appears containing the default value as well as the 
     * corresponding namespace URI, local name, and prefix when applicable.
     * <br>Per [<a href='http://www.w3.org/TR/1999/REC-xml-names-19990114/'>XML Namespaces</a>]
     * , applications must use the value null as the namespaceURI parameter 
     * for methods if they wish to have no namespace.
     * @param namespaceURI The namespace URI of the node to remove.
     * @param localName The local name of the node to remove.
     * @return The node removed from this map if a node with such a local 
     *   name and namespace URI exists.
     * @exception DOMException
     *   NOT_FOUND_ERR: Raised if there is no node with the specified 
     *   <code>namespaceURI</code> and <code>localName</code> in this map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     *   <br>NOT_SUPPORTED_ERR: May be raised if the implementation does not 
     *   support the feature "XML" and the language exposed through the 
     *   Document does not support XML Namespaces (such as [<a href='http://www.w3.org/TR/1999/REC-html401-19991224/'>HTML 4.01</a>]). 
     */
	public final T removeNamedItemNS(String namespaceURI, String localName) throws JavaScriptException {
		Node node = ot.removeNamedItemNS(namespaceURI, localName);
		return (node != null) ? OMNode.<T>convert(node) : null;
	}
	
	/**
	 * Returns an iterator over the {@link org.vectomatic.dom.svg.OMNode}
	 * elements in this map in proper sequence.
	 *
	 * <p>This implementation returns a straightforward implementation of the
	 * iterator interface, relying on the backing map's {@code getNumberOfItems()},
	 * and {@code getItem(int)} methods.
	 *
	 * <p>Note that the iterator returned by this method will throw an
	 * {@code UnsupportedOperationException} in response to its
	 * {@code remove} method.
	 *
	 * @return an iterator over the {@link org.vectomatic.dom.svg.OMNode}
	 * elements in this map in proper sequence
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int index;

			@Override
			public boolean hasNext() {
				return index < getLength();
			}

			@Override
			public T next() {
				return item(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
}
