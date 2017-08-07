package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.AppInfo;
import com.test.mobileguardtest.db.dao.AppLockDao;
import com.test.mobileguardtest.utils.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2017/8/4.
 */

public class AppLockActivity extends Activity {

    private TextView tvAppLockDes;
    private ListView lvAppUnlock;
    private ListView lvAppLock;
    private Button btLockYes;
    private Button btLockNo;
    private AppLockDao mAppLockDao;
    private List<AppInfo> mUnLockList;
    private List<AppInfo> mLockList;
    private AppLockAdapter mLockAdapter;
    private AppLockAdapter mUnLockAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mLockAdapter = new AppLockAdapter(mLockList, true);
            mUnLockAdapter = new AppLockAdapter(mUnLockList, false);
            lvAppLock.setAdapter(mLockAdapter);
            lvAppUnlock.setAdapter(mUnLockAdapter);
            tvAppLockDes.setText("未加锁应用："+mUnLockList.size());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool_app_lock);
        initUI();
        initData();
        initListener();
    }

    private void initListener() {
        btLockNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvAppUnlock.setVisibility(View.VISIBLE);
                lvAppLock.setVisibility(View.GONE);
                btLockYes.setBackgroundResource(R.drawable.tab_right_default);
                btLockNo.setBackgroundResource(R.drawable.tab_left_pressed);
                tvAppLockDes.setText("未加锁应用："+mUnLockList.size());
            }
        });
        btLockYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvAppUnlock.setVisibility(View.GONE);
                lvAppLock.setVisibility(View.VISIBLE);
                btLockYes.setBackgroundResource(R.drawable.tab_right_pressed);
                btLockNo.setBackgroundResource(R.drawable.tab_left_default);
                tvAppLockDes.setText("加锁应用："+mLockList.size());
            }
        });
    }

    private void initData() {
        mAppLockDao = AppLockDao.getInstance(getApplicationContext());
        new Thread() {
            @Override
            public void run() {
                initList();
                //通知适配器进行ui操作
                mHandler.sendEmptyMessage(0);

            }
        }.start();


    }

    private void initList() {
        //全部应用信息
        List<AppInfo> appInfoList = AppInfoParser.getAppInfoList(this);

        mLockList = new ArrayList<AppInfo>();
        mUnLockList = new ArrayList<AppInfo>();

        //加锁应用包名列表
        List<String> packageNameList = mAppLockDao.queryAll();

        for (AppInfo info : appInfoList) {
            if (packageNameList.contains(info.packageName)) {
                mLockList.add(info);
            } else {
                mUnLockList.add(info);
            }
        }

    }

    private void initUI() {
        btLockNo = (Button) findViewById(R.id.bt_lock_no);
        btLockYes = (Button) findViewById(R.id.bt_lock_yes);
        tvAppLockDes = (TextView) findViewById(R.id.tv_app_lock_des);
        lvAppUnlock = (ListView) findViewById(R.id.lv_app_unlock);
        lvAppLock = (ListView) findViewById(R.id.lv_app_lock);
    }

    private class AppLockAdapter extends BaseAdapter {
        private List<AppInfo> list;
        private boolean isLockList;

        public AppLockAdapter(List<AppInfo> list, boolean isLockList) {
            this.list = list;
            this.isLockList = isLockList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_app_lock, null);
                holder = new ViewHolder();
                holder.ivAppLockIcon = (ImageView) convertView.findViewById(R.id.iv_app_lock_icon);
                holder.tvAppLockName = (TextView) convertView.findViewById(R.id.tv_app_lock_name);
                holder.ivLockIcon = (ImageView) convertView.findViewById(R.id.iv_lock_icon);
                holder.tvLockDes = (TextView) convertView.findViewById(R.id.tv_lock_des);
                holder.llLockIcon = (LinearLayout) convertView.findViewById(R.id.ll_lock_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            holder.ivAppLockIcon.setImageDrawable(appInfo.icon);
            holder.tvAppLockName.setText(appInfo.name);
            if (isLockList) {
                holder.ivLockIcon.setImageResource(R.drawable.unlock);
                holder.tvLockDes.setText("解锁");
            }
            holder.llLockIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLockList) {
                        mAppLockDao.delete(appInfo.packageName);
                        mLockList.remove(appInfo);
                        mUnLockList.add(appInfo);
                        tvAppLockDes.setText("加锁应用："+mLockList.size());
                        mLockAdapter.notifyDataSetChanged();
                        mUnLockAdapter.notifyDataSetChanged();
                    }else{
                        mAppLockDao.insert(appInfo.packageName);
                        mUnLockList.remove(appInfo);
                        mLockList.add(appInfo);
                        tvAppLockDes.setText("未加锁应用："+mUnLockList.size());
                        mLockAdapter.notifyDataSetChanged();
                        mUnLockAdapter.notifyDataSetChanged();
                    }
                    
                }
            });
            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView ivAppLockIcon;
        public TextView tvAppLockName;
        public ImageView ivLockIcon;
        public TextView tvLockDes;
        public LinearLayout llLockIcon;
    }

}
