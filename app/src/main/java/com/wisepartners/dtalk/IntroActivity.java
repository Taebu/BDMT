
package com.wisepartners.dtalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import static com.wisepartners.dtalk.MainActivity.sDistance;

public class IntroActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private Handler mHandler = new Handler();

    private LocationUtil mLocationUtil;

    private boolean mGpsFlag;

    private double mLongitude;

    private double mLatitude;

    private String mGu, mSi;
    private String mAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);

//        newGps();

        // findLocation();

        // new GpsTask().execute();

         loading();

        // loading2();
    }

    private void newGps() {
        GpsInfo gpsInfo = new GpsInfo(this);

        if (gpsInfo.isGetLocation()) {
            mLatitude = gpsInfo.getLatitude();
            mLongitude = gpsInfo.getLongitude();
            mGpsFlag = true;

            Log.e(TAG, "당신의 위치 - 위도: " + mLongitude + "경도: " + mLatitude);

            loading();

        } else {
            mGpsFlag = false;
            gpsInfo.showSettingAlert();
        }
    }

    private void intentAction() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.putExtra("gpsflag", mGpsFlag);
        intent.putExtra("lat", mLatitude);
        intent.putExtra("lon", mLongitude);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void loading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.putExtra("gpsflag", mGpsFlag);
                intent.putExtra("lat", mLatitude);
                intent.putExtra("lng", mLongitude);

                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 3000);
    }

    private class AddressJsonTask extends AsyncTask<Double, Void, JSONObject> {

        private final String TAG = getClass().getSimpleName();

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

                if ("동두천시".equals(mSi) || "안산시".equals(mSi) || "강서구".equals(mGu)
                        || "양천구".equals(mGu) || "의정부시".equals((mSi))) {
                    sDistance = 3;
                } else if ("양산시".equals(mSi)) {
                    sDistance = 5;
                } else {
                    sDistance = 2;
                }

                Log.e(TAG, "address : " + mAddress);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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

    private void loading2() {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        handler.sendEmptyMessageAtTime(0, 3000);
    }

    public void findLocation() {
        mLocationUtil = LocationUtil.getInstance(this);
        mLocationUtil.start();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {

                        try {

                            mLatitude = mLocationUtil.getLastLocation().getLatitude();
                            mLongitude = mLocationUtil.getLastLocation().getLongitude();

                            mGpsFlag = true;

                        } catch (NullPointerException e) {
                            Log.d("JAY", "gps exception");
                            e.printStackTrace();

                            mGpsFlag = false;
                        }
                    }
                });
            }
        };

        if (mLocationUtil.isRunLocationUtil) {
            mLocationUtil.stop();
        }

        intentAction();

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    private class GpsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLocationUtil = LocationUtil.getInstance(IntroActivity.this);
            mLocationUtil.start();
        }

        @Override
        protected Void doInBackground(String... params) {
            mLocationUtil.start();
            mLatitude = mLocationUtil.getLastLocation().getLatitude();
            mLongitude = mLocationUtil.getLastLocation().getLongitude();
            mGpsFlag = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            if (mLocationUtil.isRunLocationUtil) {
                mLocationUtil.stop();
            }

        }
    }

}
