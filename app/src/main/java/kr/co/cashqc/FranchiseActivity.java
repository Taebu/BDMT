
package kr.co.cashqc;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by anp on 14. 12. 9..
 */
public class FranchiseActivity extends BaseActivity {

    private boolean mOwner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchise);
        killer.addActivity(this);
        mDialog = new CustomDialog(this);

        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.franchise_radio);
        final RadioButton yes = (RadioButton)findViewById(R.id.owner_on);
        final RadioButton no = (RadioButton)findViewById(R.id.owner_off);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == yes.getId()) {
                    mOwner = true;
                } else if (checkedId == no.getId()) {
                    mOwner = false;
                }

            }
        });

        ImageView img1 = (ImageView)findViewById(R.id.franchise_img1);
        ImageView img2 = (ImageView)findViewById(R.id.franchise_img2);
        ImageView img3 = (ImageView)findViewById(R.id.franchise_img3);
        ImageView img4 = (ImageView)findViewById(R.id.franchise_img4);

        final EditText phoneEditText = (EditText)findViewById(R.id.franchise_phone);
        final EditText addressEditText = (EditText)findViewById(R.id.franchise_address);

        findViewById(R.id.btn_franchise).setOnClickListener(new View.OnClickListener() {

            String phone = phoneEditText.getText().toString();

            String address = addressEditText.getText().toString();

            @Override
            public void onClick(View v) {

                if (!radioGroup.isSelected()) {
                    new CustomDialog(FranchiseActivity.this, "업주 여부를 체크해주세요").show();
                } else if (phone.isEmpty()) {
                    new CustomDialog(FranchiseActivity.this, "음식점 전화번호를 입력해주세요").show();
                } else if (address.isEmpty()) {
                    new CustomDialog(FranchiseActivity.this, "음식점 주소를 입력해주세요").show();
                } else {
                    Bitmap[] img = new Bitmap[4];
                    new FranchiseRequestTask(mOwner, phone, address, img).execute();
                }
            }
        });
    }

    public void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.franchise_img1:

                break;
            case R.id.franchise_img2:

                break;
            case R.id.franchise_img3:

                break;
            case R.id.franchise_img4:

                break;
        }
    }

    private class FranchiseRequestTask extends AsyncTask<Void, Void, String> {

        private FranchiseRequestTask(boolean mIsOwner, String mPhone, String mAddress, Bitmap[] mImg) {
            this.mIsOwner = mIsOwner;
            this.mPhone = mPhone;
            this.mAddress = mAddress;
            this.mImg = mImg;
        }

        private boolean mIsOwner;

        private String mPhone, mAddress;

        private Bitmap[] mImg = new Bitmap[4];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing())
                mDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            StringBuilder sb = new StringBuilder("");

            return new JSONParser().getJSONStringFromUrl(sb.toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }
}
