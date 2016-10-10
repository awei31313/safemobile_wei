package com.example.mobilesafe1.activity;

import com.example.mobilesafe1.R;
import com.example.mobilesafe1.utils.ConstantValue;
import com.example.mobilesafe1.utils.Md5Util;
import com.example.mobilesafe1.utils.SpUtil;
import com.example.mobilesafe1.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// 初始化UI
		initUI();
		// 初始化数据
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		// 准备数据(文字(9组),图片(9张))
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

		mDrawableIds = new int[] { R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
				R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings };

		// 设置GridView的数据适配器
		gv_home.setAdapter(new MyAdapter());

		// 注册九宫格单个条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				//手机防盗
				case 0:
					showDialog();
					break;
				//手机黑名单
				case 1:
					Intent intent1 = new Intent(getApplicationContext(), BlackNumberActivity.class);
					startActivity(intent1);
					break;
				//手机应用列表
				case 2:
					Intent intent2 = new Intent(getApplicationContext(), AppManagerActivity.class);
					startActivity(intent2);
					break;
				//进程列表
				case 3:
					Intent intent3 = new Intent(getApplicationContext(), ProcessManagerActivity.class);
					startActivity(intent3);
					break;
				//流量统计
				case 4:
					Intent intent4 = new Intent(getApplicationContext(), TrafficActivity.class);
					startActivity(intent4);
					break;
				//跳转到手机杀毒
				case 5:
					startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
					break;
				//缓存清理
				case 6:
					startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
					break;
				//高级工具
				case 7:
					Intent intent7 = new Intent(getApplicationContext(), AToolActivity.class);
					startActivity(intent7);
					break;
				//设置中心
				case 8:
					Intent intent8 = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent8);
					break;
				}
			}
		});
	}

	protected void showDialog() {
		// 判断本地是否有存储密码(sp 字符串)
		String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, null);
		if (TextUtils.isEmpty(psd)) {
			// 1,初始设置密码对话框
			showSetPsdDialog();
		} else {
			// 2,确认密码对话框
			showConfirmPsdDialog();
		}
	}

	private void showConfirmPsdDialog() {
		// 因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
		// view是由自己编写的xml转换成的view对象xml----->view
		final View view = View.inflate(getApplicationContext(), R.layout.dialog_activity_home_confirmpsd, null);
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button bt_cancle_confirm = (Button) view.findViewById(R.id.bt_cancle_confirm);
		Button bt_commit_confirm = (Button) view.findViewById(R.id.bt_commit_confirm);
		// 对话框取消按钮的监听
		bt_cancle_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 对话框确定按钮的监听
		bt_commit_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_confirmpsd_set = (EditText) view.findViewById(R.id.et_confirmpsd_set);
				String confirmPsd = et_confirmpsd_set.getText().toString();
				String savedPsd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, null);

				if (TextUtils.isEmpty(confirmPsd)) {
					ToastUtil.show(getApplicationContext(), "密码不能为空");
				} else {
					if (!Md5Util.encoder(confirmPsd).equals(savedPsd)) {
						ToastUtil.show(getApplicationContext(), "密码错误");
					} else {
						// 进入应用手机防盗模块,开启一个新的activity
						Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
					}
				}
			}
		});
	}

	private void showSetPsdDialog() {
		// 因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
		// view是由自己编写的xml转换成的view对象xml----->view
		final View view = View.inflate(getApplicationContext(), R.layout.dialog_activity_home_setpsd, null);
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button bt_cancle_set = (Button) view.findViewById(R.id.bt_cancle_set);
		Button bt_commit_set = (Button) view.findViewById(R.id.bt_commit_set);
		// 对话框取消按钮的监听
		bt_cancle_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 对话框确定按钮的监听
		bt_commit_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_setpsd_set = (EditText) view.findViewById(R.id.et_setpsd_set);
				EditText et_confirmpsd_set = (EditText) view.findViewById(R.id.et_setpsd_confirm);

				String confirmPsd = et_confirmpsd_set.getText().toString();
				String setPsd = et_setpsd_set.getText().toString();

				if (TextUtils.isEmpty(confirmPsd) || TextUtils.isEmpty(setPsd)) {
					ToastUtil.show(getApplicationContext(), "密码不能为空");
				} else {
					if (!confirmPsd.equals(setPsd)) {
						ToastUtil.show(getApplicationContext(), "两次输入的密码不一致");
					} else {
						// 进入应用手机防盗模块,开启一个新的activity
						Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
						//将密码通过MD5加密存储在SP中
						SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(setPsd));
					}
				}
			}
		});

	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// 找到控件
		gv_home = (GridView) findViewById(R.id.gv_home);

	}

	/**
	 * @author Administrator 自定义数据适配器
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_activity_home_gridview, null);
			}
			ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			tv_name.setText(mTitleStrs[position]);
			return convertView;
		}

	}

}
