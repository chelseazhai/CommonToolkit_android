package com.richitec.commontoolkit.addressbook;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class AddressBookManager {

	// singleton instance
	private static volatile AddressBookManager _singletonInstance;
	
	// content context
	Context _mContext;

	// all contacts info array
	private ArrayList<ContactBean> _mAllContactsInfoArray;

	// private constructor
	private AddressBookManager() {
		Log.d("AddressBookManager", "private constructor");

		//
	}

	// get addressBookManager singleton instance
	public static AddressBookManager getInstance() {
		if (null == _singletonInstance) {
			synchronized (AddressBookManager.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new AddressBookManager();
				}
			}
		}

		return _singletonInstance;
	}

	public ArrayList<ContactBean> allContactsInfoArray() {
		return _mAllContactsInfoArray;
	}

	// traversal addressBook, important, do it first
	public void traversalAddressBook() {
		Log.d("AddressBookManager", "traversal addressBook");

		// use contentResolver to get all contacts information
		_mContext.getContentResolver();
	}

}
