package com.richitec.commontoolkit.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtils {

	private static final String LOG_TAG = "JSONUtils";

	// get json object value with key and value class name
	private static Object getJSONObjectValue(JSONObject jsonObject, String key,
			Class<? extends Object> valueClass) {
		Object _value = null;

		// check json object
		if (null != jsonObject) {
			// get value
			try {
				if (Integer.class == valueClass) {
					_value = jsonObject.getInt(key);
				} else if (Long.class == valueClass) {
					_value = jsonObject.getLong(key);
				} else if (Double.class == valueClass) {
					_value = jsonObject.getDouble(key);
				} else if (Boolean.class == valueClass) {
					_value = jsonObject.getBoolean(key);
				} else if (String.class == valueClass) {
					_value = jsonObject.getString(key);
				} else if (JSONObject.class == valueClass) {
					_value = jsonObject.getJSONObject(key);
				} else if (JSONArray.class == valueClass) {
					_value = jsonObject.getJSONArray(key);
				} else {
					_value = jsonObject.get(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();

				Log.e(LOG_TAG,
						"Get json = " + jsonObject + " value with key = " + key
								+ " and value class = " + valueClass.getName()
								+ " exception: " + e.getMessage());
			}
		} else {
			Log.e(LOG_TAG, "Get json value with key = " + key
					+ " and value class = " + valueClass.getName()
					+ ", json object is null");
		}

		return _value;
	}

	// get integer from json object with key
	public static Integer getIntegerFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Integer) getJSONObjectValue(jsonObject, key, Integer.class);
	}

	// get long from json object with key
	public static Long getLongFromJSONObject(JSONObject jsonObject, String key) {
		return (Long) getJSONObjectValue(jsonObject, key, Long.class);
	}

	// get double from json object with key
	public static Double getDoubleFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Double) getJSONObjectValue(jsonObject, key, Double.class);
	}

	// get boolean from json object with key
	public static Boolean getBooleanFromJSONObject(JSONObject jsonObject,
			String key) {
		return (Boolean) getJSONObjectValue(jsonObject, key, Boolean.class);
	}

	// get string from json object with key
	public static String getStringFromJSONObject(JSONObject jsonObject,
			String key) {
		return (String) getJSONObjectValue(jsonObject, key, String.class);
	}

	// get json object from json object with key
	public static JSONObject getJSONObjectFromJSONObject(JSONObject jsonObject,
			String key) {
		return (JSONObject) getJSONObjectValue(jsonObject, key,
				JSONObject.class);
	}

	// get json array from json object with key
	public static JSONArray getJSONArrayFromJSONObject(JSONObject jsonObject,
			String key) {
		return (JSONArray) getJSONObjectValue(jsonObject, key, JSONArray.class);
	}

	// get object from json object with key
	public static Object getObjectFromJSONObject(JSONObject jsonObject,
			String key) {
		return getJSONObjectValue(jsonObject, key, Object.class);
	}

}
