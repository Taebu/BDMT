
package com.anp.bdmt;

import com.hb.views.PinnedSectionListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho
 */

public class ShopListAdapter extends BaseAdapter implements
        PinnedSectionListView.PinnedSectionListAdapter {

    private final String TAG = getClass().getSimpleName();

    public ShopListAdapter(Context context, View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mData = new ArrayList<ShopListData>();

        // mLazy = new ImageLoader(context);

        mContext = context;

    }

    private View.OnClickListener mOnClickListener = null;

    // private ImageLoader mLazy;

    private ArrayList<ShopListData> mData;

    private Context mContext;

    // separators

    public static final int TYPE_ITEM = 0;

    public static final int TYPE_GOLD = 1;

    public static final int TYPE_SILVER = 2;

    public static final int TYPE_CASHQ = 3;

    public static final int TYPE_BRONZE = 4;

    public static final int TYPE_PRQ = 5;

    public static final int TYPE_NORMAL = 6;

    public static final int TYPE_MAX_COUNT = 7;

    private LayoutInflater mInflater;

    public void addItem(final ShopListData item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(final ShopListData item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getSeparatorType();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 1 || viewType == 2 || viewType == 3 || viewType == 4 || viewType == 5;
    }

    private class ViewHolder {

        private ImageView separatorBar;

        private RelativeLayout thmLayout;

        private LinearLayout pointInfo;

        // private LinearLayout btnLayout;

        private RelativeLayout ll;

        private TextView name;

        // private TextView distance;

        // private TextView time1;

        // private TextView time2;

        private TextView minPay;

        private ImageView thumbImg;

        // private Button btnTel;

        private TextView dong;

        private RatingBar score;

        // separator view
        private ImageView separatorImage;

        private TextView separatorText;

        // private ImageView separatorRow;

        // private ImageView imgPoint;

        // private LinearLayout pointBenefit;

        // private TextView pointBanner;

        private TextView pointAmount;

        private LinearLayout paynowLayout;

        private ImageView paynowRibbon;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder h;

        View v = convertView;

        int type = getItemViewType(position);

        if (v == null) {

            h = new ViewHolder();

            if (type == TYPE_ITEM) {
                v = mInflater.inflate(R.layout.row_shop, null);

                h.name = (TextView)v.findViewById(R.id.cashq_list_name);
                h.minPay = (TextView)v.findViewById(R.id.min_pay);
                h.thumbImg = (ImageView)v.findViewById(R.id.list_thm);
                h.dong = (TextView)v.findViewById(R.id.dong);
                h.score = (RatingBar)v.findViewById(R.id.shoplist_rating);

                h.ll = (RelativeLayout)v.findViewById(R.id.row_id);

                h.thmLayout = (RelativeLayout)v.findViewById(R.id.thm_layout);
                h.pointInfo = (LinearLayout)v.findViewById(R.id.point_info);

                h.pointInfo = (LinearLayout)v.findViewById(R.id.point_info);
                h.pointAmount = (TextView)v.findViewById(R.id.point_amount);

                h.paynowLayout = (LinearLayout)v.findViewById(R.id.paynow_comment);
                h.paynowRibbon = (ImageView)v.findViewById(R.id.paynow_ribbon);

            } else {
                v = mInflater.inflate(R.layout.list_header, null);

                h.separatorImage = (ImageView)v.findViewById(R.id.separator_img);
                h.separatorText = (TextView)v.findViewById(R.id.separator_text);

            }

            v.setTag(h);

        } else {
            h = (ViewHolder)v.getTag();
        }

        if (type == TYPE_ITEM) {

            final ShopListData item = (ShopListData)getItem(position);

            // h.btnTel.setFocusable(false);
            // if (mOnClickListener != null) {
            // h.btnTel.setTag(R.id.num, item.getTel());
            // h.btnTel.setTag(R.id.name, item.getName());
            // h.btnTel.setTag(R.id.img1, item.getImg1());
            // h.btnTel.setTag(R.id.img2, item.getImg2());
            // h.btnTel.setOnClickListener(mOnClickListener);
            // }

            h.name.setText(item.getName());

            h.minPay.setText(item.getMinpay() + " 이상 주문 시 적립 인정");

            h.dong.setText(item.getDelivery_comment());
            h.dong.setSingleLine(true);
            h.dong.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            h.dong.setSelected(true);

            h.minPay.setTag(item.getPre_pay());

            h.score.setRating(Float.parseFloat(item.getReviewRating()));

            // String pre_pay = (String)h.minPay.getTag();
            String pre_pay = item.getPre_pay();

            showThumbnail(position, h);
            // 110 398 519410
            h.pointInfo.setVisibility(View.VISIBLE);

            if ("gl".equals(pre_pay)) {

                h.pointAmount.setText("2,000 Point");

                h.paynowLayout.setVisibility(View.VISIBLE);
                h.paynowRibbon.setVisibility(View.VISIBLE);

            } else if ("sl".equals(pre_pay)) {

                h.pointAmount.setText("2,000 Point");

                h.paynowLayout.setVisibility(View.GONE);
                h.paynowRibbon.setVisibility(View.GONE);

            } else if ("on".equals(pre_pay)) {

                h.pointAmount.setText("1,000 Point");

                h.paynowLayout.setVisibility(View.GONE);
                h.paynowRibbon.setVisibility(View.GONE);

            } else if ("br".equals(pre_pay) || "".equals(pre_pay)) {

                h.pointAmount.setText("500 Point");

                h.paynowLayout.setVisibility(View.GONE);
                h.paynowRibbon.setVisibility(View.GONE);

            } else if ("pr".equals(pre_pay) || "".equals(pre_pay)) {
                h.pointInfo.setVisibility(View.GONE);
                h.minPay.setText("포인트 적립 불가");
                h.paynowLayout.setVisibility(View.GONE);
                h.paynowRibbon.setVisibility(View.GONE);
            }

        } else {

            showSeparator(h, type);
        }
        return v;
    }

    private void showSeparator(ViewHolder h, int type) {

        switch (type) {
            case TYPE_GOLD:
                h.separatorText.setText("VVIP");
                h.separatorImage.setImageResource(R.drawable.gold_member);
                break;
            case TYPE_SILVER:
                h.separatorText.setText("RVIP");
                h.separatorImage.setImageResource(R.drawable.silver_member);
                break;
            case TYPE_CASHQ:
                h.separatorText.setText("VIP");
                h.separatorImage.setImageResource(R.drawable.cashq_member);
                break;
            case TYPE_BRONZE:
                h.separatorText.setText("BRONZE");
                h.separatorImage.setImageResource(R.drawable.bronze_member);
                break;
            case TYPE_PRQ:
                h.separatorText.setText("PRQ");
                h.separatorImage.setImageResource(R.drawable.prq_member);
                break;
            case TYPE_NORMAL:
                h.separatorText.setText("일반");
                h.separatorImage.setImageResource(R.drawable.common_member);
                break;
        }
    }

    private void showThumbnail(int position, final ViewHolder h) {
        // bizhour work

        Log.d(TAG, "bizHour : " + mData.get(position).getName());

        // boolean isBizHour = item.isOpen();
        boolean isBizHour = true;

        final int typePosition = mData.get(position).getTypePosition();

        if (isBizHour) {

            final Integer noImgResources[] = new Integer[] {
                    R.drawable.no_img_chicken, R.drawable.no_img_pizza, R.drawable.no_img_chinese,
                    R.drawable.no_img_jokbal, R.drawable.no_img_night, R.drawable.no_img_soup,
                    R.drawable.no_img_korean, R.drawable.no_img_japanese,
                    R.drawable.no_img_boxlunch, R.drawable.no_img_fastfood
            };

            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();

            builder.displayer(new RoundedBitmapDisplayer(200));
            builder.cacheInMemory(true);
            builder.cacheOnDisk(true);
            builder.showImageOnLoading(R.drawable.load_anim);
            // builder.showImageForEmptyUri(noImgResources[typePosition]);
            // builder.showImageOnFail(noImgResources[typePosition]);

            DisplayImageOptions options = builder.build();

            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();

            Log.d(TAG, "name: " + mData.get(position).getName() + " url: "
                    + mData.get(position).makeURL());

            ImageLoader.getInstance().displayImage(mData.get(position).makeURL(), h.thumbImg,
                    options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            h.thumbImg.setImageResource(noImgResources[typePosition]);
                        }
                    });

        } else {

            h.thumbImg.setImageResource(R.drawable.nottime);

        }
    }

    private Bitmap overlayBitmap(Bitmap b1, Bitmap b2, int x, int y) {

        if (b1 == null || b2 == null) {
            return null;
        }

        Bitmap resultImg = Bitmap.createBitmap(b1.getWidth() + x, b1.getHeight() + y,
                b1.getConfig());

        Canvas canvas = new Canvas(resultImg);

        canvas.drawBitmap(b1, x, y, null);

        canvas.drawBitmap(b2, 0, 0, null);

        return resultImg;
    }

}
