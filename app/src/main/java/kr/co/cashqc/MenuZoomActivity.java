
package kr.co.cashqc;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static kr.co.cashqc.Utils.IMG_URL;

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
