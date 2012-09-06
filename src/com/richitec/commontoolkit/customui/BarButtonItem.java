package com.richitec.commontoolkit.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Button;

public class BarButtonItem extends Button {

	// normal background drawable
	private Drawable _mNormalBackgroundDrawable;
	// pressed background drawable
	private Drawable _mPressedBackgroundDrawable;

	public BarButtonItem(Context context) {
		super(context);
	}

	//
	public BarButtonItem(Context context, int resId) {
		super(context);

		//
	}

	// init with button title, normal background drawable, pressed background
	// drawable and button click listener
	public BarButtonItem(Context context, CharSequence title,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		super(context);

		// set title
		setText(null == title ? "" : title);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = normalBackgroundDrawable;
		_mPressedBackgroundDrawable = pressedBackgroundDrawable;

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, CharSequence title,
			OnClickListener btnClickListener) {
		this(context, title, null, null, btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		super(context);

		// set title
		setText(titleId);

		// set normal and pressed background drawable
		_mNormalBackgroundDrawable = getResources().getDrawable(
				normalBackgroundResId);
		_mPressedBackgroundDrawable = getResources().getDrawable(
				pressedBackgroundResId);

		// set on click listener
		setOnClickListener(btnClickListener);
	}

	public BarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		this(context, context.getResources().getString(titleId),
				btnClickListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// set the button background image based on whether the button in its
		// pressed state
		if (isPressed()) {
			if (null != _mPressedBackgroundDrawable) {
				setBackgroundDrawable(_mPressedBackgroundDrawable);
			}
		} else {
			if (null != _mNormalBackgroundDrawable) {
				setBackgroundDrawable(_mNormalBackgroundDrawable);
			}
		}

		super.onDraw(canvas);
	}

}
