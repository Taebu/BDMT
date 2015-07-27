
package kr.co.cashqc;

/*
 * Copyright 2013 Csaba Szugyiczki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.UpdateCheckerResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.cashqc.gcm.Dictionary;
import kr.co.cashqc.gcm.HttpRequest;
import kr.co.cashqc.gcm.Timer.TimerListener;
import kr.co.cashqc.gcm.Util;
import kr.co.cashqc.view.CircleLayout;

import static kr.co.cashqc.CommonUtilities.SENDER_ID;
import static kr.co.cashqc.ShopListFragment.adminFlag;
import static kr.co.cashqc.gcm.Util.getPhoneNumber;

public class MainActivity extends BaseActivity implements CircleLayout.OnItemSelectedListener,
        CircleLayout.OnItemClickListener, CircleLayout.OnRotationFinishedListener,
        CircleLayout.OnCenterClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static String TOKEN_ID;

    public final String TAG = getClass().getSimpleName();

    private Toast toast = null;

    private BackPressCloseHandler backPressCloseHandler;

    private String mPhoneNum;

    private String mAddress;

    public MainActivity() {
        super();
        MainActivity.Instance = this;
    }

    private AsyncTask<Void, Void, Void> mRegisterTask;

    public static boolean introFlag = true;

    public static MainActivity Instance = null;

    private LocationUtil mLocationUtil;

    private Intent mIntent;

    private TextView mAddressText;

    private boolean mGpsFlag;

    private Handler mHandler = new Handler();

    private RelativeLayout mBackgroundLayout;

    private TextView mPointText;

    private Context mContext;

    private double mLatitude, mLongitude;

    private TextView mManualTextView, mManualDistance;

    public static int sDistance = 2;

    // google api location service

//    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private String mGu, mSi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNum = getPhoneNumber(this);

        findViewById(R.id.actionbar_gps_layout).setVisibility(View.GONE);
        findViewById(R.id.logo).setVisibility(View.VISIBLE);

//        buildGoogleApiClient();

        updateChecker();

        GoogleAnalytics.getInstance(this).dispatchLocalHits();

        Tracker t = ((CashqApplication)getApplication())
                .getTracker(CashqApplication.TrackerName.APP_TRACKER);

        t.setScreenName("MainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        LinearLayout ll = (LinearLayout)findViewById(R.id.gps_layout);
        if (Build.VERSION.SDK_INT > 10) {
            ll.setAlpha(0.5f);
        }

        mManualTextView = (TextView)findViewById(R.id.manual_location);

        mContext = getApplicationContext();

        // activity killer activity add.
        killer.addActivity(this);

        // back press close handler
        backPressCloseHandler = new BackPressCloseHandler(this);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        getRegId();

        setHttpRequest();

        new kr.co.cashqc.gcm.Timer(new TimerListener() {

            public void onTick() {
                setHttpRequest();
            }
        }, 3000);

        // custom mDialog init.
        mDialog = new CustomDialog(this);

        // main bg init.
        mBackgroundLayout = (RelativeLayout)findViewById(R.id.bg_main);

        // circle menu init.
        CircleLayout mCircleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);
        mCircleMenu.setOnItemSelectedListener(this);
        mCircleMenu.setOnItemClickListener(this);
        mCircleMenu.setOnRotationFinishedListener(this);
        mCircleMenu.setOnCenterClickListener(this);

        // address field init.
        mAddressText = (TextView)findViewById(R.id.location_name1);

        mManualDistance = (TextView)findViewById(R.id.manual_distance);

        // gps util init.
        mLocationUtil = LocationUtil.getInstance(MainActivity.this);

        mLatitude = getIntent().getDoubleExtra("lat", -1);
        mLongitude = getIntent().getDoubleExtra("lng", -1);

        if (!mGpsFlag && mLatitude == -1) {
            findLocation();
        } else {
            mGpsFlag = true;
            new AddressJsonTask(mAddressText).execute(mLatitude, mLongitude);
            // mAddressText.setText(mLocationUtil.getAddress(mLatitude,
            // mLongitude));
        }

        // if(adminFlag) {
        mManualTextView.setVisibility(View.VISIBLE);
        // } else {
        // mManualTextView.setVisibility(View.GONE);
        // }

        // gps btn set listener.
        findViewById(R.id.btn_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
                // mGoogleApiClient.connect();
            }
        });

        findViewById(R.id.admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog(0);
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).build();

        ImageLoader.getInstance().init(config);

        mIntent = new Intent(this, ShopListActivity.class);

        mPointText = (TextView)findViewById(R.id.display);

        // 적립금 파싱 타스크
        // new JSONParseTask().execute();

        // 네트워크 예외

    }

    private void updateChecker() {
        UpdateChecker checker = new UpdateChecker(this, new UpdateCheckerResult() {
            @Override
            public void foundUpdateAndShowIt(String s) {
                Log.e("UpdateChecker", "foundUpdateAndShowIt : " + s + " getVersionInstalled : "
                        + getVersionInstalled());
                new UpdateCheckerDialog(MainActivity.this).show();
                // com.rampo.updatechecker.notice.Dialog.show(MainActivity.this,
                // Store.GOOGLE_PLAY, s,
                // R.drawable.ic_launcher);
            }

            @Override
            public void foundUpdateAndDontShowIt(String s) {
                Log.e("UpdateChecker", "foundUpdateAndDontShowIt : " + s
                        + " getVersionInstalled : " + getVersionInstalled());
                new UpdateCheckerDialog(MainActivity.this).show();
                // com.rampo.updatechecker.notice.Dialog.show(MainActivity.this,
                // Store.GOOGLE_PLAY, s,
                // R.drawable.ic_launcher);
            }

            @Override
            public void returnUpToDate(String s) {
                Log.e("UpdateChecker", "returnUpToDate : " + s + " getVersionInstalled : "
                        + getVersionInstalled());
            }

            @Override
            public void returnMultipleApksPublished() {
                Log.e("UpdateChecker", "returnMultipleApksPublished" + " getVersionInstalled : "
                        + getVersionInstalled());
            }

            @Override
            public void returnNetworkError() {
                Log.e("UpdateChecker", "returnNetworkError" + " getVersionInstalled : "
                        + getVersionInstalled());
            }

            @Override
            public void returnAppUnpublished() {
                Log.e("UpdateChecker", "returnAppunpublished" + " getVersionInstalled : "
                        + getVersionInstalled());
            }

            @Override
            public void returnStoreError() {
                Log.e("UpdateChecker", "returnStoreError");
            }
        });
        checker.start();
    }

    private synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(LocationServices.API)
//                .addOnConnectionFailedListener(this).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well
        // suited for
        // applications that do not require a fine-grained location and that do
        // not need location
        // updates. Gets the best and most recent location currently available,
        // which may be null
        // in rare cases when a location is not available.
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.e(TAG, "Lat : " + latitude + " lng : " + longitude);
            // mAddressText.setText(mLocationUtil.getAddress(latitude,
            // longitude));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We
        // call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes
        // might be returned in
        // onConnectionFailed.
        Log.i(TAG,
                "Connection failed: ConnectionResult.getErrorCode() = "
                        + connectionResult.getErrorCode());
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // killer.allKillActivity();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onItemSelected(View view, String name) {

        if (toast == null) {
            toast = Toast.makeText(mContext, "음식사진 터치!~", Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, getDisplayHeightSize()/4);
        } else {
            // toast.setText("ddd");
        }

        toast.show();
        mPointText.setText("음식 사진을 클릭하세요");

        int drawable = 0;
        switch (view.getId()) {
            case R.id.wmain_chicken:
                mIntent.putExtra("TYPE", 1);
                drawable = R.drawable.bg_chicken;
                break;
            case R.id.wmain_pizza:
                mIntent.putExtra("TYPE", 2);
                drawable = R.drawable.bg_pizza;
                break;
            case R.id.wmain_chinese:
                mIntent.putExtra("TYPE", 3);
                drawable = R.drawable.bg_chinese;
                break;
            case R.id.wmain_korean:
                mIntent.putExtra("TYPE", 4);
                drawable = R.drawable.bg_korean;
                break;
            case R.id.wmain_dakbal:
                mIntent.putExtra("TYPE", 5);
                drawable = R.drawable.bg_dakbal;
                break;
            case R.id.wmain_night:
                mIntent.putExtra("TYPE", 6);
                drawable = R.drawable.bg_night;
                break;
            case R.id.wmain_bossam:
                mIntent.putExtra("TYPE", 7);
                drawable = R.drawable.bg_jokbal;
                break;
            case R.id.wmain_japanese:
                mIntent.putExtra("TYPE", 8);
                drawable = R.drawable.bg_japanese;
                break;
        }

        String uri = "drawable://" + drawable;
        ImageLoader.getInstance().loadImage(uri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Drawable topImage = new BitmapDrawable(loadedImage);
                mBackgroundLayout.setBackgroundDrawable(topImage);
            }
        });

    }

    @Override
    public void onItemClick(View view, String name) {
    }

    @Override
    public void onCenterClick() {

        if (mGpsFlag) {

            mIntent.putExtra("lat", mLatitude);
            mIntent.putExtra("lng", mLongitude);
            mIntent.putExtra("distance", sDistance);

            startActivity(mIntent);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
            // activityAnimation(true);
        } else {
            Toast.makeText(mContext, "GPS위치를 잡아주세요", Toast.LENGTH_LONG).show();
        }
    }

    public String getVersionInstalled() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return null;
    }

    @Override
    public void onRotationFinished(View view, String name) {
        // Animation animation = new RotateAnimation(0, 360,
        // mBackgroundLayout.getWidth() / 2,
        // mBackgroundLayout.getHeight() / 2);
        // animation.setDuration(250);
        // view.startAnimation(animation);
    }

    class AddressJsonTask extends AsyncTask<Double, Void, JSONObject> {

        public AddressJsonTask(TextView textView) {
            tvAddress = textView;
        }

        private TextView tvAddress;

        private final String TAG = getClass().getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Double... params) {

            double lat = params[0];
            double lng = params[1];

            String url = Uri.parse("http://maps.googleapis.com/maps/api/geocode/json").buildUpon()
                    .appendQueryParameter("language", "ko")
                    .appendQueryParameter("latlng", lat + "," + lng)
                    .appendQueryParameter("sensor", "false").build().toString();

            Log.e(TAG, "URL : " + url);

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            String json = jsonObject.toString();

            try {
                json = new String(json.getBytes("8859_1"), "UTF-8");

                mAddress = getAddress(json);

                tvAddress.setText(mAddress);

                if ("동두천시".equals(mSi) || "안산시".equals(mSi) || "강서구".equals(mGu)
                        || "양천구".equals(mGu)) {
                    sDistance = 3;
                } else {
                    sDistance = 2;
                }

                mManualDistance.setText(sDistance + "km\n변경");

                Log.e(TAG, "address : " + mAddress);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }

        }

        private String getAddress(String json) {
            try {

                JSONObject jsonObject = new JSONObject(json);

                JSONArray resultsArray = jsonObject.getJSONArray("results");

                String sublocalityLevel1 = "";
                String sublocalityLevel2 = "";

                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject resultsObject = resultsArray.getJSONObject(i);

                    JSONArray addressComponentsArray = resultsObject
                            .getJSONArray("address_components");

                    String resultsTypes = resultsObject.getString("types");

                    if (resultsTypes.contains("postal_code")) {
                        for (int y = 0; y < addressComponentsArray.length(); y++) {
                            JSONObject addressComponentsObject = addressComponentsArray
                                    .getJSONObject(y);

                            String types = addressComponentsObject.getString("types");

                            if (types.contains("locality")) {
                                mSi = addressComponentsObject.getString("long_name");
                            }

                            if (types.contains("sublocality_level_1")) {
                                sublocalityLevel1 = addressComponentsObject.getString("long_name");
                                mGu = addressComponentsObject.getString("long_name");
                            }

                            if (types.contains("sublocality_level_2")) {
                                sublocalityLevel2 = addressComponentsObject.getString("long_name");
                            }
                        }
                        break;
                    }
                }
                return sublocalityLevel1 + " " + sublocalityLevel2;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        private String getAddress1(String json) {
            try {

                JSONObject jsonObject = new JSONObject(json);

                JSONArray resultsArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject resultsObject = resultsArray.getJSONObject(i);

                    JSONArray addressComponentsArray = resultsObject
                            .getJSONArray("address_components");

                    String resultsTypes = resultsObject.getString("types");

                    if (resultsTypes.contains("postal_code")) {

                        for (int y = 0; y < addressComponentsArray.length(); y++) {

                            JSONObject addressComponentsObject = addressComponentsArray
                                    .getJSONObject(y);

                            JSONArray addressComponentsTypesArray = addressComponentsObject
                                    .getJSONArray("types");

                            for (int j = 0; j < addressComponentsTypesArray.length(); j++) {

                                String types = addressComponentsTypesArray.getString(j);

                                if (types.contains("sublocality_level_2")) {

                                    // String resultsTypes =
                                    // resultsObject.getString("types");

                                    Log.e(TAG, "addressComponentsArray : " + i + y
                                            + addressComponentsArray.get(y));

                                    Log.e(TAG, "resultsTypes : " + resultsTypes);

                                    if (resultsTypes.contains("postal_code")) {

                                        // Log.e(TAG, "resultsTypes : " +
                                        // resultsTypes);

                                        // String sublocalityLevel2 =
                                        // addressComponentsObject.getString("");
                                        // Log.e(TAG, "sublocalityLevel2 : " + i
                                        // + y
                                        // + sublocalityLevel2);

                                    }
                                }
                            }
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void findLocation() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        mLocationUtil.start();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {

                        try {

                            mLatitude = mLocationUtil.getLastLocation().getLatitude();
                            mLongitude = mLocationUtil.getLastLocation().getLongitude();

                            new AddressJsonTask(mAddressText).execute(mLatitude, mLongitude);

                            String address;
                            // address = mLocationUtil.getAddress(mLatitude,
                            // mLongitude);

                            mAddressText.setVisibility(View.VISIBLE);
                            // mAddressText.setText(address);

                            mGpsFlag = true;

                        } catch (NullPointerException e) {
                            Log.d("JAY", "gps exception");
                            e.printStackTrace();

                            mAddressText.setText("<- 위치설정 버튼을 눌러 위치를 탐색해 주세요.");

                            mDialog.dismiss();

                            mGpsFlag = false;
                        }
                    }
                });
            }
        };

        if (mLocationUtil.isRunLocationUtil) {
            mLocationUtil.stop();
        }

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    @Override
    protected void onRestart() {
        setCartCount(this);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        setCartCount(this);
        super.onResume();
        // Check device for Play Services APK.
    }

    @Override
    protected void onStop() {
        super.onStop();

        GoogleAnalytics.getInstance(this).reportActivityStop(this);

//        if (mGoogleApiClient.isConnected())
//            mGoogleApiClient.disconnect();

        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        introFlag = true;

        if (!(Util.loadSharedPreferencesBoolean(this, "auto_login"))) {
            Util.saveSharedPreferences_boolean(this, "login", false);
        }

        // 레이지이미지로더 캐시 정리
        new kr.co.cashqc.lazylist.ImageLoader(mContext).clearCache();
    }

    public void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.manual_location:
                Intent i = new Intent(this, MapActivity.class);
                i.putExtra("lat", mLatitude);
                i.putExtra("lng", mLongitude);
                // i.addFlags()

                startActivity(i);
                break;
            case R.id.manual_distance:
                ManualDistanceDialog distanceDialog = new ManualDistanceDialog(this);
                distanceDialog.show();
                distanceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mManualDistance.setText(sDistance + "km\n변경");
                    }
                });
                break;
        }

    }

    private class LoadSavingTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 적립금 텍스트뷰
            // mPointText = (TextView)findViewById(R.id.point_value);

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JsonParser parser = new JsonParser();

            String pointURL = "http://cashq.co.kr/m/ajax_data/get_point.php?phone=" + mPhoneNum;

            return parser.getJSONObjectFromUrl(pointURL);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {

                mPointText.setText(jsonObject.getString("point"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRegId() {
        final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
        Log.e("JAY", "regid = " + regId);
        if (regId.equals("")) {
            GCMRegistrar.register(this, SENDER_ID);
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Toast.makeText(getApplicationContext(), "Already",
                // Toast.LENGTH_LONG).show();
            } else {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    protected Void doInBackground(Void... params) {
                        ServerUtilities
                                .register(context, "central", getPhoneNumber(context), regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
            Log.v("JAY", "Already registered");
        }
        return regId;
    }

    public void setHttpRequest() {
        try {
            String num = mPhoneNum;
            String register = Util.loadSharedPreferences(getApplicationContext(),
                    Utils.RegisterKey222);
            Log.e("JAY", "loadshared = " + register);
            if (register != null) {
                // if (TTS_MODE) {
                String url = "http://cashq.co.kr/m/set_tokenid_add.php" + "?biz_code=central"
                        + "&phone=" + num + "&token_id=" + getRegId();

                TOKEN_ID = register;

                Log.e("test", "register  :  " + register);
                Log.e("test", "url  :  " + url);

                new HttpRequest(mHttpRequestListener, url, "list");
            } else {
                new kr.co.cashqc.gcm.Timer(new TimerListener() {

                    public void onTick() {
                        setHttpRequest();
                    }
                }, 3000);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private HttpRequest.HttpRequestListener mHttpRequestListener = new HttpRequest.HttpRequestListener() {

        public void httpRequestError() {
            httpRequestErrorMsg();
        }

        public void getRequestData(ArrayList<Dictionary> dicArray, boolean isError) {

            if (dicArray.size() > 0) {
            }
        }
    };

    /**
     * 컨텐츠 요청 에러
     */
    private void httpRequestErrorMsg() {
        if (!isFinishing()) {
            // Util.showDialog(this, "알림", "인터넷 환경이 불안정 합니다. 네트워크 상태를 확인해 주세요.",
            // null);
        }
    }

    @Override
    protected Dialog onCreateDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_custom_login, null);

        builder.setView(view);
        builder.setPositiveButton("login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText passwordEditText = (EditText)view.findViewById(R.id.password);
                // EditText distanceEditText = (EditText)
                // view.findViewById(R.id.distance);

                if (passwordEditText.getText().toString().equals("1599")) {
                    adminFlag = true;
                    Toast.makeText(mContext, "관리자모드", Toast.LENGTH_SHORT).show();
                    // try {
                    // sDistance =
                    // Integer.parseInt(distanceEditText.getText().toString());
                    // } catch (NumberFormatException e) {
                    // e.printStackTrace();
                    // sDistance = 3;
                    // }
                    mManualTextView.setVisibility(View.VISIBLE);
                } else {
                    adminFlag = false;
                    sDistance = 3;
                    // mManualTextView.setVisibility(View.GONE);
                }

            }
        });

        return builder.create();
    }

}
