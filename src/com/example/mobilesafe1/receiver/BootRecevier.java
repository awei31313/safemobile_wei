package com.example.mobilesafe1.receiver;

import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * 接收手机重启的广播，用来判断当前sim卡号和本地存储sim卡号是否相同
 * @author Administrator
 *
 */
public class BootRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		//获取TelephonyManager
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//1,获取开机后手机的sim卡的序列号
		String sim_number = telephonyManager.getSimSerialNumber();
		//2,获取sp中存储的sim卡号
		String sp_sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, null);
		//3,判断卡号是否相同
		if(!sim_number.equals(sp_sim_number)){
			//4,发送短信给选中联系人号码
			SmsManager sms = SmsManager.getDefault();
			String security_number = SpUtil.getString(context, ConstantValue.SECURITY_NUMBER, null);
			sms.sendTextMessage(security_number, null, "sim change!!!", null, null);
		}
	}

}
