
package com.anp.bdmt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by ssenn_000 on 2015-02-06.
 */
public class NotificationService extends Service {

    private final String TAG = getClass().getSimpleName();

    private NotificationService mThis = this;

    /*- msg_type
    100 일반메세지
    101 적립완료
    102 지급완료
    103 첫다운로드선물
    110 지급거절
    111 계좌오류
    112 동일매장
    113 전산오류*/

    public static final int COMMON_MESSAGE = 100;

    public static final int COMPLETE_SAVING_POINT = 101;

    public static final int COMPLETE_DEPOSIT_POINT = 102;

    public static final int FIRST_PRESENT = 103;

    public static final int REJECT_DEPOSIT_POINT = 110;

    public static final int REJECT_ACCOUNT_ERROR = 111;

    public static final int REJECT_DUPLICATE_SHOP = 112;

    public static final int SYSTEM_ERROR = 113;

    private View mView;

    private boolean isTouch = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        mView = layoutInflater.inflate(R.layout.overlay_noti, null);

        initView(intent);

        popupView();

        // removeViewAfterSecond(mView);

        return super.onStartCommand(intent, flags, startId);
    }

    private void initView(final Intent intent) {

        LinearLayout iconLayout = (LinearLayout)mView.findViewById(R.id.noti_icon_layout);
        ImageView iconImageView = (ImageView)mView.findViewById(R.id.noti_icon);
        TextView titleTextView = (TextView)mView.findViewById(R.id.noti_title);
        final TextView contentTextView = (TextView)mView.findViewById(R.id.noti_content);
        // LinearLayout pointLayout =
        // (LinearLayout)mView.findViewById(R.id.noti_point_layout);

        // LinearLayout overlayLayout =
        // (LinearLayout)mView.findViewById(R.id.overlay_layout);
        Button positiveButton = (Button)mView.findViewById(R.id.noti_positive);
        Button negativeButton = (Button)mView.findViewById(R.id.noti_negative);
        negativeButton.setText("닫기");

        final int messageType = intent.getIntExtra("message_type", 0);
        switch (messageType) {
            case COMMON_MESSAGE:
                iconLayout.setVisibility(View.GONE);
                String title = intent.getStringExtra("title");
                String message = intent.getStringExtra("message");
                titleTextView.setText(title);
                contentTextView.setText(message);
                positiveButton.setText("앱 실행");
                break;

            case COMPLETE_SAVING_POINT:
                iconImageView.setImageResource(R.drawable.ic_pointcoin);
                titleTextView.setText(R.string.noti_complete_saving_title);
                contentTextView.setText(R.string.noti_complete_saving_content);
                positiveButton.setText("자세히");
                mView.findViewById(R.id.noti_caution).setVisibility(View.VISIBLE);
                break;

            case COMPLETE_DEPOSIT_POINT:
                iconImageView.setImageResource(R.drawable.ic_confirm);
                titleTextView.setText(R.string.noti_complete_deposit_title);
                contentTextView.setText(R.string.noti_complete_deposit_content);
                positiveButton.setText("리뷰 쓰기");

                break;

            case FIRST_PRESENT:
                iconImageView.setImageResource(R.drawable.ic_present);
                titleTextView.setText(R.string.noti_first_content);
                contentTextView.setText(R.string.noti_first_title);
                positiveButton.setText("자세히");
                break;

            case REJECT_DEPOSIT_POINT:
                iconImageView.setImageResource(R.drawable.ic_reject);
                titleTextView.setText(R.string.noti_reject_deposit_title);
                contentTextView.setText(R.string.noti_reject_deposit_content);
                positiveButton.setText("포인트 관리");
                // pointLayout.setVisibility(View.VISIBLE);
                // String pointResults = intent.getStringExtra("point_result");
                // ArrayList<PointResultData> pointResultDatas =
                // getPointResultDatas(pointResults);
                // ListView pointListView =
                // (ListView)mView.findViewById(R.id.noti_point_list);
                // pointListView.setAdapter(new
                // PointResultListAdapter(pointResultDatas));
                break;

            case REJECT_ACCOUNT_ERROR:
                iconImageView.setImageResource(R.drawable.ic_reject);
                titleTextView.setText(R.string.noti_account_error_title);
                contentTextView.setText(R.string.noti_account_error_content);
                positiveButton.setText("포인트 관리");
                break;

            case REJECT_DUPLICATE_SHOP:
                iconImageView.setImageResource(R.drawable.ic_reject);
                titleTextView.setText(R.string.noti_reject_duplicate_shop_title);
                contentTextView.setText(R.string.noti_reject_duplicate_shop_content);
                positiveButton.setText("포인트 관리");
                break;

            case SYSTEM_ERROR:
                iconImageView.setImageResource(R.drawable.ic_reject);
                titleTextView.setText(R.string.noti_system_error_title);
                contentTextView.setText(R.string.noti_system_error_content);
                positiveButton.setText("앱 실행");
                break;
        }

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopSelf();

                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                switch (messageType) {
                    case COMMON_MESSAGE:
                    case SYSTEM_ERROR:
                        i.setClass(mThis, IntroActivity.class);
                        break;

                    case COMPLETE_DEPOSIT_POINT:
                        i.setAction(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("market://details?id=" + getPackageName()));
                        break;

                    case COMPLETE_SAVING_POINT:
                    case REJECT_ACCOUNT_ERROR:
                    case REJECT_DUPLICATE_SHOP:
                    case REJECT_DEPOSIT_POINT:
                    case FIRST_PRESENT:
                        i.setClass(mThis, LoginActivity.class);
                        break;
                }

                startActivity(i);

            }
        });

    }

    private void popupView() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                // WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);

        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        windowManager.addView(mView, params);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.e("onTouch", "event:" + event.toString());

                Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Construct a rect of the view's bounds
                    Log.e("onTouch", "down");
                    stopSelf();
                }
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    // Construct a rect of the view's bounds
                    Log.e("onTouch", "out");
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!rect.contains(v.getLeft() + (int)event.getX(),
                            v.getTop() + (int)event.getY())) {
                        // User moved outside bounds
                        Log.e("onTouch", "move");
                    }
                }
                return false;

            }
        });

    }

    private void removeViewAfterSecond(final View view) {

        final int max = 1000;

        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.noti_progressbar);

        progressBar.setMax(max);

        new Thread(new Runnable() {
            @Override
            public void run() {

                int progressStatus = 0;

                while (progressStatus < max) {

                    if (isTouch) {
                        progressBar.setProgress(max);
                        return;
                    }

                    progressStatus++;
                    progressBar.setProgress(progressStatus);

                    try {
                        Thread.sleep(max / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });

            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeViewImmediate(mView);
        }
    }

    private ArrayList<PointResultData> getPointResultDatas(String pointResults) {

        try {
            JSONArray array = new JSONArray(pointResults);

            ArrayList<PointResultData> dataList = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {

                PointResultData data = new PointResultData();

                JSONObject object = array.getJSONObject(i);

                data.setName(getJsonString(object, "st_name"));
                data.setDate(getJsonString(object, "st_stdt"));
                data.setComment(getJsonString(object, "pl_memo"));

                dataList.add(data);

            }

            return dataList;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getJsonString(JSONObject object, String member) {

        if (object.has(member)) {
            try {
                return object.getString(member);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private class PointResultListAdapter extends BaseAdapter {

        public PointResultListAdapter(ArrayList<PointResultData> mDataList) {
            this.mDataList = mDataList;
        }

        private ArrayList<PointResultData> mDataList;

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder h;

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(
                        LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_point_result, parent, false);

                h = new ViewHolder();
                h.name = (TextView)convertView.findViewById(R.id.row_point_result_name);
                h.date = (TextView)convertView.findViewById(R.id.row_point_result_date);
                h.comment = (TextView)convertView.findViewById(R.id.row_point_result_comment);

                convertView.setTag(h);
            } else {
                h = (ViewHolder)convertView.getTag();
            }

            PointResultData item = (PointResultData)getItem(position);

            h.name.setText(item.getName());
            h.date.setText(item.getDate());
            h.comment.setText(item.getComment());

            return convertView;
        }

        private class ViewHolder {
            private TextView name;

            private TextView date;

            private TextView comment;
        }
    }
}
