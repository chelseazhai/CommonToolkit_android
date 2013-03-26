package com.richitec.commontoolkit.utils;

import java.util.Locale;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.richitec.commontoolkit.CTApplication;

public class DeviceUtils {

	// telephony manager
	private static volatile TelephonyManager _telephonyManager;

	// get telephony manager
	private static TelephonyManager getTelephonyManager() {
		// check telephony manager
		if (null == _telephonyManager) {
			synchronized (DeviceUtils.class) {
				if (null == _telephonyManager) {
					// init telephony manager object
					_telephonyManager = (TelephonyManager) CTApplication
							.getContext().getSystemService(
									Context.TELEPHONY_SERVICE);
				}
			}
		}

		return _telephonyManager;
	}

	// get system current setting language. zh_CN, zh_TW etc.
	public static Locale systemSettingLanguage() {
		// define return result
		Locale _ret = Locale.ENGLISH;

		// get default locale
		Locale _defaultLocale = Locale.getDefault();

		// check language and country
		if (Locale.CHINESE.toString().equalsIgnoreCase(
				_defaultLocale.getLanguage())) {
			if ("CN".equalsIgnoreCase(_defaultLocale.getCountry())) {
				_ret = Locale.SIMPLIFIED_CHINESE;
			} else {
				_ret = Locale.TRADITIONAL_CHINESE;
			}
		}

		return _ret;
	}

	// get device id. IMEI(15bits) for GSM or WCDMA, MEID(14bits) for CDMA
	public static String deviceId() {
		// return device id
		return getTelephonyManager().getDeviceId();
	}

	// get sim serial number, ICCID(20bits)
	public static String simSerialNumber() {
		// return sim serial number
		return getTelephonyManager().getSimSerialNumber();
	}

	// get android id
	public static String androidId() {
		// return android id
		return Secure.getString(
				CTApplication.getContext().getContentResolver(),
				Secure.ANDROID_ID);
	}

}
