
package kr.co.cashqc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 31..
 */
public class OrderActivity extends BaseActivity implements DialogInterface.OnDismissListener {

    private Activity mActivity = this;

    private int mPayment = 0;

    private final int NOT_CHOOSE = 0;

    private final int CASHQ_CARD = 1;

    private final int CASHQ_PHONE = 2;

    private final int SITE_CASH = 3;

    private final int SITE_CARD = 4;

    private TextView rbCashqCard, rbCashqPhone, rbSiteCash, rbSiteCard, tvTotal;

    private EditText etZipCode, etAddress1, etAddress2, etPhone, etComment;

    private Button btnOrder;

    private ArrayList<CartData> mCartList;

    private ListView mListView;

    private OrderListAdapter mAdapter;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String zipCode = (String)v.getTag(R.id.zipcode);
            String address = (String)v.getTag(R.id.address1);

            etZipCode.setText(zipCode);
            etAddress1.setText(address);
            Log.e("order", zipCode + address);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        killer.addActivity(this);

        setView();///

        setListener();

        mCartList = (ArrayList<CartData>)getIntent().getSerializableExtra("cart");

        mAdapter = new OrderListAdapter(this, mCartList);

        mListView = (ListView)findViewById(R.id.order_listview);
        mListView.setAdapter(mAdapter);

        setListViewHeightBasedOnChildren(mListView);

        int total = 0;
        for (int i = 0; i < mCartList.size(); i++) {
            total += mCartList.get(i).getEa() * mCartList.get(i).getPrice();
        }

        String result = String.format("%,d원", total);
        tvTotal.setText(result);

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

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPayment == NOT_CHOOSE) {
                    Toast.makeText(mActivity, "결제 방법을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (etAddress1.getText().toString().equals("")
                        || etAddress2.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "주소를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (etPhone.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "연락처를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    switch (mPayment) {
                        case CASHQ_CARD:
                            intent = new Intent(mActivity, PayActivity.class);
                            intent.putExtra("pay", "card");
                            break;
                        case CASHQ_PHONE:
                            intent = new Intent(mActivity, PayActivity.class);
                            intent.putExtra("pay", "phone");
                            break;
                        case SITE_CASH:
                            intent = new Intent(mActivity, OrderResultActivity.class);
                            intent.putExtra("cart", mCartList);
                            break;
                        case SITE_CARD:
                            intent = new Intent(mActivity, OrderResultActivity.class);
                            intent.putExtra("cart", mCartList);
                            break;
                    }
                    startActivity(intent);
                }
            }
        });
    }

    private void setView() {

        etZipCode = (EditText)findViewById(R.id.order_zipcode);
        etAddress1 = (EditText)findViewById(R.id.order_address1);
        etAddress2 = (EditText)findViewById(R.id.order_address2);
        etPhone = (EditText)findViewById(R.id.order_phone);
        etComment = (EditText)findViewById(R.id.order_comment);

        rbCashqCard = (TextView)findViewById(R.id.order_cashq_card);
        rbCashqPhone = (TextView)findViewById(R.id.order_cashq_phone);

        rbSiteCash = (TextView)findViewById(R.id.order_site_cash);
        rbSiteCard = (TextView)findViewById(R.id.order_site_card);

        btnOrder = (Button)findViewById(R.id.btn_order_pay);

        tvTotal = (TextView)findViewById(R.id.order_total);
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
                mPayment = CASHQ_CARD;
                rbCashqCard.setTextColor(Color.parseColor("#E94230"));
                rbCashqCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_card_on,
                        0, 0);
                break;
            case R.id.order_cashq_phone:
                btnOrder.setText("결제 하기");
                mPayment = CASHQ_PHONE;
                rbCashqPhone.setTextColor(Color.parseColor("#E94230"));
                rbCashqPhone.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.btn_pay_phone_on, 0, 0);
                break;
            case R.id.order_site_cash:
                btnOrder.setText("주문 하기");
                mPayment = SITE_CASH;
                rbSiteCash.setTextColor(Color.parseColor("#E94230"));
                rbSiteCash.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_pay_cash_on,
                        0, 0);
                break;
            case R.id.order_site_card:
                btnOrder.setText("주문 하기");
                mPayment = SITE_CARD;
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

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

}
