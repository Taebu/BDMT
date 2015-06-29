
package kr.co.cashqc;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ Jung-Hum Cho Created by anp on 15. 6. 25..
 */
public class PointHistoryFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_history, container, false);

        Bundle bundle = getArguments();

        String phoneNum = bundle.getString("phoneNum");

        mListView = (ListView)view.findViewById(R.id.call_history_listview);

        new SPLTask().execute(phoneNum);

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class SPLTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String phoneNum = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/get_spl.php").buildUpon()
                    .appendQueryParameter("called", phoneNum).build().toString();

            Log.e(TAG, "URL : " + url);

            return new JsonParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                JSONArray array = jsonObject.getJSONArray("posts");

                ArrayList<SplData> dataList = new ArrayList<SplData>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);

                    if (object.has("stype")) {
                        if (object.getString("stype").startsWith("PNT")) {

                            SplData data = new SplData();

                            data.setNum(String.valueOf(array.length() - 1));

                            if (object.has("wr_subject"))
                                data.setSubject(object.getString("wr_subject"));

                            if (object.has("regdate"))
                                data.setDate(object.getString("regdate"));

                            if (object.has("caller"))
                                data.setCaller(object.getString("caller"));

                            if (object.has("called"))
                                data.setCalled(object.getString("called"));

                            dataList.add(data);
                        }
                    }
                }

                PointHistoryListAdapter adapter = new PointHistoryListAdapter(getActivity(),
                        dataList);

                mListView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class PointHistoryListAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;

        private ArrayList<SplData> mDataList;

        public PointHistoryListAdapter(Context context, ArrayList<SplData> dataList) {
            super();

            inflater = LayoutInflater.from(context);

            mDataList = dataList;
        }

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

            View v = convertView;

            final ViewHolder h;

            if (v == null) {
                h = new ViewHolder();
                v = inflater.inflate(R.layout.row_point_history, null);

                h.text = (TextView)v.findViewById(R.id.row_point_history_text);

                v.setTag(h);
            } else {
                h = (ViewHolder)v.getTag();
            }

            SplData item = (SplData)getItem(position);

            h.text.setText(item.getSubject() + "\n날짜 : " + item.getDate());

            return v;
        }

        private class ViewHolder {
            private TextView text;
        }
    }
}
