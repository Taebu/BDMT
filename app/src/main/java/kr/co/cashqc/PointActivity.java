
package kr.co.cashqc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anp on 14. 11. 18..
 * 
 * @author Jung-Hum Cho
 */
public class PointActivity extends BaseActivity {

    public static ArrayList<Boolean> positionArray;

    public CheckBox checkBox;

    private ListView mListView;

    private ListAdapter mAdapter;

    private ArrayList<PointData> mPointDatas;

    private ArrayList<PointData> mCheckedDatas;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private ArrayList<PointData> mUserDatas;

    private String mPhoneNum;

    private CustomDialog dialog;

    private static int sCheckedCount;

    private static Button sBtnRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        // activity killer
        killer.addActivity(this);

        // loading dialog init.
        dialog = new CustomDialog(PointActivity.this);

        // resource init
        checkBox = (CheckBox)findViewById(R.id.check_point);

        mListView = (ListView)findViewById(R.id.list_point);

        //
        mPhoneNum = getIntent().getStringExtra("phoneNum");

        //
        sBtnRequest = (Button)findViewById(R.id.btn_cashrequest);

        if (Build.VERSION.SDK_INT > 10)
            sBtnRequest.setAlpha(0.1f);

        if (mPhoneNum != null) {
            new LoadPointTask().execute(mPhoneNum);

            sBtnRequest.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCheckedDatas = new ArrayList<PointData>();
                    for (int i = 0; i < positionArray.size(); i++) {
                        if (positionArray.get(i)) {
                            mCheckedDatas.add(mPointDatas.get(i));
                        }
                    }

                    Log.e("jaypoint", positionArray + "\r?" + sCheckedCount);

                    if (sCheckedCount == 5) {
                        if (mUserDatas.get(0).getHolder().isEmpty()
                                || mUserDatas.get(0).getAccNum().isEmpty()) {
                            new CustomDialog(PointActivity.this, "계좌정보를 설정해주세요").show();
                        } else {
                            new CustomDialog(PointActivity.this, mRequestOnClickListener).show();

                        }

                    } else {
                        Toast.makeText(PointActivity.this, "포인트 5개 선택하여 주세요", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }

    private View.OnClickListener mRequestOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new RequestPointTask().execute(mUserDatas, mCheckedDatas);
        }
    };

