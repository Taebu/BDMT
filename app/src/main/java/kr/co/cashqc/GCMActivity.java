
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.cashqc.gcm.Util;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 23..
 */
public class GCMActivity extends BaseActivity {

    private Activity mThis = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        String phoneNum = getIntent().getStringExtra("phoneNum");

        new SPLTask().execute("01077430009");

        findViewById(R.id.calllog_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.saveSharedPreferences_boolean(mThis, "calllog_autologin", false);
                startActivity(new Intent(mThis, MainActivity.class));
            }
        });
    }

    private class SPLTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String phoneNum = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/get_spl.php").buildUpon()
                    .appendQueryParameter("called", phoneNum).build().toString();

            return new JSONParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            ArrayList<String> callList = new ArrayList<String>();

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    callList.add(object.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ListView listView = (ListView)findViewById(R.id.calllog_listview);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mThis,
                    android.R.layout.simple_list_item_1, callList);

            listView.setAdapter(adapter);

            setListViewHeightBasedOnChildren(listView);

            if (mDialog.isShowing())
                mDialog.dismiss();
        }

        private void setListViewHeightBasedOnChildren(ListView listView) {
            Adapter adapter = listView.getAdapter();

            if (adapter == null) {
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, listView.getChildAt(0), listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();

        }
    }
}
