
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
 * @author Jung-Hum Cho Created by anp on 14. 12. 24..
 */

public class AddressBookAdapter extends BaseAdapter {

    public AddressBookAdapter(Context context, ArrayList<String> addressList) {

        inflater = LayoutInflater.from(context);

        mAddressList = addressList;
    }

    private LayoutInflater inflater;

    private ArrayList<String> mAddressList;

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAddressList.get(position);
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
            v = inflater.inflate(R.layout.row_addressbook, null);

            h.radio = (RadioButton)v.findViewById(R.id.dialog_addressbook_radio);
            h.menu = (TextView)v.findViewById(R.id.dialog_addressbook_menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((ListView)parent).setItemChecked((Integer)h.radio.getTag(), true);

                }
            });

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        String address = (String)getItem(position);

        h.radio.setFocusable(false);
        h.radio.setClickable(false);
        h.radio.setTag(position);

//        if ((Integer)h.radio.getTag() == ((ListView)parent).getCheckedItemPosition()) {}

        h.radio.setChecked(((ListView)parent).isItemChecked((Integer)h.radio.getTag()));

        String[] splitAddress;
        try {
            splitAddress = address.split("_");
            h.menu.setText(splitAddress[1] + "\n" + splitAddress[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // h.price.setText(item.getPrice());

        return v;
    }

    private class ViewHolder {
        private RadioButton radio;

        private TextView menu, price;
    }
}
