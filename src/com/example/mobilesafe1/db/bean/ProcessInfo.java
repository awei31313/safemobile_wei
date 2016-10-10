package com.example.mobilesafe1.db.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	public String name;//应用名称
	public Drawable icon;//应用图标
	public long memSize;//应用已使用的内存数
	public boolean isCheck;//是否被选中
	public boolean isSystem;//是否为系统应用
	public String packageName;//如果进程没有名称,则将其所在应用的包名最为名称
	
}
