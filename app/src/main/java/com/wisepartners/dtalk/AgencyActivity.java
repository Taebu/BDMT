
package com.wisepartners.dtalk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 9..
 */
public class AgencyActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency);
        killer.addActivity(this);

        mDialog = new CustomDialog(this);

        final EditText phoneEditText = (EditText)findViewById(R.id.id_phone);
        final EditText localEditText = (EditText)findViewById(R.id.id_local);
        final EditText commentEditText = (EditText)findViewById(R.id.id_comment);

        findViewById(R.id.btn_agency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = phoneEditText.getText().toString();
                String local = localEditText.getText().toString();
                String comment = commentEditText.getText().toString();

                if (phone.isEmpty()) {
                    new CustomDialog(AgencyActivity.this, "연락처를 입력해주세요.").show();
                } else if (local.isEmpty()) {
                    new CustomDialog(AgencyActivity.this, "지역을 입력해주세요.").show();
                } else if (comment.isEmpty()) {
                    new CustomDialog(AgencyActivity.this, "내용을 입력해주세요.").show();
                } else {
                    new AgencyRequestTask().execute(phone, local, comment);
                }
            }
        });
    }

    private class AgencyRequestTask extends AsyncTask<String, Void, String> {
        public AgencyRequestTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String phone = params[0], local = params[1], comment = params[2];

            StringBuilder sb = new StringBuilder();

            return new JsonParser().getJSONStringFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

            String result;
            CustomDialog dialog = new CustomDialog(AgencyActivity.this, "result");
        }
    }
}
