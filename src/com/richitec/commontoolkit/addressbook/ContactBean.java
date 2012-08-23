package com.richitec.commontoolkit.addressbook;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836226421304538227L;

	// id
	private Integer _mId;
	// groups
	private ArrayList<String> _mGroups;
	// display name
	private String _mDisplayName;
	// full names
	private ArrayList<String> _mFullNames;
	// name phonetics
	private ArrayList<String[]> _mNamePhonetics;
	// phone numbers
	private ArrayList<String> _mPhoneNumbers;
	// photo
	private Byte[] _mPhoto;

	public Integer id() {
		return _mId;
	}

	public void setId(Integer pId) {
		_mId = pId;
	}

	public ArrayList<String> groups() {
		return _mGroups;
	}

	public void setGroups(ArrayList<String> pGroups) {
		_mGroups = pGroups;
	}

	public String displayName() {
		return _mDisplayName;
	}

	public void setDisplayName(String pDisplayName) {
		_mDisplayName = pDisplayName;
	}

	public ArrayList<String> fullNames() {
		return _mFullNames;
	}

	public void setFullNames(ArrayList<String> pFullNames) {
		_mFullNames = pFullNames;
	}

	public ArrayList<String[]> namePhonetics() {
		return _mNamePhonetics;
	}

	public void setNamePhonetics(ArrayList<String[]> pNamePhonetics) {
		_mNamePhonetics = pNamePhonetics;
	}

	public ArrayList<String> phoneNumbers() {
		return _mPhoneNumbers;
	}

	public void setPhoneNumbers(ArrayList<String> pPhoneNumbers) {
		_mPhoneNumbers = pPhoneNumbers;
	}

	public Byte[] photo() {
		return _mPhoto;
	}

	public void setPhoto(Byte[] pPhoto) {
		_mPhoto = pPhoto;
	}

}
