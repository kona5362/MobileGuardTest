package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends Activity {
	private EditText et_phone_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		initUI();
	}
	
	private void initUI() {
		Button bt_select_contact = (Button) findViewById(R.id.bt_select_contact);
		bt_select_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		String phone = SpUtil.getString(this, ConstantValue.CONTACT_NUMBER, "");
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_phone_number.setText(phone); 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");
			phone = phone.replace("-", "").replace(" ", "");
			et_phone_number.setText(phone);
		}
	}
	
																																																							public void prePage(View view){
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
	}
	
	public void nextPage(View view){ 
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		String phone = et_phone_number.getText().toString();
		SpUtil.putString(this, ConstantValue.CONTACT_NUMBER, phone);
	}
}
