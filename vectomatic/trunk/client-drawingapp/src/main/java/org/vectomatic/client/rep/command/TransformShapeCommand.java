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
package org.vectomatic.client.rep.command;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.MouseControllerSelector;
import org.vectomatic.client.rep.controller.SelectShapeController;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.TransformMatrix;

/**
 * Command to alter the 2D transform of a shape
 */
public class TransformShapeCommand extends CommandBase  {
	public static final int SCALE = 1;
	public static final int ROTATE = 2;
	public static final int TRANSLATE = 3;
	private TransformMatrix _t, _mSelection;
	private List<Shape> _shapes;
	private int _kind;
	private MouseControllerSelector _selector;
	private SelectShapeController _selectShapeController;
	
	/**
	 * Constructor
	 * @param selection
	 * The selection object
	 * @param t
	 * The transform to apply to all shapes positional matrix
	 * @param mSelection
	 * The positional matrix of the selection before the transform was applied
	 * @param kind
	 * The kind of transformation (SCALING, ROTATION, TRANSLATION)
	 * @param selector
	 * The controller selector
	 * @param selectShapeController 
	 */
	public TransformShapeCommand(RepApplication app, TransformMatrix t, TransformMatrix mSelection, int kind, MouseControllerSelector selector, SelectShapeController selectShapeController) {
		super(app);
		_shapes = new ArrayList<Shape>(_app.getSelection().getSelectedShapes());
		_t = new TransformMatrix(t);
		_mSelection = new TransformMatrix(mSelection);
		_kind = kind;
		_selector = selector;
		_selectShapeController = selectShapeController;
	}
	
	public String getDescription() {
		switch(_kind) {
			case SCALE:
				return _app.getConstants().scaleShapeCommand();
			case ROTATE:
				return _app.getConstants().rotateShapeCommand();
			default:
				return _app.getConstants().translateShapeCommand();
		}
	}
	
	public void unexecute() {
		// Activate the transform controller
		if (_selector != null) {
			_selector.selectController(_selectShapeController.getButton());
		}
		
		// Cancel all previous transforms and reselect the shapes
		TransformMatrix mTmp = new TransformMatrix();
		TransformMatrix tInv = new TransformMatrix();
		TransformMatrix mInv = new TransformMatrix();
		_t.invert(tInv);
		_mSelection.invert(mInv);
		tInv.preMultiply(mInv);
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(tInv, mTmp));
		}
		
		// Select the shapes
		_app.getSelection().select(_shapes);			
		
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(_mSelection));
		}

		_app.getSelection().getRootShape().setTransform(_mSelection);
	}
	
	public void execute() {
		// Activate the transform controller
		if (_selector != null) {
			_selector.selectController(_selectShapeController.getButton());
		}
	
		// Cancel all previous transforms and reselect the shapes
		TransformMatrix mTmp = new TransformMatrix();
		TransformMatrix mInv = new TransformMatrix();
		TransformMatrix t = new TransformMatrix();
		_mSelection.invert(mInv);
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(mInv, mTmp));
		}

		// Select the shapes
		_app.getSelection().select(_shapes);			
		
		_mSelection.preMultiply(_t, t);
		for (int i = 0, size = _shapes.size(); i < size; i++) {
			Shape shape = _shapes.get(i);
			shape.setTransform(shape.getTransform().preMultiply(t, mTmp));
		}
		_app.getSelection().getRootShape().setTransform(t);
	}
}
