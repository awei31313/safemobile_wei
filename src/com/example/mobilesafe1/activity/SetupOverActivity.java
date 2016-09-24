package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.lidroid.xutils.db.annotation.Finder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setupover);
		
		if(!SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false)){
			//密码输入成功,四个导航界面没有设置完成----->跳转到导航界面第1
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//开启了一个新的界面以后,关闭功能列表界面
			finish();
		}
		//密码输入成功,并且四个导航界面设置完成----->停留在设置完成功能列表界面
		setContentView(R.layout.activity_setupover);
		//初始化UI
		initUI();
	}

	private void initUI() {

		//初始化tv_setup_number
		TextView tv_setup_number = (TextView) findViewById(R.id.tv_setup_number);
		String security_number = SpUtil.getString(this, ConstantValue.SECURITY_NUMBER, "");
		tv_setup_number.setText(security_number);
		//初始化iv_setupover_lock
		ImageView iv_setupover_lock = (ImageView) findViewById(R.id.iv_setupover_lock);
		boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			iv_setupover_lock.setBackgroundResource(R.drawable.unlock);
		}else{
			iv_setupover_lock.setBackgroundResource(R.drawable.lock);
		}
		//初始化tv_setup_reset
		TextView tv_setup_reset = (TextView) findViewById(R.id.tv_setup_reset);
		tv_setup_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
