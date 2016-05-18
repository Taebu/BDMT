
package com.anp.bdmt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.anp.bdmt.Utils.IMG_URL;

/**
 * @author Jung-Hum Cho Created by anp on 15. 6. 2..
 */
public class MenuZoomActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuzoom);

        webView = (WebView)findViewById(R.id.webview_zoom);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);

        String url = getHtml(IMG_URL + getIntent().getStringExtra("img1"), IMG_URL
                + getIntent().getStringExtra("img2"));

        webView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);

        findViewById(R.id.btn_menuzoom_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        findViewById(R.id.btn_menuzoom_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void call() {
        Tracker t = ((CashqApplication)getApplication())
                .getTracker(CashqApplication.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder().setCategory("ShopPageActivity")
                .setAction("Press Button").setLabel("App(BDMT) Call").build());

        String num = getIntent().getStringExtra("tel");
        String name = getIntent().getStringExtra("name");
        //
        // checkContact(mActivity, name, num);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.putExtra(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "디스플레이 네임");
        intent.setData(Uri.parse("tel:" + num));

        startActivity(intent);

        Intent menu = new Intent(new Intent(this, CallService.class));

//        menu.putExtra("pre_pay", mPrePay);
        menu.putExtra("pay", getIntent().getStringExtra("pay"));
        menu.putExtra("img1", getIntent().getStringExtra("img1"));
        menu.putExtra("img2", getIntent().getStringExtra("img2"));

        startService(menu);

        // mNum = "tel:" + getIntent().getStringExtra("tel");
        // startActivity(new Intent(Intent.ACTION_CALL,
        // Uri.parse("tel:010-3745-2742")));
        // PhoneCall.call(mNum, mActivity);
    }

    private String getHtml(String url1, String url2) {
        Log.e(TAG, "params url : " + url1 + "\n" + url2);
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
        Log.e(TAG, "html : " + sb.toString());
        return sb.toString();
    }
}
