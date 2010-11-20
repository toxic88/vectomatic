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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.impl.Attr;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Class to provide a {@link com.extjs.gxt.ui.client.data.ModelData}
 * implementation backed by a DOM element.
 * @author laaglu
 */
public class SVGTreeNode implements TreeModel {
	/**
	 * Interface to abstract access to a property of
	 * the backing DOM element
	 * @author laaglu
	 * @param <T> The property type
	 */
	public static interface IProperty<T> {
		/**
		 * Returns the identifier of this property.
		 * @return
		 */
		String getPropertyId();
		/**
		 * Returns the value of this property for
		 * the specified node.
		 * @param treeNode The tree node
		 * @return The value of this property for the specified node
		 */
		T get(SVGTreeNode treeNode);
		/**
		 * Sets the value of this property for
		 * the specified node and returns its former value.
		 * @param treeNode The tree node
		 * @param value The value of this property for the specified node
		 * @return The former value of the property
		 */
		T set(SVGTreeNode treeNode, T value);
		/**
		 * Removes the property from the the specified 
		 * node and returns its former value.
		 * @param treeNode The tree node
		 * @return The former value of the property
		 */
		T remove(SVGTreeNode treeNode);
	}
	
	/**
	 * {@link org.vectomatic.svg.edit.client.engine.SVGTreeNode.IProperty}
	 * implementation based on XPaths. The XPath must evaluate to
	 * a DOM attribute or a DOM text node.
	 * @author laaglu
	 * @param <T> The property type
	 */
	public static class XPathProperty<T> implements IProperty<T> {
		private String xpath;
		/**
		 * Constructor
		 * @param xpath The XPath which gives access to
		 * the property value from the DOM node.
		 */
		public XPathProperty(String xpath) {
			this.xpath = xpath;
		}
		
		/**
		 * Returns the XPath which gives access to
		 * the property value from the DOM node. 
		 * The XPath must evaluate to
		 * a DOM attribute or a DOM text node.
		 * @return the XPath which gives access to
		 * the property value from the DOM node
		 */
		public String getXPath() {
			return xpath;
		}
		
		@Override
		public String getPropertyId() {
			return xpath;
		}
		
		@Override
		public T get(SVGTreeNode treeNode) {
			Node node = DOMHelper.evaluateNodeXPath(treeNode.element, xpath, SVGPrefixResolver.INSTANCE);
			if (node != null) {
				switch(node.getNodeType()) {
					case Node.TEXT_NODE:
						return (T)((Text)node.cast()).getData();
					case 2:
						return (T)((Attr)node.cast()).getValue();
					default:
						assert(false);
				}
			}
			return null;
		}
		
		@Override
		public T set(SVGTreeNode treeNode, T value) {
			T oldValue = null;
			Node node = DOMHelper.evaluateNodeXPath(treeNode.element, xpath, SVGPrefixResolver.INSTANCE);
			if (node != null) {
				switch(node.getNodeType()) {
					case Node.TEXT_NODE:
						{
							Text text = node.cast();
							oldValue = (T)text.getData();
							text.setData((String)value);
						}
						break;
					case 2:
						{
							Attr attr = node.cast();
							oldValue = (T)attr.getValue();
							attr.setValue((String)value);
						}
						break;
					default:
						assert(false);
				}
				treeNode.notifyPropertyChanged(getPropertyId(), null, oldValue);
			}
			return oldValue;
		}
		
		@Override
		public T remove(SVGTreeNode treeNode) {
			T oldValue = null;
			Node node = DOMHelper.evaluateNodeXPath(treeNode.element, xpath, SVGPrefixResolver.INSTANCE);
			if (node != null) {
				switch(node.getNodeType()) {
					case Node.TEXT_NODE:
						{
							Text text = node.cast();
							oldValue = (T)text.getData();
							text.getParentElement().removeChild(text);
						}
					break;
					case 2:
						{
							Attr attr = node.cast();
							oldValue = (T)attr.getValue();
							attr.getOwnerElement().removeAttribute(attr.getName());
						}
					break;
					default:
						assert(false);
				}
				treeNode.notifyPropertyChanged(getPropertyId(), null, oldValue);
			}
			return oldValue;
		}
	}
	
	/**
	 * {@link org.vectomatic.svg.edit.client.engine.SVGTreeNode.IProperty}
	 * implementation based on CSS style properties. The value of the property
	 * can either be stored in the CSS style, or as an SVG attribute. Graphically,
	 * the CSS style property has precedence over the SVG attribute. 
	 * The implementation first tries to retrieve the value from the CSS style.
	 * If it fails it looks up the corresponding SVG property
	 * @author laaglu
	 * @param <T> The property type
	 */
	public static class CssProperty<T> implements IProperty<T>  {
		private String propertyName;
		
