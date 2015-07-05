package com.cmsc.cmmusic.common.demo;

import java.net.URLEncoder;
import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.app.chatroom.mgmusic.MgMusicActivity;
import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.ExclusiveManagerInterface;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.MvManagerInterface;
import com.cmsc.cmmusic.common.OnlineListenerMusicInterface;
import com.cmsc.cmmusic.common.PaymentManagerInterface;
import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.UserManagerInterface;
import com.cmsc.cmmusic.common.VibrateRingManagerInterface;
import com.cmsc.cmmusic.common.data.AccountResult;
import com.cmsc.cmmusic.common.data.AlbumListRsp;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.common.data.CrbtListRsp;
import com.cmsc.cmmusic.common.data.CrbtOpenCheckRsp;
import com.cmsc.cmmusic.common.data.CrbtPrelistenRsp;
import com.cmsc.cmmusic.common.data.DownloadResult;
import com.cmsc.cmmusic.common.data.DownloadRsp;
import com.cmsc.cmmusic.common.data.LoginResult;
import com.cmsc.cmmusic.common.data.MVMonthPolicy;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.cmsc.cmmusic.common.data.PayPolicy;
import com.cmsc.cmmusic.common.data.PaymentResult;
import com.cmsc.cmmusic.common.data.QueryResult;
import com.cmsc.cmmusic.common.data.RechargeResult;
import com.cmsc.cmmusic.common.data.RegistResult;
import com.cmsc.cmmusic.common.data.RegistRsp;
import com.cmsc.cmmusic.common.data.Result;
import com.cmsc.cmmusic.common.data.SingerInfoRsp;
import com.cmsc.cmmusic.common.data.SmsLoginInfoRsp;
import com.cmsc.cmmusic.common.data.StreamRsp;
import com.cmsc.cmmusic.common.data.TagListRsp;
import com.cmsc.cmmusic.common.data.TransferResult;
import com.cmsc.cmmusic.common.data.UserResult;
import com.cmsc.cmmusic.init.InitCmmInterface;

public class CMMusicDemo extends Activity implements OnClickListener {
	private final static String LOG_TAG = "CMMusicDemo";

	private ProgressDialog dialog;

	private long requestTime;

	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long responseTime = System.currentTimeMillis() - requestTime;

