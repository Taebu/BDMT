
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.cashqc.gcm.Util;

import static kr.co.cashqc.Utils.setListViewHeightBasedOnChildren;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 23..
 */
public class OrderListActivity extends BaseActivity {

    private Activity mThis = this;

    private TextView tvSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

//        tvSubject = (TextView)findViewById(R.id.orderlist_subject);
//        tvSubject.setText("캐시큐 주문 내역");

        String phoneNum = getIntent().getStringExtra("phoneNum");

        new OrderListTask().execute(phoneNum);

        findViewById(R.id.calllog_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.saveSharedPreferences_boolean(mThis, "orderlist_autologin", false);
                startActivity(new Intent(mThis, MainActivity.class));
            }
        });
    }

    private class OrderListTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String phoneNum = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/get_bedal_info.php").buildUpon()
                    .appendQueryParameter("mb_hp", phoneNum).build().toString();

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            ArrayList<OrderData> orderDataList = new ArrayList<OrderData>();

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                for (int i = 0; i < array.length(); i++) {

                    Log.e("OrderListActivty.OrderListTask", "array : " + array.getString(i));

                    JSONObject object = array.getJSONObject(i);

                    OrderData orderData = new OrderData();

                    if (object.has("seq"))
                        orderData.setNumber(Integer.parseInt(object.getString("seq")));

                    if (object.has("mb_hp"))
                        orderData.setUserPhone(object.getString("mb_hp"));

                    if (object.has("mb_zip"))
                        orderData.setZipCode(object.getString("mb_zip"));

                    if (object.has("mb_addr1"))
                        orderData.setAddress1(object.getString("mb_addr1"));

                    if (object.has("mb_addr2"))
                        orderData.setAddress1(object.getString("mb_addr2"));

                    if (object.has("mb_addr2"))
                        orderData.setAddress1(object.getString("mb_addr2"));

                    if (object.has("st_seq"))
                        orderData.setShopCode(object.getString("st_seq"));

                    if (object.has("st_name"))
                        orderData.setShopName(object.getString("st_name"));

                    if (object.has("st_phone"))
                        orderData.setShopPhone(object.getString("st_phone"));

                    if (object.has("st_vphone"))
                        orderData.setShopVPhone(object.getString("st_vphone"));

                    if (object.has("Tradeid"))
                        orderData.setTradeId(object.getString("Tradeid"));

                    if (object.has("pay_status"))
                        orderData.setPayStatus(object.getString("pay_status"));

                    if (object.has("ordmenu"))
                        orderData.setSimpleMenu(object.getString("ordmenu"));

                    if (object.has("appamount"))
                        orderData.setTotal(Integer.parseInt(object.getString("appamount")));

                    if(object.has("menucode"))
                        orderData.setMenuCode(object.getString("menucode"));

                    if(object.has("insdate"))
                        orderData.setDate(object.getString("insdate"));

                    if(object.has("comment"))
                        orderData.setComment(object.getString("comment"));

                    if(object.has("pay_type"))
                        orderData.setPayType(object.getString("pay_type"));

                    orderDataList.add(orderData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ListView listView = (ListView)findViewById(R.id.calllog_listview);

            OrderListAdapter adapter = new OrderListAdapter(mThis, orderDataList);

            listView.setAdapter(adapter);

            setListViewHeightBasedOnChildren(listView);

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
