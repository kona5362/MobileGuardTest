package com.test.mobileguardtest.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.test.mobileguardtest.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kona on 2017/8/4.
 */

public class AppLockDao {
    private Context context;
    private AppLockOpenHelper mAppLockOpenHelper;
    private static AppLockDao mAppLockDao;

    private AppLockDao(Context context) {
        this.context = context;
        mAppLockOpenHelper = new AppLockOpenHelper(context);
    }

    public static AppLockDao getInstance(Context context) {
        if (mAppLockDao == null) {
            mAppLockDao = new AppLockDao(context);
        }
        return mAppLockDao;
    }

    public void insert(String packageName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packageName);
        db.insert("applocklist", null, contentValues);
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://com.kona.mobilesafe.applock"),null);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        db.delete("applocklist", "packagename = ?", new String[]{packageName});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://com.kona.mobilesafe.applock"),null);
    }

    public boolean isExist(String packageName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applocklist", new String[]{"packagename"}, "packagename = ?", new String[]{packageName}, null, null, null);
        if (cursor != null) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    public List<String> queryAll() {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applocklist", new String[]{"packagename"}, null, null, null, null, null);
        ArrayList<String> list = new ArrayList<String>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String packageName = cursor.getString(cursor.getColumnIndex("packagename"));
                list.add(packageName);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
