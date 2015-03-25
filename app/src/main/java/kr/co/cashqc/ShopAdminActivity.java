
package kr.co.cashqc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static kr.co.cashqc.Utils.setExpandableListViewHeight;

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

            setExpandableListViewHeight(listView, 0);

            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                        long id) {
                    setExpandableListViewHeight(parent, groupPosition);
                    return false;
                }
            });

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        private ArrayList<OrderData> getOrderList(JSONObject jsonObject) {

            ArrayList<OrderData> orderDataList = new ArrayList<OrderData>();

            try {

                JSONArray array = jsonObject.getJSONArray("posts");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject orderObject = array.getJSONObject(i);

                    if (true) {
                        // if (orderObject.getString("seq").equals("2")) {

                        OrderData orderData = new OrderData();

                        // orderData.setNumber(i);

                        orderData.setNumber(Integer.parseInt(orderObject.getString("seq")));

                        orderData.setDate(orderObject.getString("insdate").replace(" ", "\n"));

                        orderData.setTotal(Integer.parseInt(orderObject.getString("appamount")));

                        orderData.setShopName(orderObject.getString("st_name"));

                        orderData.setPayType(orderObject.getString("pay_type"));

                        orderData.setZipCode(orderObject.getString("mb_zip"));

                        orderData.setAddress1(orderObject.getString("mb_addr1"));

                        orderData.setAddress2(orderObject.getString("mb_addr2"));

                        orderData.setUserPhone(orderObject.getString("mb_hp"));

                        orderData.setComment(orderObject.getString("comment"));

                        ArrayList<CartData> cartDataList = new ArrayList<CartData>();

                        int total = 0;

                        String[] menus = orderObject.getString("ordmenu").split("&");
                        Log.e("shopadmin", "menus : " + Arrays.toString(menus));
                        for (int y = 0; y < menus.length; y++) {
                            CartData cartData = new CartData();
                            String[] menu = menus[y].split("_");
                            Log.e("shopadmin", "menu : " + Arrays.toString(menu));

                            try {
                                cartData.setMenuName(menu[0]);
                                cartData.setEa(Integer.parseInt(menu[1]));
                                cartData.setPrice(Integer.parseInt(menu[2]));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            // Log.e("shopadmin", "\ncartData : " +
                            // cartData.getMenuName() + "\nea : "
                            // + cartData.getEa() + "\nprice : " +
                            // cartData.getMenuName());
                            cartDataList.add(cartData);
                        }

                        orderData.setMenu(cartDataList);

                        String menu = orderData.getMenu().get(0).getMenuName();
                        int quantity = orderData.getMenu().size() - 1;
                        String simpleMenu = quantity < 1 ? menu : menu + " 외 " + quantity + "건";

                        orderData.setSimpleMenu(simpleMenu);

                        orderDataList.add(orderData);
                    }
                }

                return orderDataList;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    // private void setListViewHeight(ExpandableListView listView) {
    //
    // ShopAdminAdapter adapter =
    // (ShopAdminAdapter)listView.getExpandableListAdapter();
    //
    // if (adapter == null) {
    // return;
    // }
    //
    // int totalHeight = 0;
    // for (int i = 0; i < adapter.getGroupCount(); i++) {
    // View listItem = adapter.getGroupView(i, false, listView.getChildAt(0),
    // listView);
    // listItem.measure(0, 0);
    // totalHeight += listItem.getMeasuredHeight();
    // }
    //
    // ViewGroup.LayoutParams params = listView.getLayoutParams();
    // params.height = totalHeight + (listView.getDividerHeight() *
    // (adapter.getGroupCount() - 1));
    // listView.setLayoutParams(params);
    // listView.requestLayout();
    // }
}
