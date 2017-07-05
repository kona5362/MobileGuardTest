package com.test.mobileguardtest.service;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.db.dao.AddressDao;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AddressService extends Service {

	private TelephonyManager mTM;
	private WindowManager mWM;
	private int[] mToastStyleColorsResource;
	private View mToastView;
	private LayoutParams params;
	private int mScreenWidth;
	private int mScreenHeight;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String address = (String)msg.obj;
			tv_toast_view.setText(address);
			super.handleMessage(msg);
		}
	};
	private TextView tv_toast_view;
	private MyPhoneStateListener mPhoneStateListener;
	private InnerReceiver mInnerReceiver;

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		
		mInnerReceiver = new InnerReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mInnerReceiver, intentFilter);
		
		
		super.onCreate();
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String number= getResultData();//TODO 
			showToast(number);
		}
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if(mWM!=null&&mToastView!=null){
					mWM.removeView(mToastView);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				showToast(incomingNumber);
				break;
			}
		}
		
	}

	public void showToast(String number) {
		mToastStyleColorsResource = new int[]{
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		
		mToastView = View.inflate(this, R.layout.toast_address_view, null);
		tv_toast_view = (TextView) mToastView.findViewById(R.id.tv_toast_view);
		params = new WindowManager.LayoutParams();
		
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE; //一定得有这个 不然报错
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  //不设这个就会聚焦到这个上，导致不能挂电话，接电话
//              | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	默认能够被触摸
             | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;//透明
        params.setTitle("Toast");
       //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT+Gravity.TOP;
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_X, 0);
      	params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_Y, 0);
      	int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_STYLE, 0);
      	tv_toast_view.setBackgroundResource(mToastStyleColorsResource[toastStyleIndex]);
		
		mToastView.setOnTouchListener(new OnTouchListener() {
			//private float downX;
			//private float downY;
			private float downRawX;
			private float downRawY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {//TODO  不明白为什么只能用getRawX/Y, 用getX/Y就会抖(虽然勉强也能拖动)
				case MotionEvent.ACTION_DOWN:
					//downX = event.getX();
					//downY = event.getY();
					downRawX = event.getRawX();
					downRawY = event.getRawY();
					//Log.i("AddressService:DOWN:event.getX()", ""+event.getX());
					//Log.i("AddressService:DOWN:event.getY()", ""+event.getY());
					//Log.i("AddressService:DOWN:event.getRawX()", ""+event.getRawX());
					//Log.i("AddressService:DOWN:event.getRawY()", ""+event.getRawY());
					break;
				case MotionEvent.ACTION_MOVE:
					//float moveX = event.getX();
					//float moveY = event.getY();
					float moveRawX = event.getRawX();
					float moveRawY = event.getRawY();
					//Log.i("AddressService:MOVE:event.getX()", ""+moveX);
					//Log.i("AddressService:MOVE:event.getY()", ""+moveY);
					//Log.i("AddressService:MOVE:event.getRawX()", ""+moveRawX);
					//Log.i("AddressService:MOVE:event.getRawY()", ""+moveRawY);
					
					//float offsetX = moveX - downX;
					//float offsetY = moveY - downY;
					float offsetRawX = moveRawX - downRawX;
					float offsetRawY = moveRawY - downRawY;
					//Log.i("AddressService:offsetX", ""+offsetX);
					//Log.i("AddressService:offsetY", ""+offsetY);
					//Log.i("AddressService:offsetRawX", ""+offsetRawX);
					//Log.i("AddressService:offsetRawY", ""+offsetRawY);
					//float lastX = mToastView.getX(); 
					//float lastY = mToastView.getY(); 
					float lastX = params.x; 
					float lastY = params.y; 
					
					/*Log.i("AddressService:mToastView.getX():", ""+mToastView.getX());
					Log.i("AddressService:mToastView.getY():", ""+mToastView.getY());
					Log.i("AddressService:params.x:", ""+params.x);
					Log.i("AddressService:params.y:", ""+params.y);
					Log.i("AddressService:mToastView.getWidth():", ""+mToastView.getWidth());
					Log.i("AddressService:mToastView.getHeight():", ""+mToastView.getHeight());
					Log.i("AddressService:params.width:", ""+params.width);  //-2好像表示包裹内容
					Log.i("AddressService:params.height:", ""+params.height);  
					//根据上面的分析，得出只能用params.x/y   mToastView.getWidth/Height()
*/					
					int newX = (int) (lastX + offsetRawX);
					int newY = (int) (lastY + offsetRawY);
					
					if(newX<=0){
						newX = 0;
					}else if(newX+mToastView.getWidth()>=mScreenWidth){
						newX = mScreenWidth - mToastView.getWidth();
					}
					if(newY<=0){
						newY = 0;
					}else if(newY+mToastView.getHeight()>=mScreenHeight){
						newY = mScreenHeight - mToastView.getHeight();
					}
					
					params.x = newX; 
					params.y = newY; 
					mWM.updateViewLayout(mToastView, params);
					downRawX = moveRawX;
					downRawY = moveRawY;
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_X, params.x);
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_Y, params.y);
					break;
				}
				return true;
			}
		});
		
		mWM.addView(mToastView, params);
		queryNum(number);
	}

	private void queryNum(final String number) {
		new Thread(){
			public void run() {
				String address = AddressDao.query(number);
				Message msg = Message.obtain();
				msg.obj = address;
				mHandler.sendMessage(msg);
			};
		}.start();
		
	}
	
	@Override
	public void onDestroy() {
		if(mTM != null && mPhoneStateListener != null){
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(mInnerReceiver != null){
			unregisterReceiver(mInnerReceiver);
		}
		
		super.onDestroy();
	}
}
