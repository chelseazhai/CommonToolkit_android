package com.richitec.commontoolkit.activityextension;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.richitec.commontoolkit.R;

public class AppInstructionActivity extends Activity {

	private static final String LOG_TAG = "AppInstructionActivity";

	// key for need to launch application instruction
	public static final String APPINSTRUCTION_LAUNCHFLAG = "need_to_launch_application_instruction";

	// application instruction activity onCreate param key
	public static final String APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY = "application_target_intent";
	public static final String APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY = "application_instruction_content_image_resource_id_list";

	// application target intent
	private Intent _mAppTargetIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.app_instruction_activity_layout);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle
		if (null != _data
				&& null != _data
						.getIntegerArrayList(APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY)
				&& null != _data
						.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY)) {
			// init application instruction content image list
			//

			// save application target intent
			_mAppTargetIntent = _data
					.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY);
		}
	}

	//

}
