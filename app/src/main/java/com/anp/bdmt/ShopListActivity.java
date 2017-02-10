
package com.anp.bdmt;

import com.actionbarsherlock.app.ActionBar;
import com.anp.bdmt.gcm.Util;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

/**
 * @author Jung-Hum Cho
 */

public class ShopListActivity extends BaseActivity implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        // activity killer activity add.
        killer.addActivity(this);

        if (!Util.isOnline(this)) {
            Util.showDialog_normal(this, "네트워크 에러", "네트워크 연결 상태를 확인해주세요");
        }

        boolean life = getIntent().getBooleanExtra("life", false);

        String[] tabs;
        String[] types;

        if (life) {
            tabs = new String[] {
                    "오락/레져", "건강/뷰티", "꽃배달", "병원/약국", "인테리어", "학원", "이사/용달/퀵", "부동산", "자동차",
                    "컴퓨터/인터넷"
            };

            types = new String[] {
                    "W09", "W10", "W11", "W12", "W13", "W14", "W15", "W16", "W18", "W19"
            };

        } else {
            tabs = new String[] {
                    "치 킨", "피 자", "중 식", "한식/분식", "야식", "찜 / 탕", "족발/보쌈", "일식/돈가스", "도시락", "패스트푸드",
                    "생활편의", "대리운전", "퀵서비스"
            };

            types = new String[] {
                    "W01", "W02", "W03", "W04", "W05", "W06", "W07", "W08", "W22", "W23", "W10", "W26", "W27"
            };

        }

        // types = new String[] {
        // "W01", "W02", "W03", "W04", "W05", "W06", "W07", "W08", "W22", "W23",
        // "W10"
        // };

        // Initialization
        ShopListPagerAdapter pagerAdapter = new ShopListPagerAdapter(getSupportFragmentManager(),
                tabs, types);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        // mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

        // 액션바 활성
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            for (String tabName : tabs) {
                mActionBar.addTab(mActionBar.newTab().setText(tabName).setTabListener(this));
            }
        }

        int position = getIntent().getIntExtra("position", 0);
        mViewPager.setCurrentItem(position);
        mActionBar.setSelectedNavigationItem(position);

    }

    @Override
    protected void onRestart() {
        setCartCount(this);
        super.onRestart();
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
