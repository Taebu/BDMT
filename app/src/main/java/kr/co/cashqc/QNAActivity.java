
package kr.co.cashqc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static kr.co.cashqc.Utils.initExpandableListViewHeight;
import static kr.co.cashqc.Utils.setExpandableListViewHeight;

/**
 * @author Jung-Hum Cho Created by anp on 15. 4. 8..
 */
public class QNAActivity extends BaseActivity {

    private Activity mThis = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        String phoneNum = getPhoneNumber();

        new QNATask().execute("010-7931-0141");
        // new QNATask().execute(phoneNum);
    }

    private class QNATask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String phoneNum = params[0];

            String url = "http://cashq.co.kr/m/ajax_data/get_board.php?board=qna_1&mb_hp="
                    + phoneNum;

            return new JSONParser().getJSONObjectFromUrl(url);
            // return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {

                JSONArray array = jsonObject.getJSONArray("posts");

                ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < array.length(); i++) {

                    HashMap<String, String> map = new HashMap<String, String>();

                    JSONObject object = array.getJSONObject(i);

                    Log.e("QNAActivity", "i : " + i);

                    // 질문
                    if (object.has("wr_subject"))
                        map.put("q_subject", object.getString("wr_subject"));

                    if (object.has("wr_content"))
                        map.put("q_content", object.getString("wr_content"));

                    if (object.has("wr_datetime"))
                        map.put("q_datetime", object.getString("wr_datetime"));

                    // 답변
                    if (object.has("reply")) {
                        JSONObject reply = object.getJSONObject("reply");

                        if (reply.has("wr_content")) {
                            map.put("a_content", reply.getString("wr_content"));

                            if (reply.has("wr_datetime"))
                                map.put("a_datetime", reply.getString("wr_datetime"));
                        }
                    }

                    dataList.add(map);
                }

                Log.e("QNAActivity", "dataListSize : " + dataList.size());
                ExpandableListView listView = (ExpandableListView)findViewById(R.id.qna_listview);
                QNAListAdapter adapter = new QNAListAdapter(mThis, dataList);
                listView.setAdapter(adapter);

                initExpandableListViewHeight(listView);

                listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
                        setExpandableListViewHeight(parent, groupPosition);
                        return false;
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }
}
