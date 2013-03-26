package com.richitec.commontoolkit.activityextension;

import java.io.Serializable;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.R;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.ImageBarButtonItem;

public class NavigationActivity extends Activity {

	private static final String LOG_TAG = NavigationActivity.class
			.getCanonicalName();

	// commonToolkit navigation activity onCreate param key
	private static final String NAV_ACTIVITY_PARAM_KEY = "nav_back_btn_default_title";

	// nav bar back button item
	private BarButtonItem _mBackBarBtnItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle
		if (null != _data && null != _data.getString(NAV_ACTIVITY_PARAM_KEY)) {
			// init default nav bar back button item
			_mBackBarBtnItem = new BarButtonItem(this,
					_data.getString(NAV_ACTIVITY_PARAM_KEY),
					BarButtonItemStyle.LEFT_BACK,
					backBarBtnItemNormalDrawable(),
					backBarBtnItemPressedDrawable(), new OnClickListener() {

						@Override
						public void onClick(View v) {
							// finish self activity
							finish();

						}
					});
		}
	}

	// hide navigation bar when navigation activity on created
	protected boolean hideNavigationBarWhenOnCreated() {
		return false;
	}

	@Override
	public void setContentView(int layoutResID) {
		// set content view
		super.setContentView(R.layout.navigation_activity_layout);

		// check is hide navigation bar on navigation activity created
		if (!hideNavigationBarWhenOnCreated()) {
			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// set nav bar back button item, if not null
			if (null != _mBackBarBtnItem) {
				setLeftBarButtonItem(_mBackBarBtnItem);
			}
		}

		// set parameter view to navigation content linearLayout
		getLayoutInflater().inflate(layoutResID,
				(LinearLayout) findViewById(R.id.navContent_relativeLayout));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		Log.d(LOG_TAG, "Navigation activity set content view, view: " + view
				+ " and layout params: " + params);

		// set content view
		super.setContentView(view, params);
	}

	@Override
	public void setContentView(View view) {
		// set content view
		super.setContentView(R.layout.navigation_activity_layout);

		// check is hide navigation bar on navigation activity created
		if (!hideNavigationBarWhenOnCreated()) {
			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// set nav bar back button item, if not null
			if (null != _mBackBarBtnItem) {
				setLeftBarButtonItem(_mBackBarBtnItem);
			}
		}

		// set parameter view to navigation content linearLayout
		((LinearLayout) findViewById(R.id.navContent_relativeLayout))
				.addView(view);
	}

	// set navBar background color
	public void setNavBarBackgroundColor(int navBarBackgroundColor) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundColor(navBarBackgroundColor);
	}

	// set navBar background resource
	public void setNavBarBackgroundResource(int navBarBackgroundResId) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundResource(navBarBackgroundResId);
	}

	// set navBar background drawable
	public void setNavBarBackgroundDrawable(Drawable navBarBackgroundDrawable) {
		((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
				.setBackgroundDrawable(navBarBackgroundDrawable);
	}

	// nav back bar button item normal drawable
	protected Drawable backBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_normal_bg);
	}

	// nav back bar button item pressed drawable
	protected Drawable backBarBtnItemPressedDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_touchdown_bg);
	}

	// set left bar button item
	public void setLeftBarButtonItem(BarButtonItem barButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _leftBtnLayout = (FrameLayout) findViewById(R.id.left_btn_frameLayout);
		_leftBtnLayout.removeAllViews();
		_leftBtnLayout.addView(removeViewFromParent4Setting(barButtonItem));
	}

	public void setLeftBarButtonItem(ImageBarButtonItem imageBarButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _leftBtnLayout = (FrameLayout) findViewById(R.id.left_btn_frameLayout);
		_leftBtnLayout.removeAllViews();
		_leftBtnLayout
				.addView(removeViewFromParent4Setting(imageBarButtonItem));
	}

	// set right bar button item
	public void setRightBarButtonItem(BarButtonItem barButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _rightBtnLayout = (FrameLayout) findViewById(R.id.right_btn_frameLayout);
		_rightBtnLayout.removeAllViews();
		_rightBtnLayout.addView(removeViewFromParent4Setting(barButtonItem));
	}

	public void setRightBarButtonItem(ImageBarButtonItem imageBarButtonItem) {
		// remove default navigation bar left button item and add the new one
		FrameLayout _rightBtnLayout = (FrameLayout) findViewById(R.id.right_btn_frameLayout);
		_rightBtnLayout.removeAllViews();
		_rightBtnLayout
				.addView(removeViewFromParent4Setting(imageBarButtonItem));
	}

	// set title view with tag
	public void setTitle(View titleView, String titleTag) {
		// check title tag and set navigation activity title
		if (null == titleTag || "".equalsIgnoreCase(titleTag.trim())) {
			setTitle(R.string.ct_navigation_title_textView_hint);
		} else {
			setTitle(titleTag);
		}

		// get title content view
		FrameLayout _titleContentView = (FrameLayout) findViewById(R.id.title_contentView);

		// show title contentView if needed and hide title textView
		if (View.VISIBLE != _titleContentView.getVisibility()) {
			_titleContentView.setVisibility(View.VISIBLE);

			((TextView) findViewById(R.id.title_textView))
					.setVisibility(View.GONE);
		}

		// clear title contentView
		_titleContentView.removeAllViews();

		// check and add title view
		if (null != titleView) {
			_titleContentView.addView(removeViewFromParent4Setting(titleView));
		} else {
			Log.e(LOG_TAG, "Title view is null, show nothing.");
		}
	}

	// set title view with tag
	public void setTitle(View titleView) {
		// use default navigation title hint as back operation button title
		setTitle(titleView, null);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);

		// get title textView
		TextView _titleTextView = (TextView) findViewById(R.id.title_textView);

		// show title textView if needed and hide title contentView
		if (View.VISIBLE != _titleTextView.getVisibility()) {
			_titleTextView.setVisibility(View.VISIBLE);

			((FrameLayout) findViewById(R.id.title_contentView))
					.setVisibility(View.GONE);
		}

		// set title textView text
		_titleTextView.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);

		// get title textView
		TextView _titleTextView = (TextView) findViewById(R.id.title_textView);

		// show title textView if needed and hide title contentView
		if (View.VISIBLE != _titleTextView.getVisibility()) {
			_titleTextView.setVisibility(View.VISIBLE);

			((FrameLayout) findViewById(R.id.title_contentView))
					.setVisibility(View.GONE);
		}

		// set title
		_titleTextView.setText(titleId);
	}

	@Override
	public void setTitleColor(int textColor) {
		super.setTitleColor(textColor);

		// set title textView text color
		((TextView) findViewById(R.id.title_textView)).setTextColor(textColor);
	}

	// set title font
	public void setTitleSize(int textSize) {
		// set title textView text font size
		((TextView) findViewById(R.id.title_textView)).setTextSize(textSize);
	}

	// push activity with extra data to navigation activity stack
	public void pushActivity(Class<? extends Activity> activityClass,
			Map<String, ?> extraData) {
		// define the intent
		Intent _intent = new Intent(this, activityClass);

		// set intent extra parameter data
		_intent.putExtra(NAV_ACTIVITY_PARAM_KEY, (String) this.getTitle());

		if (null != extraData) {
			for (String extraDataKey : extraData.keySet()) {
				// get value object
				Object _valueObject = extraData.get(extraDataKey);

				// check extra data type
				if (_valueObject instanceof Short) {
					_intent.putExtra(extraDataKey, (Short) _valueObject);
				} else if (_valueObject instanceof Integer) {
					_intent.putExtra(extraDataKey, (Integer) _valueObject);
				} else if (_valueObject instanceof Long) {
					_intent.putExtra(extraDataKey, (Long) _valueObject);
				} else if (_valueObject instanceof Float) {
					_intent.putExtra(extraDataKey, (Float) _valueObject);
				} else if (_valueObject instanceof Double) {
					_intent.putExtra(extraDataKey, (Double) _valueObject);
				} else if (_valueObject instanceof Character) {
					_intent.putExtra(extraDataKey, (Character) _valueObject);
				} else if (_valueObject instanceof Byte) {
					_intent.putExtra(extraDataKey, (Byte) _valueObject);
				} else if (_valueObject instanceof Boolean) {
					_intent.putExtra(extraDataKey, (Boolean) _valueObject);
				} else if (_valueObject instanceof String) {
					_intent.putExtra(extraDataKey, (String) _valueObject);
				} else if (_valueObject instanceof CharSequence) {
					_intent.putExtra(extraDataKey, (CharSequence) _valueObject);
				} else if (_valueObject instanceof Serializable) {
					_intent.putExtra(extraDataKey, (Serializable) _valueObject);
				} else {
					// others, not implementation
					Log.d(LOG_TAG, "Type = "
							+ extraData.get(extraDataKey).getClass().getName()
							+ " not implementation");
				}
			}
		}

		// go to the activity
		startActivity(_intent);
	}

	// push activity to navigation activity stack
	public void pushActivity(Class<? extends Activity> activityClass) {
		this.pushActivity(activityClass, null);
	}

	// pop this activity from activity stack
	public void popActivity() {
		// finish self activity
		finish();
	}

	// remove title view, left and right (image)bar button item from its parent
	// view
	private View removeViewFromParent4Setting(View view) {
		// check view
		if (null != view) {
			// get view parent
			ViewParent _viewParent = view.getParent();

			// check view parent and remove view if needed
			if (null != _viewParent) {
				// remove view from its parent view
				((ViewGroup) _viewParent).removeView(view);
			}
		} else {
			Log.e(LOG_TAG,
					"Remove view from parent for setting error, view is null");
		}

		// return view for setting
		return view;
	}

	// @Override
	// public void onBackPressed() {
	// // get alive activity list
	// List<ActivityManager.RunningTaskInfo> _aliveActivityList =
	// ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
	// .getRunningTasks(100);
	//
	// // check alive activity list
	// if (null != _aliveActivityList && _aliveActivityList.size() >= 1) {
	// for (ActivityManager.RunningTaskInfo _runningTaskInfo :
	// _aliveActivityList) {
	// // check top activity
	// if (!_runningTaskInfo.topActivity.getPackageName()
	// .equalsIgnoreCase(getPackageName())) {
	// continue;
	// } else {
	// // only one activity
	// if (1 == _runningTaskInfo.numActivities) {
	// Log.d(LOG_TAG, "Only one activity"
	// + " and activities number = "
	// + _runningTaskInfo.numActivities);
	//
	// // ??
	// } else {
	// Log.d(LOG_TAG, "Top activity = "
	// + _runningTaskInfo.topActivity.getClassName()
	// + " , base activity = "
	// + _runningTaskInfo.baseActivity.getClassName()
	// + " , number activity = "
	// + _runningTaskInfo.numActivities
	// + " and number running = "
	// + _runningTaskInfo.numRunning);
	//
	// // check activity lifecycle and state
	// for (int i = 0; i < _runningTaskInfo.numActivities; i++) {
	// //
	// }
	//
	// // normal back pressed
	// super.onBackPressed();
	// }
	// }
	// }
	// } else {
	// Log.e(LOG_TAG,
	// "Get alive activity list error, alive activity list = "
	// + _aliveActivityList);
	// }
	// }

}
