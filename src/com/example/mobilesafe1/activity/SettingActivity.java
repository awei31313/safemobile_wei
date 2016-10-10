package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.service.AddressService;
import com.example.mobilesafe1.service.BlackNumberService;
import com.example.mobilesafe1.service.WatchDogService;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.ServiceUtil;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.view.CheckboxView_Setting;
import com.example.mobilesafe1.view.SingleChoiceView_Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	
	private CheckboxView_Setting isv_setting_update;
	private CheckboxView_Setting isv_setting_address;
	private SingleChoiceView_Setting scvs_setting_style;
	private String[] mToastStyleDes;
	private int mToastStyle;
	private SingleChoiceView_Setting scvs_setting_address;
	private CheckboxView_Setting isv_setting_blacknumber;
	private CheckboxView_Setting isv_setting_applock;

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
		
		//设置isv_setting_address条目的点击监听
		isv_setting_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取当前的状态
				boolean isCheck = isv_setting_address.isCheck();
				isv_setting_address.setCheck(!isCheck);
				//创建Intent对象，关联AddressService服务
				Intent intent = new Intent(getApplicationContext(),AddressService.class);
				if(!isCheck){
					//开启服务,管理吐司
					startService(intent);
				}else{
					//关闭服务
					stopService(intent);
				}
			}
		});
		
		//设置scvs_setting_style条目的点击监听
		scvs_setting_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//5,显示吐司样式的对话框
				showToastStyleDialog();
			}
		});
		
		//设置scvs_setting_address条目的点击监听
		scvs_setting_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
			}
		});
		
		//设置isv_setting_blacknumber条目的点击监听
		isv_setting_blacknumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取当前的状态
				boolean isCheck = isv_setting_blacknumber.isCheck();
				isv_setting_blacknumber.setCheck(!isCheck);
				//创建Intent对象，关联BlackNumberService服务
				Intent intent = new Intent(getApplicationContext(),BlackNumberService.class);
				if(!isCheck){
					//开启服务,管理吐司
					startService(intent);
				}else{
					//关闭服务
					stopService(intent);
				}
			}
		});
		
		isv_setting_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCheck = isv_setting_applock.isCheck();
				isv_setting_applock.setCheck(!isCheck);
				if(!isCheck){
					//开启服务
					startService(new Intent(getApplicationContext(), WatchDogService.class));
				}else{
					//关闭服务
					stopService(new Intent(getApplicationContext(), WatchDogService.class));
				}
			}
		});
		
	}

	protected void showToastStyleDialog() {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle("请选择归属地样式")
		//选择单个条目事件监听
		/*
		 * 形参1:string类型的数组描述颜色文字数组
		 * 形参2:弹出对画框的时候的选中条目索引值
		 * 形参3:点击某一个条目后触发的点击事件
		 * */
		.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//(1,记录选中的索引值,2,关闭对话框,3,显示选中色值文字)
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				dialog.dismiss();
				scvs_setting_style.setDes(mToastStyleDes[which]);
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}

	private void initUI() {
		//初始化isv_setting_update自动更新 条目
		isv_setting_update = (CheckboxView_Setting) findViewById(R.id.isv_setting_update);
		//获取已有的开关状态,用作显示
		boolean isCheck = SpUtil.getBoolean(this, ConstantValue.UPDATE_ON_OFF, false);
		//根据存储的结果设置isv_setting_update的选中状态
		isv_setting_update.setCheck(isCheck);
		
		//初始化isv_setting_address开启来电归属地服务 条目
		isv_setting_address = (CheckboxView_Setting) findViewById(R.id.isv_setting_address);
		//查询当前服务是否运行，并显示
		boolean running1 = ServiceUtil.isRunning(this,"com.example.mobilesafe1.service.AddressService");
		isv_setting_address.setCheck(running1);
		
		//初始化scvs_setting_style设置归属地显示风格 条目
		scvs_setting_style = (SingleChoiceView_Setting) findViewById(R.id.scvs_setting_style);
		scvs_setting_style.setTitle("设置归属地显示风格");
		//1,创建描述文字所在的string类型数组
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		//2,SP获取吐司显示样式的索引值(int),用于获取描述文字
		mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		//3,通过索引,获取字符串数组中的文字,显示给描述内容控件
		scvs_setting_style.setDes(mToastStyleDes[mToastStyle]);
		
		//初始化scvs_setting_address归属地提示框的位置 条目
		scvs_setting_address = (SingleChoiceView_Setting) findViewById(R.id.scvs_setting_address);
		scvs_setting_address.setTitle("归属地提示框的位置");
		scvs_setting_address.setDes("设置归属地提示框的位置");
		
		//初始化isv_setting_blacknumber开启黑名单过滤 条目
		isv_setting_blacknumber = (CheckboxView_Setting) findViewById(R.id.isv_setting_blacknumber);
		//查询当前服务是否运行，并显示
		boolean running2 = ServiceUtil.isRunning(this,"com.example.mobilesafe1.service.BlackNumberService");
		isv_setting_blacknumber.setCheck(running2);
		
		//初始化siv_app_lock软件锁条目
		isv_setting_applock = (CheckboxView_Setting) findViewById(R.id.isv_setting_applock);
		boolean isRunning = ServiceUtil.isRunning(this, "com.example.mobilesafe1.service.WatchDogService");
		isv_setting_applock.setCheck(isRunning);
	}
}
