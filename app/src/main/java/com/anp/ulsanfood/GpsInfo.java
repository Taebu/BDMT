
package com.anp.ulsanfood;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * @ Jung-Hum Cho Created by anp on 2015. 12. 8..
 */
public class GpsInfo extends Service implements LocationListener {

    public GpsInfo(Context context) {
        this.mContext = context;
        getLocation();
    }

    private final Context mContext;

    private boolean isGpsEnabled = false;

    private boolean isNetworkEnabled = false;

    private boolean isGetLocation = false;

    private Location mLocation;

    private double mLatitude;

    private double mLongitude;

    // 최소 업데이트 거리
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 업데이트 시간
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager mLocationManager;

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

            // gps 정보 가져오기
            isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                // gps, 네트워크 모두 안될때
            } else {
                this.isGetLocation = true;

                // 네트워크로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (mLocationManager != null) {
                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (mLocation != null) {
                            // 위도 경도 저장
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }

                // gps로 부터 위치값 가져오기
                if (isGpsEnabled && mLocation == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (mLocationManager != null) {
                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (mLocation != null) {
                            // 위도 경도 저장
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }

    /**
     * gps 종료
     */
    public void stopGps() {
        if (mLocation != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    /**
     * @return latitude
     */
    public double getLatitude() {
        if (mLocationManager != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    /**
     * @return longitude
     */
    public double getLongitude() {
        if (mLocationManager != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    /**
     * @return GPS나 wife 정보가 켜져 있는지 확인
     */
    public boolean isGetLocation() {
        return isGetLocation;
    }

    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용 설정");
        alertDialog.setMessage("설정에서 GPS 관련 설정을 모두 켜주세요.\n 설정으로 이동 하시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
