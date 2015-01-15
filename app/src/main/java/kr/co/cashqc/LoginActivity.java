
package kr.co.cashqc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.cashqc.gcm.Util;

/**
 * Created by anp on 14. 11. 18..
 */
public class LoginActivity extends BaseActivity {

    private EditText etPhoneNum;

    private CheckBox cbAutoLogin;

    private Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        killer.addActivity(this);

        cbAutoLogin = (CheckBox)findViewById(R.id.auto_login);

        etPhoneNum = (EditText)findViewById(R.id.field_phone);

        btnLogin = (Button)findViewById(R.id.btn_login);

        if (!(getPhoneNumber().isEmpty())) {
            etPhoneNum.setText(getPhoneNumber());
        }

        cbAutoLogin.setChecked(true);
        if (Util.loadSharedPreferencesBoolean(LoginActivity.this, "login")) {
            // btnLogin.setText("로그아웃");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Util.loadSharedPreferencesBoolean(LoginActivity.this, "login")) {
//                    Util.saveSharedPreferences_boolean(LoginActivity.this, "login", false);
//                    btnLogin.setText("로그인");
//                } else {

                    if (cbAutoLogin.isChecked()) {
                        Util.saveSharedPreferences_boolean(LoginActivity.this, "auto_login", true);
                    } else {
                        Util.saveSharedPreferences_boolean(LoginActivity.this, "auto_login", false);
                    }

                    new LoginTask().execute(etPhoneNum.getText().toString());
//                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_join:
                startActivity(new Intent(this, JoinActivity.class));
                break;
        }
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        CustomDialog dialog = new CustomDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String baseUrl = "http://cashq.co.kr/m/login_json.php?userid=";
            JSONParser parser = new JSONParser();
            return parser.getJSONObjectFromUrl(baseUrl + params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            String success = null;
            String msg = null;

            try {
                success = object.getString("success");
                msg = object.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if ("true".equals(success)) {
                Util.saveSharedPreferences_boolean(LoginActivity.this, "login", true);
                Util.saveSharedPreferences_string(LoginActivity.this, "phoneNum", etPhoneNum
                        .getText().toString());

                Intent intent = new Intent(LoginActivity.this, PointActivity.class);
                intent.putExtra("phoneNum", etPhoneNum.getText().toString());
                startActivity(intent);
            } else {
                Util.saveSharedPreferences_boolean(LoginActivity.this, "login", false);

                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                intent.putExtra("phoneNum", etPhoneNum.getText().toString());
                CustomDialog joinDialog = new CustomDialog(LoginActivity.this, msg, LoginActivity.this, intent);
                joinDialog.show();
            }



            if (dialog.isShowing())
                dialog.dismiss();
        }

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }
}
