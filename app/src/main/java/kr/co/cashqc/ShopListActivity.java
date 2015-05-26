
package kr.co.cashqc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;

import kr.co.cashqc.gcm.Util;

/**
 * @author Jung-Hum Cho
 */

public class ShopListActivity extends BaseActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;

    private ActionBar mActionBar;

    private TextView tvAddress;

    private LocationUtil mLocationUtil;

    // Tab titles
    private String[] mTabs = {
            "치킨", "피자/햄버거", "중식/냉면", "한식/분식", "닭발/오리", "야식/기타", "족발/보쌈", "일식/돈가스"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        // activity killer activity add.
        killer.addActivity(this);

        mLocationUtil = LocationUtil.getInstance(this);

//        findViewById(R.id.actionbar_gps_layout).setVisibility(View.VISIBLE);
//        findViewById(R.id.logo).setVisibility(View.GONE);

        tvAddress = (TextView)findViewById(R.id.actionbar_location_name1);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        double lat = getIntent().getDoubleExtra("lat", -1), lng = getIntent().getDoubleExtra("lng",
                -1);

        int distance = getIntent().getIntExtra("distance", 2);

        String address = mLocationUtil.getAddress(lat, lng);

        tvAddress.setText(address);

        // Initialization
        ShopListPagerAdapter pagerAdapter = new ShopListPagerAdapter(getSupportFragmentManager(),
                lat, lng, distance);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        // mViewPager.setOffscreenPageLimit(8);

        // 액션바 활성
        mActionBar = getSupportActionBar();

        // TODO 액션바 아이콘

        if (mActionBar != null) {
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            for (String tab_name : mTabs) {
                mActionBar.addTab(mActionBar.newTab().setText(tab_name).setTabListener(this));
            }
        }

        Intent intent = getIntent();

        mViewPager.setCurrentItem(intent.getIntExtra("TYPE", 1) - 1);
        mActionBar.setSelectedNavigationItem(intent.getIntExtra("TYPE", 1) - 1);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
            }
        });
    }

    @Override
    protected void onRestart() {
        setCartCount(this);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // setCartCount(this);
        super.onResume();
        // MainActivity.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
        activityAnimation(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

}
