
package kr.co.cashqc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 7..
 */
public class OrderResultActivity extends BaseActivity {

    private TextView tvResult, tvShop, tvPayment, tvComment, tvAddress1, tvAddress2, tvPhone,
            tvTotal;

    private ListView mListView;

    private OrderListAdapter mAdapter;

    private OrderData mOrderData;

    private ArrayList<CartData> mCartList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_result);
        killer.addActivity(this);

        setView();
        setData();

    }

    private void setView() {
        tvResult = (TextView)findViewById(R.id.order_result_text);
        tvShop = (TextView)findViewById(R.id.order_result_shop);
        tvPayment = (TextView)findViewById(R.id.order_result_payment);
        tvAddress1 = (TextView)findViewById(R.id.order_result_address1);
        tvAddress2 = (TextView)findViewById(R.id.order_result_address2);
        tvPhone = (TextView)findViewById(R.id.order_result_phone);
        tvComment = (TextView)findViewById(R.id.order_result_comment);
        tvTotal = (TextView)findViewById(R.id.order_result_total);
    }

    private void setData() {

        mOrderData = (OrderData)getIntent().getSerializableExtra("order");

        if (mOrderData != null) {
            mCartList = mOrderData.getMenu();
            tvShop.setText(mOrderData.getShopName());

            tvShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            tvPayment.setText(mOrderData.getPayType());

            tvAddress1.setText(mOrderData.getZipCode() + " " + mOrderData.getAddress1());

            tvAddress2.setText(mOrderData.getAddress2());

            tvPhone.setText(mOrderData.getUserPhone());

            tvComment.setText(mOrderData.getComment());

            mListView = (ListView)findViewById(R.id.order_result_listview);

            mAdapter = new OrderListAdapter(this, mCartList);

            mListView.setAdapter(mAdapter);

            setListViewHeightBasedOnChildren(mListView);

            tvTotal.setText(String.format("%,d원", mOrderData.getTotal()));
        }

        findViewById(R.id.btn_order_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
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
}
