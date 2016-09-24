package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.view.ItemSettingView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	
	private ItemSettingView isv_setting_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//初始化UI
		initUI();
		
		//初始化点击事件
		initOnClick();
	}

	private void initOnClick() {

		//设置isv_setting_update条目的点击监听
		isv_setting_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果之前是选中的,点击过后,变成未选中
				//如果之前是未选中的,点击过后,变成选中
				
				//获取之前的选中状态
				boolean check = isv_setting_update.isCheck();
				//将原有状态取反,等同上诉的两部操作
				isv_setting_update.setCheck(!check);
				//将取反后的状态存储到相应sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.UPDATE_ON_OFF,!check);
			}
		});
	}

	private void initUI() {

		//初始化isv_setting_update条目
		isv_setting_update = (ItemSettingView) findViewById(R.id.isv_setting_update);
		//获取已有的开关状态,用作显示
		boolean isCheck = SpUtil.getBoolean(this, ConstantValue.UPDATE_ON_OFF, false);
		//根据存储的结果设置isv_setting_update的选中状态
		isv_setting_update.setCheck(isCheck);
		
		
	}
}
