
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
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ Jung-Hum Cho Created by anp on 15. 6. 25..
 */
public class TtsHistoryFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private ExpandableListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tts_history, container, false);

        Bundle bundle = getArguments();

        String phoneNum = bundle.getString("phoneNum");

        mListView = (ExpandableListView)view.findViewById(R.id.tts_history_expandablelistview);

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
                        if (object.getString("stype").startsWith("TTS")) {

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

                            if (object.has("wr_content"))
                                data.setUrl(object.getString("wr_content"));

                            dataList.add(data);
                        }
                    }
                }

                TtsHistoryListAdapter adapter = new TtsHistoryListAdapter(getActivity(), dataList);

                mListView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class TtsHistoryListAdapter extends BaseExpandableListAdapter {

        public TtsHistoryListAdapter(Context context, ArrayList<SplData> data) {
            super();

            mData = data;

            inflater = LayoutInflater.from(context);

        }

        private ArrayList<SplData> mData;

        private LayoutInflater inflater = null;

        private ViewHolder h = null;

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                h = new ViewHolder();
                v = inflater.inflate(R.layout.row_child_tts_history, null);

                h.webView = (WebView)v.findViewById(R.id.child_tts_history_webview);

                v.setTag(h);
            } else {
                h = (ViewHolder)v.getTag();
            }

            String url = (String)getChild(groupPosition, childPosition);

            h.webView.loadUrl(url);

            return v;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                h = new ViewHolder();
                v = inflater.inflate(R.layout.row_group_tts_history, parent, false);
                h.ivIndicator = (ImageView)v.findViewById(R.id.group_tts_indicator);
                h.tvGroupName = (TextView)v.findViewById(R.id.group_tts_history_text);
                v.setTag(h);
            } else {
                h = (ViewHolder)v.getTag();
            }

            if (isExpanded) {
                h.ivIndicator.setImageResource(R.drawable.btn_list_close);
            } else {
                h.ivIndicator.setImageResource(R.drawable.btn_list_open);
            }

            SplData item = (SplData)getGroup(groupPosition);

            h.tvGroupName.setText(item.getSubject() + "\n날짜 : " + item.getDate());

            return v;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mData.get(groupPosition).getUrl();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private class ViewHolder {
            private ImageView ivImage, ivThumb, ivIndicator;

            private TextView tvGroupName;

            private TextView tvChildName;

            private TextView tvChildValue;

            private WebView webView;
        }

    }

}
