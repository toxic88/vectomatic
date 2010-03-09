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
package org.vectomatic.client;

import com.google.gwt.i18n.client.Constants;

/**
 * GWT NLS constants
 */
public interface UIConstants extends Constants {
	String windowTitle();
	
	String selectCommand();
	String translateShapeCommand();
	String scaleShapeCommand();
	String rotateShapeCommand();
	String newRectCommand();
	String newEllipseCommand();
	String newPolylineCommand();
	String editPolylineCommand();
	String newPathCommand();
	String editPathCommand();
	String insertVertexInPathCommand();
	String removeVertexFromPathCommand();
	String makeSegmentALineCommand();
	String makeSegmentASplineCommand();
	String cutPathAtSegmentCommand();
	String breakPathAtVertexCommand();
	String joinVertexWithSegmentCommand();
	String joinVertexCommand();
	String associateTangentsCommand();
	String dissociateTangentsCommand();
	String makeTangentsSymetricCommand();
	String endPathEdition();
	String undoCommand();
	String redoCommand();
	String groupCommand();
	String ungroupCommand();
	String saveCommand();
	String resetViewCommand();
	String copyCommand();
	String cutCommand();
	String pasteCommand();
	String deleteShapeCommand();
	String setAttributeCommand();
	String bringToFrontCommand();
	String sendToBackCommand();
	String bringForwardCommand();
	String sendBackwardCommand();
	String resizeDrawingCommand();
	String exportSVG11Command();
	String exportSVG12Command();
	String moveVertexCommand();
	String deleteVertexCommand();
	String helpCommand();
	String aboutCommand();
	
	String rectangleBaseName();
	String ellipseBaseName();
	String polylineBaseName();
	String shapeGroupBaseName();
	String editMenu();
	String modifyMenu();
	String helpMenu();
	
	String colorEditorPanel();
	String okButton();
	String cancelButton();
	String gradientsTab();
	String valuesTab();

	String paletteEditorPanel();
	String newPaletteButton();
	String clonePaletteButton();
	String deletePaletteButton();
	String editColorButton();
	String newColorButton();
	String deleteColorButton();
	String closeButton();
	String paletteNameLabel();
	String paletteBaseName();
	String copyOf();

	String editPaletteButton();
	String pickColorButton();
	String transparencyLabel();

	String lineLabel();
	String fillLabel();

	String plainItem();
	String gradientItem();
	String patternItem();
	String noneItem();
	
	String lineStyleAttribute();
	String lineOpacityAttribute();
	String linePatternAttribute();
	String lineCapAttribute();
	String lineWidthAttribute();
	String fillStyleAttribute();
	String fillOpacityAttribute();
	
	String warning();
	String error();
	
	String lineWidthEditor();
	String editLineWidth();
	
	String dimensionsPanel();
	String widthLabel();
	String heightLabel();

	String aboutPanel();
	String aboutLabel1();
	String aboutLabel2();
	String aboutLabel3();
	String aboutLabel4();
	String aboutLabel5();
}

