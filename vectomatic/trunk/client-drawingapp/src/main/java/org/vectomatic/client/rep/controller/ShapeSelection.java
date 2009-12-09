/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of Vectomatic.
 * 
 * Vectomatic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vectomatic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Vectomatic.  If not, see <http://www.gnu.org/licenses/>
 **********************************************/
package org.vectomatic.client.rep.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.common.events.IDrawingModelListener;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.CloneShapeVisitor;
import org.vectomatic.common.model.DrawingModel;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.IAttributeValue;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.ShapeGroup;


/**
 * Class to represent the currently selected shapes.
 * Selected shapes are cloned and clones are accessed with getRootShape().
 * @author Lukas Laag
 */
public class ShapeSelection implements IDrawingModelListener {
	private Shape _rootShape;
	private List<Shape> _selectedShapes;
	private DrawingModel _model;
	private CloneShapeVisitor _cloner;
	private List<IShapeSelectionListener> _shapeSelectionListeners;
	private int _current;

	public ShapeSelection(DrawingModel model) {
		_selectedShapes = new ArrayList<Shape>();
		_model = model;
		_model.addDrawingModelListener(this);
		_cloner = new CloneShapeVisitor();
	}
	
	public void addShapeSelectionListener(IShapeSelectionListener listener) {
		if (_shapeSelectionListeners == null) {
			_shapeSelectionListeners = new ArrayList<IShapeSelectionListener>();
		}
		_shapeSelectionListeners.add(listener);
	}
	
	public void removeShapeSelectionListener(IShapeSelectionListener listener) {
		if (_shapeSelectionListeners != null) {
			_shapeSelectionListeners.remove(listener);
		}
	}
	
	public void fireSelectionHasChanged() {
		if (_shapeSelectionListeners != null) {
			for (int i = 0, size = _shapeSelectionListeners.size(); i < size; i++) {
				IShapeSelectionListener listener = _shapeSelectionListeners.get(i);
				listener.selectionChanged(this);
			}
		}
	}
	
	/**
	 * Returns the clone of the currently selected shapes. 
	 * For mono selection, this is simply the selected shape clone.
	 * For multi selection, it is the temporary shape group which 
	 * contains the clones of the selected shapes.
	 */
	public Shape getRootShape() {
		return _rootShape;
	}
	
	/**
	 * Returns a list which contains all the selected shapes.
	 * The list is immutable
	 */
	public List<Shape> getSelectedShapes() {
		return _selectedShapes;
	}
	
	/**
	 * @param rootShape
	 */
	public Shape select(List<Shape> shapes) {
		_rootShape = null;
		_selectedShapes.clear();

		// Select the specified shapes
		if (shapes.size() > 1) {
			// Multi-selection
			List<Shape> shapeClones = new ArrayList<Shape>();
			for (int i = 0, size = shapes.size(); i < size; i++) {
				Shape shape = shapes.get(i);
				_selectedShapes.add(shape);
				shape.acceptVisitor(_cloner);
				shapeClones.add(_cloner.getClone());
			}
			_rootShape = new ShapeGroup(shapeClones);
		} else if (shapes.size() > 0) {
			// Mono-selection
			Shape shape = shapes.get(0);
			_selectedShapes.add(shape);
			shape.acceptVisitor(_cloner);
			_rootShape = _cloner.getClone();
		}
		if (_rootShape != null) {
			_rootShape.setAttribute(Attribute.FILL_OPACITY, new FloatAttributeValue(0.2f));
		}
		fireSelectionHasChanged();
		return _rootShape;
	}

	/**
	 * Listens to model change events. Removal of a shape from
	 * the model causes the selection to be recomputed.
	 */
	public void modelHasChanged(DrawingModel model) {
		List<Shape> newSelectedShapes = new ArrayList<Shape>();
		for (int i = 0, size = _selectedShapes.size(); i < size; i++) {
			Shape shape = _selectedShapes.get(i);
			if (_model.contains(shape)) {
				newSelectedShapes.add(shape);
			}
		}
		if (!newSelectedShapes.equals(_selectedShapes)) {
			select(newSelectedShapes);
		} else {
			// The model change may be have been caused by of the shapes
			// changing its attributes. Refresh the cloned attributes
			if (_selectedShapes.size() > 1) {
				List<Shape> shapes = ((ShapeGroup)_rootShape).getShapes();
				for (int i = 0, size = shapes.size(); i < size; i++) {
					shapes.get(i).copyAttributes(_selectedShapes.get(i));
				}
				_rootShape.setAttribute(Attribute.FILL_OPACITY, new FloatAttributeValue(0.2f));
			} else if (_selectedShapes.size() == 1) {
				_rootShape.copyAttributes(_selectedShapes.get(0));
				_rootShape.setAttribute(Attribute.FILL_OPACITY, new FloatAttributeValue(0.2f));
			}
		}
	}
	
	public Iterator<Shape> iterator() {
		_current = 0;
		return new Iterator<Shape>() {

			public boolean hasNext() {
				return _current < _selectedShapes.size();
			}

			public Shape next() {
				if (_current >= _selectedShapes.size()) {
					throw new NoSuchElementException();
				}
				return _selectedShapes.get(_current++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public boolean hasAttributeChanged(Attribute attribute, IAttributeValue value) {
		boolean changed = false;
		for (int i = 0, size = _selectedShapes.size(); i < size; i++) {
			Shape shape = _selectedShapes.get(i);
			IAttributeValue attributeValue = shape.getAttribute(attribute);
			if (!value.equals(attributeValue)) {
				changed = true;
				break;
			}
		}
		return changed;
	}
}
