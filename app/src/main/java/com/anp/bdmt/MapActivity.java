
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

        if (mAddressEditText == null) {
            mAddressEditText = (AutoCompleteTextView)findViewById(R.id.input_address);
        }

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                String keyword = mAddressEditText.getText().toString().trim();

                if (keyword.endsWith("동") && keyword.length() > 2) {
                    keyword = keyword.substring(0, keyword.length() - 1);
                }

                geocoding(keyword);
                // newGeocoding(mAddressEditText.getText().toString());
                // new
                // FuckTask().execute((mAddressEditText.getText().toString()));
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

    private class FuckTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://maps.google.com/maps/api/geocode/json?language=ko&address="
                    + params[0];

            return new JsonParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mAddressList = getAddressListFromJson(s);
            if (mAddressList == null) {
                return;
            }

            List<String> addressLineList = new ArrayList<>();
            for (Address a : mAddressList) {
                addressLineList.add(a.getAddressLine(0).replace("대한민국 ", ""));

                // try {
                // String address = a.getAddressLine(0).replace("대한민국 ", "");
                // address = new String(address.getBytes("utf-8"));
                // address = new String(address.getBytes("euc-kr"));
                // success
                // address = new String(address.getBytes("iso-8859-1"));
                // address = new String(address.getBytes("x-windows-949"));
                // addressLineList.add(address);
                // } catch (UnsupportedEncodingException e) {
                // e.printStackTrace();
                // }

                try {
                    // addressLineList.add(URLDecoder.decode(a.getAddressLine(0).replace("대한민국 ",
                    // ""), "utf-8"));
                    addressLineList.add(URLDecoder.decode(
                            URLEncoder.encode(a.getAddressLine(0).replace("대한민국 ", ""), "euc-kr"),
                            "euc-kr"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            mAddressListAdapter = new ArrayAdapter<>(MapActivity.this,
            // android.R.layout.simple_dropdown_item_1line,
            // addressLineList);
                    android.R.layout.simple_list_item_1, addressLineList);
            // R.layout.textview_autocomplete_item,
            // addressLineList);
            mAddressEditText.setAdapter(mAddressListAdapter);

            if (mAddressEditText.getOnItemClickListener() == null) {
                mAddressEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mAddressListAdapter = null;

                        LatLng latLng = new LatLng(mAddressList.get(position).getLatitude(),
                                mAddressList.get(position).getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        reverseGeocoding(latLng);
                    }
                });
            }

            mAddressEditText.showDropDown();

        }
    }

    private void geocoding(String address) {

        // south KR (33.06, 125.04, 38.27, 131.52)

        String url = "http://maps.google.com/maps/api/geocode/json?language=ko&region=kr&address=" + address;

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
                        // android.R.layout.simple_dropdown_item_1line,
                        // addressLineList);
                                android.R.layout.simple_list_item_1, addressLineList);
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

    private void newGeocoding(String dong) {

        // south KR (33.06, 125.04, 38.27, 131.52)
        dong = dong.trim();

        final String myApiKey = "a314996f3c4a87fb71420174091437";

        String apiUrl = "http://biz.epost.go.kr/KpostPortal/openapi";
        apiUrl += ("?regkey=");
        apiUrl += myApiKey;
        // &target=post !! DEPRICATED !!
        // &target=postNew !! USE THIS !!
        apiUrl += ("&target=");
        apiUrl += "postNew";
        apiUrl += ("&query=");
        try {
            apiUrl += URLEncoder.encode(dong, "euc-kr");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // mAddressList = getAddressListFromJson(response);
                        mAddressList = getAddressListFromXml(response);

                        if (mAddressList == null) {
                            return;
                        }

                        List<String> addressLineList = new ArrayList<>();
                        for (Address a : mAddressList) {
                            addressLineList.add(a.getAddressLine(0).replace("대한민국 ", ""));
                        }

                        mAddressListAdapter = new ArrayAdapter<>(MapActivity.this,
                        // android.R.layout.simple_dropdown_item_1line,
                        // addressLineList);
                                android.R.layout.simple_list_item_1, addressLineList);
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

    private List<Address> getAddressListFromXml(String response) {

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response)));
            Element el = (Element)doc.getElementsByTagName("itemlist").item(0);

            List<Address> addressList = new ArrayList<>();

            for (int i = 0; i < el.getChildNodes().getLength(); i++) {

                Node node = el.getChildNodes().item(i);

                if (!node.getNodeName().equals("item")) {
                    continue;
                }

                Address address = new Address(Locale.getDefault());
                // address.setLatitude(lat);
                // address.setLongitude(lon);
                address.setAddressLine(0, node.getChildNodes().item(1).getFirstChild()
                        .getNodeValue());

                addressList.add(address);

            }

            return addressList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    private class NewGeocodingTask extends AsyncTask<String, Void, ArrayList<AddressData>> {

        @Override
        protected ArrayList<AddressData> doInBackground(String... params) {

            String dong = params[0].trim();

            final String apiUrl = "http://biz.epost.go.kr/KpostPortal/openapi";

            final String myApiKey = "a314996f3c4a87fb71420174091437";

            ArrayList<AddressData> addressInfo = new ArrayList<AddressData>();

            HttpURLConnection conn = null;

            try {

                StringBuilder sb = new StringBuilder(3);
                sb.append(apiUrl);
                sb.append("?regkey=").append(myApiKey);
                sb.append("&target=post");
                sb.append("&query=").append(URLEncoder.encode(dong, "euc-kr"));
                String query = sb.toString();

                URL url = new URL(query);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("accept-language", "ko");

                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                byte[] bytes = new byte[4096];
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (true) {
                    int red = in.read(bytes);
                    if (red < 0) {
                        break;
                    }
                    baos.write(bytes, 0, red);
                }

                String xmlData = baos.toString("utf-8");
                baos.close();
                in.close();
                conn.disconnect();

                Document doc = docBuilder.parse(new InputSource(new StringReader(xmlData)));
                Element el = (Element)doc.getElementsByTagName("itemlist").item(0);

                for (int i = 0; i < el.getChildNodes().getLength(); i++) {

                    Node node = el.getChildNodes().item(i);

                    if (!node.getNodeName().equals("item")) {
                        continue;
                    }

                    AddressData data = new AddressData();

                    String address = node.getChildNodes().item(1).getFirstChild().getNodeValue();
                    String post = node.getChildNodes().item(3).getFirstChild().getNodeValue();

                    data.setAddress(address);
                    data.setZipcode(post.substring(0, 3) + "-" + post.substring(3));
                    data.setResult(address + "\n 우편번호 : " + post.substring(0, 3) + "-"
                            + post.substring(3));

                    addressInfo.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return addressInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<AddressData> addressInfo) {
            super.onPostExecute(addressInfo);

            for (AddressData s : addressInfo) {
                Log.e("zipcode", s.getResult());
                // adapter.add(s);
            }

        }
    }

}
