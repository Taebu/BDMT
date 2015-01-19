
package kr.co.cashqc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 19..
 */
public class ShopAdminActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopadmin);

        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        ExpandableListView listView = (ExpandableListView)findViewById(R.id.shopadmin_listview);

        String seq = "6867";

        new ShopAdminTask(listView).execute(seq);
    }

    private class ShopAdminTask extends AsyncTask<String, Void, JSONObject> {

        private ShopAdminTask(ExpandableListView listView) {
            this.listView = listView;
        }

        private ExpandableListView listView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String seq = params[0];

            String url = "http://cashq.co.kr/m/ajax_data/get_ordtake.php?seq=" + seq;

            return new JSONParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            ArrayList<OrderData> dataList = getOrderList(jsonObject);

            listView.setAdapter(new ShopAdminAdapter(getApplicationContext(), dataList));

            setListViewHeight(listView);

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        private ArrayList<OrderData> getOrderList(JSONObject jsonObject) {

            ArrayList<OrderData> orderDataList = new ArrayList<OrderData>();

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                OrderData orderData = new OrderData();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject orderObject = array.getJSONObject(i);

                    orderData.setDate(orderObject.getString("insdate"));

                    orderData.setTotal(Integer.parseInt(orderObject.getString("appamount")));

                    orderData.setShopName(orderObject.getString("st_name"));

                    orderData.setPayType(orderObject.getString("pay_type"));

                    orderData.setZipCode(orderObject.getString("mb_zip"));

                    orderData.setAddress1(orderObject.getString("mb_addr1"));

                    orderData.setAddress2(orderObject.getString("mb_addr2"));

                    orderData.setUserPhone(orderObject.getString("mb_hp"));

                    orderData.setComment(orderObject.getString("comment"));

                    ArrayList<CartData> cartDataList = new ArrayList<CartData>();
                    CartData cartData = new CartData();
                    int total = 0;
                    String[] menus = orderObject.getString("ordmenu").split("&");

                    for (int y = 0; y < menus.length; y++) {

                        String[] menu = menus[y].split("_");

                        cartData.setMenuName(menu[0]);
                        // cartData.setEa(Integer.parseInt(menu[1]));
                        // cartData.setPrice(Integer.parseInt(menu[2]));

                        cartDataList.add(cartData);

                    }

                    orderData.setMenu(cartDataList);

                    String menu = orderData.getMenu().get(0).getMenuName();
                    int quantity = orderData.getMenu().size() - 1;
                    String simpleMenu = quantity < 1 ? menu : menu + " 외 " + quantity + "건";

                    orderData.setSimpleMenu(simpleMenu);

                    orderDataList.add(orderData);

                }

                return orderDataList;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private void setListViewHeight(ExpandableListView listView) {
        ShopAdminAdapter adapter = (ShopAdminAdapter) listView.getExpandableListAdapter();

        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            View listItem = adapter.getGroupView(i, false, listView.getChildAt(0), listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getGroupCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
