package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupAcitivity {

	private static final String SECURITY_ON = "防盗保护已开启";
	private static final String SECURITY_OFF = "您没有开启防盗保护";
	private CheckBox cb_setup4_guard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		// 初始化UI
		initUI();
		// 初始化事件
		initEvents();
	}

	private void initEvents() {

		// 设置cb_setup4_guard的选择状态监听
		cb_setup4_guard.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 根据改变的状态，设置对应的Text
				if (isChecked) {
					cb_setup4_guard.setText(SECURITY_ON);
				} else {
					cb_setup4_guard.setText(SECURITY_OFF);
				}
				// 将新的选择状态存储到SP
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
			}
		});
	}

	private void initUI() {

		// 找到cb_setup4_guard
		cb_setup4_guard = (CheckBox) findViewById(R.id.cb_setup4_guard);
		// 通过查找SP回显该控件
		boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		cb_setup4_guard.setChecked(open_security);
		if (open_security) {
			cb_setup4_guard.setText(SECURITY_ON);
		} else {
			cb_setup4_guard.setText(SECURITY_OFF);
		}
	}

	@Override
	protected void showNextPage() {
		boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
			startActivity(intent);
			finish();

			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

		} else {
			ToastUtil.show(getApplicationContext(), "请开启防盗保护");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
