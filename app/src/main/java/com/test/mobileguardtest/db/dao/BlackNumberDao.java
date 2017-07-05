package com.test.mobileguardtest.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.test.mobileguardtest.bean.BlackNumberInfo;
import com.test.mobileguardtest.db.BlackNumberOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class BlackNumberDao {

	private BlackNumberOpenHelper blackNumberOpenHelper; 
	private static BlackNumberDao blackNumberDao;  
	private BlackNumberDao(Context context){
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao == null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	public void insert(String phone,int mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknum", null,values);
		db.close();
	}
	
	public void update(String phone,int mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknum", values, "phone = ?", new String[]{phone});
		db.close();
	}
	
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknum", "phone = ?", new String[]{phone});
		db.close();
	}
	
	public List<BlackNumberInfo> queryAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknum", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blackNumList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			info.setPhone(cursor.getString(0));
			info.setMode(cursor.getInt(1));
			blackNumList.add(info);
		}
		return blackNumList;
	}
	
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknum", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null, null);
		if(cursor.moveToNext()){
			return cursor.getInt(0);
		}else{
			return -1;
		}
	}
}
