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
package org.vectomatic.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GAE-based servlet to echo with the proper content-type the
 * SVG posted by a Vectomatic client
 */
public class ExportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ExportServlet.class.getName());

    /**
     * Default constructor. 
     */
    public ExportServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("host:" + request.getRemoteHost() + " address:" + request.getRemoteAddr() + " user:" + request.getRemoteUser());
		response.setContentType("text/html");
		response.getWriter().println("<html><head></head><body><p>ExportServlet alive</p></body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("host:" + request.getRemoteHost() + " address:" + request.getRemoteAddr() + " user:" + request.getRemoteUser());
		response.setContentType("image/svg+xml");
		BufferedInputStream input = new BufferedInputStream(request.getInputStream());
		BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
		char[] paramName = {'d', 'a', 't', 'a','='};
		int c, count = 0;
		while ((c = input.read()) != -1) {
			if (paramName[count] == c) {
				count++;
				if (count == paramName.length) {
					break;
				}
			} else {
				count = 0;
			}
		}
		if (c == -1) {
			throw new IOException("No data parameter found");
		}
		while ((c = input.read()) != -1) {
			switch(c) {
				case '+':
					output.write(' ');
					break;
				case '%':
					int h = fromBase16(input.read());
					int l = fromBase16(input.read());
					output.write((byte)(h * 16 + l));
					break;
				default:
					output.write((byte)c);
			}
		}
		output.flush();
	}
	
	public int fromBase16(int i) throws IOException {
		if (i >= '0' && i <= '9') {
			return i - '0';
		} else if (i >= 'A' && i <= 'F') {
			return 10 + i - 'A';
		} else {
			throw new IOException("Encoding error: " + i);
		}
	}
}
