
package com.anp.ulsanfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anp.ulsanfood.gcm.Util;

/**
 * @author Jung-Hum Cho Created by anp on 14. 10. 17..
 */
public class WebViewActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        activityAnimation(true);
        // activity killer activity add.
        killer.addActivity(this);

        mDialog = new CustomDialog(WebViewActivity.this);

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientClass());
        WebSettings set = mWebView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(false);

        Intent intent = getIntent();

        if (intent.getStringExtra("assort").equals("qna")) {
            mWebView.loadUrl("http://cashq.co.kr/g5/bbs/board.php?bo_table=qna_1");
        } else if (intent.getStringExtra("assort").equals("notice")) {
            mWebView.loadUrl("http://cashq.co.kr/g5/bbs/board.php?bo_table=gonggi");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            // mWebView.goBack();
            // return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this, MainActivity.class));
        killer.removeActivity(this);
    }

    private class WebViewClientClass extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.e("test", "url : " + url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!Util.isOnline(getApplicationContext())) {
                Util.showDialog_normal(WebViewActivity.this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            } else {
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        }
    }

    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }


}
