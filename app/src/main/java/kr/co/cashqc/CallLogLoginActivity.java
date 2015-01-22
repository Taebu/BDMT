
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.co.cashqc.gcm.Util;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 22..
 */
public class CallLogLoginActivity extends BaseActivity {

    private CheckBox cbAutoLogin;

    private EditText etPhone;

    private Activity mThis = CallLogLoginActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callloglogin);

        killer.addActivity(this);

        mDialog = new CustomDialog(this);

        cbAutoLogin = (CheckBox)findViewById(R.id.calllog_autologin);
        etPhone = (EditText)findViewById(R.id.calllog_phone);
        etPhone.setText(getPhoneNumber());

        if (Util.loadSharedPreferencesBoolean(mThis, "calllog_autologin")) {

            cbAutoLogin.setChecked(true);
            loginWork();
        }

        findViewById(R.id.calllog_loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWork();
            }
        });
    }

    private void loginWork() {

        String phoneNum = etPhone.getText().toString();

        boolean isAutoLogin = cbAutoLogin.isChecked();

        if ("".equals(phoneNum)) {

            Toast.makeText(mThis, "핸드폰번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

        } else {

            new LoginTask(isAutoLogin).execute(phoneNum);

        }
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {

        private LoginTask(boolean isAutoLogin) {
            this.isAutoLogin = isAutoLogin;
        }

        private boolean isAutoLogin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String phoneNum = params[0];

            String url = "http://cashq.co.kr/m/login_json.php?userid=" + phoneNum;

            return new JSONParser().getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                if (jsonObject.getBoolean("success")) {

                    String[] seq = jsonObject.getString("st_seq").split("_");

                    Log.e("CallLog", "seq : " + Arrays.toString(seq));

                    Util.saveSharedPreferences_boolean(mThis, "calllog_autologin", isAutoLogin);

                    Intent intent = new Intent(mThis, CallLogActivity.class);

                    intent.putExtra("seq", seq);

                    startActivity(intent);

                } else {

                    String msg = jsonObject.getString("msg");
                    Toast.makeText(mThis, msg, Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }
}
