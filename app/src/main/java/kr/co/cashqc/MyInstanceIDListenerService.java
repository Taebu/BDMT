package kr.co.cashqc;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * @ Jung-Hum Cho
 * Created by anp on 2016. 1. 5..
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