		/**
		 * Constructor
		 * @param propertyName The CSS property name
		 */
		public CssProperty(String propertyName) {
			this.propertyName = propertyName;
		}
		
		/**
		 * Returns the CSS property name
		 * @return the CSS property name
		 */
		public String getPropertyName() {
			return propertyName;
		}
		
		@Override
		public String getPropertyId() {
			return propertyName;
		}
		
		@Override
		public T get(SVGTreeNode treeNode) {
			OMSVGStyle style = treeNode.element.getStyle().cast();
			String value = style.getSVGProperty(propertyName);
			if (value == null) {
				value = treeNode.element.getAttribute(propertyName);
			}
			return (T)value;
		}
		
		@Override
		public T set(SVGTreeNode treeNode, T value) {
			OMSVGStyle style = treeNode.element.getStyle().cast();
			T oldValue = (T)style.getSVGProperty(propertyName);
			style.setSVGProperty(propertyName, (String)value);
			if (oldValue == null) {
				oldValue = (T)treeNode.element.getAttribute(propertyName);
			}
			treeNode.element.removeAttribute(propertyName);
			treeNode.notifyPropertyChanged(getPropertyId(), null, oldValue);
			return oldValue;
		}
		
		@Override
		public T remove(SVGTreeNode treeNode) {
			OMSVGStyle style = treeNode.element.getStyle().cast();
			T oldValue = (T)style.getSVGProperty(propertyName);
			style.clearSVGProperty(propertyName);
			if (oldValue == null) {
				oldValue = (T)treeNode.element.getAttribute(propertyName);
			}
			treeNode.element.removeAttribute(propertyName);
			treeNode.notifyPropertyChanged(getPropertyId(), null, oldValue);
			return oldValue;
		}
	}
	
	/**
	 * {@link org.vectomatic.svg.edit.client.engine.SVGTreeNode.IProperty}
	 * implementation to map the set of possible values for a property
	 * on another set of values. The implementation works as a decorator
	 * for another {@link org.vectomatic.svg.edit.client.engine.SVGTreeNode.IProperty}
	 * implementation.
	 * @author laaglu
	 * @param <U> The property type
	 * @param <V> The property type of the decorated 
	 * {@link org.vectomatic.svg.edit.client.engine.SVGTreeNode.IProperty} implementation
	 */
	public static class MappedProperty<U, V> implements IProperty<U>  {
		private Map<U,V> uToV;
		private Map<V,U> vToU;
		private IProperty<V> property;
		/**
		 * Constructor.
		 * @param property The property to decorate
		 * @param uToV A map to convert the values from
		 * the property type to the decorated property type
		 * @param vToU A map to convert the values from
		 * the decorated property type to the property type
		 */
		public MappedProperty(IProperty<V> property, Map<U,V> uToV, Map<V,U> vToU) {
			this.property = property;
			this.uToV = uToV;
			this.vToU = vToU;
		}
		/**
		 * Returns the decorated property.
		 * @return the decorated property
		 */
		public IProperty<V> getProperty() {
			return property;
		}
		@Override
		public String getPropertyId() {
			return property.getPropertyId();
		}
		@Override
		public U get(SVGTreeNode treeNode) {
			return vToU.get(property.get(treeNode));
		}
		@Override
		public U set(SVGTreeNode treeNode, U value) {
			return vToU.get(property.set(treeNode, uToV.get(value)));
		}
		@Override
		public U remove(SVGTreeNode treeNode) {
			return vToU.get(property.remove(treeNode));
		}
	}
	
	/**
	 * The SVG DOM element represented by this tree node
	 */
	protected SVGElement element;
	/**
	 * To implement ChangeEventSource
	 */
	protected transient ChangeEventSupport changeEventSupport;
	/**
	 * The property names of this object
	 */
	static Map<String, IProperty> nameToProperty;
	/**
	 * The model's parent.
	 */
	protected TreeModel parent;

	/**
	 * The model's children.
	 */
	protected List<ModelData> children;
	
	/**
	 * Constructor
	 * @param element The SVG DOM element represented by this node
	 * @param properties The properties of the node
	 */
	public SVGTreeNode(SVGElement element, List<IProperty> properties) {
		this.element = element;
		this.nameToProperty = new HashMap<String, IProperty>();
		for (IProperty property : properties) {
			nameToProperty.put(property.getPropertyId(), property);
		}
	    changeEventSupport = new ChangeEventSupport();
	    children = new ArrayList<ModelData>();
	}
	
	/**
	 * Returns the SVG element backing this node.
	 * @return the SVG element backing this node.
	 */
	public SVGElement getElement() {
		return element;
	}

	///////////////////////////////////////////////////
	// Implementation of the ModelData interface
	///////////////////////////////////////////////////
	
