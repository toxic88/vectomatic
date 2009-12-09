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
package org.vectomatic.common.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AccountServiceException extends Exception implements IsSerializable {
	private static final long serialVersionUID = 1L;
	/**
	 * The user enters an incorrect captcha
	 */
	public static final int CAPTCHA = 1;
	/**
	 * The requested account name is already user
	 */
	public static final int EXISTING_ACCOUNT = 2;
	/**
	 * The account number quota has been exceeded
	 */
	public static final int OUT_OF_RESOURCE = 3;
	/**
	 * Internal error
	 */
	public static final int SERVER_ERROR = 4;
	/**
	 * The account name does not match an email regexp
	 */
	public static final int INVALID_EMAIL = 5;
	/**
	 * The login does not exist or does match the password
	 */
	public static final int INVALID_LOGIN = 6;
	/**
	 * The account has not been activated
	 */
	public static final int NONACTIVATED_ACCOUNT = 7;
	/**
	 * The session has timed out
	 */
	public static final int SESSION_TIMEOUT = 8;
	/**
	 * The browser has not accepted the session cookie
	 */
	public static final int COOKIE_ERROR = 9;
	/**
	 * The user is not loggin in
	 */
	public static final int NOT_LOGGED_IN = 10;
	/**
	 * The EULA has not been approved
	 */
	public static final int EULA_NOT_APPROVED = 11;
	/**
	 * The account does not exist
	 */
	public static final int INEXISTING_ACCOUNT = 12;

	private int _id;
	public AccountServiceException() {
		super();
	}
	public AccountServiceException(int id) {
		this();
		_id = id;
	}
	public AccountServiceException(int id, Throwable cause) {
		super(cause);
		_id = id;
	}
	public int getId() {
		return _id;
	}
}