			switch (msg.what) {
			case 0:
				if (msg.obj == null) {
					hideProgressBar();
					Toast.makeText(CMMusicDemo.this, "结果 = null",
							Toast.LENGTH_SHORT).show();
					return;
				}
				new AlertDialog.Builder(CMMusicDemo.this).setTitle("结果")
						.setMessage((msg.obj).toString())
						.setPositiveButton("确认", null).show();
				break;
			}
			hideProgressBar();
			if (null != dialog) {
				dialog.dismiss();
			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmmusic_demo);
		Button projectBtn = (Button) findViewById(R.id.projectBtn);
		projectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MgMusicActivity.class);
				startActivity(intent);
			}
		});
		Button initButton = (Button) this.findViewById(R.id.initButton);
		initButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (!InitCmmInterface.initCheck(CMMusicDemo.this)) {
				dialog = ProgressDialog.show(CMMusicDemo.this, null, "请稍候……",
						true, false);
				requestTime = System.currentTimeMillis();
				new Thread(new T1()).start();
				// } else {
				// new
				// AlertDialog.Builder(CMMusicDemo.this).setTitle("init").setMessage("已初始化过")
				// .setPositiveButton("确认", null).show();
				// }
			}
		});

		// 网络MV包月开通
		Button mvMonthOpen = (Button) this.findViewById(R.id.mvMonthOpen);
		mvMonthOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "serviceId", "serviceId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								MvManagerInterface.openMvMonthByNet(
										CMMusicDemo.this, parameters,
										new CMMusicCallback<Result>() {
											@Override
											public void operationResult(
													Result result) {
												if (null != result) {
													new AlertDialog.Builder(
															CMMusicDemo.this)
															.setTitle(
																	"openMvMonthByNet")
															.setMessage(
																	result.toString())
															.setPositiveButton(
																	"确认", null)
															.show();
												}
											}
										});
							}
						});
			}
		});

		// 短信MV包月开通
		Button mvMonthOpenSms = (Button) this.findViewById(R.id.mvMonthOpenSms);
		mvMonthOpenSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String parameter) {
						Log.i("TAG", "parameter = " + parameter);
						MvManagerInterface.openMvMonthBySMS(CMMusicDemo.this,
								parameter);
					}
				});
			}
		});

		// 网络MV点播
		Button mvDownload = (Button) this.findViewById(R.id.mvDownload);
		mvDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("mvId", new ParameterCallback() {
					@Override
					public void callback(final String parameter) {
						Log.i("TAG", "parameter = " + parameter);
						MvManagerInterface.mvDownloadByNet(CMMusicDemo.this,
								parameter,
								new CMMusicCallback<DownloadResult>() {
									@Override
									public void operationResult(
											DownloadResult result) {
										if (null != result) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("mvDownloadByNet")
													.setMessage(
															result.toString())
													.setPositiveButton("确认",
															null).show();
										}
									}
								});
					}
				});
			}
		});

		// 短信MV点播
		Button mvDownloadSms = (Button) this.findViewById(R.id.mvDownloadSms);
		mvDownloadSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("mvId", new ParameterCallback() {
					@Override
					public void callback(final String parameter) {
						Log.i("TAG", "parameter = " + parameter);
						MvManagerInterface.mvDownloadBySMS(CMMusicDemo.this,
								parameter);
					}
				});
			}
		});

		// MV包月退订
		Button mvMonthCancel = (Button) this.findViewById(R.id.mvMonthCancel);
		mvMonthCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String parameter) {
						Log.i("TAG", "parameters = " + parameter);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								Result t = null;
								t = MvManagerInterface.cancelMvMonth(
										CMMusicDemo.this, parameter);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// 短信MV包月退订
		Button mvMonthCancelSms = (Button) this
				.findViewById(R.id.mvMonthCancelSms);
		mvMonthCancelSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String parameter) {
						Log.i("TAG", "parameters = " + parameter);
						MvManagerInterface.cancelMvMonthBySms(CMMusicDemo.this,
								parameter);
						Toast.makeText(CMMusicDemo.this, "短信已发出！", 0).show();
					}
				});
			}
		});

		// MV包月关系查询
		Button mvMonthQuery = (Button) this.findViewById(R.id.mvMonthQuery);
		mvMonthQuery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "serviceId", "serviceId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								showProgressBar("数据加载中...");
								new Thread() {
									@Override
									public void run() {
										MVMonthPolicy t = null;
										t = MvManagerInterface.getMvMonthQuery(
												CMMusicDemo.this, parameters);
										mUIHandler.obtainMessage(0, t)
												.sendToTarget();
									}
								}.start();
							}
						});
			}
		});

		// 个性化彩铃
		Button ownRingback = (Button) this.findViewById(R.id.ownRingback);
		ownRingback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				RingbackManagerInterface.buyOwnRingbackByNet(CMMusicDemo.this,
						false, new CMMusicCallback<Result>() {
							@Override
							public void operationResult(Result downloadResult) {
								if (null != downloadResult) {
									new AlertDialog.Builder(CMMusicDemo.this)
											.setTitle("buyOwnRingbackByNet")
											.setMessage(
													downloadResult.toString())
											.setPositiveButton("确认", null)
											.show();
								}

								Log.d(LOG_TAG, "vRing Download result is "
										+ downloadResult);
							}
						});
			}
		});

		// 第三方支付系列接口
		Button payment = (Button) this.findViewById(R.id.payment);
		payment.setOnClickListener(new OnClickListener() {
			String[] strs = new String[] { "检查用户是否已注册", "查询用户信息", "修改用户信息",
					"用户余额查询", "用户消费记录查询", "用户转账记录查询", "获取支付商列表", "用户账户充值",
					"包月业务订购", "查询业务策略", "全曲下载", "振铃下载" };

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(CMMusicDemo.this).setTitle("第三方支付系列接口")
						.setItems(strs, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								switch (which) {
								case 0:
									// 检查用户是否已注册
									showParameterDialog("accountName",
											new ParameterCallback() {

												@Override
												public void callback(
														final String accountName) {
													Log.w("TAG",
															"accountName = "
																	+ accountName);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															RegistRsp r = PaymentManagerInterface
																	.checkMember(
																			CMMusicDemo.this,
																			accountName);

															mUIHandler
																	.obtainMessage(
																			0,
																			r)
																	.sendToTarget();
														}
													}.start();
												}
											});

									break;
								case 1:
									// 查询用户信息
									showParameterDialog(new String[] { "UID",
											"accountName" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															UserResult t = null;
															t = PaymentManagerInterface
																	.queryMember(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 2:
									// 修改用户信息
									showParameterDialog("UID",
											new ParameterCallback() {

												@Override
												public void callback(
														final String UID) {
													Log.w("TAG", "UID = " + UID);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															UserResult r = PaymentManagerInterface
																	.updateMember(
																			CMMusicDemo.this,
																			UID,
																			null);

															mUIHandler
																	.obtainMessage(
																			0,
																			r)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 3:
									// 用户余额查询
									showParameterDialog(new String[] { "UID",
											"type" }, new ParameterCallbacks() {

										@Override
										public void callback(
												final String[] parameters) {
											Log.i("TAG", "parameters = "
													+ parameters);
											showProgressBar("数据加载中...");
											new Thread() {
												@Override
												public void run() {
													AccountResult t = null;
													t = PaymentManagerInterface
															.queryAccount(
																	CMMusicDemo.this,
																	parameters[0],
																	parameters[1]);

													mUIHandler.obtainMessage(0,
															t).sendToTarget();
												}
											}.start();
										}
									});
									break;
								case 4:
									// 用户消费记录查询
									showParameterDialog(new String[] { "UID",
											"type", "tradeNum", "startTime",
											"endTime" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															PaymentResult t = null;
															t = PaymentManagerInterface
																	.getPaymentResult(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1],
																			parameters[2],
																			parameters[3],
																			parameters[4]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 5:
									// 用户转账记录查询
									showParameterDialog(new String[] { "UID",
											"type", "startTime", "endTime" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															PaymentResult t = null;
															t = PaymentManagerInterface
																	.getTransferResult(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1],
																			parameters[2],
																			parameters[3]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 6:
									// 获取支付商列表
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											String t = PaymentManagerInterface
													.getPayments(CMMusicDemo.this);

											mUIHandler.obtainMessage(0, t)
													.sendToTarget();
										}
									}.start();
									break;
								case 7:
									// 用户账户充值
									showParameterDialog(new String[] { "UID",
											"type", "amount" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															RechargeResult t = null;
															t = PaymentManagerInterface
																	.getRechargeResult(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1],
																			parameters[2]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 8:
									// 包月业务订购
									showParameterDialog(new String[] { "UID",
											"type", "amount" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															Result t = null;
															t = PaymentManagerInterface
																	.getOrderOpenResult(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1],
																			parameters[2]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 9:
									// 查询业务策略
									showParameterDialog(new String[] { "UID",
											"musicId" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															PayPolicy t = null;
															t = PaymentManagerInterface
																	.getPayPolicy(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 10:
									// 全曲下载
									showParameterDialog(new String[] { "UID",
											"musicId" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															DownloadRsp t = null;
															t = PaymentManagerInterface
																	.getFullSongDownload(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 11:
									// 振铃下载
									showParameterDialog(new String[] { "UID",
											"musicId" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															DownloadRsp t = null;
															t = PaymentManagerInterface
																	.getVibrateDownload(
																			CMMusicDemo.this,
																			parameters[0],
																			parameters[1]);

															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;

								}

								// hideProgressBar();
							}
						}).create().show();
			}
		});

		// CP按次振铃下载接口
		Button cpFullSong = (Button) this.findViewById(R.id.cpFullSong);
		cpFullSong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("musicId", new ParameterCallback() {
					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						CPManagerInterface.getCPFullSongDownloadUrlByNet(
								CMMusicDemo.this, musicId, false,
								new CMMusicCallback<DownloadResult>() {
									@Override
									public void operationResult(
											DownloadResult downloadResult) {
										if (null != downloadResult) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle(
															"getCPFullSongDownloadUrlByNet")
													.setMessage(
															downloadResult
																	.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG,
												"vRing Download result is "
														+ downloadResult);
									}
								});
					}
				});
			}
		});

		// CP短信全曲下载
		Button cpFullSongSMS = (Button) this.findViewById(R.id.cpFullSongSMS);
		cpFullSongSMS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("musicId", new ParameterCallback() {
					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);

						CPManagerInterface.getCPFullSongDownloadUrlBySms(
								CMMusicDemo.this, musicId);
						Toast.makeText(CMMusicDemo.this, "短信已发送", 0).show();
					}
				});

			}
		});

		// CP按次振铃下载接口
		Button cpVibrateRing = (Button) this.findViewById(R.id.cpVibrateRing);
		cpVibrateRing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("musicId", new ParameterCallback() {
					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						CPManagerInterface.queryCPVibrateRingDownloadUrlByNet(
								CMMusicDemo.this, musicId, false,
								new CMMusicCallback<DownloadResult>() {
									@Override
									public void operationResult(
											DownloadResult downloadResult) {
										if (null != downloadResult) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle(
															"queryCPVibrateRingDownloadUrl")
													.setMessage(
															downloadResult
																	.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG,
												"vRing Download result is "
														+ downloadResult);
									}
								});
					}
				});
			}
		});

		// CP短信振铃下载
		Button cpVibrateRingSMS = (Button) this
				.findViewById(R.id.cpVibrateRingSMS);
		cpVibrateRingSMS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("musicId", new ParameterCallback() {
					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);

						CPManagerInterface.queryCPVibrateRingDownloadUrlBySms(
								CMMusicDemo.this, musicId);
						Toast.makeText(CMMusicDemo.this, "短信已发送", 0).show();
					}
				});

			}
		});

		// 转账
		Button transfer = (Button) this.findViewById(R.id.transfer);
		transfer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("UID", new ParameterCallback() {
					@Override
					public void callback(final String UID) {
						Log.i("TAG", "UID = " + UID);
						PaymentManagerInterface.transfer(CMMusicDemo.this,
								false, UID, new CMMusicCallback<Result>() {
									@Override
									public void operationResult(Result result) {
										if (null != result) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("regist")
													.setMessage(
															((TransferResult) result)
																	.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG, "ret is " + result);
									}
								});
					}
				});
			}
		});

		// 登陆
		Button loginOrder = (Button) this.findViewById(R.id.loginOrder);
		loginOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PaymentManagerInterface.login(CMMusicDemo.this, false,
						new CMMusicCallback<Result>() {
							@Override
							public void operationResult(Result result) {
								if (null != result) {
									new AlertDialog.Builder(CMMusicDemo.this)
											.setTitle("regist")
											.setMessage(
													((LoginResult) result)
															.toString())
											.setPositiveButton("确认", null)
											.show();
								}

								Log.d(LOG_TAG, "ret is " + result);
							}
						});
			}
		});

		// 注册
		Button regist = (Button) this.findViewById(R.id.regist);
		regist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PaymentManagerInterface.regist(CMMusicDemo.this, false,
						new CMMusicCallback<RegistResult>() {
							@Override
							public void operationResult(RegistResult result) {
								if (null != result) {
									new AlertDialog.Builder(CMMusicDemo.this)
											.setTitle("regist")
											.setMessage((result).toString())
											.setPositiveButton("确认", null)
											.show();
								}

								Log.d(LOG_TAG, "ret is " + result);
							}
						});
			}
		});

		// 短信赠送彩铃
		Button giveRingbackBySms = (Button) this
				.findViewById(R.id.giveRingbackBySms);
		giveRingbackBySms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								RingbackManagerInterface.giveRingbackBySms(
										CMMusicDemo.this, parameters[0],
										parameters[1]);
								Toast.makeText(CMMusicDemo.this, "短信已发送", 0)
										.show();
							}
						});
			}
		});

		// 短信赠送全曲
		Button giveFullSongBySms = (Button) this
				.findViewById(R.id.giveFullSongBySms);
		giveFullSongBySms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								FullSongManagerInterface.giveFullSongBySms(
										CMMusicDemo.this, parameters[0],
										parameters[1]);
								Toast.makeText(CMMusicDemo.this, "短信已发送", 0)
										.show();
							}
						});
			}
		});

		// 网络赠送全曲
		Button giveFullSongByNet = (Button) this
				.findViewById(R.id.giveFullSongByNet);
		giveFullSongByNet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								showProgressBar("数据加载中...");
								new Thread() {
									@Override
									public void run() {
										Result t = null;
										t = FullSongManagerInterface
												.giveFullSongByNet(
														CMMusicDemo.this,
														parameters[0],
														parameters[1]);
										mUIHandler.obtainMessage(0, t)
												.sendToTarget();
									}
								}.start();
							}
						});
			}
		});

		// 短信赠送振铃
		Button giveVibrateRingBySms = (Button) this
				.findViewById(R.id.giveVibrateRingBySms);
		giveVibrateRingBySms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								VibrateRingManagerInterface
										.giveVibrateRingBySms(CMMusicDemo.this,
												parameters[0], parameters[1]);
								Toast.makeText(CMMusicDemo.this, "短信已发送", 0)
										.show();
							}
						});
			}
		});

		// 网络赠送振铃
		Button giveVibrateRingByNet = (Button) this
				.findViewById(R.id.giveVibrateRingByNet);
		giveVibrateRingByNet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId" },
						new ParameterCallbacks() {
							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								showProgressBar("数据加载中...");
								new Thread() {
									@Override
									public void run() {
										Result t = null;
										t = VibrateRingManagerInterface
												.giveVibrateRingByNet(
														CMMusicDemo.this,
														parameters[0],
														parameters[1]);
										mUIHandler.obtainMessage(0, t)
												.sendToTarget();
									}
								}.start();
							}
						});
			}
		});

		// CP专属全曲下载接口
		Button cpFullSongDownload = (Button) this
				.findViewById(R.id.cpFullSongDownload);
		cpFullSongDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "serviceId", "musicId",
						"codeRate" }, new ParameterCallbacks() {
					@Override
					public void callback(final String[] parameters) {
						Log.i("TAG", "parameters = " + parameters);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								DownloadResult t = null;
								t = CPManagerInterface
										.queryCPFullSongDownloadUrl(
												CMMusicDemo.this,
												parameters[0], parameters[1],
												parameters[2]);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// CP专属振铃下载接口
		Button cpVibrateDownload = (Button) this
				.findViewById(R.id.cpVibrateDownload);
		cpVibrateDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "serviceId", "musicId",
						"codeRate" }, new ParameterCallbacks() {
					@Override
					public void callback(final String[] parameters) {
						Log.i("TAG", "parameters = " + parameters);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								DownloadResult t = null;
								t = CPManagerInterface
										.queryCPVibrateRingDownloadUrl(
												CMMusicDemo.this,
												parameters[0], parameters[1],
												parameters[2]);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// 查询CP专属包月订购关系接口
		Button cpQuery = (Button) this.findViewById(R.id.cpQuery);
		cpQuery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								QueryResult t = null;
								t = CPManagerInterface.queryCPMonth(
										CMMusicDemo.this, serviceId);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// CP专属包月退订接口
		Button cpCancel = (Button) this.findViewById(R.id.cpCancel);
		cpCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								Result t = null;
								t = CPManagerInterface.cancelCPMonth(
										CMMusicDemo.this, serviceId);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// CP专属包月短信订购
		Button cpOpenBySms = (Button) this.findViewById(R.id.cpOpenBySms);
		cpOpenBySms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						CPManagerInterface.openCPMonthBySms(CMMusicDemo.this,
								serviceId);
						Toast.makeText(CMMusicDemo.this, "短信已发送", 0).show();
					}
				});
			}
		});

		// CP专属包月网络订购
		Button cpOpenByNet = (Button) this.findViewById(R.id.cpOpenByNet);
		cpOpenByNet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						CPManagerInterface.openCPMonthByNet(CMMusicDemo.this,
								serviceId, false,
								new CMMusicCallback<Result>() {
									@Override
									public void operationResult(Result result) {
										if (null != result) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("openCpMonth")
													.setMessage(
															result.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG, "ret is " + result);
									}
								});
					}
				});
			}
		});

		// 短信验证码登陆
		Button login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserManagerInterface.smsAuthLogin(CMMusicDemo.this,
						new CMMusicCallback<Result>() {
							@Override
							public void operationResult(Result result) {
								if (null != result) {
									new AlertDialog.Builder(CMMusicDemo.this)
											.setTitle("login")
											.setMessage(result.toString())
											.setPositiveButton("确认", null)
											.show();
								}

								Log.d(LOG_TAG, "ret is " + result);
							}
						});
			}
		});
		// 索要彩铃
		Button crbtAskfor = (Button) this.findViewById(R.id.crbtAskfor);
		crbtAskfor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "receivemdn", "musicId",
						"validCode" }, new ParameterCallbacks() {
					@Override
					public void callback(final String[] parameters) {
						Log.i("TAG", "parameters = " + parameters);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								Result t = null;
								t = RingbackManagerInterface.getCrbtAskfor(
										CMMusicDemo.this, parameters[0],
										parameters[1], parameters[2]);
								mUIHandler.obtainMessage(0, t).sendToTarget();
							}
						}.start();
					}
				});
			}
		});
		// 短信登录验证
		Button smsAuthLoginValidate = (Button) this
				.findViewById(R.id.smsAuthLoginValidate);
		smsAuthLoginValidate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgressBar("数据加载中...");
				new Thread() {
					@Override
					public void run() {
						super.run();

						SmsLoginInfoRsp result = UserManagerInterface
								.smsAuthLoginValidate(CMMusicDemo.this);

						mUIHandler.obtainMessage(0, result).sendToTarget();

					}
				}.start();
			}
		});
		// 根据手机号查询是否开通彩铃
		Button crbtOpenCheck = (Button) this.findViewById(R.id.crbtOpenCheck);
		crbtOpenCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("手机号", new ParameterCallback() {

					@Override
					public void callback(final String phoneNum) {
						Log.i("TAG", "phoneNum = " + phoneNum);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								super.run();

								CrbtOpenCheckRsp result = RingbackManagerInterface
										.crbtOpenCheck(CMMusicDemo.this,
												phoneNum);

								mUIHandler.obtainMessage(0, result)
										.sendToTarget();

							}
						}.start();
					}
				});
			}
		});
		// 查询歌手信息
		Button singerInfo = (Button) this.findViewById(R.id.singerInfo);
		singerInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("歌手ID", new ParameterCallback() {

					@Override
					public void callback(final String singerId) {
						Log.i("TAG", "singerId = " + singerId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								super.run();

								SingerInfoRsp result = MusicQueryInterface
										.getSingerInfo(CMMusicDemo.this,
												singerId);

								mUIHandler.obtainMessage(0, result)
										.sendToTarget();

							}
						}.start();
					}
				});
			}
		});
		// 歌曲ID查询专辑信息
		Button albumListbymusic = (Button) this
				.findViewById(R.id.albumListbymusic);
		albumListbymusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("歌曲ID", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								super.run();

								AlbumListRsp result = MusicQueryInterface
										.getAlbumsByMusicId(CMMusicDemo.this,
												musicId, 1, 5);

								mUIHandler.obtainMessage(0, result)
										.sendToTarget();

							}
						}.start();
					}
				});
			}
		});
		// 歌曲ID查询歌曲信息
		Button musicQuerybymusic = (Button) this
				.findViewById(R.id.musicQuerybymusic);
		musicQuerybymusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("歌曲ID", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								super.run();

								MusicInfoResult result = MusicQueryInterface
										.getMusicInfoByMusicId(
												CMMusicDemo.this, musicId);

								mUIHandler.obtainMessage(0, result)
										.sendToTarget();

							}
						}.start();
					}
				});
			}
		});

		Button btnDel = (Button) this.findViewById(R.id.deletesong);
		btnDel.setOnClickListener(this);

		Button btnFull = (Button) this.findViewById(R.id.fullsong);
		btnFull.setOnClickListener(this);

		Button btnFullSms = (Button) this.findViewById(R.id.fullsongsms);
		btnFullSms.setOnClickListener(this);

		// 赠送彩铃
		Button giveRingback = (Button) this.findViewById(R.id.giveRingback);
		giveRingback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("musicId", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						RingbackManagerInterface.giveRingback(CMMusicDemo.this,
								musicId, new CMMusicCallback<Result>() {
									@Override
									public void operationResult(Result result) {
										if (null != result) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("openRingback")
													.setMessage(
															result.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG, "ret is " + result);
									}
								});
					}
				});
			}
		});

		// 歌曲查詢类
		Button musicQuery = (Button) this.findViewById(R.id.musicQuery);
		musicQuery.setOnClickListener(new OnClickListener() {
			String[] strs = new String[] { "获取榜单信息", "获取榜单音乐信息", "获取专辑信息",
					"获取专辑音乐信息", "获取歌手音乐信息", "获取标签信息", "获取标签音乐信息", "关键字搜索歌曲" };

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(CMMusicDemo.this).setTitle("歌曲查询类")
						.setItems(strs, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								switch (which) {
								case 0:
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											super.run();

											ChartListRsp c = MusicQueryInterface
													.getChartInfo(
															CMMusicDemo.this,
															1, 10);

											mUIHandler.obtainMessage(0, c)
													.sendToTarget();
											
										}
									}.start();

									break;
								case 1:
									showParameterDialog("chartCode",
											new ParameterCallback() {
												@Override
												public void callback(
														final String chartCode) {
													Log.i("TAG", "chartCode = "
															+ chartCode);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															MusicListRsp m = MusicQueryInterface
																	.getMusicsByChartId(
																			CMMusicDemo.this,
																			chartCode,
																			1,
																			5);

															mUIHandler
																	.obtainMessage(
																			0,
																			m)
																	.sendToTarget();
														}
													}.start();
												}
											});

									break;
								case 2:
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											AlbumListRsp a = MusicQueryInterface
													.getAlbumsBySingerId(
															CMMusicDemo.this,
															"235", 1, 5);

											mUIHandler.obtainMessage(0, a)
													.sendToTarget();
										}
									}.start();
									break;
								case 3:
									showParameterDialog("专辑ID",
											new ParameterCallback() {

												@Override
												public void callback(
														final String albumId) {
													Log.w("TAG", "albumId = "
															+ albumId);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															MusicListRsp m = MusicQueryInterface
																	.getMusicsByAlbumId(
																			CMMusicDemo.this,
																			albumId,
																			1,
																			5);

															mUIHandler
																	.obtainMessage(
																			0,
																			m)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;
								case 4:
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											MusicListRsp m = MusicQueryInterface
													.getMusicsBySingerId(
															CMMusicDemo.this,
															"235", 1, 5);

											mUIHandler.obtainMessage(0, m)
													.sendToTarget();

										}
									}.start();
									break;
								case 5:
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											TagListRsp t = MusicQueryInterface
													.getTags(CMMusicDemo.this,
															"10", 1, 5);

											mUIHandler.obtainMessage(0, t)
													.sendToTarget();
										}
									}.start();
									break;
								case 6:
									showProgressBar("数据加载中...");
									new Thread() {
										@Override
										public void run() {
											MusicListRsp t = MusicQueryInterface
													.getMusicsByTagId(
															CMMusicDemo.this,
															"100", 1, 5);

											mUIHandler.obtainMessage(0, t)
													.sendToTarget();

										}
									}.start();
									break;
								case 7:
									showParameterDialog(new String[] { "关键字",
											"关键字类型" },
											new ParameterCallbacks() {

												@Override
												public void callback(
														final String[] parameters) {
													Log.i("TAG",
															"parameters = "
																	+ parameters);
													showProgressBar("数据加载中...");
													new Thread() {
														@Override
														public void run() {
															MusicListRsp t = null;
															t = MusicQueryInterface
																	.getMusicsByKey(
																			CMMusicDemo.this,
																			URLEncoder
																					.encode(parameters[0]),
																			parameters[1],
																			1,
																			5);
															mUIHandler
																	.obtainMessage(
																			0,
																			t)
																	.sendToTarget();
														}
													}.start();
												}
											});
									break;

								}

								// hideProgressBar();
							}
						}).create().show();
			}
		});
		// 彩铃订购
		Button buyRingback = (Button) this.findViewById(R.id.buyRingback);
		buyRingback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("musicId", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						RingbackManagerInterface.buyRingbackByNet(
								CMMusicDemo.this, musicId, false,
								new CMMusicCallback<Result>() {
									@Override
									public void operationResult(Result ret) {
										if (null != ret) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("buyRingback")
													.setMessage(ret.toString())
													.setPositiveButton("确认",
															null).show();
										}
									}
								});
					}
				});
			}
		});
		// 短信彩铃订购
		Button buyRingbackSms = (Button) this.findViewById(R.id.buyRingbackSms);
		buyRingbackSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RingbackManagerInterface.buyRingbackBySms(CMMusicDemo.this,
						"60078701600", "有点甜", "BY2");

			}
		});
		// 振铃下载
		Button vRing = (Button) this.findViewById(R.id.vRing);
		vRing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("musicId", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						VibrateRingManagerInterface
								.queryVibrateRingDownloadUrlByNet(
										CMMusicDemo.this, musicId, false,
										new CMMusicCallback<DownloadResult>() {
											@Override
											public void operationResult(
													DownloadResult downloadResult) {
												if (null != downloadResult) {
													new AlertDialog.Builder(
															CMMusicDemo.this)
															.setTitle(
																	"queryVibrateRingDownloadUrl")
															.setMessage(
																	downloadResult
																			.toString())
															.setPositiveButton(
																	"确认", null)
															.show();
												}

												Log.d(LOG_TAG,
														"vRing Download result is "
																+ downloadResult);
											}
										});
					}
				});

			}
		});
		// 短信振铃下载
		Button vRingSms = (Button) this.findViewById(R.id.vRingsms);
		vRingSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VibrateRingManagerInterface.queryVibrateRingDownloadUrlBySms(
						CMMusicDemo.this, "600902000009331935", null, "有点甜",
						"BY2");
			}
		});
		// 开通会员
		Button openMem = (Button) this.findViewById(R.id.openMem);
		openMem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserManagerInterface.openMemberByNet(CMMusicDemo.this, false,
						new CMMusicCallback<Result>() {
							@Override
							public void operationResult(Result ret) {
								if (null != ret) {
									new AlertDialog.Builder(CMMusicDemo.this)
											.setTitle("openMember")
											.setMessage(ret.toString())
											.setPositiveButton("确认", null)
											.show();
								}
							}
						});
			}
		});

		// 短信全曲 包月
		Button smsSongMonth = (Button) this.findViewById(R.id.smsSongMonth);
		smsSongMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// FullSongManagerInterface.openSongMonthBySms(CMMusicDemo.this);
				Toast.makeText(CMMusicDemo.this, "此接口已隐藏", 0).show();
			}
		});
		// 短信开通会员
		Button smsOpenMem = (Button) this.findViewById(R.id.smsOpenMem);
		smsOpenMem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserManagerInterface.openMemberBySms(CMMusicDemo.this);
			}
		});

		// 获取在线听歌地址
		Button onlineLse = (Button) this.findViewById(R.id.onlineLse);
		onlineLse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog(new String[] { "musicId", "codeRate" },
						new ParameterCallbacks() {

							@Override
							public void callback(final String[] parameters) {
								Log.i("TAG", "parameters = " + parameters);
								showProgressBar("数据加载中...");
								new Thread() {
									@Override
									public void run() {
										StreamRsp s = OnlineListenerMusicInterface
												.getStream(CMMusicDemo.this,
														parameters[0],
														parameters[1]);
										mUIHandler.obtainMessage(0, s)
												.sendToTarget();
									}
								}.start();
							}
						});
			}
		});

		// 获取彩铃试听地址
		Button crbtPrelisten = (Button) this.findViewById(R.id.crbtPrelisten);
		crbtPrelisten.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("musicId", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								super.run();

								CrbtPrelistenRsp c = RingbackManagerInterface
										.getCrbtPrelisten(CMMusicDemo.this,
												musicId);

								mUIHandler.obtainMessage(0, c).sendToTarget();

							}
						}.start();
					}
				});
			}
		});

		// 查询个人铃音库
		Button crbtBox = (Button) this.findViewById(R.id.crbtBox);
		crbtBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgressBar("数据加载中...");
				new Thread() {
					@Override
					public void run() {
						CrbtListRsp c = RingbackManagerInterface
								.getCrbtBox(CMMusicDemo.this);

						mUIHandler.obtainMessage(0, c).sendToTarget();

					}
				}.start();
				// hideProgressBar();
			}
		});

		// 设置默认铃音
		Button setDefaultCrbt = (Button) this.findViewById(R.id.setDefaultCrbt);
		setDefaultCrbt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("crbtId", new ParameterCallback() {

					@Override
					public void callback(final String crbtId) {
						Log.i("TAG", "crbtId = " + crbtId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								Result c = RingbackManagerInterface
										.setDefaultCrbt(CMMusicDemo.this,
												crbtId);

								mUIHandler.obtainMessage(0, c).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// 手机号查询默认铃音
		Button getDefaultCrbt = (Button) this.findViewById(R.id.getDefaultCrbt);
		getDefaultCrbt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("msisdn", new ParameterCallback() {

					@Override
					public void callback(final String msisdn) {
						Log.i("TAG", "msisdn = " + msisdn);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								Result c = RingbackManagerInterface
										.getDefaultCrbt(CMMusicDemo.this,
												msisdn);

								mUIHandler.obtainMessage(0, c).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// 振铃试听地址
		Button ringPrelisten = (Button) this.findViewById(R.id.ringPrelisten);
		ringPrelisten.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("musicId", new ParameterCallback() {

					@Override
					public void callback(final String musicId) {
						Log.i("TAG", "musicId = " + musicId);
						showProgressBar("数据加载中...");
						new Thread() {
							@Override
							public void run() {
								DownloadResult c = VibrateRingManagerInterface
										.getRingPrelisten(CMMusicDemo.this,
												musicId);

								mUIHandler.obtainMessage(0, c).sendToTarget();
							}
						}.start();
					}
				});
			}
		});

		// 专属按次
		Button exclusive_sms = (Button) this.findViewById(R.id.exclusive_sms);
		exclusive_sms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						ExclusiveManagerInterface.exclusiveOnceBySms(
								CMMusicDemo.this, serviceId);
						Toast.makeText(CMMusicDemo.this, "短信已发送", 0).show();
						// ExclusiveManagerInterface.exclusiveOnceByNet(CMMusicDemo.this,
						// serviceId, false, new CMMusicCallback<Result>()
						// {
						// @Override
						// public void operationResult(Result result) {
						// if (null != result) {
						// new AlertDialog.Builder(CMMusicDemo.this)
						// .setTitle("exclusiveOnce").setMessage(result.toString())
						// .setPositiveButton("确认", null).show();
						// }
						//
						// Log.d(LOG_TAG, "ret is " + result);
						// }
						// });
					}
				});
				// ExclusiveManagerInterface.exclusiveOnceSms(CMMusicDemo.this,
				// "600907028000005001");
			}
		});
		// 专属按次网络
		Button exclusive_net = (Button) this.findViewById(R.id.exclusive_net);
		exclusive_net.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showParameterDialog("serviceId", new ParameterCallback() {
					@Override
					public void callback(final String serviceId) {
						Log.i("TAG", "serviceId = " + serviceId);
						ExclusiveManagerInterface.exclusiveOnceByNet(
								CMMusicDemo.this, serviceId,
								new CMMusicCallback<Result>() {
									@Override
									public void operationResult(Result result) {
										if (null != result) {
											new AlertDialog.Builder(
													CMMusicDemo.this)
													.setTitle("exclusiveOnce")
													.setMessage(
															result.toString())
													.setPositiveButton("确认",
															null).show();
										}

										Log.d(LOG_TAG, "ret is " + result);
									}
								});
					}
				});
				// ExclusiveManagerInterface.exclusiveOnceByNet(CMMusicDemo.this,
				// serviceId, prioritySendSms, callback)(,
				// "600907028000005010");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cmmusic_demo, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.deletesong:
			showParameterDialog("crbtId", new ParameterCallback() {

				@Override
				public void callback(final String crbtId) {
					Log.i("TAG", "crbtId = " + crbtId);
					RingbackManagerInterface.deletePersonRing(CMMusicDemo.this,
							crbtId, new CMMusicCallback<Result>() {
								@Override
								public void operationResult(Result result) {
									if (null != result) {
										new AlertDialog.Builder(
												CMMusicDemo.this)
												.setTitle("deletePersonRing")
												.setMessage(result.toString())
												.setPositiveButton("确认", null)
												.show();
									}
								}
							});
				}
			});
			break;
		// 全曲下载
		case R.id.fullsong:

			showParameterDialog("musicId", new ParameterCallback() {

				@Override
				public void callback(final String musicId) {
					Log.i("TAG", "musicId = " + musicId);
					FullSongManagerInterface.getFullSongDownloadUrlByNet(
							CMMusicDemo.this, musicId, false,
							new CMMusicCallback<DownloadResult>() {
								@Override
								public void operationResult(
										final DownloadResult downloadResult) {
									if (null != downloadResult) {
										new AlertDialog.Builder(
												CMMusicDemo.this)
												.setTitle(
														"getFullSongDownloadUrlByNet")
												.setMessage(
														downloadResult
																.toString())
												.setPositiveButton("确认", null)
												.show();
									}

									Log.d(LOG_TAG,
											"FullSong Download result is "
													+ downloadResult);
								}
							});
				}
			});

			break;

		case R.id.fullsongsms:
			FullSongManagerInterface.getFullSongDownloadUrlBySms(this,
					"600902000009331936", null, "有点甜", "BY2");

			break;
		}
	}

	class T1 extends Thread {
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			// if (!InitCmmInterface.initCheck(CMMusicDemo.this)) {
			Hashtable<String, String> b = InitCmmInterface
					.initCmmEnv(CMMusicDemo.this);
			Message m = new Message();
			m.what = 0;
			m.obj = b;
			mUIHandler.sendMessage(m);
			// } else {
			// if (null != dialog) {
			// dialog.dismiss();
			// }
			//
			// Toast.makeText(CMMusicDemo.this, "已初始化过",
			// Toast.LENGTH_LONG).show();
			// }
			Looper.loop();
		}
	}

	private ProgressDialog mProgress = null;

	void showProgressBar(final String msg) {
		Log.d(LOG_TAG, "showProgressBar invoked!");

		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mProgress == null) {
					mProgress = new ProgressDialog(CMMusicDemo.this);
					mProgress.setMessage(msg);
					mProgress.setIndeterminate(false);
					mProgress.setCancelable(false);
					mProgress.show();
				}
			}
		});
	}

	void hideProgressBar() {
		Log.d(LOG_TAG, "hideProgressBar invoked!");
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mProgress != null) {
					mProgress.dismiss();
					mProgress = null;
				}
			}
		});
	}

	void showParameterDialog(String title, final ParameterCallback callback) {
		View view = View.inflate(CMMusicDemo.this, R.layout.parameter_dialog,
				null);
		final EditText edt = (EditText) view.findViewById(R.id.editText1);
		new AlertDialog.Builder(CMMusicDemo.this).setTitle(title).setView(view)
				.setMessage("请输入参数:" + title).setNegativeButton("取消", null)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String parameter = edt.getText().toString();
						if (callback != null) {
							callback.callback(parameter);
						}
					}
				}).show();
	}

	void showParameterDialog(String[] titles, final ParameterCallbacks callback) {
		String title = getStrForArray(titles);
		final MyGroupView view = new MyGroupView(CMMusicDemo.this);
		for (int i = 0; i < titles.length; i++) {
			EditText paramEdt = new EditText(CMMusicDemo.this);
			paramEdt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			paramEdt.setHint(titles[i]);
			view.addView(paramEdt);

		}
		new AlertDialog.Builder(CMMusicDemo.this).setTitle(title).setView(view)
				.setMessage("请输入参数:" + title).setNegativeButton("取消", null)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int count = view.getChildCount();
						String[] parameters = new String[count];
						for (int i = 0; i < count; i++) {
							View child = view.getChildAt(i);
							if (child instanceof EditText) {
								EditText et = (EditText) child;
								parameters[i] = et.getText().toString();
							}
						}
						if (callback != null) {
							callback.callback(parameters);
						}
					}
				}).show();
	}

	interface ParameterCallback {
		void callback(String parameter);
	}

	interface ParameterCallbacks {
		void callback(String[] parameters);
	}

	String getStrForArray(String[] strs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i]);
			if (i < strs.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}
