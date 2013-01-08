package com.richitec.commontoolkit.customadapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public abstract class CommonListCursorAdapter extends CursorAdapter {

	private static final String LOG_TAG = "CommonListCursorAdapter";

	// layout inflater
	protected LayoutInflater _mLayoutInflater;
	// items layout resource id
	protected int _mItemsLayoutResId;
	// data keys
	protected String[] _mDataKeys;
	// items component resource identities
	protected int[] _mItemsComponentResIds;

	// cursor projection and type maps
	protected List<Map<String, Class<?>>> _mCursorProjection7TypeMaps;

	public CommonListCursorAdapter(Context context, int itemsLayoutResId,
			Cursor c, List<Map<String, Class<?>>> cursorProjection7TypeMaps,
			String[] dataKeys, int[] itemsComponentResIds) {
		super(context, c);

		// save layout inflater, items layout resource id, data keys and items
		// component resource identities
		_mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mItemsLayoutResId = itemsLayoutResId;
		_mDataKeys = dataKeys;
		_mItemsComponentResIds = itemsComponentResIds;

		// save cursor projection and type maps
		_mCursorProjection7TypeMaps = cursorProjection7TypeMaps;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// set item component view subViews
		for (int i = 0; i < _mItemsComponentResIds.length; i++) {
			// generate bind data map
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// define data value
			Object _dataValue = null;

			// get cursor projection and its type
			String _projection = _mCursorProjection7TypeMaps.get(i).keySet()
					.iterator().next();
			Class<?> _projectionType = _mCursorProjection7TypeMaps.get(i).get(
					_projection);

			// check projection type and update data value
			if (Short.class.equals(_projectionType)) {
				// short value
				_dataValue = cursor
						.getShort(cursor.getColumnIndex(_projection));
			} else if (Integer.class.equals(_projectionType)) {
				// integer value
				_dataValue = cursor.getInt(cursor.getColumnIndex(_projection));
			} else if (Long.class.equals(_projectionType)) {
				// long value
				_dataValue = cursor.getLong(cursor.getColumnIndex(_projection));
			} else if (Float.class.equals(_projectionType)) {
				// float value
				_dataValue = cursor
						.getFloat(cursor.getColumnIndex(_projection));
			} else if (Double.class.equals(_projectionType)) {
				// double value
				_dataValue = cursor.getDouble(cursor
						.getColumnIndex(_projection));
			} else if (String.class.equals(_projectionType)) {
				// String value
				_dataValue = cursor.getString(cursor
						.getColumnIndex(_projection));
			} else if (Byte[].class.equals(_projectionType)) {
				// bytes value
				_dataValue = cursor.getBlob(cursor.getColumnIndex(_projection));
			} else {
				// get cursor value error
				Log.e(LOG_TAG, "Get cursor value error, projection = "
						+ _projection + " , projection type = "
						+ _projectionType + " and cursor = " + cursor);
			}

			// recombination data
			recombinationData(_dataMap, _mDataKeys[i], _dataValue);

			// bind item component view data
			bindView(view.findViewById(_mItemsComponentResIds[i]), _dataMap,
					_mDataKeys[i]);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return _mLayoutInflater.inflate(_mItemsLayoutResId, parent, false);
	}

	// recombination data
	protected abstract void recombinationData(Map<String, Object> dataMap,
			String dataKey, Object dataValue);

	// bind view and data
	protected abstract void bindView(View view, Map<String, ?> dataMap,
			String dataKey);

}
