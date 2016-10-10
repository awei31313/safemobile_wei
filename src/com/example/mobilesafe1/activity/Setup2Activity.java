package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.ToastUtil;
import com.example.mobilesafe1.view.CheckboxView_Setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupAcitivity {

	private CheckboxView_Setting isv_setup2_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		// 初始化UI
		initUI();
		// 初始化点击事件
		initOnClick();
	}

	private void initOnClick() {
		// 设置isv_setup2_bound的点击监听
		isv_setup2_bound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCheck = isv_setup2_bound.isCheck();
				isv_setup2_bound.setCheck(!isCheck);
			}
		});
	}

	private void initUI() {
		isv_setup2_bound = (CheckboxView_Setting) findViewById(R.id.isv_setup2_bound);
		// 判断sp里是否存储sim卡号，并做控件的状态改变
		String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, null);
		if (!TextUtils.isEmpty(sim_number)) {
			isv_setup2_bound.setCheck(true);
		} else {
			isv_setup2_bound.setCheck(false);
		}
	}

	@Override
	protected void showNextPage() {
		// 跳转下一页之前判断绑定sim卡的状态，如绑定就存储并允许跳转下一页，如未绑定则提醒绑定
		if (isv_setup2_bound.isCheck()) {
			// 获取sim卡序列号TelephoneManager
			TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			// 获取sim卡的序列卡号
			String number = manager.getSimSerialNumber();
			// 存储Sim卡号
			SpUtil.putString(this, ConstantValue.SIM_NUMBER, number);

			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "请绑定sim卡");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
