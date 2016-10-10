package com.example.mobilesafe1.service;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.engine.AddressDao;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AddressService extends Service {


	private TelephonyManager mTM;
	private WindowManager mWM;
	private LayoutParams mParams;
	private TextView tv_toast_tel;
	private View mToastView;
	private PhoneStateListener mListener;
	private int[] mDrawableIds;
	private int startX;
	private int startY;
	private int mScreenWidth;
	private int mScreenHeight;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String address = (String)msg.obj;
			tv_toast_tel.setText(address);
		};
	};
	private OutCallReceiver outCallReceiver;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onCreate() {
		super.onCreate();
		//第一次开启服务以后,就需要去管理吐司的显示
		//电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听)
		//1,电话管理者对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mListener = new MyPhoneStateListener();
		//2,监听电话状态
		mTM.listen(mListener , PhoneStateListener.LISTEN_CALL_STATE);
		//动态注册电话呼出的广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		outCallReceiver = new OutCallReceiver();
		registerReceiver(outCallReceiver, filter);
		//获取窗体对象
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
	}
	
	private class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = getResultData();
			showToast(data);
			
		}
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			//空闲状态（挂断状态）
			case TelephonyManager.CALL_STATE_IDLE:
				//挂断之后移除mToastView
				if(mWM!=null&&mToastView!=null){
					mWM.removeView(mToastView);
				}
				break;
			//摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			//响铃状态	
			case TelephonyManager.CALL_STATE_RINGING:
				//当接到拨入电话响铃时，显示来电归属地的Toast
				showToast(incomingNumber);
				break;
			}
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消对电话状态的监听(开启服务的时候监听电话的对象)
		if(mTM!=null && mListener!=null){
			mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
		}
		if(outCallReceiver!=null){
			unregisterReceiver(outCallReceiver);
		}
	}

	public void showToast(String incomingNumber) {
		mParams = new WindowManager.LayoutParams(); 
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不能得到焦点
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	默认能够被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;//可见时，保持屏幕高亮
        mParams.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.setTitle("Toast");
        //指定吐司的所在位置(将吐司指定在左上角)
        mParams.gravity = Gravity.LEFT|Gravity.TOP;
        //吐司显示效果(吐司布局文件),xml-->view(吐司),将吐司挂在到windowManager窗体上
        mToastView = View.inflate(getApplicationContext(), R.layout.toast_tel, null);
        tv_toast_tel = (TextView) mToastView.findViewById(R.id.tv_toast_tel);
        //获取sp中存储的toast的坐标
       mParams.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
       mParams.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
      //从sp中获取色值文字的索引,匹配图片,用作展示
        mDrawableIds = new int[]{
        		R.drawable.call_locate_white,
        		R.drawable.call_locate_orange,
        		R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,
        		R.drawable.call_locate_green};
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast_tel.setBackgroundResource(mDrawableIds[toastStyleIndex]);
        //设置mToastView的触摸移动监听
        mToastView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int)event.getRawX();
					startY = (int)event.getRawY();
					System.out.println(startX+":::"+startY);

					break;
				case MotionEvent.ACTION_MOVE:
					//得到移动后的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					//得到移动的x和y的距离
					int disX = moveX-startX;
					int disY = moveY-startY;
					//获取控件移动后的坐标
					mParams.x = mParams.x+disX;
					mParams.y = mParams.y+disY;
					//做容错处理
					if(mParams.x<0)
						mParams.x = 0;
					if(mParams.y<0)
						mParams.y = 0;
					if(mParams.x>mScreenWidth-mToastView.getWidth())
						mParams.x = mScreenWidth-mToastView.getWidth();
					if(mParams.y>mScreenHeight-mToastView.getHeight()-22)
						mParams.y = mScreenHeight-mToastView.getHeight()-22;
					//重置初始坐标
					startX = moveX;
					startY = moveY;
					//告知窗体吐司需要按照手势的移动,去做位置的更新
					mWM.updateViewLayout(mToastView, mParams);
					break;
				case MotionEvent.ACTION_UP:
//					SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_X, mParams.x);
//					SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_Y, mParams.y);
					System.out.println(startX+":::"+startY);
					break;
				}
				return true;
			}
		});
        //查询来电号码的归属地
        query(incomingNumber);
        //在窗体上挂在一个view(SYSTEM_ALERT_WINDOW权限)
        mWM.addView(mToastView, mParams);
	}

	
	private void query(final String incomingNumber) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				//查询得到来电号码的归属地
				String address = AddressDao.getAddress(incomingNumber);
				Message msg = mHandler.obtainMessage();
				msg.obj = address;
				mHandler.sendMessage(msg);
			}
		}.start();
		
	}
		

}
