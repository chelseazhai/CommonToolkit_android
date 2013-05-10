package com.richitec.commontoolkit.utils;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;

public class VersionUtils {

	private static final String LOG_TAG = VersionUtils.class.getCanonicalName();

	// application auto check and upgrade flag
	private static boolean _mAppAutoCheck7UpgradeFlag = true;

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
			Log.e(LOG_TAG,
					"Get application version code error, exception message = "
							+ e.getMessage());

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
			Log.e(LOG_TAG,
					"Get application version name error, exception message = "
							+ e.getMessage());

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
					"Compare version name unnecessary, left version name = "
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

	// check application current version and upgrade the application with
	// application id and upgrade mode
	public static void upgradeApp(Context activityContext, String appId,
			APPUPGRADEMODE upgradeMode) {
		// upgrade application with application id from application version
		// center
		upgradeApp(activityContext, appId, null, upgradeMode);
	}

	// check application current version and upgrade the application with
	// application id
	public static void upgradeApp(Context activityContext, String appId) {
		// auto upgrade application with application id from application version
		// center
		upgradeApp(activityContext, appId, null, APPUPGRADEMODE.AUTO);
	}

	// check application current version and upgrade the application with
	// application id, application version center url and upgrade mode
	private static void upgradeApp(Context activityContext, String appId,
			String appVCenterUrl, APPUPGRADEMODE upgradeMode) {
		// check activity context
		if (activityContext instanceof Activity) {
			// check application id
			if (null != appId) {
				// check and reset application version center url, if null use
				// common application version center url instead
				appVCenterUrl = null == appVCenterUrl ? activityContext
						.getResources().getString(
								R.string.application_upgradeVCenter_url)
						: appVCenterUrl;

				// check
				if (APPUPGRADEMODE.MANUAL == upgradeMode
						|| _mAppAutoCheck7UpgradeFlag) {
					// send get the application latest version from its version
					// center get request
					HttpUtils
							.getRequest(
									appVCenterUrl
											+ activityContext
													.getResources()
													.getString(
															R.string.get_appLatestVersion_url)
													.replace("***", appId),
									null,
									null,
									HttpRequestType.ASYNCHRONOUS,
									new GetApplicationLatestVersionHttpRequestListener(
											activityContext,
											upgradeMode,
											appVCenterUrl
													+ activityContext
															.getResources()
															.getString(
																	R.string.application_download_url)
															.replace("***",
																	appId)));
				}
			} else {
				Log.e(LOG_TAG, "Unknown application id, application id is null");

				// show the upgraded application id is null message
				Toast.makeText(activityContext,
						R.string.ct_toast_upgradedAppId_null,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e(LOG_TAG,
					"Unable to upgrade the application, because there is no activity context to builder upgrade alert dialog");
		}
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

	// application upgrade mode
	public static enum APPUPGRADEMODE {
		AUTO, MANUAL
	}

	// get the application latest version from its version center http request
	// listener
	static class GetApplicationLatestVersionHttpRequestListener extends
			OnHttpRequestListener {

		// activity context
		private Context _mContext;

		// application upgrade mode and download url
		private APPUPGRADEMODE _mAppUpgradeMode;
		private String _mAppDownloadUrl;

		public GetApplicationLatestVersionHttpRequestListener(Context context,
				APPUPGRADEMODE upgradeMode, String downloadUrl) {
			super();

			// save activity context, upgrade mode and download url
			_mContext = context;
			_mAppUpgradeMode = upgradeMode;
			_mAppDownloadUrl = downloadUrl;
		}

		@Deprecated
		public GetApplicationLatestVersionHttpRequestListener() {
			super();
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// check application upgrade mode and reset application auto check
			// and upgrade flag
			if (APPUPGRADEMODE.MANUAL != _mAppUpgradeMode) {
				_mAppAutoCheck7UpgradeFlag = false;
			}

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			// get http response entity string json object
			JSONObject _respEntityStringJsonObject = JSONUtils
					.toJSONObject(_respEntityString);

			// get the upgraded application latest version name and upgrade
			// comment
			String _latestVersionName = JSONUtils
					.getStringFromJSONObject(
							_respEntityStringJsonObject,
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUpgradedAppLatestVersionReq_resp_version));
			String _upgradeComment = JSONUtils
					.getStringFromJSONObject(
							_respEntityStringJsonObject,
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUpgradedAppLatestVersionReq_resp_comment));

			try {
				// get the local and latest version compare result
				int _compareResult = compareVersionName(versionName(),
						_latestVersionName);

				// define a alertDialog builder
				final Builder ALERTDIALOG_BUILDER = new AlertDialog.Builder(
						_mContext);

				// check the upgraded application local version if it is the
				// latest or not
				if (0 == _compareResult) {
					// show application is the latest alert dialog
					ALERTDIALOG_BUILDER
							.setTitle(
									R.string.ct_appUpgrade6theLatestApp_alertDialog_title)
							.setMessage(
									_mContext
											.getResources()
											.getString(
													R.string.ct_theLatestApp_alertDialog_message)
											.replace("***", versionName()))
							.setNeutralButton(
									R.string.ct_theLatestApp_alertDialog_neutralButton_title,
									null).show();
				} else if (_compareResult < 0) {
					// show application upgrade alert dialog
					ALERTDIALOG_BUILDER
							.setTitle(
									R.string.ct_appUpgrade6theLatestApp_alertDialog_title)
							.setMessage(
									_mContext
											.getResources()
											.getString(
													R.string.ct_appUpgrade_alertDialog_message)
											.replaceFirst("\\*\\*\\*",
													_latestVersionName)
											.replace("***", _upgradeComment))
							.setPositiveButton(
									R.string.ct_appUpgrade_alertDialog_upgradeButton_title,
									new ApplicationUpgradeButtonOnClickListener())
							.setNegativeButton(
									R.string.ct_appUpgrade_alertDialog_remindLaterButton_title,
									null).show();
				} else {
					Log.e(LOG_TAG,
							"The upgraded application latest version is less than its local version");
				}
			} catch (VersionCompareException e) {
				Log.e(LOG_TAG,
						"Compare application version name error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG,
					"Get the application latest version from its version center failed, send get the application latest version get request failed");

			// show unable to get the upgraded application latest version from
			// its version center message
			Toast.makeText(_mContext,
					R.string.ct_toast_getUpgradedAppLatestVersion_failed,
					Toast.LENGTH_SHORT).show();
		}

		// inner class
		// application upgrade button on click listener
		class ApplicationUpgradeButtonOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// download the latest application from its version center
				_mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(_mAppDownloadUrl)));
			}

		}

	}

}
