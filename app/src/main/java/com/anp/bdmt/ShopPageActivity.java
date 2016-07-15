
package com.anp.bdmt;

import static com.anp.bdmt.CameraUtil.TAKE_ALBUM;
import static com.anp.bdmt.CameraUtil.TAKE_CAMERA;
import static com.anp.bdmt.MainActivity.TOKEN_ID;
import static com.anp.bdmt.Utils.IMG_URL;
import static com.anp.bdmt.Utils.initExpandableListViewHeight;
import static com.anp.bdmt.Utils.insertMenuLevel2;
import static com.anp.bdmt.Utils.setExpandableListViewHeight;
import static com.anp.bdmt.Utils.setListViewHeightBasedOnChildren;
import static com.anp.bdmt.gcm.Util.getPhoneNumber;

import com.anp.bdmt.gcm.Util;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jung-Hum Cho Created by Administrator on 2014-10-16.
 */
public class ShopPageActivity extends BaseActivity {

    private boolean mIsOpen;

    private final String TAG = this.getClass().getSimpleName();

    private ShopPageActivity mActivity = this;

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

    // private Intent mZoomIntent;

    private boolean hasImage = true;

    public static boolean TTS_SHOP = false;

    private ImageView testtt;

    private CameraUtil mCameraUtil;

