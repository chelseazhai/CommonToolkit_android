package com.richitec.commontoolkit.addressbook;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.utils.PinyinUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class AddressBookManager {

	private static final String LOG_TAG = "AddressBookManager";

	// singleton instance
	private static volatile AddressBookManager _singletonInstance;

	// contact sqlite query content resolver
	private ContentResolver _mContentResolver;

	// collator instance
	private Collator _mCollator;

	// all contacts detail info array
	private final List<ContactBean> _mAllContactsInfoArray = new ArrayList<ContactBean>();

	// all contacts detail info map. key: aggregated id and value: contacts
	// detail info
	private final Map<Long, ContactBean> _mAllContactsInfoMap = new HashMap<Long, ContactBean>();

	// all groups members map. key: group id and value: ownership contact
	// aggregated id list
	private final Map<Long, List<Long>> _mAllGroupsMembersMap = new HashMap<Long, List<Long>>();

	// contacts search result map. key: search keyword (String), value: array of
	// contact bean (ContactBean) and contact matching index array map
	private final Map<String, List<Map<String, Object>>> _mContactsSearchResultMap = new HashMap<String, List<Map<String, Object>>>();

	// matching result contact bean and index array key
	private final String MATCHING_RESULT_CONTACT = "matchingResultContact";
	private final String MATCHING_RESULT_INDEXES = "matchingResultIndexs";

	// contact name phonetic comparator
	public static final Comparator<ContactBean> CONTACTNAMEPHONETIC_COMPARATOR = new Comparator<ContactBean>() {

		@Override
		public int compare(ContactBean lhs, ContactBean rhs) {
			Integer _ret = -1;

			// get left and right compare contact name phonetics string
			String _leftContactNamePhoneticsString = lhs
					.getNamePhoneticsString();
			String _rightContactNamePhoneticsString = rhs
					.getNamePhoneticsString();

			// compare
			if (null == _leftContactNamePhoneticsString
					&& null == _rightContactNamePhoneticsString) {
				_ret = (int) (lhs.getId() - rhs.getId());
			} else if (null != _leftContactNamePhoneticsString
					&& null == _rightContactNamePhoneticsString) {
				_ret = 1;
			} else if (null != _leftContactNamePhoneticsString
					&& null != _rightContactNamePhoneticsString) {
				_ret = AddressBookManager
						.getInstance()
						.getCollator()
						.compare(_leftContactNamePhoneticsString,
								_rightContactNamePhoneticsString);
				// _ret = _leftContactNamePhoneticsString
				// .compareTo(_rightContactNamePhoneticsString);
			}

			return _ret;
		}
	};

	// contact phone number matching index array key
	public static final String PHONENUMBER_MATCHING_INDEXES = "phoneNumberMatchingIndexes";
	// contact name matching index array key
	public static final String NAME_MATCHING_INDEXES = "nameMatchingIndexes";

	// private constructor
	private AddressBookManager() {
		// init content resolver
		_mContentResolver = AppLaunchActivity.getAppContext()
				.getContentResolver();

		// init pinyin4j
		PinyinHelper.toHanyuPinyinStringArray(PinyinUtils.PINYINUTILS_INIT);

		// init collator instance
		_mCollator = Collator.getInstance();
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

	public Collator getCollator() {
		return _mCollator;
	}

	// get original all contacts detail info array
	public List<ContactBean> allContactsInfoArray() {
		return _mAllContactsInfoArray;
	}

	// traversal addressBook, important, do it first
	public void traversalAddressBook() {
		Log.d("AddressBookManager", "traversal addressBook");

		// get all contacts detail info
		Log.d(LOG_TAG, "getAllContactsDetailInfo - begin");
		getAllContactsDetailInfo();

		// sorted all contacts detail info array
		List<ContactBean> _newCopyArray = new ArrayList<ContactBean>();
		_newCopyArray.addAll(_mAllContactsInfoArray);
		Collections.sort(_newCopyArray, CONTACTNAMEPHONETIC_COMPARATOR);

		Log.d(LOG_TAG, "getAllContactsDetailInfo - end");
	}

	// get all contacts detail info
	private void getAllContactsDetailInfo() {
		// complete contacts detail info step by step
		getAllContactsId7DisplayName();
		getAllContactsRawIds();
		getAllContactsStructuredName();
		getAllContactsPhoneNumbers();
		getAllContactsGroups();
		getAllContactsPhoto();
	}

	// get all contact aggregated id and displayName
	private void getAllContactsId7DisplayName() {
		// define constant
		final String[] _projection = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME };

		// use contentResolver to query contacts table
		Cursor _contactCursor = _mContentResolver.query(Contacts.CONTENT_URI,
				_projection, null, null, null);

		// check contact cursor and traverse result
		if (null != _contactCursor) {
			while (_contactCursor.moveToNext()) {
				// get aggregated id and display name
				Long _aggregatedId = _contactCursor.getLong(_contactCursor
						.getColumnIndex(Contacts._ID));
				String _displayName = _contactCursor.getString(_contactCursor
						.getColumnIndex(Contacts.DISPLAY_NAME));

				// Log.d(LOG_TAG,
				// "getAllContactsId7DisplayName - aggregatedId: "
				// + _aggregatedId + " and displayName: " + _displayName);

				// check contact has been existed in all contacts detail info
				// map
				if (!_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// new contact bean
					ContactBean _contact = new ContactBean();

					// set aggregated id
					_contact.setId(_aggregatedId);
					// set displayName
					_contact.setDisplayName(_displayName);

					// add to all contacts detail info map
					_mAllContactsInfoMap.put(_aggregatedId, _contact);

					// add to all contacts detail info array
					_mAllContactsInfoArray.add(_contact);
				}
			}

			// close contact cursor
			_contactCursor.close();
		}
	}

	// get all contact raw ids
	private void getAllContactsRawIds() {
		// define constant
		final String[] _projection = new String[] { RawContacts._ID,
				RawContacts.CONTACT_ID, RawContacts.ACCOUNT_NAME };

		// use contentResolver to query raw_contacts table
		Cursor _rawIdsCursor = _mContentResolver.query(RawContacts.CONTENT_URI,
				_projection, null, null, null);

		// check rawId cursor and traverse result
		if (null != _rawIdsCursor) {
			while (_rawIdsCursor.moveToNext()) {
				// get aggregated id, raw id and raw contact ownership account
				// name
				Long _aggregatedId = _rawIdsCursor.getLong(_rawIdsCursor
						.getColumnIndex(RawContacts.CONTACT_ID));
				Long _rawId = _rawIdsCursor.getLong(_rawIdsCursor
						.getColumnIndex(RawContacts._ID));
				String _ownershipAccountName = _rawIdsCursor
						.getString(_rawIdsCursor
								.getColumnIndex(RawContacts.ACCOUNT_NAME));

				// Log.d(LOG_TAG, "getAllContactsRawIds - aggregatedId: "
				// + _aggregatedId + " , rawId: " + _rawId
				// + " and ownership account name: "
				// + _ownershipAccountName);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// check contact raw ids map
					if (null == _contact.getRawIds()) {
						// generate rawIds map and put rawId and ownership
						// account name to it
						Map<Long, String> _rawIdsMap = new HashMap<Long, String>();
						_rawIdsMap.put(_rawId, _ownershipAccountName);

						// set contact rawIds map
						_contact.setRawIds(_rawIdsMap);
					} else {
						// put rawId and ownership account name to rawIds map
						_contact.getRawIds().put(_rawId, _ownershipAccountName);
					}
				}
			}

			// close rawIds cursor
			_rawIdsCursor.close();
		}
	}

	// get all contact structured name
	private void getAllContactsStructuredName() {
		// define constant
		final String[] _projection = new String[] { StructuredName.CONTACT_ID,
				StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { StructuredName.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _nameCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check name cursor and traverse result
		if (null != _nameCursor) {
			while (_nameCursor.moveToNext()) {
				// get aggregated id, given name and family name
				Long _aggregatedId = _nameCursor.getLong(_nameCursor
						.getColumnIndex(StructuredName.CONTACT_ID));
				String _givenName = _nameCursor.getString(_nameCursor
						.getColumnIndex(StructuredName.GIVEN_NAME));
				String _familyName = _nameCursor.getString(_nameCursor
						.getColumnIndex(StructuredName.FAMILY_NAME));

				// Log.d(LOG_TAG,
				// "getAllContactsStructuredName - aggregatedId: "
				// + _aggregatedId + " , given name: " + _givenName
				// + " and family name: " + _familyName);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// check contact full name list
					if (null == _contact.getFullNames()) {
						// generate full name list, put given name and family
						// name to it and generate name phonetics
						List<String> _fullNamesList = new ArrayList<String>();
						List<List<String>> _namePhoneticsList = new ArrayList<List<String>>();
						if (null != _familyName) {
							_fullNamesList.addAll(StringUtils
									.toStringList(_familyName));

							_namePhoneticsList.addAll(PinyinUtils
									.pinyins4String(_familyName));
						}
						if (null != _givenName) {
							_fullNamesList.addAll(StringUtils
									.toStringList(_givenName));

							_namePhoneticsList.addAll(PinyinUtils
									.pinyins4String(_givenName));
						}

						// set contact full names list and name phonetics if
						// have
						if (0 != _fullNamesList.size()) {
							_contact.setFullNames(_fullNamesList);
						}
						if (0 != _namePhoneticsList.size()) {
							_contact.setNamePhonetics(_namePhoneticsList);
						}
					}
				}
			}

			// close name cursor
			_nameCursor.close();
		}
	}

	// get all contact phone numbers
	private void getAllContactsPhoneNumbers() {
		// define constant
		final String[] _projection = new String[] { Phone.CONTACT_ID,
				Phone.NUMBER };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { Phone.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _phoneCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check phone cursor and traverse result
		if (null != _phoneCursor) {
			while (_phoneCursor.moveToNext()) {
				// get aggregated id and phone number
				Long _aggregatedId = _phoneCursor.getLong(_phoneCursor
						.getColumnIndex(Phone.CONTACT_ID));
				String _phoneNumber = _phoneCursor.getString(_phoneCursor
						.getColumnIndex(Phone.NUMBER));

				// Log.d(LOG_TAG,
				// "getAllContactsPhoneNumbers - aggregated id = "
				// + _aggregatedId + " and phone number = " + _phoneNumber);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// get the contact
					ContactBean _contact = _mAllContactsInfoMap
							.get(_aggregatedId);

					// check contact phone number list
					if (null == _contact.getPhoneNumbers()) {
						// generate phone number list and add phone number to it
						List<String> _phoneNumbers = new ArrayList<String>();
						_phoneNumbers.add(_phoneNumber);

						// set contact phone numbers list
						_contact.setPhoneNumbers(_phoneNumbers);
					} else {
						// add phone number to phone number list
						_contact.getPhoneNumbers().add(_phoneNumber);
					}
				}
			}

			// close phone cursor
			_phoneCursor.close();
		}
	}

	// get all contact groups
	private void getAllContactsGroups() {
		// define constant
		final String[] _projection = new String[] { GroupMembership.CONTACT_ID,
				GroupMembership.GROUP_ROW_ID };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { GroupMembership.CONTENT_ITEM_TYPE };
		final String[] _groupProjection = new String[] { Groups._ID,
				Groups.TITLE };

		// use contentResolver to query data table
		Cursor _groupsCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check groups cursor and traverse result
		if (null != _groupsCursor) {
			while (_groupsCursor.moveToNext()) {
				// get aggregated id and group row id
				Long _aggregatedId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(GroupMembership.CONTACT_ID));
				Long _groupRowId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(GroupMembership.GROUP_ROW_ID));

				// Log.d(LOG_TAG, "getAllContactsGroups - aggregated id = "
				// + _aggregatedId + " and group row id = " + _groupRowId);

				// check group has been existed in all group members map
				if (_mAllGroupsMembersMap.containsKey(_groupRowId)) {
					// put the contact aggregated id to contacts aggregated ids
					// list
					_mAllGroupsMembersMap.get(_groupRowId).add(_aggregatedId);
				} else {
					// generate member contacts aggregated ids list and put
					// contact aggregated id to it
					List<Long> _membersAggregatedIdsList = new ArrayList<Long>();
					_membersAggregatedIdsList.add(_aggregatedId);

					// add member contacts aggregated ids list to all groups
					// members map
					_mAllGroupsMembersMap.put(_groupRowId,
							_membersAggregatedIdsList);
				}
			}

			// close groups cursor
			_groupsCursor.close();
		}

		// use contentResolver to query groups table
		_groupsCursor = _mContentResolver.query(Groups.CONTENT_URI,
				_groupProjection, null, null, null);

		// check groups cursor and traverse result
		if (null != _groupsCursor) {
			while (_groupsCursor.moveToNext()) {
				// get group row id and title
				Long _groupRowId = _groupsCursor.getLong(_groupsCursor
						.getColumnIndex(Groups._ID));
				String _groupTitle = _groupsCursor.getString(_groupsCursor
						.getColumnIndex(Groups.TITLE));

				// Log.d(LOG_TAG, "getAllContactsGroups - group row id = "
				// + _groupRowId + " and group title = " + _groupTitle);

				// check group has been existed in all group members map
				if (_mAllGroupsMembersMap.containsKey(_groupRowId)) {
					// get members contacts aggregated ids list
					List<Long> _membersAggregatedIdsList = _mAllGroupsMembersMap
							.get(_groupRowId);

					// add group title to members contacts groups list
					for (Long _membersAggregatedId : _membersAggregatedIdsList) {
						// get contact from all contacts info map
						if (_mAllContactsInfoMap
								.containsKey(_membersAggregatedId)) {
							// get the contact
							ContactBean _contact = _mAllContactsInfoMap
									.get(_membersAggregatedId);

							// get contact group list
							if (null == _contact.getGroups()) {
								// generate contact group list and put group
								// title to it
								List<String> _groupsList = new ArrayList<String>();
								_groupsList.add(_groupTitle);

								// set contact group list
								_contact.setGroups(_groupsList);
							} else {
								// put group title to member contact group list
								_contact.getGroups().add(_groupTitle);
							}
						}
					}
				}
			}

			// close group cursor
			_groupsCursor.close();
		}
	}

	// get all contact photo
	private void getAllContactsPhoto() {
		// define constant
		final String[] _projection = new String[] { Photo.CONTACT_ID,
				Photo.PHOTO };
		final String _selection = Data.MIMETYPE + "=?";
		final String[] _selectionArgs = new String[] { Photo.CONTENT_ITEM_TYPE };

		// use contentResolver to query data table
		Cursor _photoCursor = _mContentResolver.query(Data.CONTENT_URI,
				_projection, _selection, _selectionArgs, null);

		// check photo cursor and traverse result
		if (null != _photoCursor) {
			while (_photoCursor.moveToNext()) {
				// get aggregated id and photo
				Long _aggregatedId = _photoCursor.getLong(_photoCursor
						.getColumnIndex(Photo.CONTACT_ID));
				byte[] _photoData = _photoCursor.getBlob(_photoCursor
						.getColumnIndex(Photo.PHOTO));

				// Log.d(LOG_TAG, "getAllContactsPhoto - aggregated id = "
				// + _aggregatedId + " and photo string = " + _photoData);

				// check contact has been existed in all contacts detail info
				// map
				if (_mAllContactsInfoMap.containsKey(_aggregatedId)) {
					// set contact photo
					_mAllContactsInfoMap.get(_aggregatedId)
							.setPhoto(_photoData);
				}
			}

			// close photo cursor
			_photoCursor.close();
		}
	}

	// get contact bean object by aggregated id
	public ContactBean getContactByAggregatedId(Long aggregatedId) {
		ContactBean _contact = null;

		if (_mAllContactsInfoMap.containsKey(aggregatedId)) {
			_contact = _mAllContactsInfoMap.get(aggregatedId);
		}

		return _contact;
	}

	// get contacts display name list by given phone number
	public List<String> getContactsDisplayNamesByPhone(String phoneNumber) {
		List<String> _displayNames = new ArrayList<String>();

		// traversal all contacts detail info array
		for (ContactBean _contact : _mAllContactsInfoArray) {
			// get contact phone numbers list
			List<String> _contactPhoneNumbers = _contact.getPhoneNumbers();

			// check the contact phone numbers
			if (null != _contactPhoneNumbers
					&& 0 != _contactPhoneNumbers.size()
					&& _contactPhoneNumbers.contains(phoneNumber)) {
				_displayNames.add(_contact.getDisplayName());
			}
		}

		// check return display names list
		if (0 == _displayNames.size()) {
			_displayNames.add(phoneNumber);
		}

		return _displayNames;
	}

	// get contacts list by phone number with sorted type
	public List<ContactBean> getContactsByPhone(String phoneNumber,
			ContactSortedType sortedType) {
		List<ContactBean> _searchedContacts = new ArrayList<ContactBean>();

		// check all contacts detail info array
		if (0 == _mAllContactsInfoArray.size()) {
			getAllContactsDetailInfo();
		}

		// check contacts search phone number
		if (_mContactsSearchResultMap.containsKey(phoneNumber)) {
			for (Map<String, Object> _resultMap : _mContactsSearchResultMap
					.get(phoneNumber)) {
				// get matching result contact
				ContactBean _contact = (ContactBean) _resultMap
						.get(MATCHING_RESULT_CONTACT);

				// add contact to searched result list and reset its phone
				// matching indexes
				_searchedContacts.add(_contact);
				_contact.getExtension().put(PHONENUMBER_MATCHING_INDEXES,
						_resultMap.get(MATCHING_RESULT_INDEXES));
			}
		} else {
			// define contacts search scope
			List<ContactBean> _searchScope = _mAllContactsInfoArray;

			// check search scope and reset scope
			if (phoneNumber.length() >= 2
					&& _mContactsSearchResultMap.keySet().contains(
							phoneNumber.substring(0, phoneNumber.length()))) {
				_searchScope = new ArrayList<ContactBean>();
				for (Map<String, Object> _resultMap : _mContactsSearchResultMap
						.get(phoneNumber.substring(0, phoneNumber.length()))) {
					_searchScope.add((ContactBean) _resultMap
							.get(MATCHING_RESULT_CONTACT));
				}
			}

			// define contacts searched results list
			List<Map<String, Object>> _contactsSearchedResults = new ArrayList<Map<String, Object>>();

			// search in scope
			for (ContactBean _contact : _searchScope) {
				// phone number matching indexe's array array
				List<List<Integer>> _phoneNumberMatchingIndexesList = new ArrayList<List<Integer>>();

				// has phone number matched flag
				boolean _hasOnePhoneNumberMatched = false;

				// get the contact in search scope phone numbers list
				List<String> _contactPhones = _contact.getPhoneNumbers();
				// traversal the contact in search scope each phone number
				if (null != _contactPhones) {
					for (String _contactPhone : _contactPhones) {
						if (_contactPhone.contains(phoneNumber)) {
							_hasOnePhoneNumberMatched = true;

							// add phone number matching index array to phone
							// number matching index array's array
							_phoneNumberMatchingIndexesList
									.add(generateIntRangeList(
											_contactPhone.indexOf(phoneNumber),
											phoneNumber.length()));
						} else {
							// add empty matching index array to phone number
							// matching index array's array
							_phoneNumberMatchingIndexesList
									.add(new ArrayList<Integer>());
						}
					}
				}

				// has one phone number in contact matched the phone number
				// parameter
				if (_hasOnePhoneNumberMatched) {
					// add contact to result
					_searchedContacts.add(_contact);

					// append contact matching indexes array
					_contact.getExtension().put(PHONENUMBER_MATCHING_INDEXES,
							_phoneNumberMatchingIndexesList);

					// generate contact searched result and add it to searched
					// contact array
					Map<String, Object> _contactsSearchedResult = new HashMap<String, Object>();
					_contactsSearchedResult.put(MATCHING_RESULT_CONTACT,
							_contact);
					_contactsSearchedResult.put(MATCHING_RESULT_INDEXES,
							_phoneNumberMatchingIndexesList);

					_contactsSearchedResults.add(_contactsSearchedResult);
				}
			}

			// add contact searched results to contacts search result map
			_mContactsSearchResultMap
					.put(phoneNumber, _contactsSearchedResults);
		}

		// check sorted type
		if (ContactSortedType.PHONETICS == sortedType) {
			Collections.sort(_searchedContacts, CONTACTNAMEPHONETIC_COMPARATOR);
		}

		return _searchedContacts;
	}

	// get contacts list by phone number: sub matching
	public List<ContactBean> getContactsByPhone(String phoneNumber) {
		return this
				.getContactsByPhone(phoneNumber, ContactSortedType.PHONETICS);
	}

	// get contacts list by name with matching type and sorted type
	public List<ContactBean> getContactsByName(String name,
			ContactNameMatchingType nameMatchingType,
			ContactSortedType sortedType) {
		List<ContactBean> _searchedContacts = new ArrayList<ContactBean>();

		// name to lower case
		name = name.toLowerCase();

		// check all contacts detail info array
		if (0 == _mAllContactsInfoArray.size()) {
			getAllContactsDetailInfo();
		}

		// check contacts search name
		if (_mContactsSearchResultMap.containsKey(name)) {
			for (Map<String, Object> _resultMap : _mContactsSearchResultMap
					.get(name)) {
				// get matching result contact
				ContactBean _contact = (ContactBean) _resultMap
						.get(MATCHING_RESULT_CONTACT);

				// add contact to searched result list and reset its name
				// matching indexes
				_searchedContacts.add(_contact);
				_contact.getExtension().put(NAME_MATCHING_INDEXES,
						_resultMap.get(MATCHING_RESULT_INDEXES));
			}
		} else {
			// define contacts search scope
			List<ContactBean> _searchScope = _mAllContactsInfoArray;

			// check search scope and reset scope
			if (name.length() >= 2
					&& _mContactsSearchResultMap.keySet().contains(
							name.substring(0, name.length()))) {
				_searchScope = new ArrayList<ContactBean>();
				for (Map<String, Object> _resultMap : _mContactsSearchResultMap
						.get(name.substring(0, name.length()))) {
					_searchScope.add((ContactBean) _resultMap
							.get(MATCHING_RESULT_CONTACT));
				}
			}

			// define contacts searched results list
			List<Map<String, Object>> _contactsSearchedResults = new ArrayList<Map<String, Object>>();

			// split contact search name
			List<String> _ContactSearchNameSplitList = null;
			if (_searchScope.size() >= 1) {
				// init contact search name split array
				_ContactSearchNameSplitList = null;
			}

			// search in scope
			for (ContactBean _contact : _searchScope) {
				// traversal all search name split array
				for (String _splitString : _ContactSearchNameSplitList) {
					// split name unmatch flag
					boolean _splitNameUnmatch = false;

					// name matching indexe's array array
					List<Object> _nameMatchingIndexesList = new ArrayList<Object>();

					//
				}

				//
			}

			//
		}

		return _searchedContacts;
	}

	// get contacts list by name(not Chinese character): fuzzy matching
	public List<ContactBean> getContactsByName(String name) {
		return this.getContactsByName(name, ContactNameMatchingType.FUZZY,
				ContactSortedType.PHONETICS);
	}

	// get contact end
	public void getContactEnd() {
		// clear contacts search result map
		_mContactsSearchResultMap.clear();
	}

	// generate integer range array list with location and length
	private List<Integer> generateIntRangeList(Integer location, Integer length) {
		List<Integer> _intRangeList = new ArrayList<Integer>();

		if (length >= 1) {
			for (int i = 0; i < length; i++) {
				_intRangeList.add(location + i);
			}
		}

		return _intRangeList;
	}

	// inner class
	// contact searched sorted type
	public static enum ContactSortedType {
		IDENTITY, PHONETICS
	}

	// contact searched name matching type
	public static enum ContactNameMatchingType {
		FUZZY, ORDER
	}

}
