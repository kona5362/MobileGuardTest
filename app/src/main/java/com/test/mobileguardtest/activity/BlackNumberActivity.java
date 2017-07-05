package com.test.mobileguardtest.activity;

import java.util.List;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.BlackNumberInfo;
import com.test.mobileguardtest.db.dao.BlackNumberDao;
import com.test.mobileguardtest.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents.Insert;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private Button bt_add;
	private ListView lv_blacknum_list;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mBlackNumberInfoList;
	private MyAdapter mAdapter;
	private Handler mHandler = new Handler(){
		

		@Override
		public void handleMessage(Message msg) {
			mAdapter = new MyAdapter();
			lv_blacknum_list.setAdapter(mAdapter);
			super.handleMessage(msg);
		}
	};
	private int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		
		initUI();
		initData();
		initEvent();
	}

	private void initEvent() {
		bt_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAddDialog();
			}
		});
	}

	protected void showAddDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();//TODO 忘了
		View view = View.inflate(this, R.layout.dialog_add_black_number, null);
		dialog.setView(view,0,0,0,0);
		final EditText et_number = (EditText) view.findViewById(R.id.et_number);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		mode = 0;
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 0;
					break;
				case R.id.rb_phone:
					mode = 1;
					break;
				case R.id.rb_all:
					mode = 2;
					break;
				}
			}
		});
		
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_number.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					mDao.insert(phone, mode);
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.setPhone(phone);
					blackNumberInfo.setMode(mode);
					mBlackNumberInfoList.add(0, blackNumberInfo);
					if(mAdapter != null){
						mAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "号码不能为空！");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void initData() {
		mDao = BlackNumberDao.getInstance(this);
		new Thread(){
			public void run() {
				mBlackNumberInfoList = mDao.queryAll();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknum_list = (ListView) findViewById(R.id.lv_blacknum_list);
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mBlackNumberInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.item_lv_black_num, null);
				holder = new HolderView();
				holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.ib_remove =  (ImageButton) convertView.findViewById(R.id.ib_remove);
				convertView.setTag(holder);
			}else{
				holder = (HolderView) convertView.getTag();
			}
			
			final BlackNumberInfo info = mBlackNumberInfoList.get(position);
			holder.tv_number.setText(info.getPhone());
			switch (info.getMode()) {
			case 0:
				holder.tv_mode.setText("拦截短信");
				break;
			case 1:
				holder.tv_mode.setText("拦截电话");
				break;
			case 2:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			
			holder.ib_remove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDao.delete(info.getPhone());
					mBlackNumberInfoList.remove(info);
					mAdapter.notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
	}
	
	private class HolderView{
		public TextView tv_number;
		public TextView tv_mode;
		public ImageButton ib_remove;
	}
}
