package com.example.mobilesafe1.receiver;


import com.example.mobilesafe1.engine.ProcessInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//杀死进程
		ProcessInfoProvider.killAll(context);
	}
}
