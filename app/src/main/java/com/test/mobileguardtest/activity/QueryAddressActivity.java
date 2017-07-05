package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.db.dao.AddressDao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAddressActivity extends Activity {
	private TextView tv_address_result;
	private String mAddress;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			tv_address_result.setText(mAddress);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
	}

	private void initUI() {
		final EditText et_phone = (EditText) findViewById(R.id.et_phone);
		final Button bt_query = (Button) findViewById(R.id.bt_query);
		tv_address_result = (TextView) findViewById(R.id.tv_address_result);
		
		bt_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					queryAddress(phone);
				}else{
					Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					/* 自定义插补器
					 * shake.setInterpolator(new Interpolator() {
						
						@Override
						public float getInterpolation(float input) {
							return 0;
						}
					});*/
					et_phone.startAnimation(shake);
					
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//vibrator.vibrate(2000);//震动两秒
					long[] pattern = new long[]{1000,2000,1000,3000};// 先等待1秒,再震动2秒,再等待1秒,再震动3秒...
					vibrator.vibrate(pattern, -1);// 参2等于-1时,表示不循环,大于等于0时,表示从以上数组的哪个位置开始循环
				}
			}
		});
		et_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				queryAddress(et_phone.getText().toString());
			}
		});
	}

	protected void queryAddress(final String phone) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.query(phone);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
