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
package org.vectomatic.client;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.vectomatic.client.command.CommandHistoryGWTTest;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class DrawingAppGWTTestSuite extends GWTTestSuite {
	public static Test suite() {
	    TestSuite suite= new TestSuite("Vectomatic drawing application test suite");

	    suite.addTestSuite(CommandHistoryGWTTest.class);
	    return suite;
	}
}
