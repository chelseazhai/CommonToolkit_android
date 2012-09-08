package com.richitec.commontoolkit.activityextension;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public abstract class AppLaunchActivity extends Activity {

	private static final String LOG_TAG = "AppLaunchActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.app_launch_activity_layout);

		// set loading splash image
		((ImageView) findViewById(R.id.appSplash_imageView))
				.setImageDrawable(splashImg());

		// new application launching task to do didFinishLaunching in background
		new AppLaunchingTask().execute(intentActivity());
	}

	// application loading splash image
	public abstract Drawable splashImg();

	// application intent activity
	public abstract Intent intentActivity();

	// application did finish launching
	public abstract void didFinishLaunching();

	// inner class
	// application launching task
	class AppLaunchingTask extends AsyncTask<Intent, Integer, Integer> {

		// task intent
		Intent _mIntent;

		@Override
		protected Integer doInBackground(Intent... params) {
			// init return result
			Integer _ret = -1;

			// save task intent
			if (1 == params.length) {
				_mIntent = params[params.length - 1];

				// did finish launch
				didFinishLaunching();

				// reset return result
				_ret = 0;
			} else {
				Log.e(LOG_TAG,
						"open application launching task failed, param = "
								+ params.toString());
			}

			return _ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			// check result
			if (0 == result) {
				// go to intent activity and finish application launch activity
				startActivity(_mIntent);

				finish();
			}
		}

	}

}
