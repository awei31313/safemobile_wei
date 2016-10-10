package com.example.mobilesafe1.view;

import com.example.mobilesafe1.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckboxView_Setting extends RelativeLayout {

	private static final String SPACENAME = "http://schemas.android.com/apk/res/com.example.mobilesafe1";
	private TextView tv_setting_title;
	private TextView tv_setting_des;
	private CheckBox cb_setting;
	private String mdestitle;
	private String mdesoff;
	private String mdeson;

	public CheckboxView_Setting(Context context) {
		this(context,null);
	}

	public CheckboxView_Setting(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public CheckboxView_Setting(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//xml--->view	将设置界面的一个条目转换成view对象,直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.checkbox_view_activity_setting, this);
		//等同于以下两行代码
		/*View view = View.inflate(context, R.layout.setting_item_view, null);
		this.addView(view);*/
		
		//自定义组合控件中的标题描述
		tv_setting_title = (TextView) findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) findViewById(R.id.tv_setting_des);
		cb_setting = (CheckBox) findViewById(R.id.cb_setting);
	
		//获取自定义的属性值
		initAttrs(attrs);
		//获取布局文件中定义的字符串,赋值给自定义组合控件的标题
		tv_setting_title.setText(mdestitle);
	}

	/**
	 * 返回属性集合中自定义属性属性值
	 * @param attrs	构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {

		mdestitle = attrs.getAttributeValue(SPACENAME, "destitle");
		mdesoff = attrs.getAttributeValue(SPACENAME, "desoff");
		mdeson = attrs.getAttributeValue(SPACENAME, "deson");
	}
	
	/**
	 * 判断是否开启的方法
	 * @return	返回当前ItemSettingView是否选中状态	true开启(checkBox返回true)	false关闭(checkBox返回true)
	 */
	public boolean isCheck(){
		return cb_setting.isChecked();
	}
	
	/**
	 * @param isCheck	是否作为开启的变量,由点击过程中去做传递
	 */
	public void setCheck(boolean isCheck){
		cb_setting.setChecked(isCheck);
		if(isCheck){
			tv_setting_des.setText(mdeson);
		}else{
			tv_setting_des.setText(mdesoff);
		}
	}

}
