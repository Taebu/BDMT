
package com.anp.bdmt;

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

import static com.anp.bdmt.ShopListFragment.adminFlag;
import static com.anp.bdmt.gcm.Util.getPhoneNumber;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anp.bdmt.gcm.Util;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.UpdateCheckerResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static String TOKEN_ID;

    public final String TAG = getClass().getSimpleName();

    private Toast toast = null;

    private com.anp.bdmt.BackPressCloseHandler backPressCloseHandler;

    private String mPhoneNum;

    private String mAddress;

    public static boolean SALE_ZONE = false;

    public static boolean LIFE_ZONE = false;

    public static MainActivity Instance = null;

    private int mPosition = 0;

    public static final String APP_ID = "bdmt";

    public MainActivity() {
        super();
        MainActivity.Instance = this;
    }

    private AsyncTask<Void, Void, Void> mRegisterTask;

    public static boolean introFlag = true;

    private LocationUtil mLocationUtil;

    private Intent mIntent;

    private TextView mAddressText;

    private boolean mGpsFlag;

    private Handler mHandler = new Handler();

    private RelativeLayout mBackgroundLayout;

    private TextView mPointText;

    private Context mContext;

    public static double sLatitude;

    public static double sLongitude;

    // 170131 2km로 변경
    public static int sDistance = 2;

    // private ImageView mManualTextView;
    private TextView mManualDistance;

    // google api location service

    // private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private String mGu, mSi;

    private ImageView btnSale;

    private ImageView mLifeImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle args = new Bundle();
        args.putBoolean("has_call", false);

        PaynowDialog paynowDialog = new PaynowDialog();
        paynowDialog.setArguments(args);

        // 7/22 popup 안보이게
        // paynowDialog.show(getSupportFragmentManager(), "paynow_dialog");

        // mPhoneNum = getPhoneNumber(this);

        // mLifeImageView = (ImageView)findViewById(R.id.main_life);

        findViewById(R.id.actionbar_gps_layout).setVisibility(View.GONE);
        findViewById(R.id.logo).setVisibility(View.VISIBLE);

        // buildGoogleApiClient();

        // updateChecker();

        UpdateChecker updateChecker = new UpdateChecker(this);
        updateChecker.setSuccessfulChecksRequired(1);
        updateChecker.start();

        registBroadcastReceiver();

        getInstanceIdToken();

        GoogleAnalytics.getInstance(this).dispatchLocalHits();

        Tracker t = ((CashqApplication)getApplication())
                .getTracker(CashqApplication.TrackerName.APP_TRACKER);

        t.setScreenName("MainActivity(BDMT)");
        t.send(new HitBuilders.AppViewBuilder().build());

        LinearLayout ll = (LinearLayout)findViewById(R.id.gps_layout);
        if (Build.VERSION.SDK_INT > 10) {
            // ll.setAlpha(0.5f);
        }

        // mManualTextView = (ImageView)findViewById(R.id.manual_location);

        // btnSale = (ImageView)findViewById(R.id.main_sale);

        mContext = getApplicationContext();

        // activity killer activity add.
        killer.addActivity(this);

        // back press close handler
        backPressCloseHandler = new BackPressCloseHandler(this);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        // getRegId();

        // setHttpRequest();

        // new com.anp.bdmt.gcm.Timer(new TimerListener() {
        //
        // public void onTick() {
        // setHttpRequest();
        // }
        // }, 3000);

        // custom mDialog init.
        mDialog = new CustomDialog(this);

        // main bg init.
        // mBackgroundLayout = (RelativeLayout)findViewById(R.id.bg_main);

        // circle menu init.
        // CircleLayout mCircleMenu =
        // (CircleLayout)findViewById(R.id.main_circle_layout);
        // mCircleMenu.setOnItemSelectedListener(this);
        // mCircleMenu.setOnItemClickListener(this);
        // mCircleMenu.setOnRotationFinishedListener(this);
        // mCircleMenu.setOnCenterClickListener(this);

        // address field init.
        mAddressText = (TextView)findViewById(R.id.location_name1);

        mManualDistance = (TextView)findViewById(R.id.manual_distance);

        // gps util init.
        mLocationUtil = LocationUtil.getInstance(MainActivity.this);

        mGpsFlag = getIntent().getBooleanExtra("gpsflag", false);

        mAddressText.setText("lat: " + String.valueOf(sLatitude) + "lon: "
                + String.valueOf(sLongitude));

        if (!mGpsFlag) {
            findLocation();
        } else {
            mGpsFlag = true;
            Log.e(TAG, "lat: " + String.valueOf(sLatitude) + "lon: " + String.valueOf(sLongitude));
            new AddressJsonTask(mAddressText).execute();
            // mAddressText.setText(mLocationUtil.getAddress(sLatitude,
            // sLongitude));
        }

        // if(adminFlag) {
        // mManualTextView.setVisibility(View.VISIBLE);
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
                // showDialog(0);
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).build();

        ImageLoader.getInstance().init(config);

        mIntent = new Intent(this, ShopListActivity.class);

        // mPointText = (TextView)findViewById(R.id.display);

        // 적립금 파싱 타스크
        // new JSONParseTask().execute();

        // 네트워크 예외

        // initViewPager();

        findViewById(R.id.left_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUrl("http://anpr.cafe24.com/?page_mode=sub&depth_1=6&depth_2=2");
            }
        });

        findViewById(R.id.right_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUrl("http://anpr.cafe24.com/?page_mode=sub&depth_1=6&depth_2=2");
            }
        });

    }

