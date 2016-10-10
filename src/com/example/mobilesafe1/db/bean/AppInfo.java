package com.example.mobilesafe1.db.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
//	名称,包名,图标,(内存,sd卡),(系统,用户)
	public String name;
	public String packageName;
	public Drawable icon;
	public boolean isSdCard;
	public boolean isSystem;
	

	@Override
	public String toString() {
		return "AppInfo [name=" + name + "]";
	}
	
	
}
