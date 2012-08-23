package com.richitec.commontoolkit.utils;

import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinUtils {

	// convert string to pinyin array
	public static ArrayList<String[]> pinyins4String(String origString) {
		ArrayList<String[]> _ret = new ArrayList<String[]>();

		// define not Chinese character string builder
		StringBuilder _notCCSB = new StringBuilder();

		// process the original string
		for (int i = 0; i < origString.length(); i++) {
			// get character converted pinyin array
			String[] _charPinyins = PinyinHelper
					.toHanyuPinyinStringArray(origString.charAt(i));

			// not Chinese character, hold the original, else add the converted
			// pinyin array
			if (null == _charPinyins) {
				// append char to not Chinese character string builder
				_notCCSB.append(origString.toLowerCase().charAt(i));
			} else {
				// add not Chinese character string builder to result and clear
				// it
				if (0 != _notCCSB.length()) {
					_ret.add(new String[] { _notCCSB.toString() });
					_notCCSB.setLength(0);
				}

				// add character pinyin array to result
				_ret.add(_charPinyins);
			}
		}

		return _ret;
	}

}
