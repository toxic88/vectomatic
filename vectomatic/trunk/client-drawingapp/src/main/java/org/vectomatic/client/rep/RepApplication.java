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
package org.vectomatic.client.rep;

import org.vectomatic.client.UIConstants;
import org.vectomatic.client.UIMessages;
import org.vectomatic.client.rep.command.ChangeOrderingCommand;
import org.vectomatic.client.rep.command.CommandHistory;
import org.vectomatic.client.rep.controller.AboutPanel;
import org.vectomatic.client.rep.controller.ContextualMenuVisitor;
import org.vectomatic.client.rep.controller.CopyController;
import org.vectomatic.client.rep.controller.CutController;
import org.vectomatic.client.rep.controller.DeleteController;
import org.vectomatic.client.rep.controller.EditPathController;
import org.vectomatic.client.rep.controller.EditPolylineController;
import org.vectomatic.client.rep.controller.ExportController;
import org.vectomatic.client.rep.controller.GroupController;
import org.vectomatic.client.rep.controller.LineWidthController;
import org.vectomatic.client.rep.controller.MouseControllerSelector;
import org.vectomatic.client.rep.controller.NewEllipseController;
import org.vectomatic.client.rep.controller.NewPathController;
import org.vectomatic.client.rep.controller.NewPolylineController;
import org.vectomatic.client.rep.controller.NewRectController;
import org.vectomatic.client.rep.controller.OrderingController;
import org.vectomatic.client.rep.controller.PasteController;
import org.vectomatic.client.rep.controller.RedoController;
import org.vectomatic.client.rep.controller.RepresentationController;
import org.vectomatic.client.rep.controller.ResizeController;
import org.vectomatic.client.rep.controller.SelectShapeController;
import org.vectomatic.client.rep.controller.ShapeSelection;
import org.vectomatic.client.rep.controller.StyleController;
import org.vectomatic.client.rep.controller.UndoController;
import org.vectomatic.client.rep.controller.UngroupController;
import org.vectomatic.client.rep.view.DrawingView;
import org.vectomatic.common.model.Attribute;
import org.vectomatic.common.model.DrawingModel;
import org.vectomatic.common.model.FloatAttributeValue;
import org.vectomatic.common.model.style.Color;
import org.vectomatic.common.model.style.Palette;
import org.vectomatic.common.model.style.PaletteList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Application root class
 */
public class RepApplication implements EntryPoint,EventListener {
	public static RepApplication app;
	public UIConstants _constants;
	public UIMessages _messages;
	public UIImageBundle _icons;
	public TextArea debugArea;
	private DrawingView _view;
	private DrawingModel _model;
	private CommandHistory _history;
	private ShapeSelection _selection;
	private ClipBoard _clipBoard;
	private MouseControllerSelector _selector;
	private PaletteList _paletteList;
	private StyleController _lineStyleController;
	private StyleController _fillStyleController;
	private LineWidthController _lineWidthController;
	
	public void debugPrint(String s) {
		//debugArea.setText(debugArea.getText() + s + "\n");
	}
	
	public CommandHistory getHistory() {
		return _history;
	}
	
	public DrawingModel getModel() {
		return _model;
	}
	
	public ShapeSelection getSelection() {
		return _selection;
	}
	
	public ClipBoard getClipboard() {
		return _clipBoard;
	}
	
	public DrawingView getView() {
		return _view;
	}

	public PaletteList getPalettes() {
		return _paletteList;
	}

	public MouseControllerSelector getSelector() {
		return _selector;
	}
	
	public UIConstants getConstants() {
		return _constants;
	}
	
	public UIMessages getMessages() {
		return _messages;
	}
	
	public UIImageBundle getIcons() {
		return _icons;
	}
	
	public StyleController getLineStyleController() {
		return _lineStyleController;
	}
	
	public StyleController getFillStyleController() {
		return _fillStyleController;
	}
	
	public LineWidthController getLineWidthController() {
		return _lineWidthController;
	}

