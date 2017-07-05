package com.test.mobileguardtest.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	public static String path = "data/data/com.test.mobileguardtest/files/address.db";
	public static String query(String phone){
		String address = "未知号码";
		String regularExpression = "^1[3-8]\\d{9}";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phone.matches(regularExpression)){
			phone = phone.substring(0, 7);
			Cursor data1Cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
			if(data1Cursor.moveToNext()){
				String outkey = data1Cursor.getString(0);
				Cursor data2Cursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
				if(data2Cursor.moveToNext()){
					address = data2Cursor.getString(0);
				}
				data2Cursor.close();
			}
			data1Cursor.close();
		}else{
			switch (phone.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务电话";
				break;
			case 7:
				address = "固定电话";
				break;
			case 11: //3+8
				String areaNum = phone.substring(1, 3);
				Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{areaNum}, null, null, null);
				if(cursor.moveToNext()){
					address = cursor.getString(0);
				}
				cursor.close();
				break;
			case 12: //4+8
				String areaNum1 = phone.substring(1, 4);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{areaNum1}, null, null, null);
				if(cursor1.moveToNext()){
					address = cursor1.getString(0);
				}
				cursor1.close();
				break;
			}
		}
		db.close();
		return address;
	}
}
