package com.test.mobileguardtest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import com.test.mobileguardtest.bean.AppInfo;
import com.test.mobileguardtest.bean.TaskInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by kona on 2017/7/4.
 */

public class TaskUtils {

    /**
     * 获取正在运行的进程数量
     * @param context
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取总进程数量
     * @param context
     * @return
     */
    public static int getTotalProcessCount(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        int count = 0;
        for (PackageInfo info : installedPackages) {
            HashSet<String> set = new HashSet<String>();
            String processName = info.applicationInfo.processName;
            set.add(processName);
            ActivityInfo[] activities = info.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    set.add(activityInfo.processName);
                }
            }
            ServiceInfo[] services = info.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    set.add(serviceInfo.processName);
                }
            }

            ActivityInfo[] receivers = info.receivers;
            if (receivers != null) {
                for (ActivityInfo activityInfo : receivers) {
                    set.add(activityInfo.processName);
                }
            }

            ProviderInfo[] providers = info.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    set.add(providerInfo.processName);
                }
            }
            count += set.size();
        }
        return count;
    }

    /**
     * 获取剩余内存
     * @param context
     * @return
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
    /**
     * 获取总内存
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        //该方法需要api>=16才能用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return memoryInfo.totalMem;
        }else{
            return getLowTotalMemory();
        }
    }

    /**
     * 低版本 获取当前内存大小
     * @return
     */
    public static long getLowTotalMemory() {
        File file = new File("/proc/meminfo");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            line.replace("MemTotal", "");
            line.replace("kB", "");
            line.trim();
            return Long.parseLong(line) * 1024;//kb转回byte
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  0;
    }
}
