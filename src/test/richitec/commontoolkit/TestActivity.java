package test.richitec.commontoolkit;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.utils.PinyinUtils;

public class TestActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// test by ares
		Log.d("TestActivity", "begin to test.");

		// test string to pinyin array
		ArrayList<String[]> pinyinArray = PinyinUtils
				.pinyins4String("hello12翟aREs@#绍虎簡訊 ,是 a吗");
		for (int i = 0; i < pinyinArray.size(); i++) {
			for (String string : pinyinArray.get(i)) {
				Log.d("TestActivity", "string at index " + i + " and pinyin = "
						+ string);
			}
		}

		// test addressBook manager
		AddressBookManager.getInstance().traversalAddressBook();
	}

}
