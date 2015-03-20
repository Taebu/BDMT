
package kr.co.cashqc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 6..
 */
public class PayActivity extends BaseActivity {

    private Activity mThis = this;

    private WebView mWebView;

    private boolean isISP_call = false;

    private final String CASHQ_CARD = "CASHQ_CARD";

    private final String CASHQ_CELL = "CASHQ_CELL";
    private OrderData mOrderData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        killer.addActivity(this);

        mWebView = (WebView)findViewById(R.id.pay_webview);
        mWebView.setWebViewClient(new MyWebView());
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        // mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // mWebView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);

        mOrderData = (OrderData)getIntent().getSerializableExtra("order");

        byte[] postData = makePostData();

        String payType = getIntent().getStringExtra("pay_type");

        String url = "";

        if (payType.equals(CASHQ_CARD)) {
            url = "http://cashq.co.kr/m/cn_web.php";
        } else if (payType.equals(CASHQ_CELL)) {
            url = "http://cashq.co.kr/m/kg_json.php";
        }

        Log.e("pay", Arrays.toString(postData));

        mWebView.postUrl(url, postData);
    }

    private byte[] makePostData() {

        String menu = mOrderData.getMenu().get(0).getMenuName();
        int quantity = mOrderData.getMenu().size() - 1;
        String name = quantity < 1 ? menu : menu + " 외 " + quantity + "건";

        String price = String.valueOf(mOrderData.getTotal());
        String tradeId = mOrderData.getTradeId();
        String payType = mOrderData.getPayType();
        // String seq =
        // mOrderData.getShopCode().substring(mOrderData.getShopCode().indexOf("_") + 1);
        String seq = mOrderData.getShopCode();

        StringBuilder sb = new StringBuilder();
        sb.append("Prdtprice=").append(price);
        sb.append("&Prdtnm=").append(name);
        sb.append("&Tradeid=").append(tradeId);
        sb.append("&pay_type=").append(payType);
        sb.append("&MSTR=").append(seq);

        Log.e("PayActivity.makePostData", sb.toString());

        return EncodingUtils.getBytes(sb.toString(), "BASE64");

    }

    private class ChromeClient extends WebChromeClient {

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
        public void onPageFinished(WebView view, String url) {

            Log.e("PayActivity.ChromeClient.onPageFinished", "url : " + url);

            String content = view.getTitle();

            Log.e("PayActivity.ChromeClient.onPageFinished", "content : " + content);

            String okUrl = "http://cashq.co.kr/m/okjson.php";

            if (okUrl.equals(url)) {

                try {
                    JSONObject jsonObject = new JSONObject(content);

                    boolean success = false;

                    if (jsonObject.has("success")) {
                        success = jsonObject.getBoolean("success");
                    }

                    Intent intent = new Intent();
                    intent.setClass(mThis, OrderResultActivity.class);
                    intent.putExtra("order", mOrderData);
                    intent.putExtra("success", success);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
                String realm) {
            Log.e("PayActivity.onReceivedHttpAuthRequest", "host : " + host + "\nrealm : " + realm);
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e("PayActivity.shouldOverrideUrlLoading", "url : " + url);

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

    private class OKJsonTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String url = params[0];

            JSONParser jsonParser = new JSONParser();

            return jsonParser.getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            boolean success = false;

            try {
                success = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
