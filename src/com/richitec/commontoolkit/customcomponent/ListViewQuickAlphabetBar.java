package com.richitec.commontoolkit.customcomponent;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.activityextension.R;

public class ListViewQuickAlphabetBar {

	private static final String LOG_TAG = "ListViewQuickAlphabetBar";

	// alphabet relativeLayout
	private RelativeLayout _mAlphabetRelativeLayout;

	// dependent listView
	private ListView _mDependentListView;

	// listView quick alphabet bar touch listener
	private OnTouchListener _mOnTouchListener;

	public ListViewQuickAlphabetBar(ListView dependentListView) {
		// get quickAlphabetBar frameLayout
		FrameLayout _quickAlphabetBarFrameLayout = (FrameLayout) ((LayoutInflater) AppLaunchActivity
				.getAppContext().getSystemService(
						Activity.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_quickalphabetbar_layout, null);

		// save alphabet relativeLayout
		_mAlphabetRelativeLayout = (RelativeLayout) _quickAlphabetBarFrameLayout
				.findViewById(R.id.alphabet_relativeLayout);

		// set alphabet relativeLayout on touch listener
		_mAlphabetRelativeLayout
				.setOnTouchListener(new OnAlphabetRelativeLayoutTouchListener());

		// check dependent listView
		if (null != dependentListView && null != dependentListView.getParent()
				&& dependentListView.getParent() instanceof FrameLayout) {
			// save dependent listView
			_mDependentListView = dependentListView;

			// update dependent listView ui
			// add list view padding right added 20dp
			dependentListView.setPadding(dependentListView.getPaddingLeft(),
					dependentListView.getPaddingTop(),
					dependentListView.getPaddingRight() + 30,
					dependentListView.getPaddingBottom());

			// hide scroll bar
			dependentListView.setScrollBarStyle(View.INVISIBLE);

			// add alphabet relativeLayout to dependent listView
			_quickAlphabetBarFrameLayout.removeView(_mAlphabetRelativeLayout);
			((FrameLayout) dependentListView.getParent())
					.addView(_mAlphabetRelativeLayout);
		} else {
			Log.e(LOG_TAG, "Dependent listView = " + dependentListView
					+ " and its parent view = " + dependentListView.getParent());
		}
	}

	// set listView quickAlphabetBar on touch listener
	public void setOnTouchListener(OnTouchListener onTouchListener) {
		_mOnTouchListener = onTouchListener;
	}

	// inner class
	// alphabet relativeLayout on touch listener
	class OnAlphabetRelativeLayoutTouchListener implements
			android.view.View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check event action
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				// update alphabet relativeLayout background resource
				_mAlphabetRelativeLayout
						.setBackgroundResource(R.drawable.listview_alphabetrelativelayout_bg);

				// check touch listener
				if (null != _mOnTouchListener && null != _mDependentListView) {
					_mOnTouchListener.onTouch(_mAlphabetRelativeLayout,
							_mDependentListView, event, 0);
				} else {
					if (null == _mDependentListView) {
						Log.e(LOG_TAG, "Dependent listView is null");
					} else {
						Log.w(LOG_TAG,
								"ListView quickAlphabetBar not be stted on touch listener");
					}
				}
			} else if (MotionEvent.ACTION_UP == event.getAction()) {
				Log.d(LOG_TAG, "ACTION_UP");

				// update alphabet relativeLayout background color
				_mAlphabetRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
			}

			return false;
		}

	}

	// listView quick alphabet bar touch listener
	public static abstract class OnTouchListener {

		// listView quick alphabet bar on touch
		protected abstract boolean onTouch(
				RelativeLayout alphabetRelativeLayout,
				ListView dependentListView, MotionEvent event,
				int alphabeticalIndex);

	}

}
