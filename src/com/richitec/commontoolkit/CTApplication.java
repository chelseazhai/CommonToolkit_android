package com.richitec.commontoolkit;

import android.app.Application;
import android.content.Context;

public class CTApplication extends Application {

	// singleton instance
	private static volatile CTApplication _singletonInstance;

	public CTApplication() {
		super();

		// init singleton instance
		_singletonInstance = this;
	}

	// retrieve application's context
	public static Context getContext() {
		return _singletonInstance;
	}

}
