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
 * <p>This interface defines a list of SVGLength objects.</p> <p>{@link org.vectomatic.dom.svg.OMSVGLengthList}
 * has the same attributes and methods as other SVGxxxList interfaces. Implementers
 * may consider using a single base class to implement the various SVGxxxList
 * interfaces.</p> <p id="ReadOnlyLengthList">An {@link org.vectomatic.dom.svg.OMSVGLengthList}
 * object can be designated as <em>read only</em>, which means that attempts
 * to modify the object will result in an exception being thrown, as described
 * below.</p>
 */
public class OMSVGLengthList implements Iterable<OMSVGLength> {
  private JavaScriptObject ot;
  protected OMSVGLengthList(JavaScriptObject ot) {
    this.ot = ot;
  }

  // Implementation of the svg::SVGLengthList W3C IDL interface
  /**
   * The number of items in the list.
   */
  public final native int getNumberOfItems() /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.numberOfItems;
  }-*/;
  /**
   * Clears all existing current items from the list, with the result being
   * an empty list.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   */
  public final native void clear() throws JavaScriptException /*-{
    this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.clear();
  }-*/;
  /**
   * Clears all existing current items from the list and re-initializes the
   * list to hold the single item specified by the parameter.  If the inserted
   * item is already in a list, it is removed from its previous list before
   * it is inserted into this list.  The inserted item is the item itself and
   * not a copy.
   * @param newItem The item which should become the only member of the list.
   * @return The item being inserted into the list.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   */
  public final native OMSVGLength initialize(OMSVGLength newItem) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.initialize(newItem);
  }-*/;
  /**
   * Returns the specified item from the list.  The returned item is the item
   * itself and not a copy.  Any changes made to the item are immediately reflected
   * in the list.
   * @param index The index of the item from the list which is to be   returned.
   * The first item is number 0.
   * @return The selected item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   */
  public final native OMSVGLength getItem(int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.getItem(index);
  }-*/;
  /**
   * Inserts a new item into the list at the specified position. The first item
   * is number 0. If <var>newItem</var> is already in a list, it is removed
   * from its previous list before it is inserted into this list. The inserted
   * item is the item itself and not a copy. If the item is already in this
   * list, note that the index of the item to insert before is <i>before</i>
   * the removal of the item.
   * @param newItem The item which is to be inserted into the list.
   * @param index The index of the item before which the new item is to be 
   * inserted. The first item is number 0.  If the index is equal to 0,   then
   * the new item is inserted at the front of the list. If the index   is greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGLengthList#getNumberOfItems()},
   * then the new item is   appended to the end of the list.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   */
  public final native OMSVGLength insertItemBefore(OMSVGLength newItem, int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.insertItemBefore(newItem, index);
  }-*/;
  /**
   * Replaces an existing item in the list with a new item. If <var>newItem</var>
   * is already in a list, it is removed from its previous list before it is
   * inserted into this list.  The inserted item is the item itself and not
   * a copy.  If the item is already in this list, note that the index of the
   * item to replace is <i>before</i> the removal of the item.
   * @param newItem The item which is to be inserted into the list.
   * @param index The index of the item which is to be replaced. The first 
   * item is number 0.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   * @throws DOMException(INDEX_SIZE_ERR) Raised if the index number is   greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGLengthList#getNumberOfItems()}.
   */
  public final native OMSVGLength replaceItem(OMSVGLength newItem, int index) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.replaceItem(newItem, index);
  }-*/;
  /**
   * Removes an existing item from the list.
   * @param index The index of the item which is to be removed. The first  
   * item is number 0.
   * @return The removed item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   * @throws DOMException(INDEX_SIZE_ERR) Raised if the index number is   greater
   * than or equal to {@link org.vectomatic.dom.svg.OMSVGLengthList#getNumberOfItems()}.
   */
  public final native OMSVGLength removeItem(int index) /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.removeItem(index);
  }-*/;
  /**
   * Inserts a new item at the end of the list. If <var>newItem</var> is already
   * in a list, it is removed from its previous list before it is inserted into
   * this list.  The inserted item is the item itself and not a copy.
   * @param newItem The item which is to be inserted. The first item is   number
   * 0.
   * @return The inserted item.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised when the list
   * corresponds to a <a href="svgdom.html#ReadOnlyNodes">read only attribute</a>
   * or when the object itself is   <a href="#ReadOnlyLengthList">read only</a>.
   */
   public final native OMSVGLength appendItem(OMSVGLength newItem) throws JavaScriptException /*-{
    return this.@org.vectomatic.dom.svg.OMSVGLengthList::ot.appendItem(newItem);
  }-*/;

  /**
   * Returns an iterator over the {@link org.vectomatic.dom.svg.OMSVGLength}
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
   * @return an iterator over the {@link org.vectomatic.dom.svg.OMSVGLength}
   * elements in this list in proper sequence
   */
  @Override
  public final Iterator<OMSVGLength> iterator() {
	return new Iterator<OMSVGLength>() {
		private int index;
		@Override
		public boolean hasNext() {
			return index < getNumberOfItems();
		}

		@Override
		public OMSVGLength next() {
			return getItem(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
  }
}
