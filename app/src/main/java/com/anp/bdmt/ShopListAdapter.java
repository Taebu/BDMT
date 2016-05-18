
package com.anp.bdmt;

import com.hb.views.PinnedSectionListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
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

    public static final int TYPE_PRQ = 4;

    public static final int TYPE_NORMAL = 5;

    public static final int TYPE_MAX_COUNT = 6;

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

        private LinearLayout thmLayout;

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

//        private ImageView separatorRow;

        // private ImageView imgPoint;

        // private LinearLayout pointBenefit;

        // private TextView pointBanner;

        private TextView pointAmount;

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
                // h.distance =
                // (TextView)v.findViewById(R.id.cashq_list_distance);
                // h.time1 = (TextView)v.findViewById(R.id.cashq_list_time1);
                // h.time2 = (TextView)v.findViewById(R.id.cashq_list_time2);
                h.minPay = (TextView)v.findViewById(R.id.min_pay);
                h.thumbImg = (ImageView)v.findViewById(R.id.list_thm);
                // h.btnTel = (Button)v.findViewById(R.id.tel_btn);
                h.dong = (TextView)v.findViewById(R.id.dong);
                h.score = (RatingBar)v.findViewById(R.id.shoplist_rating);

                h.ll = (RelativeLayout)v.findViewById(R.id.row_id);

                h.thmLayout = (LinearLayout)v.findViewById(R.id.thm_layout);
                h.pointInfo = (LinearLayout)v.findViewById(R.id.point_info);
                // h.btnLayout = (LinearLayout)v.findViewById(R.id.btn_layout);
