
package com.anp.bdmt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jung-Hum Cho Created by anp on 15. 6. 5..
 */
public class BankAccountDialog extends Dialog {

    private final Spinner mSpinner;

    private final EditText etAccNum;

    private final EditText etHolder;

    private String idx;

    private Activity mActivity;

    private String mPhoneNum;

    private Button btnOk;

    public BankAccountDialog(final Activity activity, final String phoneNum) {
        super(activity);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_bankaccount);

        mActivity = activity;
        mPhoneNum = phoneNum;

        mSpinner = (Spinner)findViewById(R.id.spinner_bank);
        etAccNum = (EditText)findViewById(R.id.acc_num);
        etHolder = (EditText)findViewById(R.id.holder);
        btnOk = (Button) findViewById(R.id.ok);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.bank, android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        new LoadInfoTask().execute();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveInfoTask().execute();
            }
        });
    }

    private class LoadInfoTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnOk.setText("불러 오는 중 ...");
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String baseUrl = "http://cashq.co.kr/m/ajax_data/get_point_list.php?phone=";
            JsonParser parser = new JsonParser();
            return parser.getJSONObjectFromUrl(baseUrl + mPhoneNum);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                idx = object.getString("idx");

                if (object.has("accnum")) {
                    String accNum = (object.getString("accnum"));
                    accNum = accNum.replace(" ", "").trim();
                    etAccNum.setText(accNum);
                }

                if (object.has("holder")) {
                    String holder = (object.getString("holder"));
                    holder = holder.replace(" ", "").trim();
                    etHolder.setText(holder);
                }

                if (object.has("bank")) {
                    for (int i = 0; i < mSpinner.getCount(); i++) {
                        if (object.getString("bank").equals(mSpinner.getItemAtPosition(i))) {
                            mSpinner.setSelection(i);
                            break;
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            btnOk.setText("계좌 정보 저장");
        }
    }

    private class SaveInfoTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnOk.setText("저장 중 ...");
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            StringBuilder sb = new StringBuilder(
                    "http://cashq.co.kr/m/request_save.php?board=user_member&state=modify&mode=json");
            sb.append("&idx=").append(idx);
            sb.append("&phone=").append(mPhoneNum);
            sb.append("&bank=").append(mSpinner.getSelectedItem().toString());

            String accNum = etAccNum.getText().toString();
            String holder = etHolder.getText().toString();

            try {
                accNum = accNum.replace(" ", "").trim();
                holder = holder.replace(" ", "").trim();

                sb.append("&accnum=").append(accNum);
                sb.append("&holder=").append(holder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return new JsonParser().getJSONObjectFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                Toast.makeText(mActivity, object.getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dismiss();

            Intent intent = new Intent(mActivity, PointActivity.class);
            intent.putExtra("phoneNum", mPhoneNum);
            mActivity.startActivity(intent);
        }
    }
}
