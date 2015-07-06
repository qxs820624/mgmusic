package com.app.chatroom.mgmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.data.Result;
import com.cmsc.cmmusic.common.demo.CMMusicDemo;
import com.cmsc.cmmusic.common.demo.R;

public class MusicMenuActivity extends Activity {
	String musicId;
	Button music_list_menu_cl_btn;// 设置成彩铃
	Button music_list_menu_sj_btn;// 设置成手机铃声
	Button music_list_menu_cancel_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list_menu);
		Intent intent = getIntent();
		if (null != intent) {
			musicId = intent.getStringExtra("musicid");
		}
		System.out.println("musicid:" + musicId);
		initView();
		initListener();
	}

	void initView() {
		music_list_menu_cl_btn = (Button) findViewById(R.id.music_list_menu_cl_btn);
		music_list_menu_sj_btn = (Button) findViewById(R.id.music_list_menu_sj_btn);
		music_list_menu_cancel_btn = (Button) findViewById(R.id.music_list_menu_cancel_btn);
	}

	void initListener() {
		music_list_menu_cl_btn.setOnClickListener(listener);
		music_list_menu_sj_btn.setOnClickListener(listener);
		music_list_menu_cancel_btn.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.music_list_menu_cl_btn:
				
				break;
			case R.id.music_list_menu_sj_btn:
				break;
			case R.id.music_list_menu_cancel_btn:
				break;
			default:
				break;
			}
		}
	};
}
