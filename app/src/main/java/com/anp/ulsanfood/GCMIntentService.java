//package com.anp.ulsanfood;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.google.android.gcm.GCMBaseIntentService;
//
//import com.anp.ulsanfood.gcm.Util;
//
//import static com.anp.ulsanfood.CommonUtilities.SENDER_ID;
//
//public class GCMIntentService extends GCMBaseIntentService {
//	private String urlDefault = "http://cashq.co.kr/m/";
//
//	public GCMIntentService() {
//		super(SENDER_ID);
//	}
//
//	private static final String TAG = "===GCMIntentService===";
//
//	@Override
//	protected void onRegistered(Context arg0, String registrationId) {
//		Log.i(TAG, "Device registered: regId = " + registrationId);
//		Util.saveSharedPreferences_string(getApplicationContext(), Utils.REGISTER, registrationId);
//	}
//
//	@Override
//	protected void onUnregistered(Context arg0, String arg1) {
//		Log.i(TAG, "unregistered = " + arg1);
//	}
//
//	@Override
//	protected void onMessage(Context context, Intent intent) {
//		String seq = intent.getStringExtra("seq");
//		String title = intent.getStringExtra("title");
//		String msg = intent.getStringExtra("msg");
//		String bo_table = intent.getStringExtra("bo_table");
//		String board = intent.getStringExtra("board");
//		String wr_id = intent.getStringExtra("wr_id");
//		String link = intent.getStringExtra("link");
//		String get_biz_code = intent.getStringExtra("get_biz_code");
//
//		Intent i = new Intent(context, ShowMsg.class);
//		Bundle b = new Bundle();
//		b.putString("seq", seq);
//		b.putString("title", title);
//		b.putString("msg", msg);
//		b.putString("bo_table", bo_table);
//		b.putString("board", board);
//		b.putString("wr_id", wr_id);
//		b.putString("link", link);
//		b.putString("get_biz_code", get_biz_code);
//		i.putExtras(b);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(i);
//
//		Intent sendIntent;
//		PendingIntent pendingIntent;
//		NotificationManager notiManager;
//		notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		Notification notification;
//
//		notification = new Notification(R.drawable.ic_launcher, "알림이 도착했습니다.",
//				System.currentTimeMillis());
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		sendIntent = new Intent(this, MainActivity.class);
//		pendingIntent = PendingIntent.getActivity(this, 0, sendIntent,
//				PendingIntent.FLAG_ONE_SHOT);
//		notification.defaults = Notification.DEFAULT_SOUND
//				| Notification.DEFAULT_VIBRATE;
//		notification.setLatestEventInfo(this, title, msg, pendingIntent);
//		notiManager.notify(1, notification);
//	}
//
//	@Override
//	protected void onError(Context arg0, String errorId) {
//		Log.i(TAG, "Received error: " + errorId);
//	}
//
//	@Override
//	protected boolean onRecoverableError(Context context, String errorId) {
//		return super.onRecoverableError(context, errorId);
//	}
//}
