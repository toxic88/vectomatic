/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of carballo-gwt.
 * 
 * carballo-gwt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * carballo-gwt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with carballo-gwt.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package com.alonsoruibal.chess;

import java.util.HashMap;
import java.util.Map;

public class StaticConfig implements Config {

	//private static ResourceBundle config = ResourceBundle.getBundle("chess"); 
	private Map<String, String> config;
	public StaticConfig() {
		config = new HashMap<String, String>();
		config.put("book", "/book_small.json");

		// Evaluator
		config.put("evaluator", "complete");
		//valuator=completenew
		//evaluator=simplified

		// Search configuration
		//search.negascout=true
		config.put("search.nullmove", "true");
		config.put("search.iid", "true");
		config.put("search.extensions", "true");
		config.put("search.lmr", "true");
		//search.futility=true
		//search.futility.margin=300
		//search.aggressiveFutility=true
		//search.aggressiveFutility.margin=500

		// Aspiration window and size in centipawns
		config.put("aspirationWindow", "true");
		config.put("aspirationWindow.size", "10");

		// Size of the transposition table, in MB
		//config.put("transpositionTable.size", "256");
		config.put("transpositionTable.size", "256");
	}
	
	public String getProperty(String name) {
		//return config.getString(name);
		return config.get(name);
	}

	public boolean getBoolean(String name) {
		if (!config.containsKey(name)) return false;
		//String value = config.getString(name);
		String value = config.get(name);
		return "1".equals(value) || "true".equals(value);
	}
	
	public boolean containsProperty(String name) {
		return config.containsKey(name);
	}
}
