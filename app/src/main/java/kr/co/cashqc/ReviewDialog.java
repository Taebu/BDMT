
package kr.co.cashqc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;

import static kr.co.cashqc.MainActivity.TOKEN_ID;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 5..
 */
public class ReviewDialog extends Dialog {

    public static final int PICK_FROM_CAMERA = 0;

    public static final int PICK_FROM_ALBUM = 1;

    public static final int CROP_FROM_CAMERA = 2;

    private int mScore = 0;

    private ImageView reviewImg[] = new ImageView[4];

    private Context mContext;

    private Activity mActivity;

    public ReviewDialog(final Context context, String shopName, final String seq,
            final String phone, final Activity activity) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ㅎ흫 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_review);

        mContext = context;

        mActivity = activity;

        final EditText content = (EditText)findViewById(R.id.dialog_review_comment);
        final RatingBar ratingBar = (RatingBar)findViewById(R.id.dialog_review_rating);

        TextView tvShopName = (TextView)findViewById(R.id.dialog_review_name);

        tvShopName.setText(shopName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mScore = (int)rating;
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ratingBar.getRating() == 0) {
                    Toast.makeText(context, "별점을 주세요", Toast.LENGTH_SHORT).show();
                } else if (content.getText().toString().isEmpty()) {
                    Toast.makeText(context, "후기를 남겨주세요", Toast.LENGTH_SHORT).show();
                } else {

                    ReviewData data = new ReviewData();

                    data.setSeq(seq);
                    data.setPhone(phone);
                    data.setNick("test");
                    data.setContent(content.getText().toString().trim());
                    data.setRating(mScore);
                    data.setTokenId(TOKEN_ID);

                    Log.e("ReviewDialog", "content : " + content.getText().toString());
                    new ReviewTask().execute(data);
                    dismiss();
                }
            }
        });

        reviewImg[0] = (ImageView)findViewById(R.id.dialog_review_img1);
        reviewImg[1] = (ImageView)findViewById(R.id.dialog_review_img2);
        reviewImg[2] = (ImageView)findViewById(R.id.dialog_review_img3);
        reviewImg[3] = (ImageView)findViewById(R.id.dialog_review_img4);

        reviewImg[0].setOnClickListener(reviewImageOnClick);

    }

    private View.OnClickListener reviewImageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_review_img1:
                    Intent intent = new Intent();

                    // intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    // intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // mActivity.startActivityForResult(intent, 6974);

                    // intent.setAction(Intent.ACTION_GET_CONTENT);
                    // intent.setType("image/*");
                    // mContext.startActivity(intent);

                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    mActivity.startActivityForResult(intent, PICK_FROM_ALBUM);

                    // ImageDialogFragment dialogFragment = new
                    // ImageDialogFragment();

                    break;
                case R.id.dialog_review_img2:
                    break;
                case R.id.dialog_review_img3:
                    break;
                case R.id.dialog_review_img4:
                    break;
            }
        }
    };

    public ReviewDialog(final Context context, String shopName, final String seq) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_review);

        final EditText content = (EditText)findViewById(R.id.dialog_review_comment);
        final RatingBar ratingBar = (RatingBar)findViewById(R.id.dialog_review_rating);

        TextView tvShopName = (TextView)findViewById(R.id.dialog_review_name);

        tvShopName.setText(shopName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mScore = (int)rating;
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ratingBar.getRating() == 0) {
                    Toast.makeText(context, "별점을 주세요", Toast.LENGTH_SHORT).show();
                } else if (content.getText().toString().isEmpty()) {
                    Toast.makeText(context, "후기를 남겨주세요", Toast.LENGTH_SHORT).show();
                } else {

                    ReviewData modifyData = new ReviewData();

                    modifyData.setSeq(seq);
                    modifyData.setNick("test");
                    modifyData.setContent(content.getText().toString().trim());
                    modifyData.setRating(mScore);
                    modifyData.setTokenId(TOKEN_ID);

                    Log.e("ReviewDialog", "content : " + content.getText().toString());
                    new ReviewModifyTask().execute(modifyData);
                    dismiss();
                }
            }
        });

    }

    private class ReviewTask extends AsyncTask<ReviewData, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ReviewData... params) {

            ReviewData data = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/set_review.php").buildUpon()
                    .appendQueryParameter("st_seq", data.getSeq())
                    .appendQueryParameter("mb_hp", data.getPhone())
                    .appendQueryParameter("mb_nick", data.getNick())
                    .appendQueryParameter("content", data.getContent())
                    .appendQueryParameter("token_id", TOKEN_ID)
                    .appendQueryParameter("rating", String.valueOf(data.getRating())).build()
                    .toString();

            // if (data.containsKey("img1"))
            // url += "&re_img1=" + data.get("img1");
            // if (data.containsKey("img2"))
            // url += "&re_img2=" + data.get("img2");
            // if (data.containsKey("img3"))
            // url += "&re_img3=" + data.get("img3");
            // if (data.containsKey("img4"))
            // url += "&re_img4=" + data.get("img4");

            Log.e("ReviewDialog", "url : " + url);

            return new JsonParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("ReviewDialog", s);
        }
    }

    private class ReviewModifyTask extends AsyncTask<ReviewData, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ReviewData... params) {

            ReviewData data = params[0];

            String url = Uri.parse("http://cashq.co.kr/m/ajax_data/set_review.php?mode=update")
                    .buildUpon().appendQueryParameter("seq", data.getSeq())
                    .appendQueryParameter("content", data.getContent())
                    .appendQueryParameter("token_id", TOKEN_ID)
                    .appendQueryParameter("rating", String.valueOf(data.getRating())).build()
                    .toString();

            // url += "&re_img1=" + data.get("img1");

            Log.e("ReviewDialog", "url : " + url);

            return new JsonParser().getJSONStringFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("ReviewDialog", s);
        }
    }

    private void imageUpload() {
        HttpURLConnection httpURLConnection;
    }

}

// class ImageDialogFragment extends DialogFragment {
//
// @Override
// public Dialog onCreateDialog(Bundle savedInstanceState) {
//
// AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
// builder.setMessage("카메라 ?? 앨범 ??");
//
// builder.setPositiveButton("album", new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// doTakePhoto();
// }
// });
//
// builder.setNegativeButton("camera", new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// doTakeAlbum();
// }
// });
//
// return builder.create();
// }
//
// private void doTakeAlbum() {
//
//
// }
//
// private void doTakePhoto() {
//
// }
// }
