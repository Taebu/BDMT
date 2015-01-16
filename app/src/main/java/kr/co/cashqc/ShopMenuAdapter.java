
package kr.co.cashqc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopMenuAdapter extends BaseExpandableListAdapter {

    public ShopMenuAdapter(Context context, ShopMenuData data) {
        super();

        mData = data;

        inflater = LayoutInflater.from(context);
    }

    private ShopMenuData mData;

    private LayoutInflater inflater = null;

    private ViewHolder h = null;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.list_menu_row, null);
            h.tvChildName = (TextView)v.findViewById(R.id.tv_child);
            h.tvChildValue = (TextView)v.findViewById(R.id.tv_value);
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        final MenuData item = (MenuData)getChild(groupPosition, childPosition);

        h.tvChildName.setText(item.getLabel());
        h.tvChildValue.setText(item.getPrice());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new OrderMenuDialog(inflater.getContext(), mData,
                        groupPosition, childPosition).show();

//                Toast.makeText(inflater.getContext(),
//                        "group : " + groupPosition + "\nchild : " + childPosition,
//                        Toast.LENGTH_SHORT).show();
            }
        });

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
            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        if (isExpanded) {

            h.ivImage.setBackgroundColor(Color.GREEN);
        } else {
            h.ivImage.setBackgroundColor(Color.WHITE);
        }

        h.tvGroupName.setText(((MenuData)getGroup(groupPosition)).getLabel());

        return v;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.getMenu().get(groupPosition).getChild().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.getMenu().get(groupPosition).getChild().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.getMenu().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mData.getMenu().size();
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
        private ImageView ivImage;

        private TextView tvGroupName;

        private TextView tvChildName;

        private TextView tvChildValue;
    }

}
