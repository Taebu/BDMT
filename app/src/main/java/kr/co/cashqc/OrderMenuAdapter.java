
package kr.co.cashqc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 24..
 */

public class OrderMenuAdapter extends BaseAdapter {

    public OrderMenuAdapter(Context context, MenuData data) {

        inflater = LayoutInflater.from(context);

        mData = data;
    }

    private MenuData mData;

    private LayoutInflater inflater;

    @Override
    public int getCount() {
        return mData.getChild().size();
    }

    @Override
    public Object getItem(int position) {
        return mData.getChild().get(position);
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
            v = inflater.inflate(R.layout.row_menu, null);

            h.radio = (RadioButton)v.findViewById(R.id.dialog_order_radio);
            h.menu = (TextView)v.findViewById(R.id.dialog_order_menu);
            h.price = (TextView)v.findViewById(R.id.dialog_order_price);

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

        MenuData item = (MenuData)getItem(position);

        h.radio.setFocusable(false);
        h.radio.setClickable(false);
        h.radio.setTag(position);

        if ((Integer)h.radio.getTag() == ((ListView)parent).getCheckedItemPosition()) {

        }

        h.radio.setChecked(((ListView)parent).isItemChecked((Integer)h.radio.getTag()));

        h.menu.setText(item.getLabel());

        String price = String.format("+ %,d 원", Integer.parseInt(item.getPrice()));

        h.price.setText(price);

        return v;
    }

    private class ViewHolder {
        private RadioButton radio;

        private TextView menu, price;
    }
}
