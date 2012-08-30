package com.richitec.commontoolkit.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtils {

	// get current version code
	public static Integer currentVersionCode(Context context) {
		// define current version code
		Integer _currentVersionCode = -1;

		try {
			_currentVersionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return _currentVersionCode;
	}

	// get current version name
	public static String currentVersionName(Context context) {
		// define current version name
		String _currentVersionName = "";

		try {
			_currentVersionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return _currentVersionName;
	}

}
