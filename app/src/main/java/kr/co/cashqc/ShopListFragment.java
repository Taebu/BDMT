
package kr.co.cashqc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.co.cashqc.gcm.Util;

import static kr.co.cashqc.Utils.checkContact;

/**
 * @author Jung-Hum Cho
 */

public class ShopListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final String TAG = getClass().getSimpleName();

    // Constructor
    public ShopListFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("life", "pause");
        mTypeDuplicator = 0;
    }

    private ImageView mEmpty;

    private CustomDialog mDialog;

    private ListView mListView;

    private ShopListAdapter mAdapter;

    private int mPage = 1;

    private int mType;

    private ArrayList<ShopListData> mShopListData;

    private LinearLayout footerLayout;

    private int mTypeDuplicator = 0;

    private int mDistance = 2;

    // 관리자 모드
    public static boolean adminFlag = false;

    @SuppressLint("InlineApi")
    private static final String[] FROM_COLUMNS = {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                : ContactsContract.Contacts.DISPLAY_NAME
    };

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_shop, container, false);

        Tracker t = ((CashqApplication)getActivity().getApplication())
                .getTracker(CashqApplication.TrackerName.APP_TRACKER);

        t.setScreenName("ShopListFragment");
        t.send(new HitBuilders.AppViewBuilder().build());

        // custom mDialog init.
        mDialog = new CustomDialog(getActivity());

        // list dataset init
        mShopListData = new ArrayList<ShopListData>();

        // bundle class get extra
        mType = getArguments().getInt("mType");

        final double latitude = getArguments().getDouble("lat");
        final double longitude = getArguments().getDouble("lng");
        final int distance = getArguments().getInt("distance");

        Log.e("ShopListFragment", "\n" + latitude);
        Log.e("ShopListFragment", "\n" + longitude);

        // adapter init
        mAdapter = new ShopListAdapter(getActivity(), mOnClickListener);

        mEmpty = (ImageView)view.findViewById(R.id.empty);

        footerLayout = (LinearLayout)inflater.inflate(R.layout.footer, null);

        // list scroll, click listener set-up
        mListView = (ListView)view.findViewById(R.id.cashq_listView);

        // mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        mListView.addFooterView(footerLayout);

        // JSONParse AsyncTask Method
        new LoadShopListTask(latitude, longitude, distance).execute(mType, 1);

        for (String s : FROM_COLUMNS)
            Log.e("contacts", "contacts :: " + s);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPage++;
                Log.e("JAY", "mType : " + mType + " mPage" + mPage);
                new LoadShopListTask(latitude, longitude, distance).execute(mType, mPage);
            }
        });

        return view;
    }

    private class LoadShopListTask extends AsyncTask<Integer, Integer, String> {

        public LoadShopListTask(double lat, double lng, int distance) {
            mLat = lat;
            mLng = lng;
            mDistance = distance;
        }

        double mLat, mLng;

        int mDistance;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!Util.isOnline(getActivity())) {
                // Util.showDialog_normal(getActivity(), "네트워크 에러",
                // "네트워크 연결 상태를 확인해주세요");
                mDialog.dismiss();
            } else {

                if (!mDialog.isShowing())
                    mDialog.show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {

            // 본사 gps 37.636992, 126.775057

            // TODO 출시는 3km
            int distance = mDistance;
            int listsize = 10;

            if (adminFlag) {
                distance = 3;
            }

            int type = params[0];
            int page = params[1];

            StringBuilder sb = new StringBuilder("http://cashq.co.kr/m/list_json.php");
            sb.append("?listsize=").append(listsize);
            sb.append("&distance=").append(distance);
            sb.append("&lat=").append(mLat);
            sb.append("&lng=").append(mLng);
            sb.append("&type=W0").append(type);
            sb.append("&page=").append(page);

            Log.e("ShopListFragment.ShopList", "url : " + sb.toString());

            return new JsonParser().getJSONStringFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            JSONArray array = null;

            try {
                array = new JSONArray(json);
                Log.e("JAY", "array : " + array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (array != null && !"[]".equals(array.toString())) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String prePay = object.getString("pre_pay");

                        if (mTypeDuplicator == 0) {
                            if ("gl".equals(prePay)) {
                                mTypeDuplicator = 0;
                            } else if ("sl".equals(prePay)) {
                                mTypeDuplicator = 1;
                            } else if ("on".equals(prePay)) {
                                mTypeDuplicator = 2;
                            } else if ("".equals(prePay)) {
                                mTypeDuplicator = 3;
                            }
                        } else if (mTypeDuplicator == 1) {
                            if ("sl".equals(prePay)) {
                                mTypeDuplicator = 1;
                            } else if ("on".equals(prePay)) {
                                mTypeDuplicator = 2;
                            } else if ("".equals(prePay)) {
                                mTypeDuplicator = 3;
                            }
                        } else if (mTypeDuplicator == 2) {
                            if ("on".equals(prePay)) {
                                mTypeDuplicator = 2;
                            } else if ("".equals(prePay)) {
                                mTypeDuplicator = 3;
                            }
                        }

                        ShopListData separator = new ShopListData();

                        if ("gl".equals(prePay) && mTypeDuplicator == 0) {

                            separator.setSeparatorType(ShopListAdapter.TYPE_GOLD);
                            mAdapter.addSeparatorItem(separator);
                            mTypeDuplicator = 1;

                        } else if ("sl".equals(prePay) && mTypeDuplicator == 1) {

                            separator.setSeparatorType(ShopListAdapter.TYPE_SILVER);
                            mAdapter.addSeparatorItem(separator);
                            mTypeDuplicator = 2;

                        } else if ("on".equals(prePay) && mTypeDuplicator == 2) {

                            separator.setSeparatorType(ShopListAdapter.TYPE_CASHQ);
                            mAdapter.addSeparatorItem(separator);
                            mTypeDuplicator = 3;

                        } else if ("".equals(prePay) && mTypeDuplicator == 3) {

                            separator.setSeparatorType(ShopListAdapter.TYPE_NORMAL);
                            mAdapter.addSeparatorItem(separator);
                            mTypeDuplicator = 4;
                        }

                        ShopListData data = getRowData(object);

                        if (data != null) {
                            mAdapter.addItem(data);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // if(mListView.getFooterViewsCount() < 1) {
                // mListView.removeFooterView(footerLayout);
                // }

                mListView.setEmptyView(mEmpty);
                mEmpty.setVisibility(View.VISIBLE);
                Log.e("JAY", "\narray null");
            }

            // adapter set-up
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            if (mPage > 1) {

                mListView.setSelection((mPage - 1) * 9);
            }
            mDialog.dismiss();
        }
    }

    private ShopListData getRowData(JSONObject object) {

        try {

            Log.e("JAY", "object : " + object);

            ShopListData shopData = new ShopListData();

            if (object.has("r_tel")) {
                String r_tel = object.getString("r_tel");

                if (r_tel.startsWith("del_")) {
                    return null;
                } else {
                    shopData.setTel(r_tel);
                }
            }

            if (object.has("tel"))
                shopData.setTel(object.getString("tel"));

            if (object.has("name"))
                shopData.setName(object.getString("name"));

            if (object.has("pre_pay"))
                shopData.setPre_pay(object.getString("pre_pay"));

            if (object.has("seq"))
                shopData.setSeq(object.getString("seq"));

            if (object.has("thm"))
                shopData.setThm(object.getString("thm"));

            if (object.has("delivery_comment_cashq"))
                shopData.setDelivery_comment(object.getString("delivery_comment_cashq"));

            if (object.has("callcnt"))
                shopData.setCallcnt(object.getString("callcnt"));

            if (object.has("address"))
                shopData.setAddress(object.getString(("address")));

            if (object.has("distance"))
                shopData.setDistance(object.getString("distance"));

            if (object.has("time1"))
                shopData.setTime1(object.getString("time1").substring(3));

            if (object.has("time2"))
                shopData.setTime2(object.getString("time2").substring(6));

            if (object.has("img1"))
                shopData.setImg1(object.getString("img1"));

            if (object.has("img2"))
                shopData.setImg2(object.getString("img2"));

            if (object.has("review_cnt"))
                shopData.setReviewCount(object.getString("review_cnt"));

            if (object.has("review_rating"))
                shopData.setReviewRating(object.getString("review_rating"));

            if (object.has("minpay"))
                shopData.setMinpay(object.getString("minpay"));

            shopData.setSeq(object.getString("seq"));

            if (object.has("biz_code"))
                shopData.setBizCode(object.getString("biz_code"));

            shopData.setPay(object.getString("pay"));

            boolean isOpen = isBizHours(shopData.getTime1(), shopData.getTime2());

            shopData.setIsOpen(isOpen);

            return shopData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this will
     *            be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("JAY", "onitemclick!!!!");

        ShopListData data = (ShopListData)mAdapter.getItem(position);

        if (data.getSeparatorType() == ShopListAdapter.TYPE_ITEM) {

            Intent intent = new Intent(getActivity(), ShopPageActivity.class);
            // Intent intent = new Intent(getActivity(),
            // ShopMenuActivity.class);

            intent.putExtra("seq", data.getSeq());
            intent.putExtra("delivery_comment_cashq", data.getDelivery_comment());
            intent.putExtra("name", data.getName());
            intent.putExtra("thumb", data.getThm());
            intent.putExtra("time1", data.getTime1());
            intent.putExtra("time2", data.getTime2());
            intent.putExtra("callcnt", data.getCallcnt());
            intent.putExtra("distance", data.getDistance());
            intent.putExtra("pre_pay", data.getPre_pay());
            intent.putExtra("minpay", data.getMinpay());
            intent.putExtra("tel", data.getTel());

            intent.putExtra("img1", data.getImg1());
            intent.putExtra("img2", data.getImg2());
            intent.putExtra("thm", data.getThm());

            intent.putExtra("pre_pay", data.getPre_pay());

            intent.putExtra("store_code", data.getStoreCode());

            intent.putExtra("pay", data.getPay());

            intent.putExtra("review_cnt", data.getReviewCount());

            intent.putExtra("review_rating", data.getReviewRating());

            intent.putExtra("biz_code", data.getBizCode());


            // intent.putExtra("isopen", data.isOpen());
            intent.putExtra("isopen", true);

            startActivity(intent);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.tel_btn) {

                Tracker t = ((CashqApplication)getActivity().getApplication())
                        .getTracker(CashqApplication.TrackerName.APP_TRACKER);

                t.send(new HitBuilders.EventBuilder().setCategory("ShopListFragment")
                        .setAction("Press Button").setLabel("App Call").build());

                String num = v.getTag(R.id.num).toString();
                String name = v.getTag(R.id.name).toString();
                String img1 = v.getTag(R.id.img1).toString();
                String img2 = v.getTag(R.id.img2).toString();

                // PhoneCall.call(num, getActivity());

                checkContact(getActivity(), name, num);

                Intent call = new Intent(Intent.ACTION_CALL);
                call.putExtra(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        "디스플레이 네임");
                call.setData(Uri.parse("tel:" + num));

                startActivity(call);

                Intent menu = new Intent(new Intent(getActivity(), CallService.class));
                menu.putExtra("img1", img1);
                menu.putExtra("img2", img2);
                getActivity().startService(menu);
            }
        }
    };

    private boolean isBizHours(String t1, String t2) {

        try {

            String strNow = new SimpleDateFormat("HH00", Locale.KOREAN).format(new Date(System
                    .currentTimeMillis()));

            int nowTime = Integer.parseInt(strNow);

            t1 = t1.trim().replace(":", "");
            t2 = t2.trim().replace(":", "");

            int openTime = Integer.parseInt(t1);
            int closeTime = Integer.parseInt(t2);

            Log.e(TAG, "isBizHours : " + nowTime + " " + openTime + " " + closeTime);

            // TODO 시간 처리할것

            boolean afterOpen = openTime <= nowTime;
            boolean beforeClose = nowTime < closeTime;

            if (openTime < closeTime) {

                Log.e(TAG, "day - " + (afterOpen && beforeClose));
                return afterOpen && beforeClose;

            } else {
                Log.e(TAG, "next day - " + (afterOpen || beforeClose));
                return afterOpen || beforeClose;
            }

        } catch (NumberFormatException e) {
            // e.printStackTrace();
        }

        Log.e(TAG, "isBizHours true");
        return true;
    }
}
