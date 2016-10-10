package com.example.mobilesafe1.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class SDCacheClearActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(getApplicationContext());
		textView.setText("SDCacheClearActivity");
		textView.setTextColor(Color.BLACK);
		setContentView(textView);
	}
}
