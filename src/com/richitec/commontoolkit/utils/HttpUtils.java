package com.richitec.commontoolkit.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils.AsyncHttpRequestTask.RequestExecuteResult;

public class HttpUtils {

	private static final String LOG_TAG = HttpUtils.class.getCanonicalName();

	// singleton instance
	private static volatile HttpUtils _singletonInstance;

	// apache default http client
	private HttpClient _mDefaultHttpClient;

	// max connection count
	public final static int MAX_TOTAL_CONNECTIONS = 400;
	// max connection count per route
	public final static int MAX_ROUTE_CONNECTIONS = 200;

	// get connection from connection manager timeout
	private int _mTimeoutGetConnection = 1000;

	// connection and socket timeout
	private int _mTimeoutConnection = 5000;
	private int _mTimeoutSocket = 10000;

	// user name parameter key
	private static final String USERNAME_PARAMETER_KEY = "username";
	// signature parameter key
	private static final String SIGNATURE_PARAMETER_KEY = "sig";

	private HttpUtils() {
		// init http param
		HttpParams _httpParameters = new BasicHttpParams();

		// set connection count
		ConnManagerParams.setMaxTotalConnections(_httpParameters,
				MAX_TOTAL_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(_httpParameters,
				new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS));

		// set timeout
		ConnManagerParams.setTimeout(_httpParameters, _mTimeoutGetConnection);
		HttpConnectionParams.setConnectionTimeout(_httpParameters,
				_mTimeoutConnection);
		HttpConnectionParams.setSoTimeout(_httpParameters, _mTimeoutSocket);

		// define scheme registry for http and https
		SchemeRegistry _schemeRegistry = new SchemeRegistry();
		_schemeRegistry.register(new Scheme(HttpUrlPrefix.HTTP.name()
				.toLowerCase(Locale.US), PlainSocketFactory.getSocketFactory(),
				80));
		_schemeRegistry.register(new Scheme(HttpUrlPrefix.HTTPS.name()
				.toLowerCase(Locale.US), SSLSocketFactory.getSocketFactory(),
				443));

		// initialize http client using thread safe client connection manager
		ClientConnectionManager _clientConnectionManager = new ThreadSafeClientConnManager(
				_httpParameters, _schemeRegistry);
		_mDefaultHttpClient = new DefaultHttpClient(_clientConnectionManager,
				_httpParameters);
	}

	private HttpClient getDefaultHttpClient() {
		return _mDefaultHttpClient;
	}

	// get apache http client
	private static HttpClient getHttpClient() {
		if (null == _singletonInstance) {
			synchronized (HttpUtils.class) {
				if (null == _singletonInstance) {
					_singletonInstance = new HttpUtils();
				}
			}
		}

		return _singletonInstance.getDefaultHttpClient();
	}

	// perfect http request url with prefix
	public static String perfectHttpRequestUrl(String url,
			HttpUrlPrefix httpUrlPrefix) {
		String _ret = url;

		// check url is nil and has prefix "http://" or "https://"
		if (null != url && null != httpUrlPrefix && !url.equalsIgnoreCase("")) {
			// check need to perfect http request url
			if (!url.startsWith(httpUrlPrefix.prefix())) {
				// define url string builder
				StringBuilder _urlStringBuilder = new StringBuilder();

				// need to split with prefix
				if (url.startsWith(httpUrlPrefix.anotherHttpUrlPrefix()
						.prefix())) {
					_ret = _urlStringBuilder
							.append(httpUrlPrefix.prefix())
							.append(url.substring(httpUrlPrefix
									.anotherHttpUrlPrefix().prefix().length()))
							.toString();
				} else {
					_ret = _urlStringBuilder.append(httpUrlPrefix.prefix())
							.append(url).toString();
				}
			}
		} else {
			Log.e(LOG_TAG, "Perfect http request url error, url = " + url
					+ " and prefix = " + httpUrlPrefix);
		}

		return _ret;
	}

