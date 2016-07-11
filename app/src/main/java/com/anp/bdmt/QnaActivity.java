
package com.anp.bdmt;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.anp.bdmt.gcm.Util.getPhoneNumber;

/**
 * @author Jung-Hum Cho Created by anp on 15. 4. 8..
 */
public class QnaActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private Activity mThis = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        final String phoneNum = getPhoneNumber(this);

        new QNATask().execute(phoneNum);

        findViewById(R.id.qna_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QnaDialog qnaDialog = new QnaDialog(mThis, phoneNum);
                qnaDialog.show();
                qnaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        new QNATask().execute(phoneNum);
                    }
                });
            }
        });

    }

    private class QNATask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String phone = params[0];
            // String url =
            // "http://cashq.co.kr/m/ajax_data/get_board.php?board=qna_1&mb_hp="
            // + phone;
            String url = "http://cashq.co.kr/m/ajax_data/get_board.php?board=qna_bdtt&mb_hp=" + phone;

            Log.e(TAG, "url : " + url);

            return new JsonParser().getJSONObjectFromUrl(url);
            // return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e(TAG, "JsonParse : " + jsonObject);

            ArrayList<BoardData> articleList = makeBoardData(jsonObject);

            if (articleList.size() != 0) {

                ExpandableListView listView = (ExpandableListView)findViewById(R.id.qna_listview);
                QnaListAdapter adapter = new QnaListAdapter(mThis, articleList);
                listView.setAdapter(adapter);

//                setExpandableListViewHeight1(listView, -1);

                listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
//                        setExpandableListViewHeight1(parent, groupPosition);
                        return false;
                    }
                });
            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
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
                        if (object.has("wr_name")) {
                            articleData.setName(object.getString("wr_name"));
                        }

                        if (object.has("wr_1")) {
                            articleData.setPhone(object.getString("wr_1"));
                        }

                        if (object.has("wr_subject")) {
                            articleData.setSubject(object.getString("wr_subject"));
                        }

                        if (object.has("wr_content")) {
                            articleData.setContent(object.getString("wr_content"));
                        }

                        if (object.has("wr_datetime")) {
                            articleData.setDatetime(object.getString("wr_datetime"));
                        }

                        ArrayList<BoardData> replyDataList = new ArrayList<BoardData>();

//                        Log.e(TAG, "reply : " + object.getString("reply"));

                        if(object.has("reply")) {
                            if ("[]".equals(object.getString("reply"))) {
                                Log.e(TAG, "reply hasnt'");
                                BoardData replyData = new BoardData();
                                replyData.setDatetime("");
                                replyData.setContent("미답변 상태입니다.");
                                replyDataList.add(replyData);
                            } else {
                                Log.e(TAG, "reply has");
                                replyDataList = makeBoardData(object);
                            }
                        }

                        articleData.setReply(replyDataList);

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