    private LinearLayout mPaynowLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppage);

        // mActivity killer mActivity add.
        killer.addActivity(this);

        Tracker t = ((CashqApplication)getApplication())
                .getTracker(CashqApplication.TrackerName.APP_TRACKER);

        t.setScreenName("ShopPageActivity(BDMT)");
        t.send(new HitBuilders.AppViewBuilder().build());

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        // activityAnimation(true);

        mDialog = new CustomDialog(this);

        testtt = (ImageView)findViewById(R.id.testtt);

        testtt.setVisibility(View.GONE);

        setRowLayout();
        setLayout();

        float reviewRating = Float.parseFloat(getIntent().getStringExtra("review_rating"));

        final String reviewCount = getIntent().getStringExtra("review_cnt");

        tvReviewCount.setText(reviewCount + "개 리뷰 보기");

        tvRatingScore.setText(String.valueOf(reviewRating));

        mRatingBar.setRating(reviewRating);

        mRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String phoneNum = getPhoneNumber(mActivity);

                if (phoneNum.isEmpty()) {
                    phoneNum = "4444444444";
                }

                ReviewDialog reviewDialog = new ReviewDialog(mActivity, getIntent().getStringExtra(
                        "name"), getIntent().getStringExtra("seq"), phoneNum, mPhotoListener);
                reviewDialog.show();
                reviewDialog.setOnDismissListener(mReviewOnDismissListener);
                // stopService(mZoomIntent);
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

        mPrePay = getIntent().getStringExtra("pre_pay");
        Log.e("ShopPageActivity", "prepay : " + mPrePay);

        LinearLayout paynowLayout = (LinearLayout)findViewById(R.id.paynow_comment);
        ImageView paynowRibbon = (ImageView)findViewById(R.id.paynow_ribbon);

        if ("gl".equals(mPrePay) || "sl".equals(mPrePay)) {

            paynowLayout.setVisibility(View.VISIBLE);
            paynowRibbon.setVisibility(View.VISIBLE);

        } else if ("on".equals(mPrePay)) {

            paynowLayout.setVisibility(View.GONE);
            paynowRibbon.setVisibility(View.GONE);

        } else if ("pr".equals(mPrePay) || "".equals(mPrePay)) {
            paynowLayout.setVisibility(View.GONE);
            paynowRibbon.setVisibility(View.GONE);
        }

        // TTS_SHOP = mPrePay.equals("gl");

        boolean hasList = "1".equals(getIntent().getStringExtra("pay"));

        if (hasList) {
            setListView();

            if (!hasImage) {
                isWeb = false;
            }

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

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Uri createSaveCropFile() {
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        return uri;

    }

    private File getImageFile(Uri imgUri) {
        String[] projection = {
            MediaStore.Images.Media.DATA
        };
        if (imgUri == null) {
            imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(imgUri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    private String getImageNameFromUri(Uri data) {

        String[] proj = {
            MediaStore.Images.Media.DATA
        };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(columnIndex);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;

    }

    private String getImageNameToUri(Uri data) {
        String[] prj = {
            MediaStore.Images.Media.DATA
        };
        Cursor cursor = managedQuery(data, prj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(columnIndex);

        return imgPath.substring(imgPath.lastIndexOf("/") + 1);
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

        mPaynowLayout = (LinearLayout)findViewById(R.id.btn_shoppage_paynow);

        mPaynowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("num", getIntent().getStringExtra("tel"));
                args.putString("pre_pay", getIntent().getStringExtra("pre_pay"));
                args.putString("pay", getIntent().getStringExtra("pay"));
                args.putString("img1", getIntent().getStringExtra("img1"));
                args.putString("img2", getIntent().getStringExtra("img2"));
                args.putBoolean("has_call", true);

                PaynowDialog paynowDialog = new PaynowDialog();
                paynowDialog.setArguments(args);

                paynowDialog.show(getSupportFragmentManager(), "paynow_dialog");
            }
        });

        findViewById(R.id.btn_shoppage_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

    }

    private void setReviewView(boolean isExpanded) {

        if (isExpanded) {
            new ReviewTask().execute(getIntent().getStringExtra("seq"));
            mReviewListView.setVisibility(View.VISIBLE);
            tvReviewCount
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_list_open, 0);

        } else {
            mReviewListView.setVisibility(View.GONE);
            tvReviewCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_list_close,
                    0);
        }

        setListViewHeightBasedOnChildren(mReviewListView);

    }

    private class ReviewTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String seq = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/get_review.php?").buildUpon()
                    .appendQueryParameter("seq", seq).toString();

            return new JsonParser().getJSONObjectFromUrl(url);
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

                    if (object.has("seq")) {
                        reviewData.setSeq(object.getString("seq"));
                    }

                    if (object.has("mb_hp")) {
                        reviewData.setPhone(object.getString("mb_hp"));
                    }

                    if (object.has("mb_nick")) {
                        reviewData.setNick(object.getString("mb_nick"));
                    }

                    if (object.has("content")) {
                        reviewData.setContent(object.getString("content").trim());
                    }

                    if (object.has("rating")) {
                        reviewData.setRating(Integer.parseInt(object.getString("rating")));
                    }

                    if (object.has("insdate")) {
                        reviewData.setInsdate(object.getString("insdate"));
                    }

                    if (object.has("token_id")) {
                        reviewData.setTokenId(object.getString("token_id"));
                    }

                    reviewDataList.add(reviewData);
                }

                ReviewListAdapter adapter = new ReviewListAdapter(mActivity, reviewDataList,
                        mOnClickListener);
                mReviewListView.setAdapter(adapter);

                setListViewHeightBasedOnChildren(mReviewListView);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String seq = v.getTag(R.id.seq).toString();
            switch (v.getId()) {
                case R.id.row_review_modify:
                    ReviewDialog reviewDialog = new ReviewDialog(mActivity, getIntent()
                            .getStringExtra("name"), seq);
                    reviewDialog.show();
                    reviewDialog.setOnDismissListener(mReviewOnDismissListener);
                    // stopService(mZoomIntent);
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
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String seq = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/set_review.php?mode=del")
                    .buildUpon().appendQueryParameter("seq", seq)
                    .appendQueryParameter("token_id", TOKEN_ID).toString();

            Log.e(TAG, "remove url : " + url);
            return new JsonParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, s);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        Log.e("life", "onResume");
        // setCartCount(this);
        super.onResume();
        if (isWeb) {
            // startService(mZoomIntent);
        }
    }

    @Override
    protected void onStop() {
        // setCartCount(this);
        Log.e("life", "onStop");
        super.onStop();

        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        // stopService(mZoomIntent);
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

            if (!mDialog.isShowing()) {
                mDialog.show();
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

    private void setRowLayout() {

        TextView nameTextView = (TextView)findViewById(R.id.cashq_list_name);
        // nameTextView.setVisibility(View.INVISIBLE);
        nameTextView.setText(getIntent().getStringExtra("name"));

        TextView dongTextView = (TextView)findViewById(R.id.dong);
        dongTextView.setText(getIntent().getStringExtra("delivery_comment_cashq"));
        dongTextView.setSingleLine(true);
        dongTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        dongTextView.setSelected(true);

        TextView callcntTextView = (TextView)findViewById(R.id.calllog);
        callcntTextView.setVisibility(View.INVISIBLE);
        callcntTextView.setText(getIntent().getStringExtra("callcntTextView") + " 건 주문");
        // String bizCode = getIntent().getStringExtra("biz_code");
        //
        // boolean invisibleCallCnt = bizCode.equals("a061");
        //
        // if (invisibleCallCnt) {
        // callcntTextView.setVisibility(View.INVISIBLE);
        // } else {
        // callcntTextView.setVisibility(View.VISIBLE);
        // }

        RatingBar ratingBar = (RatingBar)findViewById(R.id.shoplist_rating);
        ratingBar.setRating(Float.parseFloat(getIntent().getStringExtra("review_rating")));

        TextView topNameTextView = (TextView)findViewById(R.id.shoppage_name);
        topNameTextView.setText(getIntent().getStringExtra("name"));

        String prePay = getIntent().getStringExtra("pre_pay");

        TextView pointAmountTextView = (TextView)findViewById(R.id.point_amount);
        if ("on".equals(prePay)) {
            pointAmountTextView.setText("1,000 Point");
        }

        String minPay = getIntent().getStringExtra("minpay") + " 이상 주문 시 적립 인정";

        LinearLayout pointInfo = (LinearLayout)findViewById(R.id.point_info);

        if ("".equals(prePay) || "pr".equals(prePay)) {
            minPay = "포인트 적립 불가";
            pointInfo.setVisibility(View.INVISIBLE);
        }

        TextView minPayTextView = (TextView)findViewById(R.id.min_pay);
        minPayTextView.setText(minPay);

        ImageView thumbnailImageView = (ImageView)findViewById(R.id.list_thm);
        mIsOpen = getIntent().getBooleanExtra("isopen", true);
        if (mIsOpen) {

            final Integer noImgResources[] = new Integer[] {
                    R.drawable.no_img_chicken, R.drawable.no_img_pizza, R.drawable.no_img_chinese,
                    R.drawable.no_img_jokbal, R.drawable.no_img_night, R.drawable.no_img_soup,
                    R.drawable.no_img_korean, R.drawable.no_img_japanese,
                    R.drawable.no_img_boxlunch, R.drawable.no_img_fastfood
            };

            int position = noImgResources[getIntent().getIntExtra("position", 0)];

            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();

            builder.displayer(new RoundedBitmapDisplayer(200));
            builder.cacheInMemory(true);
            builder.cacheOnDisk(true);
            builder.showImageOnLoading(R.drawable.load_anim);
            builder.showImageForEmptyUri(position);
            builder.showImageOnFail(position);

            DisplayImageOptions options = builder.build();

            String thumbUri = IMG_URL + getIntent().getStringExtra("thumb");

            ImageLoader.getInstance().displayImage(thumbUri, thumbnailImageView, options);

        } else {

            thumbnailImageView.setImageResource(R.drawable.nottime);

        }

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

        Intent menu = new Intent(new Intent(mActivity, CallService.class));

        menu.putExtra("pre_pay", getIntent().getStringExtra("pre_pay"));
        menu.putExtra("pay", getIntent().getStringExtra("pay"));
        menu.putExtra("img1", getIntent().getStringExtra("img1"));
        menu.putExtra("img2", getIntent().getStringExtra("img2"));

        startService(menu);

        // mNum = "tel:" + getIntent().getStringExtra("tel");
        // startActivity(new Intent(Intent.ACTION_CALL,
        // Uri.parse("tel:010-3745-2742")));
        // PhoneCall.call(mNum, mActivity);
    }

    private void setWebView() {

        // mZoomIntent = new Intent(new Intent(mActivity, ZoomService.class));
        //
        // mZoomIntent.putExtra("img1", getIntent().getStringExtra("img1"));
        // mZoomIntent.putExtra("img2", getIntent().getStringExtra("img2"));
        //
        // startService(mZoomIntent);

        findViewById(R.id.btn_shoppage_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, MenuZoomActivity.class);

                intent.putExtra("img1", getIntent().getStringExtra("img1"));
                intent.putExtra("img2", getIntent().getStringExtra("img2"));
                intent.putExtra("tel", getIntent().getStringExtra("tel"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });

        // mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new WebViewClientClass());
        WebSettings set = mWebView.getSettings();

        // TODO 버전별 처리 할것

        // if (Build.VERSION.SDK_INT > 10) {
        // set.setDisplayZoomControls(false);
        // } else {
        // ZoomButtonsController zoomButtonsController;
        // try {
        // zoomButtonsController = (ZoomButtonsController)mWebView.getClass()
        // .getMethod("getZoomButtonsController").invoke(mWebView, null);
        // zoomButtonsController.getContainer().setVisibility(View.GONE);
        // } catch (IllegalAccessException e) {
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // e.printStackTrace();
        // } catch (NoSuchMethodException e) {
        // e.printStackTrace();
        // }
        // }

        // set.setLoadWithOverviewMode(true);
        // set.setUseWideViewPort(true);
        // set.setJavaScriptEnabled(true);

        // 내장 줌 사용여부
        // set.setBuiltInZoomControls(true);
        set.setBuiltInZoomControls(false);

        // 내장 줌 사용시 줌 컨트롤 표시 여부
        // set.setDisplayZoomControls(true);
        // set.setDisplayZoomControls(false);

        // 줌 컨트롤과 제스처를 지원할지 여부
        // set.setSupportZoom(true);
        set.setSupportZoom(false);

        set.setCacheMode(WebSettings.LOAD_NO_CACHE);

        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String url = getHtml(getIntent().getStringExtra("img1"), getIntent().getStringExtra("img2"));

        mWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
    }

    private String getHtml(String img1, String img2) {

        final int NOTHING = 0;
        final int HAS_IMG1 = 1;
        final int HAS_IMG2 = 2;
        final int HAS_ALL = 3;

        int imgFlag = NOTHING;

        if (!("".equals(img1) || "null".equals(img1) || img1.isEmpty())) {

            img1 = IMG_URL + img1;

            imgFlag = HAS_IMG1;
        }

        if (!("".equals(img2) || "null".equals(img2) || img2.isEmpty())) {

            img2 = IMG_URL + img2;

            if (imgFlag == HAS_IMG1) {
                imgFlag = HAS_ALL;
            } else {
                imgFlag = HAS_IMG2;
            }
        }

        Log.e("JAY", "params url : " + img1 + "\n" + img2);

        StringBuilder sb = new StringBuilder("<HTML>");
        sb.append("<HEAD>").append("</HEAD>").append("<BODY>");

        switch (imgFlag) {

            case NOTHING:
                sb.append("<p style='text-align:center; margin-top:250px'>");
                sb.append("<img src='http://cashq.co.kr/m/img/img_no_image.png'></p>");
                hasImage = false;
                break;

            case HAS_IMG1:
                sb.append("<img width='100%' src='").append(img1).append("'>");
                break;

            case HAS_IMG2:
                sb.append("<img width='100%' src='").append(img2).append("'>");
                break;

            case HAS_ALL:
                sb.append("<img width='100%' src='").append(img1).append("'>");
                sb.append("<img width='100%' src='").append(img2).append("'>");
                break;
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

        if (isWebView) {
            mListMenu.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            switcher.setText("메뉴 보기");
            // startService(mZoomIntent);
        } else {
            mListMenu.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
            switcher.setText("전단지 보기");
            // stopService(mZoomIntent);
        }

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
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String url = "http://cashq.co.kr/ajax/get_menu.php?store_code=" + params[0];

            Log.e(TAG, "GET MENU URL: " + url);

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {

                Log.e("ShopMenuActivity", object.toString());

                mData = makeShopMenuData(object);

                ShopMenuAdapter adapter = new ShopMenuAdapter(mActivity, mData, mOnDismissListener,
                        mActivity);

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

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

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

                    if (object.has("price")) {
                        if (object.getString("price").equals("")) {
                            data.setPrice(0);
                        } else {
                            data.setPrice(Integer.parseInt(object.getString("price")));
                        }
                    }

                    data.setIsDeal(false);

                    if (object.has("is_deal")) {

                        if (object.getBoolean("is_deal")) {

                            data.setIsDeal(true);

                            if (object.has("dis_price"))
                                data.setDiscountPrice(object.getInt("dis_price"));

                            if (object.has("discount"))
                                data.setDiscountRate(object.getInt("discount"));

                            if (object.has("quantity"))
                                data.setQuantity(object.getInt("quantity"));
                        }
                    }

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

    private ArrayList<MenuData> makeMenuData1(JSONObject jsonObject, String parentId) {

        try {

            ArrayList<MenuData> dataList = new ArrayList<MenuData>();

            JSONArray array = null;

            if (jsonObject.has("posts")) {
                array = jsonObject.getJSONArray("posts");
            } else if (jsonObject.has("children")) {
                array = jsonObject.getJSONArray("children");
            }

            if (array != null) {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    MenuData data = new MenuData();

                    if (object.has("label")) {
                        data.setLabel(object.getString("label"));
                    }

                    if (object.has("id")) {
                        data.setId(object.getString("id"));
                    }

                    if (object.has("price")) {
                        data.setPrice(Integer.valueOf(object.getString("price")));
                    }

                    data.setIsDeal(false);

                    if (object.has("is_deal") && object.getBoolean("is_deal")) {

                        data.setIsDeal(true);

                        if (object.has("dis_price")) {
                            data.setDiscountPrice(object.getInt("dis_price"));
                        }

                        if (object.has("discount")) {
                            data.setDiscountRate(object.getInt("discount"));
                        }

                        if (object.has("quantity")) {
                            data.setQuantity(object.getInt("quantity"));
                        }
                    }

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
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            StringBuilder sb = new StringBuilder(
                    "http://cashq.co.kr/ajax/get_menu_img.php?store_code=");
            sb.append(params[0]);

            return new JsonParser().getJSONObjectFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            mMenuImgList = new ArrayList<HashMap<String, String>>(4);

            try {

                boolean hasImage = false;

                JSONArray jsonArray = jsonObject.getJSONArray("imglst");

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (i == 4) {
                        break;
                    }

                    JSONObject object = jsonArray.getJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    if (object.has("wr_id")) {
                        hashMap.put("id", object.getString("wr_id"));
                    }

                    if (object.has("bf_file")) {
                        hashMap.put("img", object.getString("bf_file"));
                    }

                    if (object.has("bf_content")) {
                        hashMap.put("text", object.getString("bf_content"));
                    }

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

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
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

            if (TTS_SHOP) {
                // if (false) {
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
                    Log.e("imageMenuOrder", "level2Id : " + level2Id + " imageId : " + imageId);

                    if (level2Id.equals(imageId)) {

                        // Toast.makeText(mActivity, i + ", " + y,
                        // Toast.LENGTH_SHORT).show();

                        boolean hasLevel3 = !childData.get(y).getChild().isEmpty();

                        if (hasLevel3) {
                            OrderMenuDialog dialog = new OrderMenuDialog(mActivity, mData, i, y,
                                    mOnDismissListener);
                            dialog.show();
                            dialog.setOnDismissListener(mOnDismissListener);
                        } else {
                            insertMenuLevel2(mActivity, mData, i, y);
                            setCartCount(mActivity);
                        }

                    }
                }

            }
        }
    }

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            setCartCount(mActivity);
        }
    };

    private DialogInterface.OnDismissListener mReviewOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            // startService(mZoomIntent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stopService(mZoomIntent);
    }

    private View.OnClickListener mPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mCameraUtil = new CameraUtil(mActivity);

            mCameraUtil.takeCamera();
            // setPhotoListener();

            // switch (v.getId()) {
            // case R.id.dialog_review_img1:
            // break;
            // case R.id.dialog_review_img2:
            // break;
            // case R.id.dialog_review_img3:
            // break;
            // case R.id.dialog_review_img4:
            // break;
            // }
        }
    };

    private void setPhotoListener() {

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCameraUtil.takeCamera();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCameraUtil.takeAlbum();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(mActivity).setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener).setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(mActivity, "Result Code : " + resultCode, Toast.LENGTH_SHORT).show();

        Log.e(TAG, "resultCode : " + resultCode);

        if (resultCode != RESULT_OK) {
            return;
        }

        // take camera

        switch (requestCode) {
            case TAKE_CAMERA:
                mCameraUtil.takePhoto(data, testtt);
                break;

            case TAKE_ALBUM:
                break;
        }
    }

}