	public void onModuleLoad() {
		_constants = (UIConstants)GWT.create(UIConstants.class);
		_messages = (UIMessages)GWT.create(UIMessages.class);
		_icons = (UIImageBundle)GWT.create(UIImageBundle.class);
		
		app = this;
		//debugArea = new TextArea();
		_model = new DrawingModel();
		_selection = new ShapeSelection(_model);
		_history = new CommandHistory(20);
	    _paletteList = new PaletteList();
	    _paletteList.addPalette(Palette.getEGA());
	    _paletteList.addPalette(Palette.getGOLD());
	    
		RepresentationController repController = new RepresentationController(this);
		_view = new DrawingView(repController, _model, 400, 400);
		_view.setTabIndex(1);
		_view.setFocus(true);
		_selector = new MouseControllerSelector(_view);	
		
		Attribute.LINE_STYLE.setName(_constants.lineStyleAttribute());
		Attribute.LINE_OPACITY.setName(_constants.lineOpacityAttribute());
		Attribute.LINE_PATTERN.setName(_constants.linePatternAttribute());
		Attribute.LINE_CAP.setName(_constants.lineCapAttribute());
		Attribute.LINE_WIDTH.setName(_constants.lineWidthAttribute());
		Attribute.FILL_STYLE.setName(_constants.fillStyleAttribute());
		Attribute.FILL_OPACITY.setName(_constants.fillOpacityAttribute());

	    _lineStyleController = new StyleController(this, _constants.lineLabel(), Attribute.LINE_STYLE, Attribute.LINE_OPACITY, Color.BLACK, new FloatAttributeValue(1f));
		_fillStyleController = new StyleController(this, _constants.fillLabel(), Attribute.FILL_STYLE, Attribute.FILL_OPACITY, Color.fromRGB(0, 128, 255), new FloatAttributeValue(1f));
		_lineWidthController = new LineWidthController(this);
		_clipBoard = new ClipBoard();
		UndoController undoController = new UndoController(this);
		RedoController redoController = new RedoController(this);
		CopyController copyController = new CopyController(this);
		CutController cutController = new CutController(this);
		PasteController pasteController = new PasteController(this);
		GroupController groupController = new GroupController(this);
		UngroupController ungroupController = new UngroupController(this);
		DeleteController deleteController = new DeleteController(this);
		OrderingController orderingController = new OrderingController(this);
		ResizeController resizeController = new ResizeController(this, repController);
		
		FormPanel form = new FormPanel("export");
		ExportController exportController = new ExportController(this, form);
		
		ContextualMenuVisitor contextVisitor = new ContextualMenuVisitor(deleteController, ungroupController, groupController, orderingController, _view);
		
		SelectShapeController selectShapeController = new SelectShapeController(this, deleteController, contextVisitor);
		NewRectController newRectController = new NewRectController(this);
		NewEllipseController newEllipseController = new NewEllipseController(this);
		NewPolylineController newPolylineController = new NewPolylineController(this);
		EditPolylineController editPolylineController = new EditPolylineController(this);
		NewPathController newPathController = new NewPathController(this);
		EditPathController editPathController = new EditPathController(this);
		
		_selector.add(selectShapeController.getButton());
		_selector.add(newRectController.getButton());
		_selector.add(newEllipseController.getButton());
		_selector.add(newPolylineController.getButton());
		_selector.add(editPolylineController.getButton());
		_selector.add(newPathController.getButton());
		_selector.add(editPathController.getButton());
		_selector.selectController(newRectController.getButton());

		final Grid commandButtons = new Grid(5, 2);
		commandButtons.setWidget(0, 0, selectShapeController.getButton());
		commandButtons.setWidget(0, 1, repController.getSaveButton());
		commandButtons.setWidget(1, 0, newRectController.getButton());
		commandButtons.setWidget(1, 1, newEllipseController.getButton());
		commandButtons.setWidget(2, 0, newPolylineController.getButton());
		commandButtons.setWidget(2, 1, editPolylineController.getButton());
		commandButtons.setWidget(3, 0, newPathController.getButton());
		commandButtons.setWidget(3, 1, editPathController.getButton());
		commandButtons.setWidget(4, 0, undoController.getUndoButton());
		commandButtons.setWidget(4, 1, redoController.getRedoButton());

		final VerticalPanel toolBar = new VerticalPanel();
		toolBar.add(commandButtons);

		HorizontalPanel row1 = new HorizontalPanel();
		row1.add(toolBar);

		HorizontalPanel row2 = new HorizontalPanel();
	    row2.add(_lineStyleController.getWidget());
	    row2.add(_lineWidthController.getWidget());
	    row2.add(_fillStyleController.getWidget());

		
		VerticalPanel col1 = new VerticalPanel();
		col1.add(row2);
		col1.add(_view);
		row1.add(col1);
		//debugArea.setCharacterWidth(80);
		//debugArea.setVisibleLines(20);
		//row1.add(debugArea);
		
		VerticalPanel col2 = new VerticalPanel();
		MenuBar editMenu = new MenuBar(true);
		editMenu.addItem(_constants.resetViewCommand(), new Command() {
			public void execute() {
				_view.reset();
			}			
		});
		editMenu.addItem(undoController.getUndoMenuItem());
		editMenu.addItem(redoController.getRedoMenuItem());
		editMenu.addItem(cutController.getCutMenuItem());
		editMenu.addItem(copyController.getCopyMenuItem());
		editMenu.addItem(pasteController.getPasteMenuItem());
		editMenu.addItem(exportController.getExportSVG11Item());
		editMenu.addItem(exportController.getExportSVG12Item());
		MenuBar modifyMenu = new MenuBar(true);
		modifyMenu.addItem(groupController.getGroupMenuItem());
		modifyMenu.addItem(ungroupController.getUngroupMenuItem());
		modifyMenu.addItem(orderingController.newMenuItem(_view, ChangeOrderingCommand.BRING_TO_FRONT));
		modifyMenu.addItem(orderingController.newMenuItem(_view, ChangeOrderingCommand.SEND_TO_BACK));
		modifyMenu.addItem(orderingController.newMenuItem(_view, ChangeOrderingCommand.BRING_FORWARD));
		modifyMenu.addItem(orderingController.newMenuItem(_view, ChangeOrderingCommand.SEND_BACKWARD));
		modifyMenu.addItem(resizeController.getResizeMenuItem(_view));
		MenuBar helpMenu = new MenuBar(true);
		helpMenu.addItem(_constants.helpCommand() , new Command() {
			public void execute() {
			}			
		});
		final AboutPanel panel = new AboutPanel();
		helpMenu.addItem(_constants.aboutCommand() , new Command() {
			public void execute() {
				panel.show();
			}			
		});
		MenuBar menuBar = new MenuBar();
		menuBar.addItem(_constants.editMenu(), editMenu);
		menuBar.addItem(_constants.modifyMenu(), modifyMenu);
		menuBar.addItem(_constants.helpMenu(), helpMenu);
		col2.add(menuBar);
		col2.add(row1);
		col2.add(form);
		
		//EventPanel eventPanel = new EventPanel(_view);
		//eventPanel.add(col1);
		//RootPanel.get().add(eventPanel);
		RootPanel.get().add(col2);
		repController.open();
		RootPanel rootPanel = RootPanel.get();
		rootPanel.sinkEvents(0xFFFFFFFF);
		DOM.setEventListener(RootPanel.getBodyElement(), this);
		_view.render();
	}
	
	public void onBrowserEvent(Event event) {
		_view.render();
	}


}
