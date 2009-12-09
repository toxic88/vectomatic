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
package org.vectomatic.client.rep.view;

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.client.rep.controller.ColorEditor;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.Palette;
import org.vectomatic.common.model.style.PaletteList;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget class to edit palettes
 */
public class PaletteEditor extends DialogBox implements ChangeListener {
	private Button _newPaletteButton;
	private Button _clonePaletteButton;
	private Button _deletePaletteButton;
	private Button _editColorButton;
	private Button _newColorButton;
	private Button _deleteColorButton;
	private Button _closeButton;
	private ListBox _paletteList;
	private TextBox _paletteName;
	private PaletteList _palettes;
	private PaletteWidget _paletteWidget;
	private ColorEditor _colorEditor;
	private PlainColorMenu _menu;
	private static int _count;
	private RepApplication _app;
	
	public PaletteEditor(RepApplication app, ColorEditor colorEditor, PlainColorMenu menu) {
		super();
		_app = app;
		setText(_app.getConstants().paletteEditorPanel());
		_palettes = _app.getPalettes();
		_colorEditor = colorEditor;
		_menu = menu;

		_newPaletteButton = new Button(_app.getConstants().newPaletteButton(), new ClickListener() {
			public void onClick(Widget sender) {
				newPalette();
			}
		});
		_newPaletteButton.setWidth("100%");

		_clonePaletteButton = new Button(_app.getConstants().clonePaletteButton(), new ClickListener() {
			public void onClick(Widget sender) {
				clonePalette();
			}
		});
		_clonePaletteButton.setWidth("100%");
		
		_deletePaletteButton = new Button(_app.getConstants().deletePaletteButton(), new ClickListener() {
			public void onClick(Widget sender) {
				deletePalette();
			}
		});
		_deletePaletteButton.setWidth("100%");

		_editColorButton = new Button(_app.getConstants().editColorButton(), new ClickListener() {
			public void onClick(Widget sender) {
				editColor();
			}
		});
		_editColorButton.setWidth("100%");
		
		_newColorButton = new Button(_app.getConstants().newColorButton(), new ClickListener() {
			public void onClick(Widget sender) {
				newColor();
			}
		});
		_newColorButton.setWidth("100%");
		
		_deleteColorButton = new Button(_app.getConstants().deleteColorButton(), new ClickListener() {
			public void onClick(Widget sender) {
				deleteColor();
			}
		});
		_deleteColorButton.setWidth("100%");
		
		_closeButton = new Button(_app.getConstants().closeButton(), new ClickListener() {
			public void onClick(Widget sender) {
				close();
			}
		});
		_closeButton.setWidth("100%");
		
		_paletteList = new ListBox();
		_paletteList.setWidth("100%");
		_paletteList.setHeight("100%");
		_paletteList.setVisibleItemCount(5);
		for (int i = 0, size = _palettes.size(); i < size; i++) {
			_paletteList.addItem(_palettes.getPalette(i).getName());
		}
		
		_paletteName = new TextBox();
		_paletteName.setWidth("100%");
		_paletteName.setMaxLength(32);
		_paletteName.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				if (_paletteName.getText().length() > 0) {
					_paletteWidget.getPalette().setName(_paletteName.getText());
					_paletteList.setItemText(_paletteList.getSelectedIndex(), _paletteName.getText());
				} else {
					_paletteName.setText(_paletteWidget.getPalette().getName());
				}
			}
		});
		_paletteList.addChangeListener(this);
		_paletteWidget = new PaletteWidget(null, _colorEditor);
		if (_palettes.size() > 0) {
			_paletteList.setSelectedIndex(0);
			onChange(_paletteList);
		}
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, _paletteList);
		table.setWidget(0, 1, new Label(_app.getConstants().paletteNameLabel()));
		table.setWidget(1, 0, _paletteName);
		ScrollPanel scrollPanel = new ScrollPanel(_paletteWidget);
		scrollPanel.setWidth("195px");
		scrollPanel.setHeight("125px");
		table.setWidget(2, 0, scrollPanel);
		table.setWidget(3, 0, _newPaletteButton);
		table.setWidget(3, 1, _newColorButton);
		table.setWidget(4, 0, _clonePaletteButton);
		table.setWidget(4, 1, _editColorButton);
		table.setWidget(5, 0, _deletePaletteButton);
		table.setWidget(5, 1, _deleteColorButton);
		table.setWidget(6, 1, _closeButton);
		table.getFlexCellFormatter().setRowSpan(0, 0, 3);
		table.getColumnFormatter().setWidth(0, "195px");
		table.getColumnFormatter().setWidth(1, "195px");
		setWidget(table);
	}
	
	
	private void newPalette() {
		Palette palette = _palettes.newPalette();
		palette.setName(_app.getConstants().paletteBaseName() + _count++);
		_paletteList.addItem(palette.getName());
		_paletteList.setSelectedIndex(_palettes.size() - 1);
		onChange(_paletteList);
	}
	
	private void deletePalette() {
		int index = _paletteList.getSelectedIndex();
		_palettes.removePalette(index);
		_paletteList.removeItem(index);
		index = Math.min(_paletteList.getItemCount() - 1, index);
		if (index != -1) {
			_paletteList.setSelectedIndex(index);
		}
		onChange(_paletteList);
	}
	
	private void clonePalette() {
		int index = _paletteList.getSelectedIndex();
		Palette clone = _palettes.clonePalette(index);
		clone.setName(_app.getConstants().copyOf() + _palettes.getPalette(index).getName());
		_paletteList.addItem(clone.getName());
		_paletteList.setSelectedIndex(_palettes.size() - 1);
		onChange(_paletteList);
	}
	
	private void newColor() {
		_paletteWidget.getPalette().addColor(Color.fromRGB(255, 255, 255));
		refresh();
	}
	
	private void deleteColor() {
		int index = _paletteWidget.getSelectedColorIndex();
		Palette palette = _paletteWidget.getPalette();
		palette.removeColor(index);
		index = Math.min(palette.getSize() - 1, index);
		_paletteWidget.selectColor(index);
		refresh();
	}
	
	private void editColor() {
		_colorEditor.setColor(_paletteWidget.getPalette().getColor(_paletteWidget.getSelectedColorIndex()), new ChangeListener() {
			public void onChange(Widget sender) {
				Palette palette = _paletteWidget.getPalette();
				int index = _paletteWidget.getSelectedColorIndex();
				_colorEditor.getColor(palette.getColor(index));
				palette.setColor(index, palette.getColor(index));
			}		
		});
		_colorEditor.show();
	}
	
	private void close() {
		hide();
		_menu.setPalette(getSelectedPalette());
	}

	public void onChange(Widget sender) {
		Palette palette = getSelectedPalette();
		_paletteWidget.setPalette(palette);
		_paletteName.setText(palette != null ? palette.getName() : "");
		refresh();
	}
	
	private void refresh() {
		int index = _paletteList.getSelectedIndex();
		_clonePaletteButton.setEnabled(index != -1);
		_deletePaletteButton.setEnabled((index != -1) && (_palettes.size() > 1));
		if (index != -1 && (_palettes.getPalette(index) != _paletteWidget.getPalette())) {
			_paletteWidget.setPalette(_palettes.getPalette(index));
		}
		_paletteName.setEnabled(index != -1);
		Palette palette = (index != -1) ? _palettes.getPalette(index) : null;
		_deleteColorButton.setEnabled((palette != null) && (_paletteWidget.getSelectedColorIndex() != -1));
		_newColorButton.setEnabled(palette != null);
		_editColorButton.setEnabled((palette != null) && (_paletteWidget.getSelectedColorIndex() != -1));
	}
	
	public Palette getSelectedPalette() {
		int index = _paletteList.getSelectedIndex();
		return (index != -1) ? _palettes.getPalette(index) : null;
	}
}
