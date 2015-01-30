
package kr.co.cashqc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.cashqc.lazylist.ImageLoader;

/**
 * @author Jung-Hum Cho
 */

public class ShopListAdapter extends BaseAdapter {

    public ShopListAdapter(Context context, View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mData = new ArrayList<ShopListData>();

        mLazy = new ImageLoader(context);

        mContext = context;

    }

    private View.OnClickListener mOnClickListener = null;

    private ImageLoader mLazy;

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

        private ImageView score;

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
                h.score = (ImageView)v.findViewById(R.id.score);

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

            ShopListData data = (ShopListData)getItem(position);

            h.btnTel.setFocusable(false);
            if (mOnClickListener != null) {
                h.btnTel.setTag(R.id.num, data.getTel());
                h.btnTel.setTag(R.id.name, data.getName());
                h.btnTel.setOnClickListener(mOnClickListener);
            }

            h.name.setText(data.getName());

            h.distance.setText(data.getDistance());

            h.minPay.setText(data.getMinpay());

            h.dong.setText(data.getDelivery_comment());

            h.minPay.setTag(data.getPre_pay());

            h.time1.setText(data.getTime1() + " ~");
            h.time2.setText(data.getTime2());

            String pre_pay = (String)h.minPay.getTag();

            if ("".equals(pre_pay)) {

                h.thmLayout.setVisibility(View.GONE);
                h.score.setVisibility(View.GONE);

                h.btnTel.setBackgroundResource(R.drawable.btn_list_gray);
                h.btnTel.setText("일반 주문");
                h.minPay.setText("포인트 적립 불가");
                h.separatorRow.setBackgroundResource(R.drawable.list_title_gray);

                h.imgPoint.setVisibility(View.GONE);

            } else {
                mLazy.DisplayImage(data.makeURL(), h.thumbImg);

                h.imgPoint.setVisibility(View.VISIBLE);
                h.thmLayout.setVisibility(View.VISIBLE);
                h.score.setVisibility(View.VISIBLE);
                h.thumbImg.setVisibility(View.VISIBLE);

                Bitmap thumb = null, ribbon = null, thumbRibbon = null, result = null;

                if ("gl".equals(pre_pay)) {

                    h.btnTel.setText("골드 주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_gold);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_gold);

                    h.imgPoint.setImageResource(R.drawable.point_2000);

                    ribbon = BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.img_ribbon_pr);

                } else if ("sl".equals(pre_pay)) {

                    h.btnTel.setText("실버 주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_silver);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_silver);

                    ribbon = BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.img_ribbon_po);

                    h.imgPoint.setImageResource(R.drawable.point_1500);

                } else if ("on".equals(pre_pay)) {

                    h.btnTel.setText("캐시큐 주문");
                    h.btnTel.setBackgroundResource(R.drawable.btn_list_red);
                    h.separatorRow.setBackgroundResource(R.drawable.list_title_red);

                    ribbon = BitmapFactory.decodeResource(mContext.getResources(), 
                            R.drawable.img_ribbon_po);

                    h.imgPoint.setImageResource(R.drawable.point_1000);
                }

                /*
                 * // ribbon thumbnail work if (data.getThm().isEmpty()) { thumb
                 * = BitmapFactory.decodeResource(mContext.getResources(),
                 * R.drawable.img_no_image_80x120); } else { thumb =
                 * mLazy.getBitmap(data.makeURL()); } thumbRibbon =
                 * overlayBitmap(thumb, ribbon, 5, 5); // bizhour work if
                 * (isBizHours(data.getTime1(), data.getTime2())) { result =
                 * thumbRibbon; } else { Bitmap overtime =
                 * BitmapFactory.decodeResource(mContext.getResources(),
                 * R.drawable.nottime); result = overlayBitmap(thumbRibbon,
                 * overtime, 0, 0); } h.thumbImg.setImageBitmap(result);
                 */
            }

        } else {
            if (type == TYPE_GOLD) {

                h.separatorText.setText("골드 가맹점 - 2000 포인트 적립");
                h.separatorImage.setImageResource(R.drawable.list_title_gold);

            } else if (type == TYPE_SILVER) {

                h.separatorText.setText("실버 가맹점 - 1500 포인트 적립");
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

    private boolean isBizHours(String t1, String t2) {

        String strNow = new SimpleDateFormat("H").format(new Date(System.currentTimeMillis()));
        int nowTime = Integer.parseInt(strNow);
        try {
            t1 = t1.trim().replace(":", "").replace("0", "");
            t2 = t2.trim().replace(":", "").replace("0", "");

            int openTime = Integer.parseInt(t1);
            int closeTime = Integer.parseInt(t2);

            if (nowTime <= openTime && nowTime >= closeTime) {
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return true;
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