//    private void initViewPager() {
//
//        ViewPager viewPager = (ViewPager)findViewById(R.id.main_viewpager);
//
//        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//
//            @Override
//            public int getCount() {
//                return 2;
//            }
//
//            @Override
//            public Fragment getItem(final int position) {
//                return new Fragment() {
//                    @Override
//                    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                            Bundle savedInstanceState) {
//                        final View view = inflater.inflate(R.layout.fragment_banner, container,
//                                false);
//                        if (position == 0) {
//                            view.setBackgroundResource(R.drawable.bottom_banner_1);
//                        } else if (position == 1) {
//                            view.setBackgroundResource(R.drawable.bottom_banner_2);
//                        }
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                goUrl("http://anpr.cafe24.com/?page_mode=sub&depth_1=6&depth_2=2");
//                            }
//                        });
//
//                        return view;
//                    }
//                };
//            }
//
//        });
//        pageSwitcher(viewPager);
//        // viewPager.setAdapter(new PagerAdapter() {
//        //
//        // @Override
//        // public Object instantiateItem(ViewGroup container, int position) {
//        // View view = getLayoutInflater().inflate(R.layout.fragment_banner,
//        // container);
//        //
//        // ImageView ivBanner = (ImageView)
//        // view.findViewById(R.id.main_banner_img);
//        //
//        // if(position == 0) {
//        // ivBanner.setImageResource(R.drawable.bottom_banner_1);
//        // } else if (position == 1) {
//        // ivBanner.setImageResource(R.drawable.bottom_banner_2);
//        // }
//        //
//        // return view;
//        // }
//        //
//        // @Override
//        // public int getCount() {
//        // return 2;
//        // }
//        //
//        // @Override
//        // public boolean isViewFromObject(View view, Object object) {
//        // return false;
//        // }
//        // });
//    }

    private void goUrl(String url) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void pageSwitcher(final ViewPager viewPager) {

        int second = 2 * 1000;

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewPager.getCurrentItem();

                        if (position == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (position == 1) {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });

            }
        }, 0, second);
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
        UpdateChecker.start();
    }

    private synchronized void buildGoogleApiClient() {
        // mGoogleApiClient = new
        // GoogleApiClient.Builder(this).addConnectionCallbacks(this)
        // .addOnConnectionFailedListener(this).addApi(LocationServices.API)
        // .addOnConnectionFailedListener(this).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mGoogleApiClient.connect();
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
        // mLastLocation =
        // LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
        // mGoogleApiClient.connect();
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

    private void goShopList(boolean life) {

        if (mGpsFlag) {
            mIntent.putExtra("position", mPosition);
            mIntent.putExtra("life", life);

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

    public void mainOnClick(View view) {
        //
        switch (view.getId()) {

            case R.id.main_chicken:
                mPosition = 0;
                break;
            case R.id.main_pizza:
                mPosition = 1;
                break;
            case R.id.main_chinese:
                mPosition = 2;
                break;
            case R.id.main_korean:
                mPosition = 3;
                break;
            case R.id.main_night:
                mPosition = 4;
                break;
            case R.id.main_soup:
                mPosition = 5;
                break;
            case R.id.main_jokbal:
                mPosition = 6;
                break;
            case R.id.main_japanese:
                mPosition = 7;
                break;
            case R.id.main_boxlunch:
                mPosition = 8;
                break;
            case R.id.main_fastfood:
                mPosition = 9;
                break;
            case R.id.main_driver:
                mPosition = 11;
                break;
            case R.id.main_quick:
                mPosition = 12;
                break;

        }

        goShopList(false);

    }

    public void infoClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cashq.co.kr/policy/privacy.html")));
    }

    private class AddressJsonTask extends AsyncTask<Void, Void, JSONObject> {

        public AddressJsonTask(TextView textView) {
            tvAddress = textView;
        }

        private TextView tvAddress;

        private final String TAG = getClass().getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            String url = Uri.parse("http://maps.googleapis.com/maps/api/geocode/json").buildUpon()
                    .appendQueryParameter("language", "ko")
                    .appendQueryParameter("latlng", sLatitude + "," + sLongitude)
                    .appendQueryParameter("sensor", "false").build().toString();

            Log.e(TAG, "URL : " + url);

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                String json = jsonObject.toString();
                json = new String(json.getBytes("8859_1"), "UTF-8");

                mAddress = getAddress(json);

                // if (SALE_ZONE) {
                // btnSale.setVisibility(View.VISIBLE);
                // mPointText.setVisibility(View.GONE);
                // } else {
                // btnSale.setVisibility(View.GONE);
                // mPointText.setVisibility(View.VISIBLE);
                // }

                tvAddress.setText(mAddress);
//                sDistance = 3;

                if ("부천시".equals(mSi) && "소사구".equals(mGu)) {
                    sDistance = 2;
                } else if ("제천시".equals(mSi)) {
                    sDistance = 5;
                }
                // } else if ("양산시".equals(mSi)) {
                // mLifeImageView.setVisibility(View.GONE);
                // sDistance = 5;
                // } else if ("안산시".equals(mSi) || "의정부시".equals(mSi) ||
                // "성북구".equals(mGu)) {
                // // mPointText.setVisibility(View.GONE);
                // mLifeImageView.setVisibility(View.VISIBLE);
                // LIFE_ZONE = true;
                // sDistance = 3;

                // String uri = "drawable://" + R.drawable.bg_chicken_crop;
                // ImageLoader.getInstance().loadImage(uri, new
                // SimpleImageLoadingListener() {
                // @Override
                // public void onLoadingComplete(String imageUri, View view,
                // Bitmap loadedImage) {
                // Drawable topImage = new BitmapDrawable(loadedImage);
                // mBackgroundLayout.setBackgroundDrawable(topImage);
                // }
                // });

                // } else {
                // mLifeImageView.setVisibility(View.GONE);
                // }

                mManualDistance.setText(sDistance + "km\n변경");

                Log.e(TAG, "address : " + mAddress);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

        }

        private String getAddress(String json) {
            try {

                JSONObject jsonObject = new JSONObject(json);

                JSONArray resultsArray = jsonObject.getJSONArray("results");

                // SALE_ZONE = resultsArray.toString().contains("안산시");

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

                            sLatitude = mLocationUtil.getLastLocation().getLatitude();
                            sLongitude = mLocationUtil.getLastLocation().getLongitude();

                            new AddressJsonTask(mAddressText).execute();

                            String address;
                            // address = mLocationUtil.getAddress(sLatitude,
                            // sLongitude);

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
        // NotificationManager nm =
        // (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // nm.cancelAll();
        setCartCount(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        super.onResume();
        // Check device for Play Services APK.
    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        // if (mGoogleApiClient.isConnected())
        // mGoogleApiClient.disconnect();

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
        // new com.anp.bdmt.lazylist.ImageLoader(mContext).clearCache();
    }

    public void mOnClick(View view) {
        switch (view.getId()) {
        // case R.id.main_sale:
        // mPosition = 0;
        // mType = "W00";
        // mIntent.putExtra("TYPE", mType);
        // mIntent.putExtra("lat", sLatitude);
        // mIntent.putExtra("lng", sLongitude);
        // mIntent.putExtra("distance", sDistance);
        //
        // startActivity(mIntent);
        // if (!mDialog.isShowing()) {
        // mDialog.show();
        // }
        // break;

            case R.id.manual_location:
                startActivity(new Intent(this, MapActivity.class));
                break;

            case R.id.location_name1:
                startActivity(new Intent(this, MapActivity.class));
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
            case R.id.main_life:
                new LifeDialog(this, mOnClickListener).show();
                break;

            case R.id.btn_main_driver:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:05041114111")));
                break;
        //
        // // mIntent.putExtra("TYPE", mType);
        // // mIntent.putExtra("lat", sLatitude);
        // // mIntent.putExtra("lng", sLongitude);
        // // mIntent.putExtra("distance", sDistance);
        //
        // // startActivity(mIntent);
        // // if (!mDialog.isShowing()) {
        // // mDialog.show();
        // // }
        // break;
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.v(TAG, "getid: " + v.getId());

            switch (v.getId()) {
                case R.id.life_09:
                    mPosition = 0;
                    break;
                case R.id.life_10:
                    mPosition = 1;
                    break;
                case R.id.life_11:
                    mPosition = 2;
                    break;
                case R.id.life_12:
                    mPosition = 3;
                    break;
                case R.id.life_13:
                    mPosition = 4;
                    break;
                case R.id.life_14:
                    mPosition = 5;
                    break;
                case R.id.life_15:
                    mPosition = 6;
                    break;
                case R.id.life_16:
                    mPosition = 7;
                    break;
                case R.id.life_18:
                    mPosition = 8;
                    break;
                case R.id.life_19:
                    mPosition = 9;
                    break;
            }

            mIntent.putExtra("life", true);
            mIntent.putExtra("position", mPosition);

            startActivity(mIntent);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    };

    private class LoadSavingTask extends AsyncTask<String, String, JSONObject> {

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

    // public String getRegId() {
    // final String regId =
    // GCMRegistrar.getRegistrationId(getApplicationContext());
    // Log.e("JAY", "regid = " + regId);
    // if (regId.equals("")) {
    // GCMRegistrar.register(this, SENDER_ID);
    // GCMRegistrar.checkDevice(this);
    // GCMRegistrar.checkManifest(this);
    // } else {
    // if (GCMRegistrar.isRegisteredOnServer(this)) {
    // // Toast.makeText(getApplicationContext(), "Already",
    // // Toast.LENGTH_LONG).show();
    // } else {
    // final Context context = this;
    // mRegisterTask = new AsyncTask<Void, Void, Void>() {
    //
    // protected Void doInBackground(Void... params) {
    // ServerUtilities
    // .register(context, "central", getPhoneNumber(context), regId);
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute(Void result) {
    // mRegisterTask = null;
    // }
    // };
    // mRegisterTask.execute(null, null, null);
    // }
    // Log.v("JAY", "Already registered");
    // }
    // return regId;
    // }

    // public void setHttpRequest() {
    // try {
    // String num = mPhoneNum;
    // String register = Util.loadSharedPreferences(getApplicationContext(),
    // Utils.REGISTER);
    // Log.e("JAY", "loadshared = " + register);
    // if (register != null) {
    // // if (TTS_MODE) {
    // String url = "http://cashq.co.kr/m/set_tokenid_add.php" +
    // "?biz_code=central"
    // + "&phone=" + num + "&token_id=" + getRegId();
    //
    // TOKEN_ID = register;
    //
    // Log.e("test", "register  :  " + register);
    // Log.e("test", "url  :  " + url);
    //
    // new HttpRequest(mHttpRequestListener, url, "list");
    // } else {
    // new com.anp.bdmt.gcm.Timer(new TimerListener() {
    //
    // public void onTick() {
    // setHttpRequest();
    // }
    // }, 3000);
    // }
    // } catch (NullPointerException e) {
    // e.printStackTrace();
    // }
    // }

    // private HttpRequest.HttpRequestListener mHttpRequestListener = new
    // HttpRequest.HttpRequestListener() {
    //
    // public void httpRequestError() {
    // httpRequestErrorMsg();
    // }
    //
    // public void getRequestData(ArrayList<Dictionary> dicArray, boolean
    // isError) {
    //
    // if (dicArray.size() > 0) {
    // }
    // }
    // };

    /**
     * 컨텐츠 요청 에러
     */
    // private void httpRequestErrorMsg() {
    // if (!isFinishing()) {
    // // Util.showDialog(this, "알림", "인터넷 환경이 불안정 합니다. 네트워크 상태를 확인해 주세요.",
    // // null);
    // }
    // }

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
                    // mManualTextView.setVisibility(View.VISIBLE);
                } else {
                    adminFlag = false;
                    sDistance = 3;
                    // mManualTextView.setVisibility(View.GONE);
                }

            }
        });

        return builder.create();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라
     * UI에 변화를 준다.
     */
    public void registBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(QuickstartPreferences.REGISTRATION_READY)) {
                    // 액션이 READY일 경우
                    Log.i(TAG, "regist READY");
                } else if (action.equals(QuickstartPreferences.REGISTRATION_GENERATING)) {
                    // 액션이 GENERATING일 경우
                    Log.i(TAG, "regist GENERATING");
                } else if (action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)) {
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");

                    TOKEN_ID = token;

                    Log.i(TAG, "token: " + token);
                    getTokenId(token);
                } else if (action.equals(QuickstartPreferences.REGISTRATION_ALREADY)) {
                    Log.i(TAG, "regist Already");
                }

            }
        };
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void getTokenId(final String token) {

        final String phoneNum = getPhoneNumber(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://cashq.co.kr/m/ajax_data/get_token_id.php?appid=" + APP_ID + "&phone="
                + phoneNum;

        Log.i(TAG, "Get token Url: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            String receivedToken = jsonObject.getString("token_id");
                            Log.i(TAG, "received: " + receivedToken);

                            if (receivedToken.equals(token)) {
                                Log.i(TAG, "token already !!");
                            } else {
                                setTokenId(token);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(stringRequest);
    }

    private void setTokenId(String token) {

        final String phoneNum = getPhoneNumber(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://cashq.co.kr/m/set_tokenid_add.php?biz_code=central&appid=" + APP_ID
                + "&gcm_type=gcm3" + "&phone=" + phoneNum + "&token_id=" + token;

        Log.e(TAG, "setToken: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "PUSH 서버 등록 성공 !", Toast.LENGTH_SHORT)
                                .show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "PUSH 서버 등록 실패.. !", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        requestQueue.add(stringRequest);
    }

}
