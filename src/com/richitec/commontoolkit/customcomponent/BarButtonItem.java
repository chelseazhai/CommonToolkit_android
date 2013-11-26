package com.richitec.commontoolkit.customcomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.richitec.commontoolkit.R;

public class BarButtonItem extends Button {

	// normal background drawable
	private Drawable _mNormalBackgroundDrawable;
	// pressed background drawable
	private Drawable _mPressedBackgroundDrawable;
	// disable background drawable
	private Drawable _mDisableBackgroundDrawable;

	public BarButtonItem(Context context) {
		super(context);
	}

	// private constructor using bar button item style and normal background
	// drawable
	private BarButtonItem(Context context, BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable) {
		this(context);

		// check bar button item style
		switch (barBtnItemStyle) {
		case LEFT_BACK:
			// set normal background drawable
			setBackgroundDrawable(null != normalBackgroundDrawable ? normalBackgroundDrawable
					: leftBarBtnItemNormalDrawable());
			break;

		case RIGHT_GO:
		default:
			// set normal background drawable
			setBackgroundDrawable(null != normalBackgroundDrawable ? normalBackgroundDrawable
					: rightBarBtnItemNormalDrawable());
			break;
		}
	}

	// init with button resource id
	public BarButtonItem(Context context, int resId) {
		this(context);

		//
	}

	// init with button title, bar button item style, normal background
	// drawable, pressed background drawable and button click listener
	public BarButtonItem(Context context, CharSequence title,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, normalBackgroundDrawable);

		// set title and title color
		setText(null == title ? "" : title);
		setTextColor(Color.WHITE);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, CharSequence title,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable,
			Drawable disableBackgroundDrawable, OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, normalBackgroundDrawable);

		// set title and title color
		setText(null == title ? "" : title);
		setTextColor(Color.WHITE);

		// set normal, pressed and disable background drawable
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;
		_mDisableBackgroundDrawable = disableBackgroundDrawable;

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, CharSequence title,
			OnClickListener btnClickListener) {
		this(context, title, BarButtonItemStyle.RIGHT_GO, null, null,
				btnClickListener);
	}

	public BarButtonItem(Context context, CharSequence title,
			BarButtonItemStyle barBtnItemStyle, OnClickListener btnClickListener) {
		this(
				context,
				title,
				barBtnItemStyle,
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources().getDrawable(
								R.drawable.img_leftbarbtnitem_normal_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_rightbarbtnitem_normal_bg)
								: null),
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources().getDrawable(
								R.drawable.img_leftbarbtnitem_touchdown_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_rightbarbtnitem_touchdown_bg)
								: null), btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		this(context, BarButtonItemStyle.RIGHT_GO, context.getResources()
				.getDrawable(normalBackgroundResId));

		// set title and title color
		setText(titleId);
		setTextColor(Color.WHITE);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = getResources().getDrawable(
				normalBackgroundResId);
		_mPressedBackgroundDrawable = getResources().getDrawable(
				pressedBackgroundResId);

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			int disableBackgroundResId, OnClickListener btnClickListener) {
		this(context, BarButtonItemStyle.RIGHT_GO, context.getResources()
				.getDrawable(normalBackgroundResId));

		// set title and title color
		setText(titleId);
		setTextColor(Color.WHITE);

		// set normal, pressed and disable background drawable
		_mNormalBackgroundDrawable = getResources().getDrawable(
				normalBackgroundResId);
		_mPressedBackgroundDrawable = getResources().getDrawable(
				pressedBackgroundResId);
		_mDisableBackgroundDrawable = getResources().getDrawable(
				disableBackgroundResId);

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		this(context, context.getResources().getString(titleId),
				btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			BarButtonItemStyle barBtnItemStyle, OnClickListener btnClickListener) {
		this(context, context.getResources().getString(titleId),
				barBtnItemStyle, btnClickListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// set the button background image based on whether the button in its
		// disable and pressed state
		if (!isEnabled()) {
			if (null != _mDisableBackgroundDrawable) {
				setBackgroundDrawable(_mDisableBackgroundDrawable);
			}
		} else {
			if (isPressed()) {
				if (null != _mPressedBackgroundDrawable) {
					setBackgroundDrawable(_mPressedBackgroundDrawable);
				}
			} else {
				if (null != _mNormalBackgroundDrawable) {
					setBackgroundDrawable(_mNormalBackgroundDrawable);
				}
			}
		}

		super.onDraw(canvas);
	}

	// left bar button item normal drawable
	protected Drawable leftBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_leftbarbtnitem_normal_bg);
	}

	// right bar button item normal drawable
	protected Drawable rightBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_rightbarbtnitem_normal_bg);
	}

	// inner class
	// bar button item style
	public enum BarButtonItemStyle {
		LEFT_BACK, RIGHT_GO
	}

}
