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
package org.vectomatic.common.model.geometry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;



public class PolylineTest extends PolylineGWTTest {
	@Override
	public void runBare() throws Throwable {
		Throwable exception = null;
		gwtSetUp();
		try {
			runTest2();
		} catch (Throwable running) {
			exception = running;
		} finally {
			try {
				gwtTearDown();
			} catch (Throwable tearingDown) {
				if (exception == null) {
					exception = tearingDown;
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
		return;
	}

	protected void runTest2() throws Throwable {
		assertNotNull(getName());
		Method runMethod = null;
		try {
			runMethod = getClass().getMethod(getName(), (Class[]) null);
		} catch (NoSuchMethodException e) {
			fail("Method \"" + getName() + "\" not found");
		}
		if (!Modifier.isPublic(runMethod.getModifiers())) {
			fail("Method \"" + getName() + "\" should be public");
		}
		try {
			runMethod.invoke(this, (Object[]) new Class[0]);
		} catch (InvocationTargetException e) {
			e.fillInStackTrace();
			throw e.getTargetException();
		} catch (IllegalAccessException e) {
			e.fillInStackTrace();
			throw e;
		}
	}

}
