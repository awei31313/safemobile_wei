package com.example.mobilesafe1.receiver;

import java.io.IOException;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class smsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDpm;
	private ComponentName mCn;

	@Override
	public void onReceive(Context context, Intent intent) {

		//1,首先判断是否开启了防盗
		if(SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false)){
			//2,获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//3,循环遍历短信过程
			for (Object object : objects) {
				//4,获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//5,获取短信对象的基本信息
				String messageBody = sms.getMessageBody();
				//6,判断短信内容是否包含关键字
				if(messageBody.contains("#*alarm*#")){
					//7,播放音乐(准备音乐,MediaPlayer)
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					try {
						mediaPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mediaPlayer.start();
				}
				
				if(messageBody.contains("#*location*#")){
					//8,开启获取位置服务
					context.startService(new Intent(context,com.example.mobilesafe1.service.LocationService.class));
				}
				
				//判断短信内容是否包含关键字
				if(messageBody.contains("#*lockscrenn*#")){
					mDpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
					mCn = new ComponentName(context, AdminReceiver.class);
					//判断激活的状态
					boolean adminActive = mDpm.isAdminActive(mCn);
					if(adminActive){
						mDpm.lockNow();
						//设置开机密码
						mDpm.resetPassword("", 0);
					}
					
				}
				//判断短信内容是否包含关键字
				if(messageBody.contains("#*wipedate*#")){
					mDpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
					mCn = new ComponentName(context,AdminReceiver.class);
					//判断激活状态
					boolean adminActive = mDpm.isAdminActive(mCn);
					if(adminActive){
//						mDpm.wipeData(0);
						//除了清除手机数据外,还清除sd卡中的数据
//						mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					}
				}
			}
		}
	}

}
