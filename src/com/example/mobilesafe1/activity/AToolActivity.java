package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {

	private TextView tv_atool_adress;

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
	}

	private void initUI() {

		tv_atool_adress = (TextView) findViewById(R.id.tv_atool_adress);
		
		
	}
}
