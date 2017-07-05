package com.test.mobileguardtest.activity;

import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.mobileguardtest.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {
	private ListView lv_contactlist;
	private List<HashMap<String,String>> list;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			lv_contactlist.setAdapter(new MyAdapter());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);
		
		initUI();
		initData();
	}

	private void initData() {
		 //TODO 可能是耗时操作(数据量多时) 
		new Thread(){
			public void run() {
				list = new ArrayList<HashMap<String,String>>();
				ContentResolver contentResolver = getContentResolver();
				Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
						new String[]{"contact_id"}, null, null, null);
				while(cursor.moveToNext()){
					String id = cursor.getString(0);
					if(TextUtils.isEmpty(id)){
						continue;
					}
					Cursor cursorList = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
							new String[]{"mimetype","data1"},
							"raw_contact_id = ?",
							new String[]{id}, null);
					HashMap<String, String> hashMap = new HashMap<String,String>();
					while(cursorList.moveToNext()){
						String type = cursorList.getString(0);
						String data= cursorList.getString(1);
						switch (type) {
						case "vnd.android.cursor.item/name":
							hashMap.put("name", data);
							break;
						case "vnd.android.cursor.item/phone_v2":
							hashMap.put("phone", data);
							break;
						default:
							break;
						}
					}
					list.add(hashMap);
					cursorList.close();
				}
				cursor.close();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		lv_contactlist = (ListView) findViewById(R.id.lv_contactlist);
		
		lv_contactlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = ((HashMap<String,String>)parent.getItemAtPosition(position)).get("phone").toString();
				Intent intent = new Intent();
				intent.putExtra("phone", phone);//TODO
				setResult(0, intent);
				finish();
			}
		});
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_lv_context, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			tv_name.setText(list.get(position).get("name").toString());
			tv_phone.setText(list.get(position).get("phone").toString());
			
			return view;
		}
		
	}
}
