
package kr.co.cashqc;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 5..
 */

public class MapActivity extends BaseActivity implements GoogleMap.OnMapClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private final String TAG = getClass().getSimpleName();

    LocationUtil mLocationUtil;

    private GoogleMap mMap;

    private Marker mMarker;

    private UiSettings mUiSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        killer.addActivity(this);

        mLocationUtil = LocationUtil.getInstance(this);

        setUpMapIfNeeded();

        // 본사 gps 37.636992, 126.775057

        // LatLng place = new LatLng(37.636992, 126.775057);

        Double lat, lng;
        lat = getIntent().getDoubleExtra("lat", -1);
        lng = getIntent().getDoubleExtra("lng", -1);

        LatLng place = new LatLng(lat, lng);

        // mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        // mMap.ic

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));

        mMarker = mMap.addMarker(new MarkerOptions().title("place").snippet("cashq company")
                .position(place).flat(true));

        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(false);
        }

        String address = mLocationUtil.getAddress(latLng.latitude, latLng.longitude);

        // String address = mLocationUtil.getAddress1(latLng.latitude,
        // latLng.longitude);

        Log.e("LocationUtil", "\naddress : " + address);

        mMarker.remove();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMarker = mMap.addMarker(new MarkerOptions().title("클릭하여 현재 위치 지정하기").position(latLng)
                .snippet(address));

        // mUiSettings.setMyLocationButtonEnabled(true);
        mMarker.showInfoWindow();

        // Toast.makeText(this, address, Toast.LENGTH_LONG).show();
        // Toast.makeText(this, ""+latLng, Toast.LENGTH_LONG).show();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("lat", marker.getPosition().latitude);
        i.putExtra("lng", marker.getPosition().longitude);
        i.putExtra("gpsflag", true);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        // killer.removeActivity();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                String address1 = (String)v.getTag(R.id.address1);
                String address2 = (String)v.getTag(R.id.address2);

                String fullAddress = address1 + address2;

                Log.e(TAG, fullAddress);

                new GeocodingTask().execute(fullAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:

                new AddressListDialog(this, mOnClickListener).show();

                // EditText address =
                // (EditText)findViewById(R.id.input_address);
                // if (address.length() != 0) {
                // LatLng latLng =
                // addressToLatLng(address.getText().toString());
                // if (latLng != null) {
                // onMapClick(latLng);
                // }
                // }
                break;
        }
    }

    private class GeocodingTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String address = params[0].replace(" ", "");

            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address;

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                String status = jsonObject.getString("status");

                if (!status.equals("OK"))
                    return;

                JSONArray resultsArray = jsonObject.getJSONArray("results");

                JSONObject resultsObject = resultsArray.getJSONObject(0);

                JSONObject geometryObject = resultsObject.getJSONObject("geometry");

                JSONObject locationObject = geometryObject.getJSONObject("location");

                double latitude = locationObject.getDouble("lat");
                double longitude = locationObject.getDouble("lng");

                LatLng latLng = new LatLng(latitude, longitude);

                onMapClick(latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private LatLng addressToLatLng(String address) {
        String s = address;
        try {
            List<Address> addr = new Geocoder(this, Locale.KOREAN).getFromLocationName(s, 5);

            if (addr.size() == 0) {
                return null;
            } else {

                LatLng latLng = null;

                for (Address a : addr) {
                    latLng = new LatLng(a.getLatitude(), a.getLongitude());
                }
                return latLng;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
