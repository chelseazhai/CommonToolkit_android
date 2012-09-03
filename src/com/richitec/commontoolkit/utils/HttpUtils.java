package com.richitec.commontoolkit.utils;

import java.util.HashMap;

public class HttpUtils {

	// http request type
	public static enum HttpRequestType {
		SYNCHRONOUS, ASYNCHRONOUS
	}

	// post request format
	public static enum PostRequestFormat {
		URLENCODED, MULTIPARTFORMDATA
	}

	// send get request
	public static void getRequest(String pUrl, HashMap<String, Object> pParam,
			HashMap<String, ?> pUserInfo, HttpRequestType pRequestType) {
		//
	}

	// send post request
	public static void postRequest(String pUrl, PostRequestFormat pPostFormat,
			HashMap<String, Object> pParam, HashMap<String, ?> pUserInfo,
			HttpRequestType pRequestType) {
		//
	}

	// send signature get request
	public static void getSignatureRequest(String pUrl,
			HashMap<String, Object> pParam, HashMap<String, ?> pUserInfo,
			HttpRequestType pRequestType) {
		//
	}

	// send signature post request
	public static void postSignatureRequest(String pUrl,
			PostRequestFormat pPostFormat, HashMap<String, Object> pParam,
			HashMap<String, ?> pUserInfo, HttpRequestType pRequestType) {
		//
	}

	// inner class
	// http request handle
	public static abstract class HttpRequestHandle {
		// onFinished
		//

		// onFailed
		//
	}

}
