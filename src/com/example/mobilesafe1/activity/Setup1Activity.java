package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends BaseSetupAcitivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	
	@Override
	protected void showNextPage() {
		
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		
		//开启平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);		
	}
	
	
	@Override
	protected void showPrePage() {
		
	}
	
	
}
