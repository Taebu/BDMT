package com.anp.bdmt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCall {

    public static final String PARAM_CALL_DONE = "CALL_DONE";

    public static void call(String phoneNumber, Activity activity) {
        CallEndedListener.createListenerFor(activity);
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(callIntent);
    }

    private static class CallEndedListener extends PhoneStateListener {
        private boolean called = false;
        private final TelephonyManager telephonyManager;
        private final Context context;
        private Activity activity;

        private CallEndedListener(Activity act) {
            this.context = act.getBaseContext();
            this.activity = act;
            this.telephonyManager = (TelephonyManager) context
                    .getSystemService(Activity.TELEPHONY_SERVICE);
        }

        public static void createListenerFor(Activity act) {
            CallEndedListener listener = new CallEndedListener(act);
            listener.telephonyManager.listen(listener, LISTEN_CALL_STATE);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

//            if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                Intent intent = new Intent(context, FakeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }

            if (called && state == TelephonyManager.CALL_STATE_IDLE) {
                called = false;
                telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
                try {
                    Intent t = new Intent(context, activity.getClass());
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    t.putExtras(activity.getIntent());
                    t.putExtra(PARAM_CALL_DONE, true);
                    t.setAction(Intent.ACTION_MAIN);
                    activity.finish();
                    activity = null;
                    context.startActivity(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    called = true;
                }
            }

        }
    }
}