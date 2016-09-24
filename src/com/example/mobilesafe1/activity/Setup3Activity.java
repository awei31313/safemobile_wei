package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupAcitivity {

	private Button btn_setup3_addcontact;
	private EditText et_setup3_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		// 初始化UI
		initUI();
		// 初始化点击事件
		initOnClick();
	}

	private void initOnClick() {

		// 设置btn_setup3_addcontact添加联系人按钮的点击监听
		btn_setup3_addcontact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到联系人列表页面
				Intent intent = new Intent(Setup3Activity.this, ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			// 1,返回到当前界面的时候,接受结果的方法
			String phone = data.getStringExtra("phone");
			// 2,将特殊字符过滤(中划线转换成空字符串)
			phone = phone.replace("-", "").replace(" ", "").trim();
			// 3,回显到输入框
			et_setup3_number.setText(phone);
		}
	}

	private void initUI() {
		// 初始化输入框控件
		et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);
		// 查找SP中有无已添加的安全号码,有就显示到输入框中
		String security_number = SpUtil.getString(this, ConstantValue.SECURITY_NUMBER, null);
		et_setup3_number.setText(security_number);
		// 初始化按钮
		btn_setup3_addcontact = (Button) findViewById(R.id.btn_setup3_addcontact);

	}

	@Override
	protected void showNextPage() {
		// 得到输入框内中的号码
		String phone = et_setup3_number.getText().toString();
		// 判断号码是否空值
		if (!TextUtils.isEmpty(phone)) {
			// 不是空值就保存并跳转下一页
			SpUtil.putString(this, ConstantValue.SECURITY_NUMBER, phone);
			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			// 否则提示用户
			ToastUtil.show(this, "请输入安全号码");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
