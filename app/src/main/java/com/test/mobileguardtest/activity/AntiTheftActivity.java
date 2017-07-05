package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;

public class AntiTheftActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if(!setup_over){
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}else{
			setContentView(R.layout.activity_antitheft);
			initUI();
		}

	}

	private void initUI() {
		TextView tv_safe_num = (TextView) findViewById(R.id.tv_safe_num);
		TextView tv_reset = (TextView) findViewById(R.id.tv_reset);
		
		String phone = SpUtil.getString(this, ConstantValue.CONTACT_NUMBER, "");
		if(!TextUtils.isEmpty(phone)){
			tv_safe_num.setText(phone);
		}
		
		tv_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
