package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AtoolActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		initAddress();
		initAppLock();
	}

	private void initAppLock() {
		TextView tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
		tv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
			}
		});
	}

	private void initAddress() {
		TextView tv_address = (TextView) findViewById(R.id.tv_address);
		tv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),QueryAddressActivity.class);
				startActivity(intent);
			}
		});
	}
}
