
package kr.co.cashqc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static kr.co.cashqc.Utils.getVersionCode;
import static kr.co.cashqc.Utils.getVersionName;

/**
 * @author Jung-Hum Cho Created by anp on 15. 4. 8..
 */
public class NoticeActivity extends BaseActivity {

    private Activity mThis = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        String versionCode = getVersionCode(mThis);
        String versionName = getVersionName(mThis);

        TextView tvVersionCode = (TextView) findViewById(R.id.version_code);
        TextView tvVersionName = (TextView) findViewById(R.id.version_name);

        tvVersionCode.setText(versionCode);
        tvVersionName.setText(versionName);

        new NoticeTask().execute();
    }

    private class NoticeTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String url = "http://cashq.co.kr/m/ajax_data/get_board.php?board=gonggi";

            return new JsonParser().getJSONObjectFromUrl(url);
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

                    if (object.has("wr_subject"))
                        map.put("subject", object.getString("wr_subject"));

                    if (object.has("wr_content"))
                        map.put("content", object.getString("wr_content"));

                    dataList.add(map);
                }

                ExpandableListView listView = (ExpandableListView)findViewById(R.id.notice_listview);
                NoticeListAdapter adapter = new NoticeListAdapter(mThis, dataList);
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

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(this, MainActivity.class));
        finish();
        killer.removeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

}
