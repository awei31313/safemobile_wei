package com.example.mobilesafe1.activity;

import java.util.ArrayList;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.db.bean.BlackNameInfo;
import com.example.mobilesafe1.db.dao.BlackNumberDao;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

//1,复用convertView
//2,对findViewById次数的优化,使用ViewHolder
//3,将ViewHolder定义成静态,不会去创建多个对象
//4,listView如果有多个条目的时候,可以做分页算法,每一次加载20条,逆序返回

public class BlackNumberActivity extends Activity {

	private static final int UPDATE_UI = 100;
	private static final int UPDATE_UI_MORE = 200;
	private ArrayList<BlackNameInfo> mList;
	private Button bt_blacknumber_add;
	private ListView lv_blacknumber_list;
	private BlackNumberDao mDao;
	private MyAdapter mAdapter;
	private int mCount;
	private String mMode = "1";
	private boolean mIsload = true;
	
	private Handler mHandler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_UI:
				if(mAdapter==null){
					mAdapter = new MyAdapter();
					lv_blacknumber_list.setAdapter(mAdapter);
				}else{
					mAdapter.notifyDataSetChanged();
				}
				break;
			case UPDATE_UI_MORE:
				if(mAdapter==null){
					mAdapter = new MyAdapter();
					lv_blacknumber_list.setAdapter(mAdapter);
				}else{
					mAdapter.notifyDataSetChanged();
				}
				mIsload = true;
				break;
			};
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		//初始化UI
		initUI();
		//初始化数据
		initData();
		//初始化事件
		initEvent();
		
		System.out.println("onCreate被调用了");
	}
	
	

	

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public BlackNameInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			//listView的优化
			//1,复用convertView
			//2,减少findViewById()次数
			
			//复用viewHolder步骤一
			ViewHolder holder = null;
			if(convertView==null){
				//复用viewHolder步骤三
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_activity_blacknumber_list, null);
				//复用viewHolder步骤四
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				//复用viewHolder步骤五
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			//设置数据
			holder.tv_phone.setText(getItem(position).phone);
			switch (getItem(position).mode) {
			case "1":
				holder.tv_mode.setText("拦截短信");
				break;
			case "2":
				holder.tv_mode.setText("拦截电话");
				break;
			case "3":
				holder.tv_mode.setText("拦截所有");
				break;
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除数据库中的数据
					mDao.delete(getItem(position).phone);
					//删除list中的数据
					mList.remove(position);
					//通知数据适配器更新
					if(mAdapter!=null)
					mAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
	}
	
	//复用viewHolder步骤二
	private static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	/**
	 * 显示点击添加按钮的对话框
	 */
	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		//找到对话框的View布局对象和控件对象
		View view = View.inflate(this, R.layout.dialog_activity_blacknumber_add, null);
		final EditText et_dialog_activity_blacknumber_add_phone = (EditText) view.findViewById(R.id.et_dialog_activity_blacknumber_add_phone);
		RadioGroup rg_dialog_activity_blacknumber_add_group = (RadioGroup) view.findViewById(R.id.rg_dialog_activity_blacknumber_add_group);
		Button bt__dialog_activity_blacknumber_add_cancel = (Button) view.findViewById(R.id.bt__dialog_activity_blacknumber_add_cancel);
		Button bt__dialog_activity_blacknumber_add_commit = (Button) view.findViewById(R.id.bt__dialog_activity_blacknumber_add_commit);
		//单选框选中变更的监听
		rg_dialog_activity_blacknumber_add_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_dialog_activity_blacknumber_add_sms:
					mMode = "1";
					break;
				case R.id.rb_dialog_activity_blacknumber_add_call:
					mMode = "2";
					break;
				case R.id.rb_dialog_activity_blacknumber_add_all:
					mMode = "3";
					break;
				}
			}
		});
		//确定按钮的监听
		bt__dialog_activity_blacknumber_add_commit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1,获取输入框中的电话号码
				String phone = et_dialog_activity_blacknumber_add_phone.getText().toString().trim();
				if(TextUtils.isEmpty(phone)){
					ToastUtil.show(getApplicationContext(), "输入的号码不能为空");
				}else{
					//2,数据库插入当前输入的拦截电话号码
					mDao.insert(phone, mMode);
					//3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
					BlackNameInfo blackNameInfo = new BlackNameInfo();
					blackNameInfo.phone = phone;
					blackNameInfo.mode = mMode;
					//4,将对象插入到集合的最顶部
					mList.add(0, blackNameInfo);
					if(mAdapter!=null)
					//5,通知数据适配器刷新(数据适配器中的数据有改变了
					mAdapter.notifyDataSetChanged();
					//6,隐藏对话框
					dialog.dismiss();
				}
			}
		});
		//取消按钮的监听
		bt__dialog_activity_blacknumber_add_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}
	
	private void initEvent() {
		
		//点击添加按钮的监听
		bt_blacknumber_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		
		//滑动listview的监听
		lv_blacknumber_list.setOnScrollListener(new OnScrollListener() {
			//滚动过程中,状态发生改变调用方法()
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	飞速滚动状态
//				OnScrollListener.SCROLL_STATE_IDLE	 空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	拿手触摸着去滚动状态	
				
				if(mList!=null&&mCount>mList.size()&&scrollState==OnScrollListener.SCROLL_STATE_IDLE
						&&view.getLastVisiblePosition()>=mList.size()-1&&mIsload){
					mIsload = false;
					new Thread(){
						public void run() {
							mDao = BlackNumberDao.getIntance(getApplicationContext());
							//查询数据库中所有的黑名单
							ArrayList<BlackNameInfo> arrayList = mDao.query(mList.size());
							//将查询出来的arrayList添加到mList的后面
							mList.addAll(arrayList);
							//发送消息到主线程
							mHandler.sendEmptyMessage(UPDATE_UI_MORE);
						};
					}.start();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
	}

	private void initData() {

		new Thread(){
			public void run() {
				mDao = BlackNumberDao.getIntance(getApplicationContext());
				mCount = mDao.getCount();
				//查询数据库中所有的黑名单
				mList = mDao.query(0);
				//发送消息到主线程
				mHandler.sendEmptyMessage(UPDATE_UI);
				
			};
		}.start();
	}

	private void initUI() {

		bt_blacknumber_add = (Button) findViewById(R.id.bt_blacknumber_add);
		lv_blacknumber_list = (ListView) findViewById(R.id.lv_blacknumber_list);
	}
	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		System.out.println("onStart被调用了");
//	}
//	@Override
//	protected void onPause() {
//		super.onStart();
//		System.out.println("onPause被调用了");
//	}
//	@Override
//	protected void onStop() {
//		super.onStart();
//		System.out.println("onStop被调用了");
//	}
//	@Override
//	protected void onResume() {
//		super.onStart();
//		System.out.println("onResume被调用了");
//	}
//	@Override
//	protected void onDestroy() {
//		super.onStart();
//		System.out.println("onDestroy被调用了");
//	}
}
