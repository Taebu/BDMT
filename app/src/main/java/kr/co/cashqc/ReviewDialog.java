package kr.co.cashqc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 5..
 */
public class ReviewDialog extends Dialog {

    private int mScore = 0;

    public ReviewDialog(final Context context, String shopName, final String seq, final String phone) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_review);

        final EditText content = (EditText) findViewById(R.id.dialog_review_comment);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.dialog_review_rating);

        TextView tvShopName = (TextView) findViewById(R.id.dialog_review_name);

        tvShopName.setText(shopName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mScore = (int) rating;
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final HashMap<String, String> map = new HashMap<String, String>();

                map.put("seq", seq);
                map.put("phone", phone);
                map.put("nick", "test");
                map.put("content", content.getText().toString());
                map.put("rating", String.valueOf(mScore));

                Log.e("ReviewDialog", "content : " + content.getText().toString());
                new ReviewTask().execute(map);
                dismiss();
            }
        });

    }

    private class ReviewTask extends AsyncTask<HashMap<String, String>, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {

            HashMap<String, String> map = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/set_review.php").buildUpon()
                    .appendQueryParameter("st_seq", map.get("seq"))
                    .appendQueryParameter("mb_hp", map.get("phone"))
                    .appendQueryParameter("mb_nick", map.get("nick"))
                    .appendQueryParameter("content", map.get("content"))
                    .appendQueryParameter("rating", map.get("rating")).build().toString();

            if (map.containsKey("img1"))
                url += "&re_img1=" + map.get("img1");
            if (map.containsKey("img2"))
                url += "&re_img2=" + map.get("img2");
            if (map.containsKey("img3"))
                url += "&re_img3=" + map.get("img3");
            if (map.containsKey("img4"))
                url += "&re_img4=" + map.get("img4");

            Log.e("ReviewDialog", "url : " + url);

            return new JSONParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
