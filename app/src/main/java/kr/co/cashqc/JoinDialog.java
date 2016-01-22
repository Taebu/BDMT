
package kr.co.cashqc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jung-Hum Cho Created by anp on 14. 11. 19..
 */
public class JoinDialog extends Dialog {

    public JoinDialog(final Context context, String phoneNum) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.context = context;

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_join);
        // setContentView(R.layout.dialog_custom_ok);
        setCancelable(true);

        cbTerms1 = (CheckBox)findViewById(R.id.terms1);
        cbTerms2 = (CheckBox)findViewById(R.id.terms2);
        etNum = (EditText)findViewById(R.id.field_phone);

        etNum.setText(phoneNum);

        findViewById(R.id.btn_joinok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbTerms1.isChecked() && cbTerms2.isChecked()) {
                    new JoinTask().execute(etNum.getText().toString());
                } else {
                    Toast.makeText(context, "동의하여 주세요.", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private CheckBox cbTerms1, cbTerms2;

    private EditText etNum;

    private Context context;

    private class JoinTask extends AsyncTask<String, Void, JSONObject> {
        CustomDialog dialog = new CustomDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String baseUrl = "http://cashq.co.kr/m/ajax_data/set_member.php?board=user_member&phone=";
            JsonParser parser = new JsonParser();
            return parser.getJSONObjectFromUrl(baseUrl + params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                String success = object.getString("success");
                String msg = object.getString("msg");

                if (success.equals("true")) {
                    Toast.makeText(context, "회원 가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    dismiss();
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

}
