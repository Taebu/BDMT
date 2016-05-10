
package com.wisepartners.dtalk;

import com.actionbarsherlock.app.ActionBar;
import com.wisepartners.dtalk.gcm.Util;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

/**
 * @author Jung-Hum Cho
 */

public class ShopListActivity extends BaseActivity implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    private ActionBar mActionBar;

    private TextView tvAddress;

    private LocationUtil mLocationUtil;

    // Tab titles
    private String[] mTabs = {
            "할인", "치킨", "피자/햄버거", "중식/냉면", "한식/분식", "닭발/오리", "야식/기타", "족발/보쌈", "일식/돈가스"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        // activity killer activity add.
        killer.addActivity(this);

        mLocationUtil = LocationUtil.getInstance(this);

        // findViewById(R.id.logo).setVisibility(View.GONE);

        // findViewById(R.id.actionbar_gps_layout).setVisibility(View.VISIBLE);

        tvAddress = (TextView)findViewById(R.id.actionbar_location_name1);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        double lat = getIntent().getDoubleExtra("lat", -1);
        double lng = getIntent().getDoubleExtra("lng", -1);

        int distance = getIntent().getIntExtra("distance", 2);

        boolean life = getIntent().getBooleanExtra("LIFE", false);

        String type = getIntent().getStringExtra("TYPE");
        int position = getIntent().getIntExtra("POSITION", 1);

        // String address = mLocationUtil.getAddressShort(lat, lng);

        // tvAddress.setText(address);

        // Initialization
        ShopListPagerAdapter pagerAdapter = new ShopListPagerAdapter(getSupportFragmentManager(),
                lat, lng, distance, type, life);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        // mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

        // 액션바 활성
        mActionBar = getSupportActionBar();

        // TODO 액션바 아이콘

        if (life) {
            mTabs = new String[] {
                    "오락/레져", "건강/뷰티", "꽃배달", "병원/약국", "인테리어", "학원", "이사/용달/퀵", "부동산", "자동차",
                    "컴퓨터/인터넷"
            };

        } else {
            mTabs = new String[] {
                    "치킨", "피자/햄버거", "중식", "한식", "분식", "야식", "족발/보쌈", "일식/돈가스"
            };

        }

        // if (SALE_ZONE) {
        // mTabs = new String[] {
        // "할인", "치킨", "피자/햄버거", "중식/냉면", "한식/분식", "닭발/오리", "야식/기타", "족발/보쌈",
        // "일식/돈가스",
        // "생활/편의"
        // };
        // } else {
        // mTabs = new String[] {
        // "치킨", "피자/햄버거", "중식/냉면", "한식/분식", "닭발/오리", "야식/기타", "족발/보쌈", "일식/돈가스"
        // };
        // }
        // if (!SALE_ZONE) {
        // position -= 1;
        // }

        if (mActionBar != null) {
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            for (String tab_name : mTabs) {
                mActionBar.addTab(mActionBar.newTab().setText(tab_name).setTabListener(this));
            }
        }

        mViewPager.setCurrentItem(position);
        mActionBar.setSelectedNavigationItem(position);

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
    public void finish() {
        super.finish();
        activityAnimation(false);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
