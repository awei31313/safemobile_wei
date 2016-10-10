package com.example.mobilesafe1.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.db.bean.ProcessInfo;
import com.example.mobilesafe1.engine.ProcessInfoProvider;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ProcessManagerActivity extends Activity implements OnClickListener {

	private TextView tv_activity_processmanager_process_count, tv_activity_processmanager_memory_info,
			tv_activity_processmanager_des;
	private int mProcessCount;
//	private int mSdkVersion;
	private int mUserListSize;
	private int mSystemListSize;
	private long mAvailSpace;
	private String mStrTotalSpace;
	private ListView lv_activity_processmanager_process_list;
	private Button bt_activity_processmanager_select_all, bt_activity_processmanager_select_reverse,
			bt_activity_processmanager_clear, bt_activity_processmanager_setting;
	protected MyAdapter mAdapter;
	private ArrayList<Object> mAllList;// 显示在listview上的集合
	private ArrayList<Integer> mDesIndex;// 存储标题元素坐标的集合

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			String des = (String) msg.obj;
			mAdapter = new MyAdapter();
			lv_activity_processmanager_process_list.setAdapter(mAdapter);
			tv_activity_processmanager_des.setText(des);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processmanager);
//		//判断当前手机系统的SDK版本
//		mSdkVersion = SpUtil.getInt(this, ConstantValue.SDK_VERSION, 0);
//		if(mSdkVersion == 0){
//			mSdkVersion = android.os.Build.VERSION.SDK_INT;
//			SpUtil.putInt(this, ConstantValue.SDK_VERSION, mSdkVersion);
//		}
		// initTitle();
		initTitle();
		// 初始化listview
		initList();
	}
	

	private void initList() {
		// 找到listview下面的所有控件
		tv_activity_processmanager_des = (TextView) findViewById(R.id.tv_activity_processmanager_des);
		lv_activity_processmanager_process_list = (ListView) findViewById(R.id.lv_activity_processmanager_process_list);
		bt_activity_processmanager_select_all = (Button) findViewById(R.id.bt_activity_processmanager_select_all);
		bt_activity_processmanager_select_reverse = (Button) findViewById(
				R.id.bt_activity_processmanager_select_reverse);
		bt_activity_processmanager_clear = (Button) findViewById(R.id.bt_activity_processmanager_clear);
		bt_activity_processmanager_setting = (Button) findViewById(R.id.bt_activity_processmanager_setting);
		// 给按钮设置点击监听
		bt_activity_processmanager_select_all.setOnClickListener(this);
		bt_activity_processmanager_select_reverse.setOnClickListener(this);
		bt_activity_processmanager_clear.setOnClickListener(this);
		bt_activity_processmanager_setting.setOnClickListener(this);

		getData();

		// 设置listview滑动监听
		lv_activity_processmanager_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 滚动过程中调用方法
			// AbsListView中view就是listView对象
			// firstVisibleItem第一个可见条目索引值
			// visibleItemCount当前一个屏幕的可见条目数
			// 总共条目总数
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mDesIndex != null && mAdapter != null) {
					for (int i = mDesIndex.size() - 1; i >= 0; i--) {
						int position = mDesIndex.get(i);
						if (firstVisibleItem >= position) {
							tv_activity_processmanager_des.setText(mAdapter.getItem(position).toString());
							break;
						}
					}
				}
			}
		});

		lv_activity_processmanager_process_list.setOnItemClickListener(new OnItemClickListener() {
			// view选中条目指向的view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter != null) {
					// 标题条目点击无效
					if (mAdapter.getItem(position).getClass().getSimpleName().equals("String")) {
						return;
					} else {
						ProcessInfo mProcessInfo = (ProcessInfo) mAllList.get(position);
						if (mProcessInfo != null) {
							if (!mProcessInfo.packageName.equals(getPackageName())) {
								// 选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
								// 状态取反
								mProcessInfo.isCheck = !mProcessInfo.isCheck;
								// checkbox显示状态切换
								// 通过选中条目的view对象,findViewById找到此条目指向的cb_box,然后切换其状态
								CheckBox cb_box = (CheckBox) view
										.findViewById(R.id.cb_item_activity_processmanager_list_box);
								cb_box.setChecked(mProcessInfo.isCheck);
							}
						}
					}
				}
			}
		});

	}

	private void getData() {
		// 新建一个集合用来存储所有顺序的元素
		mAllList = new ArrayList<Object>();
		// 存储标题元素坐标的集合
		mDesIndex = new ArrayList<Integer>();
		mUserListSize = 0;
		mSystemListSize = 0;
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("加载中...");
		dialog.show();
		new Thread() {
			public void run() {
					// 获取手机所有应用集合
				List<ProcessInfo> processInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
				// 创建分类集合
				ArrayList<Object> userList = new ArrayList<Object>();
				ArrayList<Object> systemList = new ArrayList<Object>();
				// 循环判断添加到各个分类集合
				for (ProcessInfo processInfo : processInfoList) {
					if (processInfo.isSystem) {
						systemList.add(processInfo);
					} else {
						userList.add(processInfo);
					}
				}
				mUserListSize = userList.size();
				mSystemListSize = systemList.size();
				// 给各个分类集合添加相应的标题元素，然后添加到mAllList中
				if (mUserListSize != 0) {
//					Object obj = "用户应用(" + userList.size() + ")";
					Object obj = "用户应用";
					userList.add(0, obj);
					mAllList.addAll(userList);
					int index = mAllList.indexOf(obj);
					mDesIndex.add(index);
				}
				if (mSystemListSize != 0) {
//					Object obj = "系统应用(" + systemList.size() + ")";
					Object obj = "系统应用";
					systemList.add(0, obj);
					mAllList.addAll(systemList);
					int index = mAllList.indexOf(obj);
					mDesIndex.add(index);
				}
				Message msg = mHandler.obtainMessage();
				msg.obj = "用户应用(" + mUserListSize + ")";
				mHandler.sendMessage(msg);
				dialog.dismiss();
			};
		}.start();
	}

	// 初始化Title
	private void initTitle() {
		tv_activity_processmanager_process_count = (TextView) findViewById(
				R.id.tv_activity_processmanager_process_count);
		tv_activity_processmanager_memory_info = (TextView) findViewById(R.id.tv_activity_processmanager_memory_info);

		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_activity_processmanager_process_count.setText("进程总数:" + mProcessCount);

		// 获取可用内存大小,并且格式化
		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

		// 总运行内存大小,并且格式化
		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

		tv_activity_processmanager_memory_info.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_activity_processmanager_select_all:
			selectAll();
			break;
		case R.id.bt_activity_processmanager_select_reverse:
			selectReverse();
			break;
		case R.id.bt_activity_processmanager_clear:
			clearAll();
			break;
		case R.id.bt_activity_processmanager_setting:
			setting();
			break;
		}
	}

	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 清理选中进程
	 */
	private void clearAll() {
		// 1,获取选中进程
		// 2,创建一个记录需要杀死的进程的集合
		List<Object> killProcessList = new ArrayList<Object>();
		//释放空间的大小
		long totalReleaseSpace = 0;
		for (Object obj : mAllList) {
			if(obj instanceof ProcessInfo){
				ProcessInfo processInfo = (ProcessInfo)obj;
				if(processInfo.packageName.equals(getPackageName())){
					continue;
				}
				if(processInfo.isCheck){
					//3,在这个集合中添加需要杀死的进程
					killProcessList.add(obj);
					//4,杀死进程
					ProcessInfoProvider.killProcess(this, processInfo);
					//5,记录释放空间的总大小
					totalReleaseSpace += processInfo.memSize;
				}
			}
		}
		// 6,循环遍历killProcessList,然后去移除mAllList中的对象
		for (Object obj : killProcessList) {
			// 判断当前进程是否在集合总，在就移除
			if (mAllList.contains(obj)) {
				mAllList.remove(obj);
			}
		}
		//7 创建分类集合，获取分类集合的size
		ArrayList<Object> userList = new ArrayList<Object>();
		ArrayList<Object> systemList = new ArrayList<Object>();
		// 循环判断添加到各个分类集合
		for (Object obj : mAllList) {
			if(obj instanceof ProcessInfo){
				ProcessInfo info = (ProcessInfo)obj;
				if (info.isSystem) {
					systemList.add(info);
				} else {
					userList.add(info);
				}
			}
		}
		mUserListSize = userList.size();
		mSystemListSize = systemList.size();
		
		// 8,在集合改变后,需要通知数据适配器刷新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
//		//重新获取数据并更新listview
//		getData();
		// 9,进程总数的更新
		mProcessCount -= killProcessList.size();
		// 10,更新可用剩余空间(释放空间+原有剩余空间 == 当前剩余空间)
		mAvailSpace += totalReleaseSpace;
		// 11,根据进程总数和剩余空间大小
		tv_activity_processmanager_process_count.setText("进程总数:" + mProcessCount);
		tv_activity_processmanager_memory_info.setText("剩余/总共" + Formatter.formatFileSize(this, mAvailSpace) + "/" + mStrTotalSpace);
		// 12,通过吐司告知用户,释放了多少空间,杀死了几个进程,
		String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
		// ToastUtil.show(getApplicationContext(),
		// "杀死了"+killProcessList.size()+"个进程,释放了"+totalRelease+"空间");

		// jni java--c c---java
		// 占位符指定数据%d代表整数占位符,%s代表字符串占位符
		ToastUtil.show(getApplicationContext(), String.format("杀死了%d进程,释放了%s空间", killProcessList.size(), totalRelease));
	}

	private void selectReverse() {
		//1,将所有的集合中的对象上isCheck字段取反
		for (Object obj : mAllList) {
			if(obj instanceof ProcessInfo){
				ProcessInfo processInfo = (ProcessInfo)obj;
				processInfo.isCheck = !processInfo.isCheck;
			}
		}
		//2,通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		//1,将所有的集合中的对象上isCheck字段取反
		for (Object obj : mAllList) {
			if(obj instanceof ProcessInfo){
				ProcessInfo processInfo = (ProcessInfo)obj;
				if(processInfo.packageName.equals(getPackageName())){
					continue;
				}
				processInfo.isCheck = true;
			}
		}
		//2,通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends BaseAdapter {

		// 获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
		@Override
		public int getItemViewType(int position) {
			if (getItem(position).getClass().getSimpleName().equals("String")) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)){
				return mAllList.size();
			}else{
				return mUserListSize+1;
			}
		}

		@Override
		public Object getItem(int position) {
			Object obj = mAllList.get(position);
			if(obj instanceof String){
				String strDes = (String)obj;
				if(strDes.equals("用户应用")){
					return "用户应用(" + mUserListSize + ")";
				}else{
					return "系统应用(" + mSystemListSize + ")";
				}
			}else{
				return mAllList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			// 判断listView的条目类型
			if (type == 0) {
				ViewTitleHolder holder = null;
				if (convertView == null) {
					holder = ViewTitleHolder.getViewTitleHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.item_activity_appmanager_applist_title,
							null);
					holder.tv_item_activity_appmanager_applist_title = (TextView) convertView
							.findViewById(R.id.tv_item_activity_appmanager_applist_title);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				holder.tv_item_activity_appmanager_applist_title.setText(getItem(position).toString());
				return convertView;
				// 否则是内容
			} else {
				ViewContentHolder holder = null;
				if (convertView == null) {
					holder = ViewContentHolder.getViewContentHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.item_activity_processmanager_list,
							null);
					holder.iv_item_activity_processmanager_list_icon = (ImageView) convertView
							.findViewById(R.id.iv_item_activity_processmanager_list_icon);
					holder.tv_item_activity_processmanager_list_name = (TextView) convertView
							.findViewById(R.id.tv_item_activity_processmanager_list_name);
					holder.tv_item_activity_processmanager_list_memory_info = (TextView) convertView
							.findViewById(R.id.tv_item_activity_processmanager_list_memory_info);
					holder.cb_item_activity_processmanager_list_box = (CheckBox) convertView
							.findViewById(R.id.cb_item_activity_processmanager_list_box);
					convertView.setTag(holder);
				} else {
					holder = (ViewContentHolder) convertView.getTag();
				}
				holder.iv_item_activity_processmanager_list_icon
						.setBackgroundDrawable(((ProcessInfo) getItem(position)).icon);
				holder.tv_item_activity_processmanager_list_name.setText(((ProcessInfo) getItem(position)).name);
				String strSize = Formatter.formatFileSize(getApplicationContext(),
						((ProcessInfo) getItem(position)).memSize);
				holder.tv_item_activity_processmanager_list_memory_info.setText(strSize);
				// 本进程不能被选中,所以先将checkbox隐藏掉
				if (((ProcessInfo) getItem(position)).packageName.equals(getPackageName())) {
					holder.cb_item_activity_processmanager_list_box.setVisibility(View.GONE);
				} else {
					holder.cb_item_activity_processmanager_list_box.setVisibility(View.VISIBLE);
				}

				holder.cb_item_activity_processmanager_list_box.setChecked(((ProcessInfo) getItem(position)).isCheck);
				return convertView;
			}
		}

	}

	// holder1
	private static class ViewTitleHolder {
		private static ViewTitleHolder holder;

		private ViewTitleHolder() {

		}

		// 单例模式
		public static ViewTitleHolder getViewTitleHolder() {
			if (holder == null) {
				return new ViewTitleHolder();
			}
			return holder;
		}

		public TextView tv_item_activity_appmanager_applist_title;
	}

	// holder2
	private static class ViewContentHolder {
		private static ViewContentHolder holder;

		private ViewContentHolder() {

		}

		// 单例模式
		public static ViewContentHolder getViewContentHolder() {
			if (holder == null) {
				return new ViewContentHolder();
			}
			return holder;
		}

		public ImageView iv_item_activity_processmanager_list_icon;
		public TextView tv_item_activity_processmanager_list_name;
		public TextView tv_item_activity_processmanager_list_memory_info;
		public CheckBox cb_item_activity_processmanager_list_box;
	}
}
