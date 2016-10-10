package com.example.mobilesafe1.activity;

import java.util.List;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.engine.CommonnumDao;
import com.example.mobilesafe1.engine.CommonnumDao.Child;
import com.example.mobilesafe1.engine.CommonnumDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroup;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commonnumber);
		initUI();
		initData();
	}

	/**
	 * 给可扩展ListView准备数据,并且填充
	 */
	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroup = commonnumDao.getGroup();
		
		mAdapter = new MyAdapter();
		elv_common_number.setAdapter(mAdapter);
		//给可扩展listview注册点击事件
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		//开启系统的打电话界面
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	class MyAdapter extends BaseExpandableListAdapter{
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder = ViewHolder.getViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.child_item_elv_common_number, null);
				holder.tv_child_item_elv_common_number_name = (TextView) convertView.findViewById(R.id.tv_child_item_elv_common_number_name);
				holder.tv_child_item_elv_common_number_number = (TextView) convertView.findViewById(R.id.tv_child_item_elv_common_number_number);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
//			View view = View.inflate(getApplicationContext(), R.layout.child_item_elv_common_number, null);
//			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_child_item_elv_common_number_name);
//			TextView tv_number = (TextView) convertView.findViewById(R.id.tv_child_item_elv_common_number_number);
			
			holder.tv_child_item_elv_common_number_name.setText(getChild(groupPosition, childPosition).name);
			holder.tv_child_item_elv_common_number_number.setText(getChild(groupPosition, childPosition).number);
			
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		
		//dip = dp
		//dpi == ppi	像素密度(每一个英寸上分布的像素点的个数)
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText(getGroup(groupPosition).name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			textView.setPadding(0, 30, 0, 30);
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			return textView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		//孩子节点是否响应事件
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	private static  class ViewHolder{
		private static ViewHolder holder;
		private ViewHolder(){
			
		}
		//单例模式
		public static ViewHolder getViewHolder(){
			if(holder==null){
				return new ViewHolder();
			}
			return holder;
		}
		public TextView tv_child_item_elv_common_number_name;
		public TextView tv_child_item_elv_common_number_number;
	}
}