	// send get request
	public static void getRequest(String pUrl, Map<String, String> pParam,
			Map<String, ?> pUserInfo, HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// perfect http request url
		StringBuilder _httpRequestUrl = new StringBuilder(
				perfectHttpRequestUrl(pUrl, HttpUrlPrefix.HTTP));
		// append char '?' first and param pairs, if param not null
		if (null != pParam && !pParam.isEmpty()) {
			_httpRequestUrl.append('?');

			// append param pairs
			for (String _paramKey : pParam.keySet()) {
				_httpRequestUrl.append(_paramKey + "=" + pParam.get(_paramKey)
						+ '&');
			}

			// trim last char '&' in http request url
			_httpRequestUrl.deleteCharAt(_httpRequestUrl.length() - 1);
		}

		// new httpGet object
		HttpGet _getHttpRequest = new HttpGet(_httpRequestUrl.toString());

		// check http request type
		switch (pRequestType) {
		case SYNCHRONOUS:
			// send synchronous get request
			try {
				HttpResponse _response = getHttpClient().execute(
						_getHttpRequest);

				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							_getHttpRequest, _response);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous get request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// process sending http request exception
				processSendingHttpRequestException(_getHttpRequest,
						httpRequestListener, e);
			}
			break;

		case ASYNCHRONOUS:
			// new asynchronous http request task to do get request in
			// background
			new AsyncHttpRequestTask().execute(_getHttpRequest,
					httpRequestListener);
			break;
		}
	}

	// send post request
	public static void postRequest(String pUrl, PostRequestFormat pPostFormat,
			Map<String, ?> pParam, Map<String, ?> pUserInfo,
			HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// new httpPost object
		HttpPost _postHttpRequest = new HttpPost(perfectHttpRequestUrl(pUrl,
				HttpUrlPrefix.HTTP));

		// check param and set post request param
		if (null != pParam && !pParam.isEmpty()) {
			try {
				switch (pPostFormat) {
				case URLENCODED: {
					// define urlEncodedForm post request param
					List<NameValuePair> _urlEncodedFormPostReqParam = new ArrayList<NameValuePair>();

					// update urlEncodedForm post request param
					for (String _paramKey : pParam.keySet()) {
						// get param value
						Object _paramVaue = pParam.get(_paramKey);

						// check param value
						if (_paramVaue instanceof String) {
							_urlEncodedFormPostReqParam
									.add(new BasicNameValuePair(_paramKey,
											(String) _paramVaue));
						} else {
							Log.e(LOG_TAG,
									"Url encoded form post request param value must string, param value = "
											+ _paramVaue);

							// throw unsupported post request param value
							// exception
							throw new UnsupportedPostReqParamValueException(
									"url encoded form post request param value must string");
						}
					}

					// set entity
					_postHttpRequest.setEntity(new UrlEncodedFormEntity(
							_urlEncodedFormPostReqParam, HTTP.UTF_8));
				}

					break;

				case MULTIPARTFORMDATA: {
					// init multipart entity
					MultipartEntity _multipartEntity = new MultipartEntity();

					// define content body
					ContentBody _contentBody = null;

					// update multipart entity
					for (String _paramKey : pParam.keySet()) {
						// get param value
						Object _paramVaue = pParam.get(_paramKey);

						// check param value
						if (_paramVaue instanceof String) {
							_contentBody = new StringBody((String) _paramVaue,
									Charset.forName(HTTP.UTF_8));
						} else if (_paramVaue instanceof File) {
							_contentBody = new FileBody((File) _paramVaue);
						} else if (_paramVaue instanceof byte[]) {
							_contentBody = new ByteArrayBody(
									(byte[]) _paramVaue, "fileName");
						} else {
							Log.d(LOG_TAG, "Param value = " + _paramVaue
									+ " not implementation");

							// throw unsupported post request param value
							// exception
							throw new UnsupportedPostReqParamValueException(
									"not implementation");
						}

						// check content body and add as part
						if (null != _contentBody) {
							_multipartEntity.addPart(_paramKey, _contentBody);
						}
					}

					// set entity
					_postHttpRequest.setEntity(_multipartEntity);
				}
					break;
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(LOG_TAG,
						"Post request post body unsupported encoding exception message = "
								+ e.getMessage());

				e.printStackTrace();
			} catch (UnsupportedPostReqParamValueException e) {
				Log.e(LOG_TAG,
						"Post request param value unsupported exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		// check http request type
		switch (pRequestType) {
		case SYNCHRONOUS:
			// send synchronous post request
			try {
				HttpResponse _response = getHttpClient().execute(
						_postHttpRequest);

				// check http request listener and bind request response
				// callback function
				if (null != httpRequestListener) {
					httpRequestListener.bindReqRespCallBackFunction(
							_postHttpRequest, _response);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send synchronous post request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// process sending http request exception
				processSendingHttpRequestException(_postHttpRequest,
						httpRequestListener, e);
			}
			break;

		case ASYNCHRONOUS:
			// new asynchronous http request task to do post request in
			// background
			new AsyncHttpRequestTask().execute(_postHttpRequest,
					httpRequestListener);
			break;
		}
	}

	// generate signature with param
	private static String generateSignature(Map<String, String> pParam) {
		// update param
		pParam = null == pParam ? new HashMap<String, String>() : pParam;

		// put user name
		pParam.put(USERNAME_PARAMETER_KEY, UserManager.getInstance().getUser()
				.getName());

		// get param string list
		ArrayList<String> _paramStringList = new ArrayList<String>();
		for (String _paramKey : pParam.keySet()) {
			_paramStringList.add(new StringBuilder(_paramKey).append('=')
					.append(pParam.get(_paramKey)).toString());
		}
		// sorted
		Collections.sort(_paramStringList);

		// get param string
		StringBuilder _paramString = new StringBuilder();
		for (String string : _paramStringList) {
			_paramString.append(string);
		}

		// append userKey
		_paramString.append(UserManager.getInstance().getUser().getUserKey());

		return StringUtils.md5(_paramString.toString());
	}

	// send signature get request
	public static void getSignatureRequest(String pUrl,
			Map<String, String> pParam, Map<String, ?> pUserInfo,
			HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// init signature get request param
		HashMap<String, String> _signatureGetRequestParam = null == pParam ? new HashMap<String, String>()
				: new HashMap<String, String>(pParam);

		// append user name and signature
		_signatureGetRequestParam.put(USERNAME_PARAMETER_KEY, UserManager
				.getInstance().getUser().getName());
		_signatureGetRequestParam.put(SIGNATURE_PARAMETER_KEY,
				generateSignature(pParam));

		// send signature get request
		getRequest(pUrl, _signatureGetRequestParam, pUserInfo, pRequestType,
				httpRequestListener);
	}

	// send signature post request
	public static void postSignatureRequest(String pUrl,
			PostRequestFormat pPostFormat, Map<String, String> pParam,
			Map<String, ?> pUserInfo, HttpRequestType pRequestType,
			OnHttpRequestListener httpRequestListener) {
		// init signature post request param
		HashMap<String, String> _signaturePostRequestParam = null == pParam ? new HashMap<String, String>()
				: new HashMap<String, String>(pParam);

		// append user name and signature
		_signaturePostRequestParam.put(USERNAME_PARAMETER_KEY, UserManager
				.getInstance().getUser().getName());
		_signaturePostRequestParam.put(SIGNATURE_PARAMETER_KEY,
				generateSignature(pParam));

		// send signature post request
		postRequest(pUrl, pPostFormat, _signaturePostRequestParam, pUserInfo,
				pRequestType, httpRequestListener);
	}

	// get http response entity string using character encoding UTF-8
	public static String getHttpResponseEntityString(HttpResponse response) {
		String _respEntityString = null;

		// check response
		if (null != response) {
			if (response instanceof SimpleHttpResponse) {
				_respEntityString = ((SimpleHttpResponse) response)
						.getEntityString();
			} else {
				try {
					// get http response entiry
					HttpEntity _responseEntiry = response.getEntity();

					// save response entity as string
					_respEntityString = EntityUtils.toString(_responseEntiry,
							HTTP.UTF_8);

					// consume response entity content
					_responseEntiry.consumeContent();
				} catch (Exception e) {
					Log.e(LOG_TAG,
							"Get http response entity excetion message = "
									+ e.getMessage());

					e.printStackTrace();
				}
			}
		} else {
			Log.e(LOG_TAG, "Get http response entity, response is null");
		}

		return _respEntityString;
	}

	// check http request listener and process some exceptions for http request
	// sending
	private static void processSendingHttpRequestException(
			HttpRequestBase httpRequest,
			OnHttpRequestListener httpRequestListener, Exception e) {
		// check http request listener
		if (null != httpRequestListener) {
			// process the exception
			if (ConnectTimeoutException.class == e.getClass()) {
				// timeout
				httpRequestListener.onTimeout(httpRequest);
			} else if (UnknownHostException.class == e.getClass()) {
				// unknown host
				httpRequestListener.onUnknownHost(httpRequest);
			} else {
				// unknown exception
				httpRequestListener.onUnknownException(httpRequest);
			}
		}
	}

	// inner class
	// http url prefix
	public enum HttpUrlPrefix {
		HTTP, HTTPS;

		// http url prefix string
		public String prefix() {
			// define return result
			String _ret = this.name();

			// set return result
			switch (this) {
			case HTTPS:
				_ret = "https://";
				break;

			case HTTP:
				_ret = "http://";
			default:
				break;
			}

			return _ret;
		}

		// another http url prefix
		public HttpUrlPrefix anotherHttpUrlPrefix() {
			HttpUrlPrefix _ret = this;

			// set return result
			switch (this) {
			case HTTPS:
				_ret = HTTP;
				break;

			case HTTP:
			default:
				_ret = HTTPS;
				break;
			}

			return _ret;
		}

		@Override
		public String toString() {
			return prefix();
		}

	}

	// http request type
	public enum HttpRequestType {
		SYNCHRONOUS, ASYNCHRONOUS
	}

	// post request format
	public enum PostRequestFormat {
		URLENCODED, MULTIPARTFORMDATA
	}

	// http request listener
	public static abstract class OnHttpRequestListener {

		// bind request response callback function
		private void bindReqRespCallBackFunction(HttpRequest request,
				HttpResponse response) {
			// check response status code
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				onFinished(request, response);
				break;

			case HttpStatus.SC_BAD_REQUEST:
				onBadRequest(request, response);
				break;

			case HttpStatus.SC_FORBIDDEN:
				onForbidden(request, response);
				break;

			case HttpStatus.SC_NOT_FOUND:
				onNotFound(request, response);
				break;

			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				onInternalServerError(request, response);
				break;

			default:
				onFailed(request, response);
				break;
			}
		}

		// onFinished
		public abstract void onFinished(HttpRequest request,
				HttpResponse response);

		// bad request
		public void onBadRequest(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// forbidden
		public void onForbidden(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// not found
		public void onNotFound(HttpRequest request, HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// internal server error
		public void onInternalServerError(HttpRequest request,
				HttpResponse response) {
			// call onFailed callback function
			onFailed(request, response);
		}

		// onFailed
		public abstract void onFailed(HttpRequest request, HttpResponse response);

		// on timeout
		public void onTimeout(HttpRequest request) {
			// call onFailed callback function for timeout
			onFailed(request, null);
		}

		// on unknown host
		public void onUnknownHost(HttpRequest request) {
			// call onFailed callback function for unknown host
			onFailed(request, null);
		}

		// on unknown exception
		public void onUnknownException(HttpRequest request) {
			// call onFailed callback function for unknown exception
			onFailed(request, null);
		}

	}

	// simple http response
	static class SimpleHttpResponse implements HttpResponse {

		// apache http response
		private HttpResponse _mApacheHttpResponse;

		// simple response entity string
		private String _mSimpleResponseEntityString;

		// constructor, mustn't constructor in main thread
		public SimpleHttpResponse(HttpResponse apacheHttpResponse) {
			super();

			// save apache http response and entity string
			_mApacheHttpResponse = apacheHttpResponse;
			_mSimpleResponseEntityString = getHttpResponseEntityString(apacheHttpResponse);
		}

		@Override
		public void addHeader(Header header) {
			_mApacheHttpResponse.addHeader(header);
		}

		@Override
		public void addHeader(String name, String value) {
			_mApacheHttpResponse.addHeader(name, value);
		}

		@Override
		public boolean containsHeader(String name) {
			return _mApacheHttpResponse.containsHeader(name);
		}

		@Override
		public Header[] getAllHeaders() {
			return _mApacheHttpResponse.getAllHeaders();
		}

		@Override
		public Header getFirstHeader(String name) {
			return _mApacheHttpResponse.getFirstHeader(name);
		}

		@Override
		public Header[] getHeaders(String name) {
			return _mApacheHttpResponse.getHeaders(name);
		}

		@Override
		public Header getLastHeader(String name) {
			return _mApacheHttpResponse.getLastHeader(name);
		}

		@Override
		public HttpParams getParams() {
			return _mApacheHttpResponse.getParams();
		}

		@Override
		public ProtocolVersion getProtocolVersion() {
			return _mApacheHttpResponse.getProtocolVersion();
		}

		@Override
		public HeaderIterator headerIterator() {
			return _mApacheHttpResponse.headerIterator();
		}

		@Override
		public HeaderIterator headerIterator(String name) {
			return _mApacheHttpResponse.headerIterator(name);
		}

		@Override
		public void removeHeader(Header header) {
			_mApacheHttpResponse.removeHeader(header);
		}

		@Override
		public void removeHeaders(String name) {
			_mApacheHttpResponse.removeHeaders(name);
		}

		@Override
		public void setHeader(Header header) {
			_mApacheHttpResponse.setHeader(header);
		}

		@Override
		public void setHeader(String name, String value) {
			_mApacheHttpResponse.setHeader(name, value);
		}

		@Override
		public void setHeaders(Header[] headers) {
			_mApacheHttpResponse.setHeaders(headers);
		}

		@Override
		public void setParams(HttpParams params) {
			_mApacheHttpResponse.setParams(params);
		}

		@Override
		public HttpEntity getEntity() {
			return _mApacheHttpResponse.getEntity();
		}

		@Override
		public Locale getLocale() {
			return _mApacheHttpResponse.getLocale();
		}

		@Override
		public StatusLine getStatusLine() {
			return _mApacheHttpResponse.getStatusLine();
		}

		@Override
		public void setEntity(HttpEntity entity) {
			_mApacheHttpResponse.setEntity(entity);
		}

		@Override
		public void setLocale(Locale loc) {
			_mApacheHttpResponse.setLocale(loc);
		}

		@Override
		public void setReasonPhrase(String reason) throws IllegalStateException {
			_mApacheHttpResponse.setReasonPhrase(reason);
		}

		@Override
		public void setStatusCode(int code) throws IllegalStateException {
			_mApacheHttpResponse.setStatusCode(code);
		}

		@Override
		public void setStatusLine(StatusLine statusline) {
			_mApacheHttpResponse.setStatusLine(statusline);
		}

		@Override
		public void setStatusLine(ProtocolVersion ver, int code) {
			_mApacheHttpResponse.setStatusLine(ver, code);
		}

		@Override
		public void setStatusLine(ProtocolVersion ver, int code, String reason) {
			_mApacheHttpResponse.setStatusLine(ver, code, reason);
		}

		// get entity string
		public String getEntityString() {
			return _mSimpleResponseEntityString;
		}

	}

	// asynchronous http request task
	// Objects: HttpUriRequest, OnHttpRequestListener
	static class AsyncHttpRequestTask extends
			AsyncTask<Object, Integer, RequestExecuteResult> {

		// http request
		private HttpUriRequest _mHttpRequest;
		// simple http response
		private SimpleHttpResponse _mSimpleHttpResponse;
		// http request listener
		private OnHttpRequestListener _mHttpRequestListener;

		@Override
		protected RequestExecuteResult doInBackground(Object... params) {
			// init return result
			RequestExecuteResult _ret = RequestExecuteResult.NORMAL;

			// save http request and request listener
			_mHttpRequest = (HttpUriRequest) getSuitableParam(
					HttpUriRequest.class, params);
			_mHttpRequestListener = (OnHttpRequestListener) getSuitableParam(
					OnHttpRequestListener.class, params);

			// save http response
			try {
				_mSimpleHttpResponse = new SimpleHttpResponse(getHttpClient()
						.execute(_mHttpRequest));
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Send asynchronous http request excetion message = "
								+ e.getMessage());

				e.printStackTrace();

				// check http request listener
				if (null != _mHttpRequestListener) {
					// process the exception and update request execute result
					if (ConnectTimeoutException.class == e.getClass()
							|| SocketException.class == e.getClass()) {
						// timeout
						_ret = RequestExecuteResult.TIMEOUT;
					} else if (UnknownHostException.class == e.getClass()) {
						// unknown host
						_ret = RequestExecuteResult.UNKNOWN_HOST;
					} else {
						// unknown exception
						_ret = RequestExecuteResult.UNKNOWN_EXCEPTION;
					}
				}
			}

			return _ret;
		}

		@Override
		protected void onPostExecute(RequestExecuteResult result) {
			super.onPostExecute(result);

			// check http request listener and bind request response
			// callback function
			if (null != _mHttpRequestListener) {
				// check result
				switch (result) {
				case TIMEOUT:
					_mHttpRequestListener.onTimeout(_mHttpRequest);
					break;

				case UNKNOWN_HOST:
					_mHttpRequestListener.onUnknownHost(_mHttpRequest);
					break;

				case UNKNOWN_EXCEPTION:
					_mHttpRequestListener.onUnknownException(_mHttpRequest);
					break;

				case NORMAL:
				default:
					_mHttpRequestListener.bindReqRespCallBackFunction(
							_mHttpRequest, _mSimpleHttpResponse);
					break;
				}
			}
		}

		// get suitable param from params with class name
		private Object getSuitableParam(Class<?> className, Object... params) {
			Object _ret = null;

			// process params
			for (int i = 0; i < params.length; i++) {
				if (className.isInstance(params[i])) {
					_ret = params[i];

					break;
				}
			}

			return _ret;
		}

		// inner class
		// request execute result
		enum RequestExecuteResult {
			NORMAL, TIMEOUT, UNKNOWN_HOST, UNKNOWN_EXCEPTION
		}

	}

	// unsupported post request param value exception
	public static class UnsupportedPostReqParamValueException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4346450562124139037L;

		public UnsupportedPostReqParamValueException(String reason) {
			super("Can't process post request parameter value, the reason is "
					+ reason);
		}

	}

}
