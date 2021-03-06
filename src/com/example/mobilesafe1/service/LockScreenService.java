package com.example.mobilesafe1.service;


import com.example.mobilesafe1.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockScreenService extends Service {
	private IntentFilter intentFilter;
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		
		//锁屏action
		intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
		
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("锁频后", "我被调用了");
			//清理手机正在运行的进程
			ProcessInfoProvider.killAll(context);
		}
	}
}
