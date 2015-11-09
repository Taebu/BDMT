
package kr.co.cashqc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho
 */

public class ShopListAdapter extends BaseAdapter {

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

    public static final int TYPE_NORMAL = 4;

    public static final int TYPE_MAX_COUNT = 5;

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

    private class ViewHolder {

        private ImageView separatorBar;

        private LinearLayout thmLayout, contentLayout, btnLayout;

        private RelativeLayout ll;

        private TextView name;

        private TextView distance;

        private TextView time1;

        private TextView time2;

        private TextView minPay;

        private ImageView thumbImg;

        private Button btnTel;

        private TextView dong;

        private RatingBar score;

        // separator view
        private ImageView separatorImage;

        private TextView separatorText;

        private ImageView separatorRow;

        private ImageView imgPoint;
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
                h.distance = (TextView)v.findViewById(R.id.cashq_list_distance);
                h.time1 = (TextView)v.findViewById(R.id.cashq_list_time1);
                h.time2 = (TextView)v.findViewById(R.id.cashq_list_time2);
                h.minPay = (TextView)v.findViewById(R.id.min_pay);
                h.thumbImg = (ImageView)v.findViewById(R.id.list_thm);
                h.btnTel = (Button)v.findViewById(R.id.tel_btn);
                h.dong = (TextView)v.findViewById(R.id.dong);
                h.score = (RatingBar)v.findViewById(R.id.shoplist_rating);

                h.ll = (RelativeLayout)v.findViewById(R.id.row_id);

                h.thmLayout = (LinearLayout)v.findViewById(R.id.thm_layout);
                h.contentLayout = (LinearLayout)v.findViewById(R.id.content_layout);
                h.btnLayout = (LinearLayout)v.findViewById(R.id.btn_layout);
                h.separatorRow = (ImageView)v.findViewById(R.id.separator_row);
                h.imgPoint = (ImageView)v.findViewById(R.id.row_img_point);

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

            h.btnTel.setFocusable(false);
            if (mOnClickListener != null) {
                h.btnTel.setTag(R.id.num, item.getTel());
                h.btnTel.setTag(R.id.name, item.getName());
                h.btnTel.setTag(R.id.img1, item.getImg1());
                h.btnTel.setTag(R.id.img2, item.getImg2());
                h.btnTel.setOnClickListener(mOnClickListener);
            }

            h.name.setText(item.getName());

            h.distance.setText(item.getDistance());

            h.minPay.setText(item.getMinpay());

            h.dong.setText(item.getDelivery_comment());

            h.minPay.setTag(item.getPre_pay());

            h.time1.setText(item.getTime1() + " ~");
            h.time2.setText(item.getTime2());

            h.score.setRating(Float.parseFloat(item.getReviewRating()));

            String pre_pay = (String)h.minPay.getTag();

            if ("".equals(pre_pay)) {

                h.thmLayout.setVisibility(View.GONE);
                h.score.setVisibility(View.GONE);

                h.btnTel.setBackgroundResource(R.drawable.btn_list_gray);
                h.btnTel.setText("일반\n주문");
                h.minPay.setText("포인트 적립 불가");
                h.separatorRow.setBackgroundResource(R.drawable.list_title_gray);

                h.imgPoint.setVisibility(View.GONE);

            } else {
                // mLazy.DisplayImage(item.makeURL(), h.thumbImg);

                // bizhour work

                Log.d(TAG, "bizHour : " + item.getName());

//                boolean isBizHour = item.isOpen();
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

                h.imgPoint.setVisibility(View.VISIBLE);
                h.thmLayout.setVisibility(View.VISIBLE);
                h.score.setVisibility(View.VISIBLE);
                h.thumbImg.setVisibility(View.VISIBLE);

                // Bitmap thumb = null, ribbon = null, thumbRibbon = null,
                // result = null;

                if ("gl".equals(pre_pay)) {

                    h.btnTel.setText("골드\n주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_gold);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_gold);

                    h.imgPoint.setImageResource(R.drawable.point_2000);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_pr);

                } else if ("sl".equals(pre_pay)) {

                    h.btnTel.setText("실버\n주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_silver);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_silver);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_po);

                    h.imgPoint.setImageResource(R.drawable.point_2000);

                } else if ("on".equals(pre_pay)) {

                    h.btnTel.setText("캐시큐\n주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_red);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_red);

                    // ribbon =
                    // BitmapFactory.decodeResource(mContext.getResources(),
                    // R.drawable.img_ribbon_po);

                    h.imgPoint.setImageResource(R.drawable.point_1000);
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

                h.separatorText.setText("골드 가맹점 - 2000 포인트 적립");
                h.separatorImage.setImageResource(R.drawable.list_title_gold);

            } else if (type == TYPE_SILVER) {

                h.separatorText.setText("실버 가맹점 - 2000 포인트 적립");
                h.separatorImage.setImageResource(R.drawable.list_title_silver);

            } else if (type == TYPE_CASHQ) {

                h.separatorText.setText("캐시큐 가맹점 - 1000 포인트 적립");
                h.separatorImage.setImageResource(R.drawable.list_title_red);

            } else if (type == TYPE_NORMAL) {

                h.separatorText.setText("일반 가맹점 - 포인트 적립 불가");
                h.separatorImage.setImageResource(R.drawable.list_title_gray);

            }
        }
        return v;
    }

    private Bitmap overlayBitmap(Bitmap b1, Bitmap b2, int x, int y) {

        if (b1 == null || b2 == null)
            return null;

        Bitmap resultImg = Bitmap.createBitmap(b1.getWidth() + x, b1.getHeight() + y,
                b1.getConfig());

        Canvas canvas = new Canvas(resultImg);

        canvas.drawBitmap(b1, x, y, null);

        canvas.drawBitmap(b2, 0, 0, null);

        return resultImg;
    }

}
