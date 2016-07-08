
package com.anp.bdmt;

import static com.anp.bdmt.gcm.Util.saveSharedPreferences_boolean;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author Jung-Hum Cho Created by Jung-Hum Cho on 2016. 5. 20..
 */
public class InitializeActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);

        init();

    }

    private void init() {

        InitializePagerAdapter pagerAdapter = new InitializePagerAdapter(
                getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.initialize_main);
        viewPager.setAdapter(pagerAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.initialize_indicator);

        circlePageIndicator.setViewPager(viewPager);

        circlePageIndicator.bringToFront();

    }

    public void mOnClick(View view) {
        if (view.getId() == R.id.skip) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            saveSharedPreferences_boolean(this, "init", true);
        }
    }

    private class InitializePagerAdapter extends FragmentPagerAdapter {

        public InitializePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new InitializeFragment();

            Bundle bundle = new Bundle();

            bundle.putInt("page", position);

            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
