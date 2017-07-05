package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.AppInfo;
import com.test.mobileguardtest.utils.AppInfoParser;
import com.test.mobileguardtest.utils.DensityUtils;
import com.test.mobileguardtest.view.ProgressDesView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static android.os.Environment.getDataDirectory;

public class AppManagerActivity extends Activity {

    private ListView lv_app_list;
    private TextView tv_sd_card;
    private TextView tv_memory;
    private ProgressDesView pdv_memory;
    private ProgressDesView pdv_sd_memory;
    private ProgressBar pb;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lv_app_list.setAdapter(new AppListAdapter());
            pb.setVisibility(View.INVISIBLE);
            super.handleMessage(msg);
        }

    };
    private List<AppInfo> mUserAppList;
    private List<AppInfo> mSystemAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
    }

    private void initUI() {
       // tv_memory = (TextView) findViewById(R.id.tv_memory);
        //tv_sd_card = (TextView) findViewById(R.id.tv_sd_card);
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        pdv_memory = (ProgressDesView) findViewById(R.id.pdv_memory);
        pdv_sd_memory = (ProgressDesView) findViewById(R.id.pdv_sd_memory);
        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
    }

    private void initData() {
        //剩余内存
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //总内存
        long romTotalSpace = Environment.getDataDirectory().getTotalSpace();
        long romSpaceUsed = romTotalSpace - romFreeSpace;
//        设置进度条
        pdv_memory.setProgress((int) (romFreeSpace * 100 / romTotalSpace + 0.5f));
        pdv_memory.setTitle("内存：");
      /*  pdv_memory.setMemoryAvailable(romFreeSpace);
        pdv_memory.setMemoryUsed(romSpaceUsed);*/
        pdv_memory.setMemoryAvailable(android.text.format.Formatter.formatFileSize(this,romFreeSpace));
        pdv_memory.setMemoryUsed(android.text.format.Formatter.formatFileSize(this,romSpaceUsed));

        //剩余内存
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //总内存
        long sdTotalSpace = Environment.getExternalStorageDirectory().getTotalSpace();
        long sdSpaceUsed = sdTotalSpace - sdFreeSpace;
        pdv_sd_memory.setProgress((int) (sdFreeSpace * 100 / sdTotalSpace + 0.5f));
        pdv_sd_memory.setTitle("sd卡：");
        pdv_sd_memory.setMemoryAvailable(android.text.format.Formatter.formatFileSize(this,sdFreeSpace));
        pdv_sd_memory.setMemoryUsed(android.text.format.Formatter.formatFileSize(this,sdSpaceUsed));


        new Thread(){
            @Override
            public void run() {
                super.run();
                List<AppInfo> mAppInfos = AppInfoParser.getAppInfoList(getApplicationContext());
                mUserAppList = new ArrayList<AppInfo>();
                mSystemAppList = new ArrayList<AppInfo>();
                for (AppInfo info : mAppInfos) {
                    if(info.isUserApp){
                        mUserAppList.add(info);
                    }else{
                        mSystemAppList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class AppListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mUserAppList.size()+mSystemAppList.size()+2;
        }

        @Override
        public AppInfo getItem(int position) {
            if(position == 0){
                return null;
            }else if(position == mUserAppList.size()+1){
                return null;
            }else if(position <= mUserAppList.size()){
                return mUserAppList.get(position - 1);
            }else{
                return mSystemAppList.get(position - mUserAppList.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0) {
                TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                textView.setText("用户应用：" + mUserAppList.size() + "个");
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.GRAY);
                textView.setPadding(
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5));
                return textView;
            }
            if (position == mUserAppList.size() + 1) {
                TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                textView.setText("系统应用：" + mSystemAppList.size() + "个");
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.GRAY);
                textView.setPadding(
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5));
                return textView;
            }


             /* if (convertView == null || !(convertView instanceof LinearLayout)) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_lv_app, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_app_position = (TextView) convertView.findViewById(R.id.tv_app_position);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }*/


            ViewHolder holder = null;
            if(convertView!=null && convertView instanceof LinearLayout){
                holder = (ViewHolder) convertView.getTag();
            }else{
                convertView = View.inflate(getApplicationContext(), R.layout.item_lv_app, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_app_position = (TextView) convertView.findViewById(R.id.tv_app_position);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
                convertView.setTag(holder);
            }
            AppInfo item = getItem(position);
            holder.iv_app_icon.setImageDrawable(item.icon);
            holder.tv_app_name.setText(item.name);
            if(item.isRom){
                holder.tv_app_position.setText("手机内存");
            }else{
                holder.tv_app_position.setText("SD卡");
            }
            holder.tv_app_size.setText(android.text.format.Formatter.formatFileSize(getApplicationContext(),item.appSize));
            return convertView;
        }
    }

    static class ViewHolder{
        public ImageView iv_app_icon;
        public TextView tv_app_name;
        public TextView tv_app_position;
        public TextView tv_app_size;
    }

}
