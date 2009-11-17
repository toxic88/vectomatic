package com.alonsoruibal.chess;

import java.util.ResourceBundle;

public class TestConfig implements Config {

	private ResourceBundle config = ResourceBundle.getBundle("chess"); 
	
	public String getProperty(String name) {
		return config.getString(name);
	}

	public boolean getBoolean(String name) {
		if (!config.containsKey(name)) return false;
		String value = config.getString(name);
		return "1".equals(value) || "true".equals(value);
	}
	
	public boolean containsProperty(String name) {
		return config.containsKey(name);
	}

}
