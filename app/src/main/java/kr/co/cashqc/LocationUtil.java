
package kr.co.cashqc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    private final String TAG = getClass().getSimpleName();

    boolean mFlag = true;

    private boolean networkLocationActivated = false;

    private boolean gpsLocationActivated = false;

    private Location lastLocation = null;

    private LocationManager lm = null;

    private static LocationUtil instance = null;

    private Activity mActivity;

    protected LocationListenerAdaptor mGpsLocationListener;

    protected LocationListenerAdaptor mNetworkLocationListener;

    protected LocationListener mLocationReceiver;

    public boolean isRunLocationUtil = false;

    private LocationUtil(Activity pmainActivity) {
        mActivity = pmainActivity;
        lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);

    }

    static public LocationUtil getInstance(Activity mainActivity) {
        if (instance == null) {
            instance = new LocationUtil(mainActivity);
        }
        return instance;
    }

    public boolean isGPSAvailable() {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isWifiOrCellIDLocationAvailable() {
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public synchronized void start() {

        isRunLocationUtil = true;

        Log.v(TAG, "Location util start()");

        // Toast.makeText(mActivity, "Location util start()",
        // Toast.LENGTH_SHORT).show();

        // initialize state of location providers and launch location listeners

        Criteria criteria = new Criteria();
        String bestProvider = lm.getBestProvider(criteria, false);

        // lm.requestLocationUpdates(bestProvider, 20000, 1, (LocationListener)
        // mActivity);

        if (!networkLocationActivated && isWifiOrCellIDLocationAvailable()) {
            networkLocationActivated = true;
            mNetworkLocationListener = new LocationListenerAdaptor();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000L, 10,
                    this.mNetworkLocationListener);
        }

        if (!gpsLocationActivated && isGPSAvailable()) {
            gpsLocationActivated = true;
            mGpsLocationListener = new LocationListenerAdaptor();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000L, 10,
                    this.mGpsLocationListener);
        }

        // get the best location using bestProvider()
        try {
            lastLocation = lm.getLastKnownLocation(bestProvider());
            Log.i(TAG, lastLocation.getLatitude() + ":" + lastLocation.getLongitude());

        } catch (Exception e) {
            Log.e(TAG, "Error getting the first location");
            // Toast.makeText(mActivity, "Error getting the first location",
            // Toast.LENGTH_SHORT).show();
        }

        // test to see which location services are available
        if (!gpsLocationActivated) {
            mFlag = false;
            if (!networkLocationActivated) {

                // no location providers are available, ask the user if they
                // want to go and change the setting
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setCancelable(true);
                builder.setMessage(
                        "정확한 배달 위치를 잡으시려면 위치 정보서비스(GPS) 설정을 켜주세요. ※Google 위치정보 수집에 동의하여 주세요.")
                        .setCancelable(false)
                        .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.dismiss();
                                } catch (IllegalArgumentException e) {
                                    // if orientation change, thread
                                    // continue but the mDialog cannot be
                                    // dismissed without exception
                                }
                                mActivity.startActivity(new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                /*
                 * .setNegativeButton("취소", new
                 * DialogInterface.OnClickListener() { public void
                 * onClick(DialogInterface mDialog, int id) { mDialog.cancel();
                 * } })
                 */
                ;
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // we have network location but no GPS, tell the user that
                // accuracy is bad because of this
                // Toast.makeText(mActivity,
                // "위치정보를 가져올 수 없습니다.",Toast.LENGTH_LONG).show();
            }
        } else if (!networkLocationActivated) {
            // we have GPS (but no network), this tells the user
            // that they might have to wait for a fix
            // Toast.makeText(mActivity,
            // "위치정보를 가져올 수 없습니다.",Toast.LENGTH_LONG).show();
        }
        // Log.v(TAG, "Location util end");
    }

    public synchronized void stop() {
        isRunLocationUtil = false;
        Log.v(TAG, "LocationHandler Stop");
        // Toast.makeText(mActivity, "LocationHandler Stop",
        // Toast.LENGTH_SHORT).show();
        try {
            lm.removeUpdates(mGpsLocationListener);
            networkLocationActivated = false;
            mGpsLocationListener = null;
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Ignoring: " + e);
            // there's no gps location listener to disable
        }
        try {
            lm.removeUpdates(mNetworkLocationListener);
            gpsLocationActivated = false;
            mNetworkLocationListener = null;
        } catch (IllegalArgumentException e) {
            Log.v(TAG, "Ignoring: " + e);
            // there's no network location listener to disable
        }
    }

    /**
     * Defines the best location provider using isBestProvider() test
     * 
     * @return LocationProvider or null if none are available
     */
    protected String bestProvider() {
        String bestProvider = null;
        if (networkLocationActivated
                && isBestProvider(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))) {
            bestProvider = LocationManager.NETWORK_PROVIDER;
        } else if (gpsLocationActivated) {
            bestProvider = LocationManager.GPS_PROVIDER;
        }
        return bestProvider;
    }

    private boolean isBestProvider(Location myLocation) {
        if (myLocation == null) {
            return false;
        }
        boolean isBestProvider = false;
        String myProvider = myLocation.getProvider();
        boolean gpsCall = myProvider.equalsIgnoreCase(LocationManager.GPS_PROVIDER);
        boolean networkCall = myProvider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER);
        // get all location accuracy in meter; note that less is better!
        float gpsAccuracy = Float.MAX_VALUE;
        long gpsTime = 0;
        if (gpsLocationActivated) {
            Location lastGpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastGpsLocation != null) {
                gpsAccuracy = lastGpsLocation.getAccuracy();
                gpsTime = lastGpsLocation.getTime();
            }
        }
        float networkAccuracy = Float.MAX_VALUE;
        if (networkLocationActivated) {
            Location lastNetworkLocation = lm
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastNetworkLocation != null) {
                networkAccuracy = lastNetworkLocation.getAccuracy();
            }
        }
        float currentAccuracy = myLocation.getAccuracy();
        long currentTime = myLocation.getTime();
        // Use myLocation if:
        // 1. it's a gps location & network is disabled
        // 2. it's a gps loc & network activated
        // & gps accuracy is better than network
        // 3. it's a network loc & gps is disabled
        // 4. it's a network loc, gps enabled
        // & (network accuracy is better than gps
        // OR last network fix is newer than last gps fix+30seconds)
        boolean case1 = gpsCall && !networkLocationActivated;
        boolean case2 = gpsCall && networkLocationActivated && currentAccuracy < networkAccuracy;
        boolean case3 = networkCall && !gpsLocationActivated;
        boolean case4 = networkCall && gpsLocationActivated
                && (currentAccuracy < gpsAccuracy || currentTime > gpsTime + 30000);
        if (case1 || case2 || case3 || case4) {
            isBestProvider = true;
        }
        return isBestProvider;
    }

    public String getAddress(double latitude, double longitude) {
        String address = "";
        List<Address> addresses = null;
        Geocoder geoCoder = new Geocoder(mActivity, Locale.KOREAN);

        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {

            // Log.i("tag", "addresses : " + addresses.toString());

            for (Address addr : addresses) {
                int index = addr.getMaxAddressLineIndex();

                // Log.i("tag", "index : " + index);

                for (int i = 0; i <= index; i++) {
                    address = addr.getAddressLine(i);
                    // Log.i("tag", "address : " + address);
                    String[] str = address.split(" ", 4);
                    address = "";
                    for (int j = 1; j < str.length; j++) {
                        if (!str[j].equals("")) {
                            address = address + str[j] + " ";
                        }
                    }
                }
            }
        }
        return address;
    }

    public String getAddressShort(double latitude, double longitude) {

        String address = "";
        List<Address> addresses = null;
        Address a = null;
        Geocoder geoCoder = new Geocoder(mActivity, Locale.KOREAN);
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            a = addresses.get(0);
            Log.i("tag", "addresses : " + addresses.toString());
            address = a.getThoroughfare() + a.getFeatureName();
            for (Address addr : addresses) {
                int index = addr.getMaxAddressLineIndex();

                // Log.i("tag", "index : " + index);

                for (int i = 0; i <= index; i++) {

                    address = addr.getAddressLine(i);
                    Log.i("tag", "address : " + address);
                    String[] str = address.split(" ", 4);
                    address = str[3];

                    // address = "";
                    // for (int j = 1; j < str.length; j++) {
                    // if (!str[j].equals("")) {
                    // address = address + str[j] + " ";
                    // }
                    // }
                }
            }

        }
        address = a.getThoroughfare() + " " + a.getFeatureName();

        return address;
    }

    public String getAddress1(double lat, double lng) {

        Geocoder geocoder = new Geocoder(mActivity, Locale.KOREAN);

        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LocationUtil", "IO EXCEPTION in getFromlocation()");
            return "다시 시도해 주세요";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            String errorString = "Illegal arguments " + lat + ", " + lng
                    + " passed to address service";
            Log.e("Locationutil", errorString);
            return errorString;
        }

        if (addressList != null && addressList.size() > 0) {
            Address a = addressList.get(0);

            Log.i("LocationUtil",
                    "\ngetMax : " + a.getAddressLine(0) + "\nfalse : " + a.getAdminArea()
                            + a.getLocality() + a.getSubLocality() + a.getThoroughfare()
                            + a.getSubThoroughfare());

            if (a.getMaxAddressLineIndex() > 0) {
                return a.getAddressLine(0);
            } else {
                StringBuilder sb = new StringBuilder("");
                sb.append(a.getAdminArea()).append(" ");
                sb.append(a.getLocality()).append(" ");
                sb.append(a.getSubLocality()).append(" ");
                sb.append(a.getThoroughfare()).append(" ");
                sb.append(a.getSubThoroughfare());
                return sb.toString();
            }

        } else {
            return "주소를 찾을 수 없습니다.";
        }
    }

    private class LocationListenerAdaptor implements LocationListener {
        public void onLocationChanged(final Location loc) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

            if (provider.equals(bestProvider())) {
                lastLocation = lm.getLastKnownLocation(provider);
            }
        }

        public void onProviderEnabled(String a) { /* ignore */
        }

        public void onProviderDisabled(String a) { /* ignore */
        }
    }

    public Location getLastLocation() {
        // Log.d("JAY","lastLocation : " + lastLocation.toString());
        return lastLocation;
    }
}
