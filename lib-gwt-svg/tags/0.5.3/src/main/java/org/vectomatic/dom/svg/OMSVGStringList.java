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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * <p>This interface defines a list of DOMString values.</p> <p>{@link org.vectomatic.dom.svg.OMSVGStringList}
 * has the same attributes and methods as other SVGxxxList interfaces. Implementers
 * may consider using a single base class to implement the various SVGxxxList
 * interfaces.</p>
 */
public class OMSVGStringList implements Iterable<String> {
  private JavaScriptObject ot;
  protected OMSVGStringList(JavaScriptObject ot) {
    this.ot = ot;
  }

  // Implementation of the svg::SVGStringList W3C IDL interface
  /**
   * The number of items in the list.
   */
  public final native int getNumberOfItems() /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.numberOfItems;
  }-*/;
  /**
   * Clears all existing current items from the list, with the result being
   * an empty list.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   */
  public final native void clear() throws JavaScriptException /*-{
    this.@org.vectomatic.dom.svg.OMSVGStringList::ot.clear();
  }-*/;
  /**
   * Clears all existing current items from the list and re-initializes the
   * list to hold the single item specified by the parameter.
   * @param newItem The item which should become the only member of the list.
   * @return The item being inserted into the list.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   */
  public final native String initialize(String newItem) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.initialize(newItem);
  }-*/;
  /**
   * Returns the specified item from the list.
   * @param index The index of the item from the list which is to be   returned.
   * The first item is number 0.
   * @return The selected item.
   * @throws DOMException(INDEX_SIZE_ERR) Raised if the index number is   greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGStringList#getNumberOfItems()}.
   */
  public final native String getItem(int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.getItem(index);
  }-*/;
  /**
   * Inserts a new item into the list at the specified position. The first item
   * is number 0.
   * @param newItem The item which is to be inserted into the list.
   * @param index The index of the item before which the new item is to be 
   * inserted. The first item is number 0.  If the index is equal to 0,   then
   * the new item is inserted at the front of the list. If the index   is greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGStringList#getNumberOfItems()},
   * then the new item is   appended to the end of the list.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   */
  public final native String insertItemBefore(String newItem, int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.insertItemBefore(newItem, index);
  }-*/;
  /**
   * Replaces an existing item in the list with a new item.
   * @param newItem The item which is to be inserted into the list.
   * @param index The index of the item which is to be replaced. The first 
   * item is number 0.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   * @throws DOMException(INDEX_SIZE_ERR) Raised if the index number is   greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGStringList#getNumberOfItems()}.
   */
  public final native String replaceItem(String newItem, int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.replaceItem(newItem, index);
  }-*/;
  /**
   * Removes an existing item from the list.
   * @param index The index of the item which is to be removed. The first  
   * item is number 0.
   * @return The removed item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   * @throws DOMException(INDEX_SIZE_ERR) Raised if the index number is   greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGStringList#getNumberOfItems()}.
   */
  public final native String removeItem(int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.removeItem(index);
  }-*/;
  /**
   * Inserts a new item at the end of the list.
   * @param newItem The item which is to be inserted. The first item is   number
   * 0.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * cannot be modified.
   */
  public final native String appendItem(String newItem) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGStringList::ot.appendItem(newItem);
  }-*/;

  /**
   * Returns an iterator over the {@link java.lang.String}
   * elements in this list in proper sequence.
   *
   * <p>This implementation returns a straightforward implementation of the
   * iterator interface, relying on the backing list's {@code getNumberOfItems()},
   * and {@code getItem(int)} methods.
   *
   * <p>Note that the iterator returned by this method will throw an
   * {@code UnsupportedOperationException} in response to its
   * {@code remove} method.
   *
   * @return an iterator over the {@link java.lang.String}
   * elements in this list in proper sequence
   */
  @Override
  public final Iterator<String> iterator() {
	return new Iterator<String>() {
		private int index;
		@Override
		public boolean hasNext() {
			return index < getNumberOfItems();
		}

		@Override
		public String next() {
			return getItem(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
  }

}
