package com.richitec.commontoolkit.activityextension;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.utils.DataStorageUtils;

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

		// new application launching instruction and task to do
		// didFinishLaunching in background
		new AppLaunchingTask().execute(instructionContentImgResIds(),
				intentActivity());
	}

	// application loading splash image
	public abstract Drawable splashImg();

	// application instruction content images resource id
	protected List<Integer> instructionContentImgResIds() {
		// default return null
		return null;
	}

	// application intent activity
	public abstract Intent intentActivity();

	// application did finish launching
	public abstract void didFinishLaunching();

	// inner class
	// application launching task
	class AppLaunchingTask extends AsyncTask<Object, Integer, Integer> {

		// instruction image resource id list
		List<Integer> _mInstructionImgResIds;

		// target intent
		Intent _mIntent;

		@Override
		protected Integer doInBackground(Object... params) {
			// init return result
			Integer _ret = -1;

			// save instruction image list and target intent
			if (2 == params.length) {
				// workaround by ares
				@SuppressWarnings("unchecked")
				List<Integer> _instructionImgResIds = (List<Integer>) params[0];

				_mInstructionImgResIds = _instructionImgResIds;
				_mIntent = (Intent) params[params.length - 1];

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
				// check intent activity
				if (null != _mIntent) {
					// get application instruction launch flag
					Boolean _need2LaunchAppInstruction = DataStorageUtils
							.getBoolean(AppInstructionActivity.APPINSTRUCTION_LAUNCHFLAG);

					// need to launch application instruction
					if (null == _need2LaunchAppInstruction
							|| true == _need2LaunchAppInstruction) {
						// check application instruction content image list
						if (null != _mInstructionImgResIds
								&& !_mInstructionImgResIds.isEmpty()) {
							// define application instruction intent
							Intent _appInstructionIntent = new Intent(
									AppLaunchActivity.this,
									AppInstructionActivity.class);

							// put instruction content image list and intent
							// activity
							_appInstructionIntent
									.putIntegerArrayListExtra(
											AppInstructionActivity.APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY,
											(ArrayList<Integer>) _mInstructionImgResIds);
							_appInstructionIntent
									.putExtra(
											AppInstructionActivity.APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY,
											_mIntent);

							// go to application instruction activity
							startActivity(_appInstructionIntent);
						} else {
							Log.w(LOG_TAG,
									"launch application instruction error, instruction image = "
											+ _mInstructionImgResIds);

							// go to intent activity
							startActivity(_mIntent);
						}
					} else {
						// go to intent activity
						startActivity(_mIntent);
					}
				} else {
					Log.e(LOG_TAG, "intent activity is null");
				}

				// finish application launch activity
				finish();
			}
		}

	}

}
