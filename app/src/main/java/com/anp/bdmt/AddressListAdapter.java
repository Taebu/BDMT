
package com.anp.bdmt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 2..
 */
public class AddressListAdapter extends BaseAdapter {

    public AddressListAdapter(Context context, ArrayList<AddressData> datas, View.OnClickListener onClickListener) {
        super();

        mOnClickListener = onClickListener;

        mDataList = datas;

        inflater = LayoutInflater.from(context);
    }

    private View.OnClickListener mOnClickListener = null;

    private ArrayList<AddressData> mDataList;

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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v = convertView;

        final ViewHolder h;

        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_address, null);

            h.radio = (RadioButton)v.findViewById(R.id.row_address_radio);
            h.address = (TextView)v.findViewById(R.id.row_address);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView)parent).setItemChecked((Integer) h.radio.getTag(), true);
                }
            });

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        AddressData item = (AddressData) getItem(position);

        h.radio.setFocusable(false);
        h.radio.setClickable(false);
        h.radio.setTag(position);
        h.radio.setChecked(((ListView)parent).isItemChecked((Integer) h.radio.getTag()));

//        if(mOnClickListener != null) {
//            h.radio.setTag(R.id.zipcode, item.getZipcode());
//            h.radio.setTag(R.id.address1, item.getAddress());
//            h.radio.setOnClickListener(mOnClickListener);
//        }

        h.address.setText(item.getResult());

        return v;
    }

    private class ViewHolder {
        private TextView address;

        private RadioButton radio;
    }
}
