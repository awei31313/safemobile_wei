package com.example.mobilesafe1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.mobilesafe1.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {

	private static final int LIST_OVER = 100;
	private MyAdapter mAdapter;
	private ListView lv_contactlist;
	
	/**
	 * 存储系统里面联系人数据的list集合
	 */
	private List<HashMap<String,String>> mContactList = new ArrayList<HashMap<String,String>>();
	
	/**
	 * 创建Handler对象接收分线程的消息
	 */
	/*private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==100){
				mAdapter = new MyAdapter();
				lv_contactlist.setAdapter(new MyAdapter());
				
			}
		};
	};*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);
		//初始化UI
		initUI();
		//初始化数据
		initData();
		//初始化点击事件
		initOnChlick();
	}
	
	private void initOnChlick() {

		//设置ListView的条目点击监听
		lv_contactlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//1,获取点中条目的索引指向集合中的对象
				if(mAdapter!=null){
					HashMap<String, String> hashMap = mAdapter.getItem(position);
					//2,获取当前条目指向集合对应的电话号码
					String phone = hashMap.get("phone");
					//3,此电话号码需要给第三个导航界面使用
					
					//4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(1, intent);
					
					finish();
				}
			}
		});
	}

	/**
	 * 继承BaseAdapter的私有内部类
	 * @author Administrator
	 */
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mContactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return mContactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(getApplicationContext(), R.layout.item_activity_contactlist, null);
			}
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			return convertView;
		}
		
	}

	private void initData() {
		//因为读取系统联系人,可能是一个耗时操作,放置到子线程中处理
//		Debug.startMethodTracing("abddd");
		new Thread(){
			public void run() {
				//重新加载数据前清空list集合，避免重复
				mContactList.clear();
				//获取内容解析器对象
				ContentResolver resolver = getContentResolver();
				//获取联系人数据库对外提供的URI
				Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
				//做查询系统联系人数据库表过程(读取联系人权限)
				Cursor contact_id = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
				//循环游标,直到没有数据为止
				while(contact_id.moveToNext()){
					//得到用户唯一性id值
					String contactId = contact_id.getString(0);
					HashMap<String, String> hm = new HashMap<String, String>();
					uri = Uri.parse("content://com.android.contacts/data");
					//根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
					Cursor contact_info = resolver.query(uri, new String[]{"data1","mimetype"}, "raw_contact_id = ?",
							new String[]{contactId}, null);
					//循环获取每一个联系人的电话号码以及姓名,数据类型
					while(contact_info.moveToNext()){
						String data = contact_info.getString(0);
						String mimetype = contact_info.getString(1);
						//6,区分类型去给hashMap填充数据
						if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
							//数据非空判断
							if(!TextUtils.isEmpty(data)){
								hm.put("phone", data);
							}
						}else if(mimetype.equals("vnd.android.cursor.item/name")){
							if(!TextUtils.isEmpty(data)){
								hm.put("name", data);
							}
						}
					}
					contact_info.close();
					mContactList.add(hm);
				}
				contact_id.close();
//				mHandler.sendEmptyMessage(LIST_OVER);
				runOnUiThread( new Runnable() {
					public void run() {
						mAdapter = new MyAdapter();
						lv_contactlist.setAdapter(mAdapter);
					}
				});
//				Debug.stopMethodTracing();
			};
		}.start();
	}

	private void initUI() {

		lv_contactlist = (ListView) findViewById(R.id.lv_contactlist);
	}
}
