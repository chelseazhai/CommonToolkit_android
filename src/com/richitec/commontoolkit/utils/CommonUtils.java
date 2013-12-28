package com.richitec.commontoolkit.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;

public class CommonUtils {

	private static final String LOG_TAG = CommonUtils.class.getCanonicalName();

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

	// compare two list
	public static boolean compareList(List<?> leftList, List<?> rightList,
			boolean isOrdered) {
		boolean _ret = false;

		// check left and right list
		// both null
		if (null == leftList && null == rightList) {
			// set equals
			_ret = true;

			Log.d(LOG_TAG, "Not need to compare two object list, left list = "
					+ leftList + " and right list = " + rightList
					+ ", set equals");
		}
		// neither left list null nor riht list null
		else if ((null == leftList && null != rightList)
				|| (null != leftList && null == rightList)) {
			Log.d(LOG_TAG, "Can't compare two object list, left list = "
					+ leftList + " and right list = " + rightList
					+ ", set not equals");
		} else {
			// check class and size
			if (leftList.getClass().equals(rightList.getClass())
					&& leftList.size() == rightList.size()) {
				// check size
				if (0 == leftList.size() && 0 == rightList.size()) {
					// set equals
					_ret = true;

					Log.d(LOG_TAG,
							"Not need to compare two object list, both left list and right list are empty, set equals");
				} else {
					for (int i = 0; i < leftList.size(); i++) {
						// check compare ordered or unordered
						if (isOrdered) {
							if (!leftList.get(i).equals(rightList.get(i))) {
								Log.d(LOG_TAG,
										"Left list element = "
												+ leftList.get(i)
												+ " at index = "
												+ i
												+ " not equals to the right list element = "
												+ rightList.get(i)
												+ " at the same position");

								// break immediately
								break;
							}
						} else {
							if (!rightList.contains(leftList.get(i))) {
								Log.d(LOG_TAG, "Left list element = "
										+ leftList.get(i) + " at index = " + i
										+ " not contains in the right list");

								// break immediately
								break;
							} else {
								// remove the matched object from right list
								rightList.remove(rightList.indexOf(leftList
										.get(i)));
							}
						}

						if (leftList.size() - 1 == i) {
							_ret = true;
						}
					}
				}
			} else {
				Log.d(LOG_TAG, "Compare two object list, left list class = "
						+ leftList.getClass() + ", size = " + leftList.size()
						+ " and right list class = " + rightList.getClass()
						+ ", size = " + rightList.size());
			}
		}

		return _ret;
	}

	// check if intent available or not
	public static boolean isIntentAvailable(Intent intent) {
		// define return result
		boolean _ret = true;

		// get application context
		Context _appContext = CTApplication.getContext();

		// get and check intent resolve info list
		List<ResolveInfo> _resolveInfoList = _appContext.getPackageManager()
				.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);

		// check
		if (_resolveInfoList.size() <= 0) {
			// update return result
			_ret = false;

			Log.e(LOG_TAG, "Intent = " + intent + " is not available");

			// show intent is not available message
			Toast.makeText(_appContext, R.string.ct_toast_intent_not_available,
					Toast.LENGTH_SHORT).show();
		}

		return _ret;
	}

}
