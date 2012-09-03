package com.richitec.commontoolkit.user;

import java.io.Serializable;

public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9021309741995960929L;

	// user name
	private String _mName;
	// user password
	private String _mPassword;
	// user key
	private String _mUserKey;

	public String name() {
		return _mName;
	}

	public void setName(String pName) {
		_mName = pName;
	}

	public String password() {
		return _mPassword;
	}

	public void setPassword(String pPassword) {
		_mPassword = pPassword;
	}

	public String userKey() {
		return _mUserKey;
	}

	public void setUserKey(String pUserKey) {
		_mUserKey = pUserKey;
	}

}
