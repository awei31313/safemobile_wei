package com.example.mobilesafe1.activity;

import java.io.File;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.engine.SmsBackup;
import com.example.mobilesafe1.engine.SmsBackup.CallBack;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {

	private TextView tv_atool_adress;
	private TextView tv_atool_backup;
	private TextView tv_atool_commonnumber_query;
	private TextView tv_atool_app_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		//初始化UI
		initUI();
		//初始化事件
		initEvent();
		
	}

	private void initEvent() {
		//初始化tv_atool_adress的点击监听
		tv_atool_adress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AToolActivity.this,QueryAdressActivity.class);
				startActivity(intent);
			}
		});
		//初始化tv_atool_backup的点击监听
		tv_atool_backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//调用备份短信的方法
				new AlertDialog.Builder(AToolActivity.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("短信备份")
				.setMessage("确认备份短信")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						smsBackup();
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", null)
				.show();
				
			}
		});
		
		//初始化tv_atool_commonnumber_query的点击监听
		tv_atool_commonnumber_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
			}
		});
		
		//初始化tv_atool_app_lock的点击监听
		tv_atool_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
			}
		});
	}

	protected void smsBackup() {

		//1,创建一个带进度条的对话框
		final ProgressDialog dialog = new ProgressDialog(this);
		//2，设置进度条的标题和图标，样式
		dialog.setIcon(R.drawable.ic_launcher);
		dialog.setTitle("短信备份");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3,展示进度条
		dialog.show();
		//通过在子线程直接调用备份短信方法
		new Thread(){
			public void run() {
				//判断SD卡是否是挂载
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					
					String path = Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"sms74.xml";
					SmsBackup.backup(getApplicationContext(),path,new CallBack() {
						
						@Override
						public void setProgress(int index) {
							dialog.setProgress(index);
						}
						
						@Override
						public void setMax(int max) {
							dialog.setMax(max);
						}
					});
					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "请插入SD卡");;
				}
			};
		}.start();
		
	}

	private void initUI() {

		//初始化电话归属地的条目
		tv_atool_adress = (TextView) findViewById(R.id.tv_atool_adress);
		//初始化短信备份的条目
		tv_atool_backup = (TextView) findViewById(R.id.tv_atool_backup);
		//初始化常用号码的条目
		tv_atool_commonnumber_query = (TextView) findViewById(R.id.tv_atool_commonnumber_query);
		//初始化应用锁的条目
		tv_atool_app_lock = (TextView) findViewById(R.id.tv_atool_app_lock);
		
	}
}
