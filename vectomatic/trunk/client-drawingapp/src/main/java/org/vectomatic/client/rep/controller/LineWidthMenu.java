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

import org.vectomatic.client.rep.RepApplication;
import org.vectomatic.common.model.FloatAttributeValue;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Menu class to select line widths
 */
public class LineWidthMenu extends PopupPanel {
	private LineWidthController _controller;
	private LineWidthEditor _editor;
	public LineWidthMenu(LineWidthController controller) {
		super(true, true);
		_controller = controller;
		_editor = new LineWidthEditor(controller);
		Grid grid = new Grid(6, 1);
		Label editLabel = new Label(RepApplication.app._constants.editLineWidth());
		editLabel.setStyleName("lineWidthMenuCustom");
		editLabel.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
			}			
		});
		final int[] lineWidths = {1, 2, 3, 5, 8};
		for (int i = 0; i < lineWidths.length; i++) {
			LineWidthWidget lineWidthWidget = new LineWidthWidget(lineWidths[i], true);
			grid.setWidget(i, 0, lineWidthWidget);
		}
		grid.setWidget(lineWidths.length, 0, editLabel);
		setWidget(grid);
		setStyleName("lineWidthMenu");
		grid.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
				hide();
				if (row < lineWidths.length) {
					_controller.setLineWidth(new FloatAttributeValue(lineWidths[row]));
				} else {
					_editor.show();
				}
			}
		});
	}
}
