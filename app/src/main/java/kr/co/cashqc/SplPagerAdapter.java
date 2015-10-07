
package kr.co.cashqc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @ Jung-Hum Cho Created by anp on 15. 6. 25..
 */
public class SplPagerAdapter extends FragmentPagerAdapter {

    private String mPhoneNum;

    public SplPagerAdapter(FragmentManager fm, String phoneNum) {
        super(fm);

        mPhoneNum = phoneNum;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 2:
                fragment = new TtsHistoryFragment();
                break;

            case 0:
                fragment = new CallHistoryFragment();
                break;

            case 1:
                fragment = new PointHistoryFragment();
                break;

            default:
                fragment = new CallHistoryFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("phoneNum", mPhoneNum);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
