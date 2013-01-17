package com.richitec.commontoolkit.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class VersionUtils {

	private static final String LOG_TAG = "VersionUtils";

	// get current version code
	public static Integer versionCode() {
		// define current version code
		Integer _currentVersionCode = -1;

		// get application context
		Context _appContext = CTApplication.getContext();

		try {
			_currentVersionCode = _appContext.getPackageManager()
					.getPackageInfo(_appContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return _currentVersionCode;
	}

	// get current version name
	public static String versionName() {
		// define current version name
		String _currentVersionName = "";

		// get application context
		Context _appContext = CTApplication.getContext();

		try {
			_currentVersionName = _appContext.getPackageManager()
					.getPackageInfo(_appContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return _currentVersionName;
	}

	// compare version name
	public static int compareVersionName(String lhs, String rhs)
			throws VersionCompareException {
		// define return result
		int _ret = 0;

		// version name split word
		final String VERSIONNAME_SPLITWORD = ".";

		// check left and right handle side version name
		if (null == lhs || null == rhs
				|| ("".equalsIgnoreCase(lhs) && "".equalsIgnoreCase(rhs))) {
			Log.e(LOG_TAG,
					"compare version name unnecessary, left version name = "
							+ lhs + " and right version name = " + rhs);

			throw new VersionCompareException(
					"unnecessary to compare, left handside = " + lhs
							+ " and right handside = " + rhs);
		}

		// get left and right handle side version name string list
		@SuppressWarnings("unchecked")
		List<String> _lhsIntList = (List<String>) CommonUtils
				.array2List(StringUtils.split(lhs, VERSIONNAME_SPLITWORD));
		@SuppressWarnings("unchecked")
		List<String> _rhsIntList = (List<String>) CommonUtils
				.array2List(StringUtils.split(rhs, VERSIONNAME_SPLITWORD));

		// get left and right handle side version name string list max count
		// and update each
		Integer _versionNameIntListMaxCount = Math.max(_lhsIntList.size(),
				_rhsIntList.size());
		if (_lhsIntList.size() < _versionNameIntListMaxCount) {
			for (int i = 0; i < _versionNameIntListMaxCount
					- _lhsIntList.size(); i++) {
				_lhsIntList.add("0");
			}
		} else if (_rhsIntList.size() < _versionNameIntListMaxCount) {
			for (int i = 0; i < _versionNameIntListMaxCount
					- _rhsIntList.size(); i++) {
				_rhsIntList.add("0");
			}
		}

		// compare version name
		for (int i = 0; i < _versionNameIntListMaxCount; i++) {
			// check sub version name
			if ((_ret = _lhsIntList.get(i).compareTo(_rhsIntList.get(i))) != 0) {
				// break immediately
				break;
			}
		}

		return _ret;
	}

	// inner class
	// version name compare exception
	public static class VersionCompareException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6622844578793344270L;

		public VersionCompareException(String reason) {
			super("Version compare error, the reason is " + reason);
		}

	}

}
