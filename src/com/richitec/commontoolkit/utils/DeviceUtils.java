package com.richitec.commontoolkit.utils;

import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.richitec.commontoolkit.CTApplication;

public class DeviceUtils {

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

	// device uuid, device id can expose user's info to open
	public static String deviceUUID() {
		// get telephony manager
		final TelephonyManager _telephonyManager = (TelephonyManager) CTApplication
				.getContext().getSystemService(Context.TELEPHONY_SERVICE);

		// get device id, sim serial number and android id
		// IMEI for GSM or WCDMA, MEID for CDMA
		String _deviceId = "" + _telephonyManager.getDeviceId();
		// IMSI
		String _simSerialNumber = "" + _telephonyManager.getSimSerialNumber();
		String _androidId = ""
				+ Secure.getString(CTApplication.getContext()
						.getContentResolver(), Secure.ANDROID_ID);

		// generate device uuid and return
		return new UUID(_androidId.hashCode(),
				((long) _deviceId.hashCode() << 32)
						| _simSerialNumber.hashCode()).toString();
	}

}
