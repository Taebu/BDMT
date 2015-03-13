
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static kr.co.cashqc.gcm.Util.loadSharedPreferences;
import static kr.co.cashqc.gcm.Util.saveSharedPreferences_string;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 31..
 */
public class OrderActivity extends BaseActivity {

    private Activity mThis = this;

    private String mPayType = "NOT_CHOOSE";

    private final String NOT_CHOOSE = "NOT_CHOOSE";

    private final String CASHQ_CARD = "CASHQ_CARD";

    private final String CASHQ_CELL = "CASHQ_CELL";

    private final String FIELD_CASH = "FIELD_CASH";

    private final String FIELD_CARD = "FIELD_CARD";

    private TextView rbCashqCard, rbCashqPhone, rbSiteCash, rbSiteCard, tvTotal, tvShopName,
            tvShopPhone;

    private EditText etZipCode, etAddress1, etAddress2, etPhone, etComment;

    private CheckBox cbMember;

    private Button btnOrder;

    private OrderData mOrderData;

    private ArrayList<CartData> mCartList;

    private ListView mListView;

    private OrderListAdapter mAdapter;

    private Button btnAddressBook;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String zipCode = (String)v.getTag(R.id.zipcode);
            String address1 = (String)v.getTag(R.id.address1);
            String address2 = (String)v.getTag(R.id.address2);

            etZipCode.setText(zipCode);
            etAddress1.setText(address1);
            etAddress2.setText(address2);
            Log.e("order", zipCode + address1 + address2);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        killer.addActivity(this);

        mDialog = new CustomDialog(this);

        mOrderData = (OrderData)getIntent().getSerializableExtra("order");

        mCartList = mOrderData.getMenu();

        setView();

        setListener();

        mAdapter = new OrderListAdapter(this, mCartList);

        mListView = (ListView)findViewById(R.id.order_listview);
        mListView.setAdapter(mAdapter);

        setListViewHeightBasedOnChildren(mListView);

