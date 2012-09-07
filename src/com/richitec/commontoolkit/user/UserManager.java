package com.richitec.commontoolkit.user;

import com.richitec.commontoolkit.utils.StringUtils;

public class UserManager {

	// singleton instance
	private static volatile UserManager _singletonInstance;

	// user bean
	private UserBean _mUserBean;

	private UserManager() {
		// init user bean
		_mUserBean = new UserBean();
	}

	// get userManager singleton instance
	public static UserManager getInstance() {
		if (null == _singletonInstance) {
			synchronized (UserManager.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new UserManager();
				}
			}
		}

		return _singletonInstance;
	}

	// get user
	public UserBean getUser() {
		return _mUserBean;
	}

	// set user
	public UserBean setUser(UserBean pUser) {
		return _mUserBean = pUser;
	}

	// set user with user name and password
	public UserBean setUser(String pName, String pPassword) {
		// generator user digit key
		StringBuilder _digitKeyString = new StringBuilder(pName);
		_digitKeyString.append(pPassword);
		String _digitKey = StringUtils.md5(_digitKeyString.toString());

		// set user bean
		_mUserBean = null == _mUserBean ? new UserBean() : _mUserBean;
		_mUserBean.setName(pName);
		_mUserBean.setPassword(StringUtils.md5(pPassword));
		_mUserBean.setUserKey(_digitKey);

		return _mUserBean;
	}

	// update userKey
	public UserBean updateUserKey(String pUserKey) {
		if (null != _mUserBean) {
			_mUserBean.setUserKey(pUserKey);
		}

		return _mUserBean;
	}

	// remove the user
	public void removeUser() {
		_mUserBean = null;
	}

}
