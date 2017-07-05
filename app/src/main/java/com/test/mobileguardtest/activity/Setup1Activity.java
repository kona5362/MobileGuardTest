package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	public void nextPage(View view){ //必须public
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
	}
}
