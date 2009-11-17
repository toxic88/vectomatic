package com.alonsoruibal.chess;


public interface Config {
	
	public String getProperty(String name);

	public boolean getBoolean(String name);
	
	public boolean containsProperty(String name);
}
