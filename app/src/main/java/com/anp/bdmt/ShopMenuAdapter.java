
package com.anp.bdmt;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.anp.bdmt.BaseActivity.setCartCount;
import static com.anp.bdmt.Utils.insertMenuLevel2;

//import static com.anp.coupangbook.ShopPageActivity.TTS_SHOP;

public class ShopMenuAdapter extends BaseExpandableListAdapter {

    public ShopMenuAdapter(Context context, ShopMenuData data,
                           DialogInterface.OnDismissListener onDismissListener, ShopPageActivity activity) {
        super();

        mData = data;

        inflater = LayoutInflater.from(context);

        mOnDismissListener = onDismissListener;

        mActivity = activity;
    }

    private ShopPageActivity mActivity;

    private DialogInterface.OnDismissListener mOnDismissListener;

    private ShopMenuData mData;

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
            h.ivThumb = (ImageView)v.findViewById(R.id.iv_thumb);

            h.tvPrice = (TextView)v.findViewById(R.id.tv_price);
            h.tvDiscountRate = (TextView)v.findViewById(R.id.tv_rate);
            h.tvDiscountPrice = (TextView)v.findViewById(R.id.tv_discountprice);
            h.tvQuantity = (TextView)v.findViewById(R.id.tv_quantity);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        final MenuData item = (MenuData)getChild(groupPosition, childPosition);

        h.tvChildName.setText(item.getLabel());

        String price = "가격 정보 없음";

        if (item.getPrice() != 0) {
            price = String.format("%,d 원", item.getPrice());
        }

        h.tvPrice.setText(price);

        if (item.isDeal()) {

            h.tvQuantity.setText("남은 수량 : " + item.getQuantity());

            h.tvPrice.setPaintFlags(h.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int rate = item.getDiscountRate();

            h.tvDiscountRate.setText(String.valueOf(rate) + "%");

            int discountPrice = item.getDiscountPrice();

            // if (item.getPrice() != 0) {
            //
            // discountPrice = (int)(item.getPrice() * (100 - rate) * 0.01);
            // }

            String strDiscountPrice = String.format("%,d 원", discountPrice);

            h.tvDiscountPrice.setText(strDiscountPrice);

        } else {

            h.tvQuantity.setVisibility(View.GONE);
            h.tvDiscountPrice.setVisibility(View.GONE);
            h.tvDiscountRate.setVisibility(View.GONE);

        }

        String imgUrl = "http://cashq.co.kr/adm/upload/thumb/1424842254UWDWC.jpg";

        // ImageLoader.getInstance().displayImage(imgUrl, h.ivThumb);
        // if (TTS_SHOP) {
        // if (false) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean hasLevel3 = !item.getChild().isEmpty();

                if (hasLevel3) {

                    OrderMenuDialog dialog = new OrderMenuDialog(inflater.getContext(), mData,
                            groupPosition, childPosition, mOnDismissListener);
                    dialog.show();
                    dialog.setOnDismissListener(mOnDismissListener);

                } else {
                    insertMenuLevel2(inflater.getContext(), mData, groupPosition, childPosition);
                    setCartCount(mActivity);
                }

                // Toast.makeText(inflater.getContext(),
                // "group : " + groupPosition + "\nchild : " +
                // childPosition,
                // Toast.LENGTH_SHORT).show();
            }
        });
        // }
        return v;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.list_menu_row, parent, false);
            v.setBackgroundColor(Color.rgb(237, 237, 237));
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
        private ImageView ivImage, ivThumb, ivIndicator;

        private TextView tvGroupName;

        private TextView tvChildName;

        private TextView tvPrice;

        private TextView tvDiscountPrice;

        private TextView tvQuantity;

        private TextView tvDiscountRate;

    }

}
