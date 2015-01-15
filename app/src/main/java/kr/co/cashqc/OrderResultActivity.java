
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

    private TextView tvResult, tvShop, tvShopTel, tvPayment, tvComment, tvAddress1, tvAddress2,
            tvPhone, tvTotal;

    private ListView mListView;

    private OrderListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_result);
        killer.addActivity(this);

        tvResult = (TextView)findViewById(R.id.order_result_text);

        tvTotal = (TextView) findViewById(R.id.order_result_text);

        findViewById(R.id.btn_order_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        ArrayList<CartData> dataList = (ArrayList<CartData>)getIntent().getSerializableExtra("cart");

        mListView = (ListView)findViewById(R.id.order_result_listview);

        mAdapter = new OrderListAdapter(this, dataList);

        mListView.setAdapter(mAdapter);

        setListViewHeightBasedOnChildren(mListView);

        int total = 0;
        for (int i = 0; i < dataList.size(); i++) {
            total += dataList.get(i).getEa() * dataList.get(i).getPrice();
        }

        String result = String.format("%,d원", total);
        tvTotal.setText(result);
    }

    public void mOnClick(View view) {

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
