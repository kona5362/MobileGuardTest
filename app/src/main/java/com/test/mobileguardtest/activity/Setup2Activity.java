package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.utils.ToastUtil;
import com.test.mobileguardtest.view.SettingItemView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		initUI();
	}
	
	private void initUI() {
		final SettingItemView siv_bound = (SettingItemView) findViewById(R.id.siv_bound);
		String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
/*		if(!TextUtils.isEmpty(sim_number)){
			siv_bound.setCheck(true);
		}else{
			siv_bound.setCheck(false);
		}*/
		siv_bound.setCheck(!TextUtils.isEmpty(sim_number));
		
		siv_bound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isChecked = siv_bound.isChecked();
				siv_bound.setCheck(!isChecked);
				if(!isChecked){
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //TODO 忘了
					String simSerialNumber = tm.getSimSerialNumber();
					SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
				}else{
					SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
				}
			}
		});
	}

	public void prePage(View view){
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	
	public void nextPage(View view){ 
		String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
		if(!TextUtils.isEmpty(sim_number)){
			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			finish();
		}else{
			ToastUtil.show(this, "请绑定sim卡");
		}
	}
}
