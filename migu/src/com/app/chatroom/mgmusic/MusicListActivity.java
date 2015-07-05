package com.app.chatroom.mgmusic;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.app.chatroom.adapter.MusicListAdapter;
import com.app.chatroom.contants.ConstantsJrc;
import com.app.chatroom.util.Commond;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.cmsc.cmmusic.common.demo.R;
/**
 * 音乐列表
 * @author Administrator
 *
 */
public class MusicListActivity extends Activity {
	String ChartCode = "";
	ImageButton musiclist_close_btn;
	ListView musiclist_listview;
	RelativeLayout musiclist_progressbar_RelativeLayout;
	public ArrayList<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();// 歌曲列表
	MusicListAdapter musicListAdapter;
	GetListThread getListThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musiclist_dialog);
		initView();
		initListener();
		Intent intent = getIntent();
		ChartCode = intent.getStringExtra("ChartCode");
		System.out.println("歌单ID:" + ChartCode);
		getListThread = new GetListThread();
		getListThread.start();

		musiclist_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MusicInfo m = musicListAdapter.list.get(position);
				System.out.println(m);

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri
						.parse("http://m.12530.com/order/web/in/0022181/ "
								+ m.getMusicId() + "/0001/");
				intent.setData(content_url);
				startActivity(intent);
			}
		});
	}

	private class GetListThread extends Thread {
		private boolean _run = true;

		public void stopThread(boolean run) {
			this._run = !run;
		}

		@Override
		public void run() {
			if (_run) {
				try {
					getAllList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Handler listhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case ConstantsJrc.HANDLER_SHOW_PROGRESS:
				try {
					musiclist_progressbar_RelativeLayout
							.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case ConstantsJrc.HANDLER_CANCEL_PROGRESS:
				try {
					musiclist_progressbar_RelativeLayout
							.setVisibility(View.GONE);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}
		};
	};

	private void getAllList() throws ClientProtocolException, IOException {
		listhandler.sendEmptyMessage(ConstantsJrc.HANDLER_SHOW_PROGRESS);

		MusicListRsp mlist = MusicQueryInterface.getMusicsByChartId(
				getApplicationContext(), ChartCode, 1, 30);

		musicInfoList = (ArrayList<MusicInfo>) mlist.getMusics();
		listhandler.sendEmptyMessage(ConstantsJrc.HANDLER_CANCEL_PROGRESS);
		listhandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					if (musicInfoList != null) {
						musicListAdapter = new MusicListAdapter(
								MusicListActivity.this, musicInfoList,
								musiclist_listview);
						musiclist_listview.setAdapter(musicListAdapter);
						musiclist_listview.requestLayout();
					} else {
						Commond.showToast(getApplicationContext(), "网络出错");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	void initView() {
		musiclist_close_btn = (ImageButton) findViewById(R.id.musiclist_close_btn);
		musiclist_progressbar_RelativeLayout = (RelativeLayout) findViewById(R.id.musiclist_progressbar_RelativeLayout);
		musiclist_listview = (ListView) findViewById(R.id.musiclist_listview);
	}

	void initListener() {
		musiclist_close_btn.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.musiclist_close_btn:
				finish();
				break;

			default:
				break;
			}
		}
	};
}
