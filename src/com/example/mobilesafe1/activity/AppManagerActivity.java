package com.example.mobilesafe1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.db.bean.AppInfo;
import com.example.mobilesafe1.engine.AppInfoProvider;
import com.example.mobilesafe1.utils.StorageList;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener {

	
	private ArrayList<Object> mAllList;
	private ArrayList<Integer> mDesIndex;
	private ListView lv_activity_appmanager_applist;
	private TextView tv_activity_appmanager_des;
	private BaseAdapter mAdapter;
	private PopupWindow mPopupWindow;
	protected AppInfo mAppInfo;
	protected String tag = "AppManagerActivity";
	
	private Handler mHandler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			String des = (String)msg.obj;
			mAdapter = new MyAdapter();
			lv_activity_appmanager_applist.setAdapter(mAdapter);
			tv_activity_appmanager_des.setText(des);
			
		};
	};
	
	private class MyAdapter extends BaseAdapter{

		

		//获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount()+1;
		}
		
		//指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
		@Override
		public int getItemViewType(int position) {
			if(getItem(position).getClass().getSimpleName().equals("String")){
				return 0;
			}else{
				return 1;
			}
		}
		
		@Override
		public int getCount() {
			return mAllList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAllList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			//判断listView的条目类型
			if(type==0){
				ViewTitleHolder holder = null;
				if(convertView==null){
					holder = ViewTitleHolder.getViewTitleHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.item_activity_appmanager_applist_title, null);
					holder.tv_item_activity_appmanager_applist_title = (TextView) convertView.findViewById(R.id.tv_item_activity_appmanager_applist_title);
					convertView.setTag(holder);
				}else{
					holder = (ViewTitleHolder) convertView.getTag();
				}
				holder.tv_item_activity_appmanager_applist_title.setText(getItem(position).toString());
				return convertView;
			//否则是内容
			}else{
				ViewContentHolder holder = null;
				if(convertView==null){
					holder = ViewContentHolder.getViewContentHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.item_activity_appmanager_applist, null);
					holder.iv_item_activity_appmanager_applist_icon = (ImageView) convertView.findViewById(R.id.iv_item_activity_appmanager_applist_icon);
					holder.tv_item_activity_appmanager_applist_name = (TextView) convertView.findViewById(R.id.tv_item_activity_appmanager_applist_name);
					holder.tv_item_activity_appmanager_applist_path = (TextView) convertView.findViewById(R.id.tv_item_activity_appmanager_applist_path);
					convertView.setTag(holder);
				}else{
					holder = (ViewContentHolder) convertView.getTag();
				}
				holder.iv_item_activity_appmanager_applist_icon.setBackgroundDrawable(((AppInfo)getItem(position)).icon);
				holder.tv_item_activity_appmanager_applist_name.setText(((AppInfo)getItem(position)).name);
				if(((AppInfo)getItem(position)).isSdCard){
					holder.tv_item_activity_appmanager_applist_path.setText("SD卡应用");
				}else{
					holder.tv_item_activity_appmanager_applist_path.setText("手机应用");
				}
				return convertView;
			}
		}
		
	}
	
	//holder1
	private static  class ViewTitleHolder{
		private static ViewTitleHolder holder;
		private ViewTitleHolder(){
			
		}
		//单例模式
		public static ViewTitleHolder getViewTitleHolder(){
			if(holder==null){
				return new ViewTitleHolder();
			}
			return holder;
		}
		public TextView tv_item_activity_appmanager_applist_title;
	}
	
	//holder2
	private  static class ViewContentHolder{
		private static ViewContentHolder holder;
		private ViewContentHolder(){
			
		}
		//单例模式
		public static ViewContentHolder getViewContentHolder(){
			if(holder==null){
				return new ViewContentHolder();
			}
			return holder;
		}
		public ImageView iv_item_activity_appmanager_applist_icon;
		public TextView tv_item_activity_appmanager_applist_name;
		public TextView tv_item_activity_appmanager_applist_path;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//获取应用信息数据
		getData();
		System.out.println("onResume被调用了");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		//初始化显示空间的条目
		initTitle();
		//初始化listview
		initList();
	}


	private void getData() {
		//新建一个集合用来存储所有顺序的元素
		mAllList = new ArrayList<Object>();
		mDesIndex = new ArrayList<Integer>();
		final ProgressDialog dialog = new ProgressDialog(AppManagerActivity.this);
		dialog.setMessage("加载中...");
		dialog.show();
		new Thread(){
			public void run() {
				//获取手机所有应用集合
				List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList2(getApplicationContext());
				//创建分类集合
				ArrayList<Object> userList = new ArrayList<Object>();
				ArrayList<Object> systemList = new ArrayList<Object>();
				//循环判断添加到各个分类集合
				for (AppInfo appInfo : appInfoList) {
					if(appInfo.isSystem){
						systemList.add(appInfo);
					}else{
						userList.add(appInfo);
					}
				}
				//给各个分类集合添加相应的标题元素，然后添加到mAllList中
				if(userList.size()!=0){
					Object obj = "用户应用("+userList.size()+")";
					userList.add(0,obj);
					mAllList.addAll(userList);
					int index = mAllList.indexOf(obj);
//					Log.e("index=", index+"");
					mDesIndex.add(index);
				}
				if(systemList.size()!=0){
					Object obj = "系统应用("+systemList.size()+")";
					systemList.add(0,obj);
					mAllList.addAll(systemList);
					int index = mAllList.indexOf(obj);
//					Log.e("index=", index+"");
					mDesIndex.add(index);
				}
				Message msg = mHandler.obtainMessage();
				msg.obj = "用户应用("+userList.size()+")";
				mHandler.sendMessage(msg);
				dialog.dismiss();
			};
		}.start();
	}


	private void initList() {

		lv_activity_appmanager_applist = (ListView) findViewById(R.id.lv_activity_appmanager_applist);
		tv_activity_appmanager_des = (TextView) findViewById(R.id.tv_activity_appmanager_des);

		//设置listview滑动监听
		lv_activity_appmanager_applist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			//滚动过程中调用方法
			//AbsListView中view就是listView对象
			//firstVisibleItem第一个可见条目索引值
			//visibleItemCount当前一个屏幕的可见条目数
			//总共条目总数
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				if(mDesIndex!=null&&mAdapter!=null){
					for (int i = mDesIndex.size()-1; i >= 0; i--) {
						int position = mDesIndex.get(i);
//						Log.e("滚动获取的当前position", position+"");
						if(firstVisibleItem>=position){
							tv_activity_appmanager_des.setText(mAdapter.getItem(position).toString());
							break;
						}
					}
				}
			} 
		});
		
		//设置listview中item点击监听
		lv_activity_appmanager_applist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mAdapter!=null){
					//标题条目点击无效
					if(mAdapter.getItem(position).getClass().getSimpleName().equals("String")){
						return;
					}else{
						mAppInfo = (AppInfo) mAdapter.getItem(position);
						showPopupWindwo(view);
					}
				}
			}
		});
	}


	

	protected void showPopupWindwo(View view) {
		View popupView = View.inflate(this, R.layout.popup_activity_appmanager, null);
		
		TextView tv_popup_activity_appmanager_uninstall = (TextView) popupView.findViewById(R.id.tv_popup_activity_appmanager_uninstall);
		TextView tv_popup_activity_appmanager_start = (TextView) popupView.findViewById(R.id.tv_popup_activity_appmanager_start);
		TextView tv_popup_activity_appmanager_share = (TextView) popupView.findViewById(R.id.tv_popup_activity_appmanager_share);
		
		tv_popup_activity_appmanager_uninstall.setOnClickListener(this);
		tv_popup_activity_appmanager_start.setOnClickListener(this);
		tv_popup_activity_appmanager_share.setOnClickListener(this);
		
		//透明动画(透明--->不透明)
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(600);
		alphaAnimation.setFillAfter(true);
		
		//缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(600);
		alphaAnimation.setFillAfter(true);
		//动画集合Set
		AnimationSet animationSet = new AnimationSet(true);
		//添加两个动画
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		//1,创建窗体对象,指定宽高
		
		mPopupWindow = new PopupWindow(popupView, 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		//2,设置一个透明背景(new ColorDrawable())
//		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.local_popup_bg));
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		//3,指定窗体位置
//		mPopupWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
		mPopupWindow.showAsDropDown(view, view.getWidth()/2, -view.getHeight());
		//4,popupView执行动画
		popupView.startAnimation(animationSet);
	}



	private void initTitle() {
		//2,获取sd卡可用大小,sd卡路径
		String sdPath = getExternalFilesDir(null).getAbsolutePath();
		StorageList storageList = new StorageList(this);
		String[] volumePaths = storageList.getVolumePaths();
		if(volumePaths!=null){
			for (String string : volumePaths) {
				if((string.toLowerCase()).contains("sdcard")){
					sdPath = string;
				}
			}
		}
		//1,获取磁盘(内存,区分于手机运行内存)可用大小,磁盘路径
		String path = getFilesDir().getAbsolutePath();
		//3,获取以上两个路径下文件夹的可用大小
		String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));
		
		TextView tv_activity_appmanager_memory = (TextView) findViewById(R.id.tv_activity_appmanager_memory);
		TextView tv_activity_appmanager_sd_memory = (TextView) findViewById(R.id.tv_activity_appmanager_sd_memory);
		
		tv_activity_appmanager_memory.setText("磁盘可用："+memoryAvailSpace);
		tv_activity_appmanager_sd_memory.setText("SD卡可用："+sdMemoryAvailSpace);
		
	}

	
	/**
	 * 返回值结果单位为byte = 8bit,最大结果为2147483647 bytes
	 * @param path
	 * @return	返回指定路径可用区域的byte类型值
	 */
	private long getAvailSpace(String path) {
		
		//获取可用磁盘大小类
		StatFs statFs = new StatFs(path);
		//获取可用区块的个数
		long count = statFs.getAvailableBlocks();
		//获取区块的大小
		long size = statFs.getBlockSize();
		//区块大小*可用区块个数 == 可用空间大小
		return count*size;
//		Integer.MAX_VALUE	代表int类型数据的最大大小
//		0x7FFFFFFF
//				
//		2147483647bytes/1024 =  2096128 KB
//		2096128KB/1024 = 2047	MB
//		2047MB = 2G
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_popup_activity_appmanager_uninstall:
			if(!mAppInfo.isSystem){
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.packageName));
				startActivity(intent);			
			}else{
				ToastUtil.show(getApplicationContext(), "此应用不能卸载");
			}
			break;
		case R.id.tv_popup_activity_appmanager_start:
			//通过桌面去启动指定包名应用
			PackageManager pm = getPackageManager();
			//通过Launch开启制定包名的意图,去开启应用
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.packageName);
			if(launchIntentForPackage!=null){
				startActivity(launchIntentForPackage);
			}else{
				ToastUtil.show(getApplicationContext(), "此应用不能被开启");
			}
			break;
			//分享(第三方(微信,新浪,腾讯)平台),智慧北京
			//拍照-->分享:将图片上传到微信服务器,微信提供接口api,推广
			//查看朋友圈的时候:从服务器上获取数据(你上传的图片)
		case R.id.tv_popup_activity_appmanager_share:
			//通过短信应用,向外发送短信
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用,应用名称为"+mAppInfo.name);
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		//点击了窗体后消失窗体
		if(mPopupWindow!=null)
		mPopupWindow.dismiss();
	}
	

}
