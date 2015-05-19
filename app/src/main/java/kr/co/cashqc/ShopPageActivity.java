
package kr.co.cashqc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.cashqc.gcm.Util;

import static kr.co.cashqc.MainActivity.TOKEN_ID;
import static kr.co.cashqc.Utils.IMG_URL;
import static kr.co.cashqc.Utils.checkContact;
import static kr.co.cashqc.Utils.initExpandableListViewHeight;
import static kr.co.cashqc.Utils.insertMenuLevel2;
import static kr.co.cashqc.Utils.setExpandableListViewHeight;
import static kr.co.cashqc.Utils.setListViewHeightBasedOnChildren;

/**
 * @author Jung-Hum Cho Created by Administrator on 2014-10-16.
 */
public class ShopPageActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    private ShopPageActivity mThis = this;

    private CustomDialog mDialog;

    private String mNum = null;

    private ExpandableListView mListView;

    private RatingBar mRatingBar;

    private TextView tvReviewCount, tvRatingScore;

    private boolean mIsExpandedReview = false;

    private ArrayList<HashMap<String, String>> mMenuImgList;

    private ShopMenuData mData;

    private ScrollView mScrollView;

    private WebView mWebView;

    private RelativeLayout mMenuImgLayout;

    private String mPrePay;

    private ListView mReviewListView;

    private LinearLayout mListMenu;

    private Button switcher;

    private boolean isWeb = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppage);

        // mThis killer mThis add.
        killer.addActivity(this);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        // activityAnimation(true);

        mDialog = new CustomDialog(this);

        setRowLayout();
        setLayout();

        float reviewRating = Float.parseFloat(getIntent().getStringExtra("review_rating"));

        String reviewCount = getIntent().getStringExtra("review_cnt");

        tvReviewCount.setText(reviewCount + "개 리뷰");

        tvRatingScore.setText(String.valueOf(reviewRating));

        mRatingBar.setRating(reviewRating);

        mRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String phoneNum = getPhoneNumber();

                if (phoneNum.isEmpty())
                    phoneNum = "4444444444";

                new ReviewDialog(mThis, getIntent().getStringExtra("name"), getIntent()
                        .getStringExtra("seq"), phoneNum).show();
                return false;
            }
        });

        tvReviewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIsExpandedReview = !mIsExpandedReview;

                setReviewView(mIsExpandedReview);

            }
        });

        setWebView();

        // mPrePay = getIntent().getStringExtra("pre_pay");
        // Log.e("ShopPageActivity", "prepay : " + mPrePay);

        boolean hasList = "1".equals(getIntent().getStringExtra("pay"));

        if (hasList) {
            setListView();
            switcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isWeb = !isWeb;

                    isVisibilityWebView(isWeb);

                }
            });
        } else {
            switcher.setVisibility(View.GONE);
        }

        isVisibilityWebView(isWeb);

    }

    private void setLayout() {

        mListMenu = (LinearLayout)findViewById(R.id.shoppage_listmenu);

        mWebView = (WebView)findViewById(R.id.shoppage_webview);
        mListView = (ExpandableListView)findViewById(R.id.shoppage_listview);
        mMenuImgLayout = (RelativeLayout)findViewById(R.id.shoppage_menulist);
        mScrollView = (ScrollView)findViewById(R.id.shoppage_scrollview);

        mRatingBar = (RatingBar)findViewById(R.id.shoppage_rating);
        tvRatingScore = (TextView)findViewById(R.id.shoppage_ratingscore);
        tvReviewCount = (TextView)findViewById(R.id.shoppage_reviewcount);

        switcher = (Button)findViewById(R.id.shoppage_switcher);

        mReviewListView = (ListView)findViewById(R.id.shoppage_reviewlistview);

        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void setReviewView(boolean isExpanded) {

        int draw;
        int visibility;

        if (isExpanded) {
            new ReviewTask().execute(getIntent().getStringExtra("seq"));
            visibility = View.VISIBLE;
            draw = R.drawable.btn_list_open;
        } else {
            visibility = View.GONE;
            draw = R.drawable.btn_list_close;
        }

        mReviewListView.setVisibility(visibility);
        tvReviewCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, draw, 0);
    }

    private class ReviewTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String seq = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/get_review.php?seq=6867")
                    .buildUpon().appendQueryParameter("seq", seq).toString();

            return new JSONParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                ArrayList<ReviewData> reviewDataList = new ArrayList<ReviewData>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    ReviewData reviewData = new ReviewData();

                    if (object.has("seq"))
                        reviewData.setSeq(object.getString("seq"));

                    if (object.has("mb_hp"))
                        reviewData.setPhone(object.getString("mb_hp"));

                    if (object.has("mb_nick"))
                        reviewData.setNick(object.getString("mb_nick"));

                    if (object.has("content"))
                        reviewData.setContent(object.getString("content"));

                    if (object.has("rating"))
                        reviewData.setRating(Integer.parseInt(object.getString("rating")));

                    if (object.has("insdate"))
                        reviewData.setInsdate(object.getString("insdate"));

                    if (object.has("token_id"))
                        reviewData.setTokenId(object.getString("token_id"));

                    reviewDataList.add(reviewData);
                }

                ReviewListAdapter adapter = new ReviewListAdapter(mThis, reviewDataList,
                        mOnClickListener);
                mReviewListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(mReviewListView);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String seq = v.getTag(R.id.seq).toString();
            switch (v.getId()) {
                case R.id.row_review_modify:
                    new ReviewDialog(mThis, getIntent().getStringExtra("name"), seq).show();
                    break;
                case R.id.row_review_remove:
                    new ReviewRemoveTask().execute(seq);
                    break;
            }
        }
    };

    private class ReviewRemoveTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String seq = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/set_review.php?mode=del")
                    .buildUpon().appendQueryParameter("seq", seq)
                    .appendQueryParameter("token_id", TOKEN_ID).toString();

            Log.e(TAG, "remove url : " + url);
            return new JSONParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, s);
            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }

    @Override
    protected void onRestart() {
        // setCartCount(this);
        Log.e("life", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.e("life", "onStart");
        // setCartCount(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e("life", "onResume");
        // setCartCount(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        // setCartCount(this);
        Log.e("life", "onStop");
        super.onStop();
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        Log.e("life", "finish");
        super.finish();
        activityAnimation(false);
    }

    public Bitmap combineImage(Bitmap bmp1, Bitmap bmp2) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;

        Bitmap bitmap = Bitmap.createScaledBitmap(bmp1, bmp1.getWidth(),
                bmp1.getHeight() + bmp2.getHeight(), true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(bmp1, 0, 0, p);
        c.drawBitmap(bmp2, 0, bmp1.getHeight(), p);

        bmp1.recycle();
        bmp2.recycle();

        return bitmap;
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        }
    }

    private void setRowLayout() {

        final ImageView thm = (ImageView)findViewById(R.id.list_thm);
        TextView name = (TextView)findViewById(R.id.cashq_list_name);
        TextView time1 = (TextView)findViewById(R.id.cashq_list_time1);
        TextView time2 = (TextView)findViewById(R.id.cashq_list_time2);
        TextView minPay = (TextView)findViewById(R.id.min_pay);
        TextView distance = (TextView)findViewById(R.id.cashq_list_distance);
        TextView dong = (TextView)findViewById(R.id.dong);
        Button btnTel = (Button)findViewById(R.id.tel_btn);
        TextView callcnt = (TextView)findViewById(R.id.calllog);
        // ImageView iconCalllog = (ImageView)findViewById(R.id.icon_calllog);
        RatingBar score = (RatingBar)findViewById(R.id.shoplist_rating);
        ImageView separatorRow = (ImageView)findViewById(R.id.separator_row);
        ImageView img2000 = (ImageView)findViewById(R.id.row_img_point);

        callcnt.setVisibility(View.VISIBLE);
        // iconCalllog.setVisibility(View.VISIBLE);

        name.setText(getIntent().getStringExtra("name"));
        time1.setText(getIntent().getStringExtra("time1") + " ~");
        time2.setText(getIntent().getStringExtra("time2"));
        minPay.setText(getIntent().getStringExtra("minpay"));
        distance.setText(getIntent().getStringExtra("distance"));
        dong.setText(getIntent().getStringExtra("delivery_comment_cashq"));
        callcnt.setText(getIntent().getStringExtra("callcnt") + " 건 주문");

        score.setRating(Float.parseFloat(getIntent().getStringExtra("review_rating")));

        if ("".equals(getIntent().getStringExtra("pre_pay"))) {
            LinearLayout ll = (LinearLayout)findViewById(R.id.thm_layout);
            ll.setVisibility(View.GONE);
            score.setVisibility(View.GONE);
            btnTel.setText("일반\n주문");
            btnTel.setBackgroundResource(R.drawable.btn_list_gray);
            minPay.setText("포인트 적립 불가");

            separatorRow.setBackgroundResource(R.drawable.list_title_gray);
            img2000.setVisibility(View.GONE);
        } else if ("gl".equals(getIntent().getStringExtra("pre_pay"))) {
            separatorRow.setBackgroundResource(R.drawable.list_title_gold);
            btnTel.setText("골드\n주문");
            btnTel.setBackgroundResource(R.drawable.btn_list_gold);
        } else if ("sl".equals(getIntent().getStringExtra("pre_pay"))) {
            separatorRow.setBackgroundResource(R.drawable.list_title_silver);
            btnTel.setBackgroundResource(R.drawable.btn_list_silver);
            btnTel.setText("실버\n주문");
        }

        dong.setSingleLine(true);
        dong.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        dong.setSelected(true);

        btnTel.setFocusable(true);
        findViewById(R.id.tel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.tel_btn) {

                    String num = getIntent().getStringExtra("tel");
                    String name = getIntent().getStringExtra("name");

                    checkContact(mThis, name, num);

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.putExtra(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            "디스플레이 네임");
                    intent.setData(Uri.parse("tel:" + num));

                    startActivity(intent);

                    Intent menu = new Intent(new Intent(mThis, CallService.class));

                    menu.putExtra("pre_pay", mPrePay);
                    menu.putExtra("pay", getIntent().getStringExtra("pay"));
                    menu.putExtra("img1", getIntent().getStringExtra("img1"));
                    menu.putExtra("img2", getIntent().getStringExtra("img2"));
                    startService(menu);
                }

                // mNum = "tel:" + getIntent().getStringExtra("tel");
                // startActivity(new Intent(Intent.ACTION_CALL,
                // Uri.parse("tel:010-3745-2742")));
                // PhoneCall.call(mNum, mThis);
            }
        });

        ImageLoader.getInstance().displayImage(IMG_URL + getIntent().getStringExtra("thumb"), thm,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        thm.setImageResource(R.drawable.img_no_image_80x120);
                    }
                });

    }

    private void setWebView() {

        // mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new WebViewClientClass());
        WebSettings set = mWebView.getSettings();

        // TODO 버전별 처리 할것

        if (Build.VERSION.SDK_INT > 10) {
            set.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zoomButtonsController;
            try {
                zoomButtonsController = (ZoomButtonsController)mWebView.getClass()
                        .getMethod("getZoomButtonsController").invoke(mWebView, null);
                zoomButtonsController.getContainer().setVisibility(View.GONE);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        // set.setJavaScriptEnabled(true);
        // set.setBuiltInZoomControls(false);
        set.setBuiltInZoomControls(true);
        // set.setSupportZoom(false);
        set.setSupportZoom(true);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String url;

        if ("".equals(getIntent().getStringExtra("pre_pay"))) {
            url = getNoImage();
        } else {
            url = getHtml(IMG_URL + getIntent().getStringExtra("img1"), IMG_URL
                    + getIntent().getStringExtra("img2"));
        }

        mWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
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

    private String getNoImage() {
        return String.valueOf(new StringBuilder("<HTML>").append("<HEAD>").append("</HEAD>")
                .append("<BODY>").append("<p style='text-align:center; margin-top:250px'>")
                .append("<img src='http://cashq.co.kr/m/img/img_no_image.png'></p>")
                .append("</BODY>").append("</HTML>"));
    }

    private void isVisibilityWebView(boolean isWebView) {

        int webViewVisibility;
        int listViewVisibility;
        String content;

        if (isWebView) {
            webViewVisibility = View.VISIBLE;
            listViewVisibility = View.GONE;
            content = "메뉴 보기";
        } else {
            webViewVisibility = View.GONE;
            listViewVisibility = View.VISIBLE;
            content = "전단지 보기";
        }

        // btnUp.setVisibility(listViewVisibility);
        // mListView.setVisibility(listViewVisibility);
        // mMenuImgLayout.setVisibility(listViewVisibility);
        mListMenu.setVisibility(listViewVisibility);
        mWebView.setVisibility(webViewVisibility);

        switcher.setText(content);

    }

    private void setListView() {

        // btnUp.setVisibility(View.VISIBLE);
        // mListView.setVisibility(View.VISIBLE);
        // mMenuImgLayout.setVisibility(View.VISIBLE);

        String storeCode = getIntent().getStringExtra("store_code");

        new MenuTask().execute(storeCode);

        new MenuImageTask().execute(storeCode);

    }

    private class MenuTask extends AsyncTask<String, Void, JSONObject> {
        public MenuTask() {
            super();
            mDialog = new CustomDialog(ShopPageActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            StringBuilder sb = new StringBuilder("http://cashq.co.kr/ajax/get_menu.php?store_code=");
            sb.append(params[0]);

            return new JSONParser().getJSONObjectFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {

                Log.e("ShopMenuActivity", object.toString());

                mData = makeShopMenuData(object);

                ShopMenuAdapter adapter = new ShopMenuAdapter(mThis, mData, mOnDismissListener,
                        mThis);

                mListView.setAdapter(adapter);

                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    mListView.expandGroup(i);
                }

                initExpandableListViewHeight(mListView);

                mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
                        setExpandableListViewHeight(parent, groupPosition);
                        return false;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();

        }
    }

    private ShopMenuData makeShopMenuData(JSONObject jsonObject) {

        // try {
        // shop.setShopName(jsonObject.getString("name"));
        // shop.setShopCode(jsonObject.getString("storecode"));
        //
        // JSONArray arrayLevel1 = jsonObject.getJSONArray("posts");
        //
        // ArrayList<MenuData> listLevel1 = new ArrayList<MenuData>();
        //
        // // level 1
        // for (int i = 0; i < arrayLevel1.length(); i++) {
        //
        // JSONObject objectLevel1 = arrayLevel1.getJSONObject(i);
        //
        // MenuData level1 = new MenuData();
        //
        // if (objectLevel1.has("label"))
        // level1.setLabel(objectLevel1.getString("label"));
        //
        // if (objectLevel1.has("id"))
        // level1.setId(objectLevel1.getString("id"));
        //
        // if (!(objectLevel1.getString("price").equals("")))
        // level1.setPrice(Integer.parseInt(objectLevel1.getString("price")));
        //
        // level1.setCode(shop.getShopCode(), level1.getId());
        //
        // // level 2
        // if (objectLevel1.has("children")) {
        //
        // JSONArray arrayLevel2 = objectLevel1.getJSONArray("children");
        //
        // ArrayList<MenuData> listLevel2 = new ArrayList<MenuData>();
        //
        // for (int y = 0; y < arrayLevel2.length(); y++) {
        //
        // JSONObject objectLevel2 = arrayLevel2.getJSONObject(y);
        //
        // MenuData level2 = new MenuData();
        //
        // if (objectLevel2.has("label"))
        // level2.setLabel(objectLevel2.getString("label"));
        //
        // if (objectLevel2.has("id"))
        // level2.setId(objectLevel2.getString("id"));
        //
        // if (objectLevel2.getString("price") != null)
        // level2.setPrice(objectLevel2.getInt("price"));
        //
        // level2.setCode(level1.getCode(), level2.getId());
        //
        // // level 3
        // if (objectLevel2.has("children")) {
        //
        // JSONArray arrayLevel3 = objectLevel2.getJSONArray("children");
        //
        // ArrayList<MenuData> listLevel3 = new ArrayList<MenuData>();
        //
        // for (int j = 0; j < arrayLevel3.length(); j++) {
        //
        // JSONObject objectLevel3 = arrayLevel3.getJSONObject(j);
        //
        // MenuData level3 = new MenuData();
        //
        // if (objectLevel3.has("label"))
        // level3.setLabel(objectLevel3.getString("label"));
        //
        // if (objectLevel3.has("id"))
        // level3.setId(objectLevel3.getString("id"));
        //
        // if (objectLevel3.getString("price") != null)
        // level3.setPrice(objectLevel3.getInt("price"));
        //
        // level3.setCode(level2.getCode(), level3.getId());
        //
        // // level 4
        // if (objectLevel3.has("children")) {
        //
        // ArrayList<MenuData> listLevel4 = new ArrayList<MenuData>();
        //
        // JSONArray arrayLevel4 = objectLevel3.getJSONArray("children");
        //
        // for (int k = 0; k < arrayLevel4.length(); k++) {
        // JSONObject objectLevel4 = arrayLevel4.getJSONObject(k);
        //
        // MenuData level4 = new MenuData();
        //
        // if (objectLevel4.has("label"))
        // level4.setLabel(objectLevel4.getString("label"));
        //
        // if (objectLevel4.has("id"))
        // level4.setId(objectLevel4.getString("id"));
        //
        // if (objectLevel4.getString("price") != null)
        // level4.setPrice(objectLevel4.getInt("price"));
        //
        // level4.setCode(level3.getCode(), level4.getId());
        //
        // listLevel4.add(level4);
        // }
        // level3.setChild(listLevel4);
        // } // level 4
        // listLevel3.add(level3);
        // }
        // level2.setChild(listLevel3);
        // } // level 3
        // listLevel2.add(level2);
        // }
        // level1.setChild(listLevel2);
        // } // level 2
        // listLevel1.add(level1);
        // } // level1
        //
        // // shop.setMenu(listLevel1);
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }

        ShopMenuData shop = new ShopMenuData();

        try {

            shop.setShopName(jsonObject.getString("name"));
            String storeCode = jsonObject.getString("storecode");
            String seq = storeCode.substring(storeCode.indexOf("_") + 1);
            shop.setShopCode(seq);
            shop.setShopPhone(jsonObject.getString("phone"));
            shop.setShopVPhone(jsonObject.getString("vphone"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        shop.setMenu(makeMenuData(jsonObject, shop.getShopCode()));

        for (int i = 0; i < shop.getMenu().size(); i++) {

            Log.e("fucking_tree", "level 1 : " + shop.getMenu().get(i).getCode()
                    + shop.getMenu().get(i).getLabel());

            if (shop.getMenu().get(i).getChild() != null) {

                for (int y = 0; y < shop.getMenu().get(i).getChild().size(); y++) {

                    Log.e("fucking_tree", "level 2 : "
                            + shop.getMenu().get(i).getChild().get(y).getCode()
                            + shop.getMenu().get(i).getChild().get(y).getLabel());

                    if (shop.getMenu().get(i).getChild().get(y).getChild() != null) {

                        for (int j = 0; j < shop.getMenu().get(i).getChild().get(y).getChild()
                                .size(); j++) {

                            Log.e("fucking_tree", "level 3 : "
                                    + shop.getMenu().get(i).getChild().get(y).getChild().get(j)
                                            .getCode()
                                    + shop.getMenu().get(i).getChild().get(y).getChild().get(j)
                                            .getLabel());

                            if (shop.getMenu().get(i).getChild().get(y).getChild().get(j)
                                    .getChild() != null) {

                                for (int k = 0; k < shop.getMenu().get(i).getChild().get(y)
                                        .getChild().get(j).getChild().size(); k++) {

                                    Log.e("fucking_tree", "level 4 : "
                                            + shop.getMenu().get(i).getChild().get(y).getChild()
                                                    .get(j).getChild().get(k).getCode()
                                            + shop.getMenu().get(i).getChild().get(y).getChild()
                                                    .get(j).getChild().get(k).getLabel());
                                }
                            }
                        }
                    }
                }
            }
        }

        return shop;
    }

    private ArrayList<MenuData> makeMenuData(JSONObject jsonObject, String parentId) {

        try {

            ArrayList<MenuData> dataList = new ArrayList<MenuData>();

            JSONArray array = null;

            if (jsonObject.has("posts")) {
                array = jsonObject.getJSONArray("posts");
            } else if (jsonObject.has("children"))
                array = jsonObject.getJSONArray("children");

            if (array != null) {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    MenuData data = new MenuData();

                    if (object.has("label"))
                        data.setLabel(object.getString("label"));

                    if (object.has("id"))
                        data.setId(object.getString("id"));

                    if (object.has("price"))
                        data.setPrice(object.getString("price"));

                    data.setCode(parentId, data.getId());

                    ArrayList<MenuData> childData = new ArrayList<MenuData>();

                    if (object.has("children")) {
                        childData = makeMenuData(object, data.getCode());
                    }

                    data.setChild(childData);

                    dataList.add(data);
                }
                return dataList;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class MenuImageTask extends AsyncTask<String, Void, JSONObject> implements
            View.OnClickListener {

        private MenuImageTask() {
            img1 = (ImageView)findViewById(R.id.img1);
            img2 = (ImageView)findViewById(R.id.img2);
            img3 = (ImageView)findViewById(R.id.img3);
            img4 = (ImageView)findViewById(R.id.img4);

            content1 = (TextView)findViewById(R.id.text1);
            content2 = (TextView)findViewById(R.id.text2);
            content3 = (TextView)findViewById(R.id.text3);
            content4 = (TextView)findViewById(R.id.text4);

            price1 = (TextView)findViewById(R.id.price1);
            price2 = (TextView)findViewById(R.id.price2);
            price3 = (TextView)findViewById(R.id.price3);
            price4 = (TextView)findViewById(R.id.price4);
        }

        private ImageView img1, img2, img3, img4;

        private TextView content1, content2, content3, content4, price1, price2, price3, price4;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            StringBuilder sb = new StringBuilder(
                    "http://cashq.co.kr/ajax/get_menu_img.php?store_code=");
            sb.append(params[0]);

            return new JSONParser().getJSONObjectFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            mMenuImgList = new ArrayList<HashMap<String, String>>(4);

            try {

                boolean hasImage = false;

                JSONArray jsonArray = jsonObject.getJSONArray("imglst");

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (i == 4)
                        break;

                    JSONObject object = jsonArray.getJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    if (object.has("wr_id"))
                        hashMap.put("id", object.getString("wr_id"));

                    if (object.has("bf_file"))
                        hashMap.put("img", object.getString("bf_file"));

                    if (object.has("bf_content"))
                        hashMap.put("text", object.getString("bf_content"));

                    hasImage = true;

                    mMenuImgList.add(hashMap);
                }

                if (hasImage) {
                    setMenuImage();
                    mMenuImgLayout.setVisibility(View.VISIBLE);
                } else {
                    mMenuImgLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        private void setMenuImage() {
            String baseUrl = "http://cashq.co.kr/adm/upload/thumb/";

            String[] img = new String[4];
            String[] content = new String[4];
            String[] price = new String[4];

            for (int i = 0; i < mMenuImgList.size(); i++) {
                img[i] = baseUrl + mMenuImgList.get(i).get("img");

                String[] spliter = mMenuImgList.get(i).get("text").split("_");
                content[i] = spliter[0];
                price[i] = String.format("%,d 원", Integer.parseInt(spliter[1]));

            }

            ImageLoader.getInstance().displayImage(img[0], img1);
            ImageLoader.getInstance().displayImage(img[1], img2);
            ImageLoader.getInstance().displayImage(img[2], img3);
            ImageLoader.getInstance().displayImage(img[3], img4);

            content1.setText(content[0]);
            content2.setText(content[1]);
            content3.setText(content[2]);
            content4.setText(content[3]);

            price1.setText(price[0]);
            price2.setText(price[1]);
            price3.setText(price[2]);
            price4.setText(price[3]);

            // if(true){
            if (false) {
                img1.setOnClickListener(this);
                img2.setOnClickListener(this);
                img3.setOnClickListener(this);
                img4.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            int num = 0;
            switch (v.getId()) {
                case R.id.img1:
                    num = 0;
                    break;
                case R.id.img2:
                    num = 1;
                    break;
                case R.id.img3:
                    num = 2;
                    break;
                case R.id.img4:
                    num = 3;
                    break;
            }
            imageMenuOrder(num);
        }

        private void imageMenuOrder(int num) {

            String imageId = mMenuImgList.get(num).get("id");

            ArrayList<MenuData> groupData = mData.getMenu();

            for (int i = 0; i < groupData.size(); i++) {

                ArrayList<MenuData> childData = groupData.get(i).getChild();

                for (int y = 0; y < childData.size(); y++) {

                    String level2Id = childData.get(y).getId();
                    Log.e("ShopPageActivity.imageMenuOrder", "level2Id : " + level2Id
                            + " imageId : " + imageId);

                    if (level2Id.equals(imageId)) {

                        // Toast.makeText(mThis, i + ", " + y,
                        // Toast.LENGTH_SHORT).show();

                        boolean hasLevel3 = !childData.get(y).getChild().isEmpty();

                        if (hasLevel3) {
                            OrderMenuDialog dialog = new OrderMenuDialog(mThis, mData, i, y,
                                    mOnDismissListener);
                            dialog.show();
                            dialog.setOnDismissListener(mOnDismissListener);
                        } else {
                            insertMenuLevel2(mThis, mData, i, y);
                        }

                    }
                }

            }
        }
    }

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            setCartCount(mThis);
        }
    };
}
