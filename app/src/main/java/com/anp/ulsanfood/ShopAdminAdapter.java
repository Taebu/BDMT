
package com.anp.ulsanfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ShopAdminAdapter extends BaseExpandableListAdapter {

    public ShopAdminAdapter(Context context, ArrayList<OrderData> data) {
        super();

        mDataList = data;

        inflater = LayoutInflater.from(context);
    }

    private ArrayList<OrderData> mDataList;

    private LayoutInflater inflater = null;

    private ViewHolder h = null;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_order, parent, false);

            h.tvName = (TextView)v.findViewById(R.id.row_order_menu);
            h.tvPrice = (TextView)v.findViewById(R.id.row_order_price);
            h.tvEa = (TextView)v.findViewById(R.id.row_order_ea);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        CartData item = (CartData)getChild(groupPosition, childPosition);

        h.tvName.setText(item.getMenuName());

        h.tvPrice.setText(String.format("%,d원", item.getResultPrice()));

        h.tvEa.setText(String.valueOf(item.getEa()));

        return v;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_group_shopadmin, parent, false);

            h.ivSwitch = (ImageView)v.findViewById(R.id.row_group_shopadmin_switch);
            h.tvNum = (TextView)v.findViewById(R.id.row_group_shopadmin_num);
            h.tvDate = (TextView)v.findViewById(R.id.row_group_shopadmin_date);
            h.tvSimpleMenu = (TextView)v.findViewById(R.id.row_group_shopadmin_simplemenu);
            h.tvTotal = (TextView)v.findViewById(R.id.row_group_shopadmin_total);

            h.infoView = (LinearLayout)v.findViewById(R.id.row_group_shopadmin_info);

            h.ivLine = (ImageView)v.findViewById(R.id.row_group_shopadmin_line);

            h.tvShop = (TextView)v.findViewById(R.id.row_group_shopadmin_shop);
            h.tvPayType = (TextView)v.findViewById(R.id.row_group_shopadmin_payment);
            h.tvAddress1 = (TextView)v.findViewById(R.id.row_group_shopadmin_address1);
            h.tvAddress2 = (TextView)v.findViewById(R.id.row_group_shopadmin_address2);
            h.tvPhone = (TextView)v.findViewById(R.id.row_group_shopadmin_phone);
            h.tvComment = (TextView)v.findViewById(R.id.row_group_shopadmin_comment);

            v.setTag(h);

        } else {
            h = (ViewHolder)v.getTag();
        }

        OrderData item = (OrderData)getGroup(groupPosition);

        h.tvShop.setText(item.getShopName());
        h.tvPayType.setText(item.getPayType());
        h.tvAddress1.setText(item.getZipCode() + " " + item.getAddress1());
        h.tvAddress2.setText(item.getAddress2());
        h.tvPhone.setText(item.getUserPhone());
        h.tvComment.setText(item.getComment());

        if (isExpanded) {
            h.ivSwitch.setImageResource(R.drawable.btn_list_close);
            h.infoView.setVisibility(View.VISIBLE);
            h.ivLine.setVisibility(View.VISIBLE);
        } else {
            h.ivSwitch.setImageResource(R.drawable.btn_list_open);
            h.infoView.setVisibility(View.GONE);
            h.ivLine.setVisibility(View.GONE);
        }

        h.tvNum.setText(String.valueOf(item.getNumber()));
        h.tvDate.setText(item.getDate());
        h.tvSimpleMenu.setText(item.getSimpleMenu());
        h.tvTotal.setText(String.format("%,d원", item.getTotal()));

        return v;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDataList.get(groupPosition).getMenu().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDataList.get(groupPosition).getMenu().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mDataList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class ViewHolder {

        private ImageView ivSwitch, ivLine;

        private TextView tvNum, tvDate, tvSimpleMenu, tvTotal;

        private TextView tvShop, tvPayType, tvAddress1, tvAddress2, tvPhone, tvComment;

        private TextView tvName, tvPrice, tvEa;

        private LinearLayout infoView;

    }

}
