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
 * along with Vectomatic.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.client.rep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.command.SetAttributeCommand;
import org.vectomatic.client.rep.events.IShapeSelectionListener;
import org.vectomatic.client.rep.view.IStyleMenu;
import org.vectomatic.client.rep.view.PlainColorMenu;
import org.vectomatic.client.rep.view.StyleWell;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.Shape;
import org.vectomatic.common.model.geometry.ShapeGroup;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.IStyle;
import org.vectomatic.common.model.style.NoneStyle;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to manage changes of the color or transparency of a shape
 * @author Lukas Laag
 */
public class StyleController extends ControllerBase implements IShapeSelectionListener, ChangeListener {
	private Attribute _styleAttribute;
	private Attribute _opacityAttribute;
	private ListBox _styleBox;
	private StyleWell _styleWell;
	private Label _styleLabel;
	private HorizontalPanel _row;
	private IStyle _defaultStyle;
	private FloatAttributeValue _defaultOpacity;

	private Set<Integer> _styleKinds;
	private Set<IStyle> _styleValues;
	private Set<FloatAttributeValue> _opacities;
	private Map<Integer, IStyleMenu> _styleKindToMenu;

	public StyleController(RepApplication app, String label, Attribute styleAttribute, Attribute opacityAttribute, IStyle defaultStyle, FloatAttributeValue defaultOpacity) {
		super(app);
		_styleKinds = new HashSet<Integer>();
		_styleValues = new HashSet<IStyle>();
		_opacities = new HashSet<FloatAttributeValue>();
		_styleKindToMenu = new HashMap<Integer, IStyleMenu>();
		
		_app.getSelection().addShapeSelectionListener(this);
		_styleAttribute = styleAttribute;
		_opacityAttribute = opacityAttribute;
		_defaultStyle = defaultStyle;
		_defaultOpacity = defaultOpacity;
		
	    PlainColorMenu plainColorMenu = new PlainColorMenu(app, this);
	    _styleKindToMenu.put(Color.BLACK.getKind(), plainColorMenu);
		_styleLabel = new Label(label);
		_styleWell = new StyleWell();
		_styleWell.setWidth("30px");
		_styleWell.setHeight("8px");
		_styleBox = new ListBox();
		_styleBox.addItem(_app.getConstants().plainItem());
		//_styleBox.addItem(_constants.gradientItem());
		//_styleBox.addItem(_constants.patternItem());
		_styleBox.addItem(_app.getConstants().noneItem());
		_styleBox.addChangeListener(this);
		_row = new HorizontalPanel();
		_row.add(_styleLabel);
		_row.add(_styleBox);
		_row.add(_styleWell);
		
		IStyleMenu menu = _styleKindToMenu.get(_defaultStyle.getKind());
		_styleWell.setStyle(_defaultStyle, menu);
		if (menu != null) {
			menu.setOpacity(_defaultOpacity);
		}
	}
	
	public Widget getWidget() {
		return _row;
	}
	
	public void selectionChanged(ShapeSelection selection) {
		List<Shape> shapes = new ArrayList<Shape>(selection.getSelectedShapes());
		_styleKinds.clear();
		_styleValues.clear();
		_opacities.clear();
		
		IStyle styleValue = null;
		Integer styleKind = null;
		FloatAttributeValue opacity = null;
		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = shapes.get(i);
			styleValue = (IStyle)shape.getAttribute(_styleAttribute);
			if ((styleValue == null) && (shape instanceof ShapeGroup)) {
				shapes.addAll(((ShapeGroup)shape).getShapes());
				continue;
			}
			styleKind = styleValue.getKind();
			opacity = (FloatAttributeValue)shape.getAttribute(_opacityAttribute);
			_styleKinds.add(styleKind);
			_styleValues.add(styleValue);
			_opacities.add(opacity);
		}
		
		if (_styleValues.size() == 0) {
			_styleBox.setSelectedIndex(_defaultStyle.getKind().intValue());
			IStyleMenu menu = _styleKindToMenu.get(_defaultStyle.getKind());
			_styleWell.setStyle(_defaultStyle, menu);
			if (menu != null) {
				menu.setOpacity(_defaultOpacity);
			}
		} else if (_styleValues.size() == 1) {
			_styleBox.setSelectedIndex(styleKind.intValue());
			IStyleMenu menu = _styleKindToMenu.get(styleKind);
			_styleWell.setStyle(styleValue, menu);
			if (menu != null) {
				menu.setOpacity(opacity);
			}
		} else {
			IStyleMenu menu = null;
			if (_styleKinds.size() == 1) {
				_styleBox.setSelectedIndex(styleKind.intValue());
				menu = _styleKindToMenu.get(styleKind);
			} else {
				_styleBox.setSelectedIndex(-1);
			}
			_styleWell.setStyle(NoneStyle.NONE, menu);
		}
	}

	/**
	 * Invoked when the user changes the list box.
	 */
	public void onChange(Widget sender) {
		RepApplication.app.debugPrint("style kind change");
		IStyleMenu menu = _styleKindToMenu.get(new Integer(_styleBox.getSelectedIndex()));
		IStyle style = (menu != null) ? menu.getSelectedStyle() : NoneStyle.NONE;
		_styleWell.setStyle(style, menu);
		//Determine if the change is real and should generate a corresponding command
		if (_app.getSelection().hasAttributeChanged(_styleAttribute, style)) {
			SetAttributeCommand setAttributeCommand = new SetAttributeCommand(_app, _styleAttribute, style);
			setAttributeCommand.execute();
			_app.getHistory().addCommand(setAttributeCommand);
		}
		if (_app.getSelection().getSelectedShapes().size() == 0) {
			_defaultStyle = style;
		}
	}
	
	/**
	 * Invoked when the user changes the opacity in the color menu
	 * @param opacity 
	 */
	public void setOpacity(FloatAttributeValue opacity) {
		RepApplication.app.debugPrint("opacity change");
		//Determine if the change is real and should generate a corresponding command
		if (_app.getSelection().hasAttributeChanged(_opacityAttribute, opacity)) {
			SetAttributeCommand setAttributeCommand = new SetAttributeCommand(_app, _opacityAttribute, opacity);
			setAttributeCommand.execute();
			_app.getHistory().addCommand(setAttributeCommand);
		}
		if (_app.getSelection().getSelectedShapes().size() == 0) {
			_defaultOpacity = opacity;
		}
	}
	
	/**
	 * Invoked when the user changes the color in the color menu
	 */
	public void setStyle(IStyle style) {
		RepApplication.app.debugPrint("style value change");
		IStyleMenu menu = _styleKindToMenu.get(style.getClass().getName());
		_styleWell.setStyle(style, menu);
		//Determine if the change is real and should generate a corresponding command
		if (_app.getSelection().hasAttributeChanged(_styleAttribute, style)) {
			SetAttributeCommand setAttributeCommand = new SetAttributeCommand(_app, _styleAttribute, style);
			setAttributeCommand.execute();
			_app.getHistory().addCommand(setAttributeCommand);
		}
		if (_app.getSelection().getSelectedShapes().size() == 0) {
			_defaultStyle = style;
		}
	}


	/**
	 * Returns the currently selected style.
	 * This method is called by controllers which instantiate new shapes
	 * @return
	 */
	public IStyle getStyle() {
		return _defaultStyle;
	}
	
	/**
	 * Returns the currently selected opacity
	 * This method is called by controllers which instantiate new shapes
	 * @return
	 */
	public FloatAttributeValue getOpacity() {
		return _defaultOpacity;
	}
	
	public Attribute getStyleAttribute() {
		return _styleAttribute;
	}
	
}
