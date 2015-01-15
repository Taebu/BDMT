
package kr.co.cashqc;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

import java.net.URISyntaxException;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 6..
 */
public class PayActivity extends BaseActivity {

    private WebView mWebView, webview;

    private boolean isISP_call = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        killer.addActivity(this);

        mWebView = (WebView)findViewById(R.id.pay_webview);
        mWebView.setWebViewClient(new MyWebView());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        // mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // mWebView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);

        String url = "http://cashq.co.kr/m/kg_json.php";
        String postData = "Prdtnm=chicken&Prdtprice=13000&MSTR=1234|fried|central|on|empty|cho|410|816|seoulgangnam|123|010-1234-1234|02-123-1234|empty";
        mWebView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                final android.webkit.JsResult result) {
            new AlertDialog.Builder(PayActivity.this).setTitle("알림").setMessage(message)
                    .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setCancelable(false).create().show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                final android.webkit.JsResult result) {
            new AlertDialog.Builder(PayActivity.this)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
    }

    private class MyWebView extends WebViewClient {

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
                String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e("test", "url : " + url);

            // TODO Auto-generated method stub
            if (url.contains("market://") || url.endsWith(".apk")
                    || url.contains("http://market.android.com") || url.contains("vguard")
                    || url.contains("v3mobile") || url.contains("ansimclick")
                    || url.endsWith("ansimclick") || url.toLowerCase().contains("vguardstart")
                    || url.contains("lottesmartpay://") || url.contains("smhyundaiansimclick://")
                    || url.contains("smshinhanansimclick://")
                    || url.contains("smshinhancardusim://") || url.contains("hanaansim://")
                    || url.contains("citiansimmobilevaccine://") || url.contains("droidxantivirus")
                    || url.contains("http://m.ahnlab.com/kr/site/download")
                    || url.contains("ilkansimmobilevaccine://")
                    || url.contains("mpocketansimclick://")
                    || url.toLowerCase().contains("cloudpay://") /*
                                                                  * ||
                                                                  * url.contains
                                                                  * (
                                                                  * "ispmobile://"
                                                                  * )
                                                                  */
                    || url.contains("shinhan-sr-ansimclick://") || url.contains("SAMSUNG")) {
                Intent intent = null;
                try {
                    Log.e("URL ==>", url);
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Log.e("get Scheme ==>", intent.getScheme());
                    Log.e("get Scheme ==>", intent.getDataString());

                } catch (URISyntaxException ex) {
                    Log.e("Browser", "Bad URI " + url + ":" + ex.getMessage());
                }

                if (url.startsWith("intent")) {
                    if (url.contains("com.ahnlab.v3mobileplus")) {
                        try {
                            view.getContext().startActivity(intent.parseUri(url, 0));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        if (getPackageManager().resolveActivity(intent, 0) == null) {
                            String packagename = intent.getPackage();
                            if (packagename != null) {
                                Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                return true;
                            }
                        }
                    }
                }

                try {
                    Uri uri = Uri.parse(intent.getDataString());
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Log.e("ERROR", e.getMessage());
                    return false;
                }
            } else if (url.startsWith("ispmobile")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                Log.d("test", "ISP MOBILE[" + isISP_call + "]: " + url);

                try {
                    isISP_call = true;
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException ex) {
                    isISP_call = false;
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"));
                    startActivity(intent);
                }

                return true;

            } else {
                view.loadUrl(url);
                return true;
            }
            return true;
        }
    }

}
