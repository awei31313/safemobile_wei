package com.example.mobilesafe1.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.StreamUtils;
import com.example.mobilesafe1.utils.downLoadFileInfoUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.ConfigurationStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

	/**
	 * 更新版本状态码
	 */
	protected static final int UPDATE_VERSION = 100;
	
	/**
	 * 进入应用程序主界面状态码
	 */
	protected static final int ENTER_HOME = 101;

	protected static final String TAG = "SplashActivity";
	
	private TextView tv_version_name;
	private RelativeLayout rl_root;
	
	private String mVersionDescribe;
	private int mlocalVersionCode;
	private String mDownLoadURL;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//提醒更新
			case UPDATE_VERSION:
				showUpdateDialog();
				break;
			//进入程序主界面
			case ENTER_HOME:
				enterHome();
				break;
			}
		};
	};

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//1,初始化UI
		initUi();
		//2,初始化数据
		initData();
		//3，初始化动画
		initAnimation();
		//4，初始化数据库
		initDataBase();
	}

	
	/**
	 * 初始化所有的数据库
	 */
	private void initDataBase() {
		//归属地数据库拷贝过程
		initAdressDB("address.db");
		
	}


	/**
	 * 初始化归属地数据库
	 * @param dbName	要初始化的数据库名称
	 */
	private void initAdressDB(String dbName) {

		File file = getFilesDir();
		file = new File(file,dbName);
		//判断数据文件是否存在
		if(!file.exists()){
			AssetManager assets = getAssets();
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				is = assets.open(dbName);
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = -1;
				while((len = is.read(buffer))!=-1){
					fos.write(buffer, 0, len);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(fos!=null){
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}


	/**
	 * 添加淡入效果
	 */
	private void initAnimation() {

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1500);
		rl_root.setAnimation(alphaAnimation);
		
	}


	protected void showUpdateDialog() {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle("版本更新")
		.setMessage(mVersionDescribe)
		.setPositiveButton("马上更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downLoadAPK();
			}
		})
		.setNegativeButton("稍后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		})
		//设置点击返回按钮的监听
		.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//即使用户点击取消,也需要让其进入应用程序主界面
				enterHome();
				dialog.dismiss();
			}
		})
		.show();
	}


	/**
	 * 从服务器上下载新版本的文件
	 */
	protected void downLoadAPK() {
		//apk下载链接地址,放置apk的所在路径
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+
					downLoadFileInfoUtils.getDownLoadFile(mDownLoadURL);
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mDownLoadURL, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> ResponseInfo) {
					File file = ResponseInfo.result;
					Log.i(TAG, "下载成功");
					//提示用户安装
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(TAG, "下载失败");
				}
				
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					super.onLoading(total, current, isUploading);
					Log.i(TAG, "正在下载。。。。");
				}
				
				@Override
				public void onStart() {
					super.onStart();
					Log.i(TAG, "开始下载");
				}
			});
		}
	}


	/**
	 * @param file	下载好待安装的文件
	 */
	protected void installApk(File file) {
		//系统应用界面,源码,安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//		startActivity(intent);
		startActivityForResult(intent, 0);
	}
	
	//开启一个activity后,返回结果调用的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}


	/**
	 * 进入应用程序主界面
	 */
	protected void enterHome() {

		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
		finish();
	}


	/**
	 * 初始化数据的方法
	 */
	private void initData() {
		//获取版本名称并设置到对应TextView上
		tv_version_name.setText("版本名称："+getVersionName());
		
		//获取本地版本号
		mlocalVersionCode = getVersionCode();
		
		//获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
		//http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
		//json中内容包含:
		/* 更新版本的版本名称
		 * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
		if(!SpUtil.getBoolean(this, ConstantValue.UPDATE_ON_OFF, false)){
			//在发送消息4秒后去处理,ENTER_HOME状态码指向的消息
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}else{
			checkVersion();
		}
	}

	private void checkVersion() {
		new Thread(){
			public void run() {
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				HttpURLConnection openConnection = null;
				try {
					URL url = new URL("http://192.168.2.101:8080/versionInfo.json");
					openConnection = (HttpURLConnection) url.openConnection();
					openConnection.setConnectTimeout(3000);
					openConnection.setReadTimeout(3000);
					openConnection.connect();
					int responseCode = openConnection.getResponseCode();
					if(responseCode==200){
						InputStream is = openConnection.getInputStream();
						String json = StreamUtils.streamToString(is);
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject.getString("versionName");
						mVersionDescribe = jsonObject.getString("versionDescribe");
						String versionCode = jsonObject.getString("versionCode");
						mDownLoadURL = jsonObject.getString("downLoadURL");
						
						//比对版本号(服务器版本号>本地版本号,提示用户更新)
						if(mlocalVersionCode<Integer.parseInt(versionCode)){
							//如果需要更新，发送相应消息，提示更新
							msg.what = UPDATE_VERSION;
						}else{
							//如果不需要更新，也发送相应的消息，直接进入主界面
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = ENTER_HOME;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = ENTER_HOME;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = ENTER_HOME;
				}finally{
					//关闭连接
					openConnection.disconnect();
					//判断连接时间，如果超过小于4秒，就等待到整4秒
					long endTime = System.currentTimeMillis();
					if(endTime-startTime<4000){
						SystemClock.sleep(4000-(endTime-startTime));
					}
					mHandler.sendMessage(msg);
				}
				
			};
		}.start();
	}


	/**
	 * 获取本地应用的版本号
	 * @return	本地应用的版本号
	 */
	private int getVersionCode() {
		
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}


	/**
	 * 获取应用的版本名称
	 * @return	版本名称
	 */
	private String getVersionName() {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化UI的方法
	 */
	private void initUi() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
}
