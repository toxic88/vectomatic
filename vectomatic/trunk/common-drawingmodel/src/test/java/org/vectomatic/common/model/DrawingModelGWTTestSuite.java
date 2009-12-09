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
package org.vectomatic.common.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.vectomatic.common.model.geometry.BoundingBoxGWTTest;
import org.vectomatic.common.model.geometry.EllipseGWTTest;
import org.vectomatic.common.model.geometry.PointGWTTest;
import org.vectomatic.common.model.geometry.PolylineGWTTest;
import org.vectomatic.common.model.geometry.RectGWTTest;
import org.vectomatic.common.model.geometry.ShapeGroupGWTTest;
import org.vectomatic.common.model.geometry.TransformMatrixGWTTest;
import org.vectomatic.common.model.style.ColorGWTTest;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class DrawingModelGWTTestSuite extends GWTTestSuite {
	public static Test suite() {
	    TestSuite suite= new TestSuite("Vectomatic drawing model test suite");

	    suite.addTestSuite(DrawingModelGWTTest.class);
	    suite.addTestSuite(ShapeGWTTest.class);

	    suite.addTestSuite(BoundingBoxGWTTest.class);
	    suite.addTestSuite(EllipseGWTTest.class);
	    suite.addTestSuite(PointGWTTest.class);
	    suite.addTestSuite(PolylineGWTTest.class);
	    suite.addTestSuite(RectGWTTest.class);
	    suite.addTestSuite(ShapeGroupGWTTest.class);
	    suite.addTestSuite(TransformMatrixGWTTest.class);

	    suite.addTestSuite(ColorGWTTest.class);
	    return suite;
	}
}
