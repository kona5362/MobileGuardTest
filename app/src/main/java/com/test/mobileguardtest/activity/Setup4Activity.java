package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends Activity {
	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		initUI();
	}
	
	private void initUI() {
		
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		cb_box.setChecked(setup_over);
		if(setup_over){
			cb_box.setText("您已经开启防盗保护");
		}else{
			cb_box.setText("您没有开启防盗保护");
		}
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					SpUtil.putBoolean(getApplicationContext(), ConstantValue.SETUP_OVER, isChecked);
					if(isChecked){
						cb_box.setText("您已经开启防盗保护");
					}else{
						cb_box.setText("您没有开启防盗保护");
					}
			}
		});
		
	}

	public void prePage(View view){
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
	}
	
	public void nextPage(View view){ 
		if(cb_box.isChecked()){
			Intent intent = new Intent(this, AntiTheftActivity.class);
			startActivity(intent);
			finish();
		}else{
			ToastUtil.show(this, "请开启防盗保护");
		}
	}
}
