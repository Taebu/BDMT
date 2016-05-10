
package com.wisepartners.dtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 9..
 */
public class OrderMenuListAdapter extends BaseAdapter {

    public OrderMenuListAdapter(Context context, ArrayList<CartData> datas) {
        mDataList = datas;

        inflater = LayoutInflater.from(context);
    }

    private ArrayList<CartData> mDataList;

    private LayoutInflater inflater = null;

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

        ViewHolder h;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_order, parent, false);

            h.name = (TextView)v.findViewById(R.id.row_order_menu);
            h.price = (TextView)v.findViewById(R.id.row_order_price);
            h.ea = (TextView)v.findViewById(R.id.row_order_ea);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        CartData item = (CartData)getItem(position);

        h.name.setText(item.getMenuName());

        h.price.setText(String.format("%,d원", item.getResultPrice()));

        h.ea.setText(String.valueOf(item.getEa()));

        return v;
    }

    private class ViewHolder {
        private TextView name, price, ea;
    }
}
