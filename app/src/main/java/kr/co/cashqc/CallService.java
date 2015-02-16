
package kr.co.cashqc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import java.lang.reflect.Method;

/**
 * @author Jung-Hum Cho Created by ssenn_000 on 2015-02-06.
 */
public class CallService extends Service {

    public static final String IMG_URL = "http://cashq.co.kr/adm/upload/";

    private ImageView img;

    private LinearLayout ll;

    private Button speaker, end;

    private WebView webView;

    private boolean isExpand = true, isSpeaker = false, isCalled = true;

    private AudioManager audioManager;

    private TelephonyManager telephonyManager;

    StatePhoneReceiver myPhoneStateListener;

    boolean callFromApp=false; // 전화가 어플에서부터 걸린건지 확인하기 위함

    boolean callFromOffHook=false; // idle 변화를 체크하기 위함

    String img1;
    String img2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        img1 = intent.getStringExtra("img1");
        img2 = intent.getStringExtra("img2");

        setWebView(img1, img2);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Point point = new Point();
        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(point);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.overlay_layout, null);

        ll = (LinearLayout)v.findViewById(R.id.ll);

        webView = (WebView)v.findViewById(R.id.webview);

        img = (ImageView)v.findViewById(R.id.expand);

        speaker = (Button)v.findViewById(R.id.speaker);

        end = (Button)v.findViewById(R.id.end);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        myPhoneStateListener = new StatePhoneReceiver(this);
//        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        callFromApp = true;

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "EE!", Toast.LENGTH_SHORT).show();
                if (isExpand) {
                    webView.setVisibility(View.GONE);
//                    ll.setVisibility(View.GONE);
                    isExpand = false;
                } else {
//                    ll.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    isExpand = true;
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSpeaker) {

                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);

                } else {

                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);

                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Class c = Class.forName(telephonyManager.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    Object telephonyService = m.invoke(telephonyManager);
                    c = Class.forName(telephonyService.getClass().getName());
                    m = c.getDeclaredMethod("endCall");
                    m.setAccessible(true);
                    m.invoke(telephonyService);

                    webView.setVisibility(View.GONE);
                    ll.setVisibility(View.GONE);
                    isExpand = false;
                    stopSelf();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams((int)(point.x / 1.5),
                point.y, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(ll, params);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ll != null) {
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeViewImmediate(ll);
            ll = null;
        }
    }

    private void setWebView(String img1, String img2) {

        // webView.setWebViewClient(new WebViewClientClass());
        WebSettings set = webView.getSettings();

        // TODO 버전별 처리 할것

        if (Build.VERSION.SDK_INT > 10) {
            set.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zoomButtonsController;
            try {
                zoomButtonsController = (ZoomButtonsController)webView.getClass()
                        .getMethod("getZoomButtonsController").invoke(webView, null);
                zoomButtonsController.getContainer().setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        // set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        set.setSupportZoom(true);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String url = getHtml(IMG_URL + img1, IMG_URL + img2);

        webView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);

        // webView.loadUrl("http:www.naver.com");
    }

    private String getHtml(String url1, String url2) {
        Log.e("JAY", "params url : " + url1 + "\n" + url2);
        StringBuilder sb = new StringBuilder("<HTML>");
        sb.append("<HEAD>").append("</HEAD>").append("<BODY>");

        if (url1.endsWith("null") && url2.endsWith("null")) {
            sb.append("<p style='text-align:center; margin-top:250px'>");
            sb.append("<img src='http://cashq.co.kr/m/img/img_no_image.png'></p>");
        } else {
            if (!url1.endsWith("null")) {
                sb.append("<img width='100%' src='").append(url1).append("'>");
            }
            if (!url2.endsWith("null")) {
                sb.append("<img width='100%' src='").append(url2).append("'>");
            }
        }
        sb.append("</BODY>").append("</HTML>");
        Log.e("JAY", "html : " + sb.toString());
        return sb.toString();
    }

    private class StatePhoneReceiver extends PhoneStateListener {
        Context context;

        private StatePhoneReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK: // Call is established
                    if (callFromApp) {
                        callFromApp = false;
                        callFromOffHook = true;

                        try {
                            Thread.sleep(500); // 0.5초 정도 딜레이를 줌으로 핸들링하기 쉽게
                        } catch (final InterruptedException e) {
                        }

                        // Activate loudspeaker
                        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);// AudioManager
                                                                        // 를
                                                                        // 전화모드로
                                                                        // !
                        audioManager.setSpeakerphoneOn(true); // 스피커폰을 ㄱㄱ!
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE: // Call is finished
                    if (callFromOffHook) {
                        callFromOffHook = false;
                        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_NORMAL); // Deactivate

                        telephonyManager.listen(myPhoneStateListener, // Remove listener
                                PhoneStateListener.LISTEN_NONE);
                    }
                    break;
            }

        }
    }
}