    public void mOnClick(View view) {

        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_modify_member:
                intent = new Intent(this, ModifyActivity.class);
                intent.putExtra("phoneNum", mPhoneNum);
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    private class LoadPointTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
            // list set init.
            mPointDatas = new ArrayList<PointData>();
            mUserDatas = new ArrayList<PointData>();

            sCheckedCount = 0;
            positionArray = new ArrayList<Boolean>();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String baseUrl = "http://cashq.co.kr/m/ajax_data/get_point_list.php?phone=";
            return new JSONParser().getJSONObjectFromUrl(baseUrl + params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            JSONArray pointArray = null;
            try {
                pointArray = object.getJSONArray("posts");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (pointArray != null) {
                for (int i = 0; i < pointArray.length(); i++) {
                    try {
                        JSONObject obj = pointArray.getJSONObject(i);
                        mPointDatas.add(getPointData(obj));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Log.e("JAY", "else if");
            }

            TextView tvAccrue = (TextView)findViewById(R.id.tv_accrue);
            int point = 0;
            for (int i = 0; i < mPointDatas.size(); i++) {
                try {
                    point += Integer.parseInt(mPointDatas.get(i).getPoint());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            tvAccrue.append(String.valueOf(point));

            mAdapter = new ListAdapter(getApplicationContext(), R.layout.row_point, mPointDatas);
            mListView.setAdapter(mAdapter);

            mUserDatas.add(getUserData(object));

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private PointData getPointData(JSONObject object) {

        try {

            PointData datas = new PointData();

            datas.setName(object.getString("store_name"));
            datas.setDate(object.getString("ed_dt"));
            datas.setStatus(object.getString("status"));
            datas.setPoint(object.getString("point"));
            datas.setDeadline(object.getString("ev_st_dt") + " ~ " + object.getString("ev_ed_dt"));
            datas.setComment(object.getString("memo"));

            datas.setSeq(object.getString("seq"));
            datas.setType(object.getString("type"));

            datas.setPoint_seq(object.getString("seq") + "_" + object.getString("type"));

            datas.setEventcode(object.getString("eventcode"));
            datas.setBiz_code(object.getString("biz_code"));

            return datas;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PointData getUserData(JSONObject object) {
        try {
            PointData datas = new PointData();

            datas.setAccNum(object.getString("accnum"));
            datas.setHolder(object.getString("holder"));
            datas.setBank(object.getString("bank"));
            datas.setUserid(object.getString("userid").trim());
            datas.setBiz_code(object.getString("biz_code"));

            return datas;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class RequestPointTask extends AsyncTask<ArrayList<PointData>, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(ArrayList<PointData>... params) {

            PointData userDatas = params[0].get(0);
            ArrayList<PointData> pointDatas = params[1];

            StringBuilder sb = new StringBuilder(
                    "http://cashq.co.kr/m/request_save.php?board=0507_cash&mode=json");

            try {
                sb.append("&bank=").append(userDatas.getBank());

                String holder = userDatas.getHolder();
                try {
                    holder = holder.replace(" ", "");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    sb.append("&holder=").append(holder);
                }

                String accnum = userDatas.getAccNum();
                try {
                    accnum = accnum.replace(" ", "");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    sb.append("&accnum=").append(accnum);
                }

                // sb.append("&accnum=").append(userDatas.getAccNum());
                // sb.append("&holder=").append(userDatas.getHolder());
                sb.append("&userid=").append(userDatas.getUserid());
                sb.append("&mb_hp=").append(mPhoneNum);

                sb.append("&biz_code=").append(pointDatas.get(0).getBiz_code());

                sb.append("&eventcode=").append(pointDatas.get(0).getEventcode());

                for (PointData al : pointDatas) {
                    sb.append("&chk_seq[]=").append(al.getPoint_seq());
                }
                Log.e("JAYPOINT", "url : " + sb.toString());
                return new JSONParser().getJSONObjectFromUrl(sb.toString());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        // http://cashq.co.kr/m/request_save.php?board=0507_cash&mode=json&bank=기업은행&accnum=01045892754&holder=도치상
        // (가온패키지)&userid=kvym4692&mb_hp=01045892754&biz_code=a026&eventcode=a026_2&chk_seq[]=77318_W03&chk_seq[]=74016_W07&chk_seq[]=72473_W06&chk_seq[]=64546_W08&chk_seq[]=56442_W04
        // http://cashq.co.kr/m/request_save.php?board=0507_cash&mode=json&bank=기업은행&accnum=16901992103014&holder=강
        // 병
        // 용&userid=ifaz9202&mb_hp=01028744168&biz_code=a018&eventcode=a018_2&chk_seq[]=76353_W01&chk_seq[]=74639_W01&chk_seq[]=64410_W06&chk_seq[]=62692_W01&chk_seq[]=53657_W01
        // http://cashq.co.kr/m/request_save.php?board=0507_cash&mode=json&bank=신한은행&holder=유기호&accnum=33604501811&userid=zkfhsdmldkcl
        // &mb_hp=01046158212&biz_code=a013&eventcode=a013_4&chk_seq[]=118133_W07&chk_seq[]=105485_W01&chk_seq[]=102562_W08&chk_seq[]=101063_W01&chk_seq[]=86294_W06
        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            String msg = null;
            try {
                msg = object.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // startActivity(new Intent(PointActivity.this,
            // PointActivity.class));
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            sCheckedCount = 0;

            Intent intent = new Intent(PointActivity.this, PointActivity.class);
            intent.putExtra("phoneNum", mPhoneNum);
            new CustomDialog(PointActivity.this, msg, PointActivity.this, intent).show();
        }
    }

    public static class ListAdapter extends BaseAdapter {

        private long mLastClickTime = 0;

        public ListAdapter(Context context, int viewResourceId, ArrayList<PointData> objects) {

            this.mContext = context;
            this.mViewResId = viewResourceId;
            this.objects = objects;

            positionArray = new ArrayList<Boolean>(objects.size());

            for (PointData object : objects) {
                positionArray.add(false);
            }

        }

        private PointData datas;

        private Context mContext;

        private int mViewResId;

        private ArrayList<PointData> objects;

        private class ViewHolder {

            private TextView tvDate, tvName, tvStatus, tvDeadline, tvComment, tvPoint;

            private CheckBox checkBox;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder h;

            if (convertView == null) {
                convertView = View.inflate(mContext, mViewResId, null);

                h = new ViewHolder();

                h.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
                h.tvName = (TextView)convertView.findViewById(R.id.tv_name);
                h.tvStatus = (TextView)convertView.findViewById(R.id.tv_status);
                h.tvDeadline = (TextView)convertView.findViewById(R.id.tv_deadline);
                h.tvComment = (TextView)convertView.findViewById(R.id.tv_comment);
                h.tvPoint = (TextView)convertView.findViewById(R.id.tv_point);
                h.checkBox = (CheckBox)convertView.findViewById(R.id.check_point);
                convertView.setTag(h);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        LinearLayout ll = (LinearLayout)v;
                        CheckBox cb = (CheckBox)ll.findViewById(R.id.check_point);
                        PointData data = (PointData)cb.getTag();

                        if (data.getStatus().equals("사용가능")) {
                            if (cb.isChecked() && sCheckedCount <= 5) {
                                sCheckedCount--;
                                cb.setChecked(false);
                                positionArray.set(data.getPosition(), false);

                            } else if (!cb.isChecked() && sCheckedCount < 5) {
                                sCheckedCount++;
                                cb.setChecked(true);
                                positionArray.set(data.getPosition(), true);

                            }
                        }

                        if (sCheckedCount == 5) {
                            if (Build.VERSION.SDK_INT > 10)
                                sBtnRequest.setAlpha(1.0f);
                            h.checkBox.setEnabled(false);
                        } else {
                            if (Build.VERSION.SDK_INT > 10)
                                sBtnRequest.setAlpha(0.1f);
                        }
                        Log.e("JAYPOINT",
                                "pos : " + position + "data.getPosition() : " + data.getPosition());

                        data.setChecked(cb.isChecked());
                    }
                });

            } else {
                h = (ViewHolder)convertView.getTag();
            }

            datas = getItem(position);

            h.tvName.setText(datas.getName());
            h.tvDate.setText(datas.getDate());
            h.tvPoint.setText(datas.getPoint());
            h.tvComment.setText(datas.getComment());
            h.tvDeadline.setText("포인트 사용 기간 : " + datas.getDeadline());
            h.tvStatus.setText(datas.getStatus());
            h.tvStatus.setTag(datas.getStatus());

            h.checkBox.setEnabled(false);

            String stauts = (String)h.tvStatus.getTag();

            if ("사용가능".equals(stauts)) {
                h.checkBox.setVisibility(View.VISIBLE);
            } else {

                h.checkBox.setVisibility(View.INVISIBLE);
            }

            // PointData data = objects.get(position);
            datas.setPosition(position);
            // h.checkBox.setFocusable(false);

            h.checkBox.setChecked(datas.isChecked());
            h.checkBox.setTag(datas);

            Log.e("JAYPOINT", "pos : " + position);
            return convertView;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public PointData getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
