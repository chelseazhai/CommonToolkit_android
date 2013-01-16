package com.richitec.commontoolkit.activityextension;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;

public class AppInstructionActivity extends Activity {

	private static final String LOG_TAG = "AppInstructionActivity";

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
			// init application instruction activity UI
			initInstructionUI(_data
					.getIntegerArrayList(APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY));

			// save application target intent
			_mAppTargetIntent = _data
					.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY);
		}
	}

	// init application instruction activity UI
	private void initInstructionUI(List<Integer> instructionImgResIds) {
		// define valid instruction images count
		int _validInstructionImagesCount = 0;

		// get instruction viewFlipper
		ViewFlipper _instructionViewFlipper = ((ViewFlipper) findViewById(R.id.instructionViewFlipper));

		// get instruction images
		for (int i = 0; i < instructionImgResIds.size(); i++) {
			// check valid instruction images count and instruction viewFlipper
			// child count
			if (_validInstructionImagesCount <= _instructionViewFlipper
					.getChildCount()) {
				// get each instruction image
				Drawable _instructionImage = CTApplication.getContext()
						.getResources()
						.getDrawable(instructionImgResIds.get(i));

				// check each instruction image
				if (null != _instructionImage) {
					// get instruction content imageView
					ImageView _instructionContentImageView = (ImageView) _instructionViewFlipper
							.getChildAt(_validInstructionImagesCount)
							.findViewById(R.id.instructionContent_imageView);

					// set instruction content imageView image and show it
					_instructionContentImageView
							.setImageDrawable(_instructionImage);
					_instructionContentImageView.setVisibility(View.VISIBLE);

					// increase valid instruction images count
					_validInstructionImagesCount++;
				}
			} else {
				Log.w(LOG_TAG, "Application instruction can't show more images");

				// return immediately
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		// nothing to do
	}

}
