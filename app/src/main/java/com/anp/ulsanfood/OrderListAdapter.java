
package com.anp.ulsanfood;

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
public class OrderListAdapter extends BaseAdapter {

    public OrderListAdapter(Context context, ArrayList<OrderData> datas) {
        mDataList = datas;

        inflater = LayoutInflater.from(context);
    }

    private ArrayList<OrderData> mDataList;

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
            v = inflater.inflate(R.layout.row_orderlist, parent, false);

            h.price = (TextView) v.findViewById(R.id.price);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        OrderData item = (OrderData)getItem(position);

//        h.price.setText(String.format("%,d원", item.getTotal()));

        h.price.setText(item.getSimpleMenu());
        return v;
    }

    private class ViewHolder {
        private TextView name, price, ea;
    }
}
