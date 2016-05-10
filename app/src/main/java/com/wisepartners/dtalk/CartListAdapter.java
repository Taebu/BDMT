
package com.wisepartners.dtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 5..
 */
public class CartListAdapter extends BaseAdapter {

    public CartListAdapter(Context context, ArrayList<CartData> dataList,
            View.OnClickListener onClickListener) {

        mOnClickListener = onClickListener;

        inflater = LayoutInflater.from(context);

        mDataList = dataList;

    }

    private View.OnClickListener mOnClickListener = null;

    private LayoutInflater inflater;

    private ArrayList<CartData> mDataList;

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final ViewHolder h;

        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_cart, null);

            h.menu = (TextView)v.findViewById(R.id.row_cart_order);
            h.price = (TextView)v.findViewById(R.id.row_cart_price);
            h.ea = (TextView)v.findViewById(R.id.row_cart_ea);
            h.remove = (TextView)v.findViewById(R.id.row_cart_remove);

            h.plus = (Button)v.findViewById(R.id.row_cart_plus);
            h.minus = (Button)v.findViewById(R.id.row_cart_minus);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        CartData item = (CartData)getItem(position);

        // h.plus.setFocusable(false);
        if (mOnClickListener != null) {
            h.plus.setTag(position);
            h.plus.setOnClickListener(mOnClickListener);
            h.minus.setTag(position);
            h.minus.setOnClickListener(mOnClickListener);
            h.remove.setTag(position);
            h.remove.setOnClickListener(mOnClickListener);
        }

        h.ea.setText(String.valueOf(item.getEa()));

//        h.parent.setText(item.getParentName());

        h.menu.setText(item.getMenuName());

        h.price.setText(String.format("%,d원", item.getResultPrice()));

        return v;
    }

    private class ViewHolder {
        private TextView menu, price, ea, remove;

        private Button plus, minus;

    }
}
