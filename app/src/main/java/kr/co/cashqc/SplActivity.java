
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import kr.co.cashqc.gcm.Util;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 23..
 */
public class SplActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private Activity mThis = this;

    private TextView tvPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);
        killer.addActivity(this);

        init();

    }

    private void init() {

        mDialog = new CustomDialog(this);

        tvPage = (TextView)findViewById(R.id.callog_page);

        String phoneNum = getIntent().getStringExtra("phoneNum");

        SplPagerAdapter pagerAdapter = new SplPagerAdapter(getSupportFragmentManager(), phoneNum);

        ViewPager viewPager = (ViewPager)findViewById(R.id.spl_pager);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0:
                        tvPage.setText("TTS");
                        break;

                    case 1:
                        tvPage.setText("CALL");
                        break;

                    case 2:
                        tvPage.setText("POINT");
                        break;
                }

            }
        });

        findViewById(R.id.calllog_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.saveSharedPreferences_boolean(mThis, "spl_autologin", false);
                startActivity(new Intent(mThis, MainActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        killer.removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
