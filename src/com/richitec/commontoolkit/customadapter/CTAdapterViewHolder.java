package com.richitec.commontoolkit.customadapter;

import android.util.SparseArray;
import android.view.View;

public class CTAdapterViewHolder {

	// views for holding
	private SparseArray<View> views4Holding;

	public CTAdapterViewHolder() {
		super();

		// init views sparse array for holding
		views4Holding = new SparseArray<View>();
	}

	public SparseArray<View> getViews4Holding() {
		return views4Holding;
	}

}
