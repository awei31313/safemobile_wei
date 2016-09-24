package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.engine.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAdressActivity extends Activity {

	private static final int SHOW_ADDRESS = 100;
	private EditText et_query_address_phone;
	private Button bt_query_address_query;
	private TextView tv_query_address_result;
	private String mAddress;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_query_address_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
		//初始化事件
		initEvent();
	}

	private void initEvent() {

		//设置bt_query_address_query按钮的监听
		bt_query_address_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_query_address_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//查询是耗时操作,开启子线程
					query(phone);
				}else{
					//抖动
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					et_query_address_phone.startAnimation(animation);
				}
			}
		});
		
		//设置et_query_address_phone的监听
		et_query_address_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			//实时查询(监听输入框文本变化)
			@Override
			public void afterTextChanged(Editable s) {
				String phone = s.toString();
				query(phone);
			}
		});
	}

	//在子线程中执行查询数据库的操作
	protected void query(final String phone) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(SHOW_ADDRESS);
			}
		}.start();
	}

	private void initUI() {

		et_query_address_phone = (EditText) findViewById(R.id.et_query_address_phone);
		bt_query_address_query = (Button) findViewById(R.id.bt_query_address_query);
		tv_query_address_result = (TextView) findViewById(R.id.tv_query_address_result);
	}
}
