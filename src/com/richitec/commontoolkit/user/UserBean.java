package com.richitec.commontoolkit.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

	// extension map
	private Map<String, Object> extension;

	// UserBean constructor
	public UserBean() {
		super();

		// init extension map
		extension = new HashMap<String, Object>();
	}

	public UserBean(String name, String password, String userKey) {
		// set name, password and user key
		this.name = name;
		this.password = password;
		this.userKey = userKey;

		// init extension map
		extension = new HashMap<String, Object>();
	}

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

	public Map<String, Object> getExtension() {
		return extension;
	}

	public void setExtension(Map<String, Object> extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		// init user description
		StringBuilder _userDescription = new StringBuilder();

		// append user name, password and userKey
		_userDescription.append("user name: ").append(name).append(", ");
		_userDescription.append("password: ").append(password).append(", ");
		_userDescription.append("userKey: ").append(userKey).append(" and ");
		_userDescription.append("extension: ").append(extension).append("\n");

		return _userDescription.toString();
	}

}
