package com.app.chatroom.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.demo.R;

public class Commond {
	private String gamehtml, lsgh;
	private int downflg;
	private String newpath, link;
	private File donghuadownload, zipFile, htmlfile;
	private String htmlname, foldername;
	static List<String> urls = new ArrayList<String>();
	private static Activity context;

	/**
	 * 显示提示消息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(final Context context, String msg) {
		if (context == null)
			return;
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastRoot = vi.inflate(R.layout.toastzdy, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		TextView tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
		if (tv != null)
			tv.setText(msg);
		if (!TextUtils.isEmpty(msg)) {
			toast.show();
		}
	}

}
