package com.test.mobileguardtest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2017/8/2.
 */

public class TaskInfoParser {

    public static List<TaskInfo> getTaskInfoList(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        ArrayList<TaskInfo> taskInfoList = new ArrayList<TaskInfo>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();
            //进程包名
            taskInfo.packageName = runningAppProcessInfo.processName;//TODO
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(taskInfo.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                taskInfo.name = "null";
                break;
            }
            //进程名
            taskInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
            //图标 drawabe
            taskInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            //dirty :弄脏  表示占了多少内存  默认返回int型:kb
            long totalPrivateDirty = (long)processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            taskInfo.appRomOccupy = totalPrivateDirty;
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM)!= 0) {
                taskInfo.isUserTask = false;
            }else{
                taskInfo.isUserTask = true;
            }
            taskInfoList.add(taskInfo);
        }
        return taskInfoList;
    }
}
