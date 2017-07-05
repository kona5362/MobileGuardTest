package com.test.mobileguardtest.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.test.mobileguardtest.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {

	private TelephonyManager mTM;
	private BlackNumberDao mDao;
	private MyPhoneStateListener myPhoneStateListener;
	private InnerSmsReceiver innerSmsReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		mDao = BlackNumberDao.getInstance(this);
		
			
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		innerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(innerSmsReceiver, intentFilter);
		
		
		super.onCreate();
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
				String address = smsMessage.getOriginatingAddress();
				String body = smsMessage.getMessageBody();
				int mode = mDao.getMode(address);
				if(mode == 0 || mode == 3){
					abortBroadcast();
				}
			}
		}
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);
				break;
				
			}
		}
	}

	public void endCall(String phone) {

/*		List<BlackNumberInfo> list = blackNumberDao.queryAll();
		for (BlackNumberInfo info : list) {
			if(info.getPhone().equals(incomingNumber)){
				if(info.getMode() == 1 || info.getMode() == 2){
					try {
						Class<?> clazz = Class.forName("android.os.ServiceManager");
						Method method = clazz.getMethod("getService", String.class);
						IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
						ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
						iTelephony.endCall();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
		}*/
		
		int mode = mDao.getMode(phone);
		if(mode == 1 || mode == 2){
			try {
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService", String.class);
				IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new MyContentObeserve(new Handler(),phone));
		}
		
	}
	
	class MyContentObeserve extends ContentObserver{
		
		private String phone;
		public MyContentObeserve(Handler handler,String phone) {
			super(handler);
			this.phone = phone;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
			super.onChange(selfChange);
		}
		
	}
	@Override
	public void onDestroy() {
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(innerSmsReceiver);
		
		super.onDestroy();
	}
}
