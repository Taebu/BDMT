
package kr.co.cashqc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static kr.co.cashqc.gcm.Util.getPhoneNumber;

/**
 * Created by anp on 14. 11. 19..
 */
public class JoinActivity extends BaseActivity {

    private CheckBox cbTerms1, cbTerms2;

    private EditText etNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        killer.addActivity(this);

        cbTerms1 = (CheckBox)findViewById(R.id.terms1);
        cbTerms2 = (CheckBox)findViewById(R.id.terms2);
        etNum = (EditText)findViewById(R.id.field_phone);

        etNum.setText(getPhoneNumber(this));

        findViewById(R.id.btn_joinok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbTerms1.isChecked() && cbTerms2.isChecked()) {
                    new JoinTask().execute(etNum.getText().toString());
                } else {
                    Toast.makeText(JoinActivity.this, "약관에 모두 동의하여 주세요.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private class JoinTask extends AsyncTask<String, Void, JSONObject> {
        CustomDialog dialog = new CustomDialog(JoinActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String baseUrl = "http://cashq.co.kr/m/ajax_data/set_member.php?board=user_member&phone=";
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

            CustomDialog joinDialog = new CustomDialog(JoinActivity.this, msg, true,
                    JoinActivity.this, LoginActivity.class);
            joinDialog.show();

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }
}
