package com.test.mobileguardtest.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.test.mobileguardtest.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2017/7/4.
 */

public class AppInfoParser {

    public static List<AppInfo> getAppInfoList(Context context){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0); //耗时操作？
        List<AppInfo> appInfos = new ArrayList<>();
        for (ApplicationInfo info : installedApplications) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = info.packageName;
            //appInfo.name = info.name;
            appInfo.name = info.loadLabel(pm).toString();
            appInfo.icon = info.loadIcon(pm);
            String sourceDir = info.sourceDir;
            File dir = new File(sourceDir);
            appInfo.appSize = dir.length();
            System.out.println("---------------------------------");
            System.out.println("应用的名字---" + appInfo.name);
            System.out.println("应用的包名---" + appInfo.packageName);
            System.out.println("应用的目录---" + sourceDir);
            System.out.println("应用的大小---" + appInfo.appSize);
            // 如果是系统 app /system/app
            // 如果是用户 app /data/data/app
            if(sourceDir.startsWith("/system")){
                appInfo.isUserApp = false;
                System.out.println("系统 app");
            }else{
                appInfo.isUserApp = true;
                System.out.println("用户 app");
            }
            int flags = info.flags;
            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){//说明相同
                //sd卡
                appInfo.isRom = false;
            }else{
                //内存
                appInfo.isRom = true;
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
