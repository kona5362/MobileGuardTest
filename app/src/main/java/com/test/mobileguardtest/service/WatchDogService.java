package com.test.mobileguardtest.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.test.mobileguardtest.activity.EnterPassWordActivity;
import com.test.mobileguardtest.db.dao.AppLockDao;

import java.util.List;

/**
 * Created by kona on 2017/8/4.
 */

public class WatchDogService extends Service {

    // 看门狗的标记
    private boolean flag;
    private ActivityManager am;
    private AppLockDao dao;
    private Intent intent;
    private List<String> mLockLists;
    private String stopProtectingPackageName;
    private AppLockBroadcastReceiver mAppLockBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        intent = new Intent(getApplicationContext(), EnterPassWordActivity.class);
        dao = AppLockDao.getInstance(getApplicationContext());
        mLockLists = dao.queryAll();
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(
                Uri.parse("content://com.kona.mobilesafe.applock"),
                true, new AppLockContentObserver(new Handler()));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("kona.stop.watchdog");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mAppLockBroadcastReceiver = new AppLockBroadcastReceiver();
        registerReceiver(mAppLockBroadcastReceiver, intentFilter);

        startWatchDog();

    }

    private void startWatchDog() {
        new Thread() {
            @Override
            public void run() {
                flag = true;
                while (flag) {
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    if (dao.isExist(packageName)) {
                        if (mLockLists.contains(packageName)) {
                            if (packageName.equals(stopProtectingPackageName)) {
                            } else {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("packageName", packageName);
                                startActivity(intent);
                            }

                        }
                    }
                    SystemClock.sleep(50);
                }

            }
        }.start();
    }

    private class AppLockContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mLockLists = dao.queryAll();
        }
    }

    private class AppLockBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("kona.stop.watchdog".equals(action)) {
                String packageName = intent.getStringExtra("packageName");
                stopProtectingPackageName = packageName;
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                flag = false;
                stopProtectingPackageName = null;
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (!flag) {
                    flag = true;
                    startWatchDog();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAppLockBroadcastReceiver);
    }
}
