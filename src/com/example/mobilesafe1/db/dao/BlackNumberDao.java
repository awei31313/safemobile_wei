package com.example.mobilesafe1.db.dao;

import java.util.ArrayList;

import com.example.mobilesafe1.db.BlackNumberOpenHelper;
import com.example.mobilesafe1.db.bean.BlackNameInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作黑名单数据库的增删改查类
 * 单例模式
 * @author Administrator
 *
 */
public class BlackNumberDao {

	
	private BlackNumberOpenHelper blackNumberOpenHelper;
	//声明一个当前类的对象
	private static BlackNumberDao blackNumberDao = null;
	//私有化构造方法
	private BlackNumberDao(Context context){
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	//对外提供的获取当前类对象的静态方法
	public static BlackNumberDao getIntance(Context context){
		if(blackNumberDao==null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	/**
	 * 添加一个黑名单
	 * @param phone 拦截的号码
	 * @param mode 拦截类型(1:短信	2:电话	3:拦截所有(短信+电话))
	 */
	public void insert(String phone ,String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode",mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	/**
	 * 从数据库中删除一条电话号码
	 * @param phone	删除电话号码
	 */
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone=?", new String[]{phone});
		db.close();
	}
	
	/**
	 * 根据电话号码去,更新拦截模式
	 * @param phone	更新拦截模式的电话号码
	 * @param mode	要更新为的模式(1:短信	2:电话	3:拦截所有(短信+电话)
	 */
	public void update(String phone,String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "phone=?", new String[]{phone});
		db.close();
	}
	
	/**
	 * @return	查询到数据库中所有的号码以及拦截类型所在的集合
	 */
	public ArrayList<BlackNameInfo> queryAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		ArrayList<BlackNameInfo> list = new ArrayList<BlackNameInfo>();
		while(cursor.moveToNext()){
			BlackNameInfo blackNameInfo = new BlackNameInfo();
			blackNameInfo.phone = cursor.getString(0);
			blackNameInfo.mode = cursor.getString(1);
			list.add(blackNameInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 每次查询20条数据
	 * @param index	查询的索引值
	 */
	public ArrayList<BlackNameInfo> query(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index+""});
		ArrayList<BlackNameInfo> list = new ArrayList<BlackNameInfo>();
		while(cursor.moveToNext()){
			BlackNameInfo blackNameInfo = new BlackNameInfo();
			blackNameInfo.phone = cursor.getString(0);
			blackNameInfo.mode = cursor.getString(1);
			list.add(blackNameInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * @return	数据库中数据的总条目个数,返回0代表没有数据或异常
	 */
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return count;
	}
	
	/**
	 * @param phone	作为查询条件的电话号码
	 * @return	传入电话号码的拦截模式	1:短信	2:电话	3:所有	0:没有此条数据
	 */
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int mode = 0;
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null,null);
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return mode;
	}
	
}
