package com.anp.ulsanfood;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.anp.ulsanfood.gcm.Util;

import static com.anp.ulsanfood.Utils.setListViewHeightBasedOnChildren;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 22..
 */
public class CallLogActivity extends BaseActivity {

    private Activity mThis = CallLogActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        String[] seq = getIntent().getStringArrayExtra("seq");

        new CallLogTask().execute(seq);

        findViewById(R.id.calllog_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.saveSharedPreferences_boolean(mThis, "calllog_autologin", false);
                startActivity(new Intent(mThis, MainActivity.class));
            }
        });

    }

    private class CallLogTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            // String seq = params[0];

            ArrayList<String> seq = new ArrayList<String>();

            Collections.addAll(seq, params);

            Uri.Builder ub = Uri.parse("http://cashq.co.kr/m/ajax_data/get_calllog.php")
                    .buildUpon();

            for (String s : seq) {
                ub.appendQueryParameter("st_seq[]", s);
            }

            String url = ub.build().toString();

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            ArrayList<String> callList = new ArrayList<String>();

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    String item = "날짜 : " + object.getString("END_DT") + "\n번호 : "
                            + object.getString("CALLER_NUM");

                    callList.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ListView listView = (ListView) findViewById(R.id.calllog_listview);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mThis,
                    android.R.layout.simple_list_item_1, callList);

            listView.setAdapter(adapter);

            setListViewHeightBasedOnChildren(listView);

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

//        private void setListViewHeightBasedOnChildren(ListView listView) {
//            Adapter adapter = listView.getAdapter();
//
//            if (adapter == null) {
//                return;
//            }
//
//            int totalHeight = 0;
//            for (int i = 0; i < adapter.getFreeCount(); i++) {
//                View listItem = adapter.getView(i, listView.getChildAt(0), listView);
//                listItem.measure(0, 0);
//                totalHeight += listItem.getMeasuredHeight();
//            }
//
//            ViewGroup.LayoutParams params = listView.getLayoutParams();
//            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getFreeCount() - 1));
//            listView.setLayoutParams(params);
//            listView.requestLayout();
//
//        }
    }
}
