package com.richitec.commontoolkit.activityextension;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.R;

public class AppInstructionActivity extends Activity {

	private static final String LOG_TAG = "AppInstructionActivity";

	// application instruction activity onCreate param key
	public static final String APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY = "application_target_intent";
	public static final String APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY = "application_instruction_content_image_resource&layout_id_list";

	// application target intent
	private Intent _mAppTargetIntent;

	// instruction viewFlipper
	private ViewFlipper _mInstructionViewFlipper;

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
			// get application instruction image resource and layout ids
			List<Integer> _appInstructionImgIds = _data
					.getIntegerArrayList(APPINSTRUCTION_ACTIVITY_IMGRESIDS_PARAM_KEY);

			// get instruction viewFlipper
			_mInstructionViewFlipper = ((ViewFlipper) findViewById(R.id.instructionViewFlipper));

			// set instruction viewFlipper on touch listener
			_mInstructionViewFlipper.setOnTouchListener(new InstructionViewFlipperOnTouchListener());

			// init application instruction activity UI
			initInstructionUI(
					_appInstructionImgIds.subList(0,
							_appInstructionImgIds.size() - 1),
					_appInstructionImgIds.get(_appInstructionImgIds.size() - 1));

			// save application target intent
			_mAppTargetIntent = _data
					.getParcelable(APPINSTRUCTION_ACTIVITY_TARGETINTENT_PARAM_KEY);
		}
	}

	// init application instruction activity UI
	private void initInstructionUI(List<Integer> instructionImgResIds,
			Integer instructionActiveImgLayoutId) {
		// define valid instruction images count
		int _validInstructionImagesCount = 0;

		// get instruction images
		for (int i = 0; i < instructionImgResIds.size(); i++) {
			// check valid instruction images count and instruction viewFlipper
			// child count
			if (_validInstructionImagesCount < _mInstructionViewFlipper
					.getChildCount()) {
				// get each instruction image
				Drawable _instructionImage = CTApplication.getContext()
						.getResources()
						.getDrawable(instructionImgResIds.get(i));

				// check each instruction image
				if (null != _instructionImage) {
					// set instruction content imageView image
					((ImageView) _mInstructionViewFlipper.getChildAt(
							_validInstructionImagesCount++).findViewById(
							R.id.instructionContent_imageView))
							.setImageDrawable(_instructionImage);
				}
			} else {
				Log.w(LOG_TAG, "Application instruction can't show more images");

				// return immediately
				break;
			}
		}

		// get instruction active image parent view
		ViewGroup _instructionActiveImgParentView = (ViewGroup) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(instructionActiveImgLayoutId, null);

		// get active content imageView and button
		ImageView _activeContentImageView = (ImageView) _instructionActiveImgParentView
				.findViewById(R.id.activeContent_imageView);
		Button _activeContentButton = (Button) _instructionActiveImgParentView
				.findViewById(R.id.activeContent_button);

		// check active content imageView and button
		if (null != _activeContentButton) {
			// get instruction viewFlipper child relativeLayout
			RelativeLayout _viewFlipperChildRelativeLayout = (RelativeLayout) _mInstructionViewFlipper
					.getChildAt(_validInstructionImagesCount++);

			// check active content imageView and set instruction content
			// imageView image
			if (null != _activeContentImageView) {
				((ImageView) _viewFlipperChildRelativeLayout
						.findViewById(R.id.instructionContent_imageView))
						.setImageDrawable(_activeContentImageView.getDrawable());
			} else {
				Log.w(LOG_TAG,
						"active content parent view child imageView is null");
			}

			// remove active button from active content parent view first
			_instructionActiveImgParentView.removeView(_activeContentButton);

			// set active content button on click listener
			_activeContentButton
					.setOnClickListener(new GotoTargetIntentBtnOnClickListener());

			// append go to target intent button
			_viewFlipperChildRelativeLayout.addView(_activeContentButton);
		} else {
			Log.e(LOG_TAG,
					"application instruction active button lost in active content parent view");

			// finish application instruction activity
			finish();
		}

		// init instruction next navigation and cursors
		if (1 < _validInstructionImagesCount) {
			// show next instruction image navigation imageView
			((ImageView) findViewById(R.id.instructionNav_NextImageView))
					.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < _validInstructionImagesCount; i++) {
			// get instruction cursor imageView, set its drawable and show it
			ImageView _instructionCursorImageView = ((ImageView) ((LinearLayout) findViewById(R.id.instructionCursorLinearLayout))
					.getChildAt(i));
			_instructionCursorImageView
					.setImageResource(0 == i ? R.drawable.img_instructioncursor_selected
							: R.drawable.img_instructioncursor_unselected);
			_instructionCursorImageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		// nothing to do
	}

	// inner class
	// instruction viewFlipper on touch listener
	class InstructionViewFlipperOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			new GestureDetector(null);
			
			return false;
		}

	}

	// go to target intent button on click listener
	class GotoTargetIntentBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to intent activity
			startActivity(_mAppTargetIntent);

			// finish application instruction activity
			finish();
		}

	}

}
