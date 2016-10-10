package com.example.mobilesafe1.view;

import com.example.mobilesafe1.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleChoiceView_Setting extends RelativeLayout {

	private TextView tv_setting_title;
	private TextView tv_setting_des;

	public SingleChoiceView_Setting(Context context) {
		this(context,null);
	}

	public SingleChoiceView_Setting(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SingleChoiceView_Setting(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//xml--->view	将设置界面的一个条目转换成view对象,直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.singlechoice_view_activity_setting, this);
		
		
		//自定义组合控件中的标题描述
		tv_setting_title = (TextView) findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) findViewById(R.id.tv_setting_des);
	
	}

	
	/**
	 * @param title	设置标题内容
	 */
	public void setTitle(String title){
		tv_setting_title.setText(title);
	}
	
	/**
	 * @param des	设置描述内容
	 */
	public void setDes(String des){
		tv_setting_des.setText(des);
	}
	

}
