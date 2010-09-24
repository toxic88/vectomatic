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
package org.vectomatic.svg.edit.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * NLS constants definition class
 * @author laaglu
 */
public interface AppConstants extends Constants {
	public static final AppConstants INSTANCE = GWT.create(AppConstants.class);
	public String fileMenu();
	public String openUrlMenuItem();
	public String openRssFeedMenuItem();
	public String recentDocumentsMenuItem();

	public String windowMenu();
	public String resetViewMenuItem();
	public String tileWindowsMenuItem();
	public String stackWindowsMenuItem();
	public String closeWindowMenuItem();

	public String aboutMenu();
	public String aboutMenuItem();
	
	public String aboutText();
	public String openUrlText();
	public String openButton();
	public String cancelButton();
	public String closeButton();

}
