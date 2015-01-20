
package kr.co.cashqc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
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
            v = inflater.inflate(R.layout.row_child_shopadmin, null);
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        return v;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_group_shopadmin, parent, false);

            h.ivSwitch = (ImageView) v.findViewById(R.id.row_group_shopadmin_switch);
            h.tvNum = (TextView) v.findViewById(R.id.row_group_shopadmin_num);
            h.tvDate = (TextView) v.findViewById(R.id.row_group_shopadmin_date);
            h.tvSimpleMenu = (TextView) v.findViewById(R.id.row_group_shopadmin_simplemenu);
            h.tvTotal = (TextView) v.findViewById(R.id.row_group_shopadmin_total);

            v.setTag(h);

        } else {
            h = (ViewHolder)v.getTag();
        }

        if (isExpanded) {
            h.ivSwitch.setImageResource(R.drawable.btn_list_close);
        } else {
            h.ivSwitch.setImageResource(R.drawable.btn_list_open);
        }

        OrderData item = (OrderData) getGroup(groupPosition);

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

        private ImageView ivSwitch;

        private TextView tvNum, tvDate, tvSimpleMenu, tvTotal;

    }

}
