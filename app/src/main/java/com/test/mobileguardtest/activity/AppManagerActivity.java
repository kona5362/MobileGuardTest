package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.AppInfo;
import com.test.mobileguardtest.utils.AppInfoParser;
import com.test.mobileguardtest.utils.DensityUtils;
import com.test.mobileguardtest.utils.ToastUtil;
import com.test.mobileguardtest.view.ProgressDesView;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private ListView lv_app_list;
    private ProgressDesView pdv_memory;
    private ProgressDesView pdv_sd_memory;
    private ProgressBar pb;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_des_cover.setText("用户应用：" + mUserAppList.size() + "个");
            lv_app_list.setAdapter(new AppListAdapter());
            pb.setVisibility(View.INVISIBLE);

            super.handleMessage(msg);
        }

    };
    private List<AppInfo> mUserAppList = new ArrayList<AppInfo>();
    private List<AppInfo> mSystemAppList = new ArrayList<AppInfo>();
    private TextView tv_des_cover;
    private PopupWindow mPopupWindow;
    private AppInfo mClickedAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
        initListener();
    }

    private void initListener() {

        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem >= mUserAppList.size()+1){
                    tv_des_cover.setText("系统应用：" + mSystemAppList.size() + "个");
                }else{
                    tv_des_cover.setText("用户应用：" + mUserAppList.size() + "个");
                }
                /* 有bug  飞快滑动就会不执行
                else if(firstVisibleItem == mUserAppList.size()){ //在这里用==比<=更省性能吧(虽然就一点点)
                    tv_des_cover.setText("用户应用：" + mUserAppList.size() + "个");
                }*/
            }
        });
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == mUserAppList.size()+1){
                    return;
                }

                mClickedAppInfo = (AppInfo) parent.getItemAtPosition(position);//TODO
                showPopupWindow(parent,view);


            }
        });
    }

    private void showPopupWindow(AdapterView<?> parent, View view) {
        View popView = View.inflate(getApplicationContext(),R.layout.pop_app_item,null);
        mPopupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);//点popWindow外的区域，则让它消失
        mPopupWindow.isFocusable();//获取焦点
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.showAsDropDown(view, DensityUtils.dip2px(getApplicationContext(),80),-view.getHeight());

        TextView tv_uninstall = (TextView) popView.findViewById(R.id.tv_uninstall);
        TextView tv_play = (TextView) popView.findViewById(R.id.tv_play);
        TextView tv_share = (TextView) popView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_play.setOnClickListener(this);
        tv_share.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_uninstall:
                initUninstall();
                break;
            case R.id.tv_play:
                initPlay();
                break;
            case R.id.tv_share:
                initShare();
                break;
        }
    }

    private void initShare() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "分享");
        intent.putExtra("android.intent.extra.TEXT", "Hi！推荐您使用软件："
        + mClickedAppInfo.name + "下载地址:"
        + "https://play.google.com/store/apps/details?id="
        + mClickedAppInfo.packageName);
        startActivity(intent);
    }

    private void initPlay() {
        Intent localIntent = getPackageManager().getLaunchIntentForPackage(mClickedAppInfo.packageName);
        if(localIntent != null){
            startActivity(localIntent);
        }else{
            ToastUtil.show(this,"该应用程序没有入口");

        }


        /*错的
        Intent intent = new Intent(Intent.ACTION_RUN);
        intent.setData(Uri.parse("package:" + mClickedAppInfo.packageName));
        startActivity(intent);*/
    }

    private void initUninstall() {
        //Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);也可以用
        if(mClickedAppInfo.isUserApp){
            if("com.test.mobileguardtest".equals(mClickedAppInfo.packageName)){
                ToastUtil.show(this,"无法卸载当前应用！");
            }else{
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:"+mClickedAppInfo.packageName));
                startActivity(intent);
            }
        }else{
            ToastUtil.show(this,"系统应用，无法卸载！");
        }

        mPopupWindow.dismiss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private void initUI() {
       // tv_memory = (TextView) findViewById(R.id.tv_memory);
        //tv_sd_card = (TextView) findViewById(R.id.tv_sd_card);
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        pdv_memory = (ProgressDesView) findViewById(R.id.pdv_memory);
        pdv_sd_memory = (ProgressDesView) findViewById(R.id.pdv_sd_memory);
        pb = (ProgressBar) findViewById(R.id.pb);
        tv_des_cover = (TextView) findViewById(R.id.tv_des_cover);
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
        pdv_memory.setProgressLeftWord(android.text.format.Formatter.formatFileSize(this,romFreeSpace)+"可用");
        pdv_memory.setProgressRightWord(android.text.format.Formatter.formatFileSize(this,romSpaceUsed)+"已用");

        //剩余内存
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //总内存
        long sdTotalSpace = Environment.getExternalStorageDirectory().getTotalSpace();
        long sdSpaceUsed = sdTotalSpace - sdFreeSpace;
        pdv_sd_memory.setProgress((int) (sdFreeSpace * 100 / sdTotalSpace + 0.5f));
        pdv_sd_memory.setTitle("sd卡：");
        pdv_sd_memory.setProgressLeftWord(android.text.format.Formatter.formatFileSize(this,sdFreeSpace)+"可用");
        pdv_sd_memory.setProgressRightWord(android.text.format.Formatter.formatFileSize(this,sdSpaceUsed)+"已用");


        new Thread(){
            @Override
            public void run() {
                super.run();
                List<AppInfo> mAppInfos = AppInfoParser.getAppInfoList(getApplicationContext());
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
        public int getItemViewType(int position) {
            if(position == 0  || position == mUserAppList.size()+1){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            switch (type){
                case 0:
                    HeadViewHolder headHolder = null;
                    if(convertView == null){
                        convertView = View.inflate(getApplicationContext(),R.layout.item_lv_head,null);
                        headHolder = new HeadViewHolder();
                        headHolder.tv_head = (TextView) convertView.findViewById(R.id.tv_head);
                        convertView.setTag(headHolder);
                    }else{
                        headHolder = (HeadViewHolder) convertView.getTag();
                    }
                    if(position == 0) {
                        headHolder.tv_head.setText("用户应用：" + mUserAppList.size() + "个");
                    }else if (position == mUserAppList.size() + 1) {
                        headHolder.tv_head.setText("系统应用：" + mSystemAppList.size() + "个");
                    }
                    break;
                case 1:
                    ViewHolder holder = null;
                    /*if(convertView!=null && convertView instanceof LinearLayout){
                        holder = (ViewHolder) convertView.getTag();
                    }*/
                    if(convertView!=null){
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
            }
            return convertView;
        }
/*        重写了getItemViewType getItemViewType后就不用担心复用类型不匹配了
          可以不用加convertView instanceof LinearLayout这句了
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            switch (type){
                case 0:
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
                    break;
                case 1:
                    ViewHolder holder = null;
                    *//*if(convertView!=null && convertView instanceof LinearLayout){
                        holder = (ViewHolder) convertView.getTag();
                    }*//*
                    if(convertView!=null){
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
            return null;
        }*/
        /*
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
         */

    }

    static class HeadViewHolder{
        public TextView tv_head;
    }

    static class ViewHolder{
        public ImageView iv_app_icon;
        public TextView tv_app_name;
        public TextView tv_app_position;
        public TextView tv_app_size;
    }

}
