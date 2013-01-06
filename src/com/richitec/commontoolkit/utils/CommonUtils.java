package com.richitec.commontoolkit.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class CommonUtils {

	// convert array to list
	public static List<?> array2List(Object[] array) {
		List<Object> _ret = new ArrayList<Object>();

		if (null != array) {
			for (int i = 0; i < array.length; i++) {
				_ret.add(array[i]);
			}
		}

		return _ret;
	}

	// check if intent available or not
	public static boolean isIntentAvailable(Intent intent) {
		// get and check intent resolve info list
		List<ResolveInfo> _resolveInfoList = AppLaunchActivity.getAppContext()
				.getPackageManager()
				.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);

		return _resolveInfoList.size() > 0;
	}

}