	@Override
	public <X> X get(String property) {
		return (X)nameToProperty.get(property).get(this);
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Entry<String, IProperty> entry : nameToProperty.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().get(this));
		}
		return properties;
	}

	@Override
	public Collection<String> getPropertyNames() {
		return nameToProperty.keySet();
	}

	@Override
	public <X> X remove(String property) {
		return (X)nameToProperty.get(property).remove(this);
	}

	@Override
	public <X> X set(String property, X value) {
		return (X)nameToProperty.get(property).set(this, value);
	}

	///////////////////////////////////////////////////
	// Implementation of the ChangeEventSource interface
	///////////////////////////////////////////////////
	@Override
	public void addChangeListener(ChangeListener... listener) {
	    changeEventSupport.addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener... listener) {
	    changeEventSupport.removeChangeListener(listener);
	}

	@Override
	public void removeChangeListeners() {
	    changeEventSupport.removeChangeListeners();
	}

	@Override
	public void setSilent(boolean silent) {
	    changeEventSupport.setSilent(silent);
	}

	@Override
	public void notify(ChangeEvent event) {
	    changeEventSupport.notify(event);
		if (!isSilent() && parent != null && parent instanceof ChangeEventSource) {
			event.setSource(parent);
			((ChangeEventSource) parent).notify(event);
		}
	}
	
	protected void notifyPropertyChanged(String name, Object value, Object oldValue) {
		if (!Util.equalWithNull(value, oldValue)) {
			notify(new PropertyChangeEvent(Update, this, name, oldValue, value));
		}
	}
	
	public boolean isSilent() {
	    return changeEventSupport.isSilent();
	}

	protected void fireEvent(int type) {
		notify(new ChangeEvent(type, this));
	}

	protected void fireEvent(int type, Model item) {
		notify(new ChangeEvent(type, this, item));
	}

	///////////////////////////////////////////////////
	// Implementation of the TreeModel interface
	///////////////////////////////////////////////////
	/**
	 * Adds a child to the model and fires an {@link ChangeEventSource#Add}
	 * event.
	 * @param child the child to be added
	 */
	public void add(ModelData child) {
		insert(child, getChildCount());
	}

	/**
	 * Returns the child at the given index or <code>null</code> if the index is
	 * out of range.
	 * @param index the index to be retrieved
	 * @return the model at the index
	 */
	public ModelData getChild(int index) {
		if ((index < 0) || (index >= children.size())) {
			return null;
		}
		return children.get(index);
	}

	/**
	 * Returns the number of children.
	 * @return the number of children
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * Returns the model's children.
	 * @return the children
	 */
	public List<ModelData> getChildren() {
		return children;
	}

	/**
	 * Returns the model's parent or <code>null</code> if no parent.
	 * @return the parent
	 */
	public TreeModel getParent() {
		return parent;
	}

	public int indexOf(ModelData child) {
		return children.indexOf(child);
	}

	/**
	 * Inserts a child to the model and fires an {@link ChangeEventSource#Add}
	 * event.
	 * @param child the child to be inserted
	 * @param index the location to insert the child
	 */
	public void insert(ModelData child, int index) {
		adopt(child);
		children.add(index, child);
		ChangeEvent evt = new ChangeEvent(Add, this);
		evt.setParent(this);
		evt.setItem(child);
		evt.setIndex(index);
		notify(evt);
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * Removes the child at the given index.
	 * @param index the child index
	 */
	public void remove(int index) {
		if (index >= 0 && index < getChildCount()) {
			remove(getChild(index));
		}
	}

	/**
	 * Removes the child from the model and fires a
	 * {@link ChangeEventSource#Remove} event.
	 * @param child  the child to be removed
	 */
	public void remove(ModelData child) {
		orphan(child);
		children.remove(child);
		ChangeEvent evt = new ChangeEvent(Remove, this);
		evt.setParent(this);
		evt.setItem(child);
		notify(evt);
	}

	public void removeAll() {
		for (int i = children.size() - 1; i >= 0; i--) {
			remove(getChild(i));
		}
	}

	/**
	 * Sets the model's children. All existing children are first removed.
	 * @param children the children to be set
	 */
	public void setChildren(List<ModelData> children) {
		removeAll();
		if (children != null) {
			for (ModelData child : children) {
				add(child);
			}
		}
	}

	public void setParent(TreeModel parent) {
		this.parent = parent;
	}

	private void setParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			treeChild.setParent(this);
		} else {
			child.set("gxt-parent", child);
		}
	}

	private TreeModel getParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			return treeChild.getParent();
		} else {
			return (TreeModel) child.get("gxt-parent");
		}
	}

	private void adopt(ModelData child) {
		TreeModel p = getParentInternal(child);
		if (p != null && p != this) {
			p.remove(child);
		}
		setParentInternal(child);
	}

	private void orphan(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			treeChild.setParent(null);
		} else {
			child.remove("gxt-parent");
		}
	}
}
