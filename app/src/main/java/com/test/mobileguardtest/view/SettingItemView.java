package com.test.mobileguardtest.view;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private CheckBox cb_box;
	private String deson;
	private String desoff;
	private TextView tv_des;
	private TextView tv_title;

	public SettingItemView(Context context) {
		this(context,null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View.inflate(context, R.layout.setting_item_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		
		String destitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "destitle");
		desoff = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desoff");
		deson = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "deson");
	
		tv_title.setText(destitle);
/*		if(cb_box.isChecked()){
			tv_des.setText(deson);
		}else{
			tv_des.setText(desoff);
		}*/
	}

	public boolean isChecked(){
		return cb_box.isChecked();
	}
	
	public void setCheck(boolean ischecked) {
		cb_box.setChecked(ischecked);
		if(ischecked){
			tv_des.setText(deson);
		}else{
			tv_des.setText(desoff);
		}
	}
	
}
