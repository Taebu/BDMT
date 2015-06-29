
package kr.co.cashqc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anp on 14. 11. 19..
 */
public class ModifyActivity extends BaseActivity {

    private TextView tvPhone, tvZone, tvProvided;

    private Spinner mSpinner;

    private EditText etAccNum, etHolder;

    private CustomDialog dialog;

    private String idx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        killer.addActivity(this);

        dialog = new CustomDialog(ModifyActivity.this);

        tvPhone = (TextView)findViewById(R.id.phone_num);
        tvZone = (TextView)findViewById(R.id.zone);
        tvProvided = (TextView)findViewById(R.id.provided);

        etAccNum = (EditText)findViewById(R.id.acc_num);
        etHolder = (EditText)findViewById(R.id.holder);

        mSpinner = (Spinner)findViewById(R.id.spinner_bank);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bank,
                android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        new LoadInfoTask().execute(getIntent().getStringExtra("phoneNum"));

        findViewById(R.id.btn_modify_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveInfoTask().execute(getIntent().getStringExtra("phoneNum"));
            }
        });
    }

    private class LoadInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String baseUrl = "http://cashq.co.kr/m/ajax_data/get_point_list.php?phone=";
            JsonParser parser = new JsonParser();
            return parser.getJSONObjectFromUrl(baseUrl + params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                idx = object.getString("idx");
                tvPhone.setText(object.getString("phone"));
                tvZone.setText(object.getString("biz_code"));
                // tvProvided.setText(object.getString(""));

                if (!(object.getString("accnum").isEmpty())) {
                    etAccNum.setText((object.getString("accnum")));
                }
                if (!(object.getString("holder").isEmpty())) {
                    etHolder.setText((object.getString("holder").trim()));
                }
                if (!(object.getString("bank").isEmpty())) {
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
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class SaveInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            StringBuilder sb = new StringBuilder(
                    "http://cashq.co.kr/m/request_save.php?board=user_member&state=modify&mode=json");
            sb.append("&idx=").append(idx);
            sb.append("&phone=").append(params[0]);

            String accNum = etAccNum.getText().toString();
            try {
                accNum = accNum.replace(" ", "");
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                sb.append("&accnum=").append(accNum);
            }

            String holder = etHolder.getText().toString();
            try {
                holder = holder.replace(" ", "");
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                sb.append("&holder=").append(holder);
            }

            sb.append("&bank=").append(mSpinner.getSelectedItem().toString());

            return new JsonParser().getJSONObjectFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            try {
                Toast.makeText(ModifyActivity.this, object.getString("msg"), Toast.LENGTH_SHORT)
                        .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (dialog.isShowing())
                dialog.dismiss();
            Intent intent = new Intent(ModifyActivity.this, PointActivity.class);
            intent.putExtra("phoneNum", getIntent().getStringExtra("phoneNum"));

            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }
}
