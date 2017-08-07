package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.service.AddressService;
import com.test.mobileguardtest.service.BlackNumberService;
import com.test.mobileguardtest.service.WatchDogService;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.ServiceUtil;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.view.SettingClickView;
import com.test.mobileguardtest.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private String[] mToastStyleColors;
	private int mToastStyleIndex;
	private SettingClickView scv_address_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initUpdate();
		initAddress();
		initBlackNumber();
		initAppLock();
	}

	private void initAppLock() {
		final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
		//boolean appLockState = SpUtil.getBoolean(getApplicationContext(), ConstantValue.APP_LOCK, false);
		boolean running = ServiceUtil.isRunning(getApplicationContext(), "com.test.mobileguardtest.service.WatchDogService");
		siv_app_lock.setCheck(running);
		siv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (siv_app_lock.isChecked()) {
					siv_app_lock.setCheck(false);
					//SpUtil.putBoolean(getApplicationContext(),ConstantValue.APP_LOCK,false);
					Intent intent = new Intent(getApplicationContext(), WatchDogService.class);
					stopService(intent);
				}else{
					siv_app_lock.setCheck(true);
					//SpUtil.putBoolean(getApplicationContext(),ConstantValue.APP_LOCK,true);
					Intent intent = new Intent(getApplicationContext(), WatchDogService.class);
					startService(intent);
				}
			}
		});
	}

	private void initAddress() {
		initAddressToastStyle();	
		intiAddressToastPosition();
		initAddressDisplay();
	}

	private void intiAddressToastPosition() {
		SettingClickView scv_address_position = (SettingClickView) findViewById(R.id.scv_address_position);
		scv_address_position.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddressToastPositionActivity.class);
				startActivity(intent);
			}
		});
		
	}

	private void initAddressToastStyle() {
		scv_address_style = (SettingClickView) findViewById(R.id.scv_address_style);
		mToastStyleColors = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		mToastStyleIndex = SpUtil.getInt(this, ConstantValue.ADDRESS_TOAST_STYLE, 0);
		scv_address_style.setDes(mToastStyleColors[mToastStyleIndex]);
		scv_address_style.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showToastStyleDialog();
			}
		});
	}

	private void showToastStyleDialog() {
		Builder builder = new AlertDialog.Builder(this);
		//AlertDialog dialog = builder.create();
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		builder.setSingleChoiceItems(mToastStyleColors, mToastStyleIndex, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				scv_address_style.setDes(mToastStyleColors[which]);
				SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_STYLE, which);
				dialog.dismiss();//TODO
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void initAddressDisplay() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		boolean running = ServiceUtil.isRunning(this, "com.test.mobileguardtest.service.AddressService");
		siv_address.setCheck(running);
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				siv_address.setCheck(!siv_address.isChecked());
				if(siv_address.isChecked()){
					Intent intent = new Intent(getApplicationContext(), AddressService.class);
					startService(intent);
				}else{
					Intent intent = new Intent(getApplicationContext(), AddressService.class);
					stopService(intent);
				}
			}
		});
	}

	private void initBlackNumber() {
		final SettingItemView siv_black_num = (SettingItemView) findViewById(R.id.siv_black_num);
		boolean running = ServiceUtil.isRunning(this, "com.test.mobileguardtest.service.BlackNumberService");
		siv_black_num.setCheck(running);
		siv_black_num.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 siv_black_num.setCheck(!siv_black_num.isChecked());
				 if(siv_black_num.isChecked()){
					 Intent intent = new Intent(getApplicationContext(), BlackNumberService.class);
					 startService(intent);
				 }else{
					 Intent intent = new Intent(getApplicationContext(), BlackNumberService.class);
					 stopService(intent);
				 }
			}
		});
	}

	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean ischecked = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		siv_update.setCheck(ischecked);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean checked = siv_update.isChecked();
				siv_update.setCheck(!checked);
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !checked);
			}
		});
	}
}
