package com.example.mobilesafe1.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作归属地数据库的类
 * @author Administrator
 *
 */
public class AddressDao {

	private static String path = "data/data/com.example.mobilesafe1/files/address.db";
	private static String mAddress = "";
	
	/**传递一个电话号码,开启数据库连接,进行访问,返回一个归属地
	 * @param phone	查询电话号码
	 */
	public static String getAddress(String phone) {
		mAddress = "";
		//正则表达式,匹配手机号码
		//手机号码的正则表达式
		String regularExpression = "^1[3-8]\\d{9}";
		//获取数据连接
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		//判断phone是否匹配正则
		if(phone.matches(regularExpression)){
			phone = phone.substring(0, 7);
			Cursor cursor1 = database.query("data1", new String[]{"outkey"}, "id = ? ",
					new String[]{phone}, null, null, null);
			if(cursor1.moveToNext()){
				String outkey = cursor1.getString(0);
				Cursor cursor2 = database.query("data2", new String[]{"location"}, "id = ? ", 
						new String[]{outkey}, null, null, null);
				if(cursor2.moveToNext()){
					mAddress = cursor2.getString(0);
				}
				//如果在归属地数据库中查询不到，就是未知号码
			}else{
				mAddress = "未知号码";
			}
			//如果不匹配正则就执行下面的选择
		}else{
			//获取号码的长度
			int length = phone.length();
			//根据长度做选择判断
			switch (length) {
			case 3://119 110 120 114
				mAddress = "报警电话";
				break;
			case 4://119 110 120 114
				mAddress = "模拟器";
				break;
			case 5://10086 99555
				mAddress = "服务电话";
				break;
			case 7:
				mAddress = "本地电话";
				break;
			case 8:
				mAddress = "本地电话";
				break;
			case 11:
				//(3+8) 区号+座机号码(外地),查询data2
				String area = phone.substring(1, 3);
				Cursor cursor = database.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
				if(cursor.moveToNext()){
					mAddress = cursor.getString(0);
				}else{
					mAddress = "未知号码";
				}
				break;
			case 12:
				//(4+8) 区号(0791(江西南昌))+座机号码(外地),查询data2
				String area1 = phone.substring(1, 4);
				Cursor cursor1 = database.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
				if(cursor1.moveToNext()){
					mAddress = cursor1.getString(0);
				}else{
					mAddress = "未知号码";
				}
				break;
			}
		}
		return mAddress;
	}

}
