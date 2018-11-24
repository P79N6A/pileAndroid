package com.swust.androidpile.home.citypiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.swust.androidpile.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 城市列表选择页面
 */
public class CityPilesActivity extends Activity {
	private ListView listView;
	private SortAdapter sortadapter;
	private List<PersonBean> data;
	private SideBar sidebar;
	private TextView dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_citypiles);
		init();
	}

	
	private List<PersonBean> getData(String[] data) {
		List<PersonBean> listarray = new ArrayList<PersonBean>();
		for (int i = 0; i < data.length; i++) {
			//获取名字的拼音
			String pinyin = PinyinUtils.getPingYin(data[i]);
			//获取名字拼音的大写首字母
			String Fpinyin = pinyin.substring(0, 1).toUpperCase();

			PersonBean person = new PersonBean();
			person.setName(data[i]);
			person.setPinYin(pinyin);
			// 正则表达式，判断首字母是否是英文字母
			if (Fpinyin.matches("[A-Z]")) {
				person.setFirstPinYin(Fpinyin);
			} else {
				person.setFirstPinYin("#");
			}

			listarray.add(person);
		}
		return listarray;

	}

	private void init() {
		// TODO Auto-generated method stub
		sidebar = (SideBar) findViewById(R.id.sidebar);
		listView = (ListView) findViewById(R.id.listview);
		dialog = (TextView) findViewById(R.id.dialog);
		sidebar.setTextView(dialog);
		// 设置字母导航触摸监听
		sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				// 该字母首次出现的位置
				int position = sortadapter.getPositionForSelection(s.charAt(0));

				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
		// List<PersonBean> data;
		data = getData(getResources().getStringArray(R.array.listpersons));
		// 数据在放在adapter之前需要排序
		Collections.sort(data, new PinyinComparator());
		//SortAdapter sortadapter;
		sortadapter = new SortAdapter(this, data);
		listView.setAdapter(sortadapter);
		
		//监听列表点击事件
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
				String result=parent.getItemAtPosition(pos).toString();
//				Toast.makeText(CityPilesActivity.this, result, Toast.LENGTH_SHORT).show();
				Intent intent=getIntent();
				intent.putExtra("city", result);
				setResult(0x101, intent); 
				finish();
			}
		});
	}

}