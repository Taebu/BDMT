
package com.anp.bdmt;

import static com.anp.bdmt.DataBaseOpenHelper.TABLE_NAME;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 24..
 */
public class CartActivity extends BaseActivity {

    private SQLiteDatabase mDatabase;

    private DataBaseOpenHelper mHelper;

    private TextView tvShop, tvTotal;

    private CartListAdapter mAdapter;

    private OrderData mOrderData;

    private ArrayList<CartData> mCartList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        killer.removeActivity(this);
        killer.addActivity(this);

        findViewById(R.id.btn_cart).setVisibility(View.INVISIBLE);
        TV_CART_COUNT.setVisibility(View.INVISIBLE);

        mHelper = new DataBaseOpenHelper(this);

        tvShop = (TextView)findViewById(R.id.cart_shopname);
        tvTotal = (TextView)findViewById(R.id.cart_total);

        mOrderData = select();

        mCartList = mOrderData.getMenu();

        tvShop.setText(mOrderData.getShopName() + "\ntel: " + mOrderData.getShopPhone());

        setTotal();

        ListView listView = (ListView)findViewById(R.id.cart_list);

        mAdapter = new CartListAdapter(this, mCartList, mOnClickListener);

        listView.setAdapter(mAdapter);

        findViewById(R.id.btn_cart_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCartList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "장바구니가 비어있어요~", Toast.LENGTH_SHORT).show();
                } else if (mOrderData.getTotal() < 12000) {
                    Toast.makeText(CartActivity.this, "12,000원 이상 주문만 가능합니다", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                    intent.putExtra("order", mOrderData);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    public void update(String menuCode, int ea) {
        mDatabase = mHelper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기가능

        ContentValues values = new ContentValues();
        values.put("ea", ea); // ea 값을 수정

        mDatabase.update(TABLE_NAME, values, "menu_code='" + menuCode + "'", null);
    }

    private void insert(String name, int age, String address) {
        mDatabase = mHelper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("name", name);
        values.put("age", age);
        values.put("address", address);

        mDatabase.insert(TABLE_NAME, null, values); // 테이블/널컬럼핵/데이터 (널컬럼핵=디폴트)
    }

    public void delete(String menuCode) {
        mDatabase = mHelper.getWritableDatabase();
        mDatabase.delete(TABLE_NAME, "menu_code='" + menuCode + "'", null);
        mHelper.close();
    }

    public OrderData select() {

        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용

        mDatabase = mHelper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = mDatabase.query(TABLE_NAME, null, null, null, null, null, null);

        /*
         * 위 결과는 select * from student 가 된다. Cursor는 DB결과를 저장한다. public Cursor
         * query (String table, String[] columns, String selection, String[]
         * selectionArgs, String groupBy, String having, String orderBy)
         */

        OrderData orderData = new OrderData();

        ArrayList<CartData> list = new ArrayList<CartData>();

        while (c.moveToNext()) {

            orderData.setShopPhone(c.getString(c.getColumnIndex("shop_phone")));
            orderData.setShopName(c.getString(c.getColumnIndex("shop_name")));
            orderData.setShopCode(c.getString(c.getColumnIndex("shop_code")));
            orderData.setShopVPhone(c.getString(c.getColumnIndex("shop_vphone")));

            CartData data = new CartData();

            int ea = c.getInt(c.getColumnIndex("ea"));
            int price = c.getInt(c.getColumnIndex("price"));

            data.setEa(ea);
            data.setPrice(price);

            data.setMenuCode(c.getString(c.getColumnIndex("menu_code")));
            data.setMenuName(c.getString(c.getColumnIndex("menu_name")));

            list.add(data);
        }

        orderData.setMenu(list);

        mHelper.close();
        c.close();

        return orderData;
    }

    public void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.cart_add:

                break;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (Integer)v.getTag();
            String menuCode = mCartList.get(position).getMenuCode();

            switch (v.getId()) {
                case R.id.row_cart_plus:
                    mCartList.get(position).plusEa();
                    update(menuCode, mCartList.get(position).getEa());
                    break;
                case R.id.row_cart_minus:
                    mCartList.get(position).minusEa();
                    update(menuCode, mCartList.get(position).getEa());
                    break;
                case R.id.row_cart_remove:
                    delete(menuCode);
                    mCartList.remove(position);
                    setCartCount(CartActivity.this);
                    break;
            }

            mAdapter.notifyDataSetChanged();
            mOrderData.setMenu(mCartList);
            setTotal();
        }
    };

    private void setTotal() {
        tvTotal.setText("총 주문 금액 " + String.format("%,d원", mOrderData.getTotal()));
    }
}
