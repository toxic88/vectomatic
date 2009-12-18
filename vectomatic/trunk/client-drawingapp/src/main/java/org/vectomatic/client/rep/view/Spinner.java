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
package org.vectomatic.client.rep.view;

import org.vectomatic.client.rep.RepApplication;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Spinner widget class
 */
public class Spinner extends SimplePanel {
	private TextBox _box;
	private PushButton _addButton;
	private PushButton _subButton;
	private int _min;
	private int _max;
	private int _value;
	public Spinner(int min, int max, int value) {
		_min = min;
		_max = max;
		_value = value;
		_box = new TextBox();
		int length = 1 + (int)((Math.log(_max) / Math.log(10)));
		_box.setMaxLength(length);
		_box.setVisibleLength(length);
		_box.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				try {
					int value = Integer.parseInt(_box.getText());
					if (value >= _min && value <= _max) {
						_value = value;
					}
				} catch(NumberFormatException e) {
				}
				update();
			}
		}); 
		_addButton = new PushButton(RepApplication.app._icons.upIcon().createImage());
		_addButton.setStyleName("spinner-button");
		_addButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				add();
			}		
		});
		_subButton = new PushButton(RepApplication.app._icons.downIcon().createImage());
		_subButton.setStyleName("spinner-button");
		_subButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				sub();
			}		
		});
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, _box);
		table.setWidget(0, 1, _addButton);
		table.setWidget(1, 0, _subButton);
		table.getFlexCellFormatter().setRowSpan(0, 0, 2);
		table.setCellSpacing(0);
		setWidget(table);
		update();
	}
	
	public void add() {
		_value++;
		update();
	}
	public void sub() {
		_value--;
		update();
	}
	public int getValue() {
		return _value;
	}
	public void setValue(int value) {
		_value = value;
		update();
	}
	public void update() {
		_addButton.setEnabled(_value < _max);
		_subButton.setEnabled(_value > _min);
		_box.setText(Integer.toString(_value));						
	}
}
