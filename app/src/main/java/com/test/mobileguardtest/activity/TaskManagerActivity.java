package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.TaskInfo;
import com.test.mobileguardtest.utils.DensityUtils;
import com.test.mobileguardtest.utils.TaskInfoParser;
import com.test.mobileguardtest.utils.TaskUtils;
import com.test.mobileguardtest.view.ProgressDesView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2017/7/15.
 */

public class TaskManagerActivity extends Activity {

    private ImageView ivClean;
    private ProgressDesView pdvTaskCount;
    private ProgressDesView pdvMemory;
    private ListView lvAppList;
    private ProgressBar pb;
    private TextView tvDesCover;
    private List<TaskInfo> mUserTaskInfoList = new ArrayList<TaskInfo>();
    private List<TaskInfo> mSystemTaskInfoList = new ArrayList<TaskInfo>();
    private TaskAdapter mTaskAdapter = new TaskAdapter();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lvAppList.setAdapter(mTaskAdapter);
            pb.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initUI();
        initTop();
        initData();
        initListener();
    }

    public void checkAll(View view) {
        for (TaskInfo info : mUserTaskInfoList) {
            info.isSelected = true;
        }
        for (TaskInfo info : mSystemTaskInfoList) {
            info.isSelected = true;
        }
        mTaskAdapter.notifyDataSetChanged();
    }

    public void inverse(View view) {
        for (TaskInfo info : mUserTaskInfoList) {
            info.isSelected = false;
        }
        for (TaskInfo info : mSystemTaskInfoList) {
            info.isSelected = false;
        }
        mTaskAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        lvAppList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem <= mUserTaskInfoList.size()+1){
                    tvDesCover.setText("用户进程："+ mUserTaskInfoList.size());
                }else{
                    tvDesCover.setText("系统进程："+ mSystemTaskInfoList.size());
                }
            }
        });

        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_box);
                TaskInfo taskInfo = (TaskInfo) parent.getItemAtPosition(position);
                if (taskInfo != null) {
                    cb.setChecked(!cb.isChecked());
                    taskInfo.isSelected = cb.isChecked();
                }
            }
        });

        ivClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                ArrayList<TaskInfo> cleanList = new ArrayList<TaskInfo>();
                for(TaskInfo info : mUserTaskInfoList) {
                    if (info.isSelected) {
                        cleanList.add(info);
                        am.killBackgroundProcesses(info.packageName);
                    }

                }
                for(TaskInfo info : mSystemTaskInfoList) {
                    if (info.isSelected) {
                        cleanList.add(info);
                        am.killBackgroundProcesses(info.packageName);
                    }
                }
                mUserTaskInfoList.removeAll(cleanList);
                mSystemTaskInfoList.removeAll(cleanList);
                mTaskAdapter.notifyDataSetChanged();
                initTop();
            }
        });
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                List<TaskInfo> mTaskInfoList = TaskInfoParser.getTaskInfoList(getApplicationContext());
                for (TaskInfo info : mTaskInfoList) {
                    if (info.isUserTask) {
                        mUserTaskInfoList.add(info);
                    }else{
                        mSystemTaskInfoList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    /**
     * 初始化上部分界面
     */
    private void initTop() {
        //进程
        //总进程数
        int TotalProcess = TaskUtils.getTotalProcessCount(this);
        //正在运行的进程数
        int runningProcesses = TaskUtils.getRunningProcessCount(this);
        pdvTaskCount.setProgressLeftWord("正在运行："+runningProcesses);
        pdvTaskCount.setProgressRightWord("总进程："+TotalProcess);
        pdvTaskCount.setTotalProgress(TotalProcess);
        pdvTaskCount.setProgress(runningProcesses);

        //内存
        long totalMem = TaskUtils.getTotalMemory(this);
        //可用内存
        long availMem = TaskUtils.getAvailableMemory(this);
        //占用内存
        long occupyMem = totalMem - availMem;
        String availMemStr = Formatter.formatFileSize(this, availMem);
        String occupyMemStr = Formatter.formatFileSize(this, occupyMem);
        pdvMemory.setProgress((int) (occupyMem*100/totalMem));
        pdvMemory.setProgressLeftWord("占用内存："+occupyMemStr);
        pdvMemory.setProgressRightWord("可用内存："+availMemStr);
    }

    private void initUI() {
        ivClean = (ImageView) findViewById(R.id.iv_clean);
        pdvTaskCount = (ProgressDesView) findViewById(R.id.pdv_task_count);
        pdvMemory = (ProgressDesView) findViewById(R.id.pdv_memory);
        lvAppList = (ListView) findViewById(R.id.lv_app_list);
        pb = (ProgressBar) findViewById(R.id.pb);
        tvDesCover = (TextView) findViewById(R.id.tv_des_cover);
        pb.setVisibility(View.VISIBLE);
    }

    private class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSystemTaskInfoList.size()+mUserTaskInfoList.size()+2;
        }

        @Override
        public TaskInfo getItem(int position) {
            if (position > 0 && position <= mUserTaskInfoList.size()) {
                return mUserTaskInfoList.get(position - 1);
            } else if (position > mUserTaskInfoList.size() + 1) {
                return mSystemTaskInfoList.get(position - mUserTaskInfoList.size() - 2);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_lv_task, null);
                holder = new TaskViewHolder();
                holder.ivTaskIcon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
                holder.tvTaskName = (TextView) convertView.findViewById(R.id.tv_task_name);
                holder.tvMemOccupy = (TextView) convertView.findViewById(R.id.tv_mem_occupy);
                holder.cbBox = (CheckBox) convertView.findViewById(R.id.cb_box);
                convertView.setTag(holder);
            }else{
                holder = (TaskViewHolder) convertView.getTag();
            }

            if (position == 0 || position == mUserTaskInfoList.size() + 1) {
                TextView textView = new TextView(getApplicationContext());
                textView.setPadding(
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5),
                        DensityUtils.dip2px(getApplicationContext(),5)
                );
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16);
                if (position == 0) {
                    textView.setText("用户进程：" + mUserTaskInfoList.size());
                }else{
                    textView.setText("系统进程：" + mSystemTaskInfoList.size());
                }
                return textView;
            }

            TaskInfo info = getItem(position);
            holder.ivTaskIcon.setImageDrawable(info.icon);
            holder.tvTaskName.setText(info.name);
            holder.tvMemOccupy.setText(Formatter.formatFileSize(getApplicationContext(),info.appRomOccupy));
            holder.cbBox.setChecked(info.isSelected);
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if(position ==0 || position == mUserTaskInfoList.size()+1){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }

    static class TaskViewHolder{
        private ImageView ivTaskIcon;
        private TextView tvTaskName;
        private TextView tvMemOccupy;
        private CheckBox cbBox;
    }
}
