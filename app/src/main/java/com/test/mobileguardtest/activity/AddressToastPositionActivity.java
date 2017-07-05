package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddressToastPositionActivity extends Activity {
	private float downX;
	private float downY;
	private float moveX;
	private float moveY;
	private TextView tv_tip1;
	private TextView tv_tip2;
	private ImageButton ib_drag;
	private WindowManager mWM;
	private int screenHeight;
	private int screenWidth;
	private long[] mHits = new long[2];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_toast_potion);
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		initView();
	}

	private void initView() {
		//RelativeLayout rl_address_toast_position = (RelativeLayout) findViewById(R.id.rl_address_toast_position);
		ib_drag = (ImageButton) findViewById(R.id.ib_drag);
		tv_tip1 = (TextView) findViewById(R.id.tv_tip1);
		tv_tip2 = (TextView) findViewById(R.id.tv_tip2);
		Point size = new Point();
		mWM.getDefaultDisplay().getSize(size);
		screenWidth = size.x;
		screenHeight = size.y - 30;
		//Log.i("AddressToastPositionActivity:screenWidth", screenWidth+"");
		//Log.i("AddressToastPositionActivity:screenHeight", screenHeight+"");
		//Log.i("AddressToastPositionActivity:getWidth():", ""+rl_address_toast_position.getWidth());
		//Log.i("AddressToastPositionActivity:getHeight():", ""+rl_address_toast_position.getHeight());
		int positionX = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_X, 0);
      	int positionY = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_Y, 0);
      	ib_drag.setX(positionX);
      	ib_drag.setY(positionY);
      	if(positionY>screenHeight/2){
			tv_tip1.setVisibility(View.VISIBLE);
			tv_tip2.setVisibility(View.INVISIBLE);
		}else{
			tv_tip1.setVisibility(View.INVISIBLE);
			tv_tip2.setVisibility(View.VISIBLE);
		}
      	
      	ib_drag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = event.getX(); //getX和getRawX 区别很大  ， 不过这里只是用来计算偏移量，所以OK的
					downY = event.getY();
					Log.i("downX", ""+event.getX());
					Log.i("downY", ""+event.getY());
					
					break;
				case MotionEvent.ACTION_MOVE:
					moveX = event.getX();
					moveY = event.getY();
					
					float offsetX = moveX - downX;
					float offsetY = moveY - downY;
					
					float lastX = ib_drag.getX(); 
					float lastY = ib_drag.getY(); 
					
					
					int newX = (int) (lastX + offsetX);
					int newY = (int) (lastY + offsetY);
					if(newX<=0){
						newX = 0;
						//return true;
					}else if(newX+ib_drag.getWidth()>=screenWidth){
						newX = screenWidth - ib_drag.getWidth();
						//return true;
					}
					if(newY<=0){
						newY = 0;
						//return true;
					}else if(newY+ib_drag.getHeight()>=screenHeight){
						newY = screenHeight - ib_drag.getHeight();
						//return true;
					}
					
					if(newY>screenHeight/2){
						tv_tip1.setVisibility(View.VISIBLE);
						tv_tip2.setVisibility(View.INVISIBLE);
					}else{
						tv_tip1.setVisibility(View.INVISIBLE);
						tv_tip2.setVisibility(View.VISIBLE);
					}
					
					
					ib_drag.setX(newX); 
					ib_drag.setY(newY); 
					
					
					/*int left = ib_drag.getLeft();
					int right = ib_drag.getRight();
					int top = ib_drag.getTop();
					int bottom = ib_drag.getBottom();
					ib_drag.setLeft((int) (left+offsetX));
					ib_drag.setRight((int) (right+offsetX));
					ib_drag.setTop((int) (top+offsetY));
					ib_drag.setBottom((int) (bottom+offsetY));*/
					
					break;
				case MotionEvent.ACTION_UP:
					int x = (int) ib_drag.getX();
					int y = (int) ib_drag.getY();
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_X, x);
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_Y, y);
					break;
				}
				return false;
			}
		});
		
		ib_drag.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				long uptimeMillis = SystemClock.uptimeMillis();//TODO
				mHits[mHits.length-1] = uptimeMillis;
				if(mHits[mHits.length-1]-mHits[0]<500){
					//算双/多击了
					ib_drag.setX(screenWidth/2 - ib_drag.getWidth()/2);
					ib_drag.setY(screenHeight/2 - ib_drag.getHeight()/2);
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_X, (int)ib_drag.getX());
					SpUtil.putInt(getApplicationContext(), ConstantValue.ADDRESS_TOAST_POSITION_Y, (int)ib_drag.getY());
				}
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);//TODO//TODO//TODO
			}
		});
		
	}
}
