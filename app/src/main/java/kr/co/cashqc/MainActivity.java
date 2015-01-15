
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

import static kr.co.cashqc.CommonUtilities.SENDER_ID;
import static kr.co.cashqc.ShopListFragment.adminFlag;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.cashqc.gcm.Dictionary;
import kr.co.cashqc.gcm.HttpRequest;
import kr.co.cashqc.gcm.Timer.TimerListener;
import kr.co.cashqc.gcm.Util;
import kr.co.cashqc.view.CircleLayout;

public class MainActivity extends BaseActivity implements CircleLayout.OnItemSelectedListener,
        CircleLayout.OnItemClickListener, CircleLayout.OnRotationFinishedListener,
        CircleLayout.OnCenterClickListener {

    private Toast toast = null;

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

    private LinearLayout mLinearLayout;

    private TextView mPointText;

    private Context mContext;

    private double mLatitude, mLongitude;

    private TextView mManualTextView;

    private static int mDistance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (introFlag) {
            startActivity(new Intent(this, IntroActivity.class));
            introFlag = false;
        }

        GoogleAnalytics.getInstance(getApplicationContext()).dispatchLocalHits();

        CashqApplication c = new CashqApplication();
        Tracker t = c.getTracker(CashqApplication.TrackerName.APP_TRACKER);
        t.setScreenName("MainActivity");

        t.send(new HitBuilders.AppViewBuilder().build());

        LinearLayout ll = (LinearLayout)findViewById(R.id.gps_layout);
        if (Build.VERSION.SDK_INT > 10) {
            ll.setAlpha(0.5f);
        }

        mManualTextView = (TextView) findViewById(R.id.manual_location);

        mContext = getApplicationContext();

        // activity killer activity add.
        killer.addActivity(this);

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

        // custom dialog init.
        mDialog = new CustomDialog(this);

        // main bg init.
        mLinearLayout = (LinearLayout)findViewById(R.id.bg_main);

        // circle menu init.
        CircleLayout mCircleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);
        mCircleMenu.setOnItemSelectedListener(this);
        mCircleMenu.setOnItemClickListener(this);
        mCircleMenu.setOnRotationFinishedListener(this);
        mCircleMenu.setOnCenterClickListener(this);

        // address field init.
        mAddressText = (TextView)findViewById(R.id.location_name1);

        // gps util init.
        mLocationUtil = LocationUtil.getInstance(MainActivity.this);

        mLatitude = getIntent().getDoubleExtra("lat", -1);
        mLongitude = getIntent().getDoubleExtra("lng", -1);

        if (!mGpsFlag && mLatitude == -1) {
            findLocation();
        } else {
            mGpsFlag = true;
            mAddressText.setText(mLocationUtil.getAddress(mLatitude, mLongitude));
        }

//        if(adminFlag) {
            mManualTextView.setVisibility(View.VISIBLE);
//        } else {
//            mManualTextView.setVisibility(View.GONE);
//        }

        // gps btn set listener.
        findViewById(R.id.btn_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
            }
        });

        findViewById(R.id.admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        killer.allKillActivity();
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void onItemSelected(View view, String name) {

        if (toast == null) {
            toast = Toast.makeText(mContext, "음식사진 터치!~", Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, getScreenSize()/4);
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
                mLinearLayout.setBackgroundDrawable(topImage);
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
            mIntent.putExtra("distance", mDistance);

            startActivity(mIntent);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
            activityAnimation(true);
        } else {
            Toast.makeText(mContext, "GPS위치를 잡아주세요", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRotationFinished(View view, String name) {
        // Animation animation = new RotateAnimation(0, 360,
        // mLinearLayout.getWidth() / 2,
        // mLinearLayout.getHeight() / 2);
        // animation.setDuration(250);
        // view.startAnimation(animation);
    }

    public void findLocation() {
        mDialog.show();
        mLocationUtil.start();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            String address;
                            address = mLocationUtil.getAddress(mLocationUtil.getLastLocation()
                                    .getLatitude(), mLocationUtil.getLastLocation().getLongitude());

                            // Log.d("tag", mAddressText.getText().toString() +
                            // " "
                            // + mAddressText.getText().toString().length());

                            mAddressText.setVisibility(View.VISIBLE);

                            mLatitude = mLocationUtil.getLastLocation().getLatitude();
                            mLongitude = mLocationUtil.getLastLocation().getLongitude();

                            mAddressText.setText(address);

                            mGpsFlag = true;

                            mDialog.dismiss();
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
    protected void onResume() {
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        super.onResume();
        // Check device for Play Services APK.
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                startActivity(i);
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

            JSONParser parser = new JSONParser();

            String pointURL = "http://cashq.co.kr/m/ajax_data/get_point.php?phone="
                    + getPhoneNumber();

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
                        ServerUtilities.register(context, "central", getPhoneNumber(), regId);
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
            String num = getPhoneNumber();
            String register = Util.loadSharedPreferences(getApplicationContext(),
                    Global.RegisterKey222);
            Log.e("JAY", "loadshared = " + register);
            if (register != null) {
                // if (true) {
                String url = "http://cashq.co.kr/m/set_tokenid_add.php" + "?biz_code=central"
                        + "&phone=" + num + "&token_id=" + getRegId();

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
                    // mDistance =
                    // Integer.parseInt(distanceEditText.getText().toString());
                    // } catch (NumberFormatException e) {
                    // e.printStackTrace();
                    // mDistance = 3;
                    // }
                    mManualTextView.setVisibility(View.VISIBLE);
                } else {
                    adminFlag = false;
                    mDistance = 3;
                    // mManualTextView.setVisibility(View.GONE);
                }

            }
        });

        return builder.create();
    }

    private int getScreenSize() {
        Display display = MainActivity.this.getWindowManager().getDefaultDisplay();
        int screenHeight = display.getHeight();// 전체 스크리 사이즈 높이

        Window window = getWindow();
        int topBarHeight = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();// 상태바와
                                                                                   // 타이틀바의
                                                                                   // 높이
                                                                                   // 총합입니다.

        return screenHeight - topBarHeight;
    }

}
