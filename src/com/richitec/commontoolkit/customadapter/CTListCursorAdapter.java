package com.richitec.commontoolkit.customadapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public abstract class CTListCursorAdapter extends CursorAdapter {

	private static final String LOG_TAG = CTListCursorAdapter.class
			.getCanonicalName();

	// context
	protected Context _mContext;
	// layout inflater
	protected LayoutInflater _mLayoutInflater;
	// items layout resource id
	protected int _mItemsLayoutResId;
	// data keys
	protected String[] _mDataKeys;
	// items component resource identities
	protected int[] _mItemsComponentResIds;

	// cursor data list
	protected List<Object> _mData;

	// cursor
	private Cursor _mCursor;

	public CTListCursorAdapter(Context context, int itemsLayoutResId, Cursor c,
			String[] dataKeys, int[] itemsComponentResIds) {
		super(context, c);

		// initialize commonToolkit list cursor adapter
		initCTListCursorAdapter(context, itemsLayoutResId, c, dataKeys,
				itemsComponentResIds);
	}

	public CTListCursorAdapter(Context context, int itemsLayoutResId, Cursor c,
			String[] dataKeys, int[] itemsComponentResIds, boolean autoRequery) {
		super(context, c, autoRequery);

		// initialize commonToolkit list cursor adapter
		initCTListCursorAdapter(context, itemsLayoutResId, c, dataKeys,
				itemsComponentResIds);
	}

	// initialize commonToolkit list cursor adapter
	private void initCTListCursorAdapter(Context context, int itemsLayoutResId,
			Cursor c, String[] dataKeys, int[] itemsComponentResIds) {
		// save context, layout inflater, items layout resource id, data keys
		// and items component resource identities
		_mContext = context;
		_mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mItemsLayoutResId = itemsLayoutResId;
		_mDataKeys = dataKeys;
		_mItemsComponentResIds = itemsComponentResIds;

		// init cursor data list
		_mData = new ArrayList<Object>();

		// save cursor
		_mCursor = c;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// check cursor
		if (_mCursor != cursor) {
			// reset data with cursor
			// clear old cursor data list
			_mData.clear();

			// append cursor data
			do {
				appendCursorData(_mData, cursor);
			} while (cursor.moveToPrevious());

			// reverse data list
			Collections.reverse(_mData);

			// cursor recover
			cursor.move(_mData.size());

			// update cursor
			_mCursor = cursor;
		} else {
			// check cursor position and append cursor data
			if (cursor.getPosition() >= _mData.size()) {
				appendCursorData(_mData, cursor);
			} else {
				Log.d(LOG_TAG, "Rollback, mustn't append cursor data");
			}
		}

		// set item component view subViews
		for (int i = 0; i < _mItemsComponentResIds.length; i++) {
			// recombination data and bind item component view data
			bindView(
					((CTAdapterViewHolder) view.getTag()).getViews4Holding()
							.get(_mItemsComponentResIds[i]),
					recombinationData(_mDataKeys[i],
							_mData.get(cursor.getPosition())), _mDataKeys[i]);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// define and int view holder object
		CTAdapterViewHolder _viewHolder = new CTAdapterViewHolder();

		// inflate convert view
		View _convertView = _mLayoutInflater.inflate(_mItemsLayoutResId,
				parent, false);

		// set item component view subViews
		for (int i = 0; i < _mItemsComponentResIds.length; i++) {
			_viewHolder.getViews4Holding().append(_mItemsComponentResIds[i],
					_convertView.findViewById(_mItemsComponentResIds[i]));
		}

		// set tag
		_convertView.setTag(_viewHolder);

		return _convertView;
	}

	public List<Object> getDataList() {
		return _mData;
	}

	// set data, important only for data restore
	public void setData(List<Object> data) {
		// set data
		_mData = data;
	}

	// append cursor data
	protected abstract void appendCursorData(List<Object> data, Cursor cursor);

	// recombination data
	protected abstract Map<String, ?> recombinationData(String dataKey,
			Object dataObject);

	// bind view and data
	protected abstract void bindView(View view, Map<String, ?> dataMap,
			String dataKey);

}
