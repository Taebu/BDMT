
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

        new QNATask().execute(phoneNum);
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

            // String phone = params[0];
            // String url =
            // "http://cashq.co.kr/m/ajax_data/get_board.php?board=qna_1&mb_hp="
            // + phone;

            String url = "http://cashq.co.kr/m/ajax_data/get_board.php?board=qna_1&mb_hp=010-7931-0141";

            return new JSONParser().getJSONObjectFromUrl(url);
            // return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            ArrayList<BoardData> articleList = makeBoardData(jsonObject);

            if (articleList.size() != 0) {

                ExpandableListView listView = (ExpandableListView)findViewById(R.id.qna_listview);
                QNAListAdapter adapter = new QNAListAdapter(mThis, articleList);
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
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        private ArrayList<BoardData> makeBoardData(JSONObject jsonObject) {

            ArrayList<BoardData> dataList = new ArrayList<BoardData>();

            try {

                JSONArray array = null;

                if (jsonObject.has("posts")) {
                    array = jsonObject.getJSONArray("posts");
                } else if (jsonObject.has("reply")) {
                    array = jsonObject.getJSONArray("reply");
                }

                if (array != null) {

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        BoardData articleData = new BoardData();

                        Log.e("QNAActivity", "i : " + i);

                        // 질문
                        if (object.has("wr_name"))
                            articleData.setName(object.getString("wr_name"));

                        if (object.has("wr_1"))
                            articleData.setPhone(object.getString("wr_1"));

                        if (object.has("wr_subject"))
                            articleData.setSubject(object.getString("wr_subject"));

                        if (object.has("wr_content"))
                            articleData.setContent(object.getString("wr_content"));

                        if (object.has("wr_datetime"))
                            articleData.setDatetime(object.getString("wr_datetime"));

                        ArrayList<BoardData> replyData = new ArrayList<BoardData>();

                        if (object.has("reply")) {
                            replyData = makeBoardData(object);
                        }

                        articleData.setReply(replyData);

                        dataList.add(articleData);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dataList;
        }
    }
}
