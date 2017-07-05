package com.test.mobileguardtest.activity;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private GridView gv_menu;
	private String[] mFuncStrs;
	private int[] mIcons;
	protected String tag = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		initUI();//初始化UI
		initData();//初始化数据
		
		
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mFuncStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mFuncStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_gv_menu, null);
			TextView tv_func_title = (TextView) view.findViewById(R.id.tv_func_title);
			ImageView iv_func_icon = (ImageView) view.findViewById(R.id.iv_func_icon);
			tv_func_title.setText(mFuncStrs[position]);
			iv_func_icon.setBackgroundResource(mIcons[position]);
			return view;
		}
		
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		mFuncStrs = new String[]{"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级功能","设置中心"};
		mIcons = new int[]{
				R.drawable.home_safe,R.drawable.home_callmsgsafe,
				R.drawable.home_apps,R.drawable.home_taskmanager,
				R.drawable.home_netmanager,R.drawable.home_trojan,
				R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
		};
		
		gv_menu.setAdapter(new MyAdapter());
		gv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position){
				case 0:
					showPsdDialog();
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
					break;
				case 2:
					startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					startActivity(new Intent(getApplicationContext(),AtoolActivity.class));
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	private void showPsdDialog() {
		String psd = SpUtil.getString(this,ConstantValue.MOBILE_SAFE_PWD,"");
		if(TextUtils.isEmpty(psd)){
			showSetPsdDialog();
		}else{
			showComfirmDialog();
		}
	}

	
	private void showComfirmDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD,"");
				String confirmPsd = et_confirm_psd.getText().toString();
				if(!TextUtils.isEmpty(confirmPsd)){
					if(psd.equals(confirmPsd)){
						Intent intent = new Intent(getApplicationContext(), AntiTheftActivity.class);
						startActivity(intent);
						dialog.dismiss();						
					}else{
						ToastUtil.show(getApplicationContext(), "密码错误");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		
		bt_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setView(view);
		dialog.show();
	}

	private void showSetPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_psd, null);
		
		final EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
		final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		//final String psd = et_set_psd.getText().toString();   //TODO 错误1：写成et_set_psd.toString()了！！！这个错误犯几次了！
		//final String confirmPsd = et_confirm_psd.getText().toString();//TODO 错误2：写在监听外面，现在都还没创建，用户都没输入，哪来的值！？
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();
				if(!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(confirmPsd)){
					if(psd.equals(confirmPsd)){
						Intent intent = new Intent(getApplicationContext(), AntiTheftActivity.class);
						startActivity(intent);
						dialog.dismiss();
						SpUtil.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PWD,psd);
					}else{
						ToastUtil.show(getApplicationContext(), "两次密码不一致");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		
		bt_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		gv_menu = (GridView) findViewById(R.id.gv_menu);
	}
}
