package com.app.chatroom.mgmusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cmsc.cmmusic.common.demo.R;

/**
 * 咪咕音乐首页
 * 
 * @author Administrator
 * 
 */
public class MgMusicActivity extends Activity {
	ImageButton mgmusic_close_btn;
	LinearLayout group1;// 咪咕客户端下载
	LinearLayout group2;// 音乐榜单
	LinearLayout group3;// 贱人电台
	LinearLayout group4;// 贱曲包月
	LinearLayout group5;// 贱币兑换

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mgmusic_dialog);
		initView();
		initListener();

	}

	void initView() {
		mgmusic_close_btn = (ImageButton) findViewById(R.id.mgmusic_close_btn);
		group1 = (LinearLayout) findViewById(R.id.group1);
		group2 = (LinearLayout) findViewById(R.id.group2);
		group3 = (LinearLayout) findViewById(R.id.group3);
		group4 = (LinearLayout) findViewById(R.id.group4);
		group5 = (LinearLayout) findViewById(R.id.group5);
	}

	void initListener() {
		mgmusic_close_btn.setOnClickListener(listener);
		group1.setOnClickListener(listener);
		group2.setOnClickListener(listener);
		group3.setOnClickListener(listener);
		group4.setOnClickListener(listener);
		group5.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.mgmusic_close_btn:
				finish();
				break;
			case R.id.group1:
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri
						.parse("http://wm.10086.cn/view/html5/download.do?cType=sst_client&cid=0147301&nodeId=7638&ucid=bjstsk&autodown=s");
				intent.setData(content_url);
				startActivity(intent);
				break;
			case R.id.group2:
				Intent intentgroup2 = new Intent(getApplicationContext(),
						AllListActivity.class);
				startActivity(intentgroup2);
				break;
			case R.id.group3:
				break;
			case R.id.group4:
				Intent intentgroup4 = new Intent(getApplicationContext(),
						MonthlyActivity.class);
				startActivity(intentgroup4);
				break;
			case R.id.group5:
				Intent intentgroup5 = new Intent(getApplicationContext(),
						PropActivity.class);
				startActivity(intentgroup5);
				break;
			default:
				break;
			}
		}
	};

}