        tvShopName.setText(mOrderData.getShopName() + "   ");
        tvShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO call
            }
        });

        tvShopPhone.setText(mOrderData.getShopPhone());
        tvTotal.setText(String.format("%,d원", mOrderData.getTotal()));

    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // mAdapter = listView.getAdapter();

        if (mAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View listItem = mAdapter.getView(i, listView.getChildAt(0), listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void setListener() {

        findViewById(R.id.order_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddressListDialog(OrderActivity.this, mOnClickListener).show();
            }
        });

        btnAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> addressList = loadAddressBookList();

                if (addressList.get(0) == null) {
                    Toast.makeText(mThis, "최근 사용한 주소가 없습니다.", Toast.LENGTH_LONG).show();
                } else {
                    new AddressBookDialog(mThis, addressList, mOnClickListener).show();
                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPayType.equals(NOT_CHOOSE)) {
                    Toast.makeText(mThis, "결제 방법을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (etZipCode.getText().toString().equals("")) {
                    Toast.makeText(mThis, "우편번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (etAddress1.getText().toString().equals("")
                        || etAddress2.getText().toString().equals("")) {
                    Toast.makeText(mThis, "주소를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (etPhone.getText().toString().equals("")) {
                    Toast.makeText(mThis, "연락처를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    String address = etZipCode.getText().toString() + "_"
                            + etAddress1.getText().toString() + "_"
                            + etAddress2.getText().toString();

                    saveAddressBookList(address);

                    mOrderData.setZipCode(etZipCode.getText().toString());
                    mOrderData.setAddress1(etAddress1.getText().toString());
                    mOrderData.setAddress2(etAddress2.getText().toString());
                    mOrderData.setUserPhone(etPhone.getText().toString());
                    mOrderData.setComment(etComment.getText().toString());
                    mOrderData.setPayType(mPayType);

                    new OrderTask().execute(mOrderData);
                }
            }
        });
    }

    private ArrayList<String> loadAddressBookList() {

        ArrayList<String> addressList = new ArrayList<String>();

        for (int i = 0; i < 5; i++) {
            String a = loadSharedPreferences(mThis, "addressBook" + i);
            Log.e("sharedPreferences : ", "a : " + a);
            Log.e("sharedPreferences : ", "addressBook" + i);
            if (a != null) {
                addressList.add(a);
            }
        }

        return addressList;
    }

    private void saveAddressBookList(String address) {

        ArrayList<String> addressList = loadAddressBookList();

        boolean isDuplicate = false;

        for (int i = 0; i < 5; i++) {

            Log.e("i :", "" + i);
            Log.e("address : ", address);
            Log.e("address list  : ", addressList.get(i));

            if (address.equals(addressList.get(i))) {
                isDuplicate = true;
                Log.e("!!true :", "!!");
                break;
            } else {
                isDuplicate = false;
                Log.e("!!false :", "!!");
                break;
            }
        }

        if (!isDuplicate) {
            addressList.add(address);
            for (int i = 0; i < addressList.size(); i++) {
                saveSharedPreferences_string(mThis, "addressBook" + (i + 1), addressList.get(i));
            }
        }
    }

    private void setView() {

        etZipCode = (EditText)findViewById(R.id.order_zipcode);
        etAddress1 = (EditText)findViewById(R.id.order_address1);
        etAddress2 = (EditText)findViewById(R.id.order_address2);
        etPhone = (EditText)findViewById(R.id.order_phone);
        etPhone.setText(getPhoneNumber());
        etComment = (EditText)findViewById(R.id.order_comment);

        rbCashqCard = (TextView)findViewById(R.id.order_cashq_card);
        rbCashqPhone = (TextView)findViewById(R.id.order_cashq_phone);

        rbSiteCash = (TextView)findViewById(R.id.order_site_cash);
        rbSiteCard = (TextView)findViewById(R.id.order_site_card);

        btnOrder = (Button)findViewById(R.id.btn_order_pay);

        tvTotal = (TextView)findViewById(R.id.order_total);
        tvShopName = (TextView)findViewById(R.id.order_shopname);
        tvShopPhone = (TextView)findViewById(R.id.order_shopphone);

        cbMember = (CheckBox)findViewById(R.id.order_member_address);

        btnAddressBook = (Button)findViewById(R.id.order_addressbook);
    }

    public void mOnClick(View view) {

        rbCashqCard.setTextColor(Color.parseColor("#CCCCCC"));
        rbCashqPhone.setTextColor(Color.parseColor("#CCCCCC"));
        rbSiteCash.setTextColor(Color.parseColor("#CCCCCC"));
        rbSiteCard.setTextColor(Color.parseColor("#CCCCCC"));

        rbCashqCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_card_off, 0, 0);
        rbCashqPhone.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_phone_off, 0, 0);
        rbSiteCash.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_cash_off, 0, 0);
        rbSiteCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_card_off, 0, 0);

        switch (view.getId()) {
            case R.id.order_cashq_card:
                btnOrder.setText("결제 하기");
                mPayType = CASHQ_CARD;
                rbCashqCard.setTextColor(Color.parseColor("#E94230"));
                rbCashqCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_card_on,
                        0, 0);
                break;
            case R.id.order_cashq_phone:
                btnOrder.setText("결제 하기");
                mPayType = CASHQ_CELL;
                rbCashqPhone.setTextColor(Color.parseColor("#E94230"));
                rbCashqPhone.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.btn_pay_phone_on, 0, 0);
                break;
            case R.id.order_site_cash:
                btnOrder.setText("주문 하기");
                mPayType = FIELD_CASH;
                rbSiteCash.setTextColor(Color.parseColor("#E94230"));
                rbSiteCash.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_cash_on,
                        0, 0);
                break;
            case R.id.order_site_card:
                btnOrder.setText("주문 하기");
                mPayType = FIELD_CARD;
                rbSiteCard.setTextColor(Color.parseColor("#E94230"));
                rbSiteCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_card_on,
                        0, 0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private class OrderTask extends AsyncTask<OrderData, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(OrderData... params) {

            OrderData data = params[0];

            Uri.Builder ub = Uri.parse("http://cashq.co.kr/m/ajax_data/set_ordtake.php")
                    .buildUpon().appendQueryParameter("mb_zip", data.getZipCode())
                    .appendQueryParameter("mb_addr1", data.getAddress1())
                    .appendQueryParameter("mb_addr2", data.getAddress2())
                    .appendQueryParameter("mb_hp", data.getUserPhone())
                    .appendQueryParameter("comment", data.getComment())
                    .appendQueryParameter("st_seq", data.getShopCode())
                    .appendQueryParameter("st_name", data.getShopName())
                    .appendQueryParameter("st_phone", data.getShopPhone())
                    .appendQueryParameter("st_vphone", data.getShopVPhone())
                    .appendQueryParameter("pay_type", data.getPayType())
                    .appendQueryParameter("appamount", String.valueOf(data.getTotal()));

            for (CartData c : data.getMenu()) {
                ub.appendQueryParameter("ordmenu[]",
                        c.getMenuName() + "_" + c.getEa() + "_" + c.getPrice())
                        .appendQueryParameter("menucode[]", c.getMenuCode());
            }

            String url = ub.build().toString();

            Log.e("order", "url : " + url);

            return new JSONParser().getJSONObjectFromUrl(url);
            // return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (mDialog.isShowing())
                mDialog.dismiss();

            Log.e("order", "json result : " + jsonObject.toString());

            try {
                mOrderData.setTradeId(jsonObject.getString("Tradeid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent i = new Intent();

            if (mOrderData.getPayType().equals(NOT_CHOOSE)) {
                // ???
            } else if (mOrderData.getPayType().equals(CASHQ_CARD)
                    || mOrderData.getPayType().equals(CASHQ_CELL)) {
                i.setClass(mThis, PayActivity.class);

            } else if (mOrderData.getPayType().equals(FIELD_CASH)
                    || mOrderData.getPayType().equals(FIELD_CARD)) {
                i.setClass(mThis, OrderResultActivity.class);
            }

            i.putExtra("pay_type", mPayType);
            i.putExtra("order", mOrderData);
            startActivity(i);
        }
    }

}
