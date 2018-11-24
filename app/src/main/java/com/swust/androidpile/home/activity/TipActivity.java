package com.swust.androidpile.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.swust.androidpile.R;

public class TipActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_tip);
		init();
	}

	private void init() {
		setListener();
	}

	private void setListener() {
		findViewById(R.id.back).setOnClickListener(this);;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:
			finish();
			break;
		}
	}

}
