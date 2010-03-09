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

import com.google.gwt.i18n.client.Messages;

/**
 * GWT NLS message
 */
public interface UIMessages extends Messages {

	public String retry();

	public String diagQuotaExceeded();
	public String diagDrawingDoesNotExist();
	public String diagDrawingBadFormat();
	public String diagServerProblem();
	public String diagCommunicationProblem();
	
	public String errorGetDrawings();
	public String errorNew();
	public String errorDuplicate(String drawingName);
	public String errorRename(String oldName, String newName);
	public String errorDelete(String drawingNames);
	public String errorPublish(String drawingName);
	public String errorExport(String drawingName);
	public String errorImport(String fileName);
	public String errorSave();
	public String errorOpen();
	public String warningCloseUnsaved(String fileName);

	public String copyOf(String drawingName);

	
	public String confirmTitle();
	public String deleteDrawingConfirm(String drawingNames);
	public String drawingNameCol();
	public String lastModifiedCol();
	public String publishUriCol();
	
	public String availableDocumentsTitle();
	public String errorTitle();
	
	public String newButton();
	public String duplicateButton();
	public String deleteButton();
	public String renameButton();
	
	public String otherActionsButton();
	public String publishItem();
	public String unpublishItem();
	public String exportItem();
	public String toSvg11FormatItem();
	public String toSvg12FormatItem();
	public String toNativeFormatItem();
	public String importItem();
	public String fromNativeFormatItem();
	public String zippedFolder();
	
	public String importWindowTitle();
	public String fileField();
	public String importButton();
	public String cancelButton();
	public String importLabel();
	public String importWait();
	
	public String untitled();
	public String badExtensionMessage();
	public String badLengthMessage();

}
