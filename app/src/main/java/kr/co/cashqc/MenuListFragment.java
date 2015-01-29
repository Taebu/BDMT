
package kr.co.cashqc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.cashqc.gcm.Util;

/**
 * @author Jung-Hum Cho
 */

public class MenuListFragment extends ListFragment {

    // 업데이트 모드 (false)
    final boolean updateFlag = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(kr.co.cashqc.R.layout.list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SampleAdapter adapter = new SampleAdapter(getActivity());
        adapter.add(new SampleItem("홈", R.drawable.icon_more_1_gray));
        String login = Util.loadSharedPreferencesBoolean(getActivity(), "login") ? "로그아웃" : "로그인";
        adapter.add(new SampleItem("포인트", R.drawable.icon_more_2_gray));
        adapter.add(new SampleItem(login, R.drawable.icon_pw_off));
        adapter.add(new SampleItem("문의 사항", R.drawable.icon_more_3_gray));
        adapter.add(new SampleItem("공지 사항", R.drawable.icon_more_6_gray));
        adapter.add(new SampleItem("가맹점", R.drawable.icon_more_5_gray));
        adapter.add(new SampleItem("푸시로그", R.drawable.icon_more_8_gray));
//        adapter.add(new SampleItem("제휴 문의", R.drawable.icon_more_4_gray));

        setListAdapter(adapter);
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
                if (Util.loadSharedPreferencesBoolean(getActivity(), "login")) {
                    intent = new Intent(getActivity(), PointActivity.class);
                    intent.putExtra("phoneNum",
                            Util.loadSharedPreferences(getActivity(), "phoneNum"));
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    Toast.makeText(getActivity(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (Util.loadSharedPreferencesBoolean(getActivity(), "login")) {
                    Util.saveSharedPreferences_boolean(getActivity(), "login", false);
                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(getActivity(), MainActivity.class);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                break;
            case 3:
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("assort", "qna");
                break;
            case 4:
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("assort", "notice");
                break;
            case 5:
                intent = new Intent(getActivity(), CallLogLoginActivity.class);
                break;
            case 6:
                intent = new Intent(getActivity(), GCMActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }

    }
}