//                h.separatorRow = (ImageView)v.findViewById(R.id.separator_row);
                // h.imgPoint = (ImageView)v.findViewById(R.id.row_img_point);

                // h.pointBanner = (TextView)
                // v.findViewById(R.id.row_shop_point);
                // h.pointBenefit = (LinearLayout)
                // v.findViewById(R.id.row_shop_benefit);

                h.pointInfo = (LinearLayout)v.findViewById(R.id.point_info);
                h.pointAmount = (TextView)v.findViewById(R.id.point_amount);

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

            ShopListData item = (ShopListData)getItem(position);

            // h.btnTel.setFocusable(false);
            // if (mOnClickListener != null) {
            // h.btnTel.setTag(R.id.num, item.getTel());
            // h.btnTel.setTag(R.id.name, item.getName());
            // h.btnTel.setTag(R.id.img1, item.getImg1());
            // h.btnTel.setTag(R.id.img2, item.getImg2());
            // h.btnTel.setOnClickListener(mOnClickListener);
            // }

            h.name.setText(item.getName());

            // h.distance.setText(item.getDistance());

            h.minPay.setText(item.getMinpay() + " 이상 적립");

            h.dong.setText(item.getDelivery_comment());
            h.dong.setSingleLine(true);
            h.dong.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            h.dong.setSelected(true);

            h.minPay.setTag(item.getPre_pay());

            // h.time1.setText(item.getTime1() + " ~");
            // h.time2.setText(item.getTime2());

            h.score.setRating(Float.parseFloat(item.getReviewRating()));

            String pre_pay = (String)h.minPay.getTag();

            if ("".equals(pre_pay)) {

                h.thmLayout.setVisibility(View.GONE);
                h.score.setVisibility(View.INVISIBLE);
                h.pointInfo.setVisibility(View.GONE);

                // h.btnTel.setBackgroundResource(R.drawable.btn_list_gray);
                // h.btnTel.setText("일반\n주문");
                h.minPay.setText("포인트 적립 불가");
                // h.separatorRow.setBackgroundResource(R.drawable.list_title_gray);

                // h.imgPoint.setVisibility(View.GONE);

                // h.pointBenefit.setVisibility(View.INVISIBLE);
                // h.pointBanner.setVisibility(View.INVISIBLE);

            } else {
                // bizhour work

                Log.d(TAG, "bizHour : " + item.getName());

                // boolean isBizHour = item.isOpen();
                boolean isBizHour = true;

                if (isBizHour) {

                    ImageLoader.getInstance().displayImage(item.makeURL(), h.thumbImg,
                            new SimpleImageLoadingListener() {

                                @Override
                                public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                                    h.thumbImg.setImageResource(R.drawable.img_no_image_80x120);
                                }
                            });
                } else {

                    h.thumbImg.setImageResource(R.drawable.nottime);

                }

                // h.imgPoint.setVisibility(View.VISIBLE);
                h.thmLayout.setVisibility(View.VISIBLE);
                h.score.setVisibility(View.VISIBLE);
                h.thumbImg.setVisibility(View.VISIBLE);
                h.pointInfo.setVisibility(View.VISIBLE);

                // Bitmap thumb = null, ribbon = null, thumbRibbon = null,
                // result = null;

                if ("gl".equals(pre_pay)) {

                    // h.btnTel.setText("골드\n주문");
                    // h.btnTel.setBackgroundResource(R.drawable.btn_list_gold);
                    // h.separatorRow.setBackgroundResource(R.drawable.list_title_gold);

                    // h.imgPoint.setImageResource(R.drawable.point_2000);
                    // h.pointBenefit.setVisibility(View.VISIBLE);
                    // h.pointBanner.setVisibility(View.VISIBLE);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_pr);

                    h.pointAmount.setText("2,000 Point");

                } else if ("sl".equals(pre_pay)) {

                    // h.btnTel.setText("실버\n주문");
                    // h.btnTel.setBackgroundResource(R.drawable.btn_list_silver);
                    // h.separatorRow.setBackgroundResource(R.drawable.list_title_silver);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_po);

                    // h.imgPoint.setImageResource(R.drawable.point_2000);
                    // h.pointBenefit.setVisibility(View.VISIBLE);
                    // h.pointBanner.setVisibility(View.VISIBLE);

                    h.pointAmount.setText("2,000 Point");

                } else if ("on".equals(pre_pay)) {

                    // h.btnTel.setText("캐시큐\n주문");
                    // h.btnTel.setBackgroundResource(R.drawable.btn_list_red);
                    // h.separatorRow.setBackgroundResource(R.drawable.list_title_red);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_po);

                    // h.imgPoint.setImageResource(R.drawable.point_1000);

                    // h.pointBenefit.setVisibility(View.VISIBLE);
                    // h.pointBanner.setVisibility(View.VISIBLE);

                    h.pointAmount.setText("1,000 Point");

                } else if ("pr".equals(pre_pay)) {

                    // h.btnTel.setText("PRQ\n주문");
                    // h.btnTel.setBackgroundResource(R.drawable.btn_list_prq);
                    // h.separatorRow.setBackgroundResource(R.drawable.list_title_prq);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_po);

                    // h.minPay.setText("포인트 적립 불가");

                    // h.imgPoint.setImageResource(R.drawable.point_1000);
                    // h.imgPoint.setVisibility(View.INVISIBLE);

                    // h.pointBenefit.setVisibility(View.INVISIBLE);
                    // h.pointBanner.setVisibility(View.INVISIBLE);
                }

                // ribbon thumbnail work
                // if (item.getThm().isEmpty()) {
                // thumb = BitmapFactory.decodeResource(mContext.getResources(),
                // R.drawable.img_no_image_80x120);
                // } else {
                // thumb = mLazy.getBitmap(item.makeURL());
                // }

                // thumbRibbon = overlayBitmap(thumb, ribbon, 5, 5);

            }

        } else {
            if (type == TYPE_GOLD) {

                // h.separatorText.setText("골드 가맹점 - 2000 포인트 적립");
                h.separatorText.setText("VVIP");
                // h.separatorImage.setImageResource(R.drawable.list_title_gold);
                h.separatorImage.setImageResource(R.drawable.gold_member);

            } else if (type == TYPE_SILVER) {

                // h.separatorText.setText("실버 가맹점 - 2000 포인트 적립");
                h.separatorText.setText("RVIP");
                // h.separatorImage.setImageResource(R.drawable.list_title_silver);
                h.separatorImage.setImageResource(R.drawable.silver_member);

            } else if (type == TYPE_CASHQ) {

                // h.separatorText.setText("캐시큐 가맹점 - 1000 포인트 적립");
                h.separatorText.setText("VIP");
                // h.separatorImage.setImageResource(R.drawable.list_title_red);
                h.separatorImage.setImageResource(R.drawable.cashq_member);

            } else if (type == TYPE_NORMAL) {

                // h.separatorText.setText("일반 가맹점 - 포인트 적립 불가");
                h.separatorText.setText("일반");
                // h.separatorImage.setImageResource(R.drawable.list_title_gray);
                h.separatorImage.setImageResource(R.drawable.common_member);

            } else if (type == TYPE_PRQ) {
                // h.separatorText.setText("PRQ 가맹점 - 포인트 적립 불가");
                h.separatorText.setText("PRQ");
                // h.separatorImage.setImageResource(R.drawable.list_title_prq);
                h.separatorImage.setImageResource(R.drawable.list_title_prq);
            }
        }
        return v;
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
