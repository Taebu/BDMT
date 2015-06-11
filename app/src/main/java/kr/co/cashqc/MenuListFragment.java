
package kr.co.cashqc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static kr.co.cashqc.gcm.Util.getPhoneNumber;

/**
 * @author Jung-Hum Cho
 */

public class MenuListFragment extends ListFragment {

    public static boolean sIsAdmin = false;

    public static boolean sIsInitAdminCheck = true;

    private final String TAG = getClass().getSimpleName();

    // 업데이트 모드 (false)
    final boolean updateFlag = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(kr.co.cashqc.R.layout.list, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.e(TAG, "onAttach");
        // mListener = (AdapterView.OnItemClickListener) activity;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e(TAG, "onActivityCreated");

        String phoneNum = getPhoneNumber(getActivity());

//        if (sIsInitAdminCheck) {
//            if (phoneNum != null)
//                new AdminCheckTask().execute(phoneNum);
//        } else {

            SampleAdapter adapter = new SampleAdapter(getActivity());

            adapter.add(new SampleItem("홈", R.drawable.icon_more_1_gray));
            adapter.add(new SampleItem("포인트", R.drawable.icon_more_2_gray));
            adapter.add(new SampleItem("문의 사항", R.drawable.icon_more_3_gray));
            adapter.add(new SampleItem("공지 사항", R.drawable.icon_more_6_gray));

//            if (sIsAdmin)
                adapter.add(new SampleItem("주문내역", R.drawable.icon_more_8_gray));

            setListAdapter(adapter);
//        }
    }

    private class AdminCheckTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            String phoneNumber = params[0];

            String url = "http://cashq.co.kr/m/ajax_data/get_isadmin.php?phone=" + phoneNumber;
            Log.e(TAG, "MenuListFragment isadmin url : " + url);
            return new JSONParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                sIsAdmin = jsonObject.getBoolean("success");
                sIsInitAdminCheck = false;
                Log.e("isAdminPhone", "async!");

                SampleAdapter adapter = new SampleAdapter(getActivity());

                adapter.add(new SampleItem("홈", R.drawable.icon_more_1_gray));
                adapter.add(new SampleItem("포인트", R.drawable.icon_more_2_gray));
                adapter.add(new SampleItem("문의 사항", R.drawable.icon_more_3_gray));
                adapter.add(new SampleItem("공지 사항", R.drawable.icon_more_6_gray));

                if (sIsAdmin)
                    adapter.add(new SampleItem("가맹점 주문내역", R.drawable.icon_more_8_gray));

                setListAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private class SampleItem {
        public String tag;

        public int iconRes;

        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }

    public class SampleAdapter extends ArrayAdapter<SampleItem> {

        public SampleAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(kr.co.cashqc.R.layout.row,
                        null);
            }
            ImageView icon = (ImageView)convertView.findViewById(kr.co.cashqc.R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView)convertView.findViewById(kr.co.cashqc.R.id.row_title);
            title.setText(getItem(position).tag);

            return convertView;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = null;

        switch (position) {
            case 0:
                intent = new Intent(getActivity(), MainActivity.class);
                break;
            case 1:
                intent = new Intent(getActivity(), LoginActivity.class);

                break;
            case 2:
                // intent = new Intent(getActivity(), WebViewActivity.class);
                intent = new Intent(getActivity(), QNAActivity.class);
                intent.putExtra("assort", "qna");
                break;
            case 3:
                // intent = new Intent(getActivity(), WebViewActivity.class);
                intent = new Intent(getActivity(), NoticeActivity.class);
                intent.putExtra("assort", "notice");
                break;
            case 4:
                intent = new Intent(getActivity(), SPLLoginActivity.class);
                break;

            case 5:
                intent = new Intent(getActivity(), OrderListLoginActivity.class);
                break;
        // case 6:
        // intent = new Intent(getActivity(), SPLActivity.class);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("MenuListFragment", "!!! onStop !!!");
    }
}
