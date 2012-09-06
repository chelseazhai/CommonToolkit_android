package com.richitec.commontoolkit.user;

import java.io.Serializable;

public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9021309741995960929L;

	// user name
	private String name;
	// user password
	private String password;
	// user key
	private String userKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	@Override
	public String toString() {
		// init user description
		StringBuilder _userDescription = new StringBuilder();

		// append user name, password and userKey
		_userDescription.append("name: ").append(name).append(", ");
		_userDescription.append("password: ").append(password).append(", and ");
		_userDescription.append("userKey: ").append(userKey).append('\n');

		return _userDescription.toString();
	}

}
