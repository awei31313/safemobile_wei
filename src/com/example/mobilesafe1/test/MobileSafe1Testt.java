package com.example.mobilesafe1.test;

import java.util.ArrayList;
import java.util.Random;

import com.example.mobilesafe1.db.bean.AppInfo;
import com.example.mobilesafe1.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class MobileSafe1Testt extends AndroidTestCase{

	public void test(){
		BlackNumberDao blackNumberDao = BlackNumberDao.getIntance(getContext());
		for(int i =0;i<100;i++){
			if(i<10){
				blackNumberDao.insert("1895599000"+i, 1+new Random().nextInt(3)+"");
			}else{
				blackNumberDao.insert("189559900"+i, 1+new Random().nextInt(3)+"");
			}
		}
	}
	
	public void test2(){
		ArrayList<Object> list = new ArrayList<>();
//		AppInfo appInfo = new AppInfo();
//		appInfo.setName("lisi");
//		list.add("zhangsan");
//		list.add(appInfo);
//		Object obj1 = list.get(0);
//		Object obj2 = list.get(1);
//		System.out.println(obj1.getClass().getSimpleName());
//		System.out.println(obj2.getClass().getSimpleName());
		if(list==null){
			System.out.println("null");
		}else{
			System.out.println(list.size());
		}
	}
}
