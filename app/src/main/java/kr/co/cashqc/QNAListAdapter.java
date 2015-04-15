
package kr.co.cashqc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class QNAListAdapter extends BaseExpandableListAdapter {

    public QNAListAdapter(Context context, ArrayList<BoardData> data) {
        super();

        mData = data;

        inflater = LayoutInflater.from(context);

    }

    private ArrayList<BoardData> mData;

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
            h.tvChildValue = (TextView)v.findViewById(R.id.tv_value);
            h.ivThumb = (ImageView)v.findViewById(R.id.iv_thumb);
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        BoardData item = (BoardData)getChild(groupPosition, childPosition);

        h.tvChildName.setText(item.getName() + " " + item.getPhone() + "\n" + item.getDatetime()
                + "\n" + item.getSubject() + "\n" + item.getContent());

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
            // h.ivImage.setBackgroundColor(Color.GREEN);
            h.ivIndicator.setImageResource(R.drawable.btn_list_close);
        } else {
            // h.ivImage.setBackgroundColor(Color.WHITE);
            h.ivIndicator.setImageResource(R.drawable.btn_list_open);
        }

        BoardData item = (BoardData)getGroup(groupPosition);

        h.tvGroupName.setText(item.getName() + " " + item.getPhone() + "\n" + item.getDatetime()
                + "\n" + item.getSubject() + "\n" + item.getContent());

        return v;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getReply().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getReply().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition);
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
