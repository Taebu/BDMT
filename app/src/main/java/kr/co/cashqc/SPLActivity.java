
package kr.co.cashqc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

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

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.calllog_indicator);

        pageIndicator.setFillColor(Color.parseColor("#000000"));
        pageIndicator.setStrokeColor(Color.parseColor("#333333"));

        pageIndicator.setViewPager(viewPager);

        pageIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
//                    case 0:
//                        tvPage.setText("바로결제 주문 내역");
//                        break;

                    case 0:
                        tvPage.setText("전화 주문 내역");
                        break;

                    case 1:
                        tvPage.setText("포인트 적립 내역");
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
