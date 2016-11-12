
package com.anp.bdmt;

import static com.anp.bdmt.MainActivity.sLatitude;
import static com.anp.bdmt.MainActivity.sLongitude;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 5..
 */

public class MapActivity extends BaseActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraChangeListener {

    private final String TAG = getClass().getSimpleName();

    private GoogleMap mMap;

    private Marker mMarker;

    private AutoCompleteTextView mAddressEditText;

    private ArrayAdapter<String> mAddressListAdapter;

    private List<Address> mAddressList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        killer.addActivity(this);

        if (mMap == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
        }

        // 본사 gps 37.636992, 126.775057
        // LatLng place = new LatLng(37.636992, 126.775057);

        LatLng place = new LatLng(sLatitude, sLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
        mMarker = mMap.addMarker(new MarkerOptions().title("place").snippet("cashq company")
                .position(place).flat(true));

        mMap.setOnCameraChangeListener(this);

        mMap.setOnInfoWindowClickListener(this);

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddressEditText == null) {
                    mAddressEditText = (AutoCompleteTextView)findViewById(R.id.input_address);
                }
                hideKeyboard();
                geocoding(mAddressEditText.getText().toString());
            }
        });

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        hideKeyboard();
        reverseGeocoding(cameraPosition.target);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(this, MainActivity.class);
        sLatitude = marker.getPosition().latitude;
        sLongitude = marker.getPosition().longitude;
        i.putExtra("gpsflag", true);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void geocoding(String address) {

        // south KR (33.06, 125.04, 38.27, 131.52)

        String url = "http://maps.google.com/maps/api/geocode/json?language=ko&address=" + address;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mAddressList = getAddressListFromJson(response);

                        if (mAddressList == null) {
                            return;
                        }

                        List<String> addressLineList = new ArrayList<>();

                        for (Address a : mAddressList) {
                            addressLineList.add(a.getAddressLine(0).replace("대한민국 ", ""));
                        }

                        mAddressListAdapter = new ArrayAdapter<>(MapActivity.this,
                                android.R.layout.simple_dropdown_item_1line, addressLineList);
                        // R.layout.textview_autocomplete_item,
                        // addressLineList);

                        mAddressEditText.setAdapter(mAddressListAdapter);

                        if (mAddressEditText.getOnItemClickListener() == null) {
                            mAddressEditText
                                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                                            mAddressListAdapter = null;

                                            LatLng latLng = new LatLng(mAddressList.get(position)
                                                    .getLatitude(), mAddressList.get(position)
                                                    .getLongitude());

                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                    latLng, 15));

                                            reverseGeocoding(latLng);
                                        }
                                    });
                        }

                        mAddressEditText.showDropDown();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void reverseGeocoding(final LatLng latLng) {

        String url = "http://maps.google.com/maps/api/geocode/json?language=ko&latlng="
                + latLng.latitude + "," + latLng.longitude;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Address> addressList = getAddressListFromJson(response);

                        if (addressList.size() != 0) {
                            String address = addressList.get(0).getAddressLine(0)
                                    .replace("대한민국 ", "");

                            Log.e("LocationUtil", "\naddress : " + address);

                            mMarker.setPosition(latLng);
                            mMarker.setTitle("클릭하여 현재 위치 지정하기");
                            mMarker.setSnippet(address);
                            mMarker.showInfoWindow();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void hideKeyboard() {
        // https://realm.io/kr/news/tmi-dismissing-keyboard-ios-android/
        // View view = this.getCurrentFocus();
        // if (view != null) {
        // mInputMethodManager =
        // (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        // mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
        // InputMethodManager.HIDE_IMPLICIT_ONLY);
        // }

        if (mAddressEditText == null) {
            return;
        }

        if (mAddressEditText.hasFocus()) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAddressEditText.getWindowToken(), 0);
        }

    }

    private List<Address> getAddressListFromJson(String json) {
        List<Address> addressList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = (JSONArray)jsonObject.get("results");
            for (int i = 0; i < array.length(); i++) {
                try {

                    Double lon = array.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lng");

                    Double lat = array.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lat");

                    String name = array.getJSONObject(i).getString("formatted_address");

                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(lat);
                    address.setLongitude(lon);
                    address.setAddressLine(0, name != null ? name : "");

                    addressList.add(address);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return addressList;
    }

}
