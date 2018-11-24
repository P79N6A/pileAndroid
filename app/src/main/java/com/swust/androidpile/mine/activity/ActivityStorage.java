package com.swust.androidpile.mine.activity;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

public class ActivityStorage extends Activity {

	private static Map<String,Activity> map = new HashMap<String,Activity>();
	
	public static Map<String,Activity> getMap(){
		return map;
	}
	
	public static void exit() {
		for(String str:map.keySet()){
			map.get(str).finish();
		}
		map.clear();
	}
}
