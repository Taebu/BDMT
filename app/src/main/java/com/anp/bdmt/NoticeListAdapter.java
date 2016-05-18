
package com.anp.bdmt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class NoticeListAdapter extends BaseExpandableListAdapter {

    public NoticeListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        super();

        mData = data;

        inflater = LayoutInflater.from(context);

    }

    private ArrayList<HashMap<String, String>> mData;

    private LayoutInflater inflater = null;

    private ViewHolder h = null;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.list_menuexpand_row, null);
            h.tvChildName = (TextView)v.findViewById(R.id.tv_child);
            h.tvChildValue = (TextView)v.findViewById(R.id.tv_price);
            h.ivThumb = (ImageView)v.findViewById(R.id.iv_thumb);
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        String content = (String)getChild(groupPosition, childPosition);

        h.tvChildName.setText(content);

        return v;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.list_menu_row, parent, false);
            h.tvGroupName = (TextView)v.findViewById(R.id.tv_group);
            h.ivImage = (ImageView)v.findViewById(R.id.iv_image);
            h.ivIndicator = (ImageView)v.findViewById(R.id.iv_indicator);
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        if (isExpanded) {
            // h.ivImage.setBackgroundColor(Color.GREEN);dh
            h.ivIndicator.setImageResource(R.drawable.btn_list_close);
        } else {
            // h.ivImage.setBackgroundColor(Color.WHITE);
            h.ivIndicator.setImageResource(R.drawable.btn_list_open);
        }

        String subject = (String)getGroup(groupPosition);

        h.tvGroupName.setText(subject);

        return v;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).get("content");
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition).get("subject");
    }

    @Override
    public int getGroupCount() {
        return mData.size();
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
        private ImageView ivImage, ivThumb, ivIndicator;

        private TextView tvGroupName;

        private TextView tvChildName;

        private TextView tvChildValue;
    }

}
