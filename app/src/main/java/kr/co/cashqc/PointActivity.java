
package kr.co.cashqc;

import static kr.co.cashqc.MainActivity.APP_ID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.cashqc.gcm.Util;

/**
 * Created by anp on 14. 11. 18..
 *
 * @author Jung-Hum Cho
 */
public class PointActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    public static ArrayList<Boolean> sPositionArray;

    public CheckBox checkBox;

    private ListView mListView;

    private PointListAdapter mAdapter;

    private ArrayList<PointData> mPointDataList;

    private ArrayList<PointData> mCheckedDataList;

    private static final int NOT_SELECT = 0;

    private static final int FIVE_POINT = 1;

    private static final int FREE_POINT = 2;

    public static int POINT_STATUS = NOT_SELECT;

    private final int mCheckLimit = 5;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        POINT_STATUS = NOT_SELECT;

    }

    private ArrayList<PointData> mUserDatas;

    private String mPhoneNum;

    private CustomDialog dialog;

    private static int sCheckedCount;

    private static Button sBtnRequest;

    private Activity mThis = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        // activity killer
        killer.addActivity(this);

        // loading mDialog init.
        dialog = new CustomDialog(PointActivity.this);

        // resource init
        checkBox = (CheckBox)findViewById(R.id.row_point_check);

        mListView = (ListView)findViewById(R.id.list_point);

        //
        mPhoneNum = getIntent().getStringExtra("phoneNum");

        POINT_STATUS = NOT_SELECT;

        //
        sBtnRequest = (Button)findViewById(R.id.btn_cashrequest);

        if (Build.VERSION.SDK_INT > 10) {
            sBtnRequest.setAlpha(0.1f);
        }

        if (mPhoneNum != null) {
            new LoadPointTask().execute(mPhoneNum);

            sBtnRequest.setOnClickListener(this);
        }
    }

    private View.OnClickListener mRequestOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new RequestPointTask().execute(mUserDatas, mCheckedDataList);
        }
    };

    public void mOnClick(View view) {

        Intent intent = null;

        switch (view.getId()) {
            case R.id.btn_modify_member:
                intent = null;
                new BankAccountDialog(this, mPhoneNum).show();
                break;
            case R.id.point_logout:
                Util.saveSharedPreferences_boolean(mThis, "point_autologin", false);
                intent = new Intent(mThis, MainActivity.class);
                break;
            case R.id.point_init:
                POINT_STATUS = NOT_SELECT;
                sCheckedCount = 0;
                intent = new Intent(PointActivity.this, PointActivity.class);
                break;

        }
        if (intent != null) {
            intent.putExtra("phoneNum", mPhoneNum);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    @Override
    public void onClick(View v) {

        if (mUserDatas.get(0).getHolder().isEmpty() || mUserDatas.get(0).getAccNum().isEmpty()) {
            new CustomDialog(PointActivity.this, "계좌 정보를 설정해주세요").show();
            return;
        }

        mCheckedDataList = new ArrayList<PointData>();

        for (int i = 0; i < sPositionArray.size(); i++) {
            if (sPositionArray.get(i)) {

                mCheckedDataList.add(mPointDataList.get(i));

                Log.e(TAG, "pointSeq : " + mPointDataList.get(i).getSeq() + ", pointName : "
                        + mPointDataList.get(i).getName() + ", pointDate : "
                        + mPointDataList.get(i).getDate());
            }
        }

        int total = 0;

        if (POINT_STATUS == FREE_POINT) {

            for (PointRuleData p : mCheckedDataList.get(0).getPointRuleList()) {
                if (sCheckedCount == p.getCount()) {
                    total = p.getAmount();
                    break;
                }
            }

            new CustomDialog(PointActivity.this, mRequestOnClickListener, total).show();

        } else if (POINT_STATUS == FIVE_POINT) {

            if (mCheckedDataList.size() == 0) {

                new CustomDialog(mThis, "포인트 오류 입니다.\n02-1599-9495로 문의 주세요.");

            } else {

                boolean isDuplicate = false;

                String storeSeq = mCheckedDataList.get(0).getStoreSeq();

                for (PointData data : mCheckedDataList) {

                    String targetSeq = data.getStoreSeq();

                    if (storeSeq.equals(targetSeq)) {
                        isDuplicate = true;
                    } else {
                        isDuplicate = false;
                        break;
                    }

                }

                if (isDuplicate) {

                    new CustomDialog(this, "동일 업소 주문 건은 적립이 불가합니다.").show();

                } else {

                    for (PointData p : mCheckedDataList) {
                        total += Integer.parseInt(p.getPoint());
                    }

                    new CustomDialog(PointActivity.this, mRequestOnClickListener, total).show();
                }

            }

        }

    }

    private class LoadPointTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing()) {
                dialog.show();
            }
            // list set init.
            mPointDataList = new ArrayList<PointData>();
            mUserDatas = new ArrayList<PointData>();

            sCheckedCount = 0;
            sPositionArray = new ArrayList<Boolean>();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = "http://cashq.co.kr/m/ajax_data/get_point_list2.php?phone=" + params[0]
                    + "&appid=" + APP_ID;
            Log.e(TAG, "URL : " + url);
            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                JSONArray pointArray = object.getJSONArray("posts");
                // JSONObject pointRule =
                // object.getJSONArray("agency").getJSONObject(0);
                // Log.e(TAG, "pointRule : " + pointRule);

                // if ("on".equals(pointRule.getString("pointset"))) {
                // setPointRule(pointRule);
                // }

                if (pointArray != null) {
                    for (int i = 0; i < pointArray.length(); i++) {
                        JSONObject obj = pointArray.getJSONObject(i);
                        mPointDataList.add(getPointData(obj));

                    }
                } else {
                    Log.e(TAG, "else if");
                }

                TextView tvAccrue = (TextView)findViewById(R.id.tv_accrue);

                tvAccrue.setText("포인트를 선택해주세요");

                mAdapter = new PointListAdapter(getApplicationContext(), R.layout.row_point,
                        mPointDataList);

                mListView.setAdapter(mAdapter);

                mUserDatas.add(getUserData(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private void setPointRule(JSONObject pointRule) {

            ArrayList<PointRuleData> mPointRuleList = new ArrayList<PointRuleData>();

            try {
                String[] pointItems;
                pointItems = pointRule.getString("point_items").split("&");

                for (String item : pointItems) {

                    PointRuleData pointRuleData = new PointRuleData();

                    String[] countAmount = item.split("_");

                    pointRuleData.setCount(Integer.parseInt(countAmount[0]));
                    pointRuleData.setAmount(Integer.parseInt(countAmount[1]));

                    mPointRuleList.add(pointRuleData);

                }

                for (PointRuleData data : mPointRuleList) {
                    Log.e(TAG, "pointRuleList : " + data.getCount() + ", " + data.getAmount());
                }

                String pointRuleContent = null;

                for (int i = 0; i < mPointRuleList.size(); i++) {

                    String count = String.format("%d개 ", mPointRuleList.get(i).getCount());
                    String amount = String.format("%,d원 ", mPointRuleList.get(i).getAmount());

                    String rule = count + amount;

                    if (i == 0) {

                        pointRuleContent = rule;

                    } else {

                        if (i % 2 == 0 || i == mPointRuleList.size() - 1) {
                            pointRuleContent += rule;
                        } else if (i % 2 == 1) {
                            pointRuleContent += rule + "\n";
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private PointData getPointData(JSONObject object) {

        try {

            PointData datas = new PointData();

            datas.setStoreSeq(object.getString("store_seq"));

            datas.setName(object.getString("store_name"));
            datas.setDate(object.getString("ed_dt").replace(" ", "\n"));
            datas.setStatus(object.getString("status"));
            datas.setPoint(object.getString("point"));
            datas.setDeadline(object.getString("ev_st_dt") + " ~ " + object.getString("ev_ed_dt"));
            datas.setComment(object.getString("memo").replace("\r\n", ""));

            if (object.has("location")) {

                String location = object.getString("location");

                datas.setLocation(location);

            } else {
                datas.setPointType(FIVE_POINT);
                datas.setLocation("");
            }

            datas.setSeq(object.getString("seq"));

            datas.setType(object.getString("pt_stat"));

            if (object.getString("pt_stat").equals("freept")) {
                datas.setIsFreePoint(true);
                datas.setPointType(FREE_POINT);
            } else {
                datas.setIsFreePoint(false);
                datas.setPointType(FIVE_POINT);
            }

            datas.setPoint_seq(object.getString("seq") + "_" + object.getString("type"));

            datas.setEventcode(object.getString("eventcode"));
            datas.setBiz_code(object.getString("biz_code"));

            if (object.has("pre_pay")) {
                datas.setGrade(object.getString("pre_pay"));
            }

            ArrayList<PointRuleData> pointRuleList = new ArrayList<PointRuleData>();
            if (object.getString("pointset").equals("on")) {

                try {
                    String[] pointItems;
                    pointItems = object.getString("point_items").split("&");

                    for (String item : pointItems) {

                        PointRuleData pointRuleData = new PointRuleData();

                        String[] countAmount = item.split("_");

                        pointRuleData.setCount(Integer.parseInt(countAmount[0]));
                        pointRuleData.setAmount(Integer.parseInt(countAmount[1]));

                        pointRuleList.add(pointRuleData);

                    }

                    for (PointRuleData data : pointRuleList) {
                        // Log.e(TAG, "pointRuleList : " + data.getCount() +
                        // ", " + data.getAmount());
                    }

                    String pointRuleContent = "";

                    for (int i = 0; i < pointRuleList.size(); i++) {

                        String count = String.format("%d개 ", pointRuleList.get(i).getCount());
                        String amount = String.format("%,d원 ", pointRuleList.get(i).getAmount());

                        String rule = count + amount;

                        if (i == pointRuleList.size() - 1) {
                            pointRuleContent += rule;
                        } else {
                            pointRuleContent += rule + "\n";
                        }
                    }

                    datas.setPointRuleContent(pointRuleContent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (object.has("incbiz")) {

                Log.e(TAG, "hasIncludeBizCode? - YES");

                String[] incbiz = object.getString("incbiz").split("&");

                datas.setIncludeCodes(incbiz);

                for (int i = 0; i < incbiz.length; i++) {
                    Log.e(TAG, "includeBizCode - " + i + " : " + incbiz[i]);
                }
            } else {
                Log.e(TAG, "hasIncludeBizCode? - NO");
            }

            datas.setPointRuleList(pointRuleList);

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

                sb.append("&point_type=").append(pointDatas.get(0).getType());

                // sb.append("&testmode=").append("test");

                // sb.append("&ed_type=").append("");

                for (PointData al : pointDatas) {
                    sb.append("&chk_seq[]=").append(al.getPoint_seq());
                }
                Log.e(TAG, "url : " + sb.toString());
                return new JsonParser().getJSONObjectFromUrl(sb.toString());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

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
            POINT_STATUS = NOT_SELECT;

            Intent intent = new Intent(PointActivity.this, PointActivity.class);
            intent.putExtra("phoneNum", mPhoneNum);
            new CustomDialog(PointActivity.this, msg, PointActivity.this, intent).show();
        }
    }

    private class PointListAdapter extends BaseAdapter implements View.OnClickListener {

        private long mLastClickTime = 0;

        private final String TAG = getClass().getSimpleName();

        private String[] includeCodes = null;

        public PointListAdapter(Context context, int viewResourceId, ArrayList<PointData> objects) {

            this.mContext = context;
            this.mViewResId = viewResourceId;
            this.objects = objects;

            sPositionArray = new ArrayList<Boolean>(objects.size());

            for (PointData object : objects) {
                sPositionArray.add(false);
            }

        }

        private PointData item;

        private Context mContext;

        private int mViewResId;

        private ArrayList<PointData> objects;

        private class ViewHolder {

            private TextView date, name, status, deadline, comment, value, grade;

            private CheckBox checkBox;

            private RelativeLayout rowLayout;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder h;

            if (convertView == null) {
                convertView = View.inflate(mContext, mViewResId, null);

                h = new ViewHolder();

                h.date = (TextView)convertView.findViewById(R.id.row_point_date);
                h.name = (TextView)convertView.findViewById(R.id.row_point_name);
                h.status = (TextView)convertView.findViewById(R.id.row_point_status);
                h.deadline = (TextView)convertView.findViewById(R.id.row_point_deadline);
                h.comment = (TextView)convertView.findViewById(R.id.row_point_comment);
                h.value = (TextView)convertView.findViewById(R.id.row_point_value);
                h.checkBox = (CheckBox)convertView.findViewById(R.id.row_point_check);
                h.grade = (TextView)convertView.findViewById(R.id.row_point_grade);

                convertView.setTag(h);

            } else {
                h = (ViewHolder)convertView.getTag();
            }

            item = getItem(position);

            if (item.isFreePoint()) {
                h.value.setText(item.getPointRuleContent());
                // h.value.setTextColor(Color.parseColor("#E94230"));
                h.grade.setText(item.getLocation());
            } else {

                h.value.setText(item.getPoint() + "원");

                if (item.getGrade().equals("gl")) {

                    h.grade.setText("GOLD");
                    h.grade.setTextColor(Color.parseColor("#daa520"));

                } else if (item.getGrade().equals("sl")) {

                    h.grade.setText("SILVER");
                    h.grade.setTextColor(Color.parseColor("#C0C0C0"));

                } else if (item.getGrade().equals("on")) {

                    h.grade.setText("CASHQ");
                    h.grade.setTextColor(Color.parseColor("#E94230"));

                } else if (item.getGrade().equals("")) {

                    h.grade.setText("일반");
                    h.grade.setTextColor(Color.parseColor("#CCCCCC"));

                }
            }

            h.name.setText(item.getName());
            h.date.setText(item.getDate());
            h.comment.setText(item.getComment());
            h.deadline.setText("포인트 유효 기간 : " + item.getDeadline());
            h.status.setText(item.getStatus());
            h.status.setTag(item.getStatus());
            h.checkBox.setEnabled(false);

            item.setPosition(position);

            h.checkBox.setChecked(item.isChecked());
            h.checkBox.setTag(item);

            boolean isInclude = true;

            if (item.isFreePoint()) {
                if (includeCodes == null) {

                    isInclude = true;

                } else {

                    for (String inc : includeCodes) {
                        isInclude = false;
                        if (item.getBiz_code().equals(inc)) {
                            Log.e(TAG, "inc : " + inc + " item : " + item.getBiz_code());
                            isInclude = true;
                            break;
                        }
                    }
                }
            }

            if (POINT_STATUS == NOT_SELECT || item.getPointType() == POINT_STATUS && isInclude) {

                if (item.getStatus().equals("사용가능")) {
                    h.status.setText("사용가능");
                    convertView.setOnClickListener(this);
                    h.checkBox.setVisibility(View.VISIBLE);
                    h.status.setTextColor(Color.parseColor("#666666"));
                } else {
                    convertView.setOnClickListener(null);
                    h.checkBox.setVisibility(View.INVISIBLE);
                    h.status.setTextColor(Color.RED);
                    h.status.setText("선택불가 - " + item.getStatus());
                }
                convertView.setBackgroundColor(Color.WHITE);

            } else {
                convertView.setOnClickListener(null);
                h.status.setText("선택불가 - 다른 지역");
                h.status.setTextColor(Color.RED);
                convertView.setBackgroundColor(Color.LTGRAY);
                h.checkBox.setVisibility(View.INVISIBLE);
            }

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

        @Override
        public void onClick(View v) {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();
            CheckBox cb = (CheckBox)v.findViewById(R.id.row_point_check);
            PointData clickData = (PointData)cb.getTag();

            if (clickData.getStatus().equals("사용가능")) {

                boolean pointClickable = POINT_STATUS == NOT_SELECT
                        || POINT_STATUS == clickData.getPointType();

                if (pointClickable) {

                    if (cb.isChecked()) {

                        if (POINT_STATUS == FREE_POINT) {

                            sCheckedCount--;
                            cb.setChecked(false);
                            sPositionArray.set(clickData.getPosition(), false);

                        } else if (POINT_STATUS == FIVE_POINT && sCheckedCount <= mCheckLimit) {

                            sCheckedCount--;
                            cb.setChecked(false);
                            sPositionArray.set(clickData.getPosition(), false);
                        }

                        if (sCheckedCount == 0) {
                            // 초기화
                            POINT_STATUS = NOT_SELECT;
                            includeCodes = null;
                        }
                    } else {

                        if (clickData.isFreePoint()) {

                            POINT_STATUS = clickData.getPointType();

                            includeCodes = clickData.getIncludeCodes();

                            Log.e(TAG, "INCLUDE_CODES : " + Arrays.toString(includeCodes));

                            Log.e(TAG, "POINT_STATUS : " + POINT_STATUS);

                            sCheckedCount++;
                            cb.setChecked(true);
                            sPositionArray.set(clickData.getPosition(), true);

                        } else {
                            POINT_STATUS = FIVE_POINT;
                            if (sCheckedCount < mCheckLimit) {

                                Log.e(TAG, "POINT_STATUS : " + POINT_STATUS);

                                sCheckedCount++;
                                cb.setChecked(true);
                                sPositionArray.set(clickData.getPosition(), true);
                            }
                        }
                    }
                }

                if (POINT_STATUS == FIVE_POINT) {

                    if (sCheckedCount == mCheckLimit) {
                        if (Build.VERSION.SDK_INT > 10) {
                            sBtnRequest.setAlpha(1.0f);
                        }
                        sBtnRequest.setEnabled(true);
                    } else {
                        if (Build.VERSION.SDK_INT > 10) {
                            sBtnRequest.setAlpha(0.1f);
                        }
                        sBtnRequest.setEnabled(false);
                    }

                } else if (POINT_STATUS == FREE_POINT) {

                    for (PointRuleData pointRule : clickData.getPointRuleList()) {

                        if (sCheckedCount == pointRule.getCount()) {
                            if (Build.VERSION.SDK_INT > 10) {
                                sBtnRequest.setAlpha(1.0f);
                            }
                            sBtnRequest.setEnabled(true);
                            break;
                        } else {
                            if (Build.VERSION.SDK_INT > 10) {
                                sBtnRequest.setAlpha(0.1f);
                            }
                            sBtnRequest.setEnabled(false);
                        }
                    }

                }

                // Log.e("JAYPOINT", "pos : " + position +
                // "clickData.getPosition() : "
                // + clickData.getPosition());

                clickData.setChecked(cb.isChecked());
                notifyDataSetChanged();
            }
        }
    }
}
